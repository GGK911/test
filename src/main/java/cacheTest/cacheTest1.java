package cacheTest;

import cn.com.mcsca.pdf.signature.PdfSign;
import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.io.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存测试
 * 电子签章项目有本地缓存未找到错误，现测试缓存数量
 *
 * @author TangHaoKai
 * @version V1.0 2023-12-01 11:00
 **/
public class cacheTest1 {
    public static void main(String[] args) {
        Cache<String, Object> cache = new TimedCache<>(1000);
        byte[] contractBytes = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\劳动合同( 含变更+隐私声明 )-无固定期限5.pdf");
        byte[] sealBytes = FileUtil.readBytes("C:\\Users\\ggk911\\OneDrive\\图片\\dlyd\\页眉log.png");

        int i = 0;
        while (true) {
            PdfSign pdfSign = new PdfSign();
            String pubKey = "MIIDvjCCAqagAwIBAgIQXwKc5nCmO03azRa/rz1GuDANBgkqhkiG9w0BAQsFADA9MQswCQYDVQQGEwJDTjEOMAwGA1UECgwFTUNTQ0ExDjAMBgNVBAsMBU1DU0NBMQ4wDAYDVQQDDAVNQ1NDQTAeFw0yMzA5MjYwMzI3MzVaFw0yNDA5MjUwMzI3MzVaMHsxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEQMA4GA1UECwwHbG9jYWxSQTEbMBkGA1UEBQwSMzcxNzI0MjAwMjA2MDUyMjEwMS0wKwYDVQQDDCQxMjE1NDQ1NDc1MDA4NzA4NjA4QOWUkOWlveWHr0AwMUAwMDgwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCXdWW8SuVjoaIp8fyLuPDZgYhq55PAZEvVKUONy+HM7f2icKFGU1ZG3MWidey/FgaTE35iMCLzwJRAXdI1q3as33aHU3mp0QFf+Yvefn5EG7GyifogTbuzl+fZZECsAsCUBTMv/r8yLS+cv3SOWus46F+V8OZF3f5uK8MsznxAagIxGlQ9mhvFnDj4TnIaJiFod4Z/9we3QWLFP0qropQJ/HIrvkFLpCbro0jQKMDU/LZOuzm0EcNpX5Mkdawjw169lBKv7GvI9sftPs6T8NY7vHvei3vAWlJpi1FrNp4W8gkRZcM303rZEOT56vP3M2Abu1iOBejwh5QE4yNacIHTAgMBAAGjfDB6MB8GA1UdIwQYMBaAFBBOhKC+qNNxt2y1/xPzC9MB2we7MB0GA1UdDgQWBBRNJ1HCNYC4rwvURoiE72L6ekI0tTAMBgNVHRMEBTADAQEAMAsGA1UdDwQEAwIE8DAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADggEBADtWtOtKNcxO7CGu7RoYxGYK6Y19uRtuEotYYjpmjkXZCqdpJya2yoBjf+WPUAiaZgz3jkeQM6qDTag6spj0ETub2fqfb/tc7Aj4TFaZFmmPZs/iFNQTPnlpywZ14M/rs+n57z2VMhOUT/qfP6XZdY7rhkDWNw52Ay8QgJG0ef3XRb9gWNoDG1SwsjfEi3kn8ygmvDLFs1cs0uFouUue0ndbIlijAMtKBII0typw8oEkl42AIIQJ5ZBoKq1JP0IIUyTsEJRwChpWv3NDiPNHRu5pygymz8Bh8oS/nNlhUXPeFzSVZNjRKTCzFdIWU2+NmYCN4LKfxvzpnioOGxMGzHc=";
            List<float[]> positionList = new ArrayList<>();
            positionList.add(new float[]{1.0F, 100.0F, 100.0F, 200.0F, 200.0F});
            pdfSign.setPublicCert(pubKey);
            try {
                pdfSign.generateSignText(contractBytes, sealBytes, positionList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                // 存之前先清数据
                cache.prune();
                cache.put(i + "", pdfSign);
            } catch (Exception e) {
                break;
            } finally {
                System.out.println(i);
                if (cache.containsKey((i == 1000 ? 1 : i) + "")) {
                    PdfSign pdfSign1 = (PdfSign) cache.get((i == 1000 ? 1 : i) + "");
                    if (pdfSign1 == null) {
                        System.out.println("取得VALUE NULL");
                        break;
                    }
                    i++;
                } else {
                    System.out.println("未找到KEY");
                    break;
                }
            }
        }
        // -Xmx200m 591数量
        // -Xms100m 288数量

    }
}
