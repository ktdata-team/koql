package com.koql.core.statement.structure

import com.koql.core.config.Configuration
import com.koql.core.statement.common.Renderable

abstract class Table (val name : String) : Renderable {

    abstract val schema : Schema


    override fun render(config: Configuration): String {
        return "${config.fieldSeparationCharacter}$name${config.fieldSeparationCharacter}"
    }

    fun <T> register(name: String): Column<T> {
        return Column(name , schema , this)
    }

}