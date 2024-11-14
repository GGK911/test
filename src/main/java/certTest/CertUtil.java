package certTest;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * 证书工具类
 *
 * @author TangHaoKai
 * @version V1.0 2024/8/19 15:55
 */
public class CertUtil {
    private static final Provider BC = new BouncyCastleProvider();

    public static void main(String[] args) throws CertificateException {
        // rsa
        String cert = "MIIDSDCCAjCgAwIBAgIHBGLVN+fvTjANBgkqhkiG9w0BAQsFADAyMQswCQYDVQQGEwJDTjEPMA0GA1UEChMGTUlOSUNBMRIwEAYDVQQDEwlST09UUlNBQ0EwHhcNMjQwNzEzMDcyNjIzWhcNMjkwNzEyMDcyNjIzWjAyMQswCQYDVQQGEwJDTjEPMA0GA1UEChMGTUlOSUNBMRIwEAYDVQQDEwlST09UUlNBQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC0XcHhpcSNhzb5v5b4Rf1NZ+BiW68onMgxTBqdhuwEKuFkj7YgHRgfzuogKLqju1Af5fWJn8wwlmu7c0G2Ct4/gsIhZNz+SZqCXJld3SrKQ5XwgdRbFa4uYdUXgQvJlbglDzXc60Do/d9drueURhom37WAtiCpQwQIzVvL1oNKMzD0cBcteINd+9BGbfciOTIvdmVm6SFrW0j7Dx/sum6G9QaftckC/M7t6FY1cZZAbvnutcDa7tAsnWlvf/3ZHbM73SUvw8hQUd8hGi3MLYkRpZ5KfXT+lyYkifXRG4PffvOIyHqkZmj2KvQ2Gagy8zfL3+kWwKL9YEdE2ZM/MOaNAgMBAAGjYzBhMB0GA1UdDgQWBBQJdtZrPgBzsigut+sM3y9XC4hN5DAfBgNVHSMEGDAWgBQJdtZrPgBzsigut+sM3y9XC4hN5DALBgNVHQ8EBAMCAQYwEgYDVR0TAQH/BAgwBgEB/wIBAzANBgkqhkiG9w0BAQsFAAOCAQEAm+CAwowLqGaitBrayoy5THj8Twu75OExkSOObxjxOoej5V7GZbF7Z7ZpKD9o3/sPic8Qf7O3RNVcFX5B4plBSjmL7tqzmDZzQDY0Yj3VVX9kijwhhPLgvZz1qYlnUNChKAB9qaa9SMLEDnSOiqZb61WWW5aHaGzt6SIl0UIt4v+e7o7yNpWY6Va9HrV1IpBHIUZkuoU7zMWFxCJ1jAPtXSgbSgY+omC1mYsqVIOxtmnD2q5TWi1a3JoBbQMHeT73G19fUWXhidh4h2hDeYOndKGbyNJVh/aW18/VgeISClm3HunjIPzZjAvBOFCXmRMRIn679pbTk/agHiAtPi5BLA==";
        // sm2
        // cert = "MIIBvDCCAWKgAwIBAgIHBGLVN+fvTjAKBggqgRzPVQGDdTAyMQswCQYDVQQGEwJDTjEPMA0GA1UEChMGTUlOSUNBMRIwEAYDVQQDEwlST09UU00yQ0EwHhcNMjQwNzEzMDcyOTIyWhcNMjkwNzEyMDcyOTIyWjAyMQswCQYDVQQGEwJDTjEPMA0GA1UEChMGTUlOSUNBMRIwEAYDVQQDEwlST09UU00yQ0EwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAR/Q8kpp3JIPzg1O7A8Gp/EogeNBeKAsArnJ3Fq0S3uO9R0J8TG1ayg4QOr4q6mzWrLTGxi/+xEQnUMV+LYSON8o2MwYTAdBgNVHQ4EFgQU4BGGHGMDIY0chgEm2/th7W1A0+owHwYDVR0jBBgwFoAU4BGGHGMDIY0chgEm2/th7W1A0+owCwYDVR0PBAQDAgEGMBIGA1UdEwEB/wQIMAYBAf8CAQMwCgYIKoEcz1UBg3UDSAAwRQIhAObRZYxRdmMaA5fvNoKC0h/0LamTImyiaq49FQlmPAzyAiBj/BqJ0yYJXXfCC7ZYKLYasxy0bI4T/DkF+2Ptnh3KdQ==";
        String signAlgFromCert = getSignAlgFromCert(Base64.decode(cert));
        System.out.println(String.format("%-16s", "signAlgFromCert>> ") + signAlgFromCert);
    }

    /**
     * 获取证书签名算法
     *
     * @param certBytes 证书
     * @return 证书签名算法
     * @throws CertificateException 证书对象异常
     */
    public static String getSignAlgFromCert(byte[] certBytes) throws CertificateException {
        CertificateFactory certFact = CertificateFactory.getInstance("X.509", BC);
        X509Certificate certificate = (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(certBytes));
        return certificate.getSigAlgName();
    }

    /**
     * 获取证书公钥
     *
     * @param certBytes 证书
     * @return 公钥
     * @throws CertificateException 证书对象异常
     */
    public static PublicKey getPubKeyFromCert(byte[] certBytes) throws CertificateException {
        CertificateFactory certFact = CertificateFactory.getInstance("X.509", BC);
        X509Certificate certificate = (X509Certificate) certFact.generateCertificate(new ByteArrayInputStream(certBytes));
        return certificate.getPublicKey();
    }

    /**
     * 从证书DN中获取到CN项
     *
     * @param certHolder 证书对象
     * @return CN项
     */
    public static String getCNFromCert(X509CertificateHolder certHolder) {
        try {
            // 使用 BouncyCastle 提供的 JcaX509CertificateHolder 获取 X500Name
            X500Name subject = certHolder.getSubject();
            return getCNFromX500Name(subject);
        } catch (Exception e) {
            throw new RuntimeException("解析证书CN项异常：", e);
        }
    }

    /**
     * 从证书DN中获取到CN项
     *
     * @param DN DN字符串
     * @return CN项
     */
    public static String getCNFromDN(String DN) {
        X500Name x500Name = new X500Name(DN);
        return getCNFromX500Name(x500Name);
    }

    /**
     * 从证书DN中获取到CN项
     *
     * @param subject X500Name对象
     * @return CN项
     */
    public static String getCNFromX500Name(X500Name subject) {
        RDN[] rdNs = subject.getRDNs(BCStyle.CN);
        if (rdNs.length > 0) {
            return rdNs[0].getFirst().getValue().toString();
        }
        return subject.toString();
    }
}
