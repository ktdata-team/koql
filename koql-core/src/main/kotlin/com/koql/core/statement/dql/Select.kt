package com.koql.core.statement.dql

import com.koql.core.config.Configuration
import com.koql.core.statement.common.Renderable
import com.koql.core.statement.common.StatementPart
import com.koql.core.statement.condition.Condition
import com.koql.core.statement.structure.Field
import com.koql.core.statement.structure.Query
import com.koql.core.statement.structure.Table

open class Select(
    protected val fields: MutableList<Field<*>> = mutableListOf(),
    protected var distinct : Boolean = false,
     config : Configuration
    ) : Query(config) {

    companion object {
        @JvmStatic
        protected val SELECT = "SELECT"

        @JvmStatic
        protected val DESDINCT = "DESDINCT"
    }


    open fun from(vararg fields: Table): From {
        val from = From(this , fields.toMutableList() , config)
        return from
    }

    open fun where(condition: Condition): Where {
        val where = Where(this , condition , config)
        return where
    }



    override fun render(config : Configuration): String {
        val sql = """$SELECT ${if (distinct) {DESDINCT}  else {""}  } ${fields.joinToString(" , ") { it.render(config) }}"""
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