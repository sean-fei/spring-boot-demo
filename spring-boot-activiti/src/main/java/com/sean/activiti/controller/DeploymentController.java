package com.sean.activiti.controller;

import com.sean.activiti.base.controller.RestServiceController;
import com.sean.activiti.util.Result;
import com.sean.activiti.vo.DeploymentResponse;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("deployments")
public class DeploymentController implements RestServiceController<Deployment, String> {
    @Autowired
    RepositoryService repositoryService;

    @Override
    public Object getOne(@PathVariable("id") String id) {
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(id).singleResult();
        return Result.buildResult().setObjData(new DeploymentResponse(deployment));
    }

    @Override
    public Object getList(@RequestParam(value = "rowSize", defaultValue = "1000", required = false) Integer rowSize, @RequestParam(value = "page", defaultValue = "1", required = false) Integer page) {
        List<Deployment> deployments = repositoryService.createDeploymentQuery()
                .listPage(rowSize * (page - 1), rowSize);
        long count = repositoryService.createDeploymentQuery().count();
        List<DeploymentResponse> list = new ArrayList<>();
        for(Deployment deployment: deployments){
            list.add(new DeploymentResponse(deployment));
        }

        return Result.buildResult().setRows(
                Result.Rows.buildRows()
                        .setRowSize(rowSize)
                        .setTotalPages((int) (count/rowSize+1))
                        .setTotalRows(count)
                        .setList(list)
                        .setCurrent(page)
        );
    }

    @Override
    public Object deleteOne(@PathVariable("id") String id) {
        repositoryService.deleteDeployment(id);
        return Result.buildResult().refresh();
    }

    @Override
    public Object postOne(@RequestBody Deployment entity) {
        return null;
    }

    @Override
    public Object putOne(@PathVariable("id") String s, @RequestBody Deployment entity) {
        return null;
    }

    @Override
    public Object patchOne(@PathVariable("id") String s, @RequestBody Deployment entity) {
        return null;
    }
}