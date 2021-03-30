import com.koql.core.dsl.mysql.Mysql.Companion.create
import com.koql.core.dsl.mysql.structure.Query
import model.TestSchema.testTable
import model.TestTable.firstName
import model.TestTable.id
import model.TestTable.lastName
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals

class KotlinTest {
    private val mysql = create()

    @Test
    fun simpleSelectTest() {
        val sql: Query = mysql.select(id, firstName, lastName).from(testTable)
        println(sql.render(mysql.getConfig()))
        println(sql.parameters())

    }
    @Test
    fun optionSelectTest() {
        val sql: Query = mysql.select()
            .distinct()
            .highPriority()
            .sqlNoCache(id, firstName, lastName)
        println(sql.render(mysql.getConfig()))
        println(sql.parameters())
    }

    @Test
    fun partitionSelectTest() {
        val sql = mysql.select(id, firstName, lastName)
            .from(testTable.partition("a0" , "a1"))
        println(sql.render(mysql.getConfig()))
        println(sql.parameters())
    }
}