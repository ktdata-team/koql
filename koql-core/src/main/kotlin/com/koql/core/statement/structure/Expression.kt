package com.koql.core.statement.structure

import com.koql.core.Sql
import com.koql.core.config.Configuration

abstract class Expression<T> {
    private val _hashCode: Int by lazy { toString().hashCode() }

//    /** Appends the SQL representation of this expression to the specified [queryBuilder]. */
//    abstract fun toQueryBuilder(queryBuilder: QueryBuilder)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Expression<*>) return false

        if (toString() != other.toString()) return false

        return true
    }

    override fun hashCode(): Int = _hashCode

//    override fun toString(): String = QueryBuilder(false).append(this).toString()

    abstract fun render(configuration: Configuration) : Sql

}

abstract class SelectExpr<T> :  Expression<T>() {

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
