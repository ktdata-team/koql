package com.koql.dsl.condition

import com.koql.dsl.config.Configuration
import com.koql.dsl.config.ContextConfiguration
import com.koql.dsl.const.*
import com.koql.dsl.schema.Field
import com.koql.dsl.schema.ValueField

import com.koql.dsl.statement.Statement



abstract class ConditionPart() : Statement {

    val connectorToConditionList = mutableListOf<Pair<ConditionConnector, Condition>>()
    val preparedValue = mutableListOf<Any?>()
}

abstract class Condition() : ConditionPart() {

    init {
        connectorToConditionList.add(emptyConnector to this)
    }

    fun and(condition: Condition): Condition {

        connectorToConditionList.add(AND to condition)
        return this
    }

    fun or(condition: Condition): Condition {

        connectorToConditionList.add(OR to condition)
        return this
    }

    fun not(condition: Condition): Condition {

        connectorToConditionList.add(NOT to condition)
        return this
    }

    fun andNot(condition: Condition): Condition {

        connectorToConditionList.add(AND_NOT to condition)
        return this
    }

    fun orNot(condition: Condition): Condition {

        connectorToConditionList.add(OR_NOT to condition)
        return this
    }


    abstract fun renderSelf(configuration: Configuration, contextConfiguration : ContextConfiguration): Pair<String, MutableList<Any?>>

    override fun render(configuration: Configuration, contextConfiguration: ContextConfiguration): Pair<String, MutableList<Any?>>{
        return when (configuration.preparedSql) {
            true -> {
                val str = connectorToConditionList.map {

                    val preparedCondition = it.second.renderSelf(configuration, contextConfiguration)
                    preparedValue.addAll(preparedCondition.second)

                    it.first.value + " " + it.second.renderSelf(configuration, contextConfiguration).first
                }.joinToString(" ")

                str to preparedValue
            }
            else -> {
                val str = connectorToConditionList.map {
                    it.first.value + " " + it.second.renderSelf(configuration, contextConfiguration).first
                }.joinToString(" ")

                str to preparedValue
            }
        }
    }
}

class CompareCondition<T>(
    val field: Field<T>,
    val valueField: Field<T>,
    val op: Comparator

) : Condition() {

    constructor(field: Field<T>, value: T, op: Comparator) : this(field, ValueField(value) , op)

    constructor(field: T, value: T, op: Comparator) : this(ValueField(field), ValueField(value), op)

    override fun renderSelf(configuration: Configuration, contextConfiguration: ContextConfiguration): Pair<String, MutableList<Any?>> {

        return when (configuration.preparedSql) {
            true -> {
                val values = mutableListOf<Any?>()
                val str =
                    if (field is ValueField) {
                        values.add(field.value)
                        "?"
                    } else {
                        field.render(configuration , contextConfiguration).first
                    } + " " + op.value + " " + if (valueField is ValueField) {
                        values.add(valueField.value)
                        "?"
                    } else {
                        valueField.render(configuration, contextConfiguration).first
                    } + " "

                str to values
            }
            else -> {
                val str =
                    field.render(configuration, contextConfiguration).first + " " + op.value + " " + valueField.render(configuration, contextConfiguration).first
                val values = mutableListOf<Any?>()
                str to values
            }
        }
    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        return this.render(configuration, contextConfiguration)
    }


}


class BetweenCondition<T>(val field: Field<T>, val minValue: Field<T>) : Condition() {


    constructor(field: Field<T>, minValue: T) : this(field, ValueField(minValue))

    constructor(field: T, minValue: T) : this(ValueField(field), ValueField(minValue))

    var maxValue: Field<T>? = null
        private set

    fun and(maxValue: T): BetweenCondition<T> {
        this.maxValue = ValueField(maxValue)
        return this
    }

    fun and(maxValue: Field<T>): BetweenCondition<T> {
        this.maxValue = maxValue
        return this
    }

    override fun renderSelf(configuration: Configuration, contextConfiguration: ContextConfiguration): Pair<String, MutableList<Any?>> {

        if (maxValue == null)
            throw Exception("between need and ...")

        return when (configuration.preparedSql) {
            true -> {
                val values = mutableListOf<Any?>()
                val str ="( " +
                    if (field is ValueField) {
                        values.add(field.value)
                        "?"
                    } else {
                        field.render(configuration, contextConfiguration).first
                    } +
                            " " +
                            BETWEEN.value +
                            " " +
                            if (minValue is ValueField) {
                                values.add(minValue.value)
                                "?"
                            } else {
                                minValue.render(configuration, contextConfiguration).first
                            } +
                            " " +
                            BETWEEN_AND.value +
                            " " +
                            if (maxValue is ValueField) {
                                values.add((maxValue as ValueField<T>).value)
                                "?"
                            } else {
                                maxValue!!.render(configuration, contextConfiguration).first
                            } +
                        " )"

                str to values
            }
            else -> {
                val str = field.render(configuration , contextConfiguration).first + " " +
                        BETWEEN.value + " " +
                     minValue.render(configuration, contextConfiguration).first + " " +
                        maxValue!!.render(configuration, contextConfiguration).first
                val values = mutableListOf<Any?>()
                str to values
            }
        }
    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        return this.render(configuration, contextConfiguration)
    }


}
