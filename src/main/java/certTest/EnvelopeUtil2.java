package certTest;

import cn.com.mcsca.pki.core.bouncycastle.asn1.ASN1Integer;
import cn.com.mcsca.pki.core.bouncycastle.asn1.ASN1ObjectIdentifier;
import cn.com.mcsca.pki.core.bouncycastle.asn1.ASN1OctetString;
import cn.com.mcsca.pki.core.bouncycastle.asn1.ASN1Sequence;
import cn.com.mcsca.pki.core.bouncycastle.asn1.ASN1Set;
import cn.com.mcsca.pki.core.bouncycastle.asn1.ASN1TaggedObject;
import cn.com.mcsca.pki.core.bouncycastle.asn1.DEROctetString;
import cn.com.mcsca.pki.core.bouncycastle.crypto.InvalidCipherTextException;
import cn.com.mcsca.pki.core.bouncycastle.crypto.engines.SM2Engine;
import cn.com.mcsca.pki.core.bouncycastle.crypto.engines.SM4Engine;
import cn.com.mcsca.pki.core.bouncycastle.crypto.params.ECPrivateKeyParameters;
import cn.com.mcsca.pki.core.bouncycastle.crypto.params.KeyParameter;
import cn.com.mcsca.pki.core.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import cn.com.mcsca.pki.core.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import cn.com.mcsca.pki.core.bouncycastle.jce.provider.BouncyCastleProvider;
import cn.com.mcsca.pki.core.bouncycastle.util.Arrays;
import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Base64;
import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/14 15:00
 */
