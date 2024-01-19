package PkcsTest;

import certTest.PemFormatUtil;
import cn.com.mcsca.pki.core.sm2.SM2KeyFactory;
import cn.com.mcsca.pki.core.util.KeyUtil;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.digests.SM3Digest;
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
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Arrays;
import java.util.stream.Collectors;

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
    private static final PublicKey publicKey;
    private static final PrivateKey privateKey;
    private static final KeyPair keyPair;

    static {
        //加载BC
        Security.addProvider(BC);
        // 获取SM2椭圆曲线的参数
        ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");
        // 获取一个椭圆曲线类型的密钥对生成器
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("EC", BC);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 使用SM2参数初始化生成器
        try {
            assert kpg != null;
            kpg.initialize(sm2Spec);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        // 获取密钥对
        keyPair = kpg.generateKeyPair();
        publicKey = keyPair.getPublic();
        System.out.println(publicKey.toString());
        privateKey = keyPair.getPrivate();
        System.out.println(privateKey.toString());

    }

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

        KeyFactory fact = KeyFactory.getInstance("RSA", BC);
        PrivateKey privKey = fact.generatePrivate(privKeySpec);

        PKCS8EncryptedPrivateKeyInfoBuilder builder = new JcaPKCS8EncryptedPrivateKeyInfoBuilder(privKey);
        // 给私钥加密
        PKCS8EncryptedPrivateKeyInfo priv = builder.build(new BcPKCS12PBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, CBCBlockCipher.newInstance(new DESedeEngine())).build(PASSWD));
        String encry_private_key = PemFormatUtil.pemFormat("PRIVATE KEY", priv.getEncoded());
        System.out.println("加密 pkcs8 Base64:" + Base64.toBase64String(priv.getEncoded()));
        FileUtil.writeString(encry_private_key, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\PkcsTest\\encry_private_key.key", "UTF-8");

        // 解密
        PrivateKeyInfo info = priv.decryptPrivateKeyInfo(new BcPKCS12PBEInputDecryptorProviderBuilder().build(PASSWD));
        String private_key = PemFormatUtil.pemFormat("PRIVATE KEY", info.getEncoded());
        System.out.println("解密 pkcs8 Base64:" + Base64.toBase64String(info.getEncoded()));
        FileUtil.writeString(private_key, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\PkcsTest\\private_key.key", "UTF-8");
    }

    @Test
    @SneakyThrows
    public void sm2Pkcs8Test() {
        JcaPKCS8EncryptedPrivateKeyInfoBuilder builder = new JcaPKCS8EncryptedPrivateKeyInfoBuilder(privateKey);
        // 私钥加密
        PKCS8EncryptedPrivateKeyInfo priv = builder.build(new BcPKCS12PBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, CBCBlockCipher.newInstance(new DESedeEngine())).build(PASSWD));
        // 解密
        PrivateKeyInfo info = priv.decryptPrivateKeyInfo(new BcPKCS12PBEInputDecryptorProviderBuilder().build(PASSWD));
        System.out.println("解密 pkcs8 Base64:" + Base64.toBase64String(info.getEncoded()));
    }

    @Test
    @SneakyThrows
    public void sm2Pkcs8Test02() {
        System.out.println(Hex.toHexString(privateKey.getEncoded()));
        PrivateKey privateKeyGen = SM2KeyFactory.convertPKCS8ToPrivateKey(Base64.toBase64String(privateKey.getEncoded()));
        System.out.println(Hex.toHexString(privateKeyGen.getEncoded()));

        String privateKey = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIE3kknNhUwexGBNGCz5QoSnH5PVk2fGCvRhs4XSIRhT/oIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        PrivateKey mcscaPri = SM2KeyFactory.convertPKCS8ToPrivateKey(privateKey);
        System.out.println(Hex.toHexString(mcscaPri.getEncoded()));
    }

    @Test
    @SneakyThrows
    public void sm2Pkcs8Test03() {
        KeyPair sm2 = KeyUtil.generateKeyPair("SM2", 0);
        PrivateKey aPrivate = sm2.getPrivate();
        System.out.println(Hex.toHexString(aPrivate.getEncoded()));
        System.out.println(Arrays.stream(Security.getProviders()).map(Provider::getName).collect(Collectors.joining(",")));
    }


}
