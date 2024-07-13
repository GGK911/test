package PkcsTest;


import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/4 16:58
 */
public class SM2PKCS8ToPEM {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {

        String pkcs8FilePath = "C:\\Users\\ggk911\\Desktop\\1.key";
        String ecFilePath = "C:\\Users\\ggk911\\Desktop\\1-1.key";

        // 读取PKCS#8文件
        String key = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(pkcs8FilePath)));
        key = key.replaceAll("-----\\w+ PRIVATE KEY-----", "").replaceAll("\\s", "");
        byte[] pkcs8EncodedKey = Base64.getDecoder().decode(key);

        // 解析PKCS#8密钥
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedKey);
        PrivateKeyInfo pki = PrivateKeyInfo.getInstance(keySpec.getEncoded());
        byte[] privateKeyBytes = pki.parsePrivateKey().toASN1Primitive().getEncoded();

        // 创建PEM对象
        PemObject pemObject = new PemObject("EC PRIVATE KEY", privateKeyBytes);

        // 写入PEM文件
        try (PemWriter pemWriter = new PemWriter(new FileWriter(ecFilePath))) {
            pemWriter.writeObject(pemObject);
        }

        System.out.println("转换为EC私钥格式成功。");
    }
}
