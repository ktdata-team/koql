package com.koql.core.statement.structure

import com.koql.core.config.Configuration
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


    override fun render(config: Configuration): String {
        val sql = "#{$name}"
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        return mutableMapOf(name to value)
    }


}