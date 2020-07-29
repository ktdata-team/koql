package com.koql.core.statement.const

enum class Comparator(val value : String){


     IN ("IN"),


     NOT_IN("NOT IN"),


     EQUALS("="),


     NOT_EQUALS("<>"),


     LESS("<"),


     LESS_OR_EQUAL("<="),


     GREATER(">"),


     GREATER_OR_EQUAL(">="),


     LIKE("LIKE"),


     NOT_LIKE("NOT LIKE");


    override fun toString() :String{
        return value
    }
}





enum class BetweenOperator(val value: String){
    BETWEEN("between"),

    BETWEEN_AND("and");

    override fun toString() :String{
        return value
    }
}





enum class AsOperator(val value: String){
    AS("as");

    override fun toString() :String{
        return value
    }
}




enum class CalculateOperator(val value: String){
    ;
    override fun toString() :String{
        return value
    }
}
