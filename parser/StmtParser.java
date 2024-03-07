package parser;

import lexer.Token;
import lexer.TokenList;
import lexer.TokenType;

public class StmtParser {
    public StmtParser() {
    }

    private static int judgeType(TokenList tokens) {
        int tmp = tokens.getCur();
        boolean isAssign = false;
        boolean isGetInt = false;
        if (tmp < tokens.size() &&
                tokens.get(tmp).getType() == TokenType.IDENFR) {
            tmp++;
            int bracketDepth = 0;
            while (tmp < tokens.size()) {
                if (tokens.get(tmp).getType() == TokenType.SEMICN) {
                    break;
                }
                if (tokens.get(tmp).getType() == TokenType.LBRACK) {
                    bracketDepth++;
                    tmp++;
                    continue;
                }
                if (tokens.get(tmp).getType() == TokenType.RBRACK) {
                    bracketDepth--;
                    tmp++;
                    continue;
                }
                if (tokens.get(tmp).getType() == TokenType.ASSIGN) {
                    isAssign = true;
                    if (tmp + 1 < tokens.size() &&
                            tokens.get(tmp + 1).getType() == TokenType.GETINTTK) {
                        isGetInt = true;
                        isAssign = false;
                    }
                    break;
                }
                if (bracketDepth == 0) {
                    break;
                }
                tmp++;
            }
        }
        if (isAssign) {
            return 1;
        }
        if (isGetInt) {
            return 2;
        }
        return 0;
    }

    public static Node parse(TokenList tokens) {
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.LBRACE) {
            return parseBlock(tokens);
        }
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.IFTK) {
            return parseIf(tokens);
        }
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.WHILETK) {
            return parseWhile(tokens);
        }
        if (tokens.hasNext() &&
                (tokens.peek().getType() == TokenType.BREAKTK ||
                        tokens.peek().getType() == TokenType.CONTINUETK)) {
            Node result = new Node(SyntacticType.Stmt, null);
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.SEMICN) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
            }
            return result;
        }
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.RETURNTK) {
            return parseReturn(tokens);
        }
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.PRINTFTK) {
            return parsePrintf(tokens);
        }
        int type = judgeType(tokens);
        if (type == 1) {
            return parseAssign(tokens);
        }
        if (type == 2) {
            return parseGetInt(tokens);
        }
        return parseExp(tokens);
    }

    private static Node parseBlock(TokenList tokens) {
        Node result = new Node(SyntacticType.Stmt, null);
        result.add(BlockParser.parse(tokens));
        return result;
    }

    private static Node parseIf(TokenList tokens) {
        Node result = new Node(SyntacticType.Stmt, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.IFTK) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.LPARENT) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));

                result.add(CondParser.parse(tokens));

                if (tokens.hasNext() &&
                        tokens.peek().getType() == TokenType.RPARENT) {
                    token = tokens.getNext();
                    result.add(new Node(SyntacticType.Token, token));

                }

                result.add(StmtParser.parse(tokens));

                if (tokens.hasNext() &&
                        tokens.peek().getType() == TokenType.ELSETK) {
                    token = tokens.getNext();
                    result.add(new Node(SyntacticType.Token, token));

                    result.add(StmtParser.parse(tokens));
                }
            }
        }
        return result;
    }

    private static Node parseWhile(TokenList tokens) {
        Node result = new Node(SyntacticType.Stmt, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.WHILETK) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.LPARENT) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));

                result.add(CondParser.parse(tokens));

                if (tokens.hasNext() &&
                        tokens.peek().getType() == TokenType.RPARENT) {
                    token = tokens.getNext();
                    result.add(new Node(SyntacticType.Token, token));
                }

                result.add(StmtParser.parse(tokens));
            }
        }
        return result;
    }

    private static Node parseReturn(TokenList tokens) {
        Node result = new Node(SyntacticType.Stmt, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.RETURNTK) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            if (tokens.hasNext() &&
                    tokens.peek().getType() != TokenType.SEMICN) {
                result.add(ExpParser.parse(tokens));
            }

            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.SEMICN) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
            }
        }
        return result;
    }

    private static Node parsePrintf(TokenList tokens) {
        Node result = new Node(SyntacticType.Stmt, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.PRINTFTK) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.LPARENT) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));

                if (tokens.hasNext() &&
                        tokens.peek().getType() == TokenType.STRCON) {
                    token = tokens.getNext();
                    result.add(new Node(SyntacticType.Token, token));

                    while (tokens.hasNext() &&
                            tokens.peek().getType() == TokenType.COMMA) {
                        token = tokens.getNext();
                        result.add(new Node(SyntacticType.Token, token));

                        result.add(ExpParser.parse(tokens));
                    }

                    if (tokens.hasNext() &&
                            tokens.peek().getType() == TokenType.RPARENT) {
                        token = tokens.getNext();
                        result.add(new Node(SyntacticType.Token, token));
                    }

                    if (tokens.hasNext() &&
                            tokens.peek().getType() == TokenType.SEMICN) {
                        token = tokens.getNext();
                        result.add(new Node(SyntacticType.Token, token));
                    }
                }
            }
        }
        return result;
    }

    private static Node parseAssign(TokenList tokens) {
        Node result = new Node(SyntacticType.Stmt, null);
        result.add(LValParser.parse(tokens));
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.ASSIGN) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            result.add(ExpParser.parse(tokens));

            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.SEMICN) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));
            }
        }
        return result;
    }

    private static Node parseGetInt(TokenList tokens) {
        Node result = new Node(SyntacticType.Stmt, null);
        result.add(LValParser.parse(tokens));
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.ASSIGN) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));

            if (tokens.hasNext() &&
                    tokens.peek().getType() == TokenType.GETINTTK) {
                token = tokens.getNext();
                result.add(new Node(SyntacticType.Token, token));

                if (tokens.hasNext() &&
                        tokens.peek().getType() == TokenType.LPARENT) {
                    token = tokens.getNext();
                    result.add(new Node(SyntacticType.Token, token));

                    if (tokens.hasNext() &&
                            tokens.peek().getType() == TokenType.RPARENT) {
                        token = tokens.getNext();
                        result.add(new Node(SyntacticType.Token, token));
                    }

                    if (tokens.hasNext() &&
                            tokens.peek().getType() == TokenType.SEMICN) {
                        token = tokens.getNext();
                        result.add(new Node(SyntacticType.Token, token));
                    }
                }
            }
        }
        return result;
    }

    private static Node parseExp(TokenList tokens) {
        Node result = new Node(SyntacticType.Stmt, null);
        if (tokens.hasNext() &&
                tokens.peek().getType() != TokenType.SEMICN) {
            result.add(ExpParser.parse(tokens));
        }
        if (tokens.hasNext() &&
                tokens.peek().getType() == TokenType.SEMICN) {
            Token token = tokens.getNext();
            result.add(new Node(SyntacticType.Token, token));
        }
        return result;
    }
}
