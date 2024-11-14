package certTest.x509cert;

import cn.hutool.json.JSONObject;
import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.ujmp.core.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 证书解析
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-01 10:55
 **/
public class CertAnalysisTest {
    private static final Provider BC = new BouncyCastleProvider();

    @Test
    @SneakyThrows
    public void parseCertTest() {
        String certBase64 = "MIIFBTCCBKqgAwIBAgIUQc6BaJ8YL5I6lHePwdFw0HbD6UMwCgYIKoEcz1UBg3UwJjELMAkGA1UEBhMCQ04xFzAVBgNVBAMMDlNNMiBTU0wgUHJvIENBMB4XDTI0MDUyMjA5NTA0OFoXDTI1MDYyMTA5NTA0OFowgc4xEzARBgsrBgEEAYI3PAIBAwwCQ04xGzAZBgNVBAUMEjkxNDQwMzAwTUE1R1k0WDRYSDEdMBsGA1UEDwwUUHJpdmF0ZSBPcmdhbml6YXRpb24xCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAnlub/kuJznnIExEjAQBgNVBAcMCea3seWcs+W4gjEtMCsGA1UECgwk6Zu25L+h5oqA5pyv77yI5rex5Zyz77yJ5pyJ6ZmQ5YWs5Y+4MRcwFQYDVQQDDA53d3cuem90cnVzLmNvbTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABN6Rk27mno84ECRRQ37tFxWTsLqeRYhpkUzdvEGNWMTX7YaXZRB1FxcbMh7AOpOLbwx/PCjOBxkYJQSvWVT9Y/2jggMLMIIDBzAMBgNVHRMBAf8EAjAAMA4GA1UdDwEB/wQEAwIHgDAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwEwHQYDVR0OBBYEFCjNEwJAULhSpbF7NTGSXCZ7w057MB8GA1UdIwQYMBaAFCbX4YVx7B06gMCRMbbzU0VFN0s4MDUGA1UdEQQuMCyCDnd3dy56b3RydXMuY29tgg53ZWIuem90cnVzLmNvbYIKem90cnVzLmNvbTBCBggrBgEFBQcBAQQ2MDQwMgYIKwYBBQUHMAKGJmh0dHA6Ly9haWEuY2Vyc2lnbi5jbi9jc3NzbC1wcm8tZXYuY2VyMDwGA1UdHwQ1MDMwMaAvoC2GK2h0dHA6Ly9jcmwuY2Vyc2lnbi5jbi9jc3NzbC1wcm8tZXYtMjAyNC5jcmwwTgYDVR0gBEcwRTAJBgcqgRyJ0W0OMDgGCSqBHInPaAMBBDArMCkGCCsGAQUFBwIBFh1odHRwOi8vd3d3LmNlcnNpZ24uY24vcG9saWN5LzCCAX0GCisGAQQB1nkCBAIEggFtBIIBaQFnAHUAeIOEJtVepOmH9Z1AZmTO5KhoQIhjSzM9Ut58Ekte+qUAAAGPn7X0MwAABwQARjBEAiAb5QenyAGOvRc9ORs8QSlczp5yLL2YG1AQWYCWFMVTtwIgQJtnP2nwqJVQ9mgAl0AwyH1Jr22XDdQYKteJ0EVl1igAdwAfVzFGx80ifR4tNIwg7r+XGaskn3AFjY8cbbUwPzmLIwAAAY+fth5aAAAHBABIMEYCIQCvVx3C4RXvxVRxgeaLrGtu3smfY0ro4lDRwk5og5OSxQIhAPiFfxrn2zJWz0POLYGKkFi+dHqcThykJtS701FPVTGVAHUApf1HKZ4UQeyj1NTjsg2zWGUNzez2XFNqg+wbw/MK5O8AAAGPn7ZJQAAABwQARjBEAiAUQ0XoPscmuUcwj/NsalMG9aP3TxJ6vA2kGRmzQNG6EwIgQFldj+NPUvVg/3jAaShiuf/AE5ZofwEXVrB/h5sxVNAwCgYIKoEcz1UBg3UDSQAwRgIhAPMTrrs47d4Aj5KdVwBp7a3G8hJU7ezrWjAoB0OdYO5vAiEAoYAs7H27CGgiJPF0yphziA8AkcDJ9n4Mr4j79UtOk1k=";
        CertificateFactory cf = CertificateFactory.getInstance("X.509", BC);
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(Base64.decode(certBase64)));
        // 使用 BouncyCastle 解析证书
        X509CertificateHolder certHolder = new JcaX509CertificateHolder(certificate);
        //秘钥类型
        String type = certificate.getPublicKey().getAlgorithm();
        //签名哈希算法
        String sigAlgName = certificate.getSigAlgName();
        System.out.println(type + "-" + sigAlgName);
        // 查找扩展：客户端认证 (1.3.6.1.5.5.7.3.2)
        Extensions extensions = certHolder.getExtensions();
        // 获取所有扩展OID
        // ASN1ObjectIdentifier[] extensionOIDs = extensions.getExtensionOIDs();
        // for (ASN1ObjectIdentifier extensionOID : extensionOIDs) {
        //     Extension extendedKeyUsageExt = certHolder.getExtension(extensionOID);
        //     ASN1OctetString extnValue = extendedKeyUsageExt.getExtnValue();
        //     System.out.println(extensionOID + "-" + extnValue);
        // }
        // 获取所有关键扩展OID
        ASN1ObjectIdentifier[] criticalExtensionOIDs = extensions.getCriticalExtensionOIDs();
        for (ASN1ObjectIdentifier criticalExtensionOID : criticalExtensionOIDs) {
            Extension extendedKeyUsageExt = certHolder.getExtension(criticalExtensionOID);
            ASN1OctetString extnValue = extendedKeyUsageExt.getExtnValue();
            System.out.println(criticalExtensionOID + "-" + extnValue);
        }
        // 获取所有非关键扩展OID
        ASN1ObjectIdentifier[] nonCriticalExtensionOIDs = extensions.getNonCriticalExtensionOIDs();
        for (ASN1ObjectIdentifier nonCriticalExtensionOID : nonCriticalExtensionOIDs) {
            Extension extendedKeyUsageExt = certHolder.getExtension(nonCriticalExtensionOID);
            ASN1OctetString extnValue = extendedKeyUsageExt.getExtnValue();
            System.out.println(nonCriticalExtensionOID + "-" + extnValue);
        }
        // 获取指定单个扩展项
        Extension extension = certHolder.getExtension(Extension.extendedKeyUsage);
        System.out.println(extension.getExtnValue());
        ASN1ObjectIdentifier sctOid = new ASN1ObjectIdentifier("1.3.6.1.4.1.11129.2.4.2");
        Extension extension2 = certHolder.getExtension(sctOid);
        System.out.println(extension2.getExtnValue());

