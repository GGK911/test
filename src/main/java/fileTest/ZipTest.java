package fileTest;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩文件测试
 *
 * @author TangHaoKai
 * @version V1.0 2024/6/26 11:57
 */
public class ZipTest {
    private static final String ROOT = "src/main/java/fileTest";

    @Test
    @SneakyThrows
    public void testSingle() {
        Path path = Paths.get(ROOT, "test.png");
        byte[] fileBytes = FileUtil.readBytes(path.toAbsolutePath().toString());
        Path path2 = Paths.get(ROOT, "1.txt");
        byte[] fileBytes2 = FileUtil.readBytes(path2.toAbsolutePath().toString());

        // 输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // 压缩流
        ZipOutputStream zipStream = new ZipOutputStream(out);
        //压缩条目不是具体独立的文件，而是压缩包文件列表中的列表项，称为条目，就像索引一样
        ZipEntry zipEntry = new ZipEntry("image" + "/ " + " test.png");
        //定位该压缩条目位置，开始写入文件到压缩包中
        zipStream.putNextEntry(zipEntry);
        zipStream.write(fileBytes);
        zipStream.closeEntry();

        ZipEntry zipEntry2 = new ZipEntry("text" + "/" + "1.txt");
        zipStream.putNextEntry(zipEntry2);
        zipStream.write(fileBytes2);
        zipStream.closeEntry();

        zipStream.close();

        byte[] outByteArray = out.toByteArray();
        Path outPath = Paths.get(ROOT, "test.zip");

        FileUtil.writeBytes(outByteArray, outPath.toAbsolutePath().toString());
        System.out.println("====================================压缩完成======================================");

    }
}
