package com.koql.core

import com.koql.core.dialect.SqlDialect
import com.koql.core.dsl.mysql.config.Configuration


object KOQL {


    fun <T : SqlDialect> create(sqlDialect: T): T {
        return sqlDialect
    }

    fun <T : SqlDialect> create(sqlDialect: T, configuration: Configuration): T {
        return sqlDialect.apply { setConfig(configuration) }
    }

}