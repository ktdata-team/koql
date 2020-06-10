package com.koql.dsl.const

class ConditionConnector(val value : String)

val emptyConnector = ConditionConnector("")

val AND = ConditionConnector("and")

val OR = ConditionConnector("or")

val NOT = ConditionConnector("not")

val AND_NOT = ConditionConnector("and not")

val OR_NOT = ConditionConnector("or not")

val AND_EXISTS = ConditionConnector("and exists")

val OR_EXISTS = ConditionConnector("or exists")

val AND_NOT_EXISTS = ConditionConnector("and not exists")

val OR_NOT_EXISTS = ConditionConnector("or not exists")






