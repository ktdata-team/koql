package com.koql.vertxpoolexecutor

import com.koql.base.*
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.templates.SqlTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.coroutineContext

open class VertxTxExecutor(val cnn: SqlConnection) : VertxSqlClientExecutor(cnn), TxExecutor {
    override fun begin() {
        runBlocking { beginSuspend() }
    }

    override fun beginAsync(): CompletableFuture<Void> {
        return cnn.begin().flatMap{ Future.succeededFuture<Void>()}.toCompletionStage().toCompletableFuture()
    }

    override suspend fun beginSuspend() {
        cnn.begin().await()
    }

    override fun commit() {
        runBlocking {
            commitSuspend()
        }
    }

    override fun rollback() {
        runBlocking {
            rollbackSuspend()
        }
    }

    override fun commitAsync(): CompletableFuture<Void> {
        return cnn.transaction()!!.commit().toCompletionStage().toCompletableFuture()
    }

    override fun rollbackAsync(): CompletableFuture<Void> {
        return cnn.transaction()!!.rollback().toCompletionStage().toCompletableFuture()
    }

    override suspend fun commitSuspend() {
        cnn.transaction()!!.commit().await()
    }

    override suspend fun rollbackSuspend() {
        cnn.transaction()!!.rollback().await()
    }

    override fun final() {
        runBlocking { cnn.close().await() }
    }

    override fun finalAsync(): CompletableFuture<Void> {
        return cnn.close().toCompletionStage().toCompletableFuture()
    }

    override suspend fun finalSuspend() {
        cnn.close().await()
    }

    override fun txExecutor(): TxExecutor {
        return this
    }

    override fun txExecutorAsync(): CompletableFuture<TxExecutor> {
        return CompletableFuture.supplyAsync { this }
    }

    override suspend fun txExecutorSuspend(): TxExecutor {
        return this
    }

}

suspend inline fun <T> Pool.tx(crossinline block: suspend (SqlConnection) -> T?): T? {
    val ctx = coroutineContext
    val ret = this.withTransaction {
        val future = CoroutineScope(ctx).future {
            block(it)
        }
        Future.fromCompletionStage(future)
    }
    return ret.await()
}
open class VertxSqlPoolExecutor(
    val pool: Pool
) : VertxSqlClientExecutor(pool) , RawExecutor {

    override fun <T : RawQueryStart<T, Tx, C>, Tx : TxQueryStart<Tx, C>, C : KoqlConfig<T>> withTx(
        rawQueryStart: RawQueryStart<T, Tx, C>,
        block: (Tx) -> Unit
    ) {
        pool.withTransaction {cnn ->
            val ctx = Vertx.currentContext()
            val e =  VertxTxExecutor(cnn)
            val c = rawQueryStart.toTxQueryStart(mapOf("executor" to e))
            ctx.executeBlocking<Unit> {
                block(c)
            }
        }
    }

    override fun <T : RawQueryStart<T, Tx, C>, Tx : TxQueryStart<Tx, C>, C : KoqlConfig<T>> withTxAsync(
        rawQueryStart: RawQueryStart<T, Tx, C>,
        block: (Tx) -> CompletableFuture<Unit>
    ) {
        pool.withTransaction {cnn ->
            val ctx = Vertx.currentContext()
            val e =  VertxTxExecutor(cnn)
            val c = rawQueryStart.toTxQueryStart(mapOf("executor" to e))
            Future.fromCompletionStage(block(c))
        }
    }

    override suspend fun <T : RawQueryStart<T, Tx, C>, Tx : TxQueryStart<Tx, C>, C : KoqlConfig<T>> withTxSuspend(
        rawQueryStart: RawQueryStart<T, Tx, C>,
        block: suspend (Tx) -> Unit
    ) {
        pool.tx { cnn ->
            val e =  VertxTxExecutor(cnn)
            val c = rawQueryStart.toTxQueryStart(mapOf("executor" to e))
            block(c)
        }
    }

    override fun txExecutor(): TxExecutor {
        return runBlocking {
            txExecutorSuspend()
        }
    }

    override fun txExecutorAsync(): CompletableFuture<TxExecutor> {
        return pool.getConnection()
            .map {
                VertxTxExecutor(it) as TxExecutor
            }.toCompletionStage()
            .toCompletableFuture()
    }

    override suspend fun txExecutorSuspend(): TxExecutor {
        val cnn = pool.getConnection().await()
        return VertxTxExecutor(cnn)
    }
}

