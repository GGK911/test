package pdfTest.ci;

import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pdfTest.PdfParameterEntity;
import pdfTest.PdfUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TangHaoKai
 * @version V1.0 2024/2/5 10:59
 **/
public class removeFieldTest {

    private static final Path file = Paths.get("src/test/resources", "域模板.pdf");
    private static final Path out = Paths.get("target/changeField.pdf");

    @BeforeAll
    public static void getParam() throws IOException {
        List<PdfParameterEntity> param = new ArrayList<>();
        PdfUtil.getPdfDomain(Files.readAllBytes(file), param, null);
        System.out.println(param.stream().map(PdfParameterEntity::getKeyword).collect(Collectors.joining(",")));
    }

    static byte[] bytes;

    /**
     * 删除指定域
     */
    @Test
    @Order(1)
    public void remove() throws IOException {
        List<String> names = new ArrayList<>();
        names.add("Text1");
        bytes = PdfUtil.removeFieldByNames(Files.readAllBytes(file), names);
    }

    /**
     * 删除所有域
     */
    @Test
    @Order(2)
    public void removeAll() throws IOException {
        bytes = PdfUtil.removeAllField(Files.readAllBytes(file));
    }

    @AfterAll
    public static void getParam2() {
        List<PdfParameterEntity> param = new ArrayList<>();
        PdfUtil.getPdfDomain(bytes, param, null);
        if (param.size() == 0) {
            System.out.println("空");
        } else {
            System.out.println(param.stream().map(PdfParameterEntity::getKeyword).collect(Collectors.joining(",")));
        }
        FileUtil.writeBytes(bytes, out.toAbsolutePath().toString());
    }
}