public class EnvelopeUtil2 {
    public static void main(String[] args) throws Exception {
        // String d = openTheEnvelope("MIIEQgYKKoEcz1UGAQQCBKCCBDIwggQuAgEBMYHTMIHQAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQZVqJYUlGpXieHix1naIv5DALBgkqgRzPVQGCLQMEezB5AiAgK9wa49cBIvO58/qC1nS1Z9/cB6Awh98vZiTtgloEowIhAOrtGTj6iFqlfJoDuKVI2zj0EHVJlDak6UNIPKE5NB4wBCDmDE2XGH9MEaMmczijdbEZZKEvx16MLOyTW3DJr8BNOgQQ3iecE4GecLE+OD32WlKpKjEMMAoGCCqBHM9VAYMRMFkGCiqBHM9VBgEEAgEwCQYHKoEcz1UBaIBA1dWxOCmjmEv+swk5PzjiQdXVsTgpo5hL/rMJOT844kGPwwytJnvKxXH1saRj89Chvg/ahKwKuj0v4UIUd/JetaCCAe8wggHrMIIBj6ADAgECAhBmGlO15VIwOaiyxJ6pnaeUMAwGCCqBHM9VAYN1BQAweDELMAkGA1UEBhMCQ04xEjAQBgNVBAgMCUNob25ncWluZzE6MDgGA1UECgwxRWFzdC1aaG9uZ3h1biBDZXJ0aWZpY2F0ZSBBdXRob3JpdHkgQ2VudGVyIENPLkxURDEZMBcGA1UEAwwQRWFzdC1aaG9uZ3h1biBDQTAeFw0yMzA4MTcwMTI4MzNaFw0zMzA4MTQwMTI4MzNaMC8xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEQMA4GA1UEAwwHU00yU0lHTjBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABCA6bLiYQEN5maqDtr6rfmqg5/v+GhZHlIDdoQ/SWQ+Hc3SedRXcMas5oWfBUAZ+Nko2nc1+1LMWsRJi4DgDr36jQjBAMB8GA1UdIwQYMBaAFAnuuwf+H95XmJJu7j/LUmpL+SYdMB0GA1UdDgQWBBSJqOoC7mJ441HP32hg8CmGbLpezDAMBggqgRzPVQGDdQUAA0gAMEUCIBnnZHGxXcc7Ar6Wzd3NJ+8Da5TwK8Jn8J7S8p4xsPmwAiEAmW7lerdM1A6+7W4BEfLh69J/fC+d+Hzzx2h/ohWLKQkxgfYwgfMCAQEwgYwweDELMAkGA1UEBhMCQ04xEjAQBgNVBAgMCUNob25ncWluZzE6MDgGA1UECgwxRWFzdC1aaG9uZ3h1biBDZXJ0aWZpY2F0ZSBBdXRob3JpdHkgQ2VudGVyIENPLkxURDEZMBcGA1UEAwwQRWFzdC1aaG9uZ3h1biBDQQIQZhpTteVSMDmossSeqZ2nlDAKBggqgRzPVQGDETALBgkqgRzPVQGCLQEERjBEAiA4XRuZ24bg5alWcsNJ1GERgLUoo664xGWEeccVq6E14wIgbND2lEEoCuHflcPtKsDJ1v1fgaE3Y4lIkLpg26/J64A=", "308193020100301306072a8648ce3d020106082a811ccf5501822d047930770201010420d873c87879974f477e293da9487907b04046c485820d21e0f0e3c0dd305daa4ca00a06082a811ccf5501822da14403420004e86cf685cc7e32a061435b295dd4b6697a54a965456c9d45df9f314fc594d2df7eb3d1d8dc9e6dbc3403cf4ce22c4a7184f1c8d790084e7c9714185c366651ba");
        // System.out.println(String.format("%-16s", "d>> ") + d);

        String cipher = "MIH9BgoqgRzPVQYBBAIDoIHuMIHrAgECMYGnMIGkAgECgBQyMvOOpzGo/qOrNpcmAS5Xms5wLTANBgkqgRzPVQGCLQMFAAR6MHgCIGCk/XRAQWREzUfKY9cCZaPrsovhSXU4+VfXqA3N6PGBAiAD4dkcVx+YVjVvAmgC2GxeDX7UuTJgNhWKhwD5g4lBawQgdvIZKmy7UuldupfD5qBBzhMK2duTwR00P19561mS7xUEEDm9F1f+1AxpV1qKaMv2u1wwPAYKKoEcz1UGAQQCATAcBggqgRzPVQFoAgQQMPdqjH2h6wecMRk+JvRyMoAQ9GM/xRa55D7JL3AD7PDmqw==";

        String certBase64 = "MIICvDCCAmGgAwIBAgIQX/CynFAMUO9dMkp04m65KTAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwOTA1MDgzNzU4WhcNMjQwOTE3MDgzNzU4WjB+MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjkxMzcxNzIxTUEzREs4TTA1RDEwMC4GA1UEAwwnMTc2NTIyNTIyMzAwNDcwMDY3MkDmtYvor5Xop6Plr4ZAMDFAMDA1MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEzMWdsNMbBzgkuuzD0Ilq0XeryO1IBDyjMPEBTehn+0A1p7HNAfUS9EqzRW+QHl1yuK9wzIO/U2+LCJpJZ/9+IqOCAQ4wggEKMAsGA1UdDwQEAwIEMDCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDAuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMCxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAdBgNVHQ4EFgQUMjLzjqcxqP6jqzaXJgEuV5rOcC0wHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwDAYIKoEcz1UBg3UFAANHADBEAiBcF+z5hVBw3+NwErbYL69AbqpZ5INQ3yZZWhENANdLfgIgUkcv4tatWYaEzHPxlNZmLuLWn3w6Cy9N9pHQt3fCUfU=";
        String priAndPub = "SM2256,MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgyrT/eFL79ntte+R5ERWgr0JWxu3Jnkj/+ku2PTI8KWegCgYIKoEcz1UBgi2hRANCAARTR5lso64vQc+LkzQzpK09/TzlCLQ4e4Mk7WGAESbn/Lr0OqKYUmZahvaU+tyJdg7cIgsXnjRwIz07g3SO6e5X,MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEU0eZbKOuL0HPi5M0M6StPf085Qi0OHuDJO1hgBEm5/y69DqimFJmWob2lPrciXYO3CILF540cCM9O4N0junuVw==";
        String[] split = priAndPub.split(",");
        String s = openTheEnvelope(cipher, Hex.toHexString(Base64.decode(split[1])));

    }

