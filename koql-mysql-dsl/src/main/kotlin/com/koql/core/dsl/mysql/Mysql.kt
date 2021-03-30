package com.koql.core.dsl.mysql


import com.koql.core.dialect.SqlDialect
import com.koql.core.dsl.mysql.config.Configuration
import com.koql.core.dsl.mysql.config.RenderConfig
import com.koql.core.dsl.mysql.dql.*
import com.koql.core.statement.common.Asterisk
import com.koql.core.statement.structure.Field


open class Mysql() :
    SqlDialect {



    companion object {

        @JvmStatic
        fun mysqlConfiguration(): Configuration {
            return Configuration(
                fieldSeparationCharacter = "`",
                valueSeparationCharacter = "'",
                renderParent = true
            )
        }

        @JvmStatic
        fun create(): Mysql {
            return Mysql()
        }

        @JvmStatic
        fun create(config: Configuration): Mysql {
            return Mysql(config)
        }
    }


    var configuration: Configuration = mysqlConfiguration()

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
        val select = Select(SelectBuilder(fields = fields.toMutableList() , config = configuration , renderConfig = RenderConfig()))
        return select
    }

    fun select(): SelectDistinct {
        val selectBuilder = SelectBuilder(config = configuration, renderConfig = RenderConfig())
        return selectBuilder
    }



    fun selectAll(): Select {
        val select = Select(SelectBuilder(fields = mutableListOf(Asterisk.Asterisk) , config = configuration, renderConfig = RenderConfig()))
        return select
    }

//    fun insertInto(table: Table, vararg fields: Field<*>): Insert {
//        val insert = Insert(table, fields.toMutableList(), config = configuration)
//        return insert
//    }
//
//    fun update(table: Table): Update {
//        val update = Update(table, configuration)
//        return update
//    }
//
//    fun deleteFrom(table: Table): Delete {
//        val delete = Delete(table, configuration)
//        return delete
//    }

}