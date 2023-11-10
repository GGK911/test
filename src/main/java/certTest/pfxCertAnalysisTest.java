package certTest;

import cn.com.mcsca.bouncycastle.jce.provider.X509CertificateObject;
import cn.com.mcsca.bouncycastle.util.encoders.Base64;
import cn.com.mcsca.extend.PKCS12KeyStore;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.util.Enumeration;
import java.util.Map;

/**
 * pfx证书解析
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-03 10:26
 **/
public class pfxCertAnalysisTest {
    @SneakyThrows
    public static void main(String[] args) {
        String cert1 = "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\certTest\\socialnetwork.pfx";
        String cert2 = "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\certTest\\沙箱证书.pfx";

        Map<String, String> certMap = PFXUtil.decode(new File(cert2), "123456");
        System.out.println(JSONUtil.parseObj(certMap).toStringPretty());


        PKCS12KeyStore store = new PKCS12KeyStore();
        store.engineLoad(new FileInputStream(cert1), "123456".toCharArray());
        Enumeration<String> enumeration = store.engineAliases();
        String keyAlias = enumeration.nextElement();
        Key key = store.engineGetKey(keyAlias, "123456".toCharArray());
        X509CertificateObject[] certChain = store.engineGetCertificateChain(keyAlias);
        System.out.println(new String(Base64.encode(certChain[0].getEncoded())));
        System.out.println(new String(Base64.encode(key.getEncoded())));
    }
}
