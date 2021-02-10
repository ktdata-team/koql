package com.koql.core.statement.structure

import com.koql.core.config.Configuration

open class Column<T> (val name : String,
                      val schema :Schema,
                      val table: Table
) : Field<T>() {






    override fun render(config: Configuration): String {

        if(config.renderParent){
            return "${schema.render(config)}.${table.render(config)}.${config.fieldSeparationCharacter}$name${config.fieldSeparationCharacter}"
        }


        return "${config.fieldSeparationCharacter}$name${config.fieldSeparationCharacter}"
    }

    override fun parameters(): MutableMap<String, Any?> {
        return mutableMapOf()
    }


}