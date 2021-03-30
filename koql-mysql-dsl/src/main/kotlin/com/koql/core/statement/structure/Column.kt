package com.koql.core.statement.structure


import com.koql.core.dsl.mysql.config.Configuration
import com.koql.core.dsl.mysql.config.RenderConfig

open class Column<T> (val name : String,
                      val schema : Schema,
                      val table: Table
) : Field<T>() {






    override fun render(config: Configuration, renderConfiguration: RenderConfig): String {

        if (renderConfiguration.hasAliasTable) {
            val alias = renderConfiguration.aliasTableMap[table]
            if (alias != null){
                return "${alias.render(config ,renderConfiguration)}.${config.fieldSeparationCharacter}$name${config.fieldSeparationCharacter}"
            }
        }

        if(config.renderParent){
            return "${schema.render(config)}.${table.render(config ,renderConfiguration)}.${config.fieldSeparationCharacter}$name${config.fieldSeparationCharacter}"
        }


        return "${config.fieldSeparationCharacter}$name${config.fieldSeparationCharacter}"
    }

    override fun parameters(): MutableMap<String, Any?> {
        return mutableMapOf()
    }


}