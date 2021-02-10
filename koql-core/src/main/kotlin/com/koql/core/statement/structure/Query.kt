package com.koql.core.statement.structure

import com.koql.core.config.Configuration
import com.koql.core.statement.common.StatementPart
import com.koql.core.statement.dql.From
import com.koql.core.statement.dql.OrderBy
import com.koql.core.statement.dql.Union
import com.koql.core.statement.dql.UnionMode

abstract class Query(val config : Configuration) : StatementPart {

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