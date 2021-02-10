package com.koql.core.statement.dql

import com.koql.core.config.Configuration
import com.koql.core.statement.common.StatementPart
import com.koql.core.statement.structure.Field
import com.koql.core.statement.structure.Query

enum class OrderSort {
    ASC , DESC
}
open class OrderBy(
    protected val prefixPart : StatementPart,
    protected val fields: MutableList<Field<*>> = mutableListOf(),
     config : Configuration
) : Query(config){

    companion object {
        @JvmStatic
        protected val ORDER_BY = "ORDER BY"

    }

    open fun asc(): OrderBySort {
        return OrderBySort(this , OrderSort.ASC , config)
    }

    open fun desc(): OrderBySort {
        return OrderBySort(this , OrderSort.DESC , config)
    }

    override fun render(config: Configuration): String {
        val sql = """${prefixPart.render(config)} $ORDER_BY ${fields.joinToString(" , ") { it.render(config) }} """
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

open class OrderBySort(
    protected val prefixPart : StatementPart,
    protected val sort : OrderSort,
     config : Configuration
): Query(config) {



    override fun render(config: Configuration): String {
        val sql = """${prefixPart.render(config)} ${sort.name} """
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = prefixPart.parameters()

        return params
    }

}