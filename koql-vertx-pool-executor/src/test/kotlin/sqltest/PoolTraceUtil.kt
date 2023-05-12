/*
package sqltest

import com.github.freva.asciitable.AsciiTable
import com.github.freva.asciitable.Column
import io.vertx.core.Context
import io.vertx.core.ServiceHelper
import io.vertx.core.impl.VertxBuilder
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.core.spi.VertxTracerFactory
import io.vertx.core.spi.tracing.SpanKind
import io.vertx.core.spi.tracing.TagExtractor
import io.vertx.core.spi.tracing.VertxTracer
import io.vertx.core.tracing.TracingOptions
import io.vertx.core.tracing.TracingPolicy
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.impl.SqlResultImpl
import io.vertx.sqlclient.impl.tracing.QueryRequest
import mu.KotlinLogging
import java.util.function.BiConsumer
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf


val logTracer = LogTracer

open class LogTracerFactory : VertxTracerFactory {

    val tracer = logTracer

    val resolverMap = LogTracer.resolverMap

    override fun init(builder: VertxBuilder) {
        val options = builder.options().tracingOptions
        builder.tracer(tracer(options))
    }

    override fun tracer(options: TracingOptions?): VertxTracer<*, *> {
        return tracer.apply { resolverMap.putAll(resolverMap) }
    }

    fun addResolver(resolver: TraceTypeResolver<*>) {
        resolverMap.put(resolver.getClass(), resolver as TraceTypeResolver<in Any>)
    }

    fun addResolvers(vararg resolvers: TraceTypeResolver<*>) {
        resolvers.forEach {
            addResolver(it)
        }
    }

    fun addResolvers(resolvers: List<TraceTypeResolver<*>>) {
        addResolvers(*resolvers.toTypedArray())
    }

}

open class DefaultLogTracerFactory : LogTracerFactory() {
    init {

        val facs = ServiceHelper.loadFactories(TraceTypeResolver::class.java)
        addResolvers(*facs.toTypedArray())
*/
/*        addResolvers(
            object : TraceTypeResolver<QueryRequest> {
                override fun getClass(): KClass<QueryRequest> {
                    return QueryRequest::class
                }

                override fun resolve(data: QueryRequest?): String {
                    if (data == null) return ""

                    return "\n" + """
                        sql => ${data.sql()}
                        tuple => ${data.tuples().map { it.deepToString() }}
                    """.trimIndent()
                }

            },
            object : TraceTypeResolver<RowSet<*>> {
                override fun getClass(): KClass<RowSet<*>> {
                    return RowSet::class
                }

                override fun resolve(data: RowSet<*>?): String {
                    if (data == null) return ""
                    val cols = data.columnsNames()
                        .map {
                            Column().header(it).with<Map<String, Any?>> { map -> map[it].toString() }
                        }
                    val rows = data.map {
                        if (it is Row) {
                            it.toJson().map
                        } else {
                            DatabindCodec.mapper().convertValue(it, Map::class.java) as Map<String, Any?>
                        }
                    }

                    val ret = AsciiTable.getTable(rows, cols)
                    return """
$ret
                    """
                }

            },
            object : TraceTypeResolver<SqlResultImpl<*>> {
                override fun getClass(): KClass<SqlResultImpl<*>> {
                    return SqlResultImpl::class
                }

                override fun resolve(data: SqlResultImpl<*>?): String {
                    if (data == null) return ""
                    val cols = listOf(
                        Column().header("rowCount").with<Map<String, Any?>> { map -> map["rowCount"].toString() }
                    )
                    val rows = listOf(mapOf("rowCount" to data.rowCount()))

                    val ret = AsciiTable.getTable(rows, cols)
                    return """
$ret
                    """
                }

            }
        )*//*

    }
}
class QueryRequestTraceTypeResolver: TraceTypeResolver<QueryRequest> {
    override fun getClass(): KClass<QueryRequest> {
        return QueryRequest::class
    }

    override fun resolve(data: QueryRequest?): String {
        if (data == null) return ""

        return "\n" + """
                        sql => ${data.sql()}
                        tuple => ${data.tuples().map { it.deepToString() }}
                    """.trimIndent()
    }

}
class RowSetTraceTypeResolver: TraceTypeResolver<RowSet<*>> {
    override fun getClass(): KClass<RowSet<*>> {
        return RowSet::class
    }

    override fun resolve(data: RowSet<*>?): String {
        if (data == null) return ""
        if (data.columnsNames() == null) {
            return data.toList().toString()
        }
        val cols = data.columnsNames()
            .map {
                Column().header(it).with<Map<String, Any?>> { map -> map[it].toString() }
            }
        val rows = data.map {
            if (it is Row) {
                it.toJson().map
            } else {
                DatabindCodec.mapper().convertValue(it, Map::class.java) as Map<String, Any?>
            }
        }

        val ret = AsciiTable.getTable(rows, cols)
        return """
$ret
                    """
    }

}
class SqlResultImplTraceTypeResolver: TraceTypeResolver<SqlResultImpl<*>> {
    override fun getClass(): KClass<SqlResultImpl<*>> {
        return SqlResultImpl::class
    }

    override fun resolve(data: SqlResultImpl<*>?): String {
        if (data == null) return ""
        val cols = listOf(
            Column().header("rowCount").with<Map<String, Any?>> { map -> map["rowCount"].toString() }
        )
        val rows = listOf(mapOf("rowCount" to data.rowCount()))

        val ret = AsciiTable.getTable(rows, cols)
        return """
$ret
                    """
    }

}
val logger = KotlinLogging.logger { }

interface TraceTypeResolver<T : Any> {

    fun getClass(): KClass<T>

    fun resolve(data: T?): String
}

val NO_OP_TraceTypeResolver = object : TraceTypeResolver<Any> {
    override fun getClass(): KClass<Any> {
        return Any::class
    }

    override fun resolve(data: Any?): String {
        return "$data"
    }

}

data class LogData<R, S>(
    var request: Request<R>?,
    var response: Response<S>?
)

data class Request<R>(
    var context: Context? = null,
    var kind: SpanKind? = null,
    var policy: TracingPolicy? = null,
    var request: R? = null,
    var operation: String? = null,
    var headers: MutableIterable<MutableMap.MutableEntry<String, String>>? = null,
    var headersFunction: BiConsumer<String, String>? = null,
    var tagExtractor: TagExtractor<R>? = null
)

data class Response<R>(
    var context: Context? = null,
    var response: R? = null,
    var payload: Any? = null,
    var failure: Throwable? = null,
    var tagExtractor: TagExtractor<R>? = null
)

object LogTracer : VertxTracer<Any?, Any?> {

    var resolverMap = mutableMapOf<KClass<out Any>, TraceTypeResolver<in Any>>()

    fun getResolver(data: Any?): TraceTypeResolver<in Any> {
        if (data == null) {
            return NO_OP_TraceTypeResolver
        }
        val klz = data::class.java.kotlin
        val r = resolverMap.keys
            .firstOrNull {
                klz.isSubclassOf(it)
            } ?: return NO_OP_TraceTypeResolver
        return resolverMap[r]!!

    }

    fun resolve(data: Any?): String {
        if (data == null) return ""

        val resolver = getResolver(data)
        return resolver.resolve(data)
    }

    override fun <R : Any?> receiveRequest(
        context: Context?,
        kind: SpanKind?,
        policy: TracingPolicy?,
        request: R,
        operation: String?,
        headers: MutableIterable<MutableMap.MutableEntry<String, String>>?,
        tagExtractor: TagExtractor<R>?
    ): Any? {

        return LogData<R, R>(
            request = Request(
                context = context,
                kind = kind,
                policy = policy,
                request = request,
                operation = operation,
                headers = headers,
                headersFunction = null,
                tagExtractor = tagExtractor,
            ),
            response = null
        )

    }

    override fun <R : Any?> receiveResponse(
        context: Context?,
        response: R,
        payload: Any?,
        failure: Throwable?,
        tagExtractor: TagExtractor<R>?
    ) {
        try {

//            if (!logger.isDebugEnabled) {
//                return
//            }

            payload as LogData<R, R>

            val req = resolve(payload.request?.request)

            val resp = resolve(response)

            logger.debug { "[${payload.request?.kind}] " }
            logger.debug { "operation: ${payload.request?.operation}  " }
            logger.debug { "request: $req " }
            logger.debug { "headers: ${payload.request?.headers}" }
            logger.debug { "response: $resp " }
            logger.debug { "failure: $failure " }
        } catch (e: Throwable) {
            logger.error(e) { }
        }
    }

    override fun <R : Any?> sendRequest(
        context: Context?,
        kind: SpanKind?,
        policy: TracingPolicy?,
        request: R,
        operation: String?,
        headers: BiConsumer<String, String>?,
        tagExtractor: TagExtractor<R>?
    ): Any? {

        return LogData<R, R>(
            request = Request(
                context = context,
                kind = kind,
                policy = policy,
                request = request,
                operation = operation,
                headersFunction = headers,
                tagExtractor = tagExtractor,
            ),
            response = null
        )
    }

    override fun <R : Any?> sendResponse(
        context: Context?,
        response: R,
        payload: Any?,
        failure: Throwable?,
        tagExtractor: TagExtractor<R>?

    ) {
        try {

//            if (!logger.isDebugEnabled) {
//                return
//            }
            payload as LogData<R, R>

            val req = resolve(payload.request?.request)

            val resp = resolve(response)

            logger.debug { "[${payload.request?.kind}] " }
            logger.debug { "operation: ${payload.request?.operation}  " }
            logger.debug { "request: $req " }
            logger.debug { "headers: ${payload.request?.headers}" }
            logger.debug { "response: $resp " }
            logger.debug { "failure: $failure " }

        } catch (e: Throwable) {
            logger.error(e) { }
        }
    }
}
*/
