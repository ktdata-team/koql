package com.koql.dsl.const

class MainAction (val value : String)

 val SELECT = MainAction ("SELECT")
 val FROM = MainAction ("FROM")
 val WHERE = MainAction ("WHERE")
 val DISTINCT = MainAction ("DISTINCT")