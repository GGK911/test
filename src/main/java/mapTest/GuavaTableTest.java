package mapTest;

import com.google.common.collect.ForwardingTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/5/21 14:41
 */
public class GuavaTableTest {

    @Test
    @SneakyThrows
    public void table() {
        Table<String, Boolean, Integer> table = HashBasedTable.create();
        table.put("abc1", true, 1);
        table.put("abc2", false, 2);
        table.put("abc3", true, 3);
        Map<Boolean, Map<String, Integer>> booleanMapMap = table.columnMap();
        Map<String, Integer> stringIntegerMapT = booleanMapMap.get(true);
        Map<String, Integer> stringIntegerMapF = booleanMapMap.get(false);


        System.out.println("==========================================");

    }
}
