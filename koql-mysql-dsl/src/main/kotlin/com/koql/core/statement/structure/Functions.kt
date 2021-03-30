package com.koql.core.statement.structure


import com.koql.core.dsl.mysql.config.Configuration
import com.koql.core.dsl.mysql.config.RenderConfig
import com.koql.core.statement.common.Asterisk
import com.koql.core.statement.common.StatementPart

abstract class Functions : StatementPart {
}

abstract class AggregateFunctions : Functions() {
}

open class Count(
    val field: Field<*> = Asterisk.Asterisk
) : AggregateFunctions() {

    companion object {
        @JvmStatic
        protected val COUNT = "COUNT"

    }

    override fun render(config: Configuration , renderConfig: RenderConfig): String {
        return "$COUNT(${field.render(config , renderConfig)})"
    }

    override fun parameters(): MutableMap<String, Any?> {
        return mutableMapOf<String, Any?>()
            .apply {
                putAll(field.parameters())
            }

    }
}