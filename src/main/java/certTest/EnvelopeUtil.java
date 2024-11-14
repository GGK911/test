package certTest;

import cipherTest.SymmetricAlgorithmUtil;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 信封工具
 *
 * @author TangHaoKai
 * @version V1.0 2024/8/13 10:52
 */
public class EnvelopeUtil {
    private static final Provider BC = new BouncyCastleProvider();

    public static void main(String[] args) throws Exception {
        // String d = openTheEnvelope("MIIEQgYKKoEcz1UGAQQCBKCCBDIwggQuAgEBMYHTMIHQAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQZVqJYUlGpXieHix1naIv5DALBgkqgRzPVQGCLQMEezB5AiAgK9wa49cBIvO58/qC1nS1Z9/cB6Awh98vZiTtgloEowIhAOrtGTj6iFqlfJoDuKVI2zj0EHVJlDak6UNIPKE5NB4wBCDmDE2XGH9MEaMmczijdbEZZKEvx16MLOyTW3DJr8BNOgQQ3iecE4GecLE+OD32WlKpKjEMMAoGCCqBHM9VAYMRMFkGCiqBHM9VBgEEAgEwCQYHKoEcz1UBaIBA1dWxOCmjmEv+swk5PzjiQdXVsTgpo5hL/rMJOT844kGPwwytJnvKxXH1saRj89Chvg/ahKwKuj0v4UIUd/JetaCCAe8wggHrMIIBj6ADAgECAhBmGlO15VIwOaiyxJ6pnaeUMAwGCCqBHM9VAYN1BQAweDELMAkGA1UEBhMCQ04xEjAQBgNVBAgMCUNob25ncWluZzE6MDgGA1UECgwxRWFzdC1aaG9uZ3h1biBDZXJ0aWZpY2F0ZSBBdXRob3JpdHkgQ2VudGVyIENPLkxURDEZMBcGA1UEAwwQRWFzdC1aaG9uZ3h1biBDQTAeFw0yMzA4MTcwMTI4MzNaFw0zMzA4MTQwMTI4MzNaMC8xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEQMA4GA1UEAwwHU00yU0lHTjBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABCA6bLiYQEN5maqDtr6rfmqg5/v+GhZHlIDdoQ/SWQ+Hc3SedRXcMas5oWfBUAZ+Nko2nc1+1LMWsRJi4DgDr36jQjBAMB8GA1UdIwQYMBaAFAnuuwf+H95XmJJu7j/LUmpL+SYdMB0GA1UdDgQWBBSJqOoC7mJ441HP32hg8CmGbLpezDAMBggqgRzPVQGDdQUAA0gAMEUCIBnnZHGxXcc7Ar6Wzd3NJ+8Da5TwK8Jn8J7S8p4xsPmwAiEAmW7lerdM1A6+7W4BEfLh69J/fC+d+Hzzx2h/ohWLKQkxgfYwgfMCAQEwgYwweDELMAkGA1UEBhMCQ04xEjAQBgNVBAgMCUNob25ncWluZzE6MDgGA1UECgwxRWFzdC1aaG9uZ3h1biBDZXJ0aWZpY2F0ZSBBdXRob3JpdHkgQ2VudGVyIENPLkxURDEZMBcGA1UEAwwQRWFzdC1aaG9uZ3h1biBDQQIQZhpTteVSMDmossSeqZ2nlDAKBggqgRzPVQGDETALBgkqgRzPVQGCLQEERjBEAiA4XRuZ24bg5alWcsNJ1GERgLUoo664xGWEeccVq6E14wIgbND2lEEoCuHflcPtKsDJ1v1fgaE3Y4lIkLpg26/J64A=", "308193020100301306072a8648ce3d020106082a811ccf5501822d047930770201010420d873c87879974f477e293da9487907b04046c485820d21e0f0e3c0dd305daa4ca00a06082a811ccf5501822da14403420004e86cf685cc7e32a061435b295dd4b6697a54a965456c9d45df9f314fc594d2df7eb3d1d8dc9e6dbc3403cf4ce22c4a7184f1c8d790084e7c9714185c366651ba");
        // System.out.println(String.format("%-16s", "d>> ") + d);

        byte[] randomKey = "1234567812345678".getBytes();

        String signCert = "MIIC2zCCAn6gAwIBAgIQRqY8jkgIwf74VOiAqcNT+zAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjMwODE3MDQwNzI1WhcNMjYwODE2MDQwNzI1WjCBhDELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBI5MTYxMDAwMDU1NTY5Mzk0N1IxNjA0BgNVBAMMLTEyNTQyOTExMTc3MDE2NjA2NzJA5rWL6K+VU00y5Y+M6K+B5LmmQDAxQDAwMzBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABMU3x2yKHkbV7FPrvXIJAOM+W4ifAMeCAfGeC5PxnA2o3b9cZSTuaHOOrPQdSfbBESkQ1qJcSu4ij7jG4YogQVCjggEkMIIBIDAdBgNVHQ4EFgQUm/FFFTlRC6b3eWebZR1WK1GlzwQwEgYDVR0lBAswCQYHKoEch4QLATCBvAYDVR0fBIG0MIGxMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDEuY3JsMH+gfaB7hnlsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjoxMDM4OS9DTj1jcmwxLE9VPUNSTCxPPU1DU0NBLEM9Q04/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXNlP29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MB8GA1UdIwQYMBaAFLkj5hx/z7c8WCRbpAqtihGXenu2MAsGA1UdDwQEAwIGwDAMBggqgRzPVQGDdQUAA0kAMEYCIQDU2i1y3b+GdUasHnwSF6As2tcGdi6iEs41ro+ale49VgIhAIzwjIEcWbV30XUsjppWqHceIaPO+Z+V0Nc9KrllgITj";
        String encCert = "MIIC2jCCAn6gAwIBAgIQf0v8x9dk9lERF3YZLBaRyTAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjMwODE3MDQwNzI1WhcNMjYwODE2MDQwNzI1WjCBhDELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBI5MTYxMDAwMDU1NTY5Mzk0N1IxNjA0BgNVBAMMLTEyNTQyOTExMTc3MDE2NjA2NzJA5rWL6K+VU00y5Y+M6K+B5LmmQDAxQDAwMzBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABC/wIZBsEqzE724aUtatVrwxJ9TTPujrul4pzp45bODeiqquagkZvt+sIL3ovJIQZ3GbXR/Naze3KyJmB4u/4+2jggEkMIIBIDALBgNVHQ8EBAMCBDAwHQYDVR0OBBYEFCExvX161vubQfOP69HtCLXjgPR6MBIGA1UdJQQLMAkGByqBHIeECwEwgbwGA1UdHwSBtDCBsTAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwxLmNybDB/oH2ge4Z5bGRhcDovL3d3dy5tY3NjYS5jb20uY246MTAzODkvQ049Y3JsMSxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAfBgNVHSMEGDAWgBS5I+Ycf8+3PFgkW6QKrYoRl3p7tjAMBggqgRzPVQGDdQUAA0gAMEUCIBJgMnNLx6vEo1F/8o/ICVYRMfQqVegUwXdIsR9IZOYjAiEA8HepM3/Fuc5IgwQ6WIzSR1JwwwAFW/fpE4N9Ujfwmao=";
        String doubleEncryptedPrivateKey = "MIIEfAYKKoEcz1UGAQQCBKCCBGwwggRoAgEBMYHTMIHQAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQJL6LYxt2rJxHzy9QQ6lY7TALBgkqgRzPVQGCLQMEezB5AiEA5qXWOQMPOQt1i6CE/3YeXdFTEQCr85m8ndGLjrcAc50CIGNO6zyELj0pufvQsucrpiwrxx437Tov8hVrg18iSdatBCA72n21MKl38PTRYumb1Vn7uWC9SsrbeX8Zv8Djt54g5gQQpyQIybyX8Cf36k+u2egxizEMMAoGCCqBHM9VAYMRMFkGCiqBHM9VBgEEAgEwCQYHKoEcz1UBaIBA7RBGmvV+N4qkAdm+rC7r5u0QRpr1fjeKpAHZvqwu6+aREOJ01ZDYRmXSO0MdscPBBGzderHUuxfmRir8JBHykKCCAnQwggJwMIICFKADAgECAhAt64hF75RzsvrjdE+iVl8GMAwGCCqBHM9VAYN1BQAwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0xOTA1MDgxMDMxNDNaFw0yMDA1MDcxMDMxNDNaMC8xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEQMA4GA1UEAwwHU0lOR1NNMjBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABMQDm0UOlzNbgi52CXg4ujvewzQGVm4Nm5GLF+j3Gh6pcvmNydPYPE6mxXCzz8tET7KPj9CAndabURlVZprItQGjggEQMIIBDDAfBgNVHSMEGDAWgBS/tiuJjDBsJSCLWWc4G038VxCMczAdBgNVHQ4EFgQUvCxwTFydcvF6+B9tvFV109yPXe8wCwYDVR0PBAQDAgTwMIG8BgNVHR8EgbQwgbEwL6AtoCuGKWh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMTYuY3JsMH6gfKB6hnhsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMTYsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDAYIKoEcz1UBg3UFAANIADBFAiEAqfuL4Lys2LCzFdqoMgt+bM0lj4iT/SjZ9Gko/LMK3acCIEMDOOJPFvYgpjxNsNgQ/Tl8vYNxgy0w++/OFIKyruNBMYGrMIGoAgEBMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQLeuIRe+Uc7L643RPolZfBjAKBggqgRzPVQGDETALBgkqgRzPVQGCLQEERzBFAiEA6iZSplmz/OB9IHZZ1wPgNUkplHf6/LdOmQ9AEMQjj3wCIGqbv+MEDwlcOT8k6/VLnBJjbz6Gv42myPXTh/oEdFyx";
        String sm2pri = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIE0RcwRNxHgwg2b/RRoZFQmzMFVL9bA2podObPqsCvSzoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";

        // encCert = "MIICuTCCAlygAwIBAgIQVLqJE+asfKP3UolHJxctGDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwODIzMDM0MzI5WhcNMjQwODI0MDM0MzI5WjB5MQswCQYDVQQGEwJDTjEMMAoGA1UECgwD5pegMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE3NjUyMjUyMjMwMDQ3MDA2NzJA5ZSQ5aW95YevQDAxQDA2NDBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABDqOp+hmXYHFGHUdo0ACSDlVYwDUR7B1PI+NGh6BuAUQ4fIlfhKyWI4sijEKNLfs0bdT+VhVRN0leRZ590uUcSOjggEOMIIBCjALBgNVHQ8EBAMCBDAwgboGA1UdHwSBsjCBrzAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwwLmNybDB9oHugeYZ3bGRhcDovL3d3dy5tY3NjYS5jb20uY246Mzg5L0NOPWNybDAsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwHQYDVR0OBBYEFF8Zg3C5/c1qlb4upRJ5oqDVgQ6IMB8GA1UdIwQYMBaAFPEiCmeYjfXjsqrDF2vAQh++S712MAwGCCqBHM9VAYN1BQADSQAwRgIhAMXwjE1wq7BafC1NK/QTucVPDVDXTCJL8xYfhMCxjDC/AiEAnmHg4uxBHvix5sansjZ021TUjcaJ11ML1WxigS/ce3g=";
        // doubleEncryptedPrivateKey = "MIIEQwYKKoEcz1UGAQQCBKCCBDMwggQvAgEBMYHUMIHRAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQbRMmbHA6mnSAad15ifd0kjALBgkqgRzPVQGCLQMEfDB6AiEAt+EDP4qmmZt5xXSklqA6PLQ9DoJ0XUIk06FOqtbcQg0CIQCwf5SKEQ5npPDMle/kJpy922P1u1KdMUDDEW46SjDvFgQgaT8/9nLJJOX5gILm3p4jIHZlPVKCISlq+VpIL8SkKV4EEG591JlxBwtEiFdFMS6lnWAxDDAKBggqgRzPVQGDETBZBgoqgRzPVQYBBAIBMAkGByqBHM9VAWiAQJv/z6La4PqTwvKJrcTUgJ6b/8+i2uD6k8Lyia3E1ICe873BHaVdYe2pZLjqppb0LOZkPaUG2AFsPT8wnhpl/mygggHvMIIB6zCCAY+gAwIBAgIQZhpTteVSMDmossSeqZ2nlDAMBggqgRzPVQGDdQUAMHgxCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAlDaG9uZ3FpbmcxOjA4BgNVBAoMMUVhc3QtWmhvbmd4dW4gQ2VydGlmaWNhdGUgQXV0aG9yaXR5IENlbnRlciBDTy5MVEQxGTAXBgNVBAMMEEVhc3QtWmhvbmd4dW4gQ0EwHhcNMjMwODE3MDEyODMzWhcNMzMwODE0MDEyODMzWjAvMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAMMB1NNMlNJR04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQgOmy4mEBDeZmqg7a+q35qoOf7/hoWR5SA3aEP0lkPh3N0nnUV3DGrOaFnwVAGfjZKNp3NftSzFrESYuA4A69+o0IwQDAfBgNVHSMEGDAWgBQJ7rsH/h/eV5iSbu4/y1JqS/kmHTAdBgNVHQ4EFgQUiajqAu5ieONRz99oYPAphmy6XswwDAYIKoEcz1UBg3UFAANIADBFAiAZ52RxsV3HOwK+ls3dzSfvA2uU8CvCZ/Ce0vKeMbD5sAIhAJlu5Xq3TNQOvu1uARHy4evSf3wvnfh888dof6IViykJMYH2MIHzAgEBMIGMMHgxCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAlDaG9uZ3FpbmcxOjA4BgNVBAoMMUVhc3QtWmhvbmd4dW4gQ2VydGlmaWNhdGUgQXV0aG9yaXR5IENlbnRlciBDTy5MVEQxGTAXBgNVBAMMEEVhc3QtWmhvbmd4dW4gQ0ECEGYaU7XlUjA5qLLEnqmdp5QwCgYIKoEcz1UBgxEwCwYJKoEcz1UBgi0BBEYwRAIgMdoCbXmyth0F9kMYmHjG2LoM6WSkLxDdAb85kaMuETgCICpBvqmGFFquPuQwAz8KmF17sJAomYRfXW+r6XeaZgT8";
        // signCert = "MIICxzCCAmqgAwIBAgIQf3RK0UDhwgRa69M14UkkajAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwODIzMDUzMjAwWhcNMjQwODI0MDUzMjAwWjB5MQswCQYDVQQGEwJDTjEMMAoGA1UECgwD5pegMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE4MjY2MDE0MTQzMzY0NzEwNDBA5ZSQ5aW95YevQDAxQDA2NTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABGmh1thJJwk3bb2tUoRuOncaeMYOBT99YKuBQomWPd3g+ZMSDG8g9Tb6jmsWShlPKAoknjhrssTdlG9oVwrlom2jggEcMIIBGDAfBgNVHSMEGDAWgBTxIgpnmI3147KqwxdrwEIfvku9djAdBgNVHQ4EFgQUKT2+LmP32BTw6S5DBKTAsXbMZEEwDAYDVR0TBAUwAwEBADALBgNVHQ8EBAMCBPAwgboGA1UdHwSBsjCBrzAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwwLmNybDB9oHugeYZ3bGRhcDovL3d3dy5tY3NjYS5jb20uY246Mzg5L0NOPWNybDAsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDAYIKoEcz1UBg3UFAANJADBGAiEAtB7pFEjxfk726c1rgdphO/XXfdEbbpoUm2HfM9XOEsQCIQDvtElbCQiZR1BZepX3Y4vE7kZZAN9oOrlDp8XdluLqtQ==";
        // sm2pri = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgmyFEjK2ncG/wI1+LdCyw2GOjJbAHwMinqdzSrmfqHZ+gCgYIKoEcz1UBgi2hRANCAAS2kvU4DfcPAwWE0YR5PMJcBxfEKD0FZplm6B61pPY80FiBjo0h8DDGgy1/GKvI8FB23FilKeGC/TTA/90EEAht";


        String dHex = PkcsTest.CsrUtil.analysisSM2EncKey(doubleEncryptedPrivateKey, Hex.toHexString(Base64.decode(sm2pri)));
        System.out.println(String.format("%-16s", "dHex>> ") + dHex);

        String fujian = changePriEnvelopBy35276(Base64.toBase64String(Hex.decode(dHex)), encCert, signCert, randomKey);
        System.out.println(String.format("%-16s", "fujian>> ") + fujian);

        System.out.println("*******************************************RSA**************************************************");

        //RSA 1024
        String rsapri = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKR3vt80eis7GcqT6nn4AN9QeSWGktb/VvAszkf29JpYx0NbNIc6a9TpBCUP1p98Kkbkcv1Eq2wcVLrSbZmOwaaUjW0mcxp+BIwMXhlXaHC9mBlFmA454JkuUDwh5JuGkGyHK284+bmYJJecs5Vb8Xic+a4yDG04A3SSJEap7iNzAgMBAAECgYA5D9rHcluYuC6gnGVT3/ndgPwnSuOTeI/fUIxZZ5NCId8wvWoiKODUw+vOOAqM1vWMFyLWQIcBQWscTnn8Nw10gYLfvD6aXfbiQ4SgZDu2S8bZ2fkwgTdmi1BHlHxRXq56qi0dB20JZ6tewkl1Pi0Q5wpslgfVi7WP5oFCsH2wIQJBANxaqU0iuXpFAI8k3t8Nu1c0dtS9xuLiSUp2U1anVoD1xkCM4IJ7lzdGxqXy1/QhbkZsFpKcAQYCRy6p/4YDPmkCQQC/ErVym8Ke/c2HMFACc8O0SVDfph+AhDm/V123ztiW6Q/3C6P8V0KMOehReRKGelO+V966QKsGINdTkTtvHw97AkA0ASWRsc9KXvyZy97Zj5kWJKii3sMQis03SKO0gLu2pcqLM3RM9zQh9I8vXRfAYx9ueVX+ddj7/Q+loLNQgnV5AkAAv9EIVwYHW5Vvv0fBCrUswtDXX65l8Z7MWkpayyvcQ6O1Y01MUwdGx39aum/RKS+k4nFUJ6bECmLtx/cEs4l7AkBUJ8JYZrlrWlbpX3fCPq8UmESQLox3jAV37ygxwfJqet6uIt5cJAQCUJHGKs//ihmfYvLeCHGs9aPpuvNw3EAW";
        String p10 = "MIIBTTCBtwIBADAQMQ4wDAYDVQQDDAVNQ1NDQTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEApHe+3zR6KzsZypPqefgA31B5JYaS1v9W8CzOR/b0mljHQ1s0hzpr1OkEJQ/Wn3wqRuRy/USrbBxUutJtmY7BppSNbSZzGn4EjAxeGVdocL2YGUWYDjngmS5QPCHkm4aQbIcrbzj5uZgkl5yzlVvxeJz5rjIMbTgDdJIkRqnuI3MCAwEAATANBgkqhkiG9w0BAQsFAAOBgQBHxJG70vBJvqeI/F2SaA39JCsTl+VyCtZyBBVdxVY3cMxwQUT5VRM2n4Deri+G6iGfxfnE5CV1p4hsBgLslRTQoUIBPUdh4R8ZEdena+v2ZNUHtS1fk4vpuOrPfwi8d8HERoXO9RpYGVClQzhaO8OPP4c95PCx27BoR9t/gYEolw==";
        String rsadoubleEncryptedPrivateKey = "hT9hmdvpB8K2fgM51PUV6Ty8AnL4rWBwSn0UqkjcG7dHV/BVl3rk8W81idYSdkOCzoSKnkR9G6YyBfEJ2vAxVMiAH5uDWwTvhdv6hdqqlKhKAD6cS8Bw3CF+uT/WP9DRh2hgPAU08ThvG1cIDzjVVpnfeFHQtEzr4dYJ+AE7FlAgBZV0j7hHWY1iOEEdZhTas4yanlqaiBc/mOlifvG0yg2rCe13X0zAf7wfTrJQ1tmIrjA6lKsK1/DeEBvEYIrBXr0uYBYI4A7qcp1ttQw6oWOWpbaGyFLsID2XK3Ekm8dlK3uqJPPICYzHDDQOgdXjq4Cb/NyKN9VQWbg3Y9XR1UczxxMP1Sx4wnLlzjySZBxe4SgONU2O8OrYEmhZMXgZaU3UetkQmBbr66s9KZZFlp4/MPEeL6ZNuluyrfxJu8Voi2DT7Yn898H15XNYgWjM9GBtF1AjKIMiD+q1ws3t61tUY8UeRzyIvKFz8kZ5eiPLCRQ8Dl1hLYFvM44e6PHQHL1LMAdcMSA+hj+kv2n97dzOrNsAfUNjfEWSbY5KfDdKoUWgMhZ/1MgBI0kEt+UXQtWAgZgYLui0a7yqqWHKimzli+XO3Gefu7ApqyJPMHYY6tbkPtITzyt8tIBmmtQRKQ244B4yWgV2i5D1IxrcuLIAj0vMDQW1BBkJEot2XB/Q6Oj8FuVvtnl/OV8YoaSwDNW1Br++tdBMhDNmaDYv92YG9QUZwnlRzkKT9Sni6daSowz+jYsJrFkXAtmmyTPy4K1AdCtdjJkyMJ/ZLrXk9jDhUnIqPmm/swAvTzM14otkHjGGTOD/SoS9NKAn30RZUa3RvU+28bujow==";
        String doubleEncryptedSessionKey = "NAWOIYxefF5q0GT81AiGZHPMeAP+tIw1MS19yT8XjXTX0WYQD4EjWI+MWNfofJLNb0LHXhFdndMyi+mHIl7O5kU/sniz6bn2c2QYtnYQI2Czwj/cC3plBftziiGmi8M2e9x5fLK/bEkKfdPKMXRy/7T5JY33oC3W0RtATylx55g=";
        signCert = "MIIEgTCCA2mgAwIBAgIQWSFhOr32s/90GYH+obri3zANBgkqhkiG9w0BAQsFADA9MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAsMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0yMzExMzAwNzAzMzZaFw0yMzEyMTAwNzAzMzZaMGwxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEQMA4GA1UECwwHbG9jYWxSQTEbMBkGA1UEBQwSOTE1MDAxMDhNQTVZUTdQNjY1MR4wHAYDVQQDDBVUcGVyUlNBeDFAVGVzdEAwMUAwMjQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDKeL5nGVEH6bzl7h/iEcqpr8mjjjeoxV3pXDc4gZctqM2NZ39S2rzC1kcmGqNgMQP0WAX53FxNebN7Df1wBM8FwPpYM8IwvZb79498NXFkbPzBks0proP0MvZnC/pwMoZhH+GRjfYkccpuptDM38dIR8Jwc/xhlQtckyBXKhuzp4bBypVqExZY4z+L+oBgJNtg5R2Vb8c69BnlDJY9OaPANGIDKGzNG3Xbh8VSOXo4gi1Z32KrnyIolx/mUfE2ha9/TYyBHVtdnfMuXJOUphM5erb7uYiMN4KBQik8RInbW8KQ8dH1iWpN3WLwZDSiAeenWlVLg4a0nPVUCCBv/SmrAgMBAAGjggFMMIIBSDATBgNVHSUEDDAKBggrBgEFBQcDAjAdBgNVHQ4EFgQUa/61ymccEWhrK5RyeoacnR5TzxsweAYDVR0fBHEwbzBIoEagRKRCMEAxCzAJBgNVBAYTAkNOMQwwCgYDVQQKDANKSVQxEDAOBgNVBAsMB0FERDFDUkwxETAPBgNVBAMMCGNybDExMTcxMCOgIaAfhh1odHRwOi8vMTI3LjAuMC4xL2NybDExMTcxLmNybDALBgNVHQ8EBAMCBsAwXAYIKwYBBQUHAQEEUDBOMCgGCCsGAQUFBzAChhxodHRwOi8vMTI3LjAuMC4xL2NhaXNzdWUuaHRtMCIGCCsGAQUFBzABhhZodHRwOi8vMTI3LjAuMC4xOjIwNDQzMB8GA1UdIwQYMBaAFLlR6QHwqSMfik7pDmsH0AxKLaagMAwGA1UdEwQFMAMBAQAwDQYJKoZIhvcNAQELBQADggEBAMZvgKa56MVyKGdHIZfARBVtA3/DxLpFN1LRK2TgmwQbLzKf8/hftXbK3LbEl1J0QuEgxWVZ218neaDb9Mc+MyuW4fxS0N7ALem/PiLirZ29YrRbGmDM+9MP+tJrnkypwfmXOHK57maC3fCkyhystx6WrVCvAzzJSu//5TxY2D1LxsS4nknc0932Txu1lrM8+bK15qOXLwG+A4Lq1cMrnigXZaTxRdDUq8We1J7+y3llicmXAzHHC8sWodTJcd6Nph9n0Ki7rCjkquRnhytTsob9lKaScLB2XTRyAmw2oMe5KRZLVeFnixmL63g2BoHk8Tp7MB+BzhsyILoMkl1oOXQ=";

        //RSA 1024
        rsapri = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOM+tI+MKXzE0XPvX58YGYJOKQA3WNCk9REPCxniHQZyBWHN/BFdFGcOo6Q0+ITZgsJoAtBjON5VAcp7crieF3Bd9rstJ/LOm1aVTRYSnq8s4cusGC5hHZouQ89Kd6j+oR3d9xHTosSnksdYRsVNyOCsiQVQf93IaMRHOEwhpC+RAgMBAAECgYAvY6093knslhLj7YBwahMvxGLyg5hG8o+UviFfhXLuHCsZKV6utE2D7F6d6NoGwmFtb0le8cIzQ2D7O+FBtN5IEQGB+kR9YttIoI70uezWXCYg9y103P/3h8Z0/r+h1KSJ41R0cwJfY4ZQqKPA563SqQyULdx8Yy1RlCyc92Ge7QJBAPZSM2y4f7EyyNYnbK51yFKe1dF5G+7DFtMswRopaqAd0okk+SPu9B6nnhXtJhAVZH2h/PUV7YPBA7SaIHQP4f8CQQDsLJz1rfZbtvjuCgvmr/Lg2Ib6XOmaoQHySfik1viNe+Kuqwb1N6gKMEHfdcicOV6lfEw2UEDbOPruGQHtGs5vAkEAp6P8QcMzjKrOiwmb6wRYSPq29PCi9RWrZB+ycJa9bam+Tv5t3WnUlURoz+1tmUal9OcZXMgGtUPYvMKk0bi5iwJAC25NWobE+cNtYPgTg2LYupFAVzXQCK/qTDPQWQoelZp9aj7U42GCPJwBVDN7NjSApDnT5n0T5ohnfMaxFSzK+QJBAMMYc58SV55WZ3wrjDlqouQMgTU9KDoo9AgD5ND8eA6QxI+OgtJMckTOf3EwNR8zahG1Qm2+86qFAcvVe5vxpHc=";
        p10 = "";
        rsadoubleEncryptedPrivateKey = "";
        doubleEncryptedSessionKey = "";
        signCert = "";

        // 先去解我们自己的信封
        byte[] encPriBytes = decodeMscaRSAEnveloped(rsadoubleEncryptedPrivateKey, doubleEncryptedSessionKey, rsapri, "");
        String alg = "DESEDE";
        SecretKey symKey = SymmetricAlgorithmUtil.getKeyByAlg(alg);
        byte[] iv = SymmetricAlgorithmUtil.getIVByAlg(alg);
        String ivHex = Hex.toHexString(iv);
        System.out.println(String.format("%-16s", "ivHex>> ") + ivHex);
        String CAShare = changeCAShareRsaPriEnvelop(encPriBytes, alg, symKey, iv, false, Base64.decode(signCert));
        System.out.println(String.format("%-16s", "CAShare>> ") + CAShare);
        String CAShareWithCSR = changeCAShareRsaPriEnvelopWithCSR(encPriBytes, alg, symKey, iv, false, Base64.decode(p10));
        System.out.println(String.format("%-16s", "CAShareWithCSR>> ") + CAShareWithCSR);

    }

