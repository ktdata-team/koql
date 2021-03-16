package com.koql.core.dsl.base.dql

import com.koql.core.config.Configuration
import com.koql.core.statement.structure.Field
import com.koql.core.statement.structure.SqlStatement
import com.koql.core.statement.structure.Table

open class Insert(
    protected val table: Table,
    protected val fields: MutableList<Field<*>> = mutableListOf(),
    config : Configuration
) : SqlStatement(config) {

    companion object {
        @JvmStatic
        protected val INSERT_INTO = "INSERT INTO"

    }

    open fun values(vararg fields: Field<*>): Values {
        val values = Values(this , fields.toMutableList() , config)
        return values
    }

    override fun render(config : Configuration): String {
        val sql = """${INSERT_INTO} ${table.render(config)} ( ${fields.joinToString(" , ") { it.render(config) }} )"""
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = mutableMapOf<String, Any?>()
            .let { map ->
                fields.forEach {
                    map.putAll(it.parameters())
                }
                map
            }
        return params
    }

}