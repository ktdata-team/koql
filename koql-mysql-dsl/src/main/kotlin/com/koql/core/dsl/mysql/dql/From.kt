package com.koql.core.dsl.mysql.dql

import com.koql.core.condition.Condition
import com.koql.core.config.Configuration
import com.koql.core.config.RenderConfiguration
import com.koql.core.dsl.mysql.config.RenderConfig
import com.koql.core.dsl.mysql.structure.QueryBase
import com.koql.core.statement.common.StatementPart
import com.koql.core.statement.structure.TableRef

open class From (
    protected val prefixPart : StatementPart,
    protected val fields: MutableList<TableRef> = mutableListOf(),
    config : Configuration ,
    renderConfig: RenderConfig
) : QueryBase(config , renderConfig) {

    companion object {
        @JvmStatic
        protected val FROM = "FROM"

    }

    open fun where(condition: Condition): Where {
        val where = Where(this , condition , config)
        return where
    }
//
//
//
//    open fun join(table: Table): Join {
//        val join = Join(this , table , JOIN, config)
//        return join
//    }
//
//    open fun leftJoin(table: Table): Join {
//        val join = Join(this , table , LEFT_JOIN, config)
//        return join
//    }
//
//    open fun rightJoin(table: Table): Join {
//        val join = Join(this , table , RIGHT_JOIN, config)
//        return join
//    }
//
//    open fun fullJoin(table: Table): Join {
//        val join = Join(this , table , FULL_JOIN, config)
//        return join
//    }
//
//    open fun innerJoin(table: Table): Join {
//        val join = Join(this , table , INNER_JOIN, config)
//        return join
//    }




    override fun render(config: Configuration , renderConfig: RenderConfiguration): String {
        val sql = """${prefixPart.render(config ,renderConfig)} $FROM ${fields.joinToString(" , ") { it.render(config , renderConfig) }}"""
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = prefixPart.parameters()


       return params
    }


}