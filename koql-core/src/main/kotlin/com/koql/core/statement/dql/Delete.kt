package com.koql.core.statement.dql

import com.koql.core.config.Configuration
import com.koql.core.statement.common.StatementPart
import com.koql.core.statement.condition.Condition
import com.koql.core.statement.structure.SqlStatement
import com.koql.core.statement.structure.Table

open class Delete (
    protected val table: Table,
    config: Configuration
) : SqlStatement(config) {

    companion object {
        @JvmStatic
        protected val DELETE_FROM = "DELETE FROM"
    }

    open fun where(condition: Condition): DeleteWhere {
        val where = DeleteWhere(this , condition , config)
        return where
    }

    override fun render(config : Configuration): String {
        val sql = """${DELETE_FROM}  ${table.render(config) } """
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = mutableMapOf<String, Any?>()
        return params
    }
}


open class DeleteWhere(
    protected val prefixPart: StatementPart,
    protected val condition: Condition,
    config: Configuration
): SqlStatement(config){
    companion object {
        @JvmStatic
        protected val WHERE = "WHERE"
    }



    override fun render(config: Configuration): String {
        val sql = """${prefixPart.render(config)} ${WHERE} ${condition.render(config)}"""
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