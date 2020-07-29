
import Test.TestTable.columnDouble
import Test.TestTable.columnInt
import Test.TestTable.columnString
import com.koql.core.KOQL
import com.koql.core.config.Configuration
import com.koql.core.dialect.DefautSql

import com.koql.core.statement.structure.Schema
import com.koql.core.statement.structure.Table
import org.junit.Test

class Test {

    object Zt : Schema("zt")

    object TestTable : Table("test_table",  Zt) {


        val columnInt = registerColumn<Int>("column_int")
        val columnString = registerColumn<String>("column_string")
        val columnDouble = registerColumn<Double>("column_double")


    }



//    class ZtServiceConfig : Table("zt_service_config", parent = Zt) {
//        companion object {
//            @JvmStatic
//            val ZtServiceConfig = ZtServiceConfig()
//        }
//        override fun  `as`(alias: String): ZtServiceConfig {
//            return ZtServiceConfig().apply { this.alias = alias }
//        }
//
//        var id = Column<Int>("id", this)
//
//        var orderCatagory = Column<String>("order_catagory", this)
//
//        var serviceAddress = Column<String>("service_address", this)
//
//        var serviceName = Column<String>("service_name", this)
//
//        var interfaceAddress = Column<String>("interface_address", this)
//
//        var interfaceName = Column<String>("interface_name", this)
//
//        var order = Column<Int>("order", this)
//
//        var serviceKey = Column<String>("service_key", this)
//
//        var interfaceUrl = Column<String>("interface_url", this)
//
//        var test = Column<Byte>("test", this)
//
//        var testHttpCode = Column<Int>("test_http_code", this)
//
//        var testResult = Column<String>("test_result", this)
//
//
//        override val column_map = mapOf(
//            0 to id,
//            1 to orderCatagory,
//            2 to serviceAddress,
//            3 to serviceName,
//            4 to interfaceAddress,
//            5 to interfaceName,
//            6 to order,
//            7 to serviceKey,
//            8 to interfaceUrl,
//            9 to test,
//            10 to testHttpCode,
//            11 to testResult
//        )
//    }

    @Test
    fun test() {
        val context = KOQL.dialect(
            DefautSql(
                Configuration(
                    preparedSql = true,
                    fieldSeparationCharacter = "`",
                    valueSeparationCharacter = "'",
                    renderParent = false
                )
            )
        )

        context.select(columnInt , columnString , columnDouble)
            .from(TestTable)
            .getSql()
            .run {
                println(this)
            }


//        val sub = context.select().from(ZtServiceConfig).where(id.eq(1)).asSubSelect("a")

//        context
//            .select(ZtServiceConfig.ZtServiceConfig.orderCatagory.`as`("ooo"))
//            .from(ZtServiceConfig.ZtServiceConfig.`as`("aaa") )
//            .where(
//                ZtServiceConfig.ZtServiceConfig.id.eq(2)
////                    .and(sub.field(0).eq(3))
////                .and(Column<String>("test2").eq(Column<String>("test")))
////                .or(Column<Int>("test3").between(1).and(2))
////                .orNot(Column<Int>("test4").between(Column<Int>("test5")).and(Column<Int>("test6")))
////
//            )
//            .getSql()
//            .run {
//                println(this)
//                println(ZtServiceConfig)
//            }
    }
}