package com.koql.core.dialect

import com.koql.core.config.Configuration
import com.koql.core.statement.common.Asterisk
import com.koql.core.statement.common.Renderable
import com.koql.core.statement.dql.Delete
import com.koql.core.statement.dql.Insert
import com.koql.core.statement.dql.Select
import com.koql.core.statement.dql.Update
import com.koql.core.statement.structure.Field
import com.koql.core.statement.structure.Table

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

    fun insertInto(table: Table , vararg fields : Field<*>) : Insert {
        val insert = Insert(table, fields.toMutableList() , config = configuration)
        return insert
    }

    fun update(table: Table ) : Update {
        val update = Update(table, configuration)
        return update
    }

    fun deleteFrom(table: Table ) : Delete {
        val delete = Delete(table, configuration)
        return delete
    }

}

