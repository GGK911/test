package ofdTest;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.ofdrw.layout.OFDDoc;
import org.ofdrw.layout.edit.Attachment;
import org.ofdrw.reader.OFDReader;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 添加附件测试
 * 结果：签署完后再添加附件不影响签名有效性
 *
 * @author TangHaoKai
 * @version V1.0 2024/11/11 17:15
 */
public class OfdCreateTest {
    @Test
    @SneakyThrows
    public void Test() {
        String file = "C:\\Users\\ggk911\\IdeaProjects\\pki-base\\target\\test02.ofd";
        Path outpath = Paths.get("C:\\Users\\ggk911\\IdeaProjects\\pki-base\\target", "test03.ofd");
        Path attachment = Paths.get("D:\\测试图片\\THK.png");
        byte[] bytes = FileUtil.readBytes(file);
        OFDReader reader = new OFDReader(new ByteArrayInputStream(bytes));
        OFDDoc ofdDoc = new OFDDoc(reader, outpath);

        Attachment pic = new Attachment("唐好凯的印章图片", attachment);
        ofdDoc.addAttachment(pic);

        ofdDoc.close();
        reader.close();
    }
}