    /**
     * CA互认需要的RSA加密密钥信封结构
     *
     * @param encPriBytes 加密私钥
     * @param symAlg      对称算法
     * @param key         对称KEY
     * @param iv          向量
     * @param isPadding   是否填充
     * @param signCert    签名证书
     * @return CA互认需要的RSA加密密钥信封结构
     * @throws IOException                        格式化私钥|DERSequence输出
     * @throws NoSuchAlgorithmException           算法
     * @throws InvalidAlgorithmParameterException 加密
     * @throws NoSuchPaddingException             加密
     * @throws IllegalBlockSizeException          加密
     * @throws BadPaddingException                加密
     * @throws InvalidKeyException                加密
     * @throws CertificateException               解析签名证书
     * @throws InvalidKeySpecException            格式化私钥
     */
    public static String changeCAShareRsaPriEnvelop(byte[] encPriBytes, String symAlg, SecretKey key, byte[] iv, boolean isPadding, byte[] signCert)
            throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, CertificateException, InvalidKeySpecException {
        // 私钥数据 去掉pkcs8结构
        ByteArrayInputStream bIn = new ByteArrayInputStream(encPriBytes);
        ASN1InputStream dIn = new ASN1InputStream(bIn);
        ASN1Sequence seq = (ASN1Sequence) dIn.readObject();
        DEROctetString dos = (DEROctetString) seq.getObjectAt(2);
        byte[] rsaPri = dos.getOctets();

        // 对称加密私钥
        SecretKey symKey;
        if (key != null) {
            symKey = key;
        } else {
            symKey = SymmetricAlgorithmUtil.getKeyByAlg(symAlg);
        }
        byte[] priEncryptBytes = SymmetricAlgorithmUtil.encrypt(rsaPri, symKey.getEncoded(), iv, symAlg, isPadding);

        PublicKey publicKey = CertUtil.getPubKeyFromCert(signCert);
        BCRSAPublicKey rsaPublicKey = (BCRSAPublicKey) publicKey;
        BigInteger publicExponent = rsaPublicKey.getPublicExponent();

        PrivateKey privateKey = KeyUtil.parsePriKey(encPriBytes);
        BCRSAPrivateKey bcrsaPrivateKey = (BCRSAPrivateKey) privateKey;
        BigInteger priMoudlus = bcrsaPrivateKey.getModulus();

        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", BC); //RSA_PKCS1_PADDING
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] rsaEncBytes = cipher.doFinal(symKey.getEncoded());

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
        ASN1ObjectIdentifier desAlgOID = SymmetricAlgorithmUtil.getOIDByAlgorithmName(symAlg);
        if (desAlgOID == null) {
            throw new RuntimeException("对称算法OID异常");
        }
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
        asnEncRsa.add(new DERBitString(priEncryptBytes));
        DERSequence derseq = new DERSequence(asnEncRsa);

