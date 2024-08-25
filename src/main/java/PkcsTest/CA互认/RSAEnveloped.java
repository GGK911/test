package PkcsTest.CA互认;

import cn.com.mcsca.pki.core.common.MCSCAException;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/8 17:37
 */
public class RSAEnveloped {
    private static final Provider BC = new BouncyCastleProvider();

    @Test
    @SneakyThrows
    public void rsa() {
        //RSA 1024
        String rsapri = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKR3vt80eis7GcqT6nn4AN9QeSWGktb/VvAszkf29JpYx0NbNIc6a9TpBCUP1p98Kkbkcv1Eq2wcVLrSbZmOwaaUjW0mcxp+BIwMXhlXaHC9mBlFmA454JkuUDwh5JuGkGyHK284+bmYJJecs5Vb8Xic+a4yDG04A3SSJEap7iNzAgMBAAECgYA5D9rHcluYuC6gnGVT3/ndgPwnSuOTeI/fUIxZZ5NCId8wvWoiKODUw+vOOAqM1vWMFyLWQIcBQWscTnn8Nw10gYLfvD6aXfbiQ4SgZDu2S8bZ2fkwgTdmi1BHlHxRXq56qi0dB20JZ6tewkl1Pi0Q5wpslgfVi7WP5oFCsH2wIQJBANxaqU0iuXpFAI8k3t8Nu1c0dtS9xuLiSUp2U1anVoD1xkCM4IJ7lzdGxqXy1/QhbkZsFpKcAQYCRy6p/4YDPmkCQQC/ErVym8Ke/c2HMFACc8O0SVDfph+AhDm/V123ztiW6Q/3C6P8V0KMOehReRKGelO+V966QKsGINdTkTtvHw97AkA0ASWRsc9KXvyZy97Zj5kWJKii3sMQis03SKO0gLu2pcqLM3RM9zQh9I8vXRfAYx9ueVX+ddj7/Q+loLNQgnV5AkAAv9EIVwYHW5Vvv0fBCrUswtDXX65l8Z7MWkpayyvcQ6O1Y01MUwdGx39aum/RKS+k4nFUJ6bECmLtx/cEs4l7AkBUJ8JYZrlrWlbpX3fCPq8UmESQLox3jAV37ygxwfJqet6uIt5cJAQCUJHGKs//ihmfYvLeCHGs9aPpuvNw3EAW";
        String p10 = "MIIBTTCBtwIBADAQMQ4wDAYDVQQDDAVNQ1NDQTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEApHe+3zR6KzsZypPqefgA31B5JYaS1v9W8CzOR/b0mljHQ1s0hzpr1OkEJQ/Wn3wqRuRy/USrbBxUutJtmY7BppSNbSZzGn4EjAxeGVdocL2YGUWYDjngmS5QPCHkm4aQbIcrbzj5uZgkl5yzlVvxeJz5rjIMbTgDdJIkRqnuI3MCAwEAATANBgkqhkiG9w0BAQsFAAOBgQBHxJG70vBJvqeI/F2SaA39JCsTl+VyCtZyBBVdxVY3cMxwQUT5VRM2n4Deri+G6iGfxfnE5CV1p4hsBgLslRTQoUIBPUdh4R8ZEdena+v2ZNUHtS1fk4vpuOrPfwi8d8HERoXO9RpYGVClQzhaO8OPP4c95PCx27BoR9t/gYEolw==";
        String rsadoubleEncryptedPrivateKey = "hT9hmdvpB8K2fgM51PUV6Ty8AnL4rWBwSn0UqkjcG7dHV/BVl3rk8W81idYSdkOCzoSKnkR9G6YyBfEJ2vAxVMiAH5uDWwTvhdv6hdqqlKhKAD6cS8Bw3CF+uT/WP9DRh2hgPAU08ThvG1cIDzjVVpnfeFHQtEzr4dYJ+AE7FlAgBZV0j7hHWY1iOEEdZhTas4yanlqaiBc/mOlifvG0yg2rCe13X0zAf7wfTrJQ1tmIrjA6lKsK1/DeEBvEYIrBXr0uYBYI4A7qcp1ttQw6oWOWpbaGyFLsID2XK3Ekm8dlK3uqJPPICYzHDDQOgdXjq4Cb/NyKN9VQWbg3Y9XR1UczxxMP1Sx4wnLlzjySZBxe4SgONU2O8OrYEmhZMXgZaU3UetkQmBbr66s9KZZFlp4/MPEeL6ZNuluyrfxJu8Voi2DT7Yn898H15XNYgWjM9GBtF1AjKIMiD+q1ws3t61tUY8UeRzyIvKFz8kZ5eiPLCRQ8Dl1hLYFvM44e6PHQHL1LMAdcMSA+hj+kv2n97dzOrNsAfUNjfEWSbY5KfDdKoUWgMhZ/1MgBI0kEt+UXQtWAgZgYLui0a7yqqWHKimzli+XO3Gefu7ApqyJPMHYY6tbkPtITzyt8tIBmmtQRKQ244B4yWgV2i5D1IxrcuLIAj0vMDQW1BBkJEot2XB/Q6Oj8FuVvtnl/OV8YoaSwDNW1Br++tdBMhDNmaDYv92YG9QUZwnlRzkKT9Sni6daSowz+jYsJrFkXAtmmyTPy4K1AdCtdjJkyMJ/ZLrXk9jDhUnIqPmm/swAvTzM14otkHjGGTOD/SoS9NKAn30RZUa3RvU+28bujow==";
        String doubleEncryptedSessionKey = "NAWOIYxefF5q0GT81AiGZHPMeAP+tIw1MS19yT8XjXTX0WYQD4EjWI+MWNfofJLNb0LHXhFdndMyi+mHIl7O5kU/sniz6bn2c2QYtnYQI2Czwj/cC3plBftziiGmi8M2e9x5fLK/bEkKfdPKMXRy/7T5JY33oC3W0RtATylx55g=";
        String signCert = "MIIEgTCCA2mgAwIBAgIQWSFhOr32s/90GYH+obri3zANBgkqhkiG9w0BAQsFADA9MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAsMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0yMzExMzAwNzAzMzZaFw0yMzEyMTAwNzAzMzZaMGwxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEQMA4GA1UECwwHbG9jYWxSQTEbMBkGA1UEBQwSOTE1MDAxMDhNQTVZUTdQNjY1MR4wHAYDVQQDDBVUcGVyUlNBeDFAVGVzdEAwMUAwMjQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDKeL5nGVEH6bzl7h/iEcqpr8mjjjeoxV3pXDc4gZctqM2NZ39S2rzC1kcmGqNgMQP0WAX53FxNebN7Df1wBM8FwPpYM8IwvZb79498NXFkbPzBks0proP0MvZnC/pwMoZhH+GRjfYkccpuptDM38dIR8Jwc/xhlQtckyBXKhuzp4bBypVqExZY4z+L+oBgJNtg5R2Vb8c69BnlDJY9OaPANGIDKGzNG3Xbh8VSOXo4gi1Z32KrnyIolx/mUfE2ha9/TYyBHVtdnfMuXJOUphM5erb7uYiMN4KBQik8RInbW8KQ8dH1iWpN3WLwZDSiAeenWlVLg4a0nPVUCCBv/SmrAgMBAAGjggFMMIIBSDATBgNVHSUEDDAKBggrBgEFBQcDAjAdBgNVHQ4EFgQUa/61ymccEWhrK5RyeoacnR5TzxsweAYDVR0fBHEwbzBIoEagRKRCMEAxCzAJBgNVBAYTAkNOMQwwCgYDVQQKDANKSVQxEDAOBgNVBAsMB0FERDFDUkwxETAPBgNVBAMMCGNybDExMTcxMCOgIaAfhh1odHRwOi8vMTI3LjAuMC4xL2NybDExMTcxLmNybDALBgNVHQ8EBAMCBsAwXAYIKwYBBQUHAQEEUDBOMCgGCCsGAQUFBzAChhxodHRwOi8vMTI3LjAuMC4xL2NhaXNzdWUuaHRtMCIGCCsGAQUFBzABhhZodHRwOi8vMTI3LjAuMC4xOjIwNDQzMB8GA1UdIwQYMBaAFLlR6QHwqSMfik7pDmsH0AxKLaagMAwGA1UdEwQFMAMBAQAwDQYJKoZIhvcNAQELBQADggEBAMZvgKa56MVyKGdHIZfARBVtA3/DxLpFN1LRK2TgmwQbLzKf8/hftXbK3LbEl1J0QuEgxWVZ218neaDb9Mc+MyuW4fxS0N7ALem/PiLirZ29YrRbGmDM+9MP+tJrnkypwfmXOHK57maC3fCkyhystx6WrVCvAzzJSu//5TxY2D1LxsS4nknc0932Txu1lrM8+bK15qOXLwG+A4Lq1cMrnigXZaTxRdDUq8We1J7+y3llicmXAzHHC8sWodTJcd6Nph9n0Ki7rCjkquRnhytTsob9lKaScLB2XTRyAmw2oMe5KRZLVeFnixmL63g2BoHk8Tp7MB+BzhsyILoMkl1oOXQ=";


        // 先去解我们自己的信封
        byte[] encPriBytes = decodeMscaRSAEnveloped(p10, rsadoubleEncryptedPrivateKey, doubleEncryptedSessionKey, rsapri);
        // 封装别人的信封
        byte[] envelopedRSAEncPri = envelopedRSAEncPri(encPriBytes, Base64.decode(signCert));
        System.out.println("envelopedRSAEncPri>> " + Hex.toHexString(envelopedRSAEncPri));

    }

