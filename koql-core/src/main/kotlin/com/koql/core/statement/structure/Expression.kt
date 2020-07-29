package com.koql.core.statement.structure

import com.koql.core.Sql
import com.koql.core.condition.CompareCondition
import com.koql.core.condition.Condition
import com.koql.core.config.Configuration
import com.koql.core.statement.const.CalculateOperator
import com.koql.core.statement.const.Comparator.*

abstract class Expression {
    private val _hashCode: Int by lazy { toString().hashCode() }

//    /** Appends the SQL representation of this expression to the specified [queryBuilder]. */
//    abstract fun toQueryBuilder(queryBuilder: QueryBuilder)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Expression) return false

        if (toString() != other.toString()) return false

        return true
    }

    override fun hashCode(): Int = _hashCode

//    override fun toString(): String = QueryBuilder(false).append(this).toString()

    abstract fun render(configuration: Configuration) : Sql

}

abstract class SelectExpr<T> :  Expression() {

//    open var name: String = ""

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

    fun `in`(value: T): Condition {
        return CompareCondition(this, value, IN)
    }

    fun notIn(value: T): Condition {
        return CompareCondition(this, value, NOT_IN)
    }

    fun like(value: T): Condition {
        return CompareCondition(this, value, LIKE)
    }

    fun notLike(value: T): Condition {
        return CompareCondition(this, value, NOT_LIKE)
    }


    fun eq(value: SelectExpr<T>): Condition {
        return CompareCondition(this, value, EQUALS)
    }


    fun ne(value: SelectExpr<T>): Condition {
        return CompareCondition(this, value, NOT_EQUALS)
    }

    fun ls(value: SelectExpr<T>): Condition {
        return CompareCondition(this, value, LESS)
    }

    fun le(value: SelectExpr<T>): Condition {
        return CompareCondition(this, value, LESS_OR_EQUAL)
    }

    fun gt(value: SelectExpr<T>): Condition {
        return CompareCondition(this, value, GREATER)
    }

    fun ge(value: SelectExpr<T>): Condition {
        return CompareCondition(this, value, GREATER_OR_EQUAL)
    }

    fun `in`(value: SelectExpr<T>): Condition {
        return CompareCondition(this, value, IN)
    }

    fun notIn(value: SelectExpr<T>): Condition {
        return CompareCondition(this, value, NOT_IN)
    }

    fun like(value: SelectExpr<T>): Condition {
        return CompareCondition(this, value, LIKE)
    }

    fun notLike(value: SelectExpr<T>): Condition {
        return CompareCondition(this, value, NOT_LIKE)
    }


//    fun between(value: T): BetweenCondition<T> {
//        return BetweenCondition(this, value)
//    }
//
//    fun between(value: Field<T>): BetweenCondition<T> {
//        return BetweenCondition(this, value)
//    }


}



class AsteriskField : SelectExpr<Unit>() {
    val asterisk = "*"

    override fun render(
        configuration: Configuration
    ): Sql {

        return Sql().apply { sqlStr = "*" }
    }


}


open class ValueField<T>(
    val value: T

) : SelectExpr<T>() {


    override fun render(
        configuration: Configuration
    ): Sql {
        return Sql().apply {
            sqlStr = configuration.valueSeparationCharacter + value.toString() + configuration.valueSeparationCharacter
        }
    }




}

open class CalculateField<T>(val field1: SelectExpr<T>, val field2: SelectExpr<T>, val op: CalculateOperator) :
    SelectExpr<T>() {

    override fun render(
        configuration: Configuration
    ): Sql {
        val f1 = field1.render(configuration)
        val f2 = field2.render(configuration)



        return Sql().apply {
            sqlStr = f1.sqlStr+ " " + op.value + " " + f2.sqlStr

        }

    }


}


//
//class QueryBuilder(
//    /** Whether the query is parameterized or not. */
//    val prepared: Boolean
//) {
//    private val internalBuilder = StringBuilder()
//
//    operator fun invoke(body: QueryBuilder.() -> Unit): Unit = body()
//
//    /** Appends all the elements separated using [separator] and using the given [prefix] and [postfix] if supplied. */
//    fun <T> Iterable<T>.appendTo(
//        separator: CharSequence = ", ",
//        prefix: CharSequence = "",
//        postfix: CharSequence = "",
//        transform: QueryBuilder.(T) -> Unit
//    ) {
//        internalBuilder.append(prefix)
//        forEachIndexed { index, element ->
//            if (index > 0) internalBuilder.append(separator)
//            transform(element)
//        }
//        internalBuilder.append(postfix)
//    }
//
//
//    /** Appends the specified [value] to this [QueryBuilder]. */
//    fun append(value: Char): QueryBuilder = apply { internalBuilder.append(value) }
//
//    /** Appends the specified [value] to this [QueryBuilder]. */
//    fun append(value: String): QueryBuilder = apply { internalBuilder.append(value) }
////
////    /** Appends the specified [value] to this [QueryBuilder]. */
////    fun append(value: Expression<*>): QueryBuilder = apply(value::toQueryBuilder)
//
//
//    /** Appends the receiver [Char] to this [QueryBuilder]. */
//    operator fun Char.unaryPlus(): QueryBuilder = append(this)
//
//    /** Appends the receiver [String] to this [QueryBuilder]. */
//    operator fun String.unaryPlus(): QueryBuilder = append(this)
//
//    /** Appends the receiver [Expression] to this [QueryBuilder]. */
//    operator fun Expression<*>.unaryPlus(): QueryBuilder = append(this)
//
//
//    override fun toString(): String = internalBuilder.toString()
//}
//
///** Appends all arguments to this [QueryBuilder]. */
//fun QueryBuilder.append(vararg expr: Any): QueryBuilder = apply {
//    for (item in expr) {
//        when (item) {
//            is Char -> append(item)
//            is String -> append(item)
//            is Expression<*> -> append(item)
//            else -> throw IllegalArgumentException("Can't append $item as it has unknown type")
//        }
//    }
//}
//
///** Appends all the elements separated using [separator] and using the given [prefix] and [postfix] if supplied. */
//fun <T> Iterable<T>.appendTo(
//    builder: QueryBuilder,
//    separator: CharSequence = ", ",
//    prefix: CharSequence = "",
//    postfix: CharSequence = "",
//    transform: QueryBuilder.(T) -> Unit
//): QueryBuilder = builder.apply { this@appendTo.appendTo(separator, prefix, postfix, transform) }
//
