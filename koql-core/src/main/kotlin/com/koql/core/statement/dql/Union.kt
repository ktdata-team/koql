package com.koql.core.statement.dql

import com.koql.core.config.Configuration
import com.koql.core.statement.common.StatementPart
import com.koql.core.statement.structure.Query

enum class UnionMode{
    UNION , UNION_ALL
}

open class Union(
    protected val prefixPart : StatementPart,
    protected val suffixPart : StatementPart,
    protected val mod :UnionMode = UnionMode.UNION,
    config : Configuration
) : Query(config){





    override fun render(config: Configuration): String {
        val sql = "${prefixPart.render(config)} ${mod.name} ${suffixPart.render(config)}"
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = prefixPart.parameters().plus(suffixPart.parameters()) as MutableMap<String, Any?>
        return params
    }

}