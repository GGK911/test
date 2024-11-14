package springTest.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TangHaoKai
 * @version V1.0 2024/11/12 20:06
 */
@RestController
public class CAShareController {
    @RequestMapping("/CAShare/certService/dualCertApplyAndDownload")
    public Object applyAndDown() {
        JSONObject cmsJson = JSONUtil.parseObj("{\n" +
                "    \"resHead\": {\n" +
                "        \"appId\": \"mcsca-39kxvapt01\",\n" +
                "        \"businessNO\": \"B700007\",\n" +
                "        \"methodCode\": \"B700007\",\n" +
                "        \"reqTime\": \"20230423110840768\",\n" +
                "        \"respTime\": \"20241114174522319\",\n" +
                "        \"serialNo\": \"12345678910111213141516171819\",\n" +
                "        \"token\": \"3IP4VUX4POZI8EX4XPSS6LCGCWLPQBTI\",\n" +
                "        \"resultCode\": \"2000\",\n" +
                "        \"resultMsg\": \"OK\"\n" +
                "    },\n" +
                "    \"resBody\": {\n" +
                "        \"orderId\": \"T185699683431311360043113267\",\n" +
                "        \"certDn\": \"CN=1765225223004700672@唐好凯@01@239,SERIALNUMBER=371724200206052210,OU=localRA,O=MCSCA,C=CN\",\n" +
                "        \"certs\": [\n" +
                "            {\n" +
                "                \"ipAddress\": \"222.182.52.122\",\n" +
                "                \"raCaId\": \"1167254698785243136\",\n" +
                "                \"raCertid\": \"004A5C0D39694F15B0D0B7EA9D5E9731\",\n" +
                "                \"certDN\": \"CN=1765225223004700672@唐好凯@01@239,SERIALNUMBER=371724200206052210,OU=localRA,O=MCSCA,C=CN\",\n" +
                "                \"ctmlName\": \"双证书模板\",\n" +
                "                \"notBefore\": \"20241114174521866\",\n" +
                "                \"validity\": \"1\",\n" +
                "                \"modelRoot\": \"1167254698785243136\",\n" +
                "                \"modelKeytype\": \"2\",\n" +
                "                \"modelKeylength\": \"3\",\n" +
                "                \"p10\": \"MIHKMHICAQAwEDEOMAwGA1UEAwwFTUNTQ0EwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAATNj6h/pMLmjDg506NX1X6cCh2yH7cfmJGaeFbEJ+cX2BdcT8kBPW8ghk1u3/1tRboxl4+srmM90Nege3o9R1KeoAAwCgYIKoEcz1UBg3UDSAAwRQIgHoHqZC5TtU5vq9oqw0AJrZfRd2D4K18oPeLXbdDurtECIQDPq+ZVLpTBvdp4ICQZTqZb8MmrAKq9yVzbmS0mCba9DQ==\",\n" +
                "                \"flag\": false,\n" +
                "                \"oldSignCertSN\": \"\",\n" +
                "                \"oldEncCertSN\": \"\",\n" +
                "                \"modelUse\": \"2\",\n" +
                "                \"certFunction\": \"双证\",\n" +
                "                \"certType\": \"SM2256\",\n" +
                "                \"certSN\": \"61FA35D97B6C74B011662227E888CE6C\",\n" +
                "                \"signCert\": \"MIICujCCAl6gAwIBAgIQYfo12XtsdLARZiIn6IjObDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQxMTE0MDk0NTIxWhcNMjQxMTE1MDk0NTIxWjB7MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjM3MTcyNDIwMDIwNjA1MjIxMDEtMCsGA1UEAwwkMTc2NTIyNTIyMzAwNDcwMDY3MkDllJDlpb3lh69AMDFAMjM5MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEzY+of6TC5ow4OdOjV9V+nAodsh+3H5iRmnhWxCfnF9gXXE/JAT1vIIZNbt/9bUW6MZePrK5jPdDXoHt6PUdSnqOCAQ4wggEKMAsGA1UdDwQEAwIGwDCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDAuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMCxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAdBgNVHQ4EFgQUMg1fNaXlGguJgHwUMivMZBXtbnIwHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwDAYIKoEcz1UBg3UFAANIADBFAiAw1moE1IbCbTmYlWknap7hLSFRSJFalGdaKV3IYGqzJAIhAI7eqhfJK7I3OOi9WIYyzH1iBu5KBV5ktWs1vDwCtBOD\",\n" +
                "                \"p7b\": \"MIIEqAYJKoZIhvcNAQcCoIIEmTCCBJUCAQExADALBgkqhkiG9w0BBwGgggR9MIIBuzCCAV+gAwIBAgIQawXI9s8NzHnBm5PlBh9HRDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwIBcNMjAwNDI0MTQwNzU1WhgPMjA1MDA0MTcxNDA3NTVaMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASTFIp6vFYitNsFI35CmdfNDZMzHK9L2azopOpIJXLUYRpH5gI6v8IA2qKC8cAra2p+rb6qS75QUwDBxgVELq7Ko10wWzALBgNVHQ8EBAMCAQYwDAYDVR0TBAUwAwEB/zAdBgNVHQ4EFgQU8SIKZ5iN9eOyqsMXa8BCH75LvXYwHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwDAYIKoEcz1UBg3UFAANIADBFAiBjj2WAf1xek+XYSwjqsMkWuOPtafHL3cYhmkp9QVZZJwIhAMs3DFTfq6K+cs1Dqz4MsZzeODYzibmOYtcxEstB97cZMIICujCCAl6gAwIBAgIQYfo12XtsdLARZiIn6IjObDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQxMTE0MDk0NTIxWhcNMjQxMTE1MDk0NTIxWjB7MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjM3MTcyNDIwMDIwNjA1MjIxMDEtMCsGA1UEAwwkMTc2NTIyNTIyMzAwNDcwMDY3MkDllJDlpb3lh69AMDFAMjM5MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEzY+of6TC5ow4OdOjV9V+nAodsh+3H5iRmnhWxCfnF9gXXE/JAT1vIIZNbt/9bUW6MZePrK5jPdDXoHt6PUdSnqOCAQ4wggEKMAsGA1UdDwQEAwIGwDCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDAuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMCxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAdBgNVHQ4EFgQUMg1fNaXlGguJgHwUMivMZBXtbnIwHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwDAYIKoEcz1UBg3UFAANIADBFAiAw1moE1IbCbTmYlWknap7hLSFRSJFalGdaKV3IYGqzJAIhAI7eqhfJK7I3OOi9WIYyzH1iBu5KBV5ktWs1vDwCtBODMQA=\",\n" +
                "                \"encCertSN\": \"5CEB56E232F1D39FED84771842143D6E\",\n" +
                "                \"encCert\": \"MIICuTCCAl6gAwIBAgIQXOtW4jLx05/thHcYQhQ9bjAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQxMTE0MDk0NTIxWhcNMjQxMTE1MDk0NTIxWjB7MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjM3MTcyNDIwMDIwNjA1MjIxMDEtMCsGA1UEAwwkMTc2NTIyNTIyMzAwNDcwMDY3MkDllJDlpb3lh69AMDFAMjM5MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEYV3op6dv/v/nS49snTMDMbZZkz0sWQeUbzO9feQPHa9YafrXFGy5ujgwZSzErEENVdTIRkuEyj3HIKPSS1uhMqOCAQ4wggEKMAsGA1UdDwQEAwIEMDCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDAuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMCxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAdBgNVHQ4EFgQUpzry8f5//ZLRXU2MIjtqVWqxTzowHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwDAYIKoEcz1UBg3UFAANHADBEAiAAFT0A/es94LJMOUui/syTrWw+xbnp2Cn/c6dIHQtQWwIgYLGl3yx/rDA5h/RM16lJ4tRBwZ+AwIy+HBMhBJRGtbQ=\",\n" +
                "                \"doubleP7b\": \"MIIEpwYJKoZIhvcNAQcCoIIEmDCCBJQCAQExADALBgkqhkiG9w0BBwGgggR8MIIBuzCCAV+gAwIBAgIQawXI9s8NzHnBm5PlBh9HRDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwIBcNMjAwNDI0MTQwNzU1WhgPMjA1MDA0MTcxNDA3NTVaMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASTFIp6vFYitNsFI35CmdfNDZMzHK9L2azopOpIJXLUYRpH5gI6v8IA2qKC8cAra2p+rb6qS75QUwDBxgVELq7Ko10wWzALBgNVHQ8EBAMCAQYwDAYDVR0TBAUwAwEB/zAdBgNVHQ4EFgQU8SIKZ5iN9eOyqsMXa8BCH75LvXYwHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwDAYIKoEcz1UBg3UFAANIADBFAiBjj2WAf1xek+XYSwjqsMkWuOPtafHL3cYhmkp9QVZZJwIhAMs3DFTfq6K+cs1Dqz4MsZzeODYzibmOYtcxEstB97cZMIICuTCCAl6gAwIBAgIQXOtW4jLx05/thHcYQhQ9bjAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQxMTE0MDk0NTIxWhcNMjQxMTE1MDk0NTIxWjB7MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjM3MTcyNDIwMDIwNjA1MjIxMDEtMCsGA1UEAwwkMTc2NTIyNTIyMzAwNDcwMDY3MkDllJDlpb3lh69AMDFAMjM5MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEYV3op6dv/v/nS49snTMDMbZZkz0sWQeUbzO9feQPHa9YafrXFGy5ujgwZSzErEENVdTIRkuEyj3HIKPSS1uhMqOCAQ4wggEKMAsGA1UdDwQEAwIEMDCBugYDVR0fBIGyMIGvMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDAuY3JsMH2ge6B5hndsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjozODkvQ049Y3JsMCxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDAdBgNVHQ4EFgQUpzry8f5//ZLRXU2MIjtqVWqxTzowHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwDAYIKoEcz1UBg3UFAANHADBEAiAAFT0A/es94LJMOUui/syTrWw+xbnp2Cn/c6dIHQtQWwIgYLGl3yx/rDA5h/RM16lJ4tRBwZ+AwIy+HBMhBJRGtbQxAA==\",\n" +
                "                \"encSessionKey\": \"AA==\",\n" +
                "                \"encPrivateKey\": \"MIIEQwYKKoEcz1UGAQQCBKCCBDMwggQvAgEBMYHUMIHRAgEAMEEwLTELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQQIQYfo12XtsdLARZiIn6IjObDALBgkqgRzPVQGCLQMEfDB6AiEAh2c9g5TDv5dEofVf55hYCMNzq/rtym2uDM7ikFcYfPwCIQD4+6SqFpNJ3ncG14a+ntEI3FTyHfgcbc6vWILAAkgVUQQgn/4gtGcwqeGZMiEJUGoO94lhiiMAK8aqO7G3bZYHc5QEEEBBtPq4YimuUqAgeOe8zosxDDAKBggqgRzPVQGDETBZBgoqgRzPVQYBBAIBMAkGByqBHM9VAWiAQMbkSFSNOFKHNNlihBWHA3jG5EhUjThShzTZYoQVhwN4lWS1OvWTij8j/O3EJNMT7iXiJAgpHXS7in2vV9UjJYegggHvMIIB6zCCAY+gAwIBAgIQZhpTteVSMDmossSeqZ2nlDAMBggqgRzPVQGDdQUAMHgxCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAlDaG9uZ3FpbmcxOjA4BgNVBAoMMUVhc3QtWmhvbmd4dW4gQ2VydGlmaWNhdGUgQXV0aG9yaXR5IENlbnRlciBDTy5MVEQxGTAXBgNVBAMMEEVhc3QtWmhvbmd4dW4gQ0EwHhcNMjMwODE3MDEyODMzWhcNMzMwODE0MDEyODMzWjAvMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAMMB1NNMlNJR04wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQgOmy4mEBDeZmqg7a+q35qoOf7/hoWR5SA3aEP0lkPh3N0nnUV3DGrOaFnwVAGfjZKNp3NftSzFrESYuA4A69+o0IwQDAfBgNVHSMEGDAWgBQJ7rsH/h/eV5iSbu4/y1JqS/kmHTAdBgNVHQ4EFgQUiajqAu5ieONRz99oYPAphmy6XswwDAYIKoEcz1UBg3UFAANIADBFAiAZ52RxsV3HOwK+ls3dzSfvA2uU8CvCZ/Ce0vKeMbD5sAIhAJlu5Xq3TNQOvu1uARHy4evSf3wvnfh888dof6IViykJMYH2MIHzAgEBMIGMMHgxCzAJBgNVBAYTAkNOMRIwEAYDVQQIDAlDaG9uZ3FpbmcxOjA4BgNVBAoMMUVhc3QtWmhvbmd4dW4gQ2VydGlmaWNhdGUgQXV0aG9yaXR5IENlbnRlciBDTy5MVEQxGTAXBgNVBAMMEEVhc3QtWmhvbmd4dW4gQ0ECEGYaU7XlUjA5qLLEnqmdp5QwCgYIKoEcz1UBgxEwCwYJKoEcz1UBgi0BBEYwRAIgWiZuxWdR9vtFqX3aRp4PeQ1WwKQVz2z8gCbPP8JxilwCIH6/Pxvqfgx61w5orPo+JlM8AQelqctE23SDDHtBy7Qm\",\n" +
                "                \"notAfter\": \"20241115174521866\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"sign\": \"MEUCIQC9vmy8KsNas3XohLQCvVfR1xwU09CLCW3LmDcOTEVmMwIgJAh6rbYBSF2qmsPT+rYrWCIDDxrJESNJNuGY40XzpAE=\"\n" +
                "}");
        JSONObject resBody = cmsJson.getJSONObject("resBody");
        JSONArray certs = resBody.getJSONArray("certs");
        JSONObject cert = certs.getJSONObject(0);
        String certDN = cert.getStr("certDN");
        String notBefore = cert.getStr("notBefore");
        String notAfter = cert.getStr("notAfter");
        String certSN = cert.getStr("certSN");
        String signCert = cert.getStr("signCert");
        String encCertSN = cert.getStr("encCertSN");
        String encCert = cert.getStr("encCert");
        String encPrivateKey = cert.getStr("encPrivateKey");

        JSONObject jsonObject = new JSONObject();
        jsonObject.set("responseCode", "2000");
        jsonObject.set("responseMessage", "OK");
        jsonObject.set("transactionTime", "20241108171924");
        jsonObject.set("certDn", certDN);
        jsonObject.set("signatureCertSn", certSN);
        jsonObject.set("encryptionCertSn", encCertSN);
        jsonObject.set("certValidityNotBefore", notBefore.substring(0, 14));
        jsonObject.set("certValidityNotAfter", notAfter.substring(0, 14));
        jsonObject.set("signatureHashAlgorithm", "SM3");
        jsonObject.set("certStatus", "1");
        jsonObject.set("signatureCert", signCert);
        jsonObject.set("encryptionCert", encCert);
        jsonObject.set("encryptionCertPrivateKeyEnvelope", encPrivateKey);
        return jsonObject;
    }


}
