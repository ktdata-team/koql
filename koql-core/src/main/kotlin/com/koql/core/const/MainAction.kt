package com.koql.core.const

enum class MainAction(val value: String) {
    SELECT("SELECT"),
    FROM("FROM"),
    WHERE("WHERE");
//    DISTINCT("DISTINCT");


    override fun toString() :String{
        return value
    }
}

enum class Disdinct(val value: String) {
    DISTINCT("DISTINCT") ,
    NONE("");


    override fun toString() :String{
        return value
    }
}
