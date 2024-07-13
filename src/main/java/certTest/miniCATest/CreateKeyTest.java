package certTest.miniCATest;

import certTest.createCert.PemUtil;
import lombok.SneakyThrows;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/13 15:34
 */
public class CreateKeyTest extends MiniCATest{

    @Test
    @SneakyThrows
    public void createRSAKey() {
        rsaKeyPairGenerator.initialize(2048);
        KeyPair keyPair = rsaKeyPairGenerator.generateKeyPair();

        PublicKey aPublic = keyPair.getPublic();
        PrivateKey aPrivate = keyPair.getPrivate();

        System.out.println("privateKeyHex>> " + Hex.toHexString(aPrivate.getEncoded()));
        System.out.println("privateKeyBase64>> " + Base64.toBase64String(aPrivate.getEncoded()));
        System.out.println("pubKeyHex>> " + Hex.toHexString(aPublic.getEncoded()));
        System.out.println("pubKeyBase64>> " + Base64.toBase64String(aPublic.getEncoded()));

        Path pubKeyPath = Paths.get(ROOT, "pubKey.key");
        Path priKeyPath = Paths.get(ROOT, "priKey.key");
        PemUtil.objectToFile(aPublic, pubKeyPath.toAbsolutePath().toString());
        PemUtil.objectToFile(aPrivate, priKeyPath.toAbsolutePath().toString());
        // openssl
        // 解析私钥
        // openssl rsa -in .\priKey.key -text -noout
        // openssl pkcs8 -in .\priKey.key -inform PEM -nocrypt -outform PEM | openssl rsa -text -noout
        // 解析公钥
        // openssl rsa -pubin -in .\pubKey.key -text -noout
        // openssl rsa -pubin -inform DER -in .\pubKey.key -text -noout
    }

    @Test
    @SneakyThrows
    public void createSM2Key() {
        // 获取SM2曲线参数
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        // 默认32字节，256bit位
        sm2KeyPairGenerator.initialize(ecSpec);
        KeyPair keyPair = sm2KeyPairGenerator.generateKeyPair();
        PublicKey aPublic = keyPair.getPublic();
        PrivateKey aPrivate = keyPair.getPrivate();

        System.out.println("sm2PrivateKeyHex>> " + Hex.toHexString(aPrivate.getEncoded()));
        System.out.println("sm2PrivateKeyBase64>> " + Base64.toBase64String(aPrivate.getEncoded()));
        System.out.println("sm2PubKeyHex>> " + Hex.toHexString(aPublic.getEncoded()));
        System.out.println("sm2PubKeyBase64>> " + Base64.toBase64String(aPublic.getEncoded()));

        Path pubKeyPath = Paths.get(ROOT, "sm2PubKey.key");
        Path priKeyPath = Paths.get(ROOT, "sm2PriKey.key");
        PemUtil.objectToFile(aPublic, pubKeyPath.toAbsolutePath().toString());
        PemUtil.objectToFile(aPrivate, priKeyPath.toAbsolutePath().toString());
        // openssl
        // 生成私钥
        //  openssl ecparam -genkey -name sm2 -noout -out .\miniCATest\openssl_sm2PriKey.key
        // 解析私钥
        // openssl ec -in .\miniCATest\openssl_sm2PriKey.key -text -noout
        // 解析公钥
        // openssl ec -in .\miniCATest\openssl_sm2PubKey.key -pubin -text -noout
        // 从私钥中提取公钥
        // openssl ec -in .\miniCATest\openssl_sm2PriKey.key -pubout -out .\miniCATest\openssl_sm2PubKey.key
    }
}