abstract class VertxSqlClientExecutor(
    val sqlClient: SqlClient
) : Executor {

    override fun execute(
        sql: String,
        params: Map<String, Any?>,
        type: SqlType
    ): List<Map<String, Any?>> {
        return runBlocking { executeSuspend(sql, params, type) }
    }

    override suspend fun executeSuspend(
        sql: String,
        params: Map<String, Any?>,
        type: SqlType
    ): List<Map<String, Any?>> {
        return execute0(sql, params, type).await()
    }

    override fun executeAsync(
        sql: String,
        params: Map<String, Any?>,
        type: SqlType
    ): CompletableFuture<List<Map<String, Any?>>> {
        return execute0(sql, params, type).toCompletionStage().toCompletableFuture()
    }



    override fun executeBatch(
        sql: String,
        params:List< Map<String, Any?>>,
        type: SqlType
    ): List<Map<String, Any?>>{
        return runBlocking { executeBatchSuspend(sql, params, type) }
    }

    override  suspend fun executeBatchSuspend(
        sql: String,
        params:List< Map<String, Any?>>,
        type: SqlType
    ): List<Map<String, Any?>>{
        return executeBatch0(sql, params, type).await()
    }

    override fun executeBatchAsync(
        sql: String,
        params: List<Map<String, Any?>>,
        type: SqlType
    ): CompletableFuture<List<Map<String, Any?>>>{
        return executeBatch0(sql, params, type).toCompletionStage().toCompletableFuture()
    }



    fun executeBatch0(
        sql: String,
        params: List<Map<String, Any?>>,
        type: SqlType = SqlType.QUERY
    ): Future<List<Map<String, Any?>>> {
        return when (type) {
            SqlType.QUERY -> {
                SqlTemplate.forQuery(sqlClient, sql)
                    .executeBatch(params)
                    .map {
                        it.map {
                            val map = mutableMapOf<String , Any?>()
                            val size: Int = it.size()
                            for (pos in 0 until size) {
                                val name: String = it.getColumnName(pos)
                                val value: Any? = it.getValue(pos)
                                map.put(name, value)
                            }
                            map
                        }
                    }
            }

            SqlType.UPDATE -> {
                SqlTemplate.forUpdate(sqlClient, sql)
                    .executeBatch(params)
                    .map {
                        listOf(mapOf<String, Int>("rowCount" to it.rowCount()))
                    }
            }
        }

    }

    fun execute0(
        sql: String,
        params: Map<String, Any?>,
        type: SqlType = SqlType.QUERY
    ): Future<List<Map<String, Any?>>> {
        return when (type) {
            SqlType.QUERY -> {
                SqlTemplate.forQuery(sqlClient, sql)
                    .execute(params)
                    .map {
                        it.map {
                            val map = mutableMapOf<String , Any?>()
                            val size: Int = it.size()
                            for (pos in 0 until size) {
                                val name: String = it.getColumnName(pos)
                                val value: Any? = it.getValue(pos)
                                map.put(name, value)
                            }
                            map
                        }
                    }
            }

            SqlType.UPDATE -> {
                SqlTemplate.forUpdate(sqlClient, sql)
                    .execute(params)
                    .map {
                        listOf(mapOf<String, Int>("rowCount" to it.rowCount()))
                    }
            }
        }

    }
}
