package com.koql.dsl.config

data class Configuration(
    val preparedSql: Boolean = true,
    val fieldSeparationCharacter: String = "",
    val valueSeparationCharacter: String = "",
    val renderParent : Boolean = false
) {
}


data class ContextConfiguration(
    val renderParent : Boolean = false
) {
}