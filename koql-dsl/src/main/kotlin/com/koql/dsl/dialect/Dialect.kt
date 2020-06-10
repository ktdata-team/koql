package com.koql.dsl.dialect

import com.koql.dsl.config.Configuration
import com.koql.dsl.config.ContextConfiguration
import com.koql.dsl.schema.Field
import com.koql.dsl.schema.SelectExpr
import com.koql.dsl.statement.Select
import com.koql.dsl.statement.SelectPhaseI

interface SqlDialect



class DefautSql(var configuration: Configuration = Configuration(), var contextConfiguration: ContextConfiguration = ContextConfiguration()) :
    SqlDialect {



    fun  select(vararg fields: SelectExpr): SelectPhaseI {
        val select = Select(configuration,
            contextConfiguration).apply { addSelectField(*(fields.map { it as Field<*> }.toTypedArray())) }

        return select
    }

    fun  selectDistinct(vararg fields: Field<*>): SelectPhaseI {
        val select = Select(configuration,
            contextConfiguration).apply {
            addSelectField(*(fields.map { it }.toTypedArray()))
            disdinctFlag = true
        }

        return select
    }
}

