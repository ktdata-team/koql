package com.koql.core.dsl.base.dql

import com.koql.core.config.Configuration
import com.koql.core.statement.common.StatementPart
import com.koql.core.condition.Condition
import com.koql.core.statement.structure.Field
import com.koql.core.statement.structure.SqlStatement
import com.koql.core.statement.structure.Table

open class Update(
    protected val table: Table,
    config: Configuration
) : SqlStatement(config) {
    companion object {
        @JvmStatic
        protected val UPDATE = "UPDATE"
    }




    override fun render(config : Configuration): String {
        val sql = """${UPDATE}  ${table.render(config) } """
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = mutableMapOf<String, Any?>()
        return params
    }

}


open class UpdateSet(
    protected val prefixPart : StatementPart,
    protected val fields: MutableList<Pair<Field<*> , Field<*>>> = mutableListOf(),
    config : Configuration
): SqlStatement(config){
    companion object {
        @JvmStatic
        protected val SET = "SET"
    }


    open fun where(condition: Condition): UpdateWhere {
        val where = UpdateWhere(this , condition , config)
        return where
    }

    override fun render(config: Configuration): String {
        val sql = """${prefixPart.render(config)} ${SET} ${
            fields.joinToString(",") {
                it.first.render(config) + " = " + it.second.render(
                    config
                )
            }
        }"""
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = prefixPart.parameters()
            .apply {
                fields.forEach {
                    putAll(it.first.parameters())
                    putAll(it.second.parameters())
                }
            }


        return params
    }
}

open class UpdateWhere(
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