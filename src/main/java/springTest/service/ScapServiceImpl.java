package springTest.service;

import PkcsTest.CAShare.ScapCertificateSource2;
import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Base64;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author TangHaoKai
 * @version V1.0 2024/11/11 10:15
 */
@Slf4j
@Service
public class ScapServiceImpl {

    @SneakyThrows
    public String signP1(String waitSignData) {
        ScapCertificateSource2 scapCertificateSource2 = new ScapCertificateSource2("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\PkcsTest\\CAShare\\");
        // 检查pin码，获取私钥及公钥
        String priAndPub = scapCertificateSource2.checkPinCode("654321", "sm2256");
        System.out.println(String.format("%-16s", "priAndPub>> ") + priAndPub);
        // 签名
        String sighType = "SIGN_PKCS1";
        String sign = scapCertificateSource2.signMessage("654321", Base64.decode(waitSignData), "HASH_SM3", sighType, null);
        log.info("签名值：{}", sign);
        return sign;
    }

    @SneakyThrows
    public String signP7(String waitSignData, String cert) {
        ScapCertificateSource2 scapCertificateSource2 = new ScapCertificateSource2("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\PkcsTest\\CAShare\\");
        // 检查pin码，获取私钥及公钥
        String priAndPub = scapCertificateSource2.checkPinCode("654321", "sm2256");
        System.out.println(String.format("%-16s", "priAndPub>> ") + priAndPub);
        // 签名
        String sighType = "SIGN_PKCS7_A";
        String sign = scapCertificateSource2.signMessage("654321", waitSignData.getBytes(StandardCharsets.UTF_8), "HASH_SM3", sighType, cert);
        log.info("签名值：{}", sign);
        return sign;
    }

}
