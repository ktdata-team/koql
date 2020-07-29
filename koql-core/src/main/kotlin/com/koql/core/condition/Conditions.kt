package com.koql.core.condition

import com.koql.core.Sql
import com.koql.core.config.Configuration
import com.koql.core.statement.const.ConditionConnector
import com.koql.core.statement.structure.Expression
import com.koql.core.statement.const.Comparator
import com.koql.core.statement.structure.SelectExpr
import com.koql.core.statement.structure.ValueField

abstract class ConditionPart() : Expression() {

    val connectorToConditionList = mutableListOf<Pair<ConditionConnector, Condition>>()
    val preparedValue = mutableListOf<Any?>()
}

abstract class Condition() : ConditionPart() {

    init {
        connectorToConditionList.add(ConditionConnector.emptyConnector to this)
    }

    fun and(condition: Condition): Condition {

        connectorToConditionList.add(ConditionConnector.AND to condition)
        return this
    }

    fun or(condition: Condition): Condition {

        connectorToConditionList.add(ConditionConnector.OR to condition)
        return this
    }

    fun not(condition: Condition): Condition {

        connectorToConditionList.add(ConditionConnector.NOT to condition)
        return this
    }

    fun andNot(condition: Condition): Condition {

        connectorToConditionList.add(ConditionConnector.AND_NOT to condition)
        return this
    }

    fun orNot(condition: Condition): Condition {

        connectorToConditionList.add(ConditionConnector.OR_NOT to condition)
        return this
    }



}

class CompareCondition<T>(
    val field: SelectExpr<T>,
    val valueField: SelectExpr<T>,
    val op: Comparator

) : Condition() {

    constructor(field: SelectExpr<T>, value: T, op: Comparator) : this(field, ValueField(value) , op)

    constructor(field: T, value: T, op: Comparator) : this(ValueField(field), ValueField(value), op)

    override fun render(configuration: Configuration): Sql {

        return when (configuration.preparedSql) {
            true -> {
                val values = mutableListOf<Any?>()
                val str =
                    if (field is ValueField) {
                        values.add(field.value)
                        "?"
                    } else {
                        field.render(configuration).sqlStr
                    } + " " + op.value + " " + if (valueField is ValueField) {
                        values.add(valueField.value)
                        "?"
                    } else {
                        valueField.render(configuration).sqlStr
                    } + " "

                Sql().apply {
                    sqlStr = str
                    params.addAll(values)
                    prepared = true
                }
            }
            else -> {
                val str =
                    field.render(configuration).sqlStr + " " + op.value + " " + valueField.render(configuration).sqlStr

                Sql().apply {
                    sqlStr = str
                }

            }
        }
    }




}


//class BetweenCondition<T>(val field: Field<T>, val minValue: Field<T>) : Condition() {
//
//
//    constructor(field: Field<T>, minValue: T) : this(field, ValueField(minValue))
//
//    constructor(field: T, minValue: T) : this(ValueField(field), ValueField(minValue))
//
//    var maxValue: Field<T>? = null
//        private set
//
//    fun and(maxValue: T): BetweenCondition<T> {
//        this.maxValue = ValueField(maxValue)
//        return this
//    }
//
//    fun and(maxValue: Field<T>): BetweenCondition<T> {
//        this.maxValue = maxValue
//        return this
//    }
//
//    override fun renderSelf(configuration: Configuration, contextConfiguration: ContextConfiguration): Pair<String, MutableList<Any?>> {
//
//        if (maxValue == null)
//            throw Exception("between need and ...")
//
//        return when (configuration.preparedSql) {
//            true -> {
//                val values = mutableListOf<Any?>()
//                val str ="( " +
//                    if (field is ValueField) {
//                        values.add(field.value)
//                        "?"
//                    } else {
//                        field.render(configuration, contextConfiguration).first
//                    } +
//                            " " +
//                            BETWEEN.value +
//                            " " +
//                            if (minValue is ValueField) {
//                                values.add(minValue.value)
//                                "?"
//                            } else {
//                                minValue.render(configuration, contextConfiguration).first
//                            } +
//                            " " +
//                            BETWEEN_AND.value +
//                            " " +
//                            if (maxValue is ValueField) {
//                                values.add((maxValue as ValueField<T>).value)
//                                "?"
//                            } else {
//                                maxValue!!.render(configuration, contextConfiguration).first
//                            } +
//                        " )"
//
//                str to values
//            }
//            else -> {
//                val str = field.render(configuration , contextConfiguration).first + " " +
//                        BETWEEN.value + " " +
//                     minValue.render(configuration, contextConfiguration).first + " " +
//                        maxValue!!.render(configuration, contextConfiguration).first
//                val values = mutableListOf<Any?>()
//                str to values
//            }
//        }
//    }
//
//    override fun renderAsPart(
//        configuration: Configuration,
//        contextConfiguration: ContextConfiguration
//    ): Pair<String, MutableList<Any?>> {
//        return this.render(configuration, contextConfiguration)
//    }
//
//
//}
