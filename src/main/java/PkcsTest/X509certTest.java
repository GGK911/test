package PkcsTest;

import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * @author TangHaoKai
 * @version V1.0 2024/1/23 15:57
 **/
public class X509certTest {

    @Test
    @SneakyThrows
    public void certTest() {
        Security.addProvider(new BouncyCastleProvider());
        String cert = "MIIC2jCCAkOgAwIBAgIBAzANBgkqhkiG9w0BAQUFADBYMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFQ2hpbmExFTATBgNVBAsMDEludGVybWVkaWF0ZTEiMCAGCSqGSIb3DQEJARYTMTM5ODMwNTM0NTVAMTYzLmNvbTAeFw0yNDAxMjMwNzIwMzJaFw0yNTAxMjIwNzIwMzJaMGYxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVDaGluYTESMBAGA1UEBwwJQ2hvbmdxaW5nMQ8wDQYDVQQDDAZHR0s5MTExIjAgBgkqhkiG9w0BCQEWEzEzOTgzMDUzNDU1QDE2My5jb20wWjANBgkqhkiG9w0BAQEFAANJADBGAkEAtKfkYXBXTxapcIKyK+WLaipil5hBm+EocqS9umJs+umQD3ar+xITnc5d5WVk+rK2VDFloEDGBoh0IOM9ke1+1wIBEaOB6zCB6DAdBgNVHQ4EFgQU0GzsbTWDvFUSGwzLPv7XJqYWZGgwHwYDVR0jBBgwFoAUlAgzbzJA94c32tEgqu0up27JyR4wCQYDVR0fBAIwADBfBggrBgEFBQcBAQEB/wRQME4wKAYIKwYBBQUHMAGCHGh0dHA6Ly8xMjcuMC4wLjEvY2Fpc3N1ZS5odG0wIgYIKwYBBQUHMAKCFmh0dHA6Ly8xMjcuMC4wLjE6MjA0NDMwDgYDVR0PAQH/BAQDAgbAMBIGA1UdEwEB/wQIMAYBAf8CAQMwFgYDVR0lAQH/BAwwCgYIKwYBBQUHAwgwDQYJKoZIhvcNAQEFBQADgYEAKqpSf7BFMOeWBHEuw7qR3SiNmBDPVYcSTQtKrY5z6dU1Dcz3M4pcTT2SGwQtEEAKwZsJOe2aACTkyXAoMCQed6NCeuIfBTSC4m7S10afTYKMoYYmJsgIWbxnnT91aEpJEBOqwYy2WhfunTFAQKRerBbjTixEOqIsSywUgBrodwA=";
        ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(cert));
        //将hex转换为byte输出
        ASN1Primitive asn1Primitive;
        while ((asn1Primitive = asn1InputStream.readObject()) != null) {
            ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
            // TBSCert
            ASN1Sequence tbsCert = (ASN1Sequence) sequence.getObjectAt(0);
            System.out.println("TBScert："+ Hex.toHexString(tbsCert.getEncoded()));
            // 算法
            ASN1Sequence signAlg = (ASN1Sequence) sequence.getObjectAt(1);
            ASN1ObjectIdentifier alg = (ASN1ObjectIdentifier) signAlg.getObjectAt(0);
            System.out.println(alg.getId());
            // 签名
            DERBitString sigValue = (DERBitString) sequence.getObjectAt(2);
            System.out.println("SigValue:"+ Hex.toHexString(sigValue.getOctets()));
            // 验签(用中间CA的公钥证书 验 TBSCert)
            String middleCertBase64 = "MIICgzCCAeygAwIBAgIBAjANBgkqhkiG9w0BAQUFADAqMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFQ2hpbmExCzAJBgNVBAsMAkNBMB4XDTI0MDEyMzA3MjAzMloXDTI5MDEyMTA3MjAzMlowWDELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBUNoaW5hMRUwEwYDVQQLDAxJbnRlcm1lZGlhdGUxIjAgBgkqhkiG9w0BCQEWEzEzOTgzMDUzNDU1QDE2My5jb20wgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAI3g0RPF5zaWnI0rBHokP4/hjtrWTN6ehC02aSMMpIb3z93h+O7FTRkF//BKzIXmEJPhgMrcbOpAfxk9RLsOlEm427SXhM2eNiYMOeBqlHKZl4xu2DAHJOiHGYz+3iDz+95lj6K9B4vpRqOSvTSfK0nEhuIMQFWI4wZwbJAXMI5pAgMA//+jgYowgYcwHQYDVR0OBBYEFJQIM28yQPeHN9rRIKrtLqduyckeMFIGA1UdIwRLMEmAFMA2GQetxIiXqF5yb2sJ6+Xm8SlcoS6kLDAqMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFQ2hpbmExCzAJBgNVBAsMAkNBggEBMBIGA1UdEwEB/wQIMAYBAf8CAQAwDQYJKoZIhvcNAQEFBQADgYEArn/OqlUQhidMXWDTbbtYt3wzvSFESbgX3DSOv/WxKcp/FyUElLzZPZbMOKqOWQsZxphvNCGHCj026aclM5Q7Uuv0pfXFMj2L64hDruCirtQV2NrdKCCx4koM7I8hElXKXUzVohJymqWswpVosup0/n5rZXtONP8Y0w8Vfm1fKnk=";
            ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(middleCertBase64));
            Signature signature = Signature.getInstance("SHA1WithRSA", "BC");
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
            Certificate middleCert = certificateFactory.generateCertificate(bis);
            signature.initVerify(middleCert.getPublicKey());
            signature.update(tbsCert.getEncoded());
            System.out.println(signature.verify(sigValue.getOctets()));
        }

    }
}
