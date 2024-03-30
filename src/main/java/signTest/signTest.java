package signTest;

import cn.com.mcsca.bouncycastle.crypto.params.ECDomainParameters;
import cn.com.mcsca.bouncycastle.jce.ECNamedCurveTable;
import cn.com.mcsca.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import cn.com.mcsca.bouncycastle.jce.spec.ECParameterSpec;
import cn.com.mcsca.bouncycastle.jce.spec.ECPublicKeySpec;
import cn.com.mcsca.bouncycastle.math.ec.ECCurve;
import cn.com.mcsca.bouncycastle.math.ec.ECPoint;
import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.com.mcsca.util.EncryptUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import lombok.SneakyThrows;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

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
}