    /**
     * 解sm2数字信封
     *
     * @param envelope 信封结构Base64
     * @param priHex   私钥HEX编码t
     * @return 私钥d
     * @throws NoSuchAlgorithmException   EC
     * @throws InvalidKeySpecException    私钥异常
     * @throws InvalidKeyException        私钥异常
     * @throws IOException                转换字节异常
     * @throws InvalidCipherTextException sm2解密异常
     * @throws NoSuchPaddingException     sm4解密异常
     * @throws IllegalBlockSizeException  sm4解密异常
     * @throws BadPaddingException        sm4解密异常
     */
    public static String openTheEnvelope(String envelope, String priHex) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException, InvalidCipherTextException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        ASN1Sequence asn1_epk = (ASN1Sequence) ASN1Sequence.fromByteArray(Base64.decode(envelope));
        ASN1ObjectIdentifier pkcs7type = (ASN1ObjectIdentifier) asn1_epk.getObjectAt(0);
        if (pkcs7type.getId().equals("1.2.156.10197.6.1.4.2.4") || pkcs7type.getId().equals("1.2.156.10197.6.1.4.2.3")) {
            KeyFactory kf = KeyFactory.getInstance("EC", new BouncyCastleProvider());
            //初始化sm2解密
            BCECPrivateKey pri = (BCECPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Hex.decode(priHex)));
            ECPrivateKeyParameters aPriv = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter(pri);
            SM2Engine sm2Engine = new SM2Engine();
            sm2Engine.init(false, aPriv);

            ASN1TaggedObject dtos = (ASN1TaggedObject) asn1_epk.getObjectAt(1);
            ASN1Sequence asn1_encdata = (ASN1Sequence.getInstance(dtos.getObjectParser(16, true)));
            ASN1Set d1_pubkeyencs = (ASN1Set) asn1_encdata.getObjectAt(1);
            ASN1Sequence asn1_pubkeyenc = (ASN1Sequence) d1_pubkeyencs.getObjectAt(0);
            DEROctetString dertostr = (DEROctetString) asn1_pubkeyenc.getObjectAt(3);
            ASN1Sequence dd = (ASN1Sequence) ASN1Sequence.fromByteArray(dertostr.getOctets());
            ASN1Integer x = (ASN1Integer) dd.getObjectAt(0);
            byte[] xbyte = x.getPositiveValue().toByteArray();
            ASN1Integer y = (ASN1Integer) dd.getObjectAt(1);
            byte[] ybyte = y.getPositiveValue().toByteArray();
            DEROctetString hash = (DEROctetString) dd.getObjectAt(2);
            DEROctetString pubencdata = (DEROctetString) dd.getObjectAt(3);

            byte[] xy = new byte[65];
            xy[0] = 4;
            System.arraycopy(xbyte, xbyte.length == 32 ? 0 : 1, xy, 1, 32);
            System.arraycopy(ybyte, ybyte.length == 32 ? 0 : 1, xy, 1 + 32, 32);
            byte[] c2 = new byte[16];
            System.arraycopy(pubencdata.getOctets(), 0, c2, 0, 16);
            byte[] c3 = new byte[32];
            System.arraycopy(hash.getOctets(), 0, c3, 0, 32);
            byte[] c4 = Arrays.concatenate(xy, c2, c3);
            // sm2解密得到对称密钥的key
            byte[] sm4key = sm2Engine.processBlock(c4, 0, c4.length);

            ASN1Sequence encdata = (ASN1Sequence) asn1_encdata.getObjectAt(3);
            ASN1TaggedObject sm4encdatadto = (ASN1TaggedObject) encdata.getObjectAt(2);
            ASN1OctetString dstr_sm4encdata = (DEROctetString.getInstance(sm4encdatadto.getObjectParser(4, false)));
            byte[] sm4encdata = dstr_sm4encdata.getOctets();

            // sm4解密
            SecretKeySpec newKey = new SecretKeySpec(sm4key, "SM4");
            KeyParameter key = new KeyParameter(sm4key);

            byte[] bytes = decrypt(sm4encdata, key);
            return Hex.toHexString(Arrays.copyOfRange(bytes, 32, bytes.length));
        } else {
            throw new RuntimeException("无效的SM2数字信封格式");
        }

    }

    public static byte[] decrypt(byte[] ciphertext, KeyParameter key) {
        SM4Engine engine = new SM4Engine();
        engine.init(false, key); // false 表示解密

        byte[] decryptedText = new byte[ciphertext.length];
        int offset = 0;

        // 分组解密，每次解密 16 字节
        while (offset < ciphertext.length) {
            engine.processBlock(ciphertext, offset, decryptedText, offset);
            offset += engine.getBlockSize();
        }

        return decryptedText;
    }
}
