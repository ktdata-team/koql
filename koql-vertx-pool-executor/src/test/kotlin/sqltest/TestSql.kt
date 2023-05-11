package sqltest

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.koql.base.DefaultKoqlConfig
import com.koql.base.JacksonResultMapper
import com.koql.base.Table
import com.koql.pgsql.PgKoqlConfig
import com.koql.vertxpoolexecutor.VertxSqlPoolExecutor
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.core.tracing.TracingOptions
import io.vertx.core.tracing.TracingPolicy
import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.pgclient.data.Inet
import io.vertx.pgclient.data.Interval
import io.vertx.sqlclient.PoolOptions
import io.vertx.sqlclient.data.Numeric
import mu.KotlinLogging
import java.math.BigDecimal
import java.net.InetAddress
import java.time.*
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration


data class Test0(
    var typeBoolean: Boolean? = null,
    var typeInt2: Short? = null,
    var typeInt4: Int? = null,
    var typeInt8: Long? = null,
    var typeFloat4: Float? = null,
    var typeFloat8: Double? = null,
    var typeChar: String? = null,
    var typeVarchar: String? = null,
    var typeText: String? = null,
    var typeName: String? = null,
    var typeSerial2: Short? = null,
    var typeSerial4: Int? = null,
    var typeSerial8: Long? = null,
    var typeNumeric: BigDecimal? = null,
    var typeUuid: UUID? = null,
    var typeDate: LocalDate? = null,
    var typeTime: LocalTime? = null,
    var typeTimetz: OffsetTime? = null,
    var typeTimestamp: LocalDateTime? = null,
    var typeTimestamptz: OffsetDateTime? = null,
    var typeInterval: Duration? = null,
    var typeBytea: ByteArray? = null,
    var typeInet: InetAddress? = null,
    var typeVarcharArray: Array<String>? = null,
) {

}

object TEST : Table<Test0, TEST>("test", Test0::class) {

    val typeBoolean = column("type_boolean", TEST, Test0::typeBoolean)
    val typeInt2 = column("type_int_2", TEST, Test0::typeInt2)
    val typeInt4 = column("type_int_4", TEST, Test0::typeInt4)
    val typeInt8 = column("type_int_8", TEST, Test0::typeInt8)
    val typeFloat4 = column("type_float_4", TEST, Test0::typeFloat4)
    val typeFloat8 = column("type_float_8", TEST, Test0::typeFloat8)
    val typeChar = column("type_char", TEST, Test0::typeChar)
    val typeVarchar = column("type_varchar", TEST, Test0::typeVarchar)
    val typeText = column("type_text", TEST, Test0::typeText)
    val typeName = column("type_name", TEST, Test0::typeName)
    val typeSerial2 = column("type_serial_2", TEST, Test0::typeSerial2)
    val typeSerial4 = column("type_serial_4", TEST, Test0::typeSerial4)
    val typeSerial8 = column("type_serial_8", TEST, Test0::typeSerial8)
    val typeNumeric = column("type_numeric", TEST, Test0::typeNumeric,
        { it?.let { Numeric.create(it) } },
        { it?.let { (it as Numeric).bigDecimalValue() } })
    val typeUuid = column("type_uuid", TEST, Test0::typeUuid)
    val typeDate = column("type_date", TEST, Test0::typeDate)
    val typeTime = column("type_time", TEST, Test0::typeTime)
    val typeTimetz = column("type_timetz", TEST, Test0::typeTimetz)
    val typeTimestamp = column("type_timestamp", TEST, Test0::typeTimestamp)
    val typeTimestamptz = column("type_timestamptz", TEST, Test0::typeTimestamptz)
    val typeInterval = column("type_interval", TEST, Test0::typeInterval,
        {
            it?.let {
                Interval(
                    0,
                    0,
                    it.toDaysPart().toInt(),
                    it.toHoursPart(),
                    it.toMinutesPart(),
                    it.toSecondsPart(),
                    it.toNanosPart() / 1000
                )
            }
        },
        {
            it?.let {
                (it as Interval)
                it.let { value -> (value.days.days + value.hours.hours + value.minutes.minutes + value.seconds.seconds + value.microseconds.microseconds).toJavaDuration() }
            }
        })
    val typeBytea = column("type_bytea", TEST, Test0::typeBytea)
    val typeInet = column("type_inet", TEST, Test0::typeInet, {
        it?.let {
            Inet().setAddress(it)
        }
    }, {
        it?.let {
            it as Inet
            it.address
        }
    })
    val typeVarcharArray = column("type_varchar_array", TEST, Test0::typeVarcharArray)
}

