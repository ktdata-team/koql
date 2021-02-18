package com.koql.core.statement.dql

import com.koql.core.config.Configuration
import com.koql.core.statement.common.Renderable
import com.koql.core.statement.common.StatementPart
import com.koql.core.statement.condition.Condition
import com.koql.core.statement.structure.Query
import com.koql.core.statement.structure.Table

open class From (
    protected val prefixPart : StatementPart,
    protected val fields: MutableList<Table> = mutableListOf(),
    config : Configuration
) : Query(config) {

    companion object {
        @JvmStatic
        protected val FROM = "FROM"

    }

    open fun where(condition: Condition): Where {
        val where = Where(this , condition , config)
        return where
    }



    open fun join(table: Table): Join {
        val join = Join(this , table , JOIN, config)
        return join
    }

    open fun leftJoin(table: Table): Join {
        val join = Join(this , table , LEFT_JOIN, config)
        return join
    }

    open fun rightJoin(table: Table): Join {
        val join = Join(this , table , RIGHT_JOIN, config)
        return join
    }

    open fun fullJoin(table: Table): Join {
        val join = Join(this , table , FULL_JOIN, config)
        return join
    }

    open fun innerJoin(table: Table): Join {
        val join = Join(this , table , INNER_JOIN, config)
        return join
    }




    override fun render(config: Configuration): String {
        val sql = """${prefixPart.render(config)} $FROM ${fields.joinToString(" , ") { it.render(config) }}"""
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = prefixPart.parameters()
//            .apply {
//                fields.forEach {
//                    putAll(it.parameters())
//                }
//            }


       return params
    }


}