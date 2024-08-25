package certTest;


import cn.com.mcsca.pki.core.bouncycastle.asn1.ASN1Integer;
import cn.com.mcsca.pki.core.bouncycastle.asn1.ASN1ObjectIdentifier;
import cn.com.mcsca.pki.core.bouncycastle.asn1.ASN1Sequence;
import cn.com.mcsca.pki.core.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import cn.com.mcsca.pki.core.bouncycastle.jce.provider.BouncyCastleProvider;
import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 密钥工具类（pki-core版本）
 *
 * @author TangHaoKai
 * @version V1.0 2024/8/7 11:40
 */
public class KeyUtil2 {
    public static void main(String[] args) throws Exception {
        String sm2Pri = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgBGo3dacOLVg9J1uGZxZ77QFduzZL+R//vve5v79vSS6gCgYIKoEcz1UBgi2hRANCAASbduQKgJpAnGb7OjWG9Cecy3wwvLn4LG6RBnU0Fk5NLdx9fmVrZ9jIZeBFCCPUDY7+brA3g/PVj6dAVJhWdE8R";
        String alg = getAlgFromPriKeyOrPubKey(Base64.decode(sm2Pri));
        getAlgFromPriKeyOrPubKey(Base64.decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpkozkHHw8jRbbXVqO48t1yqOO1JZnQmBo1rXa1ck58N3CdSITxNHHFrkOgmqSbpeSgQ+algVvrZTO8MId+A3QiNt1s+SrPb/QBeQs+NpyToJ4wmSX1iK45ex//zt5kRAfzwDhjIRSzIVjXyUfUj33KzRjp8y0VY0w89br8iZXrwIDAQAB"));
        PrivateKey privateKey = parsePriKey(sm2Pri);
    }

    public static String getAlgFromPriKeyOrPubKey(byte[] priKeyBytes) throws IOException {
        ASN1Sequence privateSequence = ASN1Sequence.getInstance(priKeyBytes);
        ASN1Sequence algSequence;
        if (privateSequence.getObjectAt(0) instanceof ASN1Integer) {
            algSequence = ASN1Sequence.getInstance(privateSequence.getObjectAt(1).toASN1Primitive().getEncoded());
        } else {
            algSequence = ASN1Sequence.getInstance(privateSequence.getObjectAt(0).toASN1Primitive().getEncoded());
        }
        ASN1ObjectIdentifier objectIdentifier = ASN1ObjectIdentifier.getInstance(algSequence.getObjectAt(0));
        String objectIdentifierString = objectIdentifier.toString();
        // System.out.println(objectIdentifierString);
        if (objectIdentifierString.equals("1.2.840.113549.1.1.1")) {
            return "RSA";
        } else if (objectIdentifierString.equals("1.2.840.10045.2.1")) {
            return "SM2";
        }
        return "";
    }

    public static PrivateKey parsePriKey(String pri) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String alg = getAlgFromPriKeyOrPubKey(Base64.decode(pri));
        if (alg.isEmpty()) {
            throw new RuntimeException("识别算法异常");
        }
        KeyFactory keyFactory;
        if ("RSA".equals(alg)) {
            keyFactory = KeyFactory.getInstance("RSA");
        } else {
            keyFactory = KeyFactory.getInstance("EC", new BouncyCastleProvider());
        }
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(pri)));
    }

    public static PublicKey parsePubKey(String pub) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String alg = getAlgFromPriKeyOrPubKey(Base64.decode(pub));
        if (alg.isEmpty()) {
            throw new RuntimeException("识别算法异常");
        }
        KeyFactory keyFactory;
        if ("RSA".equals(alg)) {
            keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
        } else {
            keyFactory = KeyFactory.getInstance("EC", new BouncyCastleProvider());
        }
        return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(pub)));
    }

    public static int getKeyLengthFromPublicKey(PublicKey publicKey) {
        if (publicKey instanceof BCRSAPublicKey) {
            BCRSAPublicKey bcrsaPublicKey = (BCRSAPublicKey) publicKey;
            BigInteger modulus = bcrsaPublicKey.getModulus();
            return modulus.bitLength();
        } else {
            // thk's todo 2024/8/21 14:32 SM2的长度有问题
            return 256;
        }
    }

}
