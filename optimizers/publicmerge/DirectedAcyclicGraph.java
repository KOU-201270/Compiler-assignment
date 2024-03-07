package optimizers.publicmerge;

import intermediatecode.codes.FuncCall;
import intermediatecode.codes.Input;
import intermediatecode.codes.IntermediateCode;
import intermediatecode.codes.IntermediateCodeList;
import intermediatecode.codes.Load;
import intermediatecode.codes.PushStack;
import intermediatecode.codes.Quadraple;
import intermediatecode.codes.Save;
import intermediatecode.codes.Value;
import intermediatecode.codes.types.CalcType;
import intermediatecode.codes.types.ValueType;
import parser.Node;
import symboltable.symbols.Array;
import symboltable.symbols.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class DirectedAcyclicGraph {
    private ArrayList<DagNode> nodes;
    private HashMap<Variable, DagNode> varToNode;
    private HashMap<Integer, DagNode> intToNode;
    private HashMap<DagNode, HashSet<Variable>> nodeToVar;

    public DirectedAcyclicGraph() {
        nodes = new ArrayList<>();
        varToNode = new HashMap<>();
        intToNode = new HashMap<>();
        nodeToVar = new HashMap<>();
    }

    private boolean isSingle(NodeType type) {
        return (type == NodeType.NEG ||
                type == NodeType.POS ||
                type == NodeType.NOT ||
                type == NodeType.NUL);
    }

    private boolean swappable(NodeType type) {
        return (type != NodeType.SUB &&
                type != NodeType.DIV &&
                type != NodeType.MOD &&
                type != NodeType.LSS &&
                type != NodeType.LEQ &&
                type != NodeType.MEM);
    }

    private NodeType swap(NodeType type) {
        switch (type) {
            case EQL:
                return NodeType.EQL;
            case NEQ:
                return NodeType.NEQ;
            case LSS:
                return NodeType.GRT;
            case GEQ:
                return NodeType.LEQ;
            case GRT:
                return NodeType.LSS;
            case LEQ:
                return NodeType.GEQ;
            default:
                return type;
        }
    }

    private boolean needToSwap(NodeType type) {
        return (type == NodeType.GRT ||
                type == NodeType.GEQ);
    }

    private NodeType toNodeType(CalcType type) {
        switch (type) {
            case ADD:
                return NodeType.ADD;
            case SUB:
                return NodeType.SUB;
            case MUL:
                return NodeType.MUL;
            case DIV:
                return NodeType.DIV;
            case MOD:
                return NodeType.MOD;
            case NEG:
                return NodeType.NEG;
            case POS:
                return NodeType.POS;
            case NOT:
                return NodeType.NOT;
            case EQL:
                return NodeType.EQL;
            case NEQ:
                return NodeType.NEQ;
            case LSS:
                return NodeType.LSS;
            case GEQ:
                return NodeType.GEQ;
            case GRT:
                return NodeType.GRT;
            case LEQ:
                return NodeType.LEQ;
            default:
                return NodeType.NUL;
        }
    }

    private void setNode(Value val, DagNode node) {
        if (val.getType() == ValueType.VAR) {
            Variable var = val.getVar();
            if (varToNode.containsKey(var)) {
                DagNode last = varToNode.get(var);
                HashSet<Variable> lastVars = nodeToVar.get(last);
                lastVars.remove(var);
            }
            varToNode.put(var, node);
            if (!nodeToVar.containsKey(node)) {
                HashSet<Variable> variables = new HashSet<>();
                nodeToVar.put(node, variables);
            }
            HashSet<Variable> variables =
                    nodeToVar.get(node);
            variables.add(var);
        } else {
            intToNode.put(val.getVal(), node);
        }
    }

    private void setNewNode(Value val) {
        DagNode node = new DagNode(NodeType.NUL);
        nodes.add(node);
        nodeToVar.put(node, new HashSet<>());
        setNode(val, node);
    }

    private DagNode getNode(Value val) {
        if (val.getType() == ValueType.VAR) {
            Variable var = val.getVar();
            if (!varToNode.containsKey(var)) {
                setNewNode(val);
            }
            return varToNode.get(var);
        }
        if (val.getType() == ValueType.NUMBER) {
            int num = val.getVal();
            if (!intToNode.containsKey(num)) {
                setNewNode(val);
            }
            return intToNode.get(num);
        }
        return null;
    }

    private boolean checkNode(DagNode node,
                              DagNode node1, DagNode node2,
                              NodeType type) {
        if (node.getType() != type) {
            return false;
        }
        ArrayList<DagEdge> edges = new ArrayList<>();
        edges.add(new DagEdge(node1, false));
        if (node2 == null) {
            return node.checkEqual(edges);
        }
        if (swappable(type)) {
            edges.add(new DagEdge(node2, false));
        } else {
            edges.add(new DagEdge(node2, true));
        }
        return node.checkEqual(edges);
    }

    private DagNode findNode(Value v1, Value v2,
                             NodeType nodeType) {
        DagNode node1 = getNode(v1);
        DagNode node2 = null;
        NodeType type = nodeType;
        if (v2 != null) {
            node2 = getNode(v2);
        }
        //System.out.println(v1 + " " + v2 + " : " + node1 + " " + node2);
        if (needToSwap(nodeType)) {
            DagNode tmp = node1;
            node1 = node2;
            node2 = tmp;
            type = swap(type);
        }
        for (DagNode node : nodes) {
            if (checkNode(node, node1, node2, type)) {
                return node;
            }
        }
        DagNode node = new DagNode(type);
        if (swappable(type)) {
            node.add(node1, false);
            if (v2 != null) {
                node.add(node2, false);
            }
        } else {
            node.add(node1, false);
            if (v2 != null) {
                node.add(node2, true);
            }
        }
        nodes.add(node);
        nodeToVar.put(node, new HashSet<>());
        return node;
    }

    public boolean merge(IntermediateCodeList codes) {
        boolean changed = false;
        for (int i = 0; i < codes.size(); i++) {
            IntermediateCode code = codes.get(i);
            if (code instanceof Input) {
                setNewNode(((Input) code).getVal());
                continue;
            }
            if (code instanceof Load) {
                NodeType type = NodeType.MEM;
                Value v1 = ((Load) code).getAddress();
                Value v2 = ((Load) code).getOffset();
                DagNode node = findNode(v1, v2,
                        type);
                HashSet<Variable> variables =
                        nodeToVar.get(node);
                setNode(((Load) code).getTar(), node);
                for (Variable var : variables) {
                    //System.out.println(var);
                    if (!var.equals(((Load) code).getTar().getVar())) {
                        codes.set(i, new Quadraple(((Load) code).getTar(),
                                new Value(var), null, CalcType.NUL));
                        changed = true;
                        break;
                    }
                }
                continue;
            }
            if (code instanceof Save) {
                setNewNode(((Save) code).getAddress());
                NodeType type = NodeType.MEM;
                Value v1 = ((Save) code).getAddress();
                Value v2 = ((Save) code).getOffset();
                DagNode node = findNode(v1, v2,
                        type);
                setNode(((Save) code).getVal(), node);
                continue;
            }
            if (code instanceof FuncCall) {
                HashSet<Variable> newNodes = new HashSet<>();
                for (DagNode node : nodes) {
                    HashSet<Variable> vars = nodeToVar.get(node);
                    Iterator<Variable> iterator = vars.iterator();
                    while (iterator.hasNext()) {
                        Variable var = iterator.next();
                        if (!var.isLocal() || (var instanceof Array)) {
                            iterator.remove();
                            varToNode.remove(var);
                            newNodes.add(var);
                        }
                    }
                }
                for (Variable newNode : newNodes) {
                    setNewNode(new Value(newNode));
                }
            }
            if (code instanceof Quadraple) {
                Value v1 = ((Quadraple) code).getV1();
                Value v2 = null;
                NodeType type = toNodeType(((Quadraple) code).getType());
                if (isSingle(type)) {
                    if (v1.getType() == ValueType.RETURN) {
                        setNewNode(((Quadraple) code).getDest());
                        continue;
                    } else {
                        if (((Quadraple) code).getType() == CalcType.NUL) {
                            DagNode node = getNode(v1);
                            setNode(((Quadraple) code).getDest(), node);
                            continue;
                        }
                    }
                } else {
                    v2 = ((Quadraple) code).getV2();
                    if (v1.getType() == ValueType.RETURN ||
                            v2.getType() == ValueType.RETURN) {
                        setNewNode(((Quadraple) code).getDest());
                        continue;
                    }
                }
                DagNode node = findNode(v1, v2,
                        type);
                HashSet<Variable> variables =
                        nodeToVar.get(node);
                setNode(((Quadraple) code).getDest(), node);
                for (Variable var : variables) {
                    if (!var.equals(((Quadraple) code).getDest().getVar())) {
                        ((Quadraple) code).setType(CalcType.NUL);
                        ((Quadraple) code).setV1(new Value(var));
                        ((Quadraple) code).setV2(null);
                        changed = true;
                        break;
                    }
                }
            }
        }
        return changed;
    }
}
