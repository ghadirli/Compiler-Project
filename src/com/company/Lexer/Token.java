package com.company.Lexer;

public class Token {
    private String description;
    private TokenTypes tokenType;

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
}

