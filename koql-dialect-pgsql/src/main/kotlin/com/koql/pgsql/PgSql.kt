package com.koql.pgsql

import com.koql.base.*
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

open class PgQueryStart(val config: PgKoqlConfig) : RawQueryStart {
    override fun <Entity : Any, TB : Table<Entity, TB>> table(table: TB): PgDaoImpl<Entity, TB> {
        return PgDaoImpl(
            table,
            PgKoqlConfig(HashMap(config.configs).apply { put("context", mutableMapOf<String, Any?>()) })
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
        return QuerySql(sqlTemplate , params, config.executor, Map::class as KClass<Map<String, Any?>>, null, null)
    }
    override fun startTx(): TxQueryStart {
        val e = config.executor!!.txExecutor().apply { begin() }
        val c = PgKoqlConfig(HashMap(config.configs).apply {
            put("executor", e)
            put("context", mutableMapOf<String, Any?>())
        })
        return PgTxQueryStart(c)
    }

    override fun startTxAsync(): CompletableFuture<TxQueryStart> {
        val e = config.executor!!.txExecutorAsync().thenCompose { it.beginAsync() }
        val r = e.thenApply {
            val c = PgKoqlConfig(HashMap(config.configs).apply {
                put("executor", it)
                put("context", mutableMapOf<String, Any?>())
            })
            PgTxQueryStart(c) as TxQueryStart
        }

        return r
    }

    override suspend fun startTxSuspend(): TxQueryStart {
        val e = config.executor!!.txExecutorSuspend().apply { beginSuspend() }
        val c = PgKoqlConfig(HashMap(config.configs).apply {
            put("executor", e)
        })
        return PgTxQueryStart(c)
    }

    override fun withTx(block: (TxQueryStart) -> Unit) {
        val e = config.executor!! as RawExecutor
        e.withTx(this , block)
    }

    override fun withTxAsync(block: (TxQueryStart) -> CompletableFuture<Unit>) {
        val e = config.executor!! as RawExecutor
        e.withTxAsync(this , block)
    }

    override suspend fun withTxSuspend(block: suspend (TxQueryStart) -> Unit) {
        val e = config.executor!! as RawExecutor
        e.withTxSuspend(this , block)
    }

    override fun toTxQueryStart(map: Map<String, Any?>): TxQueryStart {
        val c = PgKoqlConfig(HashMap(config.configs).plus(map).plus("context" to mutableMapOf<String, Any?>()))
        return PgTxQueryStart(c)
    }


}

open class PgTxQueryStart(
    val config: PgKoqlConfig,
    val executor: TxExecutor = config.executor as TxExecutor
) : TxQueryStart, Tx by executor {
    override fun <Entity : Any, TB : Table<Entity, TB>> table(table: TB): PgDaoImpl<Entity, TB> {
        return PgDaoImpl(
            table,
            PgKoqlConfig(HashMap(config.configs).apply { put("context", mutableMapOf<String, Any?>()) })
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
        return QuerySql(sqlTemplate , params, config.executor, Map::class as KClass<Map<String, Any?>>, null, null)
    }
}

open class PgKoqlConfig internal constructor(configs: Map<String, Any?>) : KoqlConfig<PgQueryStart>(configs) {

    @JvmOverloads
    constructor(
        refSymbol: String = "\"",
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

    override fun start(): PgQueryStart {
        return PgQueryStart(this)
    }


}

