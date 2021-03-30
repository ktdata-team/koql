package com.koql.core.dsl.mysql.dql

import com.koql.core.config.Configuration
import com.koql.core.statement.common.StatementPart
import com.koql.core.condition.Condition
import com.koql.core.dsl.mysql.structure.QueryBase

class Where(
    protected val prefixPart: StatementPart,
    protected val condition: Condition,
     config: Configuration
) : QueryBase(config) {

    companion object {
        @JvmStatic
        protected val WHERE = "WHERE"

    }


    override fun render(config: Configuration): String {
        val sql = """${prefixPart.render(config)} $WHERE ${condition.render(config)}"""
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = prefixPart.parameters()
            .apply {
                putAll(condition.parameters())
            }


        return params
    }
}