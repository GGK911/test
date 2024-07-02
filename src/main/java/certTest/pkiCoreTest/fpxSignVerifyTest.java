package certTest.pkiCoreTest;

import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Base64;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.com.mcsca.pki.core.x509.X509Certificate;
import cn.hutool.core.io.FileUtil;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

/**
 * 解析PFX文件
 *
 * @author TangHaoKai
 * @version V1.0 2024/7/2 15:01
 */
public class fpxSignVerifyTest {

    public static void main(String[] args) throws Exception {
        // 尝试读取
        byte[] bytes = FileUtil.readBytes(Paths.get("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\certTest\\pkiCoreTest\\RSA元朗.pfx").toAbsolutePath().toString());
        KeyStore ks = KeyStore.getInstance("PKCS12");
        char[] PASSWD = "123456".toCharArray();
        ks.load(new ByteArrayInputStream(bytes), PASSWD);
        Enumeration<String> aliases = ks.aliases();
        PrivateKey priKey = null;
        Certificate cert = null;
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Key key = ks.getKey(alias, PASSWD);
            if (key != null) {
                // 私钥
                priKey = (PrivateKey) key;
                System.out.println("priKey>> " + Base64.toBase64String(priKey.getEncoded()));

                // 证书
                Certificate[] certificateChain = ks.getCertificateChain(alias);
                for (Certificate certificate : certificateChain) {
                    cert = certificate;
                    System.out.println("certificate>> " + Base64.toBase64String(certificate.getEncoded()));

                    // 公钥
                    PublicKey publicKey = certificate.getPublicKey();
                    System.out.println("pubKey>> " + Base64.toBase64String(publicKey.getEncoded()));
                }
            }
        }

        X509Certificate x509Certificate = new X509Certificate(cert.getEncoded());

        String p1Signdata = new String(SignatureUtil.P1MessageSign("SHA256WITHRSA", "测试".getBytes(), priKey));
        System.out.println(p1Signdata);
        String p7SignAttach = new String(SignatureUtil.P7MessageSignAttach("SHA256WITHRSA", "测试".getBytes("UTF-8"), priKey, x509Certificate));
        System.out.println(p7SignAttach);
        String p7SignDetach = new String(SignatureUtil.P7MessageSignDetach("SHA256WITHRSA", "测试".getBytes("UTF-8"), priKey, x509Certificate));
        System.out.println(p7SignDetach);


        boolean rv = SignatureUtil.P1MessageVerify("SHA256WITHRSA", "测试".getBytes(), p1Signdata.getBytes(), x509Certificate.getPublickey());
        System.out.println(rv);
        rv = SignatureUtil.P7MessageVerifyAttach(p7SignAttach.getBytes());
        System.out.println(rv);
        rv = SignatureUtil.P7MessageVerifyDetach("测试".getBytes(), p7SignDetach.getBytes());
        System.out.println(rv);


    }

}
