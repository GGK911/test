package PkcsTest.CAShare;

import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * CA互认规范得RSA数字信封结构
 *
 * @author TangHaoKai
 * @version V1.0 2024/8/15 17:14
 */
public class RSAEnveloped2 {
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
        byte[] encPriBytes = RSAEnveloped.decodeMscaRSAEnveloped(p10, rsadoubleEncryptedPrivateKey, doubleEncryptedSessionKey, rsapri);
        String pkcs8Pri = Hex.toHexString(encPriBytes);
        System.out.println(String.format("%-16s", "pkcs8Pri>> ") + pkcs8Pri);

        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(encPriBytes);
        ByteArrayInputStream bIn = new ByteArrayInputStream(priPKCS8.getEncoded());
        ASN1InputStream dIn = new ASN1InputStream(bIn);
        ASN1Sequence seq = (ASN1Sequence) dIn.readObject();
        DEROctetString dos = (DEROctetString) seq.getObjectAt(2);
        // 私钥数据 去掉pkcs8结构
        byte[] rsaPri = dos.getOctets();
        String rsaPriHex = Hex.toHexString(rsaPri);
        System.out.println(String.format("%-16s", "rsaPriHex>> ") + rsaPriHex);


        // 对称加密
        // 创建 KeyGenerator 对象，用于生成对称密钥
        String alg = "desede";
        KeyGenerator keyGenerator;
        if (alg.equalsIgnoreCase("SM4")) {
            keyGenerator = KeyGenerator.getInstance("SM4", BC);
        } else if (alg.equalsIgnoreCase("desede")) {
            keyGenerator = KeyGenerator.getInstance("DESede");
        } else if (alg.equalsIgnoreCase("aes")) {
            keyGenerator = KeyGenerator.getInstance("aes");
        } else {
            keyGenerator = KeyGenerator.getInstance("SM4", BC);
        }
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] iv = new byte[]{};
        iv = null;

        byte[] symmetricEncryption = symmetricEncryption(rsaPri, secretKey.getEncoded(), false, iv, alg);
        String symHex = Hex.toHexString(symmetricEncryption);
        System.out.println(String.format("%-16s", "symHex>> ") + symHex);

        CertificateFactory certFact = CertificateFactory.getInstance("X.509", BC);
        X509Certificate certificate = (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(Base64.decode(signCert)));
        PublicKey publicKey = certificate.getPublicKey();
        BCRSAPublicKey rsaPublicKey = (BCRSAPublicKey) publicKey;
        BigInteger publicExponent = rsaPublicKey.getPublicExponent();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA", BC);
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(rsaPri));
        BCRSAPrivateKey bcrsaPrivateKey = (BCRSAPrivateKey) privateKey;
        BigInteger priMoudlus = bcrsaPrivateKey.getModulus();

        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", BC); //RSA_PKCS1_PADDING
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] rsaEncBytes = cipher.doFinal(secretKey.getEncoded());
        String rsaEncHex = Hex.toHexString(rsaEncBytes);
        System.out.println(String.format("%-16s", "rsaEncHex>> ") + rsaEncHex);

        // 密钥数据
        ASN1EncodableVector asnPubkey = new ASN1EncodableVector();
        asnPubkey.add(new ASN1Integer(priMoudlus));
        asnPubkey.add(new ASN1Integer(publicExponent));
        DERSequence derPubkey = new DERSequence(asnPubkey);

        //第三部分数据 包含 RSA oid和公钥
        ASN1EncodableVector asnEncRsaPub = new ASN1EncodableVector();
        ASN1ObjectIdentifier rsaAlgOID = new ASN1ObjectIdentifier("1.2.840.113549.1.1.1");
        AlgorithmIdentifier algID = new AlgorithmIdentifier(rsaAlgOID, DERNull.INSTANCE);
        asnEncRsaPub.add(algID);
        asnEncRsaPub.add(new DERBitString(derPubkey.getEncoded()));
        DERSequence derEncRsaPub = new DERSequence(asnEncRsaPub);

        //第一部分数据包含 3desc oid
        ASN1ObjectIdentifier desAlgOID = new ASN1ObjectIdentifier("1.2.840.113549.3.7");
        AlgorithmIdentifier desalgID;
        if (iv != null) {
            desalgID = new AlgorithmIdentifier(desAlgOID, new DEROctetString(iv));
        } else {
            desalgID = new AlgorithmIdentifier(desAlgOID, DERNull.INSTANCE);
        }

        ASN1EncodableVector asnEncRsa = new ASN1EncodableVector();
        asnEncRsa.add(desalgID);
        asnEncRsa.add(new DERBitString(rsaEncBytes));
        asnEncRsa.add(ASN1Sequence.fromByteArray(derEncRsaPub.getEncoded()));
        asnEncRsa.add(new DERBitString(symmetricEncryption));
        DERSequence derseq = new DERSequence(asnEncRsa);

        String envelopedHex = Hex.toHexString(derseq.getEncoded());
        System.out.println(String.format("%-16s", "envelopedHex>> ") + envelopedHex);

    }

    /**
     * 对数据进行对称加密
     *
     * @param inData    数据
     * @param pwd       对称密钥
     * @param isPadding 是否填充 PKCS5Padding/NoPadding
     * @param iv        向量
     * @param alg       对称算法 3DES AES SM4
     * @return 加密值
     */
    public static byte[] symmetricEncryption(byte[] inData, byte[] pwd, boolean isPadding, byte[] iv, String alg) throws Exception {
        byte[] outData = null;

        byte[] piv;
        if (iv != null) {
            piv = new byte[iv.length];
            System.arraycopy(iv, 0, piv, 0, iv.length);
        }
        IvParameterSpec ips = null;
        try {
            SecretKeySpec skeySpec = null;
            Cipher cipher = null;
            if (alg == null || alg.isEmpty()) {
                skeySpec = new SecretKeySpec(pwd, "SM4");
                if (isPadding) {
                    cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", BC); // NoPadding
                } else {
                    cipher = Cipher.getInstance("SM4/ECB/NoPadding", BC); // NoPadding
                }
            } else if (alg.equalsIgnoreCase("SM4")) {
                skeySpec = new SecretKeySpec(pwd, "SM4");
                if (isPadding) {
                    cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", BC); // NoPadding
                } else {
                    cipher = Cipher.getInstance("SM4/ECB/NoPadding", BC); // NoPadding
                }
            } else if (alg.equalsIgnoreCase("desede")) {
                skeySpec = new SecretKeySpec(pwd, "DESEDE");
                if (isPadding) {
                    if (iv != null) {
                        ips = new IvParameterSpec(iv);
                        cipher = Cipher.getInstance("DESEDE/CBC/PKCS5Padding", BC); // NoPadding
                    } else {
                        cipher = Cipher.getInstance("DESEDE/ECB/PKCS5Padding", BC); // NoPadding
                    }
                } else {
                    if (iv != null) {
                        ips = new IvParameterSpec(iv);
                        cipher = Cipher.getInstance("DESEDE/CBC/NoPadding", BC); // NoPadding
                    } else {
                        cipher = Cipher.getInstance("DESEDE/ECB/NoPadding", BC); // NoPadding
                    }

                }
            } else if (alg.equalsIgnoreCase("aes")) {
                AESEngine aes = new AESEngine();
                BufferedBlockCipher cipherBC = new PaddedBufferedBlockCipher(aes, new PKCS7Padding());
                KeyParameter keyParam = new KeyParameter(pwd);
                CipherParameters params = (CipherParameters) new ParametersWithRandom(keyParam);
                cipherBC.reset();
                cipherBC.init(true, params);
                byte[] buf = new byte[cipherBC.getOutputSize(inData.length + 32)];
                int len = cipherBC.processBytes(inData, 0, inData.length, buf, 0);
                len += cipherBC.doFinal(buf, len);
                byte[] out = new byte[len];
                System.arraycopy(buf, 0, out, 0, len);
                outData = out;
                return outData;
            }
            SecureRandom sr = new SecureRandom();
            if (cipher != null) {
                if (iv != null) {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ips, sr);
                } else {
                    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, sr);
                }
                outData = cipher.doFinal(inData);
            }
        } catch (Exception e) {
            throw e;
        }
        return outData;
    }


}
