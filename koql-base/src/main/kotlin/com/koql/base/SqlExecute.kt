package com.koql.base

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.impl.Utils
import io.vertx.sqlclient.templates.SqlTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KClass


enum class SqlType {
    QUERY, UPDATE
}

open class TemplateSql<ResultType : Any>(
    val sql: String,
    val params: Map<String, Any?>,
    val type: SqlType = SqlType.QUERY,
    val executor: Executor?,
    val ret: KClass<ResultType>
) {
    protected open fun execute(): List<Map<String, Any?>> {
        return executor!!.execute(sql, params, type)
    }

    protected open suspend fun executeSuspend(
    ): List<Map<String, Any?>> {
        return executor!!.executeSuspend(sql, params, type)
    }

    protected open fun executeAsync(): CompletableFuture<List<Map<String, Any?>>> {
        return executor!!.executeAsync(sql, params, type)
    }
}

interface ResultMapper {
    fun <U, T : Any> convert(obj: U, klz: KClass<T>): T

    fun <U, T : Any> convertList(obj: U, klz: KClass<T>): List<T>

}

open class JacksonResultMapper(val objectMapper: ObjectMapper) : ResultMapper {
    override fun <U, T : Any> convert(obj: U, klz: KClass<T>): T {
        return objectMapper.convertValue(obj, klz.java)
    }

    override fun <U, T : Any> convertList(obj: U, klz: KClass<T>): List<T> {
        return objectMapper.convertValue(obj, object : TypeReference<List<T>>() {})
    }

}

open class QuerySql<ResultType : Any>(
    sql: String,
    params: Map<String, Any?>,
    executor: Executor?,
    ret: KClass<ResultType>,
    val resultMapper: ResultMapper?,
    val table: Table<*, *>?
) : TemplateSql<ResultType>(
    sql,
    params,
    SqlType.QUERY,
    executor,
    ret,
) {

    val columnResultMapper = table?.let {
        { resultMap: Map<String, Any?> ->

            table.columnMap.asIterable().associate { (col, _) ->
                val colName = col.name
                val propName = table.resultNameMap.get(colName) ?: colName
                val getMapper = col.getMapper
                val result = resultMap.get(colName)

                if (result == null || getMapper == null) {
                    return@associate propName to result
                }
                propName to getMapper(result)
            }
        }
    } ?: { it }

    fun fetchOne(): ResultType {
        return super.execute()
            .first()
            .let(columnResultMapper)
            .let {
                resultMapper!!.convert(it, ret)
            }
    }

    fun fetchOneAsync(): CompletableFuture<ResultType> {
        return super.executeAsync()
            .thenApply {
                it.first()
                    .let(columnResultMapper)
                    .let {
                        resultMapper!!.convert(it, ret)
                    }
            }
    }

    suspend fun fetchOneSuspend(): ResultType {
        return super.executeSuspend()
            .first()
            .let(columnResultMapper)
            .let {
                resultMapper!!.convert(it, ret)
            }
    }

    fun fetchOneOrNull(): ResultType? {
        return super.execute()
            .firstOrNull()
            ?.let(columnResultMapper)
            ?.let {
                resultMapper!!.convert(it, ret)
            }
    }

    fun fetchOneAsyncOrNull(): CompletableFuture<ResultType?> {
        return super.executeAsync()
            .thenApply {
                it.firstOrNull()
                    ?.let(columnResultMapper)
                    ?.let {
                        resultMapper!!.convert(it, ret)
                    }
            }
    }

    suspend fun fetchOneOrNullSuspend(): ResultType? {
        return super.executeSuspend()
            .firstOrNull()
            ?.let(columnResultMapper)
            ?.let {
                resultMapper!!.convert(it, ret)
            }
    }

    fun fetchAll(): List<ResultType> {
        return super.execute()
            .let { it.map(columnResultMapper) }
            .let {
                resultMapper!!.convertList(it, ret)
            }
    }

    fun fetchAllAsync(): CompletableFuture<List<ResultType>> {
        return super.executeAsync()
            .thenApply {
                resultMapper!!.convertList(it.map(columnResultMapper), ret)

            }
    }

    suspend fun fetchAllSuspend(): List<ResultType> {
        return super.executeSuspend()
            .let {
                resultMapper!!.convertList(it.map(columnResultMapper), ret)
            }
    }

}

open class UpdateSql(
    sql: String,
    params: Map<String, Any?>,
    executor: Executor?
) : TemplateSql<UpdateResult>(
    sql,
    params,
    SqlType.UPDATE,
    executor,
    UpdateResult::class,
) {
    fun exec(): UpdateResult {
        return super.execute()
            .first()
            .let {
                UpdateResultData(
                    it.get("rowCount") as Int
                )
            }
    }

    suspend fun execSuspend(
    ): UpdateResult {
        return super.executeSuspend().first()
            .let {
                UpdateResultData(
                    it.get("rowCount") as Int
                )
            }
    }

    fun execAsync(): CompletableFuture<UpdateResult> {
        return super.executeAsync().thenApply {
            it.first()
                .let {
                    UpdateResultData(
                        it.get("rowCount") as Int
                    )
                }
        }
    }
}

interface UpdateResult {
    val rowCount: Int
}

data class UpdateResultData(
    override val rowCount: Int
) : UpdateResult

interface Executor {

    fun execute(
        sql: String,
        params: Map<String, Any?>,
        type: SqlType = SqlType.QUERY
    ): List<Map<String, Any?>>

    suspend fun executeSuspend(
        sql: String,
        params: Map<String, Any?>,
        type: SqlType = SqlType.QUERY
    ): List<Map<String, Any?>>

    fun executeAsync(
        sql: String,
        params: Map<String, Any?>,
        type: SqlType = SqlType.QUERY
    ): CompletableFuture<List<Map<String, Any?>>>

    fun txExecutor(): TxExecutor
    fun txExecutorAsync(): CompletableFuture<TxExecutor>
    suspend fun txExecutorSuspend(): TxExecutor
}

interface TxExecutor : Tx , Executor{

}

interface RawExecutor : Executor {
    fun withTx(rawQueryStart: RawQueryStart,block : (TxQueryStart)->Unit)
    fun withTxAsync(rawQueryStart: RawQueryStart,block : (TxQueryStart)->CompletableFuture<Unit>)
    suspend fun withTxSuspend(rawQueryStart: RawQueryStart,block : suspend (TxQueryStart)->Unit)

}

interface Tx {

    fun begin()
    fun beginAsync(): CompletableFuture<Void>
    suspend fun beginSuspend()

    fun commit()
    fun rollback()
    fun commitAsync(): CompletableFuture<Void>
    fun rollbackAsync(): CompletableFuture<Void>
    suspend fun commitSuspend()
    suspend fun rollbackSuspend()

    fun final()
    fun finalAsync(): CompletableFuture<Void>
    suspend fun finalSuspend()

}


