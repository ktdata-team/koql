package com.koql.core.dsl.mysql.config

data class Configuration(
    val fieldSeparationCharacter: String = "",
    val valueSeparationCharacter: String = "\"",
    val renderParent : Boolean = false
) {
}


