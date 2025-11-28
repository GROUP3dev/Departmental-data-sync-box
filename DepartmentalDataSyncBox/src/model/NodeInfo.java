package model;

public class NodeInfo {
    private int departmentId;
    private String nodeIp;

    public NodeInfo(int departmentId, String nodeIp) {
        this.departmentId = departmentId;
        this.nodeIp = nodeIp;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public String getNodeIp() {
        return nodeIp;
    }
}
