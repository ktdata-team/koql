package com.koql.core.statement.common


import com.koql.core.dsl.mysql.config.Configuration
import com.koql.core.dsl.mysql.config.RenderConfig

interface Renderable {

    fun render(config : Configuration, renderConfiguration:RenderConfig): String


}

interface Parameterd {
    fun parameters(): MutableMap<String , Any?>
}

interface StatementPart : Renderable, Parameterd {

}