import com.koql.core.KOQL
import com.koql.core.config.Configuration
import com.koql.core.dsl.base.DefautSql

import com.koql.core.statement.structure.Schema
import com.koql.core.statement.structure.Table
import com.koql.core.statement.structure.ValueField
import org.junit.Test

class Test {

    @Test
    fun test() {

        val koql = KOQL.create(DefautSql(), Configuration(renderParent = true))


        koql
            .select(TestTable.id, TestTable.firstName, TestTable.lastName)
            .from(TestTable)
            .where(
                ValueField(1).eq(1)
                    .and(
                        TestTable.id.eq(1)
                            .and(TestTable.firstName.ge(TestTable.lastName))
                    )
            )
            .also {
                println(it.render(koql.configuration))
                println(it.parameters())
            }


    }


}

object TestSchema : Schema("test_schema") {
    val testTable = TestTable
}

object TestTable : Table("test_table") {
    override val schema = TestSchema

    val id = register<Int>("id")
    val firstName = register<String>("first_name")
    val lastName = register<String>("last_name")
}