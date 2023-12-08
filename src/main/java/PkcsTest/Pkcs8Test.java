package PkcsTest;

import certTest.PemFormatUtil;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEInputDecryptorProviderBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.spec.RSAPrivateCrtKeySpec;

/**
 * PKCS8
 * 存储加密私钥标准
 *
 * @author TangHaoKai
 * @version V1.0 2023-12-04 11:15
 **/
public class Pkcs8Test {
    private static final Provider BC = new BouncyCastleProvider();
    private static final char[] PASSWD = {'1', '2', '3', '4', '5', '6'};

    /**
     * RSA参数
     */
    private static final RSAPrivateCrtKeySpec privKeySpec = new RSAPrivateCrtKeySpec(
            new BigInteger("b4a7e46170574f16a97082b22be58b6a2a629798419be12872a4bdba626cfae9900f76abfb12139dce5de56564fab2b6543165a040c606887420e33d91ed7ed7", 16),
            new BigInteger("11", 16),
            new BigInteger("9f66f6b05410cd503b2709e88115d55daced94d1a34d4e32bf824d0dde6028ae79c5f07b580f5dce240d7111f7ddb130a7945cd7d957d1920994da389f490c89", 16),
            new BigInteger("c0a0758cdf14256f78d4708c86becdead1b50ad4ad6c5c703e2168fbf37884cb", 16),
            new BigInteger("f01734d7960ea60070f1b06f2bb81bfac48ff192ae18451d5e56c734a5aab8a5", 16),
            new BigInteger("b54bb9edff22051d9ee60f9351a48591b6500a319429c069a3e335a1d6171391", 16),
            new BigInteger("d3d83daf2a0cecd3367ae6f8ae1aeb82e9ac2f816c6fc483533d8297dd7884cd", 16),
            new BigInteger("b8f52fc6f38593dabb661d3f50f8897f8106eee68b1bce78a95b132b4e5b5d19", 16));

    @SneakyThrows
    public static void main(String[] args) {
        String str = "3169301806092a864886f70d010903310b06092a864886f70d010701301c06092a864886f70d010905310f170d3233313230353036323630335a302f06092a864886f70d01090431220420f0bf80f6397be194fb94e0e6dca28f5334686ba41376a1fa326672b18646bd72";
        System.out.println(Base64.toBase64String(Hex.decode(str)));

        // KeyFactory fact = KeyFactory.getInstance("RSA", BC);
        // PrivateKey privKey = fact.generatePrivate(privKeySpec);
        //
        // PKCS8EncryptedPrivateKeyInfoBuilder builder = new JcaPKCS8EncryptedPrivateKeyInfoBuilder(privKey);
        // // 给私钥加密
        // PKCS8EncryptedPrivateKeyInfo priv = builder.build(new BcPKCS12PBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, CBCBlockCipher.newInstance(new DESedeEngine())).build(PASSWD));
        // String encry_private_key = PemFormatUtil.pemFormat("PRIVATE KEY", priv.getEncoded());
        // FileUtil.writeString(encry_private_key, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\PkcsTest\\encry_private_key.key", "UTF-8");
        //
        // // 解密
        // PrivateKeyInfo info = priv.decryptPrivateKeyInfo(new BcPKCS12PBEInputDecryptorProviderBuilder().build(PASSWD));
        // String private_key = PemFormatUtil.pemFormat("PRIVATE KEY", info.getEncoded());
        // FileUtil.writeString(private_key, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\PkcsTest\\private_key.key", "UTF-8");
    }
}
