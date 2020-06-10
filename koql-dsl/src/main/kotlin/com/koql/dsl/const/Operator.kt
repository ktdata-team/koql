package com.koql.dsl.const

class Comparator(val value : String)


val IN = Comparator("in")


val NOT_IN= Comparator("not in")


val EQUALS= Comparator("=")


val NOT_EQUALS= Comparator("<>")


val LESS= Comparator("<")


val LESS_OR_EQUAL= Comparator("<=")


val GREATER= Comparator(">")


val GREATER_OR_EQUAL= Comparator(">=")


val LIKE= Comparator("like")


val NOT_LIKE= Comparator("not like")




class BetweenOperator(val value: String)

val BETWEEN = BetweenOperator("between")

val BETWEEN_AND =  BetweenOperator("and")



class AsOperator(val value: String)

val AS = AsOperator("as")



class CalculateOperator(val value: String)
