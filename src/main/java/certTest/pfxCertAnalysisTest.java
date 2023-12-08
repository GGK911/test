package certTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.bouncycastle.pqc.jcajce.provider.bike.BCBIKEPublicKey;
import org.bouncycastle.util.encoders.Base64;

import java.io.File;
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
        // String cert1 = "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\certTest\\socialnetwork.pfx";
        String cert1 = "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\PkcsTest\\pfxTest02.pfx";
        String cert2 = "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\certTest\\沙箱证书.pfx";

        // 解析PFX文件
        Map<String, String> certMap = PFXUtil.decode(new File(cert1), "123456");
        System.out.println(JSONUtil.parseObj(certMap).toStringPretty());
        // 打印公钥
        byte[] pubKeyCerts = Base64.decode(certMap.get("pubKeyCert"));
        String publicKey = PemFormatUtil.pemFormat("PUBLIC KEY", pubKeyCerts);
        FileUtil.writeString(publicKey, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\certTest\\public_key.cer", "UTF-8");
        // 打印私钥
        byte[] priKeyCerts = Base64.decode(certMap.get("priKey"));
        PemFormatUtil.pemFormat("PRIVATE KEY", priKeyCerts);


    }
}
