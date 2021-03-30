package com.koql.core.statement.structure


import com.koql.core.dsl.mysql.config.Configuration
import com.koql.core.dsl.mysql.config.RenderConfig
import com.koql.core.statement.common.Renderable

open class Schema(val name : String) : Renderable {




    override fun render(config: Configuration , renderConfig: RenderConfig): String {
        return "${config.fieldSeparationCharacter}$name${config.fieldSeparationCharacter}"
    }


}