package com.koql.core.statement.dql

import com.koql.core.Sql
import com.koql.core.condition.Condition
import com.koql.core.statement.const.Disdinct
import com.koql.core.statement.structure.ColumnSet
import com.koql.core.statement.structure.SelectExpr
import com.koql.core.config.Configuration
import com.koql.core.statement.const.MainAction


open class SelectImpl(val configuration: Configuration) {


    val selectExprs = mutableListOf<SelectExpr<*>>()
    var distinct = Disdinct.NONE
    val fromTables = mutableListOf<ColumnSet>()
    var whereCondition : Condition? = null

    fun <T> select(vararg fields: SelectExpr<T>): SelectImpl {
        val select = this
            .apply { selectExprs.addAll(fields) }

        return select
    }

    fun <T> selectDistinct(vararg fields: SelectExpr<T>): SelectImpl {
        val select = this
            .apply {
                selectExprs.addAll(fields)
                distinct = Disdinct.DISTINCT
            }

        return select
    }


    fun from(vararg tables: ColumnSet): SelectImpl {
        val select = this
            .apply { fromTables.addAll(tables) }
        return select
    }

    fun where(conditions: Condition): SelectImpl {
        whereCondition = conditions
        return this
    }

    fun render() : Sql {

        val param = mutableListOf<Any?>()

        val str = """
            ${MainAction.SELECT} $distinct ( ${selectExprs
            .map {it.render(configuration) }
            .map { it.also { param.addAll(it.params) }.sqlStr }
            .joinToString(" , ")
        } ) ${MainAction.FROM} ( ${fromTables .map {it.render(configuration) }
            .map { it.also { param.addAll(it.params) }.sqlStr }
            .joinToString(" , ")} ) ${ whereCondition?.let { """${MainAction.WHERE} ${it.render(configuration).also { sql -> param.addAll(sql.params) }.sqlStr}""" } ?: ""} 
        """.trimIndent()

        return Sql().apply {
            sqlStr = str
            params = param
            prepared = configuration.preparedSql
        }
    }

    fun getSql() : Sql {
        return render()
    }

}