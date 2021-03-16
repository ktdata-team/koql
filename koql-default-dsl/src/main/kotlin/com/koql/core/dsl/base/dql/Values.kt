package com.koql.core.dsl.base.dql

import com.koql.core.config.Configuration
import com.koql.core.statement.common.StatementPart
import com.koql.core.statement.structure.Field
import com.koql.core.statement.structure.SqlStatement

open class Values(
    protected val prefixPart : StatementPart,
    protected val fields: MutableList<Field<*>> = mutableListOf(),
    config : Configuration
) : SqlStatement(config){
    companion object {
        @JvmStatic
        protected val VALUES = "VALUES"

    }

    override fun render(config: Configuration): String {
        val sql = """${prefixPart.render(config)} ${VALUES} ( ${fields.joinToString(" , ") { it.render(config) }} ) """
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = prefixPart.parameters()
            .apply {
                fields.forEach {
                    putAll(it.parameters())
                }
            }

        return params
    }


}