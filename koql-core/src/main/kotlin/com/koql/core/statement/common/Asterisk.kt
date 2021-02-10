package com.koql.core.statement.common

import com.koql.core.config.Configuration
import com.koql.core.statement.structure.Field


open class Asterisk : Field<String>() {

    companion object {
        @JvmStatic
        val Asterisk = Asterisk()
        @JvmStatic
        protected val ASTERISK = "*"
    }
    override fun render(config : Configuration): String {
        return ASTERISK
    }


    override fun parameters(): MutableMap<String, Any?> {
        return mutableMapOf()
    }
}