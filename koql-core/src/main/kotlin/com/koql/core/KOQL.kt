package com.koql.core

import com.koql.core.config.Configuration
import com.koql.core.dialect.DefautSql
import com.koql.core.dialect.SqlDialect

object KOQL {

    fun create(): DefautSql {
        return DefautSql()
    }

    fun <T : SqlDialect> create(sqlDialect: T): T {
        return sqlDialect
    }

    fun <T : SqlDialect> create(sqlDialect: T, configuration: Configuration): T {
        return sqlDialect.apply { setConfig(configuration) }
    }

}