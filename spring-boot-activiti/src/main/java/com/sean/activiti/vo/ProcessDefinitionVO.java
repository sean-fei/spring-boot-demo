package com.sean.activiti.vo;

import org.activiti.engine.repository.ProcessDefinition;

import java.util.Objects;

public class ProcessDefinitionVO {

    private String id;

    private String name;

    private String key;

    private int version;

    private String category;

    private String description;

    private String resourceName;

    private String deploymentId;

    private String diagramResourceName;

    private boolean hasStartFormKey;

    private boolean hasGraphicalNotation;

    private boolean isSuspended;

    private String tenantId;

    public ProcessDefinitionVO() {

    }

    public static ProcessDefinitionVO toVO(ProcessDefinition p) {
        ProcessDefinitionVO vo = new ProcessDefinitionVO();
        vo.setId(p.getId());
        vo.setCategory(p.getCategory());
        vo.setKey(p.getKey());
        vo.setName(p.getName());
        vo.setVersion(p.getVersion());
        vo.setDeploymentId(p.getDeploymentId());
        vo.setDescription(p.getDescription());
        vo.setDiagramResourceName(p.getDiagramResourceName());
        vo.setHasGraphicalNotation(p.hasGraphicalNotation());
        vo.setResourceName(p.getResourceName());
        vo.setHasStartFormKey(p.hasStartFormKey());
        vo.setSuspended(p.isSuspended());
        vo.setTenantId(p.getTenantId());
        return vo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getDiagramResourceName() {
        return diagramResourceName;
    }

    public void setDiagramResourceName(String diagramResourceName) {
        this.diagramResourceName = diagramResourceName;
    }

    public boolean isHasStartFormKey() {
        return hasStartFormKey;
    }

    public void setHasStartFormKey(boolean hasStartFormKey) {
        this.hasStartFormKey = hasStartFormKey;
    }

    public boolean isHasGraphicalNotation() {
        return hasGraphicalNotation;
    }

    public void setHasGraphicalNotation(boolean hasGraphicalNotation) {
        this.hasGraphicalNotation = hasGraphicalNotation;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public void setSuspended(boolean suspended) {
        isSuspended = suspended;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessDefinitionVO that = (ProcessDefinitionVO) o;
        return version == that.version &&
                hasStartFormKey == that.hasStartFormKey &&
                hasGraphicalNotation == that.hasGraphicalNotation &&
                isSuspended == that.isSuspended &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(key, that.key) &&
                Objects.equals(category, that.category) &&
                Objects.equals(description, that.description) &&
                Objects.equals(resourceName, that.resourceName) &&
                Objects.equals(deploymentId, that.deploymentId) &&
                Objects.equals(diagramResourceName, that.diagramResourceName) &&
                Objects.equals(tenantId, that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, key, version, category, description, resourceName, deploymentId, diagramResourceName, hasStartFormKey, hasGraphicalNotation, isSuspended, tenantId);
    }

    @Override
    public String toString() {
        return "ProcessDefinitionVO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", version=" + version +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", deploymentId='" + deploymentId + '\'' +
                ", diagramResourceName='" + diagramResourceName + '\'' +
                ", hasStartFormKey=" + hasStartFormKey +
                ", hasGraphicalNotation=" + hasGraphicalNotation +
                ", isSuspended=" + isSuspended +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
