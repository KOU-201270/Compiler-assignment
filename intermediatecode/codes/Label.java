package intermediatecode.codes;

public class Label extends IntermediateCode {
    private String name;

    public Label(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ":"
                + "\n";
    }
}
