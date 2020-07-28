package com.koql.dsl.schema

import com.koql.dsl.config.Configuration
import com.koql.dsl.config.ContextConfiguration
import com.koql.dsl.condition.BetweenCondition
import com.koql.dsl.condition.CompareCondition
import com.koql.dsl.condition.Condition
import com.koql.dsl.const.*
import com.koql.dsl.statement.Select

interface Aliasable{

    var alias: String?

    fun `as`(alias: String): Aliasable

}

interface Childrenable : Renderable {
    var parent: Childrenable?
}

interface SelectExpr : Childrenable, Aliasable {
    override fun `as`(alias: String): SelectExpr
}

interface TableReference : Childrenable, Aliasable {
    val column_map: Map<Int, SelectExpr>
    fun field(pos: Int): SelectExpr {
        return (column_map[pos] ?: error(""))
    }
//    override fun <C : TableReference> `as`(alias: String): C

    override fun equals(other: Any?): Boolean
}

//----------------------

//open class  AliasTable< A : Table>(val table: A, val alias: String) : Table(table.name, table.parent) {
//
//    override fun render(
//        configuration: Configuration,
//        contextConfiguration: ContextConfiguration
//    ): Pair<String, MutableList<Any?>> {
//
//        val rendered =
//            table.render(
//                configuration,
//                contextConfiguration
//            ).first + " " + AS.value + " " + configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter to mutableListOf<Any?>()
//
//
//        return rendered
//    }
//
//    override fun renderAsPart(
//        configuration: Configuration,
//        contextConfiguration: ContextConfiguration
//    ): Pair<String, MutableList<Any?>> {
//        val rendered =
//            configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter to mutableListOf<Any?>()
//
//        return rendered
//    }
//
//
//    override fun `as`(alias: String): A {
//        TODO("Not yet implemented")
//    }
//
//}

abstract class Table(
    val name: String = "",
    override var alias: String? = null,
    override var parent: Childrenable? = null
) : TableReference {

    abstract override fun `as`(alias: String): Table

    override fun equals(other: Any?): Boolean {

        return when (other) {
            !is Table -> false
            other.name != this.name -> false
            other.parent != this.parent -> false
            else -> true
        }

    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        return result
    }

    override fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val prefix = if (parent != null && (configuration.renderParent || contextConfiguration.renderParent)) {
            (parent as Childrenable).renderAsPart(configuration, contextConfiguration).first + "."
        } else {
            ""
        }

        val subfix = if (alias != null) {
            " " + AS.value + " " + configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter
        } else {
            ""
        }

        return (prefix + configuration.fieldSeparationCharacter + name + configuration.fieldSeparationCharacter + subfix) to mutableListOf()
    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val rendered =if (alias != null) {
            configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter to mutableListOf()
        }else {
            render(configuration, contextConfiguration)
        }
        return rendered
    }
}

open class Schema(val name: String = "") : Childrenable {
    override var parent: Childrenable? = null

    override fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        return configuration.fieldSeparationCharacter + name + configuration.fieldSeparationCharacter to mutableListOf()
    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        return render(configuration, contextConfiguration)
    }

}

abstract class Field<T>() : SelectExpr {

//    abstract val parentFieldName: String

    open var name: String = ""

    fun eq(value: T): Condition {
        return CompareCondition(this, value, EQUALS)
    }


    fun ne(value: T): Condition {
        return CompareCondition(this, value, NOT_EQUALS)
    }

    fun ls(value: T): Condition {
        return CompareCondition(this, value, LESS)
    }

    fun le(value: T): Condition {
        return CompareCondition(this, value, LESS_OR_EQUAL)
    }

    fun gt(value: T): Condition {
        return CompareCondition(this, value, GREATER)
    }

    fun ge(value: T): Condition {
        return CompareCondition(this, value, GREATER_OR_EQUAL)
    }

    fun `in`(value: T): Condition {
        return CompareCondition(this, value, IN)
    }

    fun notIn(value: T): Condition {
        return CompareCondition(this, value, NOT_IN)
    }

    fun like(value: T): Condition {
        return CompareCondition(this, value, LIKE)
    }

    fun notLike(value: T): Condition {
        return CompareCondition(this, value, NOT_LIKE)
    }


    fun eq(value: Field<T>): Condition {
        return CompareCondition(this, value, EQUALS)
    }


    fun ne(value: Field<T>): Condition {
        return CompareCondition(this, value, NOT_EQUALS)
    }

    fun ls(value: Field<T>): Condition {
        return CompareCondition(this, value, LESS)
    }

    fun le(value: Field<T>): Condition {
        return CompareCondition(this, value, LESS_OR_EQUAL)
    }

    fun gt(value: Field<T>): Condition {
        return CompareCondition(this, value, GREATER)
    }

    fun ge(value: Field<T>): Condition {
        return CompareCondition(this, value, GREATER_OR_EQUAL)
    }

    fun `in`(value: Field<T>): Condition {
        return CompareCondition(this, value, IN)
    }

    fun notIn(value: Field<T>): Condition {
        return CompareCondition(this, value, NOT_IN)
    }

    fun like(value: Field<T>): Condition {
        return CompareCondition(this, value, LIKE)
    }

    fun notLike(value: Field<T>): Condition {
        return CompareCondition(this, value, NOT_LIKE)
    }


    fun between(value: T): BetweenCondition<T> {
        return BetweenCondition(this, value)
    }

    fun between(value: Field<T>): BetweenCondition<T> {
        return BetweenCondition(this, value)
    }

}


class AsteriskField : SelectExpr {
    val asterisk = "*"

