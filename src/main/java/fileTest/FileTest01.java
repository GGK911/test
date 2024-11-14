package fileTest;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * 解决项目src文件
 * @author TangHaoKai
 * @version 1.0 2024/1/6 15:45
 */
public class FileTest01 {

    @Test
    @SneakyThrows
    public void srcPathTest() {
        final String root = new File("").getCanonicalPath()+"/src/main/java/fileTest/";
        FileUtil.writeString("test", root + "test.txt", "utf-8");
    }
}
