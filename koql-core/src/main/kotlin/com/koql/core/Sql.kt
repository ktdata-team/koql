package com.koql.core

open class Sql {
    var sqlStr : String = ""
    var params = mutableListOf<Any?>()
    var prepared  = false


    override fun toString() : String {
        return """sql : $sqlStr  
            |params : $params
            |prepared : $prepared
        """.trimMargin()
    }

}