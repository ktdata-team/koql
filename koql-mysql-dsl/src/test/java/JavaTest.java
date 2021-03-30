import com.koql.core.dsl.mysql.Mysql;
import com.koql.core.dsl.mysql.dql.From;
import com.koql.core.dsl.mysql.structure.Query;
import model.TestTable;
import org.junit.Test;
import static model.TestTable.*;
import static model.TestSchema.*;

public class JavaTest {

    private final Mysql mysql = Mysql.create();

    @Test
    public void simpleSelectTest() {
        Query sql = mysql.select(id, firstName, lastName)
                .from(testTable)
                ;
        System.out.println(sql.render(mysql.getConfig()));
        System.out.println(sql.parameters());

    }
}
