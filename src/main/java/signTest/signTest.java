package signTest;

import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.com.mcsca.util.EncryptUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;

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
    // static String privateKey = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIE3kknNhUwexGBNGCz5QoSnH5PVk2fGCvRhs4XSIRhT/oIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
    // static String publicKey = "0cb9bc3f935f7aa25b5fca543ccbfa763777a88aa5237ad6d0df07f4d1ac5a282ecbc9c4e94edf366a83b4af6494e3da040d1ea0177ff28bebdc8fd31fb13b3c";
    static String M = "test";
    static String hex = "012345679abcde";

    // 合同dev系统的公私钥
    static String privateKey = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIH+X+j2Lh38yuQMXCYlHpPYU6Wk2lG0/TRygmNZc//atoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
    static String publicKey = "58bf6f2f4f9be896f45ed866aceffa506c4f02886ff42e616fed189bb24782c2ef55bf8f52d898dfda106cdc7b9e9ad28b3d075dae179986987b7bbac1672f22";


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
        byte[] verifyMessageBytes = M.getBytes(StandardCharsets.UTF_8);
        System.out.println(secuEngine.VerifySignDataWithSM2ByPublicKey(publicKey, verifyMessageBytes, sign));
        // System.out.println(secuEngine.VerifySignDataByRSACert(publicKey, M.getBytes(StandardCharsets.UTF_8), sign, "SHA256"));

        // pki-core的
        System.out.println("pki-core:" + SignatureUtil.verifySignByPublicKey(publicKey, M, sign));
    }

    /**
     * hutool提供的
     */
    @Test
    @SneakyThrows
    public void signTest02() {
        SM2 sm2 = SmUtil.sm2(privateKey, null);
        String M = "客户委托大陆云盾电子签约平台保管和使用其电子签名私钥;客户电子签名私钥使用仅限用于大陆云盾电子签约平台的业务场景，并委托大陆云盾电子签约平台平台代为签署相关的协议。";
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

    private static final Provider BC = new BouncyCastleProvider();

    @Test
    @SneakyThrows
    public void pdfSignValueTest() {
        String pri = "6PB4Ro4WXmseFpOp8UxAFxRmd48Fba9jUzrdFHpb9V+BGR+hhsmDS6szwO4YBY9gaAbaRFpImfTLPGSrbI2nYOzuKEsKHAGxq+7B8Bzxzl4w4Ldk7zCFJAJg+80nmg0+VceY3QtxSzOlCnWw/51WX2CrXfQGgGoUGE7lGYoIn3r09oCxtR/qy9INTuL+NVz2C7yCBcTMkU7i4yoCpGhT3EQ+haRkmOUzL6UiCttxSbX+KHKIx4yPiyD7zrJu0GiuMYBxoxKV0NGLDZ6yUdDpCg==";
        String aes = "eR7jCBl9xPUDnOknmJoU97J0E8No8uPXEZHfkIJVioVOyBaLkkWKGbpgYGRyyCrkAfNaOfVyGlJ7SAdGwv2gJ+WjRsG3wHKpC8fccXbfG/NoyeF5JVfI9hpiugC8ORoAKrXYtK7lE0QyEVbLkI1nBfPAPVZWZu8fK2kdyFTiGX7k54QZ/7jVPrhEuWt+HGZD+l5kbJZtOqh3z5X9XkLqCjXqebTRd2nU/Re6//VKmiLfwsHW7bCQPw4Aw1tJ9CUEgn7gn/MfgZu8D85SyGPj57uJS+W5HkEJGIVei9v4CY3kTtU/XYB4TnnR6tGMhJ3t8B4VaR1r/h/0d0K5P3JqtA==";

        String sign = "";
        try {
            String privateKey = EncryptUtil.decrypt("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCtOM6sFUxBoVYglTlHIHpVBG/385Lawtohwu88YeOn7uJcY6yTfKfWdsfYEMrMa/Pfvp3uC+Et7ECzXlkM4YqeWHtGyxtLRXVXXlADi4IsbFEMUNhegrEMyq/yvees5ECsnxAFhug6w4f+CBhepWwDCwu0MALa5xnfVYSF9iBz3fXWpixdRRnaEs5dzgZWy3kJ2fJ/mTWbicm7AjvRuGJujsE2qqJtXcOrDKU/9ohAx1ZAW+nB0Dxs7/xOUzLZPJwYLxO8uiJiYCbxvnbrLF1aYKlpBCD6BbQF2mJwjlll0S93BzKZcYvYYYIj1obtq97O8znGjcuRgMQs8Ey+VB+vAgMBAAECggEAPdtaWjMkzw74/ZusH40mgjOadFXDrGEGmiXNXqeqLy7sIIfreaN7H+e8x5h/gu5N4SllpjsRx19lX2girqnf4VnBc+9VqNR96ZwhQJLSAmEPtDEugtlythmvKSTNlXzQ55PJmd+qEEoAxyNG6I1z+8Y3ALpgWqFKKOmV8GyK/DTAqjJJi/j7PyqLqqpQuqGs87Rjs/qbN8ScGs8AujDC+eadXGgmVk6AtRr1wkf1MpQp+xpBm6QYQTnqlCxmIluTMlLPmj9GhgxFsrgDWAVp6+/9AiRx6gSfu3H1+6ASQ30B7cusd73ZtbFayLfMa8QPUQMMgByaTypTTgyN4JOYEQKBgQDqyaw8JEdEOeHXbEA+vnEsuuKCqZ+noOOQibeweeBxUEHt0nfIo9vutiI8QVMONOJveqawk8qfiIUYaVte3zhClLYk9rys54gNMJjIXYr0WbbnUehMLSVUhBZymz6T7aBaWyHyf/97vYlcmJKHoCew8FZrEpXwstghLMuf4cYZyQKBgQC83zK2oI3UfItJsWuGrJL3KCyHeHHpZrcdQBcVHWRwBdALWpmJJwkOiB9PvtBgCgUIjyoj3HoGPXJBDxUj/EYH6VWJITW1ZDxmpKelCUCPFmkOUjS3YYGty378MAmDS3WLlKnyzTAbrt31+0AXMqU8OHANI8WvZtFNSozg9iKptwKBgGgXFvvm3Y2a18xI2sa2abh59jgVeYm4o4sN81kS/3VdLo2AVMioFLZlGxJ5p5fRzF2+E66PJzLJNLCY7QBHmEq0YXhLx2QklcW7ONED37nrGFK/lmxHS5iHougWeYzducy1QHyhUKQMaJybq8LjNxWTx8xahg0bTQSQNopgbxI5AoGBAJsXzVUaUl0CSH6jKmDUpXo/ixFTXncC2aszTcEQ+cDjhQtNwnZVj6JXNR8O2Z2DnM6CgWAhVDJ7kq7J69o49mjYulx44NmrDc5bty5WgqT9CheweYl8kDheuk/sQmOGO2f7E/NFexPAbJPpVZ+2/uiMj7a6gUKfc4+8gCLa+2vRAoGAInx8ASLZEGZkG98SKT111Fx3/VJn+XV4qpqu1rOCA1rgZaggqiIYBcAR0+K6YpSWflb7uFtHEjVwA7y86CKyU81yxkMcogDyZ/PG24isjqaEo8KO8vQqQpTjHJxCZBLp1k7gNrM0aoQuXblCH4jrEfwhcjumjv0HYy9FoOSzt4o=", aes, pri);
            String signDataBySM2 = new SecuEngine().SignDataBySM2(privateKey, Hex.decode(sign));
            System.out.println(signDataBySM2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Data
    @AllArgsConstructor
    static class ReqHead {
        String appId;
        String version;
        String productAppId;
        String reqTime;
    }

    @Data
    @AllArgsConstructor
    static class ReqBody {
        String isSeal;
    }

    /**
     * 另外一种构造对象的json
     */
    @Test
    @SneakyThrows
    public void objToJsonStrTest02() {
        // 测试 统一账户内部产品ID mcsca-4qsnlfee12 新电子合同
        String priKey = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIH+X+j2Lh38yuQMXCYlHpPYU6Wk2lG0/TRygmNZc//atoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        Map<String, Object> map = new HashMap<>();
        ReqHead reqHead = new ReqHead("1346996908593262592", "1", "mcsca-4qsnlfee12", "20240401104700000");
        ReqBody reqBody = new ReqBody("1");
        map.put("reqHead", reqHead);
        map.put("reqBody", reqBody);
        String signJson = com.alibaba.fastjson.JSONObject.toJSONString(map, SerializerFeature.MapSortField, SerializerFeature.SortField);
        System.out.println("签名原文>> " + signJson);

        String sign = SignatureUtil.doSign(priKey, signJson);
        System.out.println("签名值>> " + sign);
    }

    @Test
    @SneakyThrows
    public void verifySM2Test01() {
        // 我自己生成的
        // String pri = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIH+X+j2Lh38yuQMXCYlHpPYU6Wk2lG0/TRygmNZc//atoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        // String pub = "WL9vL0+b6Jb0XthmrO/6UGxPAohv9C5hb+0Ym7JHgsLvVb+PUtiY39oQbNx7nprSiz0HXa4XmYaYe3u6wWcvIg==";

        // 数据库的公私钥
        String pri = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQghzBYj8PWQBmtR9E4eXoXbQUgiWj0iX46V9vgDPTX63ygCgYIKoEcz1UBgi2hRANCAAQrbGDHhS7gOS3/cX3QChJD3rBLiPYvYOOCXy2dNeTBy70qPzglR1e45sryQQMJTLeluN2I7jED8NDIRglqHZWB";
        String pub = "A0IABCtsYMeFLuA5Lf9xfdAKEkPesEuI9i9g44JfLZ015MHLvSo/OCVHV7jmyvJBAwlMt6W43YjuMQPw0MhGCWodlYE=";

        // 使用公司包生成的
        // String pri = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQghm7+/CldqqFw2WpL0XwHQLHI7KTifCsGTnemn9KGEJegCgYIKoEcz1UBgi2hRANCAASwkTKpv879LjHGQAhWgA8AV5zVjeWLYjhR0kgNXbAq4ExPn2Dc8Zev1BA9lu0YfWrf719j7AXsrz3KGCZRLacz";
        // String pub = "sJEyqb/O/S4xxkAIVoAPAFec1Y3li2I4UdJIDV2wKuBMT59g3PGXr9QQPZbtGH1q3+9fY+wF7K89yhgmUS2nMw==";

        String M = "客户委托大陆云盾电子签约平台保管和使用其电子签名私钥;客户电子签名私钥使用仅限用于大陆云盾电子签约平台的业务场景，并委托大陆云盾电子签约平台平台代为签署相关的协议。";
        SecuEngine secuEngine = new SecuEngine();
        // 签名
        String signValue = secuEngine.SignDataBySM2(pri, M.getBytes(StandardCharsets.UTF_8));
        signValue = "MEUCIQCXJS0of61zAsikUoDxf41U/POrWqe/D/hT1JFj+ivAHgIgbZpKF2BQ7bHIoNsy2E8xpeO8irFDqPSPtbia22AGasE=";
        // 验签
        String publicKeyHex = Hex.toHexString(Base64.decode(pub));
        if (publicKeyHex.length() > 128) {
            publicKeyHex = publicKeyHex.substring(8);
        }
        System.out.println("publicKeyHex>> " + publicKeyHex);
        System.out.println(secuEngine.VerifySignDataWithSM2ByPublicKey(publicKeyHex, M.getBytes(StandardCharsets.UTF_8), signValue));
    }

    @Test
    @SneakyThrows
    public void verifyRSATest01() {
        String signValue = "Vid+CJ0o6dt7yuHKrtmW0X5Ky9gsUejCHSLZ+FxvWnww4OlLHuWnb8PMibi+dBpwmbLqJUdiQTYF4bt3O1clZspqk5A3NJ1vFHQA5lIm1R02pYA0HV1Wq8C2r18pA4XflzzUuvMgLzxyjY9eb5ui8hMyYZA2EKWNgTFf0Zb3N4OWkdF9bGaoq4Kl/+lygHSeqdoS50jHUUqRU8cRHUVUSLUnbND2aR9M4ERD2bcInczZqQL5MN9RhFjjRnWG4D0Jnc7+iZwRnaxTU6U1t65w2y27wgUBEWQm5zCp/inNMd8vNbx5hQmjOS2ScvwfJwIzQfTPUVOexw+zy8HiDMLz/A==";
        // 数据库的公私钥
        String pri = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQghzBYj8PWQBmtR9E4eXoXbQUgiWj0iX46V9vgDPTX63ygCgYIKoEcz1UBgi2hRANCAAQrbGDHhS7gOS3/cX3QChJD3rBLiPYvYOOCXy2dNeTBy70qPzglR1e45sryQQMJTLeluN2I7jED8NDIRglqHZWB";
        String pub = "A0IABCtsYMeFLuA5Lf9xfdAKEkPesEuI9i9g44JfLZ015MHLvSo/OCVHV7jmyvJBAwlMt6W43YjuMQPw0MhGCWodlYE=";
        String pubCert = "MIIE1jCCA76gAwIBAgIQRDvjyUTGHRKczuFLe/AU0DANBgkqhkiG9w0BAQsFADA9MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAsMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0yNDA0MTYwNjA1MzVaFw0yNzA0MTYwNjA1MzVaMHsxCzAJBgNVBAYTAkNOMQ0wCwYDVQQKDAQxMTQ5MRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLjAsBgNVBAMMJVQxMTc2NzU1NjM3MzMwMTE2NjA4QOWUkOWlveWHr0AwMUAxNjYwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCMNTaRCIORAIhd9QlE2pyqUlKGcghEhNKfFUAoAjurwuOWrM+FuHlcI1tkhHV3sk7912Dj5lM3iFQ6+UQWMLbM8AVRx3DeYnlqwRFg5I886nCKUtA00CE+vbTAy+guKzINfwwRqpTX+8AG6S9sMN87nED/yloF/4P0t7f9joWoufgKGP3N0Q+229q9IyxcpWtlq5FsBI3IU1qoym/nsL1nBzdtpqe4XRXo7uvwK26pJR+rQDfycuo6lT/tn1eTG3L6BqHXdhz8SFnoF+B3Fkai/exwH8WOSKIscONw36cDJmKeEr7A6PIwE7NNtjx3675iUYc8LeBeyeFFcN7sOIW3AgMBAAGjggGSMIIBjjAfBgNVHSMEGDAWgBS5UekB8KkjH4pO6Q5rB9AMSi2moDAdBgNVHQ4EFgQUDvtGgO2cFQC7yp2F29jNA+jT2m0wDAYDVR0TBAUwAwEBADBEBgNVHSAEPTA7MDkGByqBHIeECwMwLjAsBggrBgEFBQcCARYgaHR0cHM6Ly93d3cubWNzY2EuY29tLmNuL2Nwcy5odG0wCwYDVR0PBAQDAgP4MHgGA1UdHwRxMG8wSKBGoESkQjBAMQswCQYDVQQGEwJDTjEMMAoGA1UECgwDSklUMRAwDgYDVQQLDAdBREQxQ1JMMREwDwYDVQQDDAhjcmwxMTE3NTAjoCGgH4YdaHR0cDovLzEyNy4wLjAuMS9jcmwxMTE3NS5jcmwwXAYIKwYBBQUHAQEEUDBOMCgGCCsGAQUFBzAChhxodHRwOi8vMTI3LjAuMC4xL2NhaXNzdWUuaHRtMCIGCCsGAQUFBzABhhZodHRwOi8vMTI3LjAuMC4xOjIwNDQzMBMGA1UdJQQMMAoGCCsGAQUFBwMBMA0GCSqGSIb3DQEBCwUAA4IBAQAYhPjF2LhISXY6mGu6l0zHwoj+S76UUY0dfRjbMQAUzj9Wzc1WqFCqGdLmhNi5oPerxlUolVhvsGEfJCro5Fk1Xpffqvc6eARCDH8RSL/GBvSKx7nlFFvp7A4VSMAcRUc72u8UQZEXBumBcCsEH8M8KTxu+P2v6f5Pugc2Xm5S0wfyNBgl7cCDD54evHtjE41t5bPbSpmxRtaznVkLKw/CTtJWwYQ6cz/a74kTGOjz08FvHrsxivhK/7GqKdwDpZE/zy70UQHyWIjX8WcF1U0mTZw0zSkhy8E1iYez7CeTTTtaJld92QZ113XvElDZ5vCbnMevwNisjeap8jw5byfB";
        String M = "客户委托大陆云盾电子签约平台保管和使用其电子签名私钥;客户电子签名私钥使用仅限用于大陆云盾电子签约平台的业务场景，并委托大陆云盾电子签约平台平台代为签署相关的协议。";
        SecuEngine secuEngine = new SecuEngine();
        System.out.println(secuEngine.VerifySignDataByRSACert(pubCert, M.getBytes(StandardCharsets.UTF_8), signValue, "SHA256"));

    }

}
