package certTest.pkiCoreTest;

import cn.com.mcsca.pki.core.util.KeyUtil;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.com.mcsca.pki.core.x509.X509Certificate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Date;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/17 16:30
 */
public class Pkcs12SignTest {


    /**
     * @param args
     */
    public static void main(String[] args) {


        try {
            //从SM2 私钥文件获取私钥对象
            PrivateKey sm2Key = KeyUtil.getPrivateKeyBySM2("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\certTest\\pkiCoreTest\\1765246873783844864@唐好凯@01@023_sig.sm2", "aU%Nad#7");
//			System.out.println(Base64.toBase64String(sm2Key.getEncoded()));
            String p1Signdata = new String(SignatureUtil.P1MessageSign("SM3WITHSM2", "测试".getBytes(), sm2Key));
//			System.out.println(p1Signdata);

            // 2中获取公钥证书方式
            // 方式1：从SM2 私钥文件获取公钥证书对象
            // X509Certificate cert = CertUtil.getX509CertificateBySM2("d:\\四川国库业务测试证书.sm2");
            // 方式2：直接读cer文件
            X509Certificate cert = new X509Certificate(Files.readAllBytes(Paths.get("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\certTest\\pkiCoreTest\\1765246873783844864@唐好凯@01@023_sig_crt.cer")));
            if (cert.getNotAfter().after(new Date())) {
                System.out.println("过期证书");
            }
            boolean rv = false;

            String p7SignAttach = new String(SignatureUtil.P7MessageSignAttach("SM3WITHSM2", "测试".getBytes("UTF-8"), sm2Key, cert));
            System.out.println(p7SignAttach);

            String p7SignDetach = new String(SignatureUtil.P7MessageSignDetach("SM3WITHSM2", "测试".getBytes("UTF-8"), sm2Key, cert));

            System.out.println(p7SignDetach);


            rv = SignatureUtil.P7MessageVerifyAttach(p7SignAttach.getBytes());
            System.out.println(rv);
            rv = SignatureUtil.P7MessageVerifyDetach("测试".getBytes(), p7SignDetach.getBytes());
            System.out.println(rv);


            rv = SignatureUtil.P1MessageVerify("SM3WITHSM2", "测试".getBytes(), p1Signdata.getBytes(), cert.getPublickey());
            System.out.println(rv);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
