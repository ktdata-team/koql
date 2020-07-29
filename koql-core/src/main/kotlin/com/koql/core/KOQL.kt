package com.koql.core

import com.koql.core.dialect.SqlDialect

object KOQL {

    fun <T : SqlDialect> dialect(dialect: T): T {
        return dialect
    }


}