package com.koql.base

interface Condition<Entity : Any, TB> where TB : Table<Entity, TB> {
    fun and(condition: Condition<Entity, TB>): ConnectCondition<Entity, TB> = ConnectCondition(this , condition , AndConditionConnector)
    fun or(condition: Condition<Entity, TB>): ConnectCondition<Entity, TB> = ConnectCondition(this , condition , OrConditionConnector)

    fun render(config: KoqlConfig<*>): Pair<String , Map<String , Any?>>
}

interface ConditionConnector{
    fun render() : String
}
object AndConditionConnector : ConditionConnector {
    override fun render(): String ="AND"

}
object OrConditionConnector : ConditionConnector {
    override fun render(): String ="OR"

}
open class ConnectCondition<Entity : Any, TB>(val left: Condition<Entity, TB>, val right: Condition<Entity, TB>, val connector: ConditionConnector):
    Condition<Entity, TB> where TB : Table<Entity, TB> {

    override fun render(config: KoqlConfig<*>): Pair<String, Map<String, Any?>> {
        val l = left.render(config)
        val r = right.render(config)
        val sql = """(${l.first}) ${connector.render()} (${r.first})"""
        val map = buildMap {
            putAll(l.second)
            putAll(r.second)
        }
        return sql to map
    }

}
open class NormalCondition<Type , Entity : Any, TB> (val column : Column<Type, Entity, TB>, val value : Type, val normalOp : NormalOp):
    Condition<Entity, TB> where TB : Table<Entity, TB> {

    override fun render(config: KoqlConfig<*>): Pair<String, Map<String, Any?>> {
        val keyGenerator = config.context["keyGenerator"] as KeyGenerator
        val r = config.refSymbol
        val key = keyGenerator.nextKey()
        val sql = """ $r${column.name}$r ${normalOp.render()} #{${key}} """

        val dbValue = column.setMapper?.invoke(value) ?: value
        val map = mapOf(key to dbValue)
        return sql to map
    }

}

open class NullCondition<Type , Entity : Any, TB>(val column : Column<Type, Entity, TB>, val nullOp: NullOp):
    Condition<Entity, TB> where TB : Table<Entity, TB> {
    override fun render(config: KoqlConfig<*>): Pair<String, Map<String, Any?>> {
        val r = config.refSymbol
        val sql = """ $r${column.name}$r ${nullOp.render()} """
        val map = emptyMap<String, Any?>()
        return sql to map
    }
}



interface Op {
    fun render() : String
}

interface NormalOp : Op {

}
interface InOp : Op {

}

interface NullOp : Op {

}
object Eq: NormalOp {
    override fun render() = "="
}
object Neq: NormalOp {
    override fun render() = "<>"
}
object Gt: NormalOp {
    override fun render() = ">"
}
object Gte: NormalOp {
    override fun render() = ">="
}
object Lt: NormalOp {
    override fun render() = "<"
}
object Lte: NormalOp {
    override fun render() = "<="
}
object Like: NormalOp {
    override fun render() = "LIKE"
}

object IsNull: NullOp {
    override fun render() = "IS NULL"
}
object IsNotNull: NullOp {
    override fun render() = "IS NOT NULL"
}



object In: InOp {
    override fun render() = "IN"
}
object NotIn: InOp {
    override fun render() = "NOT IN"
}
