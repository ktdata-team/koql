package com.koql.core.statement.dql

import com.koql.core.statement.const.Disdinct
import com.koql.core.statement.structure.ColumnSet
import com.koql.core.statement.structure.SelectExpr
import com.koql.core.config.Configuration
import com.koql.core.statement.const.MainAction
import com.koql.core.statement.structure.QueryBuilder

open class SelectImpl(val configuration: Configuration) {


    val selectExprs = mutableListOf<SelectExpr<*>>()
    var distinct = Disdinct.NONE
    val fromTables = mutableListOf<ColumnSet>()

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


    fun render() {


        val str = """
            ${MainAction.SELECT} 
        """.trimIndent()
    }

}