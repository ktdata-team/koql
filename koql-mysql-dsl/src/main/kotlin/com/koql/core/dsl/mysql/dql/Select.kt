package com.koql.core.dsl.mysql.dql


import com.koql.core.condition.Condition
import com.koql.core.dsl.mysql.config.Configuration
import com.koql.core.dsl.mysql.config.RenderConfig
import com.koql.core.dsl.mysql.structure.AliasTable
import com.koql.core.dsl.mysql.structure.Query
import com.koql.core.dsl.mysql.structure.QueryBase
import com.koql.core.statement.structure.Field
import com.koql.core.statement.structure.TableRef



interface SelectFrom : Query{
    fun from(vararg fields: TableRef) : From
//    fun where(condition: Condition): Where
}

interface SelectDistinct :HighPriority{

    fun distinct() : HighPriority
    fun all() : HighPriority
    fun distinctRow() : HighPriority

    fun distinct(vararg field : Field<*>) : SelectFrom
    fun all(vararg field : Field<*>) : SelectFrom
    fun distinctRow(vararg field : Field<*>) : SelectFrom

}

interface HighPriority :StraightJoin{
    fun highPriority() : StraightJoin
    fun highPriority(vararg field : Field<*>) : SelectFrom

}

interface StraightJoin : SqlSmallResult{
    fun straightJoin() : SqlSmallResult

    fun straightJoin(vararg field : Field<*>) : SelectFrom
}

interface SqlSmallResult : SqlBigResult{
    fun sqlSmallResult() : SqlBigResult

    fun sqlSmallResult(vararg field : Field<*>) : SelectFrom
}

interface SqlBigResult :SqlBufferResult{
    fun sqlBigResult() : SqlBufferResult
    fun sqlBigResult(vararg field : Field<*>) : SelectFrom
}

interface SqlBufferResult : SqlNoCache{
    fun sqlBufferResult() : SqlNoCache
    fun sqlBufferResult(vararg field : Field<*>) : SelectFrom
}

interface SqlNoCache : SqlCalcFoundRows {
    fun sqlNoCache() : SqlCalcFoundRows
    fun sqlNoCache(vararg field : Field<*>) : SelectFrom

}

interface SqlCalcFoundRows {
    fun sqlCalcFoundRows(vararg field : Field<*>) : SelectFrom
}

enum class DistinctParam(val render : String) {
    NONE(""),
    ALL("ALL") ,
    DISDINCT("DISDINCT"),
    DISDINCTROW("DISDINCTROW")
}

data class SelectBuilder(
    var fields: MutableList<Field<*>> = mutableListOf(),
    var distinct : DistinctParam = DistinctParam.NONE,
    var sqlCalcFoundRows : Boolean = false,
    var sqlNoCache : Boolean = false,
    var sqlBufferResult : Boolean = false,
    var sqlBigResult : Boolean = false,
    var sqlSmallResult : Boolean = false,
    var straightJoin : Boolean = false,
    var highPriority : Boolean = false,
    val config : Configuration,
    val renderConfig: RenderConfig
): SelectDistinct{
    override fun distinct(): HighPriority {
        distinct = DistinctParam.DISDINCT
        return this
    }

    override fun distinct(vararg field: Field<*>): SelectFrom {
        distinct = DistinctParam.DISDINCT
        fields.addAll(field)
        return Select(this)
    }

    override fun all(): HighPriority {
        distinct = DistinctParam.ALL
        return this
    }

    override fun all(vararg field: Field<*>): SelectFrom {
        distinct = DistinctParam.ALL
        fields.addAll(field)
        return Select(this)
    }

    override fun distinctRow(): HighPriority {
        distinct = DistinctParam.DISDINCTROW
        return this
    }

    override fun distinctRow(vararg field: Field<*>): SelectFrom {
        distinct = DistinctParam.DISDINCTROW
        fields.addAll(field)
        return Select(this)
    }

    override fun highPriority(): StraightJoin {
        highPriority = true
        return this
    }

    override fun highPriority(vararg field: Field<*>): SelectFrom {
        highPriority = true
        fields.addAll(field)
        return Select(this)
    }


    override fun straightJoin(): SqlSmallResult {
        straightJoin = true
        return this
    }

    override fun straightJoin(vararg field: Field<*>): SelectFrom {
        straightJoin = true
        fields.addAll(field)
        return Select(this)
    }

//    override infix fun straightJoin(straightJoin : straightJoin) : SqlSmallResult {
//        this.straightJoin = true
//        return this
//    }

    override fun sqlSmallResult(): SqlSmallResult {
        sqlSmallResult = true
        return this
    }

    override fun sqlSmallResult(vararg field: Field<*>): SelectFrom {
        sqlSmallResult = true
        fields.addAll(field)
        return Select(this)
    }

    override fun sqlBigResult(): SqlBigResult {
        sqlBigResult = true
        return this
    }

    override fun sqlBigResult(vararg field: Field<*>): SelectFrom {
        sqlBigResult = true
        fields.addAll(field)
        return Select(this)
    }

    override fun sqlBufferResult(): SqlBufferResult {
       sqlBufferResult = true
        return this
    }

    override fun sqlBufferResult(vararg field: Field<*>): SelectFrom {
        sqlBufferResult = true
        fields.addAll(field)
        return Select(this)
    }

    override fun sqlNoCache(): SqlNoCache {
        sqlNoCache = true
        return this
    }

    override fun sqlNoCache(vararg field: Field<*>): SelectFrom {
        sqlNoCache = true
        fields.addAll(field)
        return Select(this)
    }

    override fun sqlCalcFoundRows(vararg field: Field<*>): SelectFrom {
        sqlCalcFoundRows = true
        fields.addAll(field)
        return Select(this)
    }


}

