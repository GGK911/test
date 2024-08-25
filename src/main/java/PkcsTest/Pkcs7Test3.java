package PkcsTest;

import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/22 15:46
 */
public class Pkcs7Test3 {
    private static final Provider BC = new BouncyCastleProvider();

    @Test
    @SneakyThrows
    public void changeEnvelopTest() {
        // String p10 = "MIHXMHwCAQAwHDELMAkGA1UEBhMCQ04xDTALBgNVBAMMBHRlc3QwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQbaP1Hax6URcruf17/4ZNldMilXgrZtqK5K6VKYVyaoebVFr1Gl8Xy6jCghVx8in5+wnkGGc9N2/fUIX5+XjwuMAwGCCqBHM9VAYN1BQADSQAwRgIhAKzcBLKPcABK/7uAfhIAGPAvRTqous9fnPhzcOZfrTAPAiEAn8DaVCwR+eqtCFyebDijcdnammbCUPa5FYfx867XVRg=";
        // SubjectPublicKeyInfo pubInfoFromCSR = GMSM2ProPriKey.getPubInfoFromCSR(Base64.decode(p10));

        byte[] randomKey = "1234567812345678".getBytes();

        String signCert = "MIIC2zCCAn6gAwIBAgIQRqY8jkgIwf74VOiAqcNT+zAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjMwODE3MDQwNzI1WhcNMjYwODE2MDQwNzI1WjCBhDELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBI5MTYxMDAwMDU1NTY5Mzk0N1IxNjA0BgNVBAMMLTEyNTQyOTExMTc3MDE2NjA2NzJA5rWL6K+VU00y5Y+M6K+B5LmmQDAxQDAwMzBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABMU3x2yKHkbV7FPrvXIJAOM+W4ifAMeCAfGeC5PxnA2o3b9cZSTuaHOOrPQdSfbBESkQ1qJcSu4ij7jG4YogQVCjggEkMIIBIDAdBgNVHQ4EFgQUm/FFFTlRC6b3eWebZR1WK1GlzwQwEgYDVR0lBAswCQYHKoEch4QLATCBvAYDVR0fBIG0MIGxMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDEuY3JsMH+gfaB7hnlsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjoxMDM4OS9DTj1jcmwxLE9VPUNSTCxPPU1DU0NBLEM9Q04/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXNlP29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MB8GA1UdIwQYMBaAFLkj5hx/z7c8WCRbpAqtihGXenu2MAsGA1UdDwQEAwIGwDAMBggqgRzPVQGDdQUAA0kAMEYCIQDU2i1y3b+GdUasHnwSF6As2tcGdi6iEs41ro+ale49VgIhAIzwjIEcWbV30XUsjppWqHceIaPO+Z+V0Nc9KrllgITj";

        String encCert = "MIIC2jCCAn6gAwIBAgIQf0v8x9dk9lERF3YZLBaRyTAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjMwODE3MDQwNzI1WhcNMjYwODE2MDQwNzI1WjCBhDELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBI5MTYxMDAwMDU1NTY5Mzk0N1IxNjA0BgNVBAMMLTEyNTQyOTExMTc3MDE2NjA2NzJA5rWL6K+VU00y5Y+M6K+B5LmmQDAxQDAwMzBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABC/wIZBsEqzE724aUtatVrwxJ9TTPujrul4pzp45bODeiqquagkZvt+sIL3ovJIQZ3GbXR/Naze3KyJmB4u/4+2jggEkMIIBIDALBgNVHQ8EBAMCBDAwHQYDVR0OBBYEFCExvX161vubQfOP69HtCLXjgPR6MBIGA1UdJQQLMAkGByqBHIeECwEwgbwGA1UdHwSBtDCBsTAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwxLmNybDB/oH2ge4Z5bGRhcDovL3d3dy5tY3NjYS5jb20uY246MTAzODkvQ049Y3JsMSxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAfBgNVHSMEGDAWgBS5I+Ycf8+3PFgkW6QKrYoRl3p7tjAMBggqgRzPVQGDdQUAA0gAMEUCIBJgMnNLx6vEo1F/8o/ICVYRMfQqVegUwXdIsR9IZOYjAiEA8HepM3/Fuc5IgwQ6WIzSR1JwwwAFW/fpE4N9Ujfwmao=";
        String doubleEncryptedPrivateKey = "MIIEfAYKKoEcz1UGAQQCBKCCBGwwggRoAgEBMYHTMIHQAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQJL6LYxt2rJxHzy9QQ6lY7TALBgkqgRzPVQGCLQMEezB5AiEA5qXWOQMPOQt1i6CE/3YeXdFTEQCr85m8ndGLjrcAc50CIGNO6zyELj0pufvQsucrpiwrxx437Tov8hVrg18iSdatBCA72n21MKl38PTRYumb1Vn7uWC9SsrbeX8Zv8Djt54g5gQQpyQIybyX8Cf36k+u2egxizEMMAoGCCqBHM9VAYMRMFkGCiqBHM9VBgEEAgEwCQYHKoEcz1UBaIBA7RBGmvV+N4qkAdm+rC7r5u0QRpr1fjeKpAHZvqwu6+aREOJ01ZDYRmXSO0MdscPBBGzderHUuxfmRir8JBHykKCCAnQwggJwMIICFKADAgECAhAt64hF75RzsvrjdE+iVl8GMAwGCCqBHM9VAYN1BQAwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0xOTA1MDgxMDMxNDNaFw0yMDA1MDcxMDMxNDNaMC8xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEQMA4GA1UEAwwHU0lOR1NNMjBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABMQDm0UOlzNbgi52CXg4ujvewzQGVm4Nm5GLF+j3Gh6pcvmNydPYPE6mxXCzz8tET7KPj9CAndabURlVZprItQGjggEQMIIBDDAfBgNVHSMEGDAWgBS/tiuJjDBsJSCLWWc4G038VxCMczAdBgNVHQ4EFgQUvCxwTFydcvF6+B9tvFV109yPXe8wCwYDVR0PBAQDAgTwMIG8BgNVHR8EgbQwgbEwL6AtoCuGKWh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMTYuY3JsMH6gfKB6hnhsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMTYsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDAYIKoEcz1UBg3UFAANIADBFAiEAqfuL4Lys2LCzFdqoMgt+bM0lj4iT/SjZ9Gko/LMK3acCIEMDOOJPFvYgpjxNsNgQ/Tl8vYNxgy0w++/OFIKyruNBMYGrMIGoAgEBMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQLeuIRe+Uc7L643RPolZfBjAKBggqgRzPVQGDETALBgkqgRzPVQGCLQEERzBFAiEA6iZSplmz/OB9IHZZ1wPgNUkplHf6/LdOmQ9AEMQjj3wCIGqbv+MEDwlcOT8k6/VLnBJjbz6Gv42myPXTh/oEdFyx";
        String sm2pri = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIE0RcwRNxHgwg2b/RRoZFQmzMFVL9bA2podObPqsCvSzoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";

        sm2pri = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgmyFEjK2ncG/wI1+LdCyw2GOjJbAHwMinqdzSrmfqHZ+gCgYIKoEcz1UBgi2hRANCAAS2kvU4DfcPAwWE0YR5PMJcBxfEKD0FZplm6B61pPY80FiBjo0h8DDGgy1/GKvI8FB23FilKeGC/TTA/90EEAht";
        encCert = "MIICuTCCAlygAwIBAgIQVLqJE+asfKP3UolHJxctGDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwODIzMDM0MzI5WhcNMjQwODI0MDM0MzI5WjB5MQswCQYDVQQGEwJDTjEMMAoGA1UECgwD5pegMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE3NjUyMjUyMjMwMDQ3MDA2NzJA5ZSQ5aW95YevQDAxQDA2NDBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABDqOp+hmXYHFGHUdo0ACSDlVYwDUR7B1PI+NGh6BuAUQ4fIlfhKyWI4sijEKNLfs0bdT+VhVRN0leRZ590uUcSOjggEOMIIBCjALBgNVHQ8EBAMCBDAwgboGA1UdHwSBsjCBrzAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwwLmNybDB9oHugeYZ3bGRhcDovL3d3dy5tY3NjYS5jb20uY246Mzg5L0NOPWNybDAsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwHQYDVR0OBBYEFF8Zg3C5/c1qlb4upRJ5oqDVgQ6IMB8GA1UdIwQYMBaAFPEiCmeYjfXjsqrDF2vAQh++S712MAwGCCqBHM9VAYN1BQADSQAwRgIhAMXwjE1wq7BafC1NK/QTucVPDVDXTCJL8xYfhMCxjDC/AiEAnmHg4uxBHvix5sansjZ021TUjcaJ11ML1WxigS/ce3g=";
        doubleEncryptedPrivateKey = "MIIEQwYKKoEcz1UGAQQCBKCCBDMwggQvAgEBMYHUMIHRAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQbRMmbHA6mnSAad15ifd0kjALBgkqgRzPVQGCLQMEfDB6AiEAt+EDP4qmmZt5xXSklqA6PLQ9DoJ0XUIk06FOqtbcQg0CIQCwf5SKEQ5npPDMle/kJpy922P1u1KdMUDDEW46SjDvFgQgaT8/9nLJJOX5gILm3p4jIHZlPVKCISlq+VpIL8SkKV4EEG591JlxBwtEiFdFMS6lnWAxDDAKBggqgRzPVQGDETBZBgoqgRzPVQYBBAIBMAkGByqBHM9VAWiAQJv/z6La4PqTwvKJrcTUgJ6b/8+i2uD6k8Lyia3E1ICe873BHaVdYe2pZLjqppb0LOZkPaUG2AFsPT8wnhpl/mygggHvMIIB6zCCAY+gAwIBAgIQZhpTteVSMDmossSeqZ2nlDAMBggqgRzPVQGDdQUAMHgxCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAlDaG9uZ3FpbmcxOjA4BgNVBAoMMUVhc3QtWmhvbmd4dW4gQ2VydGlmaWNhdGUgQXV0aG9yaXR5IENlbnRlciBDTy5MVEQxGTAXBgNVBAMMEEVhc3QtWmhvbmd4dW4gQ0EwHhcNMjMwODE3MDEyODMzWhcNMzMwODE0MDEyODMzWjAvMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAMMB1NNMlNJR04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQgOmy4mEBDeZmqg7a+q35qoOf7/hoWR5SA3aEP0lkPh3N0nnUV3DGrOaFnwVAGfjZKNp3NftSzFrESYuA4A69+o0IwQDAfBgNVHSMEGDAWgBQJ7rsH/h/eV5iSbu4/y1JqS/kmHTAdBgNVHQ4EFgQUiajqAu5ieONRz99oYPAphmy6XswwDAYIKoEcz1UBg3UFAANIADBFAiAZ52RxsV3HOwK+ls3dzSfvA2uU8CvCZ/Ce0vKeMbD5sAIhAJlu5Xq3TNQOvu1uARHy4evSf3wvnfh888dof6IViykJMYH2MIHzAgEBMIGMMHgxCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAlDaG9uZ3FpbmcxOjA4BgNVBAoMMUVhc3QtWmhvbmd4dW4gQ2VydGlmaWNhdGUgQXV0aG9yaXR5IENlbnRlciBDTy5MVEQxGTAXBgNVBAMMEEVhc3QtWmhvbmd4dW4gQ0ECEGYaU7XlUjA5qLLEnqmdp5QwCgYIKoEcz1UBgxEwCwYJKoEcz1UBgi0BBEYwRAIgMdoCbXmyth0F9kMYmHjG2LoM6WSkLxDdAb85kaMuETgCICpBvqmGFFquPuQwAz8KmF17sJAomYRfXW+r6XeaZgT8";

        signCert = "MIICxjCCAmqgAwIBAgIQKLme/yTIj1ieMAe781CJeDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwODIzMDYyNjAyWhcNMjQwODI0MDYyNjAyWjB5MQswCQYDVQQGEwJDTjEMMAoGA1UECgwD5pegMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE4MjY2MDE0MTQzMzY0NzEwNDBA5ZSQ5aW95YevQDAxQDA3MjBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABI1ailYUAnJzFB1CQgWuUVoLaKsHgiErlpOGwK71fO4DEF2QCY7w6KVwf0bZpzvrNe73SnsLHdmRyryB/U+OWK6jggEcMIIBGDAfBgNVHSMEGDAWgBTxIgpnmI3147KqwxdrwEIfvku9djAdBgNVHQ4EFgQUYXvOCHLQXC/N8NZB7Yq+H44NWNgwDAYDVR0TBAUwAwEBADALBgNVHQ8EBAMCBPAwgboGA1UdHwSBsjCBrzAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwwLmNybDB9oHugeYZ3bGRhcDovL3d3dy5tY3NjYS5jb20uY246Mzg5L0NOPWNybDAsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDAYIKoEcz1UBg3UFAANIADBFAiEApPGxmLP3PXg+gyzsB5J0BDwV+J/gYGtfpBJzSlA9tZECIBD8XVCC7y6aNz4tgQmARcu1knmi5ygcKE4IA70Zm7aH";

        // 中正的
        sm2pri = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQg6hdS3AX+HlUyW3JuOAqa8PgGMJTGfKB+apxLyaWprPSgCgYIKoEcz1UBgi2hRANCAAQtiLx2WE7zfFVkv9WEg6LQINWHUfl+2utow84e0RVYxS1GiY74I/LQER8k4zKO1xxKKku2A/0sMV5swP2wqe9T";
        encCert = "MIIDOjCCAt2gAwIBAgIQMuCVdpv9FHaCPjrdDP7EEDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwODIzMDYzNDE3WhcNMjQwODI0MDYzNDE3WjB5MQswCQYDVQQGEwJDTjEMMAoGA1UECgwD5pegMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE4MjY2MDE0MTQzMzY0NzEwNDBA5ZSQ5aW95YevQDAxQDA3NTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABEMSN5FQKaxq807ULcT7tDk8DAO2dG9IYnRbcKJiVwRVPxGwAXQeo6Gxr1flheLbFyyTRNVY7X3Rq6OMHokw+b6jggGPMIIBizBcBggrBgEFBQcBAQRQME4wKAYIKwYBBQUHMAKGHGh0dHA6Ly8xMjcuMC4wLjEvY2Fpc3N1ZS5odG0wIgYIKwYBBQUHMAGGFmh0dHA6Ly8xMjcuMC4wLjE6MjA0NDMwHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwCwYDVR0PBAQDAgQwMIG6BgNVHR8EgbIwga8wLqAsoCqGKGh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMC5jcmwwfaB7oHmGd2xkYXA6Ly93d3cubWNzY2EuY29tLmNuOjM4OS9DTj1jcmwwLE9VPUNSTCxPPU1DU0NBLEM9Q04/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXNlP29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MBMGA1UdJQQMMAoGCCsGAQUFBwMBMB0GA1UdDgQWBBQ2CNIDmtmLHuiCMTJ+BG+wdduxPTAMBgNVHRMEBTADAQEAMAwGCCqBHM9VAYN1BQADSQAwRgIhAOzRg05aTNdx75JDwrmV4tFjndEQ9vu15RV0IDCfUWTlAiEAiA8kXT+sw1gXMc4t3iPwcMWAcMmw8A/E4RRMPU+gstI=";
        doubleEncryptedPrivateKey = "MIIEQgYKKoEcz1UGAQQCBKCCBDIwggQuAgEBMYHSMIHPAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQRk90f35KALLTzBT0e4vmxzALBgkqgRzPVQGCLQMEejB4AiBhw5gZzKmimKYrZEq2mnUQNzEAPYHLn7mqcO/c30QQXAIgP12Htu3/P4x0XvMN2Nt9NMkCGufKvd9Khx7D4Y8dDIYEIMiR5zH6TwfjKoQ6Jkmc19lVZkbsjTn86vdQpX/e4mMLBBCbnBHdlMe5Dhfg2PZ1dRpDMQwwCgYIKoEcz1UBgxEwWQYKKoEcz1UGAQQCATAJBgcqgRzPVQFogEDNXexoTXqaSLdfEmsvVHMDzV3saE16mki3XxJrL1RzAxIfWZu+lDv8CIA/o97ThUM5Ba6Pjo5QG1GmiojcH6etoIIB7zCCAeswggGPoAMCAQICEGYaU7XlUjA5qLLEnqmdp5QwDAYIKoEcz1UBg3UFADB4MQswCQYDVQQGEwJDTjESMBAGA1UECAwJQ2hvbmdxaW5nMTowOAYDVQQKDDFFYXN0LVpob25neHVuIENlcnRpZmljYXRlIEF1dGhvcml0eSBDZW50ZXIgQ08uTFREMRkwFwYDVQQDDBBFYXN0LVpob25neHVuIENBMB4XDTIzMDgxNzAxMjgzM1oXDTMzMDgxNDAxMjgzM1owLzELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMRAwDgYDVQQDDAdTTTJTSUdOMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEIDpsuJhAQ3mZqoO2vqt+aqDn+/4aFkeUgN2hD9JZD4dzdJ51FdwxqzmhZ8FQBn42SjadzX7UsxaxEmLgOAOvfqNCMEAwHwYDVR0jBBgwFoAUCe67B/4f3leYkm7uP8tSakv5Jh0wHQYDVR0OBBYEFImo6gLuYnjjUc/faGDwKYZsul7MMAwGCCqBHM9VAYN1BQADSAAwRQIgGedkcbFdxzsCvpbN3c0n7wNrlPArwmfwntLynjGw+bACIQCZbuV6t0zUDr7tbgER8uHr0n98L534fPPHaH+iFYspCTGB9zCB9AIBATCBjDB4MQswCQYDVQQGEwJDTjESMBAGA1UECAwJQ2hvbmdxaW5nMTowOAYDVQQKDDFFYXN0LVpob25neHVuIENlcnRpZmljYXRlIEF1dGhvcml0eSBDZW50ZXIgQ08uTFREMRkwFwYDVQQDDBBFYXN0LVpob25neHVuIENBAhBmGlO15VIwOaiyxJ6pnaeUMAoGCCqBHM9VAYMRMAsGCSqBHM9VAYItAQRHMEUCIBJp+nXPGYPGcPChl4DM8DVJyDj8NQArfMpO//bVrXTXAiEAtHIjHGV1bTn9NXdHT+YzBeNhPXH5p5lXVKNt+SZI9zg=";

        String dHex = CsrUtil.analysisSM2EncKey(doubleEncryptedPrivateKey, Hex.toHexString(Base64.decode(sm2pri)));
        System.out.println(String.format("%-16s", "dHex>> ") + dHex);

        String gmPriKey = GenGMSM2ProPriKey(Base64.toBase64String(Hex.decode(dHex)), encCert, signCert, randomKey);
        System.out.println(String.format("%-16s", "gmPriKey>> ") + gmPriKey);

    }

