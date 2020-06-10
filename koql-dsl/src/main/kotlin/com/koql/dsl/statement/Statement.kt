package com.koql.dsl.statement

import com.koql.dsl.condition.Condition
import com.koql.dsl.schema.Renderable
import com.koql.dsl.schema.SubSelect
import com.koql.dsl.schema.Table


interface Statement : Renderable {

}

interface SelectPhaseI : SelectFinalI {
    fun from(vararg tables: Table): SelectWherePhaseI
}

interface SelectWherePhaseI : SelectFinalI {
    fun where(conditions: Condition): SelectFinalI
}

interface SelectFinalI {

    fun `as`(name :String) : SubSelect

    fun getSql() : String
}
