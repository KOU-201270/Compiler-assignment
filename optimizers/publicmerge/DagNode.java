package optimizers.publicmerge;

import intermediatecode.codes.types.CalcType;

import java.util.ArrayList;

public class DagNode {
    private ArrayList<DagEdge> edges;
    private NodeType type;

    public DagNode(NodeType type) {
        edges = new ArrayList<>();
        this.type = type;
    }

    public void add(DagNode node, boolean type) {
        edges.add(new DagEdge(node, type));
    }

    public boolean checkEqual(ArrayList<DagEdge> another) {
        for (DagEdge edge : edges) {
            boolean has = false;
            for (DagEdge anotherEdge : another) {
                if (anotherEdge.equals(edge)) {
                    has = true;
                }
            }
            if (!has) {
                return false;
            }
        }
        for (DagEdge anotherEdge : another) {
            boolean has = false;
            for (DagEdge edge : edges) {
                if (edge.equals(anotherEdge)) {
                    has = true;
                }
            }
            if (!has) {
                return false;
            }
        }
        return true;
    }
    public NodeType getType() {
        return type;
    }
}
