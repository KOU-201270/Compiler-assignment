package intermediatecode.codes;

public class Jump extends IntermediateCode {
    private String label;

    public Jump(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "jump to " + label
                + "\n";
    }
}
