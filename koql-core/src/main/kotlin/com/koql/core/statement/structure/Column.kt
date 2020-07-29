package com.koql.core.statement.structure

import com.koql.core.Sql
import com.koql.core.config.Configuration

class Column<T>(
    /** Table where the columns is declared. */
    val table: Table,
    /** Name of the column. */
    val name: String

) : SelectExpr<T>() {





    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Column<*>) return false
        if (!super.equals(other)) return false

        if (table != other.table) return false
        if (name != other.name) return false


        return true
    }

    override fun hashCode(): Int = table.hashCode() * 31 + name.hashCode()



    override fun render(configuration: Configuration): Sql {

        var str = configuration.fieldSeparationCharacter + name + configuration.fieldSeparationCharacter


        if(configuration.renderParent) {
            str = (table?.render(configuration)?.sqlStr?.let { "$it." }  ?: "" )+ str
        }

        val sql = Sql().apply {
            sqlStr = str
        }
        return sql
    }
}