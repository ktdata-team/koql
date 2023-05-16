package com.koql.base

import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass


interface QueryStart<T> where T : QueryStart<T> {


    fun <Entity : Any, TB : Table<Entity, TB>> table(table: TB): Dao<Entity, TB>

    fun ExecTemplateRaw(
        sqlTemplate: String,
        params: MutableMap<String, Any?> = mutableMapOf<String, Any?>()
    ): UpdateSql

    fun <ParamType : Any> ExecTemplateRaw(
        sqlTemplate: String,
        params: ParamType,
        table: Table<ParamType, *>
    ): UpdateSql {
        val p = table.columnMap.mapValues {
            it.value.get(params)
        }.map {
            table.columnMap.get(it.key)!!.name to (it.key.setMapper?.invoke(it.value) ?: it.value)
        }.toMap().toMutableMap()
        return ExecTemplateRaw(sqlTemplate, p,)
    }

    fun QueryTemplateRaw(
        sqlTemplate: String,
        params: Map<String, Any?> = mutableMapOf<String, Any?>(),
    ): QuerySql<Map<String, Any?>>

    fun <ResultType : Any> QueryTemplateRaw(
        sqlTemplate: String,
        params: Map<String, Any?> = mutableMapOf<String, Any?>(),
        table: Table<ResultType, *>
    ): QuerySql<ResultType>

    fun <ResultType : Any> QueryTemplateRaw(
        sqlTemplate: String,
        params: ResultType,
        table: Table<ResultType, *>
    ): QuerySql<ResultType> {
        val p = table.columnMap.mapValues {
            it.value.get(params)
        }.map {
            table.columnMap.get(it.key)!!.name to (it.key.setMapper?.invoke(it.value) ?: it.value)
        }.toMap().toMutableMap()
        return QueryTemplateRaw(sqlTemplate, p, table)
    }
}

interface RawQueryStart<T, Tx, C> :
    QueryStart<T> where T : RawQueryStart<T, Tx, C>, Tx : TxQueryStart<Tx , C>, C : KoqlConfig<T> {

    override fun ExecTemplateRaw(
        sqlTemplate: String,
        params: MutableMap<String, Any?>
    ): UpdateSql {
        return UpdateSql(sqlTemplate, params, executor = koqlConfig().executor)
    }

    override fun <ResultType : Any> QueryTemplateRaw(
        sqlTemplate: String,
        params: Map<String, Any?>,
        table: Table<ResultType, *>
    ): QuerySql<ResultType> {
        val clz = table.entityKlz
        return QuerySql(sqlTemplate, params, koqlConfig().executor, clz, koqlConfig().resultMapper, table)
    }

    @Suppress("UNCHECKED_CAST")
    override fun QueryTemplateRaw(
        sqlTemplate: String,
        params: Map<String, Any?>,
    ): QuerySql<Map<String, Any?>> {
        return QuerySql(
            sqlTemplate,
            params,
            koqlConfig().executor,
            Map::class as KClass<Map<String, Any?>>,
            koqlConfig().resultMapper,
            null
        )
    }

    fun koqlConfig(): C

    fun newKoqlConfig(config: Map<String, Any?>): C
    fun newTx(config: C): Tx
    fun startTx(): Tx {
        val e = koqlConfig().executor!!.txExecutor().apply { begin() }
        val c = newKoqlConfig(HashMap(koqlConfig().configs).apply {
            put("executor", e)
            put("context", mutableMapOf<String, Any?>())
        })
        return newTx(c)
    }

    fun startTxAsync(): CompletableFuture<Tx> {
        val e = koqlConfig().executor!!.txExecutorAsync().thenCompose { it.beginAsync() }
        val r = e.thenApply {
            val c = newKoqlConfig(HashMap(koqlConfig().configs).apply {
                put("executor", it)
                put("context", mutableMapOf<String, Any?>())
            })
            newTx(c)
        }
        return r
    }

    suspend fun startTxSuspend(): Tx {
        val e = koqlConfig().executor!!.txExecutorSuspend().apply { beginSuspend() }
        val c = newKoqlConfig(HashMap(koqlConfig().configs).apply {
            put("executor", e)
            put("context", mutableMapOf<String, Any?>())
        })
        return newTx(c)
    }

    fun withTx(block: (Tx) -> Unit) {
        val e = koqlConfig().executor!! as RawExecutor
        e.withTx(this, block)
    }

    fun withTxAsync(block: (Tx) -> CompletableFuture<Unit>) {
        val e = koqlConfig().executor!! as RawExecutor
        e.withTxAsync(this, block)
    }

    suspend fun withTxSuspend(block: suspend (Tx) -> Unit) {
        val e = koqlConfig().executor!! as RawExecutor
        e.withTxSuspend(this, block)
    }

    fun toTxQueryStart(map: Map<String, Any?> = mapOf()): Tx {
        val c = newKoqlConfig(
            HashMap(koqlConfig().configs).plus(map).plus("context" to mutableMapOf<String, Any?>())
        )
        return newTx(c)
    }
}

interface TxQueryStart<Txs : TxQueryStart<Txs , C>, C : KoqlConfig<*>> : QueryStart<Txs>, Tx {

    fun koqlConfig(): C

//    fun newKoqlConfig(config: Map<String, Any?>): C
    override fun ExecTemplateRaw(
        sqlTemplate: String,
        params: MutableMap<String, Any?>
    ): UpdateSql {
        return UpdateSql(sqlTemplate, params, executor = koqlConfig().executor)
    }

    override fun <ResultType : Any> QueryTemplateRaw(
        sqlTemplate: String,
        params: Map<String, Any?>,
        table: Table<ResultType, *>
    ): QuerySql<ResultType> {
        val clz = table.entityKlz
        return QuerySql(sqlTemplate, params, koqlConfig().executor, clz, koqlConfig().resultMapper, table)
    }

    @Suppress("UNCHECKED_CAST")
    override fun QueryTemplateRaw(
        sqlTemplate: String,
        params: Map<String, Any?>,
    ): QuerySql<Map<String, Any?>> {
        return QuerySql(
            sqlTemplate,
            params,
            koqlConfig().executor,
            Map::class as KClass<Map<String, Any?>>,
            koqlConfig().resultMapper,
            null
        )
    }

}

open class DefaultQueryStart(val config: DefaultKoqlConfig) :
    RawQueryStart<DefaultQueryStart, DefaultTxQueryStart, DefaultKoqlConfig> {
    override fun koqlConfig(): DefaultKoqlConfig = config

    override fun newKoqlConfig(config: Map<String, Any?>): DefaultKoqlConfig = DefaultKoqlConfig(config)
    override fun newTx(config: DefaultKoqlConfig): DefaultTxQueryStart = DefaultTxQueryStart(config)

    override fun <Entity : Any, TB : Table<Entity, TB>> table(table: TB): DaoImpl<Entity, TB, DefaultQueryStart> =
        DaoImpl(
            table,
            koqlConfig()
        )


}

open class DefaultTxQueryStart(
    val config: DefaultKoqlConfig,
    val executor: TxExecutor = config.executor as TxExecutor
) : TxQueryStart<DefaultTxQueryStart , DefaultKoqlConfig>, Tx by executor {
    override fun koqlConfig(): DefaultKoqlConfig = config
    override fun <Entity : Any, TB : Table<Entity, TB>> table(table: TB): DaoImpl<Entity, TB, DefaultQueryStart> =
        DaoImpl(
            table,
            koqlConfig()
        )


}

abstract class KoqlConfig<T : QueryStart<T>>(
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

