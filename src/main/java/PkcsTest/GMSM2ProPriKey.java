package PkcsTest;

import certTest.CsrUtil;
import certTest.pkiCoreTest.SM4;
import certTest.pkiCoreTest.SM4_Context;
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
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class GMSM2ProPriKey {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            String sm2pubkey = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEtpL1OA33DwMFhNGEeTzCXAcXxCg9BWaZZugetaT2PNBYgY6NIfAwxoMtfxiryPBQdtxYpSnhgv00wP/dBBAIbQ==";
            String sm2prikey = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgmyFEjK2ncG/wI1+LdCyw2GOjJbAHwMinqdzSrmfqHZ+gCgYIKoEcz1UBgi2hRANCAAS2kvU4DfcPAwWE0YR5PMJcBxfEKD0FZplm6B61pPY80FiBjo0h8DDGgy1/GKvI8FB23FilKeGC/TTA/90EEAht";
            String p10 = "MIHHMHACAQAwEDEOMAwGA1UEAwwFTUNTQ0EwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAS2kvU4DfcPAwWE0YR5PMJcBxfEKD0FZplm6B61pPY80FiBjo0h8DDGgy1/GKvI8FB23FilKeGC/TTA/90EEAhtMAoGCCqBHM9VAYN1A0cAMEQCIBzvHeZODnoqLsll+xqM/SqNz1afQfWWe4iRhhqPLC31AiBEUcBtGg3Zar62Jli4KsLHtoK8TsTW2CVfI3XatpsKUQ==";
            String signCert = "MIIDOzCCAt6gAwIBAgIQWypHZ6XQN/nfWv1ovEP8GjAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjMxMTMwMDM1MDQyWhcNMjYxMTI5MDM1MDQyWjB6MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjkxNjEwMDAwNTU1NjkzOTQ3UjEsMCoGA1UEAwwjVHBlclNNMngyQOa1i+ivlVNNMuWPjOivgeS5pkAwMUAwMzcwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAS2kvU4DfcPAwWE0YR5PMJcBxfEKD0FZplm6B61pPY80FiBjo0h8DDGgy1/GKvI8FB23FilKeGC/TTA/90EEAhto4IBjzCCAYswCwYDVR0PBAQDAgbAMFwGCCsGAQUFBwEBBFAwTjAoBggrBgEFBQcwAoYcaHR0cDovLzEyNy4wLjAuMS9jYWlzc3VlLmh0bTAiBggrBgEFBQcwAYYWaHR0cDovLzEyNy4wLjAuMToyMDQ0MzAfBgNVHSMEGDAWgBTxIgpnmI3147KqwxdrwEIfvku9djCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDAuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMCxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDATBgNVHSUEDDAKBggrBgEFBQcDATAdBgNVHQ4EFgQUAm/JRCe9jKOVmgCq2FAkTzROn0YwDAYDVR0TBAUwAwEBADAMBggqgRzPVQGDdQUAA0kAMEYCIQCksMQbrpbsFGBjS+XncOUR/G1D/Q8rPdkBxBrdK3B0nQIhAI0ED4hqpx14SqImXfRAKCv4L3aXePUKyHrkBEWP4szl";
            String encCert = "MIIDOzCCAt6gAwIBAgIQTGgp3hYPVwPiELgkQKfgbzAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjMxMTMwMDM1MDQyWhcNMjYxMTI5MDM1MDQyWjB6MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjkxNjEwMDAwNTU1NjkzOTQ3UjEsMCoGA1UEAwwjVHBlclNNMngyQOa1i+ivlVNNMuWPjOivgeS5pkAwMUAwMzcwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAATlbj0mjBpd8cHXjKy9EJ2qP+TMnMOHbhHWCBzoqdCcReirKSvgxai894LaeLpmo/yLWMXJGJuQQ2GZfChXkwLJo4IBjzCCAYswXAYIKwYBBQUHAQEEUDBOMCgGCCsGAQUFBzAChhxodHRwOi8vMTI3LjAuMC4xL2NhaXNzdWUuaHRtMCIGCCsGAQUFBzABhhZodHRwOi8vMTI3LjAuMC4xOjIwNDQzMB8GA1UdIwQYMBaAFPEiCmeYjfXjsqrDF2vAQh++S712MAsGA1UdDwQEAwIEMDCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDAuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMCxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDATBgNVHSUEDDAKBggrBgEFBQcDATAdBgNVHQ4EFgQURq8KjbwqBl/yG6V3a4cWX/Y08H4wDAYDVR0TBAUwAwEBADAMBggqgRzPVQGDdQUAA0kAMEYCIQDTCsDF3BFGYYBLV1gHGIvrogRYmsoq3JrA09bi1rml/QIhANuH4DfL575c9WVbomcFjCd+ty748Qx53PeUg38qzu3r";
            String doubleEncryptedPrivateKey = "MIIERQYKKoEcz1UGAQQCBKCCBDUwggQxAgEBMYHUMIHRAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQWypHZ6XQN/nfWv1ovEP8GjALBgkqgRzPVQGCLQMEfDB6AiEAms4Jzi3tq4KpXr6jgr7QQjlTVrJ5Vp5iw+UsOFvtk8sCIQC8UtXdalPw/+SaMdEKOsT0IuXZXXk/mNMx5TrkK1AdWQQgzuqdnnsH3rXIXNUAflTPyivuCMrYX4xLTxVWw+cq9kgEEOfJDJ7u+pRfwO39haIHRhUxDDAKBggqgRzPVQGDETBZBgoqgRzPVQYBBAIBMAkGByqBHM9VAWiAQFz16/Wr3wwIyuzKxQ5aTVBc9ev1q98MCMrsysUOWk1Q+F6NivwjcQMD7BvIkaTrgnDTuj7iTWQIeSwM2Cyoh8GgggHvMIIB6zCCAY+gAwIBAgIQZhpTteVSMDmossSeqZ2nlDAMBggqgRzPVQGDdQUAMHgxCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAlDaG9uZ3FpbmcxOjA4BgNVBAoMMUVhc3QtWmhvbmd4dW4gQ2VydGlmaWNhdGUgQXV0aG9yaXR5IENlbnRlciBDTy5MVEQxGTAXBgNVBAMMEEVhc3QtWmhvbmd4dW4gQ0EwHhcNMjMwODE3MDEyODMzWhcNMzMwODE0MDEyODMzWjAvMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAMMB1NNMlNJR04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQgOmy4mEBDeZmqg7a+q35qoOf7/hoWR5SA3aEP0lkPh3N0nnUV3DGrOaFnwVAGfjZKNp3NftSzFrESYuA4A69+o0IwQDAfBgNVHSMEGDAWgBQJ7rsH/h/eV5iSbu4/y1JqS/kmHTAdBgNVHQ4EFgQUiajqAu5ieONRz99oYPAphmy6XswwDAYIKoEcz1UBg3UFAANIADBFAiAZ52RxsV3HOwK+ls3dzSfvA2uU8CvCZ/Ce0vKeMbD5sAIhAJlu5Xq3TNQOvu1uARHy4evSf3wvnfh888dof6IViykJMYH4MIH1AgEBMIGMMHgxCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAlDaG9uZ3FpbmcxOjA4BgNVBAoMMUVhc3QtWmhvbmd4dW4gQ2VydGlmaWNhdGUgQXV0aG9yaXR5IENlbnRlciBDTy5MVEQxGTAXBgNVBAMMEEVhc3QtWmhvbmd4dW4gQ0ECEGYaU7XlUjA5qLLEnqmdp5QwCgYIKoEcz1UBgxEwCwYJKoEcz1UBgi0BBEgwRgIhALaQhwbSmzh1uRRDoqbGMAKkhpvzJjdLk3Hrz9VV3DAQAiEAq76SOc5m41SvLi10lVNVszHFhWYuyRGj9oTDm9wswBI=";


            signCert = "MIICuDCCAlygAwIBAgIQbRMmbHA6mnSAad15ifd0kjAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwODIzMDM0MzI5WhcNMjQwODI0MDM0MzI5WjB5MQswCQYDVQQGEwJDTjEMMAoGA1UECgwD5pegMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE3NjUyMjUyMjMwMDQ3MDA2NzJA5ZSQ5aW95YevQDAxQDA2NDBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABLaS9TgN9w8DBYTRhHk8wlwHF8QoPQVmmWboHrWk9jzQWIGOjSHwMMaDLX8Yq8jwUHbcWKUp4YL9NMD/3QQQCG2jggEOMIIBCjALBgNVHQ8EBAMCBsAwgboGA1UdHwSBsjCBrzAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwwLmNybDB9oHugeYZ3bGRhcDovL3d3dy5tY3NjYS5jb20uY246Mzg5L0NOPWNybDAsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwHQYDVR0OBBYEFAJvyUQnvYyjlZoAqthQJE80Tp9GMB8GA1UdIwQYMBaAFPEiCmeYjfXjsqrDF2vAQh++S712MAwGCCqBHM9VAYN1BQADSAAwRQIhAKwnEA3fBGYIwwinjOwT02zJrR3eoC2K5SmmoRydXZM6AiAnItLayHZXRgEGnvJecyj9y23AQR52rhrh5jbB9jXJfw==";
            encCert = "MIICuTCCAlygAwIBAgIQVLqJE+asfKP3UolHJxctGDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwODIzMDM0MzI5WhcNMjQwODI0MDM0MzI5WjB5MQswCQYDVQQGEwJDTjEMMAoGA1UECgwD5pegMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE3NjUyMjUyMjMwMDQ3MDA2NzJA5ZSQ5aW95YevQDAxQDA2NDBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABDqOp+hmXYHFGHUdo0ACSDlVYwDUR7B1PI+NGh6BuAUQ4fIlfhKyWI4sijEKNLfs0bdT+VhVRN0leRZ590uUcSOjggEOMIIBCjALBgNVHQ8EBAMCBDAwgboGA1UdHwSBsjCBrzAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwwLmNybDB9oHugeYZ3bGRhcDovL3d3dy5tY3NjYS5jb20uY246Mzg5L0NOPWNybDAsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwHQYDVR0OBBYEFF8Zg3C5/c1qlb4upRJ5oqDVgQ6IMB8GA1UdIwQYMBaAFPEiCmeYjfXjsqrDF2vAQh++S712MAwGCCqBHM9VAYN1BQADSQAwRgIhAMXwjE1wq7BafC1NK/QTucVPDVDXTCJL8xYfhMCxjDC/AiEAnmHg4uxBHvix5sansjZ021TUjcaJ11ML1WxigS/ce3g=";
            doubleEncryptedPrivateKey = "MIIEQwYKKoEcz1UGAQQCBKCCBDMwggQvAgEBMYHUMIHRAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQbRMmbHA6mnSAad15ifd0kjALBgkqgRzPVQGCLQMEfDB6AiEAt+EDP4qmmZt5xXSklqA6PLQ9DoJ0XUIk06FOqtbcQg0CIQCwf5SKEQ5npPDMle/kJpy922P1u1KdMUDDEW46SjDvFgQgaT8/9nLJJOX5gILm3p4jIHZlPVKCISlq+VpIL8SkKV4EEG591JlxBwtEiFdFMS6lnWAxDDAKBggqgRzPVQGDETBZBgoqgRzPVQYBBAIBMAkGByqBHM9VAWiAQJv/z6La4PqTwvKJrcTUgJ6b/8+i2uD6k8Lyia3E1ICe873BHaVdYe2pZLjqppb0LOZkPaUG2AFsPT8wnhpl/mygggHvMIIB6zCCAY+gAwIBAgIQZhpTteVSMDmossSeqZ2nlDAMBggqgRzPVQGDdQUAMHgxCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAlDaG9uZ3FpbmcxOjA4BgNVBAoMMUVhc3QtWmhvbmd4dW4gQ2VydGlmaWNhdGUgQXV0aG9yaXR5IENlbnRlciBDTy5MVEQxGTAXBgNVBAMMEEVhc3QtWmhvbmd4dW4gQ0EwHhcNMjMwODE3MDEyODMzWhcNMzMwODE0MDEyODMzWjAvMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAMMB1NNMlNJR04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQgOmy4mEBDeZmqg7a+q35qoOf7/hoWR5SA3aEP0lkPh3N0nnUV3DGrOaFnwVAGfjZKNp3NftSzFrESYuA4A69+o0IwQDAfBgNVHSMEGDAWgBQJ7rsH/h/eV5iSbu4/y1JqS/kmHTAdBgNVHQ4EFgQUiajqAu5ieONRz99oYPAphmy6XswwDAYIKoEcz1UBg3UFAANIADBFAiAZ52RxsV3HOwK+ls3dzSfvA2uU8CvCZ/Ce0vKeMbD5sAIhAJlu5Xq3TNQOvu1uARHy4evSf3wvnfh888dof6IViykJMYH2MIHzAgEBMIGMMHgxCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAlDaG9uZ3FpbmcxOjA4BgNVBAoMMUVhc3QtWmhvbmd4dW4gQ2VydGlmaWNhdGUgQXV0aG9yaXR5IENlbnRlciBDTy5MVEQxGTAXBgNVBAMMEEVhc3QtWmhvbmd4dW4gQ0ECEGYaU7XlUjA5qLLEnqmdp5QwCgYIKoEcz1UBgxEwCwYJKoEcz1UBgi0BBEYwRAIgMdoCbXmyth0F9kMYmHjG2LoM6WSkLxDdAb85kaMuETgCICpBvqmGFFquPuQwAz8KmF17sJAomYRfXW+r6XeaZgT8";

            signCert = "MIICxzCCAmqgAwIBAgIQf3RK0UDhwgRa69M14UkkajAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwODIzMDUzMjAwWhcNMjQwODI0MDUzMjAwWjB5MQswCQYDVQQGEwJDTjEMMAoGA1UECgwD5pegMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE4MjY2MDE0MTQzMzY0NzEwNDBA5ZSQ5aW95YevQDAxQDA2NTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABGmh1thJJwk3bb2tUoRuOncaeMYOBT99YKuBQomWPd3g+ZMSDG8g9Tb6jmsWShlPKAoknjhrssTdlG9oVwrlom2jggEcMIIBGDAfBgNVHSMEGDAWgBTxIgpnmI3147KqwxdrwEIfvku9djAdBgNVHQ4EFgQUKT2+LmP32BTw6S5DBKTAsXbMZEEwDAYDVR0TBAUwAwEBADALBgNVHQ8EBAMCBPAwgboGA1UdHwSBsjCBrzAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwwLmNybDB9oHugeYZ3bGRhcDovL3d3dy5tY3NjYS5jb20uY246Mzg5L0NOPWNybDAsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDAYIKoEcz1UBg3UFAANJADBGAiEAtB7pFEjxfk726c1rgdphO/XXfdEbbpoUm2HfM9XOEsQCIQDvtElbCQiZR1BZepX3Y4vE7kZZAN9oOrlDp8XdluLqtQ==";


            //System.out.println(Hex.toHexString(Base64.decode(sm2pubkey)));

            //解密加密证书私钥
            byte[] sm2key = getSM2EncKey(doubleEncryptedPrivateKey, sm2prikey);
            System.out.println("sm2Key Hex:" + Hex.toHexString(sm2key));
            System.out.println("sm2Key Base64:" + Base64.toBase64String(sm2key));

            //转换为32位
            byte[] b = new byte[32];
            for (int k = 0; k < b.length; k++) {
                b[k] = sm2key[32 + k];
            }

            //解析一下公钥数据
            byte[] x = getSm2PubKey(encCert, 1);
            byte[] y = getSm2PubKey(encCert, 2);

            System.out.println("encCert x Hex:" + Hex.toHexString(x));
            System.out.println("encCert y Hex:" + Hex.toHexString(y));


            byte[] randomKey = "1234567812345678".getBytes();
            byte[] prikey2 = new byte[32];
            System.arraycopy(sm2key, 32, prikey2, 0, 32);
            System.out.println("加密私钥32位格式：" + Hex.toHexString(prikey2));

            String cc;
            String enckeystr = Base64.toBase64String(prikey2);

            //hongding
            // cc = changePriEnvelopBy35291(enckeystr, encCert, signCert, randomKey);
            // System.out.println("EnvelopedKeyBlob数据格式：" + cc);


            //福建
            cc = GenSM2ProPriKey(enckeystr, encCert, signCert, randomKey);
            System.out.println("SM2密钥对保护数据格式：" + cc);


            //String bpencpri="AQAAAAEEAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA5ZF7ICX276TIAQeuPjUfimuf9uHAUC4SDU5BvXqB+1gABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADRfLuxov5O9sjCYHt0jz2F94xF57jEnB63a99mW0FccAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACPDz4otJEvrrJyznIdsOvB4p7LhiP5LGayBgksB2s/AwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADBb6p9OFp2BOoyLH/hrKX5yJzPKTNnGinw0Q0/y7ZqwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGpiXiQBd9CGUq/Or82biE2GhkHArs4colTvsXxzn/7LPvh7b3HrBqzREMeVkx1XrALybwCDSdTTMOyYzTA8KtIQAAAA57GAy8+0JK3mC+pEHrrzbw==";


            //System.out.println("bpencpri：" +Hex.toHexString(Base64.decode(bpencpri)));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @param encsm2pristr 加密私钥
     * @param encsm2cert   加密证书
     * @param signcert     签名证书
     * @param pwd          非对称算法私钥
     * @return
     * @throws Exception
     */
    public static String GenSM2ProPriKey(String encsm2pristr, String encsm2cert, String signcert, byte[] pwd) throws Exception {
        byte[] sm2pri = null;
        try {
            sm2pri = Base64.decode(encsm2pristr);
            byte[] xy = getSm2PubKey(encsm2cert);
            //byte[] y = getSm2PubKey(encsm2cert, 2);
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

            System.out.println("sm2pri:" + Hex.toHexString(sm2pri));
            //对称密钥加密
            byte[] cbp = SymmetricEncryption(sm2pri, pwd, false, "");

            SubjectPublicKeyInfo spki = getPubInfoFromCert(Base64.decode(signcert));
            //公钥加密对称密钥pwd SM2Cipher
            String pubkeyenc = PubkeyEncryption(spki, pwd);

            System.out.println("pubkeyenc:" + pubkeyenc);

            byte[] encpx = getSm2PubkeyEncryption(pubkeyenc, 1);
            byte[] encpy = getSm2PubkeyEncryption(pubkeyenc, 2);
            byte[] hash = getSm2PubkeyEncryption(pubkeyenc, 3);
            byte[] encdata = getSm2PubkeyEncryption(pubkeyenc, 4);


            ASN1EncodableVector asn1ev = new ASN1EncodableVector();

            ASN1ObjectIdentifier digestAlgOID = new ASN1ObjectIdentifier("1.2.156.10197.1.104.1");
            AlgorithmIdentifier algID = new AlgorithmIdentifier(digestAlgOID, DERNull.INSTANCE);


            asn1ev.add(algID);
            asn1ev.add(ASN1Sequence.fromByteArray(Base64.decode(pubkeyenc)));
            asn1ev.add(new DERBitString(xy));
            asn1ev.add(new DERBitString(cbp));


            DERSequence derseq = new DERSequence(asn1ev);
            return Base64.toBase64String(derseq.getEncoded());


            //return Base64.toBase64String(gmsm2propri);
        } catch (Exception e) {
            System.out.println(Hex.toHexString(sm2pri));
            throw e;
        }
    }


    /**
     * @param encsm2pristr 加密私钥
     * @param encsm2cert   加密证书
     * @param signcert     签名证书
     * @param pwd          非对称算法私钥
     * @return
     * @throws Exception
     */
    public static String GenGMSM2ProPriKey(String encsm2pristr,
                                           String encsm2cert, String signcert, byte[] pwd) throws Exception {
        byte[] sm2pri = null;
        try {
            sm2pri = Base64.decode(encsm2pristr);
            byte[] x = getSm2PubKey(encsm2cert, 1);
            byte[] y = getSm2PubKey(encsm2cert, 2);
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
            byte[] cbp = SymmetricEncryption(sm2pri, pwd, false, "");
            byte[] cbEncryptedPriKey = new byte[64];
            System.arraycopy(cbp, 0, cbEncryptedPriKey, 32, 32);

            SubjectPublicKeyInfo spki = getPubInfoFromCert(Base64.decode(signcert));
            String pubkeyenc = PubkeyEncryption(spki, pwd);
            byte[] encpx = getSm2PubkeyEncryption(pubkeyenc, 1);
            byte[] encpy = getSm2PubkeyEncryption(pubkeyenc, 2);
            byte[] hash = getSm2PubkeyEncryption(pubkeyenc, 3);
            byte[] encdata = getSm2PubkeyEncryption(pubkeyenc, 4);


            System.out.println("cbEncryptedPriKey：" + Hex.toHexString(cbEncryptedPriKey));
            System.out.println("x：" + Hex.toHexString(x));
            System.out.println("y：" + Hex.toHexString(y));
            System.out.println("encpx：" + Hex.toHexString(encpx));
            System.out.println("encpy：" + Hex.toHexString(encpy));
            System.out.println("hash：" + Hex.toHexString(hash));
            System.out.println("encdata：" + Hex.toHexString(encdata));


            byte[] gmsm2propri = GenGMENVELOPEDKEYBLOB(cbEncryptedPriKey, x, y,
                    encpx, encpy, hash, encdata);

            System.out.println("gmsm2propri：" + Hex.toHexString(gmsm2propri));
            return Base64.toBase64String(gmsm2propri);
        } catch (Exception e) {
            System.out.println(Hex.toHexString(sm2pri));
            throw e;
        }
    }


    /**
     * 组成介质需要的保护私钥
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
    public static byte[] GenGMENVELOPEDKEYBLOB(byte[] cbEncryptedPriKey,
                                               byte[] pubKey_x, byte[] pubKey_y, byte[] eccCipherBlob_x,
                                               byte[] eccCipherBlob_y, byte[] hash, byte[] cipher) {
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

    public static byte[] reverseBytes(byte[] s) {
        byte[] r = new byte[s.length];
        for (int i = 0; i < s.length; i++) {
            r[i] = s[s.length - 1 - i];
        }
        return r;
    }

    public static byte[] intToBytes_r(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);

        return reverseBytes(b);
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    /**
     * 解析sm2加密得到c++国密sm2要的参数
     *
     * @param Sm2PubkeyEncryption
     * @param type                1 x 2 y 3 hash 4 encdata
     * @return
     * @throws Exception
     */
    public static byte[] getSm2PubkeyEncryption(String Sm2PubkeyEncryption,
                                                int type) throws Exception {
        byte[] rv = null;
        try {
            ASN1Sequence seq = (ASN1Sequence) ASN1Sequence.fromByteArray(Base64
                    .decode(Sm2PubkeyEncryption));
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


    /**
     * 得到c++国密要的x或者y
     *
     * @param Base64Cert 证书
     * @param isxy       1 x 2 y
     * @return
     * @throws Exception
     */
    public static byte[] getSm2PubKey(String Base64Cert, int isxy)
            throws Exception {
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
                    throw new Exception("Not supported by the algorithm");
                } else {
                    byte[] xy = spki.getPublicKeyData().getBytes(); //.getOctets()
                    System.out.println("XY：" + Hex.toHexString(xy));
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
     * 对数据进行对称加密
     *
     * @param indata 数据
     * @param pwd    对称算法的密钥
     * @param alg    对称算法 3DES AES SM4
     * @return 加密值
     * @throws Exception
     */
    public static byte[] SymmetricEncryption(byte[] indata, byte[] pwd, boolean isPadding, String alg) throws Exception {
        byte[] outdata = null;
        byte[] pkey = new byte[pwd.length];
        System.arraycopy(pwd, 0, pkey, 0, pwd.length);

        try {
            SecretKeySpec skeySpec = null;
            Cipher cipher = null;
            if (alg == null || alg.equals("")) {
                skeySpec = new SecretKeySpec(pwd, "SM4");
                if (isPadding) {
                    cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", "BC"); // NoPadding
                } else {
                    cipher = Cipher.getInstance("SM4/ECB/NoPadding", "BC"); // NoPadding
                }
            } else if (alg.toLowerCase().equals("des")) {
                skeySpec = new SecretKeySpec(pwd, "DES");
                if (isPadding) {
                    cipher = Cipher.getInstance("DES/ECB/PKCS5Padding", "BC"); // NoPadding
                } else {
                    cipher = Cipher.getInstance("DES/ECB/NoPadding", "BC"); // NoPadding
                }
            } else if (alg.toLowerCase().equals("aes")) {
                AESEngine aes = new AESEngine();
                BufferedBlockCipher cipherBC = new PaddedBufferedBlockCipher(
                        aes, new PKCS7Padding());
                KeyParameter keyParam = new KeyParameter(pwd);
                CipherParameters params = (CipherParameters) new ParametersWithRandom(
                        keyParam);
                cipherBC.reset();
                cipherBC.init(true, params);
                byte[] buf = new byte[cipherBC
                        .getOutputSize(indata.length + 32)];
                int len = cipherBC.processBytes(indata, 0, indata.length, buf,
                        0);
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
     * @throws Exception
     */
    public static String PubkeyEncryption(SubjectPublicKeyInfo subjectPublicKeyInfo, byte[] data) throws Exception {
        String rv = "";
        try {
            String certsignalg = subjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId();
            AlgorithmIdentifier keyAlg = subjectPublicKeyInfo.getAlgorithmId();
            if (certsignalg.equals("1.2.840.10045.2.1")) // ecc
            {
                boolean issm2 = false;
                // System.out.println(subjectPublicKeyInfo.getAlgorithm().getParameters().getClass().toString());
                if (subjectPublicKeyInfo.getAlgorithm().getParameters().getClass().toString()
                        .indexOf("ASN1ObjectIdentifier") != -1) // sm2
                {
                    ASN1ObjectIdentifier pubkeyalgPoid = (ASN1ObjectIdentifier) subjectPublicKeyInfo
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
                            new DERBitString(subjectPublicKeyInfo).getBytes());
                    java.security.PublicKey pubkey = KeyFactory.getInstance(
                                    keyAlg.getAlgorithm().getId(), "BC")
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
                        keyAlg.getAlgorithm().getId(), "BC").generatePublic(
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
     * 功能说明:实现SM2返回数字信封的加密密钥
     * <p>
     * 1、对PKCS#10格式的数据进行验证
     * 2、从PKCS#10数据中获取临时公钥
     * 3、
     *
     * @param encryptedPrivateKey
     * @param signPri
     * @return
     * @throws Exception
     */


    public static byte[] getSM2EncKey(String encryptedPrivateKey, String signPri) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        byte[] key = null;
        ASN1Sequence asn1_epk = (ASN1Sequence) ASN1Sequence.fromByteArray(Base64.decode(encryptedPrivateKey));
        ASN1ObjectIdentifier pkcs7type = (ASN1ObjectIdentifier) asn1_epk.getObjectAt(0);
        if (pkcs7type.getId().equals("1.2.156.10197.6.1.4.2.4")) //sm2
        {
            KeyFactory kf = KeyFactory.getInstance("EC", "BC");
            //初始化sm2加密
            BCECPrivateKey pri = (BCECPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(signPri)));
            ECPrivateKeyParameters aPriv = (ECPrivateKeyParameters)
                    ECUtil.generatePrivateKeyParameter((PrivateKey) pri);
            SM2Engine sm2Engine = new SM2Engine();
            sm2Engine.init(false, aPriv);

            ASN1TaggedObject dtos = (ASN1TaggedObject) asn1_epk.getObjectAt(1);
            ASN1Sequence asn1_encdata = (ASN1Sequence) dtos.getBaseObject();
            // ASN1Sequence asn1_encdata= (ASN1Sequence) dtos.getObjectParser(0, true);

            ASN1Integer v = (ASN1Integer) asn1_encdata.getObjectAt(0);
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

            byte c4[] = Arrays.concatenate(xy, c2, c3);

            //sm2解密得到对称密钥的key
            byte[] sm4key = sm2Engine.processBlock(c4, 0, c4.length);

            ASN1Set d2_alg = (ASN1Set) asn1_encdata.getObjectAt(2);
            ASN1Sequence encdata = (ASN1Sequence) asn1_encdata.getObjectAt(3);
            ASN1TaggedObject sm4encdatadto = (ASN1TaggedObject) encdata.getObjectAt(2);
            DEROctetString dstr_sm4encdata = (DEROctetString) sm4encdatadto.getBaseObject();
            // DEROctetString dstr_sm4encdata=(DEROctetString) sm4encdatadto.getObjectParser(0, true);
            byte[] sm4encdata = dstr_sm4encdata.getOctets();
            //sm4解密
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = false;
            ctx.mode = SM4.SM4_DECRYPT;
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, sm4key);
            byte[] decrypted = null;
            decrypted = sm4.sm4_crypt_ecb(ctx, sm4encdata);
            key = decrypted;
        } else {
            throw new Exception("无效的SM2数字信封格式");
        }
        return key;
    }


    /**
     * 得到c++国密要的x或者y
     *
     * @param Base64Cert 证书
     * @param isxy       1 x 2 y
     * @return
     * @throws Exception
     */
    public static byte[] getSm2PubKey(String Base64Cert)
            throws Exception {
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
                    throw new Exception("Not supported by the algorithm");
                } else {
                    byte[] xy = spki.getPublicKeyData().getBytes(); //.getOctets()
                    return xy;
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


    private static String[] sm2_param = {
            "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF",// p,0
            "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC",// a,1
            "28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93",// b,2
            "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123",// n,3
            "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7",// gx,4
            "BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0" // gy,5
    };


}
