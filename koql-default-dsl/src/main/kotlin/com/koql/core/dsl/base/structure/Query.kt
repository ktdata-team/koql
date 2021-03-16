package com.koql.core.dsl.base.structure

import com.koql.core.config.Configuration
import com.koql.core.dsl.base.dql.OrderBy
import com.koql.core.dsl.base.dql.Union
import com.koql.core.dsl.base.dql.UnionMode
import com.koql.core.statement.structure.Field
import com.koql.core.statement.structure.SqlStatement

abstract class Query(config : Configuration) : SqlStatement(config) {

    open fun union(query: Query): Union {
        val union = Union(this , query , config = config)
        return union
    }

    open fun unionAll(query: Query): Union {
        val union = Union(this , query , UnionMode.UNION_ALL , config)
        return union
    }

    open fun orderBy(vararg fields: Field<*>): OrderBy {
        val orderBy = OrderBy(this , fields.toMutableList() , config)
        return orderBy
    }

}