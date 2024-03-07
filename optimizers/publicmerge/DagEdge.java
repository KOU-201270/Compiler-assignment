package optimizers.publicmerge;

public class DagEdge {
    private DagNode node;
    private boolean type;

    public DagEdge(DagNode node, boolean type) {
        this.node = node;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DagEdge)) {
            return false;
        }
        return (node.equals(((DagEdge) o).node) &&
                type == ((DagEdge) o).type);
    }
}
