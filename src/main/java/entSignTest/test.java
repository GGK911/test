package entSignTest;

import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-02 10:49
 **/
public class test {
    private static final String PRI_KEY = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEICE2psktFMQmGTrM/EEx2uyAfclL8L8RI/ybfYhfYCnFoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
    private static final String PUB_KEY = "60934e5116dc2bc8935fa3535778be1cf80605f4f0b4f66299588bc059001e534bbedfff29163ff928a5be880ed7e698a542cd86c9eec7aa4067320ca7f7def9";

    @SneakyThrows
    public static void main(String[] args) {
        Object reqHead = "{\n" +
                "\t\t\"version\": \"1.0.0\",\n" +
                "\t\t\"appId\": \"1217622160365867008\",\n" +
                "\t\t\"reqTime\": \"20231022113900000\",\n" +
                "\t\t\"productAppId\": \"\",\n" +
                "\"customerId\":\"1028\",\n" +
                "\t\t\"serialNo\": \"1028\"\n" +
                "\t}";
        Object reqBody = "{\n" +
                "    \"userId\":\"1706510795195555840\",\n" +
                "    \"smsCode\":\"175679\",\n" +
                "    \"direction\":\"2\",\n" +
                "    \"coordinate\":\"200,350\"\n" +
                "}";
        fastJson(reqHead, reqBody);

        // 创建一个长度为19的数组
        

    }

    @SneakyThrows
    public static String huTool(Object reqHead, Object reqBody) {
        JSONObject jsonHuTool = new JSONObject();
        jsonHuTool.set("reqHead", reqHead);
        jsonHuTool.set("reqBody", reqBody);
        String signHuTool = SignatureUtil.doSign(PRI_KEY, jsonHuTool.toString());
        System.out.println("使用HuTool工具签名值：" + signHuTool);
        SecuEngine secuEngine = new SecuEngine();
        if (secuEngine.VerifySignDataWithSM2ByPublicKey(PUB_KEY, jsonHuTool.toString().getBytes(StandardCharsets.UTF_8), signHuTool)) {
            System.out.println("使用HuTool工具,公钥验签通过");
        } else {
            System.out.println("使用HuTool工具,公钥验签未通过");
        }
        System.out.println("使用HuTool工具,验签原文：\n" + jsonHuTool);
        return signHuTool;
    }

    @SneakyThrows
    public static String fastJson(Object reqHead, Object reqBody) {
        Map<String, Object> map = new HashMap<>();
        map.put("reqHead", reqHead);
        map.put("reqBody", reqBody);
        String json = com.alibaba.fastjson.JSONObject.toJSONString(map, SerializerFeature.MapSortField, SerializerFeature.SortField);
        String signFastJson = SignatureUtil.doSign(PRI_KEY, json);

        System.out.println("使用FastJson工具签名值：" + signFastJson);
        SecuEngine secuEngine = new SecuEngine();
        if (secuEngine.VerifySignDataWithSM2ByPublicKey(PUB_KEY, json.getBytes(StandardCharsets.UTF_8), signFastJson)) {
            System.out.println("使用FastJson工具,公钥验签通过");
        } else {
            System.out.println("使用FastJson工具,公钥验签未通过");
        }
        System.out.println("使用FastJson工具,验签原文：\n" + json);
        return signFastJson;
    }

}
