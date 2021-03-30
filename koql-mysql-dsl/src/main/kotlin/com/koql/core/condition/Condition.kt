package com.koql.core.condition

import com.koql.core.config.Configuration
import com.koql.core.statement.common.StatementPart

abstract class Condition : StatementPart {

    fun and(condition: Condition): Condition {

        val combine = CombineCondition(this , condition , AND)
        return combine

    }

    fun or(condition: Condition): Condition {
        val combine = CombineCondition(this , condition , OR)
        return combine

    }

    fun not(condition: Condition): Condition {
        val combine = CombineCondition(this , condition , NOT)
        return combine
    }

    fun andNot(condition: Condition): Condition {
        val combine = CombineCondition(this , condition , AND_NOT)
        return combine

    }

    fun orNot(condition: Condition): Condition {
        val combine = CombineCondition(this , condition , OR_NOT)
        return combine

    }

}

open class CombineCondition(
    val prefix: Condition,
    val suffix: Condition,
    val op: ConditionConnector

) : Condition() {


    override fun render(config: Configuration): String {
        val sql = " ( ${prefix.render(config)} ) ${op.value} ( ${suffix.render(config)} ) "
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = prefix.parameters().plus(suffix.parameters()) as MutableMap<String, Any?>
        return params
    }

}