        // Extension ct_sct_ext = new Extension(sctOid, false, );

    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        File certFile = new File("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\certTest\\socialnetwork.cer");
        CertificateFactory cf;
        X509Certificate cert;
        try {
            cf = CertificateFactory.getInstance("X.509");
            FileInputStream in = new FileInputStream(certFile);
            cert = (X509Certificate) cf.generateCertificate(in);
        } catch (CertificateException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        //签发者
        String subjectDN = cert.getSubjectDN().toString();
        //使用者
        String issuerDN = cert.getIssuerDN().toString();
        //序列号（十进制转十六进制，左补零）
        String serialNumber = String.format("%" + 20 + "s", cert.getSerialNumber().toString(16)).replace(' ', '0');
        //生效时间
        Date effDate = cert.getNotBefore();
        //过期时间
        Date expDate = cert.getNotAfter();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String effStr = null;
        String expStr = null;
        try {
            effStr = sdf.format(effDate);
            expStr = sdf.format(expDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //秘钥类型
        String type = cert.getPublicKey().getAlgorithm();
        //签名哈希算法
        String sigAlgName = cert.getSigAlgName();

        jsonObject.set("subjectDN", subjectDN);
        jsonObject.set("issuerDN", issuerDN);
        jsonObject.set("serialNumber", serialNumber);
        jsonObject.set("effDate", effStr);
        jsonObject.set("expDate", expStr);
        jsonObject.set("type", type);
        jsonObject.set("sigAlgName", CertType.getNameByCode(sigAlgName));
        System.out.println(jsonObject.toStringPretty());
    }
}