        return Hex.toHexString(derseq.getEncoded());
    }

    /**
     * CA互认需要的RSA加密密钥信封结构
     *
     * @param encPriBytes 加密私钥
     * @param symAlg      对称算法
     * @param key         对称密钥
     * @param iv          向量
     * @param isPadding   是否填充
     * @param csr         P10
     * @return CA互认需要的RSA加密密钥信封结构
     * @throws IOException                        格式化私钥|DERSequence输出
     * @throws NoSuchAlgorithmException           算法
     * @throws InvalidAlgorithmParameterException 加密
     * @throws NoSuchPaddingException             加密
     * @throws IllegalBlockSizeException          加密
     * @throws BadPaddingException                加密
     * @throws InvalidKeyException                加密
     * @throws InvalidKeySpecException            格式化私钥
     */
    public static String changeCAShareRsaPriEnvelopWithCSR(byte[] encPriBytes, String symAlg, SecretKey key, byte[] iv, boolean isPadding, byte[] csr) throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        // 私钥数据 去掉pkcs8结构
        ByteArrayInputStream bIn = new ByteArrayInputStream(encPriBytes);
        ASN1InputStream dIn = new ASN1InputStream(bIn);
        ASN1Sequence seq = (ASN1Sequence) dIn.readObject();
        DEROctetString dos = (DEROctetString) seq.getObjectAt(2);
        byte[] rsaPri = dos.getOctets();

        // 对称加密私钥
        SecretKey symKey;
        if (key != null) {
            symKey = key;
        } else {
            symKey = SymmetricAlgorithmUtil.getKeyByAlg(symAlg);
        }
        byte[] priEncryptBytes = SymmetricAlgorithmUtil.encrypt(rsaPri, symKey.getEncoded(), iv, symAlg, isPadding);

        PublicKey publicKey = CsrUtil.getPub(csr);
        BCRSAPublicKey rsaPublicKey = (BCRSAPublicKey) publicKey;
        BigInteger publicExponent = rsaPublicKey.getPublicExponent();

        PrivateKey privateKey = KeyUtil.parsePriKey(encPriBytes);
        BCRSAPrivateKey bcrsaPrivateKey = (BCRSAPrivateKey) privateKey;
        BigInteger priMoudlus = bcrsaPrivateKey.getModulus();

        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", BC); //RSA_PKCS1_PADDING
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] rsaEncBytes = cipher.doFinal(symKey.getEncoded());

        // 密钥数据
        ASN1EncodableVector asnPubkey = new ASN1EncodableVector();
        asnPubkey.add(new ASN1Integer(priMoudlus));
        asnPubkey.add(new ASN1Integer(publicExponent));
        DERSequence derPubkey = new DERSequence(asnPubkey);

        // 第三部分数据 包含 RSA oid和公钥
        ASN1EncodableVector asnEncRsaPub = new ASN1EncodableVector();
        ASN1ObjectIdentifier rsaAlgOID = new ASN1ObjectIdentifier("1.2.840.113549.1.1.1");
        AlgorithmIdentifier algID = new AlgorithmIdentifier(rsaAlgOID, DERNull.INSTANCE);
        asnEncRsaPub.add(algID);
        asnEncRsaPub.add(new DERBitString(derPubkey.getEncoded()));
        DERSequence derEncRsaPub = new DERSequence(asnEncRsaPub);

        // 第一部分数据包含 3desc oid
        ASN1ObjectIdentifier desAlgOID = SymmetricAlgorithmUtil.getOIDByAlgorithmName(symAlg);
        if (desAlgOID == null) {
            throw new RuntimeException("对称算法OID异常");
        }
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
        asnEncRsa.add(new DERBitString(priEncryptBytes));
        DERSequence derseq = new DERSequence(asnEncRsa);

        return Hex.toHexString(derseq.getEncoded());
    }

    /**
     * GM/T 35276《SM2密码算法使用规范》
     * SM2密钥对的保护数据格式的ASN.1定义
     *
     * @param encsm2pristr 加密私钥
     * @param encsm2cert   加密证书
     * @param signcert     签名证书
     * @param symKey       非对称算法私钥
     * @return 信封
     */
    public static String changePriEnvelopBy35276(String encsm2pristr, String encsm2cert, String signcert, byte[] symKey) throws IOException {
        byte[] sm2pri = Base64.decode(encsm2pristr);
        byte[] xy = getSm2PubKey(encsm2cert);
        if (sm2pri.length == 31) {
            byte[] sm2pri2 = new byte[32];
            sm2pri2[0] = 0;
            System.arraycopy(sm2pri, 0, sm2pri2, 1, 31);
            sm2pri = sm2pri2;
        } else if (sm2pri.length == 33) {
            if (sm2pri[0] == 0) {
                byte[] sm2pri2 = new byte[32];
                System.arraycopy(sm2pri, 1, sm2pri2, 0, 32);
                sm2pri = sm2pri2;
            }
        }

        // 对称密钥加密加密私钥
        byte[] encEncPri = symmetricEncryption(sm2pri, symKey, false, "");
        int symCipherLength = encEncPri.length;
        System.out.println(String.format("%-16s", "symCipherLength>> ") + symCipherLength);
        // 从签名证书中拿到公钥
        SubjectPublicKeyInfo spki = getPubInfoFromCert(Base64.decode(signcert));
        // 公钥加密对称密钥pwd
        String encSymPri = PubkeyEncryption(spki, symKey);

        ASN1EncodableVector asn1ev = new ASN1EncodableVector();

        ASN1ObjectIdentifier digestAlgOID = new ASN1ObjectIdentifier("1.2.156.10197.1.104.1");
        AlgorithmIdentifier algID = new AlgorithmIdentifier(digestAlgOID, DERNull.INSTANCE);
        // 标识
        asn1ev.add(algID);
        // 被加密后的 对称密钥
        asn1ev.add(ASN1Sequence.fromByteArray(Base64.decode(encSymPri)));
        // 加密公钥Q
        asn1ev.add(new DERBitString(xy));
        // 被对称密钥加密后的 加密私钥d
        asn1ev.add(new DERBitString(encEncPri));

        DERSequence derseq = new DERSequence(asn1ev);
        return Base64.toBase64String(derseq.getEncoded());
    }

    /**
     * GM/T 35276《SM2密码算法使用规范》
     * SM2密钥对的保护数据格式的ASN.1定义
     *
     * @param encsm2pristr 加密私钥
     * @param encsm2cert   加密证书
     * @param csr          签名证书
     * @param pwd          非对称算法私钥
     * @return 信封
     */
    public static String changePriEnvelopBy35276WithCSR(String encsm2pristr, String encsm2cert, String csr, byte[] pwd) throws IOException {
        byte[] sm2pri = Base64.decode(encsm2pristr);
        byte[] xy = getSm2PubKey(encsm2cert);
        if (sm2pri.length == 31) {
            byte[] sm2pri2 = new byte[32];
            sm2pri2[0] = 0;
            System.arraycopy(sm2pri, 0, sm2pri2, 1, 31);
            sm2pri = sm2pri2;
        } else if (sm2pri.length == 33) {
            if (sm2pri[0] == 0) {
                byte[] sm2pri2 = new byte[32];
                System.arraycopy(sm2pri, 1, sm2pri2, 0, 32);
                sm2pri = sm2pri2;
            }
        }

        //对称密钥加密
        byte[] cbp = symmetricEncryption(sm2pri, pwd, false, "");

        SubjectPublicKeyInfo spki = getPubInfoFromCSR(Base64.decode(csr));
        //公钥加密对称密钥pwd SM2Cipher
        String pubkeyenc = PubkeyEncryption(spki, pwd);

        ASN1EncodableVector asn1ev = new ASN1EncodableVector();

        ASN1ObjectIdentifier digestAlgOID = new ASN1ObjectIdentifier("1.2.156.10197.1.104.1");
        AlgorithmIdentifier algID = new AlgorithmIdentifier(digestAlgOID, DERNull.INSTANCE);

        asn1ev.add(algID);
        asn1ev.add(ASN1Sequence.fromByteArray(Base64.decode(pubkeyenc)));
        asn1ev.add(new DERBitString(xy));
        asn1ev.add(new DERBitString(cbp));

        DERSequence derseq = new DERSequence(asn1ev);
        return Base64.toBase64String(derseq.getEncoded());
    }

    /**
     * 基于GB/T 35291-2017 的私钥规范 (C语言结构)
     *
     * @param encSM2PriBase64 加密证书私钥
     * @param encSM2Cert      加密证书
     * @param signCert        签名证书
     * @param SymKey          对称密钥
     * @return 信封
     */
    public static String changePriEnvelopBy35291(String encSM2PriBase64, String encSM2Cert, String signCert, byte[] SymKey) throws Exception {
        byte[] sm2pri = null;
        try {
            sm2pri = Base64.decode(encSM2PriBase64);
            byte[] x = getSm2PubKey(encSM2Cert, 1);
            byte[] y = getSm2PubKey(encSM2Cert, 2);
            if (sm2pri.length == 31) {
                byte[] sm2pri2 = new byte[32];
                sm2pri2[0] = 0;
                System.arraycopy(sm2pri, 0, sm2pri2, 1, 31);
                sm2pri = sm2pri2;
            } else if (sm2pri.length == 33) {
                if (sm2pri[0] == 0) {
                    byte[] sm2pri2 = new byte[32];
                    System.arraycopy(sm2pri, 1, sm2pri2, 0, 32);
                    sm2pri = sm2pri2;
                }
            }
            byte[] cbp = symmetricEncryption(sm2pri, SymKey, false, "");
            byte[] cbEncryptedPriKey = new byte[64];
            System.arraycopy(cbp, 0, cbEncryptedPriKey, 32, 32);
            SubjectPublicKeyInfo pubInfoFromCert = getPubInfoFromCert(Base64.decode(signCert));
            String pubkeyenc = PubkeyEncryption(pubInfoFromCert, SymKey);
            byte[] encpx = getSm2PubkeyEncryption(pubkeyenc, 1);
            byte[] encpy = getSm2PubkeyEncryption(pubkeyenc, 2);
            byte[] hash = getSm2PubkeyEncryption(pubkeyenc, 3);
            byte[] encdata = getSm2PubkeyEncryption(pubkeyenc, 4);
            byte[] gmsm2propri = SKF_ENVELOPEDKEYBLOB(cbEncryptedPriKey, x, y, encpx, encpy, hash, encdata);
            return Base64.toBase64String(gmsm2propri);
        } catch (Exception e) {
            System.out.println(Hex.toHexString(sm2pri));
            throw e;
        }
    }

    /**
     * 基于GB/T 35291-2017 的私钥规范 (C语言结构)
     *
     * @param encSM2PriBase64 加密证书私钥
     * @param encSM2Cert      加密证书
     * @param csr             原始P10
     * @param SymKey          对称密钥
     * @return 信封
     */
    public static String GenGMSM2ProPriKeyWithCSR(String encSM2PriBase64, String encSM2Cert, String csr, byte[] SymKey) throws Exception {
        byte[] sm2pri = null;
        try {
            sm2pri = Base64.decode(encSM2PriBase64);
            byte[] x = getSm2PubKey(encSM2Cert, 1);
            byte[] y = getSm2PubKey(encSM2Cert, 2);
            if (sm2pri.length == 31) {
                byte[] sm2pri2 = new byte[32];
                sm2pri2[0] = 0;
                System.arraycopy(sm2pri, 0, sm2pri2, 1, 31);
                sm2pri = sm2pri2;
            } else if (sm2pri.length == 33) {
                if (sm2pri[0] == 0) {
                    byte[] sm2pri2 = new byte[32];
                    System.arraycopy(sm2pri, 1, sm2pri2, 0, 32);
                    sm2pri = sm2pri2;
                }
            }
            byte[] cbp = symmetricEncryption(sm2pri, SymKey, false, "");
            byte[] cbEncryptedPriKey = new byte[64];
            System.arraycopy(cbp, 0, cbEncryptedPriKey, 32, 32);
            SubjectPublicKeyInfo pubInfoFromCert = getPubInfoFromCSR(Base64.decode(csr));
            String pubkeyenc = PubkeyEncryption(pubInfoFromCert, SymKey);
            byte[] encpx = getSm2PubkeyEncryption(pubkeyenc, 1);
            byte[] encpy = getSm2PubkeyEncryption(pubkeyenc, 2);
            byte[] hash = getSm2PubkeyEncryption(pubkeyenc, 3);
            byte[] encdata = getSm2PubkeyEncryption(pubkeyenc, 4);
            byte[] gmsm2propri = SKF_ENVELOPEDKEYBLOB(cbEncryptedPriKey, x, y, encpx, encpy, hash, encdata);
            return Base64.toBase64String(gmsm2propri);
        } catch (Exception e) {
            System.out.println(Hex.toHexString(sm2pri));
            throw e;
        }
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
        if (pkcs7type.getId().equals("1.2.156.10197.6.1.4.2.4")) {
            KeyFactory kf = KeyFactory.getInstance("EC", new BouncyCastleProvider());
            //初始化sm2解密
            BCECPrivateKey pri = (BCECPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Hex.decode(priHex)));
            ECPrivateKeyParameters aPriv = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter(pri);
            SM2Engine sm2Engine = new SM2Engine();
            sm2Engine.init(false, aPriv);

            ASN1TaggedObject dtos = (ASN1TaggedObject) asn1_epk.getObjectAt(1);
            ASN1Sequence asn1_encdata = (ASN1Sequence) dtos.getBaseObject();
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
            byte[] sm4encdata = dstr_sm4encdata.getOctets();
            // sm4解密
            Cipher cipher = Cipher.getInstance("SM4/ECB/NoPadding", new BouncyCastleProvider());
            SecretKeySpec newKey = new SecretKeySpec(sm4key, "SM4");
            cipher.init(Cipher.DECRYPT_MODE, newKey);
            sm4encdata = cipher.doFinal(sm4encdata);
            return Hex.toHexString(Arrays.copyOfRange(sm4encdata, 32, sm4encdata.length));
        } else {
            throw new RuntimeException("无效的SM2数字信封格式");
        }
    }

    /**
     * 组成介质需要的保护私钥，GB/T 35291-2017 信息安全技术 智能密码钥匙应用接口规范
     *
     * @param cbEncryptedPriKey
     * @param pubKey_x
     * @param pubKey_y
     * @param eccCipherBlob_x
     * @param eccCipherBlob_y
     * @param hash
     * @param cipher
     * @return
     */
    public static byte[] SKF_ENVELOPEDKEYBLOB(byte[] cbEncryptedPriKey, byte[] pubKey_x, byte[] pubKey_y, byte[] eccCipherBlob_x, byte[] eccCipherBlob_y, byte[] hash, byte[] cipher) {
        byte[] version = intToBytes_r(1);
        byte[] ulSymmAlgID = intToBytes_r(0x00000401);
        byte[] ulBits = intToBytes_r(256);
        byte[] pubKey_bBitLen = intToBytes_r(256);
        byte[] CipherLen = intToBytes_r(16);
        byte[] b = null;
        b = byteMerger(version, ulSymmAlgID);
        b = byteMerger(b, ulBits);
        b = byteMerger(b, cbEncryptedPriKey);
        b = byteMerger(b, pubKey_bBitLen);
        b = byteMerger(b, pubKey_x);
        b = byteMerger(b, pubKey_y);
        b = byteMerger(b, eccCipherBlob_x);
        b = byteMerger(b, eccCipherBlob_y);
        b = byteMerger(b, hash);
        b = byteMerger(b, CipherLen);
        b = byteMerger(b, cipher);
        return b;
    }

    public static SubjectPublicKeyInfo getPubInfoFromCSR(byte[] p10) throws IOException {
        try {
            PublicKey publicKey = CsrUtil.getPub(p10);
            return SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static SubjectPublicKeyInfo getPubInfoFromCert(byte[] cert) throws IOException {
        ByteArrayInputStream bIn = new ByteArrayInputStream(cert);
        ASN1InputStream dIn = new ASN1InputStream(bIn);
        ASN1Sequence seq = (ASN1Sequence) dIn.readObject();
        Certificate tempcert = Certificate.getInstance(seq);
        return tempcert.getSubjectPublicKeyInfo();
    }

    /**
     * 公钥加密
     *
     * @param subjectPublicKeyInfo 公钥
     * @param data                 原文数据
     * @return 加密后的数据
     */
    @SuppressWarnings("deprecation")
    public static String PubkeyEncryption(SubjectPublicKeyInfo subjectPublicKeyInfo, byte[] data) {
        String rv = "";
        try {
            String certsignalg = subjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId();
            AlgorithmIdentifier keyAlg = subjectPublicKeyInfo.getAlgorithmId();
            if (certsignalg.equals("1.2.840.10045.2.1")) // ecc
            {
                boolean issm2 = false;
                // System.out.println(subjectPublicKeyInfo.getAlgorithm().getParameters().getClass().toString());
                if (subjectPublicKeyInfo.getAlgorithm().getParameters().getClass().toString().contains("ASN1ObjectIdentifier")) // sm2
                {
                    ASN1ObjectIdentifier pubkeyalgPoid = (ASN1ObjectIdentifier) subjectPublicKeyInfo
                            .getAlgorithm().getParameters();
                    if (pubkeyalgPoid.getId().equals("1.2.156.10197.1.301")) {
                        issm2 = true;
                    }
                }

                if (!issm2) // ecc
                {
                    throw new RuntimeException("Not supported by the algorithm");
                } else {
                    // 閸欐牕鍙曢柦锟�
                    SM2Engine sm2Engine = new SM2Engine();
                    X509EncodedKeySpec xspec = new X509EncodedKeySpec(
                            new DERBitString(subjectPublicKeyInfo).getBytes());
                    java.security.PublicKey pubkey = KeyFactory.getInstance(
                                    keyAlg.getAlgorithm().getId(), BC)
                            .generatePublic(xspec);
                    BCECPublicKey localECPublicKey = (BCECPublicKey) pubkey;
                    ECParameterSpec localECParameterSpec = localECPublicKey
                            .getParameters();
                    ECDomainParameters localECDomainParameters = new ECDomainParameters(
                            localECParameterSpec.getCurve(),
                            localECParameterSpec.getG(),
                            localECParameterSpec.getN());
                    ECPublicKeyParameters aPub = new ECPublicKeyParameters(
                            localECPublicKey.getQ(), localECDomainParameters);
                    sm2Engine.init(true, new ParametersWithRandom(aPub,
                            new SecureRandom()));
                    byte[] outdata = sm2Engine.processBlock(data, 0,
                            data.length);
                    byte[] x = new byte[32];
                    byte[] y = new byte[32];
                    byte[] encdata = new byte[outdata.length - 97];
                    byte[] hash = new byte[32];
                    System.arraycopy(outdata, 1, x, 0, x.length);
                    System.arraycopy(outdata, 1 + 32, y, 0, y.length);
                    System.arraycopy(outdata, 1 + 32 + 32, encdata, 0,
                            encdata.length);
                    System.arraycopy(outdata, 1 + 32 + 32 + encdata.length,
                            hash, 0, hash.length);
                    ASN1EncodableVector asn1ev = new ASN1EncodableVector();
                    BigInteger bigx = new BigInteger(x);
                    if (bigx.compareTo(new BigInteger("00", 16)) < 0) {
                        byte[] x1 = new byte[33];
                        x1[0] = 0;
                        System.arraycopy(x, 0, x1, 1, 32);
                        bigx = new BigInteger(x1);
                    }
                    BigInteger bigy = new BigInteger(y);
                    if (bigy.compareTo(new BigInteger("00", 16)) < 0) {
                        byte[] y1 = new byte[33];
                        y1[0] = 0;
                        System.arraycopy(y, 0, y1, 1, 32);
                        bigy = new BigInteger(y1);
                    }
                    ASN1Integer derix = new ASN1Integer(bigx);
                    ASN1Integer deriy = new ASN1Integer(bigy);
                    ASN1OctetString hashdata = new DEROctetString(hash);
                    ASN1OctetString enddata = new DEROctetString(encdata);
                    asn1ev.add(derix);
                    asn1ev.add(deriy);
                    asn1ev.add(hashdata);
                    asn1ev.add(enddata);
                    DERSequence derseq = new DERSequence(asn1ev);
                    rv = Base64.toBase64String(derseq.getEncoded());
                }
            } else if (certsignalg.equals("1.2.840.113549.1.1.1")) // rsa
            {
                X509EncodedKeySpec xspec = new X509EncodedKeySpec(
                        new DERBitString(subjectPublicKeyInfo).getBytes());
                java.security.PublicKey pubkey = KeyFactory.getInstance(
                        keyAlg.getAlgorithm().getId(), BC).generatePublic(
                        xspec);
                // RSA/None/NoPadding
                Cipher cipher = Cipher.getInstance("RSA/None/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, pubkey);
                byte[] outdata = cipher.doFinal(data);
                rv = Base64.toBase64String(outdata);
            } else // 閸忚泛鐣犵粻妤佺《
            {
                throw new RuntimeException("Not supported by the algorithm");
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 InvalidCipherTextException | InvalidKeySpecException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return rv;
    }

    /**
     * 对数据进行对称加密
     *
     * @param indata 数据
     * @param pwd    对称算法的密钥
     * @param alg    对称算法 3DES AES SM4
     * @return 加密值
     */
    public static byte[] symmetricEncryption(byte[] indata, byte[] pwd, boolean isPadding, String alg) {
        byte[] outdata = null;
        byte[] pkey = new byte[pwd.length];
        System.arraycopy(pwd, 0, pkey, 0, pwd.length);

        try {
            SecretKeySpec skeySpec = null;
            Cipher cipher = null;
            if (alg == null || alg.equals("")) {
                skeySpec = new SecretKeySpec(pwd, "SM4");
                if (isPadding) {
                    cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", BC); // NoPadding
                } else {
                    cipher = Cipher.getInstance("SM4/ECB/NoPadding", BC); // NoPadding
                }
            } else if (alg.toLowerCase().equals("des")) {
                skeySpec = new SecretKeySpec(pwd, "DES");
                if (isPadding) {
                    cipher = Cipher.getInstance("DES/ECB/PKCS5Padding", BC); // NoPadding
                } else {
                    cipher = Cipher.getInstance("DES/ECB/NoPadding", BC); // NoPadding
                }
            } else if (alg.toLowerCase().equals("aes")) {
                AESEngine aes = new AESEngine();
                BufferedBlockCipher cipherBC = new PaddedBufferedBlockCipher(aes, new PKCS7Padding());
                KeyParameter keyParam = new KeyParameter(pwd);
                CipherParameters params = (CipherParameters) new ParametersWithRandom(keyParam);
                cipherBC.reset();
                cipherBC.init(true, params);
                byte[] buf = new byte[cipherBC.getOutputSize(indata.length + 32)];
                int len = cipherBC.processBytes(indata, 0, indata.length, buf, 0);
                len += cipherBC.doFinal(buf, len);
                byte[] out = new byte[len];
                System.arraycopy(buf, 0, out, 0, len);
                outdata = out;
                return outdata;
            }
            SecureRandom sr = new SecureRandom();
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, sr);
            outdata = cipher.doFinal(indata);
        } catch (RuntimeException | InvalidCipherTextException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            System.arraycopy(pkey, 0, pwd, 0, pwd.length);
            throw new RuntimeException(e);
        }
        return outdata;
    }

    /**
     * 得到c++国密要的x或者y
     *
     * @param Base64Cert 证书
     * @param isxy       1 x 2 y
     * @return
     */
    public static byte[] getSm2PubKey(String Base64Cert, int isxy) throws Exception {
        byte[] rv = null;
        ByteArrayInputStream bIn = null;
        ASN1InputStream dIn = null;
        ByteArrayInputStream bIns = null;
        ASN1InputStream aIn1 = null;
        try {
            bIn = new ByteArrayInputStream(Base64.decode(Base64Cert.getBytes()));
            dIn = new ASN1InputStream(bIn);
            ASN1Sequence seq = (ASN1Sequence) dIn.readObject();
            Certificate tempcert = Certificate.getInstance(seq);
            SubjectPublicKeyInfo spki = tempcert.getSubjectPublicKeyInfo();
            String certsignalg = spki.getAlgorithm().getAlgorithm().getId();
            AlgorithmIdentifier keyAlg = spki.getAlgorithmId();
            if (certsignalg.equals("1.2.840.10045.2.1")) // ecc
            {
                boolean issm2 = false;
                // System.out.println(spki.getAlgorithm().getParameters().getClass().toString());
                if (spki.getAlgorithm().getParameters().getClass().toString().contains("ASN1ObjectIdentifier")) // sm2
                {
                    ASN1ObjectIdentifier pubkeyalgPoid = (ASN1ObjectIdentifier) spki
                            .getAlgorithm().getParameters();
                    if (pubkeyalgPoid.getId().equals("1.2.156.10197.1.301")) {
                        issm2 = true;
                    }
                }
                if (!issm2) // ecc
                {
                    throw new Exception("Not supported by the algorithm");
                } else {
                    byte[] xy = spki.getPublicKeyData().getBytes(); //.getOctets()
                    String xyHex = Hex.toHexString(xy);
                    System.out.println(String.format("%-16s", "xyHex>> ") + xyHex);
                    if (isxy == 1) {
                        byte[] x = new byte[64];
                        System.arraycopy(xy, 1, x, 32, 32);
                        return x;
                    } else if (isxy == 2) {
                        byte[] y = new byte[64];
                        System.arraycopy(xy, 1 + 32, y, 32, 32);
                        return y;
                    } else {
                        throw new Exception("Invalid parameter");
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (dIn != null) {
                try {
                    dIn.close();
                } catch (Exception e) {
                }
            }
            if (bIn != null) {
                try {
                    bIn.close();
                } catch (Exception e) {
                }
            }
            if (aIn1 != null) {
                try {
                    aIn1.close();
                } catch (Exception e) {
                }
            }
            if (bIns != null) {
                try {
                    bIns.close();
                } catch (Exception e) {
                }
            }
        }
        return rv;
    }

    /**
     * 解析sm2加密得到c++国密sm2要的参数
     *
     * @param Sm2PubKeyEncryption
     * @param type                1 x 2 y 3 hash 4 encdata
     */
    public static byte[] getSm2PubkeyEncryption(String Sm2PubKeyEncryption, int type) {
        byte[] rv = null;
        try {
            ASN1Sequence seq = (ASN1Sequence) ASN1Sequence.fromByteArray(Base64.decode(Sm2PubKeyEncryption));
            ASN1Integer x = (ASN1Integer) seq.getObjectAt(0);
            ASN1Integer y = (ASN1Integer) seq.getObjectAt(1);
            ASN1OctetString hash = (ASN1OctetString) seq.getObjectAt(2);
            ASN1OctetString encdata = (ASN1OctetString) seq.getObjectAt(3);
            if (type == 1) {
                byte[] xrev = new byte[64];
                if (x.getPositiveValue().toByteArray().length == 32) {
                    System.arraycopy(x.getPositiveValue().toByteArray(), 0,
                            xrev, 32, 32);
                    return xrev;
                } else if (x.getPositiveValue().toByteArray().length == 33) {
                    // System.out.println("XL："+x.getPositiveValue().toByteArray().length);
                    // System.out.println("X："+Hex.toHexString(x.getPositiveValue().toByteArray()));
                    System.arraycopy(x.getPositiveValue().toByteArray(), 0,
                            xrev, 31, 33);
                    return xrev;
                } else // 31
                {
                    // System.out.println("XL："+x.getPositiveValue().toByteArray().length);
                    // System.out.println("X："+Hex.toHexString(x.getPositiveValue().toByteArray()));
                    System.arraycopy(x.getPositiveValue().toByteArray(), 0,
                            xrev, 33, 31);
                    return xrev;
                }
            } else if (type == 2) {
                byte[] yrev = new byte[64];
                if (y.getPositiveValue().toByteArray().length == 32) {
                    System.arraycopy(y.getPositiveValue().toByteArray(), 0,
                            yrev, 32, 32);
                    return yrev;
                } else if (y.getPositiveValue().toByteArray().length == 33) {
                    // System.out.println("YL："+y.getPositiveValue().toByteArray().length);
                    // System.out.println("Y："+Hex.toHexString(y.getPositiveValue().toByteArray()));
                    System.arraycopy(y.getPositiveValue().toByteArray(), 0,
                            yrev, 31, 33);
                    return yrev;
                } else // 31
                {
                    // System.out.println("YL："+y.getPositiveValue().toByteArray().length);
                    // System.out.println("Y："+Hex.toHexString(y.getPositiveValue().toByteArray()));
                    System.arraycopy(y.getPositiveValue().toByteArray(), 0,
                            yrev, 33, 31);
                    return yrev;
                }
            } else if (type == 3) {
                return hash.getOctets();
            } else if (type == 4) {
                return encdata.getOctets();
            } else {
                throw new RuntimeException("Invalid parameter");
            }

        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] intToBytes_r(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return reverseBytes(b);
    }

    public static byte[] reverseBytes(byte[] s) {
        byte[] r = new byte[s.length];
        for (int i = 0; i < s.length; i++) {
            r[i] = s[s.length - 1 - i];
        }
        return r;
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    /**
     * 得到c++国密要的x或者y
     *
     * @param Base64Cert 证书
     * @return
     */
    public static byte[] getSm2PubKey(String Base64Cert) {
        byte[] rv = null;
        ByteArrayInputStream bIn = null;
        ASN1InputStream dIn = null;
        ByteArrayInputStream bIns = null;
        ASN1InputStream aIn1 = null;
        try {
            bIn = new ByteArrayInputStream(Base64.decode(Base64Cert.getBytes()));
            dIn = new ASN1InputStream(bIn);
            ASN1Sequence seq = (ASN1Sequence) dIn.readObject();
            Certificate tempcert = Certificate.getInstance(seq);
            SubjectPublicKeyInfo spki = tempcert.getSubjectPublicKeyInfo();
            String certsignalg = spki.getAlgorithm().getAlgorithm().getId();
            AlgorithmIdentifier keyAlg = spki.getAlgorithmId();
            if (certsignalg.equals("1.2.840.10045.2.1")) // ecc
            {
                boolean issm2 = false;
                // System.out.println(spki.getAlgorithm().getParameters().getClass().toString());
                if (spki.getAlgorithm().getParameters().getClass().toString()
                        .indexOf("ASN1ObjectIdentifier") != -1) // sm2
                {
                    ASN1ObjectIdentifier pubkeyalgPoid = (ASN1ObjectIdentifier) spki
                            .getAlgorithm().getParameters();
                    if (pubkeyalgPoid.getId().equals("1.2.156.10197.1.301")) {
                        issm2 = true;
                    }
                }
                if (!issm2) // ecc
                {
                    throw new RuntimeException("Not supported by the algorithm");
                } else {
                    byte[] xy = spki.getPublicKeyData().getBytes(); //.getOctets()
                    return xy;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (dIn != null) {
                try {
                    dIn.close();
                } catch (Exception e) {
                }
            }
            if (bIn != null) {
                try {
                    bIn.close();
                } catch (Exception e) {
                }
            }
            if (aIn1 != null) {
                try {
                    aIn1.close();
                } catch (Exception e) {
                }
            }
            if (bIns != null) {
                try {
                    bIns.close();
                } catch (Exception e) {
                }
            }
        }
        return rv;
    }

    /**
     * 解MSCA大陆云盾RSA信封
     *
     * @param encryptedPrivateKey 加密证书私钥（被对称密钥加了密的）
     * @param encryptedSessionKey 对称密钥（被签名公钥加了密的）
     * @param signPri             签名私钥
     * @param paddingMode         填充方式
     * @return 加密证书私钥
     */
    public static byte[] decodeMscaRSAEnveloped(String encryptedPrivateKey, String encryptedSessionKey, String signPri, String paddingMode) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        // 从pkcs10中获取RSA公钥
        // KeyFactory keyFact = KeyFactory.getInstance("RSA", BC);
        // PKCS10CertificationRequest p10 = new PKCS10CertificationRequest(Base64.decode(pkcs10));
        // BCRSAPublicKey rsaPublicKey = (BCRSAPublicKey) keyFact.generatePublic(new X509EncodedKeySpec(p10.getSubjectPublicKeyInfo().getEncoded()));

        // 解密
        KeyFactory kf = KeyFactory.getInstance("RSA", BC);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(signPri)));

        // 解密sessionKey
        // 使用 PKCS1Encoding 和 RSAEngine 进行解密
        // RSAKeyParameters privateKeyParam = (RSAKeyParameters) PrivateKeyFactory.createKey(rsaPrivateKey.getEncoded());
        // AsymmetricBlockCipher rsaEngine = new PKCS1Encoding(new RSAEngine());
        // rsaEngine.init(false, privateKeyParam); // false 表示解密模式
        // byte[] decryptData = rsaEngine.processBlock(Base64.decode(encryptedSessionKey), 0, Base64.decode(encryptedSessionKey).length);
        if (paddingMode == null || paddingMode.isEmpty()) {
            paddingMode = "PKCS1Padding";
        }
        Cipher cipher = Cipher.getInstance("RSA/ECB/" + paddingMode, BC);
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decryptData = cipher.doFinal(Base64.decode(encryptedSessionKey));

        // 解密encPri
        Cipher instance = Cipher.getInstance("RC4");
        SecretKeySpec rc4key = new SecretKeySpec(decryptData, "RC4");
        instance.init(Cipher.DECRYPT_MODE, rc4key);
        return instance.doFinal(Base64.decode(encryptedPrivateKey));
    }

}
