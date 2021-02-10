package com.koql.core.statement.condition

class ConditionConnector(val value : String)

val emptyConnector = ConditionConnector("")

val AND = ConditionConnector("AND")

val OR = ConditionConnector("OR")

val NOT = ConditionConnector("NOT")

val AND_NOT = ConditionConnector("AND NOT")

val OR_NOT = ConditionConnector("OR NOT")

val AND_EXISTS = ConditionConnector("AND EXISTS")

val OR_EXISTS = ConditionConnector("OR EXISTS")

val AND_NOT_EXISTS = ConditionConnector("AND NOT EXISTS")

val OR_NOT_EXISTS = ConditionConnector("OR NOT EXISTS")