    /**
     * @param encSM2PriBase64 加密证书私钥
     * @param encSM2Cert      加密证书
     * @param signCert        签名证书
     * @param SymKey          对称密钥
     * @return
     */
    public static String GenGMSM2ProPriKey(String encSM2PriBase64, String encSM2Cert, String signCert, byte[] SymKey) throws Exception {
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
     * @param encSM2PriBase64 加密证书私钥
     * @param encSM2Cert      加密证书
     * @param CSR             原始P10
     * @param SymKey          对称密钥
     * @return
     */
    public static String GenGMSM2ProPriKeyWithCSR(String encSM2PriBase64, String encSM2Cert, String CSR, byte[] SymKey) throws Exception {
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
            SubjectPublicKeyInfo pubInfoFromCert = getPubInfoFromCSR(Base64.decode(CSR));
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
            PublicKey publicKey = certTest.CsrUtil.getPub(p10);
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
     * @param spki 公钥
     * @param data 原文数据
     * @return 加密后的数据
     */
    @SuppressWarnings("deprecation")
    public static String PubkeyEncryption(SubjectPublicKeyInfo spki, byte[] data) throws Exception {
        String rv = "";
        try {
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
                    // 閸欐牕鍙曢柦锟�
                    SM2Engine sm2Engine = new SM2Engine();
                    X509EncodedKeySpec xspec = new X509EncodedKeySpec(
                            new DERBitString(spki).getBytes());
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
                        new DERBitString(spki).getBytes());
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
                throw new Exception("Not supported by the algorithm");
            }
        } catch (Exception e) {
            throw e;
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
    public static byte[] symmetricEncryption(byte[] indata, byte[] pwd, boolean isPadding, String alg) throws Exception {
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
        } catch (Exception e) {
            System.arraycopy(pkey, 0, pwd, 0, pwd.length);
            throw e;
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
    public static byte[] getSm2PubkeyEncryption(String Sm2PubKeyEncryption, int type) throws Exception {
        byte[] rv = null;
        try {
            ASN1Sequence seq = (ASN1Sequence) ASN1Sequence.fromByteArray(Base64
                    .decode(Sm2PubKeyEncryption));
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
                throw new Exception("Invalid parameter");
            }

        } catch (Exception e) {
            throw e;
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

}
