package com.koql.core.dsl.base

import com.koql.core.config.Configuration
import com.koql.core.dialect.SqlDialect
import com.koql.core.dsl.base.dql.Delete
import com.koql.core.dsl.base.dql.Insert
import com.koql.core.dsl.base.dql.Select
import com.koql.core.dsl.base.dql.Update
import com.koql.core.statement.common.Asterisk
import com.koql.core.statement.structure.Field
import com.koql.core.statement.structure.Table

open class DefautSql() :
    SqlDialect {



    companion object {
        @JvmStatic
        fun create(): DefautSql {
            return DefautSql()
        }

        @JvmStatic
        fun create(config: Configuration): DefautSql {
            return DefautSql(config)
        }
    }


    var configuration: Configuration = Configuration()

    constructor(config: Configuration) : this() {
        configuration = config
    }

    override fun getConfig(): Configuration {
        return configuration
    }

    override fun setConfig(config: Configuration) {
        configuration = config
    }

    fun select(vararg fields: Field<*>): Select {
        val select = Select(fields.toMutableList(), config = configuration)
        return select
    }

    fun selectDisdinct(vararg fields: Field<*>): Select {
        val select = Select(fields.toMutableList(), true, configuration)
        return select
    }

    fun selectAll(): Select {
        val select = Select(mutableListOf(Asterisk.Asterisk), config = configuration)
        return select
    }

    fun insertInto(table: Table, vararg fields: Field<*>): Insert {
        val insert = Insert(table, fields.toMutableList(), config = configuration)
        return insert
    }

    fun update(table: Table): Update {
        val update = Update(table, configuration)
        return update
    }

    fun deleteFrom(table: Table): Delete {
        val delete = Delete(table, configuration)
        return delete
    }

}