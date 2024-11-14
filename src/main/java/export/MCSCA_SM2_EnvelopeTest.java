package export;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
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
 * 大陆云盾的SM2信封加解密测试
 *
 * @author TangHaoKai
 * @version V1.0 2024/9/13 14:31
 */
public class MCSCA_SM2_EnvelopeTest {

    public static void main(String[] args) throws Exception {

        String priDHex = "1d98697f9fb41e974573b3e92a9de64471c5e1f095bbeee3f2b087ae80e8af4d";
        String sm2pub = "";

        String doubleEncryptedPrivateKey = "MIIEfAYKKoEcz1UGAQQCBKCCBGwwggRoAgEBMYHTMIHQAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQJL6LYxt2rJxHzy9QQ6lY7TALBgkqgRzPVQGCLQMEezB5AiEA5qXWOQMPOQt1i6CE/3YeXdFTEQCr85m8ndGLjrcAc50CIGNO6zyELj0pufvQsucrpiwrxx437Tov8hVrg18iSdatBCA72n21MKl38PTRYumb1Vn7uWC9SsrbeX8Zv8Djt54g5gQQpyQIybyX8Cf36k+u2egxizEMMAoGCCqBHM9VAYMRMFkGCiqBHM9VBgEEAgEwCQYHKoEcz1UBaIBA7RBGmvV+N4qkAdm+rC7r5u0QRpr1fjeKpAHZvqwu6+aREOJ01ZDYRmXSO0MdscPBBGzderHUuxfmRir8JBHykKCCAnQwggJwMIICFKADAgECAhAt64hF75RzsvrjdE+iVl8GMAwGCCqBHM9VAYN1BQAwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0xOTA1MDgxMDMxNDNaFw0yMDA1MDcxMDMxNDNaMC8xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEQMA4GA1UEAwwHU0lOR1NNMjBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABMQDm0UOlzNbgi52CXg4ujvewzQGVm4Nm5GLF+j3Gh6pcvmNydPYPE6mxXCzz8tET7KPj9CAndabURlVZprItQGjggEQMIIBDDAfBgNVHSMEGDAWgBS/tiuJjDBsJSCLWWc4G038VxCMczAdBgNVHQ4EFgQUvCxwTFydcvF6+B9tvFV109yPXe8wCwYDVR0PBAQDAgTwMIG8BgNVHR8EgbQwgbEwL6AtoCuGKWh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMTYuY3JsMH6gfKB6hnhsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMTYsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDAYIKoEcz1UBg3UFAANIADBFAiEAqfuL4Lys2LCzFdqoMgt+bM0lj4iT/SjZ9Gko/LMK3acCIEMDOOJPFvYgpjxNsNgQ/Tl8vYNxgy0w++/OFIKyruNBMYGrMIGoAgEBMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQLeuIRe+Uc7L643RPolZfBjAKBggqgRzPVQGDETALBgkqgRzPVQGCLQEERzBFAiEA6iZSplmz/OB9IHZZ1wPgNUkplHf6/LdOmQ9AEMQjj3wCIGqbv+MEDwlcOT8k6/VLnBJjbz6Gv42myPXTh/oEdFyx";
        String sm2pri = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIE0RcwRNxHgwg2b/RRoZFQmzMFVL9bA2podObPqsCvSzoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";

        byte[] oriText = dec(Base64.decode(sm2pri), Base64.decode(doubleEncryptedPrivateKey));
        String oriHex = Hex.toHexString(oriText);
        System.out.println(String.format("%-16s", "oriText>> ") + oriHex);

    }

    /**
     * 封装信封
     *
     * @param publicKey 公钥
     * @param inData    加密内容
     * @return 信封Base64
     */
    public static String enc(byte[] publicKey, byte[] inData) {

        return "";
    }

    /**
     * 解密信封
     *
     * @param priKey  私钥
     * @param envelop 信封
     * @return 加密内容
     */
    public static byte[] dec(byte[] priKey, byte[] envelop) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidCipherTextException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        ASN1Sequence asn1_epk = (ASN1Sequence) ASN1Sequence.fromByteArray(envelop);
        ASN1ObjectIdentifier pkcs7type = (ASN1ObjectIdentifier) asn1_epk.getObjectAt(0);
        if (pkcs7type.getId().equals("1.2.156.10197.6.1.4.2.4")) {
            KeyFactory kf = KeyFactory.getInstance("EC", new BouncyCastleProvider());
            //初始化sm2解密
            BCECPrivateKey pri = (BCECPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(priKey));
            ECPrivateKeyParameters aPriv = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter(pri);
            SM2Engine sm2Engine = new SM2Engine();
            sm2Engine.init(false, aPriv);

            ASN1TaggedObject dtos = (ASN1TaggedObject) asn1_epk.getObjectAt(1);
            ASN1Sequence asn1_encdata = (ASN1Sequence) dtos.getBaseObject();
            // ASN1Sequence asn1_encdata = (ASN1Sequence) dtos.getObjectParser(0, true);

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
            DEROctetString dstr_sm4encdata = (DEROctetString) sm4encdatadto.getBaseObject();
            // DEROctetString dstr_sm4encdata = (DEROctetString) sm4encdatadto.getObjectParser(0, true);
            byte[] sm4encdata = dstr_sm4encdata.getOctets();
            // sm4解密
            Cipher cipher = Cipher.getInstance("SM4/ECB/NoPadding", new BouncyCastleProvider());
            SecretKeySpec newKey = new SecretKeySpec(sm4key, "SM4");
            cipher.init(Cipher.DECRYPT_MODE, newKey);
            sm4encdata = cipher.doFinal(sm4encdata);
            return Arrays.copyOfRange(sm4encdata, 32, sm4encdata.length);
        } else {
            throw new RuntimeException("无效的SM2数字信封格式");
        }
    }

}
