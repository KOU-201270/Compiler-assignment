package parser;

import lexer.TokenList;

import java.io.IOException;
import java.io.OutputStream;

public class Parser {
    private SyntacticTree tree;

    public Parser(TokenList tokens) {
        tree = new SyntacticTree(CompUnitParser.parse(tokens));
        tree.clean();
    }

    public SyntacticTree getTree() {
        return tree;
    }

    public void print(OutputStream output) throws IOException {
        tree.print(output);
    }
}
