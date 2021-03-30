package com.koql.core.dialect

import com.koql.core.dsl.mysql.config.Configuration


interface SqlDialect {
    fun setConfig(config: Configuration)
    fun getConfig(): Configuration
}




