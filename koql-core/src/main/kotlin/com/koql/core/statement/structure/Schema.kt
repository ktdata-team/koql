package com.koql.core.statement.structure

import com.koql.core.config.Configuration
import com.koql.core.statement.common.Renderable

open class Schema(val name : String) : Renderable {




    override fun render(config: Configuration): String {
        return "${config.fieldSeparationCharacter}$name${config.fieldSeparationCharacter}"
    }


}