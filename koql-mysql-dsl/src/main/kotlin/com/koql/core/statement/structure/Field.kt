package com.koql.core.statement.structure

import com.koql.core.statement.common.StatementPart
import com.koql.core.condition.CompareCondition
import com.koql.core.condition.*

abstract class Field<T> : StatementPart {

    fun eq(value: T): Condition {
        return CompareCondition(this, value, EQUALS)
    }


    fun ne(value: T): Condition {
        return CompareCondition(this, value, NOT_EQUALS)
    }

    fun ls(value: T): Condition {
        return CompareCondition(this, value, LESS)
    }

    fun le(value: T): Condition {
        return CompareCondition(this, value, LESS_OR_EQUAL)
    }

    fun gt(value: T): Condition {
        return CompareCondition(this, value, GREATER)
    }

    fun ge(value: T): Condition {
        return CompareCondition(this, value, GREATER_OR_EQUAL)
    }

    fun `in`(vararg value: T): Condition {
        return InCondition(this, value, IN)
    }

    fun notIn(vararg value: T): Condition {
        return InCondition(this, value, NOT_IN)
    }

    fun like(value: String): Condition {
        return LikeCondition(this, value, LIKE)
    }

    fun notLike(value: String): Condition {
        return LikeCondition(this, value, NOT_LIKE)
    }


    fun eq(value: Field<T>): Condition {
        return CompareCondition(this, value, EQUALS)
    }


    fun ne(value: Field<T>): Condition {
        return CompareCondition(this, value, NOT_EQUALS)
    }

    fun ls(value: Field<T>): Condition {
        return CompareCondition(this, value, LESS)
    }

    fun le(value: Field<T>): Condition {
        return CompareCondition(this, value, LESS_OR_EQUAL)
    }

    fun gt(value: Field<T>): Condition {
        return CompareCondition(this, value, GREATER)
    }

    fun ge(value: Field<T>): Condition {
        return CompareCondition(this, value, GREATER_OR_EQUAL)
    }

    fun `in`(vararg value: Field<T>): Condition {
        return InCondition(this, value.toList(), IN)
    }

    fun notIn(vararg value: Field<T>): Condition {
        return InCondition(this, value.toList(), NOT_IN)
    }

    fun like(value: ValueField<String>): Condition {
        return LikeCondition(this, value, LIKE)
    }

    fun notLike(value: ValueField<String>): Condition {
        return LikeCondition(this, value, NOT_LIKE)
    }


    fun between(value: T): BetweenConditionPart<T> {
        return BetweenConditionPart(this, value)
    }

    fun between(value: Field<T>): BetweenConditionPart<T> {
        return BetweenConditionPart(this, value)
    }

}