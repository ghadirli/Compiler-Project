package com.company;

public class Token {
    String description;
    TokenTypes tokenTypes;



}

enum TokenTypes{
    ID, NUM, KEYWORD, EOF, SYMBOL, COMMENT, WHITESPACE
}