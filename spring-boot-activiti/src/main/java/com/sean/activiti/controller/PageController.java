package com.sean.activiti.controller;

import com.sean.activiti.util.Status;
import com.sean.activiti.vo.KeyValue;
import com.sean.activiti.vo.MyForm;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.impl.persistence.entity.HistoricDetailVariableInstanceUpdateEntity;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class PageController {

    @Autowired
    private FormService formService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    /**
     * 编辑流程
     * @return
     */
    @GetMapping("editor")
    public String editor(){
        return "/modeler";
    }

    /**
     * 开启流程填写表单页面
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/startProcess/{id}")
    public String startProcess(@PathVariable("id") String id, org.springframework.ui.Model model) {

        //按照流程定义ID加载流程开启时候需要的表单信息
        StartFormData startFormData = formService.getStartFormData(id);
        List<FormProperty> formProperties = startFormData.getFormProperties();

        //流程定义ID
        model.addAttribute("processesId", id);
        model.addAttribute("form", formProperties);

        return "/startProcess";
    }

    /**
     * 查询我的任务
     * @param model
     * @return
     */
    @RequestMapping("/task")
    public String toTaskList(org.springframework.ui.Model model) {
        List<Task> list = taskService.createTaskQuery().taskAssignee(Status.ASSIGNEE_USER_ID).list();
        model.addAttribute("list", list);
        return "/task";
    }

    /**
     * 任务详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/task/details/{taskId}")
    public String toTaskDetails(@PathVariable("taskId") String id, org.springframework.ui.Model model) {

        Map<String, Object> map = new HashMap<>();
        //当前任务
        Task task = this.taskService.createTaskQuery().taskId(id).singleResult();
        String processInstanceId = task.getProcessInstanceId();

        TaskFormData taskFormData = this.formService.getTaskFormData(id);
        List<FormProperty> list = taskFormData.getFormProperties();
        map.put("task", task);
        map.put("list", list);
        map.put("history", assembleProcessForm(processInstanceId));

        model.addAllAttributes(map);

        return "/taskDetails";
    }

    /**
     * 组装表单过程的表单信息
     * @param processInstanceId
     * @return
     */
    public List<MyForm> assembleProcessForm(String processInstanceId) {

        List<HistoricActivityInstance> historys = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();

        List<MyForm> myform = new ArrayList<>();

        for (HistoricActivityInstance activity : historys) {

            String actInstanceId = activity.getId();
            MyForm form = new MyForm();
            form.setActName(activity.getActivityName());
            form.setAssignee(activity.getAssignee());
            form.setProcInstId(activity.getProcessInstanceId());
            form.setTaskId(activity.getTaskId());
            //查询表单信息

            List<KeyValue> maps = new LinkedList<KeyValue>();

            List<HistoricDetail> processes = historyService.createHistoricDetailQuery().activityInstanceId(actInstanceId).list();
            for (HistoricDetail process : processes) {
                HistoricDetailVariableInstanceUpdateEntity pro = (HistoricDetailVariableInstanceUpdateEntity) process;

                KeyValue keyValue = new KeyValue();

                keyValue.setKey(pro.getName());
                keyValue.setValue(pro.getTextValue());

                maps.add(keyValue);
            }
            form.setProcess(maps);

            myform.add(form);
        }

        return myform;
    }

    /**
     * 查询我的已办任务
     * @param model
     * @return
     */
    @RequestMapping("/task/history")
    public String taskHistoryList(org.springframework.ui.Model model) {
        List list = historyService.createHistoricTaskInstanceQuery().taskAssignee(Status.ASSIGNEE_USER_ID).list();
        model.addAttribute("list", list);
        return "/task-history";
    }

    /**
     * 查询我的已办任务
     * @param model
     * @return
     */
    @RequestMapping("/task/history/details/{taskId}")
    public String taskHistoryDetails(@PathVariable("taskId") String taskId, org.springframework.ui.Model model) {
//        Task task = historyService.createHistoricTaskInstanceQuery().taskId(taskId);
        model.addAttribute("task", historyService.createHistoricTaskInstanceQuery().taskId(taskId));
        return "/task-history-details";
    }

}
