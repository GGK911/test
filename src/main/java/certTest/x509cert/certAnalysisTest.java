package certTest.x509cert;

import cn.hutool.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
public class certAnalysisTest {
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