private val log = KotlinLogging.logger { }

class SqlTest() {
    @org.junit.jupiter.api.Test
    fun test() {
        val vertx = Vertx.vertx(
            VertxOptions()
                .setTracingOptions(
                    TracingOptions()
                        .setFactory(
                            LogTracerFactory()
                                .apply {
                                    addResolvers(
                                        QueryRequestTraceTypeResolver(),
                                        RowSetTraceTypeResolver(),
                                        SqlResultImplTraceTypeResolver(),
                                    )
                                }
                        )
                )
        )

        DatabindCodec.mapper()
            .registerModule(KotlinModule.Builder().build())
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val dsl = PgKoqlConfig(
            executor = VertxSqlPoolExecutor(
                PgPool.pool(
                    vertx,
                    PgConnectOptions()
                        .setHost("127.0.0.1")
                        .setPort(5432)
                        .setUser("ai_draw")
                        .setDatabase("ai_draw")
                        .setPassword("HeyDJWiiMuOOOhN")
                        .setTracingPolicy(TracingPolicy.ALWAYS),
                    PoolOptions()
                )
            ))

            .start()
//        dsl.table(TEST)
//            .where(
//                TEST.typeInt2.eq(2).and(
//                    TEST.typeBoolean.eq(true)
//                        .and(TEST.typeFloat4.eq(4f))
//                ).and(TEST.typeInt4.eq(4))
//            )
//            .limit(1)
//            .select()
//            .also {
//                println(it.sql)
//                println(it.params)
//            }
//            .fetchOne()
//            .also {
//                println(it)
//            }

        dsl.table(TEST)
            .insertReturning(
                Test0(
                    typeBoolean = true,
                    typeInt2 = 2,
                    typeInt4 = 4,
                    typeInt8 = 8,
                    typeFloat4 = 4f,
                    typeFloat8 = 8.0,
                    typeChar = "a",
                    typeVarchar = "aaa",
                    typeText = "aaaaa",
                    typeName = "aaa",
                    typeUuid = UUID.randomUUID(),
                    typeDate = LocalDate.now(),
                    typeTime = LocalTime.now(),
                    typeTimetz = OffsetTime.now(),
                    typeTimestamp = LocalDateTime.now(),
                    typeTimestamptz = OffsetDateTime.now(),
                    typeInterval = Duration.ofDays(5),
                    typeBytea = "aaa".toByteArray(),
                    typeInet = InetAddress.getLocalHost(),
                    typeVarcharArray = arrayOf("bbb")
                )
            ).fetchAll()
            .also {
                log.info { it }
            }

        Thread.sleep(5000)
//        dsl.startTx().let {dsl1 ->
//            dsl1.table(TEST)
//                .where(
//                    TEST.typeInt2.eq(2).and(
//                        TEST.typeBoolean.eq(true)
//                            .and(TEST.typeFloat4.eq(4f))
//                    ).and(TEST.typeInt4.eq(4))
//                )
//                .select()
//                .also {
//                    println(it.sql)
//                    println(it.params)
//                }
//                .fetchOne()
//                .also {
//                    println(it)
//                }
//            dsl1.commit()
//        }

//        dsl.startTx().let {
//            it.commit()
//        }

    }
}
