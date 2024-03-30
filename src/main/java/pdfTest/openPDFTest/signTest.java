// package pdfTest.openPDFTest;
//
// import certTest.saxon.rsa.RSAUtils;
// import cn.hutool.core.io.FileUtil;
// import com.lowagie.text.pdf.PdfReader;
// import com.lowagie.text.pdf.PdfSignatureAppearance;
// import com.lowagie.text.pdf.PdfStamper;
// import lombok.SneakyThrows;
// import org.junit.jupiter.api.Test;
//
// import java.io.ByteArrayOutputStream;
// import java.math.BigInteger;
// import java.security.KeyPair;
// import java.security.PrivateKey;
// import java.security.PublicKey;
// import java.security.cert.X509Certificate;
// import java.util.HashMap;
// import java.util.Random;
//
// /**
//  * openPDF签署测试
//  *
//  * @author TangHaoKai
//  * @version V1.0 2024/3/20 13:17
//  */
// public class signTest {
//
//     @Test
//     @SneakyThrows
//     public void sign() {
//
//         // 颁发者固定写死
//         String issuerStr = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,E=13983053455@163.com,L=重庆,ST=重庆";
//         // 使用者，需自定义传参带入
//         String subjectStr = "CN=GGK911,OU=GGK911,O=GGK911,C=CN,E=13983053455@163.com,L=重庆,ST=重庆";
//         // 颁发地址，后续用到什么项目，公司地址等
//         String certificateCRL = "https://www.mcsca.com.cn/";
//         // 序列号
//         BigInteger serial = BigInteger.probablePrime(256, new Random());
//         byte[] bytes = FileUtil.readBytes("C:\\Users\\ggk911\\Documents\\WeChat Files\\wxid_46xaqz9ckmyg12\\FileStorage\\File\\2024-03\\signed1522767350206660522.pdf");
//         KeyPair keyPairRsa = RSAUtils.getKey(2048);
//         PrivateKey aPrivate = keyPairRsa.getPrivate();
//         PublicKey aPublic = keyPairRsa.getPublic();
//         X509Certificate generateCertificateV3 = (X509Certificate) RSAUtils.generateCertificateV3(issuerStr, subjectStr, aPublic, aPrivate, new HashMap<>(1), certificateCRL, null, 3650);
//         final byte[] bytes1 = signNotVisible(bytes, aPrivate, generateCertificateV3);
//         FileUtil.writeBytes(bytes1, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\openPDFTest\\test.pdf");
//
//     }
//
//     /**
//      * JDK11Test
//      * @param bytes
//      * @param key
//      * @param cert
//      * @return
//      * @throws Exception
//      */
//     public static byte[] signNotVisible(byte[] bytes, PrivateKey key, X509Certificate cert) throws Exception {
//         PdfReader reader = new PdfReader(bytes);
//         ByteArrayOutputStream bos = new ByteArrayOutputStream();
//         PdfStamper stp = PdfStamper.createSignature(reader, bos, '\0', null, true);
//         PdfSignatureAppearance sap = stp.getSignatureAppearance();
//         sap.setCrypto(key, cert, null, PdfSignatureAppearance.WINCER_SIGNED);
//         sap.setReason("I'm the author");
//         sap.setLocation("Lisbon");
//         stp.close();
//         return bos.toByteArray();
//     }
// }
