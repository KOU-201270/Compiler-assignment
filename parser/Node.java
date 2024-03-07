package parser;

import lexer.Token;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Node {
    private SyntacticType type;
    private Token token;
    private Node father;
    private ArrayList<Node> sons;

    public Node(SyntacticType type, Token token) {
        this.type = type;
        this.token = token;
        father = null;
        sons = new ArrayList<>();
    }

    public void add(Node son) {
        sons.add(son);
        son.father = this;
    }

    public Node getFather() {
        return father;
    }

    public SyntacticType getType() {
        return type;
    }

    public Token getToken() {
        return token;
    }

    public ArrayList<Node> getSons() {
        return sons;
    }

    public void print(OutputStream output) throws IOException {
        for (Node son : sons) {
            son.print(output);
        }
        if (type == SyntacticType.Token) {
            output.write(token.toString().getBytes(StandardCharsets.UTF_8));
            output.write('\n');
            return;
        }
        if (type == SyntacticType.BlockItem || type == SyntacticType.Decl
                || type == SyntacticType.BType) {
            return;
        }
        output.write('<');
        output.write(type.toString().getBytes(StandardCharsets.UTF_8));
        output.write('>');
        output.write('\n');
    }
}
