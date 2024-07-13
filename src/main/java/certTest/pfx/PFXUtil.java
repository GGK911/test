package certTest.pfx;

import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.util.CertUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-03 10:20
 **/
public class PFXUtil {
    /***
     * 通过PFX证书获取证书信息
     * @param file 证书文件
     * @param pwd 密码
     * @return 证书信息json
     */
    public static Map<String, String> decode(File file, String pwd) throws Exception {
        SecuEngine secuEngine = new SecuEngine();
        //公钥
        String pubKeyCert = secuEngine.ParsingPfx(new FileInputStream(file), pwd, 1);
        //私钥证书
        String priKey = secuEngine.ParsingPfx(new FileInputStream(file), pwd, 2);

        String startDate = CertUtil.parseCert(pubKeyCert, String.valueOf(4));
        String endDate = CertUtil.parseCert(pubKeyCert, String.valueOf(5));
        String subject = CertUtil.parseCert(pubKeyCert, String.valueOf(1));
        String serialNumber = CertUtil.parseCert(pubKeyCert, String.valueOf(2));
        String isuseStr = CertUtil.parseCert(pubKeyCert, String.valueOf(3));
        String isuse = isuseStr.substring(isuseStr.indexOf("CN=") + 3);
        //不解算法 暂时没用 测试环境报错
        String alg = CertUtil.parseCert(pubKeyCert, String.valueOf(9));
        // 目前只能解析MCSCA颁发的证书
        String idNum = subject.substring(subject.indexOf("SERIALNUMBER=") + 13, subject.indexOf(",CN="));
        String[] splitStr = subject.split("@");
        String award = "";
        if (splitStr.length > 1) {
            award = splitStr[1];
        }
        boolean isRsa = alg.startsWith("1.2.840.113549.1.1.1");
        Map<String, String> map = new HashMap<>(10);
        map.put("pubKeyCert", pubKeyCert);
        map.put("priKey", priKey);
        map.put("certEffectiveDate", startDate);
        map.put("certExpirationDate", endDate);
        map.put("serialNumber", serialNumber);
        map.put("idNum", idNum);
        map.put("award", award);
        map.put("isRsa", Boolean.toString(isRsa));
        map.put("isuse", isuse);
        map.put("certSubject", subject);
        return map;
    }
}
