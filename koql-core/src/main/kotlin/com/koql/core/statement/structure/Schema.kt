package com.koql.core.statement.structure

import com.koql.core.Sql
import com.koql.core.config.Configuration


open class Schema(val name: String = "") {


    fun render(configuration: Configuration): Sql {

        val str = configuration.fieldSeparationCharacter + name + configuration.fieldSeparationCharacter

        return Sql().apply {
            sqlStr = str
        }
    }

}