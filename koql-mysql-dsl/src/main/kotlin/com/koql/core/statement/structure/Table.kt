package com.koql.core.statement.structure


import com.koql.core.dsl.mysql.config.Configuration
import com.koql.core.dsl.mysql.config.RenderConfig
import com.koql.core.statement.common.Renderable

interface TableRef : Renderable

abstract class Table (val name : String) : TableRef {

    abstract val schema : Schema


    override fun render(config: Configuration, rendConfiguration: RenderConfig): String {
        return "${config.fieldSeparationCharacter}$name${config.fieldSeparationCharacter}"
    }

    fun <T> register(name: String): Column<T> {
        return Column(name , schema , this)
    }

}