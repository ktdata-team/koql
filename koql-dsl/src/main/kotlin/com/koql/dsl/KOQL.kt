package com.koql.dsl

import com.koql.dsl.dialect.SqlDialect

object KOQL {

    fun <T : SqlDialect> dialect(dialect: T): T {
        return dialect
    }


}