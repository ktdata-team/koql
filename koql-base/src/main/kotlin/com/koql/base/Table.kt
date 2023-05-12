package com.koql.base

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

abstract class Table<Entity : Any, TB>(val name: String, val entityKlz: KClass<Entity>) where TB : Table<Entity, TB>
//    : Where<Entity, TB> where TB : Table<Entity, TB>
{

    val columnMap: MutableMap<Column<Any?, Entity, TB>, KMutableProperty1<Entity, Any?>> = mutableMapOf()
    val resultNameMap: MutableMap<String, String> = mutableMapOf()

    //    val propMap: MutableMap<KMutableProperty1<Entity , Any> , Column<Any?, Entity, TB>> = mutableMapOf()
    @Suppress("UNCHECKED_CAST")
    protected fun <Type> addColumn(col: Column<*, Entity, TB>, prop: KMutableProperty1<Entity, Type>) {
        columnMap.put(col as Column<Any?, Entity, TB>, prop as KMutableProperty1<Entity, Any?>)
        resultNameMap.put(col.name, prop.name)
    }

    //    @Suppress("UNCHECKED_CAST")
    protected fun <Type> column(
        name: String,
        table: TB,
        prop: KMutableProperty1<Entity, Type>,
        setMapper: ((Type?) -> Any?)? = null,
        getMapper: ((Any?) -> Type?)? = null,
    ): Column<Type, Entity, TB> {
        return Column<Type, Entity, TB>(name, table, setMapper, getMapper).also { addColumn(it, prop) }
    }
}

interface KeyGenerator {
    fun nextKey(): String
}

class ParamKeyGenerator : KeyGenerator {

    private var keyOrder = 1
    override fun nextKey(): String {
        val keyStr = "p$keyOrder"
        keyOrder += 1
        return keyStr
    }
}

