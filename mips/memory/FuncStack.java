package mips.memory;

import intermediatecode.codes.Value;

import java.util.Stack;

public class FuncStack {
    private int size;
    private Stack<Value> paramStack;

    public FuncStack(int size) {
        this.size = size;
        paramStack = new Stack<>();
    }

    public int allocTemp() {
        size += 4;
        return -size;
    }

    public int getSize() {
        return size;
    }

    public void push(Value val) {
        paramStack.push(val);
    }

    public Value pop() {
        return paramStack.pop();
    }
}
