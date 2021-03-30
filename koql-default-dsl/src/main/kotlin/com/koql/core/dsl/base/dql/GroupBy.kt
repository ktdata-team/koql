package com.koql.core.dsl.base.dql

import com.koql.core.config.Configuration
import com.koql.core.statement.common.StatementPart
import com.koql.core.statement.structure.Field
import com.koql.core.dsl.base.structure.Query


open class GroupBy(
    protected val prefixPart : StatementPart,
    protected val fields: MutableList<Field<*>> = mutableListOf(),
     config : Configuration
) : Query(config){

    companion object {
        @JvmStatic
        protected val GROUP_BY = "GROUP BY"

    }


    override fun render(config: Configuration): String {
        val sql = """${prefixPart.render(config)} $GROUP_BY ${fields.joinToString(" , ") { it.render(config) }} """
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