open class DaoImpl<Entity : Any, TB, Q : QueryStart>(open val table: TB, val config: KoqlConfig<Q>) :
    Dao<Entity, TB> where TB : Table<Entity, TB> {

    var condition: Condition<Entity, TB>? = null
    var limit: Int? = null
    var offset: Int? = null
    val orders = mutableListOf<OrderClause<Entity, TB>>()

    protected val r = config.refSymbol
    protected val mapper = config.resultMapper

    val keyGenerator = ParamKeyGenerator()
    override fun whereEntity(entity: Entity): Where<Entity, TB> {

        val condition = table.columnMap.mapValues {
            val a = it.value.get(entity)
            it.value.get(entity)
        }.filter {
            it.value != null
        }.map {
            it.key.eq(it.value)
        }.reduce { acc, condition ->
            acc.and(condition)
        }
        return where(condition)
    }

    override fun whereEntityWithNull(entity: Entity): Where<Entity, TB> {

        val condition = table.columnMap.mapValues {
            it.value(entity)
        }.map {
            if (it.value == null) {
                it.key.isNull()
            } else {
                it.key.eq(it.value)
            }
        }.reduce { acc, condition ->
            acc.and(condition)
        }
        return where(condition)
    }

    override fun where(condition: Condition<Entity, TB>): Where<Entity, TB> {
        if (this.condition == null) {
            this.condition = condition
        } else {
            this.condition!!.and(condition)
        }
        return this
    }

    override fun whereOr(condition: Condition<Entity, TB>): Where<Entity, TB> {
        if (this.condition == null) {
            this.condition = condition
        } else {
            this.condition!!.or(condition)
        }
        return this
    }

    override fun limit(i: Int): Offset<Entity, TB> {
        this.limit = i
        return this
    }

    override fun offset(i: Int): OrderBy<Entity, TB> {
        this.offset = i
        return this
    }

    override fun orderBy(order: OrderClause<Entity, TB>, vararg orders: OrderClause<Entity, TB>): Select<Entity, TB> {
        this.orders.apply {
            add(order)
            addAll(orders)
        }
        return this
    }

    override fun select(): QuerySql<Entity> {
        val paramMap = mutableMapOf<String, Any?>()

        config.context["keyGenerator"] = keyGenerator

        val sql =
            """SELECT ${table.columnMap.keys.joinToString(",") { """ $r${it.name}$r """ }} FROM $r${table.name}$r ${
                if (condition != null) {
                    val render = condition!!.render(config)
                    paramMap.putAll(render.second)
                    """ WHERE ${render.first}"""
                } else {
                    ""
                }
            } ${
                if (limit != null) {
                    """ LIMIT $limit"""
                } else {
                    ""
                }
            } ${
                if (offset != null) {
                    """ OFFSET $offset"""
                } else {
                    ""
                }
            } ${
                if (orders.isNotEmpty()) {
                    """ ORDER BY""" + orders.joinToString(",") {
                        """ $r${it.column.name}$r ${
                            if (it.order != null) {
                                it.order.name
                            } else {
                                ""
                            }
                        } """
                    }
                } else {
                    ""
                }
            }"""

        return QuerySql(
            sql, paramMap, executor = config.executor, ret = table.entityKlz, resultMapper = mapper,
            table = table
        )
    }

    override fun count(column: Column<*, Entity, TB>?): SingleQuerySql<Int> {
        val paramMap = mutableMapOf<String, Any?>()

        val colName = column?.name?.let { """$r${it}$r""" } ?: "*"

        config.context["keyGenerator"] = keyGenerator

        val sql =
            """SELECT count($colName) FROM $r${table.name}$r ${
                if (condition != null) {
                    val render = condition!!.render(config)
                    paramMap.putAll(render.second)
                    """ WHERE ${render.first}"""
                } else {
                    ""
                }
            } ${
                if (limit != null) {
                    """ LIMIT $limit"""
                } else {
                    ""
                }
            } ${
                if (offset != null) {
                    """ OFFSET $offset"""
                } else {
                    ""
                }
            } ${
                if (orders.isNotEmpty()) {
                    """ ORDER BY""" + orders.joinToString(",") {
                        """ $r${it.column.name}$r ${
                            if (it.order != null) {
                                it.order.name
                            } else {
                                ""
                            }
                        } """
                    }
                } else {
                    ""
                }
            }"""

        return SingleQuerySql(
            sql, paramMap, executor = config.executor, ret = Int::class, resultMapper = mapper,
        )
    }

    override fun update(entity: Entity): UpdateSql {
        val paramMap = mutableMapOf<String, Any?>()
        val values = table.columnMap.mapValues {
            it.value(entity)
        }.filter {
            it.value != null
        }.map {
            val pKey = keyGenerator.nextKey()
            val dbValue = it.key.setMapper?.invoke(it.value) ?: it.value
            """ $r${it.key.name}$r=#{$pKey} """ to (pKey to dbValue)
        }.let {
            val strs = it.joinToString(",") { it.first }
            val values = it.associate { it.second }
            paramMap.putAll(values)
            strs
        }
        config.context["keyGenerator"] = keyGenerator
        val sql = """UPDATE $r${table.name}$r SET $values ${
            if (condition == null) throw NeedWhereException("update statement need where clause") else {
                val render = condition!!.render(config)
                paramMap.putAll(render.second)
                """ WHERE ${render.first} """
            }
        }"""
        return UpdateSql(sql, paramMap, executor = config.executor)
    }


    override fun updateWithNull(entity: Entity): UpdateSql {
        val paramMap = mutableMapOf<String, Any?>()
        val values = table.columnMap.mapValues {
            it.value(entity)
        }.map {
            val pKey = keyGenerator.nextKey()
            val dbValue = it.key.setMapper?.invoke(it.value) ?: it.value
            """ $r${it.key.name}$r=#{$pKey} """ to (pKey to dbValue)
        }.let {
            val strs = it.joinToString(",") { it.first }
            val values = it.associate { it.second }
            paramMap.putAll(values)
            strs
        }
        config.context["keyGenerator"] = keyGenerator
        val sql = """UPDATE $r${table.name}$r SET $values ${
            if (condition == null) throw NeedWhereException("update statement need where clause") else {
                val render = condition!!.render(config)
                paramMap.putAll(render.second)
                """WHERE ${render.first}"""
            }
        }"""
        return UpdateSql(sql, paramMap, executor = config.executor)
    }

    override fun insert(entity: Entity): UpdateSql {
        val paramMap = mutableMapOf<String, Any?>()
        val values = table.columnMap.mapValues {
            it.value(entity)
        }.filter {
            it.value != null
        }.map {
            val pKey = keyGenerator.nextKey()
            val dbValue = it.key.setMapper?.invoke(it.value) ?: it.value
            """"${it.key.name}"""" to (pKey to dbValue)
        }.let {
            val cols = it.joinToString(",") { it.first }
            val valuesTmpl = it.joinToString(",") { """#{${it.second.first}}""" }
            val values = it.associate { it.second }
            paramMap.putAll(values)
            cols to valuesTmpl
        }
        val sql = """INSERT INTO "${table.name}" (${values.first}) VALUES (${values.second})"""
        return UpdateSql(sql, paramMap, executor = config.executor)
    }

    override fun insertWithNull(entity: Entity): UpdateSql {
        val paramMap = mutableMapOf<String, Any?>()
        val values = table.columnMap.mapValues {
            it.value(entity)
        }.map {
            val pKey = keyGenerator.nextKey()
            val dbValue = it.key.setMapper?.invoke(it.value) ?: it.value
            """"${it.key.name}"""" to (pKey to dbValue)
        }.let {
            val cols = it.joinToString(",") { it.first }
            val valuesTmpl = it.joinToString(",") { """#{${it.second.first}}""" }
            val values = it.associate { it.second }
            paramMap.putAll(values)
            cols to valuesTmpl
        }
        val sql = """INSERT INTO "${table.name}" (${values.first}) VALUES (${values.second})"""
        return UpdateSql(sql, paramMap, executor = config.executor)
    }

    override fun delete(): UpdateSql {
        val paramMap = mutableMapOf<String, Any?>()
        config.context["keyGenerator"] = keyGenerator
        val sql = """DELETE FROM "${table.name}" ${
            if (condition == null) throw NeedWhereException("delete statement need where clause") else {
                val render = condition!!.render(config)
                paramMap.putAll(render.second)
                """WHERE ${render.first}"""
            }
        }"""
        return UpdateSql(sql, paramMap, executor = config.executor)
    }

}

