package com.koql.dsl.schema

import com.koql.dsl.config.Configuration
import com.koql.dsl.config.ContextConfiguration

interface Renderable {
    fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>>

    fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>>
}

interface HaveParent {
    var parentFieldName: String

}

//interface Copiable<C> : Cloneable {
//    fun copy(): C {
//        val o = super.clone()
//
//        return o as C
//    }
//}


//
//abstract class Field<T>(val name: String) : Renderable, HaveParent, Copiable<Field<T>> {
//
////    abstract val parentFieldName: String
//
//
//    fun `as`(alias: String): AliasField<T> {
//        return AliasField(this , alias)
//    }
//
//    fun eq(value: T): Condition {
//        return CompareCondition(this, value, EQUALS)
//    }
//
//
//    fun ne(value: T): Condition {
//        return CompareCondition(this, value, NOT_EQUALS)
//    }
//
//    fun ls(value: T): Condition {
//        return CompareCondition(this, value, LESS)
//    }
//
//    fun le(value: T): Condition {
//        return CompareCondition(this, value, LESS_OR_EQUAL)
//    }
//
//    fun gt(value: T): Condition {
//        return CompareCondition(this, value, GREATER)
//    }
//
//    fun ge(value: T): Condition {
//        return CompareCondition(this, value, GREATER_OR_EQUAL)
//    }
//
//    fun `in`(value: T): Condition {
//        return CompareCondition(this, value, IN)
//    }
//
//    fun notIn(value: T): Condition {
//        return CompareCondition(this, value, NOT_IN)
//    }
//
//    fun like(value: T): Condition {
//        return CompareCondition(this, value, LIKE)
//    }
//
//    fun notLike(value: T): Condition {
//        return CompareCondition(this, value, NOT_LIKE)
//    }
//
//
//    fun eq(value: Field<T>): Condition {
//        return CompareCondition(this, value, EQUALS)
//    }
//
//
//    fun ne(value: Field<T>): Condition {
//        return CompareCondition(this, value, NOT_EQUALS)
//    }
//
//    fun ls(value: Field<T>): Condition {
//        return CompareCondition(this, value, LESS)
//    }
//
//    fun le(value: Field<T>): Condition {
//        return CompareCondition(this, value, LESS_OR_EQUAL)
//    }
//
//    fun gt(value: Field<T>): Condition {
//        return CompareCondition(this, value, GREATER)
//    }
//
//    fun ge(value: Field<T>): Condition {
//        return CompareCondition(this, value, GREATER_OR_EQUAL)
//    }
//
//    fun `in`(value: Field<T>): Condition {
//        return CompareCondition(this, value, IN)
//    }
//
//    fun notIn(value: Field<T>): Condition {
//        return CompareCondition(this, value, NOT_IN)
//    }
//
//    fun like(value: Field<T>): Condition {
//        return CompareCondition(this, value, LIKE)
//    }
//
//    fun notLike(value: Field<T>): Condition {
//        return CompareCondition(this, value, NOT_LIKE)
//    }
//
//
//    fun between(value: T): BetweenCondition<T> {
//        return BetweenCondition(this, value)
//    }
//
//    fun between(value: Field<T>): BetweenCondition<T> {
//        return BetweenCondition(this, value)
//    }
//
//}


