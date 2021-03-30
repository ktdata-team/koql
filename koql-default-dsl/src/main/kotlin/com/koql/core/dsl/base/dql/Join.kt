package com.koql.core.dsl.base.dql

import com.koql.core.config.Configuration
import com.koql.core.statement.common.Renderable
import com.koql.core.statement.common.StatementPart
import com.koql.core.condition.Condition
import com.koql.core.dsl.base.structure.Query

const val ON = "ON"

sealed class JoinMode(val name: String)
object JOIN : JoinMode("JOIN")
object INNER_JOIN : JoinMode("INNER JOIN")
object LEFT_JOIN : JoinMode("LEFT JOIN")
object RIGHT_JOIN : JoinMode("RIGHT JOIN")
object FULL_JOIN : JoinMode("FULL JOIN")


open class Join(
    protected val prefixPart: StatementPart,
    protected val suffixPart: Renderable,
    protected val mod: JoinMode = JOIN,
    val config: Configuration
) {


    fun on(on: Condition): JoinOn {
        return JoinOn(prefixPart, suffixPart, on, mod, config)
    }


}

open class JoinOn(
    protected val prefixPart: StatementPart,
    protected val join: Renderable,
    protected val on: Condition,
    protected val mod: JoinMode = JOIN,
    config: Configuration
) : Query(config) {


    override fun render(config: Configuration): String {
        val sql = "${prefixPart.render(config)} ${mod.name} ${join.render(config)} $ON ( ${on.render(config)} )"
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = (prefixPart.parameters() +
                if (join is StatementPart) {
                    join.parameters()
                } else {
                    mutableMapOf()
                } +
                on.parameters())
                as MutableMap<String, Any?>
        return params
    }
}