package com.koql.core.statement.structure


import com.koql.core.dsl.mysql.config.Configuration
import com.koql.core.dsl.mysql.config.RenderConfig
import java.util.*

open class ValueField<T>(val name: String, val value: T) : Field<T>() {

    constructor(value: T) : this(

        randomKey(),
        value
    )

    companion object {
        fun randomKey(): String {
            return UUID.randomUUID().toString()
        }
    }


    override fun render(config: Configuration , renderConfig: RenderConfig): String {
        val sql = "#{$name}"
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        return mutableMapOf(name to value)
    }


}