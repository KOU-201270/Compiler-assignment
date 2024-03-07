package symboltable;

import intermediatecode.codes.Value;
import symboltable.symbols.Symbol;
import symboltable.symbols.VarQualifier;
import symboltable.symbols.Variable;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final SymbolTable father;
    private final HashMap<String, Symbol> nameToSymbol;
    private int cnt;

    public SymbolTable(SymbolTable father) {
        this.father = father;
        nameToSymbol = new HashMap<>();
        if (father == null) {
            cnt = 0;
        } else {
            cnt = father.cnt;
        }
    }

    public SymbolTable getFather() {
        return father;
    }

    public boolean add(Symbol symbol) {
        if (nameToSymbol.containsKey(symbol.getName())) {
            return false;
        }
        nameToSymbol.put(symbol.getName(), symbol);
        return true;
    }

    public Symbol get(String name) {
        SymbolTable cur = this;
        while (cur != null) {
            if (cur.nameToSymbol.containsKey(name)) {
                return cur.nameToSymbol.get(name);
            } else {
                cur = cur.father;
            }
        }
        return null;
    }

    public String getTemp() {
        String tmp = "#" + cnt;
        while (get(tmp) != null) {
            cnt++;
            tmp = "#" + cnt;
        }
        return tmp;
    }

    public void print() {
        for (Map.Entry<String, Symbol> entry : nameToSymbol.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();
    }

    public Value getNewTemp(int dimension, int capacity) {
        String name = getTemp();
        Variable var = new Variable(name, 0, VarQualifier.VAR, true);
        add(var);
        return new Value(var);
    }

}
