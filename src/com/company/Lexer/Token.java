package com.company.Lexer;

public class Token {
    private String description;
    private TokenTypes tokenType;
    private int lineNumber;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TokenTypes getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenTypes tokenType) {
        this.tokenType = tokenType;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}

