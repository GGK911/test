package certTest;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;

/**
 * 证书工具类
 *
 * @author TangHaoKai
 * @version V1.0 2024/8/19 15:55
 */
public class CertUtil {

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
