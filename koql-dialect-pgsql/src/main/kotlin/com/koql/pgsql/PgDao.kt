package com.koql.pgsql

import com.koql.base.*

interface UpdateReturning<Entity : Any, TB> where TB : Table<Entity, TB> {
    fun updateReturning(entity: Entity): QuerySql<Entity>
    fun updateWithNullReturning(entity: Entity): QuerySql<Entity>
}

interface InsertReturning<Entity : Any, TB> where TB : Table<Entity, TB> {
    fun insertReturning(entity: Entity): QuerySql<Entity>
    fun insertWithNullReturning(entity: Entity): QuerySql<Entity>
}

interface DeleteReturning<Entity : Any, TB> where TB : Table<Entity, TB> {
    fun deleteReturning(): QuerySql<Entity>

}

interface PgAfterWhere<Entity : Any, TB> : Limit<Entity, TB>, Insert<Entity, TB>, Delete<Entity, TB>,
    Update<Entity, TB> , InsertReturning<Entity, TB>, DeleteReturning<Entity, TB>,
    UpdateReturning<Entity, TB> where TB : Table<Entity, TB>

open class PgDaoImpl<Entity : Any, TB : Table<Entity, TB>>(override val table: TB, config: PgKoqlConfig) : DaoImpl<Entity , TB , PgQueryStart>(table , config) , PgAfterWhere<Entity , TB>{


    override fun updateReturning(entity: Entity): QuerySql<Entity> {
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
                """ WHERE ${render.first} RETURNING * """
            }
        }"""
        return QuerySql(
            sql, paramMap, executor = config.executor, ret = table.entityKlz, resultMapper = mapper,
            table = table
        )
    }

    override fun updateWithNullReturning(entity: Entity): QuerySql<Entity> {
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
                """WHERE ${render.first} RETURNING * """
            }
        }"""
        return QuerySql(
            sql, paramMap, executor = config.executor, ret = table.entityKlz, resultMapper = mapper,
            table = table
        )
    }

    override fun insertReturning(entity: Entity): QuerySql<Entity> {
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
        val sql = """INSERT INTO "${table.name}" (${values.first}) VALUES (${values.second}) RETURNING *"""
        return QuerySql(
            sql, paramMap, executor = config.executor, ret = table.entityKlz, resultMapper = mapper,
            table = table
        )
    }

    override fun insertWithNullReturning(entity: Entity): QuerySql<Entity> {
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
        val sql = """INSERT INTO "${table.name}" (${values.first}) VALUES (${values.second}) RETURNING * """
        return QuerySql(
            sql, paramMap, executor = config.executor, ret = table.entityKlz, resultMapper = mapper,
            table = table
        )
    }

    override fun deleteReturning(): QuerySql<Entity> {
        val paramMap = mutableMapOf<String, Any?>()
        config.context["keyGenerator"] = keyGenerator
        val sql = """DELETE FROM "${table.name}" ${
            if (condition == null) throw NeedWhereException("delete statement need where clause") else {
                val render = condition!!.render(config)
                paramMap.putAll(render.second)
                """WHERE ${render.first} RETURNING *"""
            }
        }"""
        return QuerySql(
            sql, paramMap, executor = config.executor, ret = table.entityKlz, resultMapper = mapper,
            table = table
        )
    }

}
