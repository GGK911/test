package cipherTest;

import cn.com.mcsca.pki.core.bouncycastle.jce.provider.BouncyCastleProvider;
import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Hex;
import org.bouncycastle.crypto.digests.SM3Digest;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

/**
 * SM3测试
 *
 * @author TangHaoKai
 * @version V1.0 2024/5/6 9:58
 */
public class SM3Example {

    public static void main(String[] args) throws Exception {
        // 1.文件
        Path path = Paths.get("C:\\Users\\ggk911\\Desktop\\2.png");
        // byte[] bytes = Files.readAllBytes(path);
        // 2.信息
        byte[] bytes = Hex.decode("616263");
        bytes = "1234567812345678".getBytes(StandardCharsets.UTF_8);

        // 创建 SM3 哈希函数实例
        MessageDigest sm3Digest = MessageDigest.getInstance("SM3", new BouncyCastleProvider());
        // 对输入数据进行哈希运算
        byte[] hash = sm3Digest.digest(bytes);
        // 打印
        System.out.println(Hex.toHexString(hash));

    }

}
