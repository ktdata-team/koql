package com.koql.core

open class Sql {
    var sqlStr : String = ""
    var params = mutableListOf<Any?>()
    var prepared  = false


}