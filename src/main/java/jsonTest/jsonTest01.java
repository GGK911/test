package jsonTest;

import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.serializer.SerializerFeature;
import extense.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import pdfTest.PdfUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2023-12-20 17:03
 **/
public class jsonTest01 {
    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("key", "1");
        jsonObject.set("base64", true);
        String toStringPretty = jsonObject.toStringPretty();
        System.out.println(toStringPretty);

        PdfUtil.FillImageParam imageParam = JSONUtil.toBean(toStringPretty, PdfUtil.FillImageParam.class);
        System.out.println(imageParam.getBase64());
    }

    @Test
    public void jsonToBean() {
        String jsonStr = "{\"name\":\"唐好凯\",\"age\":\"100\"}";
        Person person = JSONUtil.toBean(jsonStr, Person.class);
        System.out.println(person.getName());
        System.out.println(person.getAge());
    }

    @Test
    public void jsonToBean2() {
        String jsonStr = "{\"array\":[{\"name\":\"唐好凯\",\"age\":\"100\"},{\"name\":\"唐好凯\",\"age\":\"100\"}]}";
        Object array = JSONUtil.parseObj(jsonStr).get("array");
        JSONArray jsonArray = JSONUtil.parseArray(array);
        for (Object o : jsonArray) {
            Person person = JSONUtil.toBean(o.toString(), Person.class);
            System.out.println(person.getName());
            System.out.println(person.getAge());
        }
    }

    @Test
    @SneakyThrows
    public void mapToJson() {
        Map<String, String> map = new HashMap<>();
        map.put("123", "qwe");
        map.put("456", "asd");
        map.put("789", "zxc");
        System.out.println(JSONUtil.toJsonPrettyStr(JSONUtil.toJsonStr(map)));
    }

    @Test
    @SneakyThrows
    public void objToJsonStrTest01() {
        Map<String, String> map = new HashMap<>();
        map.put("reqHead", "{\n" +
                "    \"version\": \"1\",\n" +
                "    \"appId\": \"1346996908593262592\",\n" +
                "    \"reqTime\": \"20240401104700000\",\n" +
                "    \"productAppId\": \"mcsca-4qsnlfee12\"\n" +
                "}");
        map.put("reqBody", "{\n" +
                "    \"isSeal\": \"1\"\n" +
                "}");
        System.out.println(com.alibaba.fastjson.JSONObject.toJSONString(map, SerializerFeature.MapSortField, SerializerFeature.SortField));
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

    @Test
    @SneakyThrows
    public void objToJsonStrTest02() {
        String priKey = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIH+X+j2Lh38yuQMXCYlHpPYU6Wk2lG0/TRygmNZc//atoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        Map<String, Object> map = new HashMap<>();
        ReqHead reqHead = new ReqHead("1346996908593262592", "1", "mcsca-4qsnlfee12", "20240401104700000");
        reqHead = new ReqHead("1346996908593262592", "1", null, "20240401104700000");
        ReqBody reqBody = new ReqBody("1");
        map.put("reqHead", reqHead);
        map.put("reqBody", reqBody);
        String signJson = com.alibaba.fastjson.JSONObject.toJSONString(map, SerializerFeature.MapSortField, SerializerFeature.SortField);
        System.out.println("签名原文>> " + signJson);

        String sign = SignatureUtil.doSign(priKey, signJson);
        System.out.println("签名值>> " + sign);

        String test = null;
        System.out.println(test.toString());
    }

    @Test
    @SneakyThrows
    public void jsonChangeTest() {
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("123", "321");
        add(jsonObject);
        System.out.println(jsonObject.toString());

    }

    public com.alibaba.fastjson.JSONObject add(com.alibaba.fastjson.JSONObject jsonObject) {
        jsonObject.put("456", "654");
        return jsonObject;
    }

}
