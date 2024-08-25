package ofdTest;

import cn.hutool.json.JSONUtil;
import org.dom4j.DocumentException;
import org.ofdrw.reader.OFDReader;
import org.ofdrw.reader.keyword.KeywordExtractor;
import org.ofdrw.reader.keyword.KeywordPosition;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/5 10:43
 */
public class OfdKeyWordFindTest {
    private static final Path ofdFile = Paths.get("src/main/java/ofdTest", "HelloWorld.ofd");

    public static void main(String[] args) throws IOException, DocumentException {
        byte[] ofdBytes = Files.readAllBytes(ofdFile);
        OFDReader reader = new OFDReader(new ByteArrayInputStream(ofdBytes));

        List<KeywordPosition> keyWordPositionList = KeywordExtractor.getKeyWordPositionList(reader, "OFD");
        System.out.println(keyWordPositionList.size());
        for (KeywordPosition keywordPosition : keyWordPositionList) {
            System.out.println(JSONUtil.toJsonStr(keywordPosition));
        }
    }
}
