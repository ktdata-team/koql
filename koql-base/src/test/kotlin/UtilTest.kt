import com.koql.base.Table
import java.math.BigDecimal
import java.net.InetAddress
import java.time.*
import java.util.*

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
    val typeNumeric = column("type_numeric", TEST, Test0::typeNumeric,)
    val typeUuid = column("type_uuid", TEST, Test0::typeUuid)
    val typeDate = column("type_date", TEST, Test0::typeDate)
    val typeTime = column("type_time", TEST, Test0::typeTime)
    val typeTimetz = column("type_timetz", TEST, Test0::typeTimetz)
    val typeTimestamp = column("type_timestamp", TEST, Test0::typeTimestamp)
    val typeTimestamptz = column("type_timestamptz", TEST, Test0::typeTimestamptz)
    val typeInterval = column("type_interval", TEST, Test0::typeInterval,)
    val typeBytea = column("type_bytea", TEST, Test0::typeBytea)
    val typeInet = column("type_inet", TEST, Test0::typeInet, )
    val typeVarcharArray = column("type_varchar_array", TEST, Test0::typeVarcharArray)
}


class UtilTest {

    @org.junit.jupiter.api.Test
    fun test() {

        val entity = Test0()

        val map = TEST.columnMap.mapValues {
            val a = it.value.get(entity)
            it.value.get(entity)
        }
        println(map)
    }

}
