package com.sean.activiti.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sean.activiti.base.controller.RestServiceController;
import com.sean.activiti.util.JsonUtil;
import com.sean.activiti.util.Result;
import com.sean.activiti.util.Status;
import com.sean.activiti.vo.KeyValue;
import com.sean.activiti.vo.MyForm;
import com.sean.activiti.vo.ProcessDefinitionVO;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.HistoricDetailVariableInstanceUpdateEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/models")
public class ModelerController implements RestServiceController<Model, String> {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;

    /**
     * 创建模型
     */
    @RequestMapping("/create")
    public Object create(HttpServletRequest request, HttpServletResponse response) {
        try {
//            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//            RepositoryService repositoryService = processEngine.getRepositoryService();

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset", stencilSetNode);
            Model modelData = repositoryService.newModel();

            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, "hello1111");
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            String description = "hello1111";
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelData.setMetaInfo(modelObjectNode.toString());
            modelData.setName("hello1111");
            modelData.setKey("12313123");

            //保存模型
            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
//            response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());
            return Result.buildResult().redirectUrl(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());
        } catch (Exception e) {
//            System.out.println("创建模型失败：");
            Result.buildResult().status(Status.FAIL).msg("创建模型失败");
        }
        return null;
    }

    /**
     * 发布模型为流程定义
     * @param id
     * @return
     * @throws Exception
     */
    @PostMapping("{id}/deployment")
    public Object deploy(@PathVariable("id")String id) throws Exception {

        //获取模型
        Model modelData = repositoryService.getModel(id);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

        if (bytes == null) {
            return Result.buildResult().status(Status.FAIL)
                    .msg("模型数据为空，请先设计流程并成功保存，再进行发布。");
        }

        JsonNode modelNode = new ObjectMapper().readTree(bytes);

        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        if(model.getProcesses().size()==0){
            return Result.buildResult().status(Status.FAIL)
                    .msg("数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .name(modelData.getName())
                .addString(processName, new String(bpmnBytes, "UTF-8"))
                .deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);

        return Result.buildResult().refresh();
    }

    @Override
    public Object getOne(@PathVariable("id") String id) {
        Model model = repositoryService.createModelQuery().modelId(id).singleResult();
        return Result.buildResult().setObjData(model);
    }

    @Override
    public Object getList(@RequestParam(value = "rowSize", defaultValue = "1000", required = false) Integer rowSize, @RequestParam(value = "page", defaultValue = "1", required = false) Integer page) {
        List<Model> list = repositoryService.createModelQuery().listPage(rowSize * (page - 1)
                , rowSize);
        long count = repositoryService.createModelQuery().count();

        return Result.buildResult().setRows(
                Result.Rows.buildRows().setCurrent(page)
                        .setTotalPages((int) (count/rowSize+1))
                        .setTotalRows(count)
                        .setList(list)
                        .setRowSize(rowSize)
        );
    }

    public Object deleteOne(@PathVariable("id")String id){
        repositoryService.deleteModel(id);
        return Result.buildResult().refresh();
    }

    @Override
    public Object postOne(@RequestBody Model entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object putOne(@PathVariable("id") String s, @RequestBody Model entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object patchOne(@PathVariable("id") String s, @RequestBody Model entity) {
        throw new UnsupportedOperationException();
    }

    /**
     * 已发布流程展示
     * @param rowSize
     * @param page
     * @return
     */
    @RequestMapping("startPage")
    public Object startPage(@RequestParam(value = "rowSize", defaultValue = "1000", required = false) Integer rowSize, @RequestParam(value = "page", defaultValue = "1", required = false) Integer page) {
        List<ProcessDefinitionVO> pdVOList = new ArrayList<ProcessDefinitionVO>();
        //加载流程定义
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().listPage(rowSize * (page - 1), rowSize);
        long count = repositoryService.createProcessDefinitionQuery().count();

        for(ProcessDefinition p : list) {
            pdVOList.add(ProcessDefinitionVO.toVO(p));
        }

//        return Result.buildResult().setRows(
//                Result.Rows.buildRows().setCurrent(page)
//                        .setTotalPages((int) (count/rowSize+1))
//                        .setTotalRows(count)
//                        .setList(pdVOList)
//                        .setRowSize(rowSize)
//        );
        return Result.buildResult().setRows(page, rowSize, count, pdVOList);
    }

    /**
     * 启动流程
     * @param processesId
     * @return
     */
    @RequestMapping("/{processesId}/start")
    public Object startProcesses(@PathVariable("processesId")String processesId) {
        Map<String, Object> param = new HashMap<>();
//        Execution last = runtimeService.createExecutionQuery().processInstanceBusinessKey(userId).processDefinitionId(processesId).singleResult();
//        if (null != last) {
//            return JsonUtil.getFailJson("请勿重复提交！");
//        }

        ProcessInstance pi = runtimeService.startProcessInstanceById(processesId, Status.SUBMIT_USER_ID, param);
        if (null == pi) {
            return Result.buildResult().msg("流程启动失败！");
        }
        return Result.buildResult().msg("流程启动成功！");

    }

    /**
     * 将任务分配给我
     * @return
     */
    @RequestMapping("/assign/me")
    public Object assignToMe() {
        List<Task> list = taskService.createTaskQuery().list();
        for(Task t : list) {
            taskService.setAssignee(t.getId(), Status.ASSIGNEE_USER_ID);
        }
        return Result.buildResult().refresh();
    }

    /**
     * 完成任务
     * @param param
     * @return
     */
    @RequestMapping("/{taskId}/complete")
    @ResponseBody
    public Object completeTasks(@PathVariable("taskId")String taskId, @RequestParam Map<String, Object> param) {

        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        taskService.complete(task.getId(), param);

        return Result.buildResult().msg("完成任务："+ taskId);

    }

    /**
     * 绘制流程图
     * @param processInstanceId
     * @return
     * @throws IOException
     */
    @RequestMapping("/{processInstanceId}/generateImg")
    @ResponseBody
    public JSONObject generateProcessImg(@PathVariable("processInstanceId")String processInstanceId) throws IOException {

        //获取历史流程实例
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());

        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

        List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        //高亮环节id集合
        List<String> highLightedActivitis = new ArrayList<String>();

        //高亮线路id集合
        List<String> highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);

        for (HistoricActivityInstance tempActivity : highLightedActivitList) {
            String activityId = tempActivity.getActivityId();
            highLightedActivitis.add(activityId);
        }
        //配置字体
        InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis, highLightedFlows, "宋体", "微软雅黑", "黑体", null, 2.0);
        BufferedImage bi = ImageIO.read(imageStream);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", bos);
        //转换成字节
        byte[] bytes = bos.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        //转换成base64串
        String png_base64 = encoder.encodeBuffer(bytes);
        //删除 \r\n
        png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");

        bos.close();
        imageStream.close();
        return JsonUtil.getSuccessJson("success", png_base64);
    }


    public List<String> getHighLightedFlows(
            ProcessDefinitionEntity processDefinitionEntity,
            List<HistoricActivityInstance> historicActivityInstances) {

        // 用以保存高亮的线flowId
        List<String> highFlows = new ArrayList<String>();
        // 对历史流程节点进行遍历
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            // 得到节点定义的详细信息
            ActivityImpl activityImpl = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i)
                            .getActivityId());
            // 用以保存后需开始时间相同的节点
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();
            ActivityImpl sameActivityImpl1 = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i + 1)
                            .getActivityId());
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                // 后续第一个节点
                HistoricActivityInstance activityImpl1 = historicActivityInstances
                        .get(j);
                // 后续第二个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances
                        .get(j + 1);
                // 如果第一个节点和第二个节点开始时间相同保存
                if (activityImpl1.getStartTime().equals(
                        activityImpl2.getStartTime())) {
                    ActivityImpl sameActivityImpl2 = processDefinitionEntity
                            .findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    // 有不相同跳出循环
                    break;
                }
            }
            // 取出节点的所有出去的线
            List<PvmTransition> pvmTransitions = activityImpl
                    .getOutgoingTransitions();
            // 对所有的线进行遍历
            for (PvmTransition pvmTransition : pvmTransitions) {
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
                        .getDestination();
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }

}