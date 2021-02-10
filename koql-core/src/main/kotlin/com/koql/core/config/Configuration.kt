package com.koql.core.config

data class Configuration(
    val fieldSeparationCharacter: String = "`",
    val valueSeparationCharacter: String = "'",
    val renderParent : Boolean = false
) {
}


