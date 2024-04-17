package certTest.createCert;

import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

/**
 * 格式化输出证书文件
 */
public class PemUtil {

    /**
     * 格式化输出证书文件到路径
     *
     * @param certificate 对象
     * @param fileName    路径
     * @throws Exception 输出异常
     */
    public static void objectToFile(Object certificate, String fileName) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(fileName);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {

            // 创建 JcaPEMWriter 对象
            JcaPEMWriter pemWriter = new JcaPEMWriter(osw);

            // 写入证书对象
            pemWriter.writeObject(certificate);

            // 关闭 PEMWriter
            pemWriter.close();
        }
    }

    /**
     * 读取带Header头的文件
     *
     * @param fileName 文件路径
     * @return 对象
     * @throws Exception 输出异常
     */
    public static Object objectFromFile(String fileName) throws Exception {
        try (FileReader fr = new FileReader(fileName);
             PEMParser pemParser = new PEMParser(fr)) {
            // 从 PEM 文件中读取对象
            return pemParser.readObject();
        }
    }
}
