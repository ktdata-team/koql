package com.koql.core.statement.const

enum class ConditionConnector(val value: String) {
    emptyConnector(""),

    AND("and"),

    OR("or"),

    NOT("not"),

    AND_NOT("and not"),

    OR_NOT("or not"),

    AND_EXISTS("and exists"),

    OR_EXISTS("or exists"),

    AND_NOT_EXISTS("and not exists"),

    OR_NOT_EXISTS("or not exists");

    override fun toString(): String {
        return value
    }
}