    override fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val prefix = if (parent != null) {
            (parent as Childrenable).renderAsPart(configuration, contextConfiguration).first + "."
        } else {
            ""
        }
        return (prefix + configuration.fieldSeparationCharacter + asterisk + configuration.fieldSeparationCharacter) to mutableListOf()
    }


    override var parent: Childrenable? = null

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        return render(configuration, contextConfiguration)
    }

    override var alias: String? = null


    override fun `as`(alias: String): SelectExpr {
        TODO("Not yet implemented")
    }


}

open class AliasColumn<T>(val column: Column<T>, override var alias: String?) :
    Column<T>(column.name, column.table, alias ,column.parent) {
    override fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {

        val rendered =
            column.render(
                configuration,
                contextConfiguration
            )
        return rendered.first + " " + AS.value + " " + configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter to rendered.second

    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val rendered =
            configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter to column.render(
                configuration,
                contextConfiguration
            ).second

        return rendered
    }


    override fun `as`(alias: String): AliasColumn<T> {
        TODO("Not yet implemented")
    }

}

open class Column<T>(
    override var name: String, val table: TableReference,
    override var alias: String? = null,
    override var parent: Childrenable? = table
) : Field<T>() {


    override fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val prefix = if (configuration.renderParent || contextConfiguration.renderParent) {
            table.renderAsPart(configuration, contextConfiguration).first + "."
        } else {
            ""
        }
        val subfix = if (alias != null) {
            " " + AS.value + " " + configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter
        } else {
            ""
        }
        return (prefix + configuration.fieldSeparationCharacter + name + configuration.fieldSeparationCharacter + subfix) to mutableListOf()
    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {


        val rendered = if (alias != null) {
             configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter
        } else {
            ""
        }


        return rendered to mutableListOf()
    }

    override fun `as`(alias: String): AliasColumn<T> {
        return AliasColumn<T>(this, alias)
    }


}

class AliasValueField<T>(val valueField: ValueField<T>, override var alias: String?) : ValueField<T>(valueField.value , alias) {
    override fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val rendered = valueField.render(configuration, contextConfiguration)
        return (rendered.first + " " +
                AS.value + " " + alias + " "
                ) to rendered.second
    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val rendered =
            configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter to valueField.render(configuration, contextConfiguration).second

        return rendered
    }

    override fun `as`(alias: String): AliasValueField<T> {
        TODO("Not yet implemented")
    }

}

open class ValueField<T>(
    val value: T, override var alias: String? = null

) : Field<T>() {


    override fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        return (configuration.valueSeparationCharacter + value.toString() + configuration.valueSeparationCharacter) to mutableListOf()
    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        return render(configuration, contextConfiguration)
    }

    override var parent: Childrenable? = null
    override fun `as`(alias: String): AliasValueField<T> {
        return AliasValueField(this, alias)
    }
}

class AliasCalculateField<T>(val calculateField: CalculateField<T>, override var alias: String?) :
    CalculateField<T>(calculateField.field1, calculateField.field2, calculateField.op) {
    override fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val rendered = calculateField.render(configuration, contextConfiguration)
        return (rendered.first + " " +
                AS.value + " " + alias + " "
                ) to rendered.second
    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val rendered =
            configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter to calculateField.render(configuration, contextConfiguration).second

        return rendered
    }

    override fun `as`(alias: String): AliasCalculateField<T> {
        TODO("Not yet implemented")
    }

}

open class CalculateField<T>(val field1: Field<T>, val field2: Field<T>, val op: CalculateOperator) :
    Field<T>() {
    //    override var alias: String? = null
    override var parent: Childrenable? = null
    override var alias: String? = null
    override fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val f1 = field1.renderAsPart(configuration, contextConfiguration)
        val f2 = field2.renderAsPart(configuration, contextConfiguration)



        return f1.first + " " + op.value + " " + f2.first + " "  to mutableListOf<Any?>().apply {
            addAll(f1.second)
            addAll(f2.second)
        }
    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val f1 = field1.renderAsPart(configuration, contextConfiguration)
        val f2 = field2.renderAsPart(configuration, contextConfiguration)


        return f1.first + " " + op.value + " " + f2.first to mutableListOf<Any?>().apply {
            addAll(f1.second)
            addAll(f2.second)
        }
    }

    override fun `as`(alias: String): AliasCalculateField<T> {
        return AliasCalculateField(this , alias)
    }


}


class SubSelect(override var alias: String?, val select: Select) : Field<Any>(), TableReference {

    override var parent: Childrenable? = null


    override val column_map = mutableListOf(
        *(select.selectFields()
            .let {
                val fields = mutableListOf<SelectExpr>()
                if (it.isEmpty()) {
                    select.fromTables()
                        .forEach { table ->
                            fields.addAll(table.column_map.values)
                        }
                } else {
                    fields.addAll(it)
                }
                fields
            }.toTypedArray())
    ).mapIndexed { index, column ->
        index to (column.apply {
            this.parent = this@SubSelect
        })
    }.toMap()


    override fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val selectRendered = select.render(
            configuration,
            contextConfiguration
        )
        val subfix = if (alias != null) {
            AS.value + " " + configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter
        } else {
            ""
        }
        return (""" ( ${selectRendered.first} ) ${subfix} """) to selectRendered.second
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = alias?.hashCode() ?: 0
        result = 31 * result + select.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + column_map.hashCode()
        return result
    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        return configuration.fieldSeparationCharacter + alias!! + configuration.fieldSeparationCharacter to mutableListOf<Any?>()
    }

    override fun `as`(alias: String): SubSelect {
        TODO("Not yet implemented")
    }


}