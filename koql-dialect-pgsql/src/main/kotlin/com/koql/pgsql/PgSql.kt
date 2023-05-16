package com.koql.pgsql

import com.koql.base.*

open class PgQueryStart(val config: PgKoqlConfig) : RawQueryStart<PgQueryStart, PgTxQueryStart, PgKoqlConfig> {
    override fun koqlConfig(): PgKoqlConfig = config

    override fun newKoqlConfig(config: Map<String, Any?>): PgKoqlConfig = PgKoqlConfig(config)
    override fun newTx(config: PgKoqlConfig): PgTxQueryStart = PgTxQueryStart(config)

    override fun <Entity : Any, TB : Table<Entity, TB>> table(table: TB): PgDaoImpl<Entity, TB> =
        PgDaoImpl(
            table,
            koqlConfig()
        )


}

open class PgTxQueryStart(
    val config: PgKoqlConfig,
    val executor: TxExecutor = config.executor as TxExecutor
) : TxQueryStart<PgTxQueryStart , PgKoqlConfig>, Tx by executor {
    override fun koqlConfig(): PgKoqlConfig = config

    override fun <Entity : Any, TB : Table<Entity, TB>> table(table: TB): PgDaoImpl<Entity, TB> =
        PgDaoImpl(
            table,
            koqlConfig()
        )

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

