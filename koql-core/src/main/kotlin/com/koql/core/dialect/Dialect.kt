package com.koql.core.dialect

import com.koql.core.config.Configuration
import com.koql.core.statement.common.Asterisk
import com.koql.core.statement.common.Renderable
import com.koql.core.statement.dql.Select
import com.koql.core.statement.structure.Field

interface SqlDialect {
    fun setConfig(config: Configuration)
    fun getConfig(): Configuration
}


class DefautSql() :
    SqlDialect {

    var configuration: Configuration = Configuration()

    constructor(config: Configuration) : this(){
        configuration = config
    }

    override fun getConfig(): Configuration {
        return configuration
    }

    override fun setConfig(config: Configuration) {
        configuration = config
    }

    fun select(vararg fields : Field<*>) : Select {
        val select = Select(fields.toMutableList() , config = configuration)
        return select
    }

    fun selectDisdinct(vararg fields : Field<*>) : Select {
        val select = Select(fields.toMutableList() , true , configuration)
        return select
    }

    fun selectAll() : Select {
        val select = Select(mutableListOf(Asterisk.Asterisk) , config = configuration)
        return select
    }


}

