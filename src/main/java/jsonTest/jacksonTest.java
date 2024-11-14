package jsonTest;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import extense.Father;
import extense.FatherSerializer;
import extense.Son;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * json测试
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-23 12:00
 **/
public class jacksonTest {
    public static void main(String[] args) {
        Father father = new Father("A", "18", DateUtil.yesterday(), DateUtil.date());

        // 普通序列化
        ObjectMapper mapper0 = new ObjectMapper();
        try {
            System.out.println(mapper0.writeValueAsString(father));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 自定义序列化器
        ObjectMapper mapper1 = new ObjectMapper();
        FatherSerializer fatherSerializer = new FatherSerializer(Father.class);
        SimpleModule simpleModule = new SimpleModule("fatherSerializer", new Version(2, 1, 3, null, null, null));
        simpleModule.addSerializer(Father.class, fatherSerializer);
        mapper1.registerModule(simpleModule);
        try {
            System.out.println(mapper1.writeValueAsString(father));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 日期格式
        ObjectMapper mapper2 = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        mapper2.setDateFormat(dateFormat);
        try {
            System.out.println(mapper2.writeValueAsString(father));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Son son = new Son("son1", "5");
        father.addSon(son);
        try {
            System.out.println(mapper0.writeValueAsString(father));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    @SneakyThrows
    public void readTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree("{\n" +
                "    \"param1\": \"test\",\n" +
                "    \"param2\": {\n" +
                "        \"a\": \"1\",\n" +
                "        \"b\": \"2\"\n" +
                "    }\n" +
                "}");

        // 直接获取指定 key 的值
        JsonNode param1 = rootNode.get("param1");
        if (param1 != null) {
            String name = param1.asText();
            System.out.println(String.format("%-16s", "param1>> ") + name);
        }

        JsonNode param2 = rootNode.get("param2");
        if (param2 != null && param2.isContainerNode()) {
            JsonNode a = param2.get("a");
            if (a != null && a.isTextual()) {
                String aText = a.asText();
                System.out.println(String.format("%-16s", "aText>> ") + aText);
            }
        }
    }

    @Test
    @SneakyThrows
    public void jacksonTest() {
        String res = "{\n" +
                "    \"resHead\": {\n" +
                "        \"appId\": \"mcsca-73hpze03nr\",\n" +
                "        \"businessNO\": \"B700007\",\n" +
                "        \"methodCode\": \"B700007\",\n" +
                "        \"reqTime\": \"20190507110840768\",\n" +
                "        \"respTime\": \"20241029183503641\",\n" +
                "        \"serialNo\": \"12345678910111213141516171819\",\n" +
                "        \"token\": \"JGRSI5E9ZTH94OPFYGEVMS71QNPRFV9U\",\n" +
                "        \"resultCode\": \"2000\",\n" +
                "        \"resultMsg\": \"OK\"\n" +
                "    },\n" +
                "    \"resBody\": {\n" +
                "        \"orderId\": \"T185121113572352819274062970\",\n" +
                "        \"certDn\": \"CN=1829379121038913536@唐好凯@01@101,SERIALNUMBER=371724200206052210,OU=localRA,O=MCSCA,C=CN\",\n" +
                "        \"certs\": [\n" +
                "            {\n" +
                "                \"ipAddress\": \"222.182.52.231\",\n" +
                "                \"raCaId\": \"1167254698785243136\",\n" +
                "                \"raCertid\": \"FB25FC51D6DF4BE2BE520AEED034710E\",\n" +
                "                \"certDN\": \"CN=1829379121038913536@唐好凯@01@101,SERIALNUMBER=371724200206052210,OU=localRA,O=MCSCA,C=CN\",\n" +
                "                \"ctmlName\": \"新SM2个人双证书模板\",\n" +
                "                \"notBefore\": \"20241029183503347\",\n" +
                "                \"validity\": \"122\",\n" +
                "                \"extensionField\": \"[{\\\"extOID\\\":\\\"1.2.86.21.1.1\\\",\\\"isCrux\\\":0,\\\"extName\\\":\\\"实体唯一编码\\\",\\\"extValue\\\":\\\"123\\\"}]\",\n" +
                "                \"modelRoot\": \"1167254698785243136\",\n" +
                "                \"modelKeytype\": \"2\",\n" +
                "                \"modelKeylength\": \"3\",\n" +
                "                \"p10\": \"MIIBQzCB6QIBADCBhjEPMA0GA1UECAwG6YeN5bqGMQ8wDQYDVQQHDAbph43luoYxIjAgBgkqhkiG9w0BCQEWEzEzOTgzMDUzNDU1QDE2My5jb20xCzAJBgNVBAYTAkNOMQ8wDQYDVQQKEwZHR0s5MTExDzANBgNVBAsTBkdHSzkxMTEPMA0GA1UEAxMGR0dLOTExMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE8jwlOAwUyNTF1f8whx3itGN32g4/rGSw44fZ6P22Nazl2oN4vH0aWtbjP4FUXmI5VfMfZfz2OeSgdZjgtZK3zKAAMAoGCCqBHM9VAYN1A0kAMEYCIQCPgdUuld74W1zCp6gUzzGQcr/beogHFpdbZIcNGqzsLAIhALNl5hdLRaIJGUu07xLJQKHNmthxVl/E5ZA+HYnU4tCh\",\n" +
                "                \"flag\": false,\n" +
                "                \"oldSignCertSN\": \"\",\n" +
                "                \"oldEncCertSN\": \"\",\n" +
                "                \"modelUse\": \"2\",\n" +
                "                \"certFunction\": \"双证\",\n" +
                "                \"certType\": \"SM2256\",\n" +
                "                \"certSN\": \"76ECB30911E46A57297CD42B0A62435B\",\n" +
                "                \"signCert\": \"MIIDEjCCAragAwIBAgIQduyzCRHkalcpfNQrCmJDWzAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQxMDI5MTAzNTAzWhcNMjUwMjI4MTAzNTAzWjB7MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjM3MTcyNDIwMDIwNjA1MjIxMDEtMCsGA1UEAwwkMTgyOTM3OTEyMTAzODkxMzUzNkDllJDlpb3lh69AMDFAMTAxMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE8jwlOAwUyNTF1f8whx3itGN32g4/rGSw44fZ6P22Nazl2oN4vH0aWtbjP4FUXmI5VfMfZfz2OeSgdZjgtZK3zKOCAWYwggFiMAsGA1UdDwQEAwIE8DAfBgNVHSMEGDAWgBS5I+Ycf8+3PFgkW6QKrYoRl3p7tjAdBgNVHQ4EFgQUPnYS4Bqv/35orL6cyxiKgsd+uLYwgbwGA1UdHwSBtDCBsTAuoCygKoYoaHR0cDovL3d3dy5tY3NjYS5jb20uY24vc20yL2NybC9jcmwxLmNybDB/oH2ge4Z5bGRhcDovL3d3dy5tY3NjYS5jb20uY246MTAzODkvQ049Y3JsMSxPVT1DUkwsTz1NQ1NDQSxDPUNOP2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RjbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludDBEBgNVHSAEPTA7MDkGByqBHIeECwEwLjAsBggrBgEFBQcCARYgaHR0cHM6Ly93d3cubWNzY2EuY29tLmNuL2Nwcy5odG0wDgYFKlYVAQEEBQwDMTIzMAwGCCqBHM9VAYN1BQADSAAwRQIhALj4RJXgdcC5R/yu5erchXknM4K13Ur7qK+Fot1V7oTgAiBw4mH2LSxUw9qy6Mh2iaFwVpm3xzMOJdpjCLiplQqPPg==\",\n" +
                "                \"p7b\": \"MIIE/gYJKoZIhvcNAQcCoIIE7zCCBOsCAQExADALBgkqhkiG9w0BBwGgggTTMIIBuTCCAV2gAwIBAgIQRunb6IncHwSuJ6mMXhnW5DAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMTkwMjIxMDYyMzI4WhcNNDkwMjEzMDYyMzI4WjAtMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAMMBU1DU0NBMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEYvXuAG/vEZj32lgDwgUfcfdXFu6XJ0kbPkW7edifHYWJTgEbyos0KJvmGcrMbqdWB416XUhUquxNfzoINDqWPqNdMFswCwYDVR0PBAQDAgEGMAwGA1UdEwQFMAMBAf8wHQYDVR0OBBYEFLkj5hx/z7c8WCRbpAqtihGXenu2MB8GA1UdIwQYMBaAFLkj5hx/z7c8WCRbpAqtihGXenu2MAwGCCqBHM9VAYN1BQADSAAwRQIhAOFT6E+JA6CeBLrinwwMZ527gAnqJRQwvYo1B5Uzb3kuAiAsJscUBhQP7Cy6J2+o64FIa2R2jAWisl6rcakR69TsFzCCAxIwggK2oAMCAQICEHbsswkR5GpXKXzUKwpiQ1swDAYIKoEcz1UBg3UFADAtMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAMMBU1DU0NBMB4XDTI0MTAyOTEwMzUwM1oXDTI1MDIyODEwMzUwM1owezELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE4MjkzNzkxMjEwMzg5MTM1MzZA5ZSQ5aW95YevQDAxQDEwMTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABPI8JTgMFMjUxdX/MIcd4rRjd9oOP6xksOOH2ej9tjWs5dqDeLx9GlrW4z+BVF5iOVXzH2X89jnkoHWY4LWSt8yjggFmMIIBYjALBgNVHQ8EBAMCBPAwHwYDVR0jBBgwFoAUuSPmHH/PtzxYJFukCq2KEZd6e7YwHQYDVR0OBBYEFD52EuAar/9+aKy+nMsYioLHfri2MIG8BgNVHR8EgbQwgbEwLqAsoCqGKGh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMS5jcmwwf6B9oHuGeWxkYXA6Ly93d3cubWNzY2EuY29tLmNuOjEwMzg5L0NOPWNybDEsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwRAYDVR0gBD0wOzA5BgcqgRyHhAsBMC4wLAYIKwYBBQUHAgEWIGh0dHBzOi8vd3d3Lm1jc2NhLmNvbS5jbi9jcHMuaHRtMA4GBSpWFQEBBAUMAzEyMzAMBggqgRzPVQGDdQUAA0gAMEUCIQC4+ESV4HXAuUf8ruXq3IV5JzOCtd1K+6ivhaLdVe6E4AIgcOJh9i0sVMPasujIdomhcFaZt8czDiXaYwi4qZUKjz4xAA==\",\n" +
                "                \"encCertSN\": \"64AD7021DBBE0117AFEA92241FBCCB25\",\n" +
                "                \"encCert\": \"MIIDEjCCAragAwIBAgIQZK1wIdu+ARev6pIkH7zLJTAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQxMDI5MTAzNTAzWhcNMjUwMjI4MTAzNTAzWjB7MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExEDAOBgNVBAsMB2xvY2FsUkExGzAZBgNVBAUMEjM3MTcyNDIwMDIwNjA1MjIxMDEtMCsGA1UEAwwkMTgyOTM3OTEyMTAzODkxMzUzNkDllJDlpb3lh69AMDFAMTAxMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE8bUJF6spWry5QLQoow9mTXSE2NQNn6iUwVkz+g7o5JHMYuyuoPJ/y71yreTiHTJuipTlDShhxg7FqGwlVjuQ+KOCAWYwggFiMAsGA1UdDwQEAwIDODAdBgNVHQ4EFgQU8VVxsScVor4bRBgXeiEwLQ1QEhQwHwYDVR0jBBgwFoAUuSPmHH/PtzxYJFukCq2KEZd6e7YwRAYDVR0gBD0wOzA5BgcqgRyHhAsBMC4wLAYIKwYBBQUHAgEWIGh0dHBzOi8vd3d3Lm1jc2NhLmNvbS5jbi9jcHMuaHRtMIG8BgNVHR8EgbQwgbEwLqAsoCqGKGh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMS5jcmwwf6B9oHuGeWxkYXA6Ly93d3cubWNzY2EuY29tLmNuOjEwMzg5L0NOPWNybDEsT1U9Q1JMLE89TUNTQ0EsQz1DTj9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Y2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnQwDgYFKlYVAQEEBQwDMTIzMAwGCCqBHM9VAYN1BQADSAAwRQIgH9GRwhcjtvMKkDweXk2+Jx5PqbQzY8QdSFpWhvQz8igCIQDxer2zvLsN5WStSueas+QGyLhSPlpLH5KUfm8CFEieWQ==\",\n" +
                "                \"doubleP7b\": \"MIIE/gYJKoZIhvcNAQcCoIIE7zCCBOsCAQExADALBgkqhkiG9w0BBwGgggTTMIIBuTCCAV2gAwIBAgIQRunb6IncHwSuJ6mMXhnW5DAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMTkwMjIxMDYyMzI4WhcNNDkwMjEzMDYyMzI4WjAtMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAMMBU1DU0NBMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEYvXuAG/vEZj32lgDwgUfcfdXFu6XJ0kbPkW7edifHYWJTgEbyos0KJvmGcrMbqdWB416XUhUquxNfzoINDqWPqNdMFswCwYDVR0PBAQDAgEGMAwGA1UdEwQFMAMBAf8wHQYDVR0OBBYEFLkj5hx/z7c8WCRbpAqtihGXenu2MB8GA1UdIwQYMBaAFLkj5hx/z7c8WCRbpAqtihGXenu2MAwGCCqBHM9VAYN1BQADSAAwRQIhAOFT6E+JA6CeBLrinwwMZ527gAnqJRQwvYo1B5Uzb3kuAiAsJscUBhQP7Cy6J2+o64FIa2R2jAWisl6rcakR69TsFzCCAxIwggK2oAMCAQICEGStcCHbvgEXr+qSJB+8yyUwDAYIKoEcz1UBg3UFADAtMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAMMBU1DU0NBMB4XDTI0MTAyOTEwMzUwM1oXDTI1MDIyODEwMzUwM1owezELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBU1DU0NBMRAwDgYDVQQLDAdsb2NhbFJBMRswGQYDVQQFDBIzNzE3MjQyMDAyMDYwNTIyMTAxLTArBgNVBAMMJDE4MjkzNzkxMjEwMzg5MTM1MzZA5ZSQ5aW95YevQDAxQDEwMTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABPG1CRerKVq8uUC0KKMPZk10hNjUDZ+olMFZM/oO6OSRzGLsrqDyf8u9cq3k4h0yboqU5Q0oYcYOxahsJVY7kPijggFmMIIBYjALBgNVHQ8EBAMCAzgwHQYDVR0OBBYEFPFVcbEnFaK+G0QYF3ohMC0NUBIUMB8GA1UdIwQYMBaAFLkj5hx/z7c8WCRbpAqtihGXenu2MEQGA1UdIAQ9MDswOQYHKoEch4QLATAuMCwGCCsGAQUFBwIBFiBodHRwczovL3d3dy5tY3NjYS5jb20uY24vY3BzLmh0bTCBvAYDVR0fBIG0MIGxMC6gLKAqhihodHRwOi8vd3d3Lm1jc2NhLmNvbS5jbi9zbTIvY3JsL2NybDEuY3JsMH+gfaB7hnlsZGFwOi8vd3d3Lm1jc2NhLmNvbS5jbjoxMDM4OS9DTj1jcmwxLE9VPUNSTCxPPU1DU0NBLEM9Q04/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXNlP29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MA4GBSpWFQEBBAUMAzEyMzAMBggqgRzPVQGDdQUAA0gAMEUCIB/RkcIXI7bzCpA8Hl5NviceT6m0M2PEHUhaVob0M/IoAiEA8Xq9s7y7DeVkrUrnmrPkBsi4Uj5aSx+SlH5vAhRInlkxAA==\",\n" +
                "                \"encSessionKey\": \"AA==\",\n" +
                "                \"encPrivateKey\": \"AQAAAAEEAAAAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABDi4Ub4wDVECg/Z/oDWXfGeig6/yMGBdiiuZKG5QLvHgABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPG1CRerKVq8uUC0KKMPZk10hNjUDZ+olMFZM/oO6OSRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADMYuyuoPJ/y71yreTiHTJuipTlDShhxg7FqGwlVjuQ+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHLEt/PJE0XLgZFE9lXU5QA2JTD8kdDfqpdvTX/H4S60AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMTZvOSbCZctarpTR+9cAqcJBmTyeX6WzV0ZrLIAaUCfBSIpZOeMRp2gdhIfuCDc6qQpJquDdiOOmabLJGwqzy4QAAAAgd3t7ZUTnjWXRQCirr5uoQ==\",\n" +
                "                \"notAfter\": \"20250228183503347\",\n" +
                "                \"modelHashAlg\": \"4\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"sign\": \"MEYCIQC/Y8JogBK0qB8vJE10WmIFvGOKaOP2f/sM/2Mt1uRlAwIhANdqch62rbxPZKlxpsoUoScky3yl68KU/qcR/xH1D4FA\"\n" +
                "}";
        String nestedValue = JSONUtil_jackson.getNestedValue(res, "resBody", "certs");
        System.out.println(String.format("%-16s", "nestedValue>> ") + nestedValue);
        String certDn = JSONUtil_jackson.getNestedValue(res, "resBody", "certDn");
        System.out.println(String.format("%-16s", "certDn>> ") + certDn);

        List<VO> vos = JSONUtil_jackson.fromJsonStringToList(nestedValue, VO.class);
        for (VO vo : vos) {
            System.out.println(JSONUtil_jackson.toJsonString(vo));
        }
        System.out.println(JSONUtil_jackson.toJsonString(vos));
    }
}
