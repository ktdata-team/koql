package com.koql.dsl.statement

import com.koql.dsl.config.Configuration
import com.koql.dsl.config.ContextConfiguration
import com.koql.dsl.condition.Condition
import com.koql.dsl.const.DISTINCT
import com.koql.dsl.const.FROM
import com.koql.dsl.const.SELECT
import com.koql.dsl.const.WHERE
import com.koql.dsl.schema.Column
import com.koql.dsl.schema.SelectExpr
import com.koql.dsl.schema.SubSelect
import com.koql.dsl.schema.Table


open class Select(
    var configuration: Configuration,
    var contextConfiguration: ContextConfiguration
) :
    SelectFinalI,
    SelectWherePhaseI,
    SelectPhaseI,
    Statement<Select> {
    private val selectFields = mutableListOf<SelectExpr<*>>()
    fun selectFields(): MutableList<SelectExpr<*>> = selectFields
    private val fromTables = mutableListOf<Table<*>>()
    fun fromTables(): MutableList<Table<*>> = fromTables
    private var whereCondition: Condition? = null
    fun whereCondition() = whereCondition
    private val preparedValues = mutableListOf<Any?>()
    fun preparedValues(): MutableList<Any?> = preparedValues

    fun addPreparedValues(vararg value: Any?) {
        preparedValues.addAll(value)
    }


    fun addSelectField(vararg fields: SelectExpr<*>) {
        selectFields.addAll(fields)
    }

    fun addFromTable(vararg tables: Table<*>) {
        fromTables.addAll(tables)
    }


    override fun from(vararg tables: Table<*>): SelectWherePhaseI {
        addFromTable(*tables)
        return this
    }

    override fun `as`(name: String): SubSelect {
        return SubSelect(name, this)
    }


    override fun where(conditions: Condition): SelectFinalI {
        whereCondition = conditions
        return this
    }


    var disdinctFlag = false


    override fun render(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {

        val contextConfig = contextConfiguration
            .let { cc ->
                val config = if (fromTables().size > 1) {
                    cc.copy(renderParent = true)
                } else {
                    cc.copy()
                }
                config
            }

        val selectPart = SELECT.value
        val distinctPart = if (disdinctFlag) {
            DISTINCT.value
        } else {
            ""
        }

        val selectField = selectFields().map {
            when {
                (it is Column<*>) -> {
                    val t = fromTables().firstOrNull() { at ->
                        it.table == at
                    }
                    t?.run {
                        it.parent = this
                    }
                    it

                }

                (it is SubSelect) -> {
                    it
                }

                else -> {
                    it
                }
            }
        }

        val fromPart = FROM.value

        val fromTables = this.fromTables()

        val wherePart = WHERE.value

        val whereConditions = whereCondition


        val rendered = selectPart + " " +
                distinctPart + " " +
                selectField.map {
                    val rendered = it.render(configuration, contextConfig)
                    preparedValues.addAll(rendered.second)
                    rendered.first
                }.joinToString(",") + " " +
                if (fromTables().isNotEmpty()) {
                    fromPart + " " + "(" +
                            " ${fromTables
                                .map {
                                    val rendered = it.render(
                                        configuration,
                                        contextConfig
                                    )
                                    preparedValues.addAll(rendered.second)
                                    rendered.first
                                }
                                .joinToString()} )"
                } else {
                    ""
                } + " " +
                " ${
                if (whereConditions != null) {
                    val renderCondition = whereConditions.render(
                        configuration,
                        contextConfig
                    )
                    preparedValues.addAll(renderCondition.second)
                    wherePart + " " + "( ${renderCondition.first} )"
                } else {
                    ""
                }
                } "

