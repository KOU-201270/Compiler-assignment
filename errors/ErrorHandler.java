package errors;

import errors.exceptions.CompileException;
import lexer.TokenType;
import parser.Node;
import parser.SyntacticTree;
import parser.SyntacticType;
import symboltable.symbols.Function;
import symboltable.SymbolTable;
import symboltable.symbols.Variable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class ErrorHandler {
    private ArrayList<CompileException> exceptions;
    private SymbolTable globalSymbolTable;

    public ErrorHandler(SyntacticTree tree) {
        exceptions = new ArrayList<>();
        globalSymbolTable = new SymbolTable(null);
        process(tree.getRoot(), exceptions, globalSymbolTable, null, 0);
        Collections.sort(exceptions);
    }

    private boolean isDef(SyntacticType type) {
        return (type == SyntacticType.ConstDef ||
                type == SyntacticType.VarDef);
    }

    private boolean isFuncDef(SyntacticType type) {
        return (type == SyntacticType.FuncDef ||
                type == SyntacticType.MainFuncDef);
    }

    private boolean isDecl(SyntacticType type) {
        return (type == SyntacticType.ConstDecl ||
                type == SyntacticType.VarDecl);
    }

    public void process(Node cur, ArrayList<CompileException> exceptions,
                        SymbolTable symbolTable, Function func, int loopDepth) {
        SymbolTable nextSymbolTable = symbolTable;
        int nextLoopDepth = loopDepth;
        Function nextFunc = null;
        if (isFuncDef(cur.getType())) {
            nextFunc = FuncChecker.checkFunc(cur, exceptions, symbolTable);
        }
        if (cur.getType() == SyntacticType.Block) {
            nextSymbolTable = new SymbolTable(symbolTable);
            if (func != null) {
                for (Variable parameter : func.getParameters()) {
                    nextSymbolTable.add(parameter);
                }
                try {
                    BlockChecker.checkBlock(cur, func);
                } catch (CompileException e) {
                    exceptions.add(e);
                }
            }
        }
        if (cur.getType() == SyntacticType.Stmt) {
            try {
                StmtChecker.checkStmt(cur, symbolTable, loopDepth);
            } catch (CompileException e) {
                exceptions.add(e);
            }
            if (cur.getSons().get(0).getType() == SyntacticType.Token &&
                    cur.getSons().get(0).getToken().getType() == TokenType.WHILETK) {
                nextLoopDepth = loopDepth + 1;
            }
        }
        for (Node son : cur.getSons()) {
            process(son, exceptions, nextSymbolTable, nextFunc, nextLoopDepth);
        }
        if (isDef(cur.getType())) {
            handleDef(cur, symbolTable, exceptions);
        }
        if (isDecl(cur.getType())) {
            handleDecl(cur, symbolTable, exceptions);
        }
        if (cur.getType() == SyntacticType.LVal) {
            handleLVal(cur, symbolTable, exceptions);
        }
        if (cur.getType() == SyntacticType.UnaryExp) {
            handleUnaryExp(cur, symbolTable, exceptions);
        }
    }

    private void handleDef(Node cur,
                           SymbolTable symbolTable, ArrayList<CompileException> exceptions) {
        try {
            DefChecker.checkDef(cur, symbolTable);
        } catch (CompileException e) {
            exceptions.add(e);
        }
    }

    private void handleDecl(Node cur,
                           SymbolTable symbolTable, ArrayList<CompileException> exceptions) {
        try {
            DeclChecker.checkDecl(cur);
        } catch (CompileException e) {
            exceptions.add(e);
        }
    }

    private void handleLVal(Node cur,
                           SymbolTable symbolTable, ArrayList<CompileException> exceptions) {
        try {
            LValChecker.checkLVal(cur, symbolTable);
        } catch (CompileException e) {
            exceptions.add(e);
        }
    }

    private void handleUnaryExp(Node cur,
                           SymbolTable symbolTable, ArrayList<CompileException> exceptions) {
        try {
            UnaryExpChecker.checkUnaryExp(cur, symbolTable);
        } catch (CompileException e) {
            exceptions.add(e);
        }
    }

    public void print(OutputStream error) throws IOException {
        for (CompileException exception : exceptions) {
            error.write(exception.toString().getBytes(StandardCharsets.UTF_8));
            error.write('\n');
        }
    }

    public boolean hasError() {
        return !exceptions.isEmpty();
    }
}
