package httpTest;

import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.util.signature.SignatureSignUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/9/11 10:37
 */
public class CMSTest {
    @Test
    @SneakyThrows
    public void verifyTest() {
        // HttpRequest request = HttpRequest.post("http://183.66.184.22:803/cms-ora-interface/service/interface/applyAndDown").form("param", "{\n" +
        //         "    \"reqBody\": {\n" +
        //         "        \"param\": {\n" +
        //         "            \"applyName\": \"唐好凯\",\n" +
        //         "            \"applyCardType\": \"01\",\n" +
        //         "            \"applyCardNo\": \"51020219670316621X\",\n" +
        //         "            \"org\": \"无\"\n" +
        //         "        },\n" +
        //         "        \"certDay\": \"1\",\n" +
        //         "        \"certExts\": {\n" +
        //         "            \"1.2.156.10260.4.1.1\": \"51020219670316621X\"\n" +
        //         "        },\n" +
        //         "        \"appTypeid\": \"1765246873783844864\",\n" +
        //         "        \"sm2P10\": \"MIHJMHICAQAwEDEOMAwGA1UEAwwFTUNTQ0EwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQNUeI6+ybG01Rgm42xBWqPgjuwQ6bW5dFTCRZ9mvMamzpz4zCG0tTBpWBLVVjbB2/vOc3AKlwBQ5Dvpbu3zp8GoAAwCgYIKoEcz1UBg3UDRwAwRAIgKNpaY91vNAIcoLRBjqAuUCEfID2NUxdz39flnCqQ2boCIEAYg8hmNKUPqXynjx64seth3oyf4q4cMnI868OfLHEP\",\n" +
        //         "        \"rsaP10\": \"\"\n" +
        //         "    },\n" +
        //         "    \"reqHead\": {\n" +
        //         "        \"appId\": \"mcsca-39kxvapt01\",\n" +
        //         "        \"businessNO\": \"B700007\",\n" +
        //         "        \"methodCode\": \"B700007\",\n" +
        //         "        \"reqTime\": \"20230423110840768\",\n" +
        //         "        \"serialNo\": \"12345678910111213141516171819\",\n" +
        //         "        \"token\": \"MMQLU2APOAB6PSL8PNCS93WTLQQ9C5PR\"\n" +
        //         "    },\n" +
        //         "    \"sign\": \"\"\n" +
        //         "}");
        // try (HttpResponse response = request.execute()) {
        //     String body = response.body();
        String body = "{\"resHead\":{\"appId\":\"mcsca-39kxvapt01\",\"businessNO\":\"B700007\",\"methodCode\":\"B700007\",\"reqTime\":\"20230423110840768\",\"respTime\":\"20240911105102533\",\"serialNo\":\"12345678910111213141516171819\",\"token\":\"MMQLU2APOAB6PSL8PNCS93WTLQQ9C5PR\",\"resultCode\":\"2000\",\"resultMsg\":\"OK\"},\"resBody\":{\"orderId\":\"T183369974524005990424284450\",\"certDn\":\"CN=1765246873783844864@唐好凯@01@012,SERIALNUMBER=51020219670316621X,OU=localRA,O=MCSCA,C=CN\",\"certs\":[{\"ipAddress\":\"222.182.52.231\",\"raCaId\":\"1167254698785243136\",\"raCertid\":\"21422D1710CC4F908CB5A25EBDDD434B\",\"certDN\":\"CN=1765246873783844864@唐好凯@01@012,SERIALNUMBER=51020219670316621X,OU=localRA,O=MCSCA,C=CN\",\"ctmlName\":\"通用证书模板\",\"notBefore\":\"20240911105102441\",\"validity\":\"1\",\"extensionField\":\"[{\\\"extOID\\\":\\\"1.2.156.10260.4.1.1\\\",\\\"isCrux\\\":0,\\\"extName\\\":\\\"身份证号\\\",\\\"extValue\\\":\\\"51020219670316621X\\\"}]\",\"modelRoot\":\"1167254698785243136\",\"modelKeytype\":\"2\",\"modelKeylength\":\"3\",\"p10\":\"MIHJMHICAQAwEDEOMAwGA1UEAwwFTUNTQ0EwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQNUeI6+ybG01Rgm42xBWqPgjuwQ6bW5dFTCRZ9mvMamzpz4zCG0tTBpWBLVVjbB2/vOc3AKlwBQ5Dvpbu3zp8GoAAwCgYIKoEcz1UBg3UDRwAwRAIgKNpaY91vNAIcoLRBjqAuUCEfID2NUxdz39flnCqQ2boCIEAYg8hmNKUPqXynjx64seth3oyf4q4cMnI868OfLHEP\",\"flag\":true,\"oldSignCertSN\":\"\",\"oldEncCertSN\":\"\",\"modelUse\":\"1\",\"certFunction\":\"单证\",\"certType\":\"SM2256\",\"certSN\":\"6DF891B941DECE4A40FA24E2915FE30C\",\"signCert\":\"MIIC6zCCAo6gAwIBAgIQbfiRuUHezkpA+iTikV/jDDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwOTExMDI1MTAyWhcNMjQwOTEyMDI1MTAyWjB7MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjUxMDIwMjE5NjcwMzE2NjIxWDEtMCsGA1UEAwwkMTc2NTI0Njg3Mzc4Mzg0NDg2NEDllJDlpb3lh69AMDFAMDEyMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEDVHiOvsmxtNUYJuNsQVqj4I7sEOm1uXRUwkWfZrzGps6c+MwhtLUwaVgS1VY2wdv7znNwCpcAUOQ76W7t86fBqOCAT4wggE6MAsGA1UdDwQEAwIE8DAMBgNVHRMEBTADAQEAMB8GA1UdIwQYMBaAFPEiCmeYjfXjsqrDF2vAQh++S712MB0GA1UdDgQWBBTpDJxRqmzAWPSwbT3obAOjS2wKaDCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDAuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMCxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAgBggqgRzQFAQBAQQUDBI1MTAyMDIxOTY3MDMxNjYyMVgwDAYIKoEcz1UBg3UFAANJADBGAiEAh0tmg4od1VLbmuZEm4ll17FBfus0ysQfDb8YIoRueuQCIQCt+kt3TISRX4ejgPuvlNFBqWHBIIvu10Qi+zZ+W6BjLQ==\",\"p7b\":\"MIIE2QYJKoZIhvcNAQcCoIIEyjCCBMYCAQExADALBgkqhkiG9w0BBwGgggSuMIIBuzCCAV+gAwIBAgIQawXI9s8NzHnBm5PlBh9HRDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwIBcNMjAwNDI0MTQwNzU1WhgPMjA1MDA0MTcxNDA3NTVaMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASTFIp6vFYitNsFI35CmdfNDZMzHK9L2azopOpIJXLUYRpH5gI6v8IA2qKC8cAra2p+rb6qS75QUwDBxgVELq7Ko10wWzALBgNVHQ8EBAMCAQYwDAYDVR0TBAUwAwEB/zAdBgNVHQ4EFgQU8SIKZ5iN9eOyqsMXa8BCH75LvXYwHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwDAYIKoEcz1UBg3UFAANIADBFAiBjj2WAf1xek+XYSwjqsMkWuOPtafHL3cYhmkp9QVZZJwIhAMs3DFTfq6K+cs1Dqz4MsZzeODYzibmOYtcxEstB97cZMIIC6zCCAo6gAwIBAgIQbfiRuUHezkpA+iTikV/jDDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQwOTExMDI1MTAyWhcNMjQwOTEyMDI1MTAyWjB7MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjUxMDIwMjE5NjcwMzE2NjIxWDEtMCsGA1UEAwwkMTc2NTI0Njg3Mzc4Mzg0NDg2NEDllJDlpb3lh69AMDFAMDEyMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEDVHiOvsmxtNUYJuNsQVqj4I7sEOm1uXRUwkWfZrzGps6c+MwhtLUwaVgS1VY2wdv7znNwCpcAUOQ76W7t86fBqOCAT4wggE6MAsGA1UdDwQEAwIE8DAMBgNVHRMEBTADAQEAMB8GA1UdIwQYMBaAFPEiCmeYjfXjsqrDF2vAQh++S712MB0GA1UdDgQWBBTpDJxRqmzAWPSwbT3obAOjS2wKaDCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDAuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMCxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAgBggqgRzQFAQBAQQUDBI1MTAyMDIxOTY3MDMxNjYyMVgwDAYIKoEcz1UBg3UFAANJADBGAiEAh0tmg4od1VLbmuZEm4ll17FBfus0ysQfDb8YIoRueuQCIQCt+kt3TISRX4ejgPuvlNFBqWHBIIvu10Qi+zZ+W6BjLTEA\",\"notAfter\":\"20240912105102441\"}]},\"sign\":\"MEYCIQDH5o2FnSTptNBtUJfXqY0JwMBj9VqzRxLptcOV68nAwAIhANZKCly7Euv7MC1KlFJJ/HW99Xd5Kipu/PwJLS/z6jqG\"}";
        body = "{\n" +
                "    \"reqBody\": {\n" +
                "        \"certSn\": \"12\",\n" +
                "        \"busId\": \"test001\",\n" +
                "        \"reqestParam\": \"\",\n" +
                "        \"result\": \"\",\n" +
                "        \"isverify\": \"1\",\n" +
                "        \"verifyTime\": \"20240101000000000\"\n" +
                "    },\n" +
                "    \"reqHead\": {\n" +
                "        \"appId\": \"mcsca-fbk4zkrwo2\",\n" +
                "        \"businessNO\": \"B710001\",\n" +
                "        \"methodCode\": \"B710001\",\n" +
                "        \"reqTime\": \"20190507110840768\",\n" +
                "        \"serialNo\": \"12345678910111213141516171819\",\n" +
                "        \"token\": \"V93TV8JHOMTQQ6CJ55DVI6LFP4IC9E6L\"\n" +
                "    },\n" +
                "    \"sign\": \"MEYCIQDbZe84wMqGIux4hGyspTk0XtOfyoj45IEtQfFdHxjw/gIhAKso/X7Sr7WzitqQ39zcIjwYjd0aPx8MZ1Nqk33V+AIG\"\n" +
                "}";

        byte[] bytes = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\json.txt");
        body = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(String.format("%-16s", "body>> ") + body);
        JSONObject bodyJson = JSONUtil.parseObj(body);

        String signValue = (String) bodyJson.get("sign");
        Map<String, Object> map = new HashMap<>();
        map.put("reqHead", bodyJson.get("reqHead"));
        map.put("reqBody", bodyJson.get("reqBody"));

        String responseStr = SignatureSignUtil.getSortJsonStr(map);
        SecuEngine se = new SecuEngine();
        // 系统私钥
        String priKeyBase64 = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIHFXc14Tdlz2LxQEL10o9elbo6PkFzLEHRvAtbt+2xLjoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        // 红鼎
        priKeyBase64 = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIFVP1eFU2X7L6WE8XxrVznKy/wDOM3Vh+FK7DwEdMmaIoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        String sign = se.SignDataBySM2(priKeyBase64, responseStr.getBytes(StandardCharsets.UTF_8));
        // signValue = sign;


        String text = SignatureSignUtil.getSortJsonStr(map);
        // fastJson 排序验证
        // 系统公钥
        String pubKeyQHex = "5183089abe52e7216b0c3c8f232822f25c4d889f0ff346b697cf354813aae6696106f7fda365f31743d67a6424e248787a6e54c8cfd412adfed27299933eb3f5";
        // 测试专用应用
        pubKeyQHex = "e783bee70c33d7397f2b1605f4cd6c919455c33fa6276ba2a46cbde091cc87cf1427a4f03225b84c4d4d4c5886a5b783f140a867bbedcb3a37008726f408a0c0";
        // 红鼎
        pubKeyQHex = "a78045407d42af0740df1f161bfdbb7f0e90a0c35774ff91b89c3e10c45a250affd58813dbeef365efb49104f7f8b2c9984d4815950b3441cfd8187921361a1b";
        boolean b = se.VerifySignDataWithSM2ByPublicKey(pubKeyQHex, text.getBytes(StandardCharsets.UTF_8), signValue);
        System.out.println(String.format("%-16s", "b>> ") + b);
        if (!b) {
            // huTool 排序验证
            text = JSONUtil.toJsonStr(map);
            b = se.VerifySignDataWithSM2ByPublicKey(pubKeyQHex, text.getBytes(StandardCharsets.UTF_8), signValue);
            System.out.println(String.format("%-16s", "b>> ") + b);
        }
        // }

    }
}
