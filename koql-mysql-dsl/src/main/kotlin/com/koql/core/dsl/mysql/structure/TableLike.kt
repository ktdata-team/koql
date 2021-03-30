package com.koql.core.dsl.mysql.structure

import com.koql.core.dsl.mysql.config.Configuration
import com.koql.core.dsl.mysql.config.RenderConfig
import com.koql.core.statement.structure.Table
import com.koql.core.statement.structure.TableRef

abstract class MysqlTable(name : String) : Table(name){

    fun partition(vararg partitionName : String) : PartitionTable {
        return PartitionTable(this , partitionName.toMutableList())
    }

    fun alias(aliasName: String): AliasTable {
        return AliasTable(this , aliasName)
    }

    fun `as`(aliasName: String): AliasTable {
        return AliasTable(this , aliasName)
    }



}



open class PartitionTable(
    val table: MysqlTable,
    val partitionNames: MutableList<String>
) : TableRef by table {

    companion object {
        @JvmStatic
        protected val PARTITION  = "PARTITION"

    }



    override fun render(config: Configuration, renderConfiguration: RenderConfig): String {
        return table.render(config, renderConfiguration)+" "+PARTITION+" ("+ partitionNames.joinToString(",") { "${config.fieldSeparationCharacter}$it${config.fieldSeparationCharacter}" } +") "
    }
}

open class AliasTable(
    val table: MysqlTable,
    val aliasName: String
) : TableRef by table {

    companion object {
        @JvmStatic
        protected val AS  = "AS"

    }



    override fun render(config: Configuration , renderConfiguration: RenderConfig): String {
        return table.render(config , renderConfiguration)+" "+AS+" `"+ aliasName+"` "
    }
}