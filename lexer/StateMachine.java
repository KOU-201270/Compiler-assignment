package lexer;

public class StateMachine {
    private static StateMachine stateMachine = new StateMachine();
    private State root;
    private State cur;

    private StateMachine() {
        root = new State(false, null, null);
        cur = root;
        buildIdentifier();
        buildInteger();
        buildString();
        buildSingle();
        buildDouble();
        buildAnnotation();
    }

    public static StateMachine getInstance() {
        return stateMachine;
    }

    private void buildIdentifier() {
        State identifier = new State(true, TokenType.IDENFR, root);

        root.addAlphas(identifier);
        root.add('_', identifier);

        identifier.addAlphas(identifier);
        identifier.addNumbers(identifier);
        identifier.add('_', identifier);
    }

    private void buildInteger() {
        State integer = new State(true, TokenType.INTCON, root);
        root.addNumbers(integer);
        integer.addNumbers(integer);
    }

    private void buildString() {
        State string = new State(false, null, null);
        State stringEnd = new State(true, TokenType.STRCON, root);
        root.add('\"', string);
        string.add('\"', stringEnd);
    }

    private void buildSingle() {
        root.add('+', new State(true, TokenType.PLUS, root));
        root.add('-', new State(true, TokenType.MINU, root));
        root.add('*', new State(true, TokenType.MULT, root));
        root.add('%', new State(true, TokenType.MOD, root));
        root.add(';', new State(true, TokenType.SEMICN, root));
        root.add(',', new State(true, TokenType.COMMA, root));
        root.add('(', new State(true, TokenType.LPARENT, root));
        root.add(')', new State(true, TokenType.RPARENT, root));
        root.add('[', new State(true, TokenType.LBRACK, root));
        root.add(']', new State(true, TokenType.RBRACK, root));
        root.add('{', new State(true, TokenType.LBRACE, root));
        root.add('}', new State(true, TokenType.RBRACE, root));
    }

    private void buildDouble() {
        State and = new State(false, null, root);
        root.add('&', and);
        and.add('&', new State(true, TokenType.AND, root));

        State or = new State(false, null, root);
        root.add('|', or);
        or.add('|', new State(true, TokenType.OR, root));

        State not = new State(true, TokenType.NOT, root);
        root.add('!', not);
        not.add('=', new State(true, TokenType.NEQ, root));

        State lss = new State(true, TokenType.LSS, root);
        root.add('<', lss);
        lss.add('=', new State(true, TokenType.LEQ, root));

        State gre = new State(true, TokenType.GRE, root);
        root.add('>', gre);
        gre.add('=', new State(true, TokenType.GEQ, root));

        State assign = new State(true, TokenType.ASSIGN, root);
        root.add('=', assign);
        assign.add('=', new State(true, TokenType.EQL, root));
    }

    private void buildAnnotation() {
        State div = new State(true, TokenType.DIV, root);
        root.add('/', div);

        State annotationLn = new State(false, null, null);
        div.add('/', annotationLn);
        annotationLn.add('\n', new State(true, TokenType.ANTTTK, root));

        State annotationPr = new State(false, null, null);
        div.add('*', annotationPr);
        State annotationEnd = new State(false, null, annotationPr);
        annotationPr.add('*', annotationEnd);
        annotationEnd.add('*', annotationEnd);
        annotationEnd.add('/', new State(true, TokenType.ANTTTK, root));
    }

    public State peek(char ch) {
        return cur.walk(ch);
    }

    public boolean isEnd(char ch) {
        return cur.walk(ch) == root;
    }

    public boolean exists() {
        return cur.isExists();
    }

    public void process(char ch) {
        cur = cur.walk(ch);
    }

    public TokenType getType() {
        return cur.getType();
    }

    public void initialize() {
        cur = root;
    }
}
