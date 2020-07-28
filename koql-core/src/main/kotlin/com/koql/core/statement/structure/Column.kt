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

    override fun toString(): String = "${table.javaClass.name}.$name"


    override fun render(configuration: Configuration): Sql {

        val

        val str = ""


        val sql = Sql(

        )
        TODO("Not yet implemented")
    }
}