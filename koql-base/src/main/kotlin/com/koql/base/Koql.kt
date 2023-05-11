package com.koql.base

import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass


interface QueryStart {
    fun <Entity : Any, TB : Table<Entity, TB>> table(table: TB): Dao<Entity, TB>
    fun ExecTemplateRaw(sqlTemplate: String, params: MutableMap<String, Any?> = mutableMapOf<String, Any?>()): UpdateSql

    fun <ResultType : Any> QueryTemplateRaw(
        sqlTemplate: String,
        params: Map<String, Any?> = mutableMapOf<String, Any?>(),
        ret: KClass<ResultType>,
        table: Table<*, *>? = null
    ): QuerySql<ResultType>

    fun QueryTemplateRaw(
        sqlTemplate: String,
        params: Map<String, Any?> = mutableMapOf<String, Any?>(),
    ): QuerySql<Map<String , Any?>>

}

interface RawQueryStart : QueryStart {
    fun startTx(): TxQueryStart
    fun startTxAsync(): CompletableFuture<TxQueryStart>
    suspend fun startTxSuspend(): TxQueryStart

    fun withTx(block: (TxQueryStart) -> Unit)
    fun withTxAsync(block: (TxQueryStart) -> CompletableFuture<Unit>)
    suspend fun withTxSuspend(block: suspend (TxQueryStart) -> Unit)

    fun toTxQueryStart(map: Map<String, Any?> = mapOf()): TxQueryStart
}

interface TxQueryStart : QueryStart, Tx {

}

open class DefaultQueryStart(val config: DefaultKoqlConfig) : RawQueryStart {


    override fun <Entity : Any, TB : Table<Entity, TB>> table(table: TB): Dao<Entity, TB> {
        return DaoImpl(
            table,
            DefaultKoqlConfig(HashMap(config.configs).apply { put("context", mutableMapOf<String, Any?>()) })
        )
    }

    override fun ExecTemplateRaw(sqlTemplate: String, params: MutableMap<String, Any?>): UpdateSql {
        return UpdateSql(sqlTemplate, params, executor = config.executor)
    }

    override fun <ResultType : Any> QueryTemplateRaw(
        sqlTemplate: String,
        params: Map<String, Any?>,
        ret: KClass<ResultType>,
        table: Table<*, *>?
    ): QuerySql<ResultType> {
        return QuerySql(sqlTemplate, params, config.executor, ret, config.resultMapper, table)
    }

    @Suppress("UNCHECKED_CAST")
    override fun QueryTemplateRaw(sqlTemplate: String, params: Map<String, Any?>): QuerySql<Map<String, Any?>> {
            return QuerySql(sqlTemplate , params, config.executor, Map::class as KClass<Map<String, Any?>>, config.resultMapper, null)
    }


    override fun startTx(): TxQueryStart {
        val e = config.executor!!.txExecutor().apply { begin() }
        val c = DefaultKoqlConfig(HashMap(config.configs).apply {
            put("executor", e)
            put("context", mutableMapOf<String, Any?>())
        })
        return DefaultTxQueryStart(c)
    }

    override fun startTxAsync(): CompletableFuture<TxQueryStart> {
        val e = config.executor!!.txExecutorAsync().thenCompose { it.beginAsync() }
        val r = e.thenApply {
            val c = DefaultKoqlConfig(HashMap(config.configs).apply {
                put("executor", it)
                put("context", mutableMapOf<String, Any?>())
            })
            DefaultTxQueryStart(c) as TxQueryStart
        }

        return r
    }

    override suspend fun startTxSuspend(): TxQueryStart {
        val e = config.executor!!.txExecutorSuspend().apply { beginSuspend() }
        val c = DefaultKoqlConfig(HashMap(config.configs).apply {
            put("executor", e)
        })
        return DefaultTxQueryStart(c)
    }

    override fun withTx(block: (TxQueryStart) -> Unit) {
        val e = config.executor!! as RawExecutor
        e.withTx(this, block)
    }

    override fun withTxAsync(block: (TxQueryStart) -> CompletableFuture<Unit>) {
        val e = config.executor!! as RawExecutor
        e.withTxAsync(this, block)
    }

    override suspend fun withTxSuspend(block: suspend (TxQueryStart) -> Unit) {
        val e = config.executor!! as RawExecutor
        e.withTxSuspend(this, block)
    }

    override fun toTxQueryStart(map: Map<String, Any?>): TxQueryStart {
        val c = DefaultKoqlConfig(HashMap(config.configs).plus(map).plus("context" to mutableMapOf<String, Any?>()))
        return DefaultTxQueryStart(c)
    }


}

open class DefaultTxQueryStart(
    val config: DefaultKoqlConfig,
    val executor: TxExecutor = config.executor as TxExecutor
) : TxQueryStart, Tx by executor {
    override fun <Entity : Any, TB : Table<Entity, TB>> table(table: TB): Dao<Entity, TB> {
        return DaoImpl(
            table,
            DefaultKoqlConfig(HashMap(config.configs).apply { put("context", mutableMapOf<String, Any?>()) })
        )
    }
    override fun ExecTemplateRaw(sqlTemplate: String, params: MutableMap<String, Any?>): UpdateSql {
        return UpdateSql(sqlTemplate, params, executor = config.executor)
    }

    override fun <ResultType : Any> QueryTemplateRaw(
        sqlTemplate: String,
        params: Map<String, Any?>,
        ret: KClass<ResultType>,
        table: Table<*, *>?
    ): QuerySql<ResultType> {
        return QuerySql(sqlTemplate, params, config.executor, ret, config.resultMapper, table)
    }

    @Suppress("UNCHECKED_CAST")
    override fun QueryTemplateRaw(sqlTemplate: String, params: Map<String, Any?>): QuerySql<Map<String, Any?>> {
        return QuerySql(sqlTemplate , params, config.executor, Map::class as KClass<Map<String, Any?>>, config.resultMapper, null)
    }

}

abstract class KoqlConfig<T : QueryStart>(
    val configs: Map<String, Any?> = mapOf()
) {

    open val refSymbol: String by configs
    open val context: MutableMap<String, Any?> by configs
    open val executor: Executor? by configs
    open val resultMapper: ResultMapper? by configs
    abstract fun start(): T

}


open class DefaultKoqlConfig internal constructor(configs: Map<String, Any?>) : KoqlConfig<DefaultQueryStart>(configs) {

    @JvmOverloads
    constructor(
        refSymbol: String = "",
        executor: Executor? = null,
        resultMapper: ResultMapper? = null
    ) : this(
        mapOf(
            "refSymbol" to refSymbol,
            "context" to mutableMapOf<String, Any?>(),
            "executor" to executor,
            "resultMapper" to resultMapper
        )
    ) {

    }

    override fun start(): DefaultQueryStart {
        return DefaultQueryStart(this)
    }


}

