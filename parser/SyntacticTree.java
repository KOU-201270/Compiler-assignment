package parser;

import java.io.IOException;
import java.io.OutputStream;

public class SyntacticTree {
    private Node root;

    public SyntacticTree(Node root) {
        this.root = root;
    }

    public void print(OutputStream output) throws IOException {
        root.print(output);
    }

    public Node getRoot() {
        return root;
    }

    private boolean cleanDfs(Node cur) {
        for (int i = 0; i < cur.getSons().size(); i++) {
            if (cleanDfs(cur.getSons().get(i))) {
                cur.getSons().remove(i);
                i--;
            }
        }
        if (cur.getType() != SyntacticType.Token
                && cur.getSons().size() == 0) {
            return true;
        }
        return false;
    }

    public void clean() {
        cleanDfs(root);
    }

}
