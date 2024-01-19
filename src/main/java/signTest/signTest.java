package signTest;

import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * 签署测试
 *
 * @author TangHaoKai
 * @version V1.0 2024/1/19 10:22
 **/
public class signTest {
    // SM2
    // static String privateKey = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIAPOVgOGLmH3cUt/DWKTHaJ9BsHmCxsrvhLymBwCz9U8oIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
    // static String publicKey = "cf08e5115f97033e5e30ec26b7d3b3a1ce959ae946681eae2b0660ffb63cbb38810e9f16412943957c3d9186cd6c6b38ad967432aec5dc296ca99f31b1bc57e8";

    // static String privateKey = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIE3JgR2K0G99/Uk5fU9rtdMDtXe2M+Y5eUCTLY+4cBaeoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
    // static String publicKey = "9cd6f54012a508b2708af45cd56f62ee3e11c9e6958e49acda16577132af91c2bab94ff370ef46b8daa66804ab9c0b13c160d0495a11c9973c5d35452e59ebdc";

    // SM2
    static String privateKey = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIE3kknNhUwexGBNGCz5QoSnH5PVk2fGCvRhs4XSIRhT/oIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
    static String publicKey = "0cb9bc3f935f7aa25b5fca543ccbfa763777a88aa5237ad6d0df07f4d1ac5a282ecbc9c4e94edf366a83b4af6494e3da040d1ea0177ff28bebdc8fd31fb13b3c";
    static String M = "test";
    static String hex = "012345679abcde";

    /**
     * 公司封装的
     */
    @Test
    @SneakyThrows
    public void signTest() {
        System.out.println(publicKey.getBytes(StandardCharsets.UTF_8).length);
        String sign = SignatureUtil.doSign(privateKey, M);
        System.out.println(sign);
        SecuEngine secuEngine = new SecuEngine();
        System.out.println(secuEngine.VerifySignDataWithSM2ByPublicKey(publicKey, M.getBytes(StandardCharsets.UTF_8), sign));
        // System.out.println(secuEngine.VerifySignDataByRSACert(publicKey, M.getBytes(StandardCharsets.UTF_8), sign, "SHA256"));
    }

    /**
     * hutool提供的
     */
    @Test
    @SneakyThrows
    public void signTest02() {
        SM2 sm2 = SmUtil.sm2(privateKey, null);
        byte[] sign = sm2.sign(M.getBytes(StandardCharsets.UTF_8));
        String signValue = Base64.toBase64String(sign);
        System.out.println(signValue);
        SecuEngine secuEngine = new SecuEngine();
        System.out.println(secuEngine.VerifySignDataWithSM2ByPublicKey(publicKey, M.getBytes(StandardCharsets.UTF_8), signValue));
    }

    @Test
    @SneakyThrows
    public void verifyTest() {
        String sign = "MEUCIQD2M5fesCRE282lcFbOgYE/CBUHCJCjN8STtQ9km/RMwgIgNz7KGaZPEf5f7ypi4sOQx2lM6OZVilj+brXMMyDTwpk=";
        String M = "{\"reqBody\":\"321\",\"reqHead\":\"123\"}";
        SecuEngine secuEngine = new SecuEngine();
        System.out.println(secuEngine.VerifySignDataWithSM2ByPublicKey(publicKey, M.getBytes(StandardCharsets.UTF_8), sign));
    }
}
