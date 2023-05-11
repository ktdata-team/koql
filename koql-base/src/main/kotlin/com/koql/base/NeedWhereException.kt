package com.koql.base

open class NeedWhereException: Exception {

    constructor(msg: String) : super(msg) {

    }
    constructor(msg: String , cause :Throwable) : super(msg ,cause) {

    }
}