/*        val str = "${SELECT.value} ${if (disdinctFlag) {
            DISTINCT.value
        } else {
            ""
        }} ${this.selectFields()
            .let {
                if (it.isEmpty()) {
                    fromTables()
                        .forEach { table ->
                            it.addAll(table.column_map.values
                                .map { f ->
                                    if (table is AliasTable) {
                                        if (f is Column) {
                                            Column<Any?>()
                                        }
                                    }
                                    TODO()

                                    (f.copy() as Column)
                                })
                        }
                }
                it
            }
            .map {


                val rendered = it.render(
                    configuration,
                    contextConfig
                )
                preparedValues.addAll(rendered.second)
                rendered.first
            }
            .joinToString()} ${
        if (fromTables().isNotEmpty()) {
            FROM.value + " " + "( ${this.fromTables()
                .map {
                    val rendered = it.render(
                        configuration,
                        contextConfig
                    )
                    preparedValues.addAll(rendered.second)
                    rendered.first
                }
                .joinToString()} )"
        } else {
            ""
        }
        } ${
        if (whereCondition != null) {
            val renderCondition = whereCondition!!.render(
                configuration,
                contextConfig
            )
            preparedValues.addAll(renderCondition.second)
            WHERE.value + " " + "( ${renderCondition.first} )"
        } else {
            ""
        }
        }"*/

        return rendered to preparedValues


        /* return when (configuration.preparedSql) {
             true -> {
                 val str = "${SELECT.value} ${if (disdinctFlag) {
                     DISTINCT.value
                 } else {
                     ""
                 }} ${this.selectFields()
                     .let {
                         if (it.isEmpty()) {
                             fromTables()
                                 .forEach { table ->
                                     it.addAll(table.column_map.values)
                                 }
                         }
                         it
                     }
                     .map {
                         val contextConfig = contextConfiguration
                             .let { cc ->
                                 val config = if (fromTables().size > 1) {
                                     cc.copy(renderParent = true)
                                 } else {
                                     cc.copy()
                                 }
                                 config
                             }


                         val rendered = it.render(
                             configuration,
                             contextConfig
                         )
                         preparedValues.addAll(rendered.second)
                         rendered.first
                     }
                     .joinToString()} ${
                 if (fromTables().isNotEmpty()) {
                     FROM.value + " " + "( ${this.fromTables()
                         .map {
                             val rendered = it.render(
                                 configuration,
                                 contextConfiguration
                             )
                             preparedValues.addAll(rendered.second)
                             rendered.first
                         }
                         .joinToString()} )"
                 } else {
                     ""
                 }
                 } ${
                 if (whereCondition != null) {
                     val renderCondition = whereCondition!!.render(
                         configuration,
                         contextConfiguration
                     )
                     preparedValues.addAll(renderCondition.second)
                     WHERE.value + " " + "( ${renderCondition.first} )"
                 } else {
                     ""
                 }
                 }"

                 str to preparedValues
             }
             else -> {
                 val str = "${SELECT.value} ${if (disdinctFlag) {
                     DISTINCT.value
                 } else {
                     ""
                 }} ${this.selectFields()
                     .let {
                         if (it.isEmpty()) {
                             fromTables()
                                 .forEach { table ->
                                     it.addAll(table.column_map.values)
                                 }
                         }
                         it
                     }
                     .map {
                         val contextConfig = contextConfiguration
                             .let { cc ->
                                 val config = if (fromTables().size > 1) {
                                     cc.copy(renderParent = true)
                                 } else {
                                     cc.copy()
                                 }
                                 config
                             }


                         it.render(
                             configuration,
                             contextConfig
                         ).first
                     }
                     .joinToString()} ${
                 if (fromTables().isNotEmpty()) {
                     FROM.value + " " + "( ${this.fromTables().map {
                         it.render(
                             configuration,
                             contextConfiguration
                         ).first
                     }.joinToString()} )"
                 } else {
                     ""
                 }
                 } ${
                 if (whereCondition != null) {
                     val renderCondition = whereCondition!!.render(
                         configuration,
                         contextConfiguration
                     )
                     WHERE.value + " " + "( ${renderCondition.first} ) "
                 } else {
                     ""
                 }
                 }"

                 str to preparedValues
             }
         }*/


    }

    override fun getSql(): String {
        val preparedStm = render(
            configuration,
            contextConfiguration
        )
        val outStr = preparedStm.first + "  ,   " + preparedStm.second
        return outStr
    }

    override fun renderAsPart(
        configuration: Configuration,
        contextConfiguration: ContextConfiguration
    ): Pair<String, MutableList<Any?>> {
        val rendered =
            render(configuration, contextConfiguration)

        return rendered
    }

}
