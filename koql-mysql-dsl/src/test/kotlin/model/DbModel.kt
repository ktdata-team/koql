package model


import com.koql.core.dsl.mysql.structure.MysqlTable
import com.koql.core.statement.structure.Schema

object TestSchema : Schema("test_schema") {
    @JvmField
    val testTable = TestTable
}

object TestTable : MysqlTable("test_table") {
    override val schema = TestSchema

    @JvmField
    val id = register<Int>("id")
    @JvmField
    val firstName = register<String>("first_name")
    @JvmField
    val lastName = register<String>("last_name")
}