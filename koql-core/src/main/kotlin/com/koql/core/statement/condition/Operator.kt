package com.koql.core.statement.condition

open class Comparator(val value : String)

val EQUALS= Comparator("=")


val NOT_EQUALS= Comparator("<>")


val LESS= Comparator("<")


val LESS_OR_EQUAL= Comparator("<=")


val GREATER= Comparator(">")


val GREATER_OR_EQUAL= Comparator(">=")




sealed class InComparator(value : String) : Comparator(value)

object IN : InComparator("IN")

object NOT_IN: InComparator("NOT IN")





sealed class LikeComparator(value : String) : Comparator(value)

object LIKE : LikeComparator("LIKE")

object NOT_LIKE :  LikeComparator("NOT LIKE")




class BetweenOperator(val value: String)

val BETWEEN = BetweenOperator("between")

val BETWEEN_AND =  BetweenOperator("and")





class AsOperator(val value: String)

val AS = AsOperator("as")



class CalculateOperator(val value: String)