    /**
     * 解MSCA大陆云盾RSA信封
     *
     * @param pkcs10              请求P10
     * @param encryptedPrivateKey 加密证书私钥（被对称密钥加了密的）
     * @param encryptedSessionKey 对称密钥（被签名公钥加了密的）
     * @param signPri             签名私钥
     * @return 加密证书私钥
     */
    public static byte[] decodeMscaRSAEnveloped(String pkcs10, String encryptedPrivateKey, String encryptedSessionKey, String signPri) throws Exception {
        byte[] key = null;
        //从pkcs10中获取RSA公钥
        KeyFactory keyFact = KeyFactory.getInstance("RSA", BC);
        PKCS10CertificationRequest p10 = new PKCS10CertificationRequest(Base64.decode(pkcs10));
        BCRSAPublicKey rsaPublicKey = (BCRSAPublicKey) keyFact.generatePublic(new X509EncodedKeySpec(p10.getSubjectPublicKeyInfo().getEncoded()));

        //解密
        KeyFactory kf = KeyFactory.getInstance("RSA", BC);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(signPri)));

        // 解密sessionKey
        // 使用 PKCS1Encoding 和 RSAEngine 进行解密
        // RSAKeyParameters privateKeyParam = (RSAKeyParameters) PrivateKeyFactory.createKey(rsaPrivateKey.getEncoded());
        // AsymmetricBlockCipher rsaEngine = new PKCS1Encoding(new RSAEngine());
        // rsaEngine.init(false, privateKeyParam); // false 表示解密模式
        // byte[] decryptData = rsaEngine.processBlock(Base64.decode(encryptedSessionKey), 0, Base64.decode(encryptedSessionKey).length);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", BC);
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decryptData = cipher.doFinal(Base64.decode(encryptedSessionKey));

        // 解密encPri
        Cipher instance = Cipher.getInstance("RC4");
        SecretKeySpec rc4key = new SecretKeySpec(decryptData, "RC4");
        instance.init(Cipher.DECRYPT_MODE, rc4key);
        byte[] deEncPri = instance.doFinal(Base64.decode(encryptedPrivateKey));
        // System.out.println("解密encPri>> " + Hex.toHexString(deEncPri));
        return deEncPri;
    }

    public static byte[] envelopedRSAEncPri(byte[] encPriBytes, byte[] signPubCertBytes) throws Exception {
        //公钥加密算法OID
        ASN1ObjectIdentifier keyEncryptionAlgorithmOID = PKCSObjectIdentifiers.rsaEncryption;
        // contentType
        ASN1ObjectIdentifier pkcs7DataContentType = PKCSObjectIdentifiers.data;
        // envelopedData contentType
        ASN1ObjectIdentifier pkcs7envelopedDataContentType = PKCSObjectIdentifiers.envelopedData;

        //初始向量长度
        int ivLength = 0;
        //密钥长度
        int keyLength = 0;

        ivLength = 8;
        keyLength = 168;
        // 对称加密算法OID
        ASN1ObjectIdentifier contentEncryptionAlgorithmOID = PKCSObjectIdentifiers.des_EDE3_CBC;

        /*初始化密钥和向量*/
        SecureRandom secureRandom = new SecureRandom();
        //密钥
        byte[] symKey = new byte[keyLength];
        KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
        keyGen.init(168); // 3DES密钥长度为168位
        SecretKey secretKey = keyGen.generateKey();
        symKey = secretKey.getEncoded();
        //初始向量
        byte[] iv = new byte[ivLength];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        /* 组装pkcs7 RecipientInfos数据 */
        ASN1EncodableVector recipientInfos = new ASN1EncodableVector();
        CertificateFactory certFact = CertificateFactory.getInstance("X.509", BC);
        X509Certificate certificate = (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(signPubCertBytes));
        // 使用 BouncyCastle 获取 X509CertificateHolder 对象
        X509CertificateHolder certHolder = new JcaX509CertificateHolder(certificate);
        // 提取 Extensions 对象
        Extensions extensions = certHolder.getExtensions();

        PublicKey publicKey = certificate.getPublicKey();
        SubjectKeyIdentifier subjectKeyIdentifier = SubjectKeyIdentifier.fromExtensions(extensions);
        RecipientIdentifier recipientIdentifier = new RecipientIdentifier(new DEROctetString(subjectKeyIdentifier));

        // 公钥加密对称密钥
        Cipher patchCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
        patchCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encBytes = patchCipher.doFinal(symKey);
        ASN1OctetString encryptedKey = new DEROctetString(encBytes);
        AlgorithmIdentifier keyEncryptionAlgorithm = new AlgorithmIdentifier(keyEncryptionAlgorithmOID, DERNull.INSTANCE);
        KeyTransRecipientInfo keyTransRecipientInfo = new KeyTransRecipientInfo(recipientIdentifier, keyEncryptionAlgorithm, encryptedKey);
        RecipientInfo recipientInfo = new RecipientInfo(keyTransRecipientInfo);
        recipientInfos.add(recipientInfo);

        byte[] desEncData = deSedeEncrypt(encPriBytes, secretKey, ivSpec);
        ASN1OctetString encryptedContent = new DEROctetString(desEncData);
        //对称算法标识
        AlgorithmIdentifier contentEncryptionAlgorithm = new AlgorithmIdentifier(contentEncryptionAlgorithmOID, new DEROctetString(iv));
        if (ivLength == 0) {
            contentEncryptionAlgorithm = new AlgorithmIdentifier(contentEncryptionAlgorithmOID, DERNull.INSTANCE);
        }
        //加密消息数据
        EncryptedContentInfo encryptedContentInfo = new EncryptedContentInfo(pkcs7DataContentType, contentEncryptionAlgorithm, encryptedContent);
        //信封数据
        EnvelopedData envelopedData = new EnvelopedData(null, new DERSet(recipientInfos), encryptedContentInfo, ASN1Set.getInstance(null));
        //pkcs7信封结构
        ContentInfo contentInfo = new ContentInfo(pkcs7envelopedDataContentType, envelopedData);
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(contentInfo);
        DERSequence s = new DERSequence(v);

        return DERObjToBytes(s);
    }

    public static byte[] DERObjToBytes(ASN1Encodable obj) throws MCSCAException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ASN1OutputStream dos = ASN1OutputStream.create(bos, "DER");

        byte[] var5;
        try {
            dos.writeObject(obj);
            var5 = bos.toByteArray();
        } catch (Exception var12) {
            throw new MCSCAException("ASN1编码对象解析到字节数组发生异常", var12);
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException var11) {
                    throw new MCSCAException("输出流关闭异常", var11);
                }
            }

        }

        return var5;
    }

    public static byte[] deSedeEncrypt(byte[] plaintext, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(plaintext);
    }

}