//abstract class Table0(val name: String, val schemaName: String) : Renderable, HaveParent, Copiable<Table> {
//
//
//    override var parentFieldName = schemaName
//
//    open fun `as`(alias: String) : AliasTable {
//        return AliasTable(this , alias)
//    }
//
//    open fun field(pos: Int): Field<Any> {
//        return (column_map[pos] ?: error("")) as Field<Any>
//    }
//
//    abstract val column_map: Map<Int, Field<*>>
//
//
//    override fun render(
//        configuration: Configuration,
//        contextConfiguration: ContextConfiguration
//    ): Pair<String, MutableList<Any?>> {
//
//        val prefix = if (configuration.renderParent || contextConfiguration.renderParent) {
//            configuration.fieldSeparationCharacter + parentFieldName + configuration.fieldSeparationCharacter + "."
//        } else {
//            ""
//        }
//        return (prefix + configuration.fieldSeparationCharacter + name + configuration.fieldSeparationCharacter) to mutableListOf()
//    }
//
//    override fun copy(): Table {
//        return this
//    }
//
//    override fun equals(other: Any?): Boolean {
//       return when(other){
//            !is Table-> false
//            else -> {
//                this.name == other.name && this.schemaName == other .schemaName
//            }
//        }
//    }
//
//    override fun hashCode(): Int {
//        var result = name.hashCode()
//        result = 31 * result + schemaName.hashCode()
//        result = 31 * result + parentFieldName.hashCode()
//        result = 31 * result + column_map.hashCode()
//        return result
//    }
//}
//
//
//open class AliasTable(val table: Table, val alias: Alias0) : Table(table.name , table.schemaName) {
//
//    constructor(table: Table, alias: String) : this(table, Alias0(alias))
//
//    override val column_map: Map<Int, Field<*>> = table.column_map
//
//    override fun render(
//        configuration: Configuration,
//        contextConfiguration: ContextConfiguration
//    ): Pair<String, MutableList<Any?>> {
//        val rendered = table.render(configuration, contextConfiguration)
//
//        return (rendered.first + " " + AS.value + " " + alias.render(
//            configuration,
//            contextConfiguration
//        ).first) to rendered.second
//    }
//
//    override var parentFieldName: String = ""
//
//
//    override fun copy(): AliasTable {
//        return AliasTable(table, alias)
//    }
//
//}
//
//open class Alias0(val alias: String) : Renderable {
//    override fun render(
//        configuration: Configuration,
//        contextConfiguration: ContextConfiguration
//    ): Pair<String, MutableList<Any?>> {
//        return (configuration.fieldSeparationCharacter + alias + configuration.fieldSeparationCharacter) to mutableListOf()
//    }
//
//}
//
//open class AliasField<T>(val field: Field<T>, val alias: Alias0) : Field<T>(field.name) {
//
//    constructor(field: Field<T>, alias: String) : this(field, Alias0(alias))
//
//
//    override fun render(
//        configuration: Configuration,
//        contextConfiguration: ContextConfiguration
//    ): Pair<String, MutableList<Any?>> {
//        val rendered = field.render(configuration, contextConfiguration)
//
//        return (rendered.first + " " + AS.value + " " + alias.render(
//            configuration,
//            contextConfiguration
//        ).first) to rendered.second
//    }
//
//    override var parentFieldName: String = ""
//
//    override fun copy(): Field<T> {
//        return AliasField(field, alias)
//    }
//
//}
//
//open class Column<T>(name: String, val table: Table) : Field<T>(name) {
//
//    override var parentFieldName: String = table.name
//
//
//    override fun render(
//        configuration: Configuration,
//        contextConfiguration: ContextConfiguration
//    ): Pair<String, MutableList<Any?>> {
//        val prefix = if (configuration.renderParent || contextConfiguration.renderParent) {
//            table.render(configuration,contextConfiguration).first + "."
//        } else {
//            ""
//        }
//        return (prefix + configuration.fieldSeparationCharacter + name + configuration.fieldSeparationCharacter) to mutableListOf()
//    }
//
//    override fun copy(): Field<T> {
//        return Column<T>(name, table)
//    }
//
//    override fun equals(other: Any?): Boolean {
//        return when(other){
//            !is Column<*> -> false
//            else -> {
//                this.name == other.name && this.table == other .table
//            }
//        }
//    }
//
//    override fun hashCode(): Int {
//        var result = table.hashCode()
//        result = 31 * result + parentFieldName.hashCode()
//        return result
//    }
//
//}
//
//class SubTableColumn<T>(name: String, val subSelect: SubSelect) : Field<T>(name) {
//
//    override var parentFieldName: String = subSelect.name
//
//
//    override fun render(
//        configuration: Configuration,
//        contextConfiguration: ContextConfiguration
//    ): Pair<String, MutableList<Any?>> {
//        val prefix =
//            configuration.fieldSeparationCharacter + parentFieldName + configuration.fieldSeparationCharacter + "."
////        val prefix = if (contextConfiguration.renderParent) {
////            configuration.fieldSeparationCharacter + parentFieldName + configuration.fieldSeparationCharacter + "."
////        } else {
////            ""
////        }
//        return (prefix + configuration.fieldSeparationCharacter + name + configuration.fieldSeparationCharacter) to mutableListOf()
//    }
//
//    override fun copy(): Field<T> {
//        return SubTableColumn<T>(name, subSelect)
//    }
//
//    override fun equals(other: Any?): Boolean {
//        return when(other){
//            !is SubTableColumn<*> -> false
//            else -> {
//                this.name == other.name && this.subSelect == other .subSelect
//            }
//        }
//    }
//
//    override fun hashCode(): Int {
//        var result = subSelect.hashCode()
//        result = 31 * result + parentFieldName.hashCode()
//        return result
//    }
//
//}
//
//class ValueField<T>(val value: T) : Field<T>(value.toString()) {
//    override var parentFieldName: String = ""
//
//    override fun render(
//        configuration: Configuration,
//        contextConfiguration: ContextConfiguration
//    ): Pair<String, MutableList<Any?>> {
//        return (configuration.valueSeparationCharacter + value.toString() + configuration.valueSeparationCharacter) to mutableListOf()
//    }
//
//    override fun copy(): Field<T> {
//        return ValueField<T>(value)
//    }
//
//    override fun equals(other: Any?): Boolean {
//        return when(other){
//            !is ValueField<*> -> false
//            else -> {
//                this.value == other.value
//            }
//        }
//    }
//
//    override fun hashCode(): Int {
//        var result = value?.hashCode() ?: 0
//        result = 31 * result + parentFieldName.hashCode()
//        return result
//    }
//}