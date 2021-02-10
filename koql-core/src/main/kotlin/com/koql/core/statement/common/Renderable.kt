package com.koql.core.statement.common

import com.koql.core.config.Configuration

interface Renderable {

    fun render(config : Configuration): String


}

interface Parameterd {
    fun parameters(): MutableMap<String , Any?>
}

interface StatementPart : Renderable , Parameterd {

}