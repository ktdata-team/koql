package com.koql.core.dsl.mysql.structure


import com.koql.core.dsl.mysql.config.Configuration
import com.koql.core.dsl.mysql.config.RenderConfig
import com.koql.core.statement.common.StatementPart


interface SqlStatement : StatementPart

interface Query : SqlStatement {


}

abstract class QueryBase(val config : Configuration, val renderConfig: RenderConfig):Query {
    //    open fun union(query: Query): Union {
//        val union = Union(this , query , config = config)
//        return union
//    }
//
//    open fun unionAll(query: Query): Union {
//        val union = Union(this , query , UnionMode.UNION_ALL , config)
//        return union
//    }
//
//    open fun orderBy(vararg fields: Field<*>): OrderBy {
//        val orderBy = OrderBy(this , fields.toMutableList() , config)
//        return orderBy
//    }

}