open class Select(
    internal val selectBuilder: SelectBuilder
) : QueryBase(selectBuilder.config , selectBuilder.renderConfig) , SelectFrom{


    companion object {
        @JvmStatic
        protected val SELECT = "SELECT"
        @JvmStatic
        protected val HIGH_PRIORITY = "HIGH_PRIORITY"
        @JvmStatic
        protected val STRAIGHT_JOIN = "STRAIGHT_JOIN"
        @JvmStatic
        protected val SQL_SMALL_RESULT = "SQL_SMALL_RESULT"
        @JvmStatic
        protected val SQL_BIG_RESULT = "SQL_BIG_RESULT"
        @JvmStatic
        protected val SQL_BUFFER_RESULT = "SQL_BUFFER_RESULT"
        @JvmStatic
        protected val SQL_NO_CACHE = "SQL_NO_CACHE"
        @JvmStatic
        protected val SQL_CALC_FOUND_ROWS = "SQL_CALC_FOUND_ROWS"

    }


    override fun from(vararg fields: TableRef): From {

        if (fields.any { it is AliasTable }){
            renderConfig.hasAliasTable = true
            fields
                .filterIsInstance<AliasTable>()
                .forEach {
                    renderConfig.aliasTableMap.apply {
                        put(it.table , it)
                    }
                }

        }

        val from = From(this , fields.toMutableList() , config ,renderConfig )
        return from
    }

//    override fun where(condition: Condition): Where {
//        val where = Where(this , condition , config , renderConfig)
//        return where
//    }




    fun render() : String {
      return  render(config ,renderConfig)
    }



    override fun render(config : Configuration , renderConfiguration: RenderConfig): String {
        val (fields , distinct , sqlCalcFoundRows , sqlNoCache , sqlBufferResult , sqlBigResult , sqlSmallResult , straightJoin ,highPriority) = selectBuilder


        val sql = """$SELECT ${distinct.render} ${if(highPriority) HIGH_PRIORITY else ""} ${if(straightJoin) STRAIGHT_JOIN else ""} ${if(sqlSmallResult) SQL_SMALL_RESULT else ""} ${if(sqlBigResult) SQL_BIG_RESULT else ""} ${if(sqlBufferResult) SQL_BUFFER_RESULT else ""} ${if(sqlNoCache) SQL_NO_CACHE else ""} ${if(sqlCalcFoundRows) SQL_CALC_FOUND_ROWS else ""} ${fields.joinToString(" , ") { it.render(config , renderConfiguration) }}"""
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val fields = selectBuilder.fields
        val params = mutableMapOf<String, Any?>()
            .let { map ->
                fields.forEach {
                    map.putAll(it.parameters())
                }
                map
            }
        return params
    }

}


