package com.koql.core.dialect

import com.koql.core.statement.const.Disdinct
import com.koql.core.statement.dql.SelectImpl
import com.koql.core.statement.structure.SelectExpr
import com.koql.core.config.Configuration

interface SqlDialect


class DefautSql(var configuration: Configuration = Configuration()) :
    SqlDialect {


    fun <T> select(vararg fields: SelectExpr<T>): SelectImpl {
        val select = SelectImpl(configuration)
            .apply { selectExprs.addAll(fields) }

        return select
    }

    fun <T> selectDistinct(vararg fields: SelectExpr<T>): SelectImpl {
        val select = SelectImpl(configuration)
            .apply {
                selectExprs.addAll(fields)
                distinct = Disdinct.DISTINCT
            }

        return select
    }
}