interface Dao<Entity : Any, TB> : Where<Entity, TB> where TB : Table<Entity, TB> {

    fun findBy(condition: Condition<Entity, TB>): QuerySql<Entity> {
        return this.where(condition).select()
    }

    fun findPagesBy(
        condition: Condition<Entity, TB>,
        pageable: Pageable<Entity, TB>,
        order: OrderClause<Entity, TB>? = null,
        vararg orders: OrderClause<Entity, TB>,
    ): PageSql<Entity> {

        val base = where(condition).limit(pageable.pageSize).offset(pageable.pageSize * pageable.pageNumber).let {
            if (order != null) {
                it.orderBy(order , *orders)
            }else{
                it
            }
        }

        return PageSql(
            contentSql = base.select(),
            countSql = base.count(),
            page = pageable
        )
    }

}

interface Select<Entity : Any, TB> where TB : Table<Entity, TB> {
    fun select(): QuerySql<Entity>

    fun count(column: Column<*, Entity, TB>? = null): SingleQuerySql<Int>
}

interface OrderBy<Entity : Any, TB> : Select<Entity, TB> where TB : Table<Entity, TB> {
    fun orderBy(order: OrderClause<Entity, TB>, vararg orders: OrderClause<Entity, TB>): Select<Entity, TB>
}

interface Offset<Entity : Any, TB> : OrderBy<Entity, TB> where TB : Table<Entity, TB> {
    fun offset(i: Int): OrderBy<Entity, TB>
}

interface Limit<Entity : Any, TB> : Offset<Entity, TB> where TB : Table<Entity, TB> {
    fun limit(i: Int): Offset<Entity, TB>
}

interface Update<Entity : Any, TB> where TB : Table<Entity, TB> {
    fun update(entity: Entity): UpdateSql
    fun updateWithNull(entity: Entity): UpdateSql
}

interface Insert<Entity : Any, TB> where TB : Table<Entity, TB> {
    fun insert(entity: Entity): UpdateSql
    fun insertWithNull(entity: Entity): UpdateSql
}

interface Delete<Entity : Any, TB> where TB : Table<Entity, TB> {
    fun delete(): UpdateSql

}

interface AfterWhere<Entity : Any, TB> : Limit<Entity, TB>, Insert<Entity, TB>, Delete<Entity, TB>,
    Update<Entity, TB> where TB : Table<Entity, TB>

interface Where<Entity : Any, TB> : AfterWhere<Entity, TB> where TB : Table<Entity, TB> {
    fun whereEntity(entity: Entity): Where<Entity, TB>
    fun whereEntityWithNull(entity: Entity): Where<Entity, TB>
    fun where(condition: Condition<Entity, TB>): Where<Entity, TB>

    fun whereOr(condition: Condition<Entity, TB>): Where<Entity, TB>
}


enum class OrderSort {
    ASC, DESC
}

open class OrderClause<Entity : Any, TB : Table<Entity, TB>>(val column: Column<*, Entity, TB>, val order: OrderSort?)

open class Column<Type, Entity : Any, TB>(
    val name: String, val table: TB,
    val setMapper: ((Type?) -> Any?)? = null,
    val getMapper: ((Any?) -> Type?)? = null,
) where TB : Table<Entity, TB> {


    fun eq(value: Type): Condition<Entity, TB> = NormalCondition(this, value, Eq)
    fun neq(value: Type): Condition<Entity, TB> = NormalCondition(this, value, Neq)
    fun gt(value: Type): Condition<Entity, TB> = NormalCondition(this, value, Gt)
    fun gte(value: Type): Condition<Entity, TB> = NormalCondition(this, value, Gte)
    fun lt(value: Type): Condition<Entity, TB> = NormalCondition(this, value, Lt)
    fun lte(value: Type): Condition<Entity, TB> = NormalCondition(this, value, Lte)
    fun like(value: Type): Condition<Entity, TB> = NormalCondition(this, value, Like)

    fun isNull(): Condition<Entity, TB> = NullCondition(this, IsNull)

    fun isNotNull(): Condition<Entity, TB> = NullCondition(this, IsNotNull)

    fun asc(): OrderClause<Entity, TB> {
        return OrderClause(this, OrderSort.ASC)
    }

    fun desc(): OrderClause<Entity, TB> {
        return OrderClause(this, OrderSort.DESC)
    }
}
