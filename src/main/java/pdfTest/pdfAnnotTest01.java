package pdfTest;


import cn.com.mcsca.bouncycastle.util.encoders.Base64;
import cn.com.mcsca.bouncycastle.util.encoders.Hex;
import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.itextpdf.text.pdf.security.DigestAlgorithms;
import cn.com.mcsca.itextpdf.text.pdf.security.PdfPKCS7;
import cn.com.mcsca.itextpdf.text.pdf.security.SecurityIDs;
import cn.com.mcsca.itextpdf.text.pdf.security.TSAClientBouncyCastle;
import cn.com.mcsca.util.CertUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.json.JSONUtil;
import com.itextpdf.text.Annotation;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfAppearance;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfIndirectReference;
import com.itextpdf.text.pdf.PdfLiteral;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * pdf
 * https://kb.itextpdf.com/home/it5kb/ebooks/the-best-itext-5-questions-on-stack-overflow
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-19 10:06
 **/
public class pdfAnnotTest01 {

    private final Provider MCS_BC = new cn.com.mcsca.bouncycastle.jce.provider.BouncyCastleProvider();
    private final Provider BC = new BouncyCastleProvider();

    @Test
    public void pdfAnnotTest() throws Exception {
        Security.addProvider(BC);
        Security.addProvider(MCS_BC);
        Document document = new Document(PageSize.A4);
        FileOutputStream out = new FileOutputStream("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\create2.pdf");
        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setPdfVersion(PdfWriter.PDF_VERSION_1_4);
        document.open();
        document.add(new Paragraph("00000000000000000000000000000000000000"));

        // PdfFormField signature = PdfFormField.createSignature(writer);
        // signature.setWidget(new Rectangle(200, 200, 300, 300), PdfName.HIGHLIGHT);
        // signature.setName("signname");
        // signature.setFieldFlags(PdfAnnotation.FLAGS_HIDDEN);
        // signature.setPage();
        // signature.setMKBorderColor(BaseColor.BLACK);
        // signature.setMKBackgroundColor(BaseColor.WHITE);
        // PdfAppearance appearance1 = PdfAppearance.createAppearance(writer, 100, 100);
        // appearance1.rectangle(0.5f, 0.5f, 99f, 99f);
        // appearance1.stroke();
        // signature.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, appearance1);
        // writer.addAnnotation(signature);

        document.close();

        byte[] pdf = FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\create2.pdf");
        PdfReader pdfReader = new PdfReader(pdf);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfStamper pdfStamper = new PdfStamper(pdfReader, bos);

        // 添加签名域并锁定
        PdfFormField sigField = PdfFormField.createSignature(pdfStamper.getWriter());
        sigField.setFieldName("McsCa");
        pdfStamper.addAnnotation(sigField, 1);
        pdfStamper.getWriter().setSigFlags(3);

        Image image = Image.getInstance(FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\resources\\file\\image\\页眉log.png"));
        int width = (int) image.getWidth();
        int heifht = (int) image.getHeight();
        int llx = 100;
        int lly = 100;
        int urx = llx + width;
        int ury = lly + heifht;
        PdfAppearance appearance = PdfAppearance.createAppearance(pdfStamper.getWriter(), width, heifht);

        // PdfAnnotation pdfAnnotation = new PdfAnnotation(pdfStamper.getWriter(), new Rectangle(llx, lly, urx, ury));
        // pdfAnnotation.put(PdfName.TYPE, new PdfLiteral("Annot"));
        // pdfAnnotation.put(PdfName.SUBTYPE, new PdfLiteral("McsCa"));
        // pdfAnnotation.put(PdfName.SIG, new PdfLiteral("sigTest"));

        PdfAnnotation annotation = pdfStamper.getWriter().createAnnotation(new Rectangle(llx, lly, urx, ury), new PdfName("McsCa:Annot"));
        byte[] replace = new byte[10240];
        Arrays.fill(replace, (byte) 48);
        PdfLiteral lit = new PdfLiteral(10240);
        annotation.put(new PdfName("SigValue"), lit);
        annotation.put(PdfName.TYPE, PdfName.ANNOT);
        annotation.put(PdfName.T, new PdfString("McsCa"));
        annotation.put(new PdfName("SigTime"), new PdfDate());

        ColumnText text = new ColumnText(appearance);
        text.addElement(image);
        text.setSimpleColumn(new Rectangle(0, 0, width, heifht));
        text.go();

        annotation.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, appearance);
        pdfStamper.addAnnotation(annotation, 1);
        System.out.println(lit.getPosition());
        System.out.println(lit.getPosLength());

        pdfStamper.close();
        byte[] returnBytes = bos.toByteArray();

        FileUtil.writeBytes(returnBytes, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\createAnnotOri.pdf");

        // thk's todo 2024/1/15 15:55 还是有可能大小超过8位
        String sigRangeFormat = "/SigRange [%-8s %-8s %-8s %-8s]";
        long[] range = new long[4];
        range[1] = lit.getPosition();
        range[2] = lit.getPosLength() + lit.getPosition();
        range[3] = returnBytes.length - (lit.getPosLength() + lit.getPosition());
        String sigRange = String.format(sigRangeFormat, range[0], range[1], range[2], range[3]);
        byte[] rangeBytes = sigRange.getBytes(StandardCharsets.UTF_8);
        range[2] = range[2] - rangeBytes.length;
        range[3] = range[3] + rangeBytes.length;
        sigRange = String.format(sigRangeFormat, range[0], range[1], range[2], range[3]);
        rangeBytes = sigRange.getBytes(StandardCharsets.UTF_8);
        // range
        System.arraycopy(rangeBytes, 0, replace, replace.length - sigRange.length(), sigRange.length());
        // range替换
        System.arraycopy(replace, 0, returnBytes, (int) lit.getPosition(), replace.length);

        // 替换前
        FileUtil.writeBytes(returnBytes, "C:\\Users\\ggk911\\Desktop\\replace.pdf");
        // 取前面
        byte[] bytes = new byte[(int) lit.getPosition()];
        System.arraycopy(returnBytes, 0, bytes, 0, (int) range[1]);
        FileUtil.writeBytes(bytes, "C:\\Users\\ggk911\\Desktop\\range1");
        // 取后面
        byte[] bytes1 = new byte[(int) range[3]];
        System.arraycopy(returnBytes, (int) range[2], bytes1, 0, (int) range[3]);
        FileUtil.writeBytes(bytes1, "C:\\Users\\ggk911\\Desktop\\range2");
        // 取中间
        byte[] bytes2 = new byte[(int) (range[2] - range[1])];
        System.arraycopy(returnBytes, (int) range[1], bytes2, 0, (int) (range[2] - range[1]));
        FileUtil.writeBytes(bytes2, "C:\\Users\\ggk911\\Desktop\\range3");


        // pkcs7
        String publicCertSm2 = "MIICmDCCAj+gAwIBAgIBAzAKBggqgRzPVQGDdTBYMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFQ2hpbmExFTATBgNVBAsMDEludGVybWVkaWF0ZTEiMCAGCSqGSIb3DQEJARYTMTM5ODMwNTM0NTVAMTYzLmNvbTAeFw0yNDAxMDIwOTA5NTFaFw0yNTAxMDEwOTA5NTFaMGYxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVDaGluYTESMBAGA1UEBwwJQ2hvbmdxaW5nMQ8wDQYDVQQDDAZHR0s5MTExIjAgBgkqhkiG9w0BCQEWEzEzOTgzMDUzNDU1QDE2My5jb20wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASNnEXTr1xvyeiCFyjvJMCitVR0UJ63UzfU4ftoGA0ejs3Pn/CLatCIzLfwjzimiqGf7jG8UxfOEdDY8QElLkqeo4HrMIHoMB0GA1UdDgQWBBSTRP7T62AAKxdBjQ5MDpIvQ+eaDDAfBgNVHSMEGDAWgBSKnV+2ua3/iWvHd8pNx80wRQmJYjAJBgNVHR8EAjAAMF8GCCsGAQUFBwEBAQH/BFAwTjAoBggrBgEFBQcwAYIcaHR0cDovLzEyNy4wLjAuMS9jYWlzc3VlLmh0bTAiBggrBgEFBQcwAoIWaHR0cDovLzEyNy4wLjAuMToyMDQ0MzAOBgNVHQ8BAf8EBAMCBsAwEgYDVR0TAQH/BAgwBgEB/wIBAzAWBgNVHSUBAf8EDDAKBggrBgEFBQcDCDAKBggqgRzPVQGDdQNHADBEAiBiMmP80Y2HbleOlOgGYSSpjANtC8rb8VxQ6/Fju2tiJwIgAjdUN740mQgwH6bTYoDw9oygZG8RVcpnrXYUWIVNt64=";
        String privateKeySm2 = "MIGHAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBG0wawIBAQQg2TSyBsSej2+rzLbzosJISpHpvxnHkytt/ZFya/v3bk6hRANCAASNnEXTr1xvyeiCFyjvJMCitVR0UJ63UzfU4ftoGA0ejs3Pn/CLatCIzLfwjzimiqGf7jG8UxfOEdDY8QElLkqe";
        // 证书链
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(publicCertSm2));
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "MCS_BC");
        Certificate[] certificateChain = new Certificate[1];
        certificateChain[0] = certificateFactory.generateCertificate(bis);
        // 签名/摘要
        String digest;
        String encryption;
        String algorithm = CertUtil.parseCert(publicCertSm2, CertUtil.PUBLIC_KEY_ALGORITHM);
        if (SecurityIDs.ID_RSA.equals(algorithm)) {
            encryption = "RSA";
            digest = "SHA256";
        } else if (SecurityIDs.ID_ECDSA.equals(algorithm)) {
            encryption = "SM2";
            digest = "SM3";
        } else {
            throw new IllegalArgumentException("签名/摘要，算法错误");
        }
        // 原文摘要
        byte[] oriDigest;
        MessageDigest messageDigest = DigestAlgorithms.getMessageDigest("SM3", "BC");
        byte[] updateData = new byte[(int) (range[1] + range[3])];
        System.arraycopy(returnBytes, 0, updateData, 0, (int) range[1]);
        System.arraycopy(returnBytes, (int) range[2], updateData, (int) range[1], (int) range[3]);
        FileUtil.writeBytes(updateData, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\createAnnotRange.pdf");
        ByteArrayInputStream input = new ByteArrayInputStream(updateData);
        byte[] buf = new byte[8192];
        int rd;
        while ((rd = input.read(buf, 0, buf.length)) > 0) {
            messageDigest.update(buf, 0, rd);
        }
        oriDigest = messageDigest.digest();

        byte[] sealBytes = FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\resources\\file\\image\\页眉log.png");
        PdfPKCS7 pdfPKCS7 = new PdfPKCS7(null, certificateChain, digest, "MCS_BC", null, false);
        // Attribute
        Calendar signingTime = Calendar.getInstance();
        byte[] attributeBytes = pdfPKCS7.getAuthenticatedAttributeBytes(oriDigest, signingTime, null, null, null, sealBytes, null);
        System.out.println(Hex.toHexString(attributeBytes));

        // 对attr签名
        byte[] signValue;
        SecuEngine secuEngine = new SecuEngine();
        if ("RSA".equals(encryption)) {
            signValue = Base64.decode(secuEngine.SignDataByRSA(privateKeySm2, attributeBytes, digest));
        } else if ("SM2".equals(encryption)) {
            signValue = Base64.decode(secuEngine.SignDataBySM2(privateKeySm2, attributeBytes));
        } else {
            throw new IllegalArgumentException("签名算法错误");
        }

        // contentInfo
        pdfPKCS7.setExternalDigest(signValue, null, encryption);
        TSAClientBouncyCastle tsaClientBouncyCastle = new TSAClientBouncyCastle("http://1.12.67.126:8082/tsa/sign?type=SM2");
        byte[] pkcs7 = pdfPKCS7.getEncodedPKCS7(oriDigest, signingTime, tsaClientBouncyCastle, null, null, null, null, null);
        String pkcs7Hex = "<" + Hex.toHexString(pkcs7);
        byte[] pkcs7HexBytes = pkcs7Hex.getBytes(StandardCharsets.UTF_8);
        // <SigValue
        System.arraycopy(pkcs7HexBytes, 0, replace, 0, pkcs7HexBytes.length);
        // >
        System.arraycopy(">".getBytes(StandardCharsets.UTF_8), 0, replace, replace.length - (rangeBytes.length + 1), 1);
        // SigValue替换
        System.arraycopy(replace, 0, returnBytes, (int) lit.getPosition(), replace.length);


        bos.close();
        pdfReader.close();
        FileUtil.writeBytes(returnBytes, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\createAnnot.pdf");
    }

    @Test
    public void pdfAnnotTest02() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Document document = new Document(PageSize.A4);
        FileOutputStream out = new FileOutputStream("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\create2.pdf");
        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setPdfVersion(PdfWriter.PDF_VERSION_1_4);
        document.open();
        // 4.添加内容
        document.add(new Paragraph("00000000000000000000000000000000000000"));
        document.close();

        byte[] pdf = FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\create2.pdf");
        PdfReader pdfReader = new PdfReader(pdf);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfStamper pdfStamper = new PdfStamper(pdfReader, bos);

        // pdfStamper.addSignature("signname", 1, 200, 200, 300, 300);
        // AcroFields acroFields = pdfReader.getAcroFields();
        // int totalRevisions = acroFields.getTotalRevisions();
        // for (int i = 0; i < totalRevisions; i++) {
        //     acroFields.signatureCoversWholeDocument("signname");
        // }


        int width = 245;
        int heifht = 80;

        Image image = Image.getInstance(FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\resources\\file\\image\\页眉log.png"));
        image.scaleToFit(width, heifht);
        image.setAbsolutePosition(100, 100);
        Annotation annotation = new Annotation("test", "1234");
        image.setAnnotation(annotation);
        PdfContentByte overContent = pdfStamper.getOverContent(1);
        overContent.addImage(image);

        pdfStamper.close();
        byte[] returnBytes = bos.toByteArray();
        bos.close();
        pdfReader.close();
        FileUtil.writeBytes(returnBytes, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\createAnnot.pdf");
    }

    @Test
    public void pdfAnnotTest03() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Document document = new Document(PageSize.A4);
        FileOutputStream out = new FileOutputStream("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\create2.pdf");
        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setPdfVersion(PdfWriter.PDF_VERSION_1_4);
        document.open();
        // 4.添加内容
        document.add(new Paragraph("00000000000000000000000000000000000000"));
        // 签名域
        PdfFormField signature = PdfFormField.createSignature(writer);
        signature.setWidget(new Rectangle(200, 200, 300, 300), PdfName.HIGHLIGHT);
        signature.setName("signname");
        signature.setFieldFlags(PdfAnnotation.FLAGS_HIDDEN);
        signature.setPage();
        signature.setMKBorderColor(BaseColor.BLACK);
        signature.setMKBackgroundColor(BaseColor.WHITE);
        PdfAppearance appearance = PdfAppearance.createAppearance(writer, 100, 100);
        appearance.rectangle(0.5f, 0.5f, 99f, 99f);
        appearance.stroke();
        signature.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, appearance);
        writer.addAnnotation(signature);
        // writer.setSigFlags(3);
        document.close();

        byte[] pdf = FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\create2.pdf");
        PdfReader pdfReader = new PdfReader(pdf);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfStamper pdfStamper = new PdfStamper(pdfReader, bos);
        pdfStamper.getWriter().setSigFlags(3);

        // int width = 245;
        // int heifht = 80;
        // Image image = Image.getInstance(FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\resources\\file\\image\\页眉log.png"));
        // image.scaleToFit(width, heifht);
        // image.setAbsolutePosition(100, 100);
        // Annotation annotation = new Annotation("test", "1234");
        // image.setAnnotation(annotation);
        // PdfContentByte overContent = pdfStamper.getOverContent(1);
        // overContent.addImage(image);


        pdfStamper.close();
        byte[] returnBytes = bos.toByteArray();
        bos.close();
        pdfReader.close();
        FileUtil.writeBytes(returnBytes, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\createAnnot.pdf");
    }

    @Test
    public void pdfAnnotPkcs7Test01() throws Exception {
        Security.addProvider(MCS_BC);
        // pkcs7
        String publicCertSm2 = "MIICmDCCAj+gAwIBAgIBAzAKBggqgRzPVQGDdTBYMQswCQYDVQQGEwJDTjEOMAwGA1UECgwFQ2hpbmExFTATBgNVBAsMDEludGVybWVkaWF0ZTEiMCAGCSqGSIb3DQEJARYTMTM5ODMwNTM0NTVAMTYzLmNvbTAeFw0yNDAxMDIwOTA5NTFaFw0yNTAxMDEwOTA5NTFaMGYxCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVDaGluYTESMBAGA1UEBwwJQ2hvbmdxaW5nMQ8wDQYDVQQDDAZHR0s5MTExIjAgBgkqhkiG9w0BCQEWEzEzOTgzMDUzNDU1QDE2My5jb20wWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAASNnEXTr1xvyeiCFyjvJMCitVR0UJ63UzfU4ftoGA0ejs3Pn/CLatCIzLfwjzimiqGf7jG8UxfOEdDY8QElLkqeo4HrMIHoMB0GA1UdDgQWBBSTRP7T62AAKxdBjQ5MDpIvQ+eaDDAfBgNVHSMEGDAWgBSKnV+2ua3/iWvHd8pNx80wRQmJYjAJBgNVHR8EAjAAMF8GCCsGAQUFBwEBAQH/BFAwTjAoBggrBgEFBQcwAYIcaHR0cDovLzEyNy4wLjAuMS9jYWlzc3VlLmh0bTAiBggrBgEFBQcwAoIWaHR0cDovLzEyNy4wLjAuMToyMDQ0MzAOBgNVHQ8BAf8EBAMCBsAwEgYDVR0TAQH/BAgwBgEB/wIBAzAWBgNVHSUBAf8EDDAKBggrBgEFBQcDCDAKBggqgRzPVQGDdQNHADBEAiBiMmP80Y2HbleOlOgGYSSpjANtC8rb8VxQ6/Fju2tiJwIgAjdUN740mQgwH6bTYoDw9oygZG8RVcpnrXYUWIVNt64=";
        String privateKeySm2 = "MIGHAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBG0wawIBAQQg2TSyBsSej2+rzLbzosJISpHpvxnHkytt/ZFya/v3bk6hRANCAASNnEXTr1xvyeiCFyjvJMCitVR0UJ63UzfU4ftoGA0ejs3Pn/CLatCIzLfwjzimiqGf7jG8UxfOEdDY8QElLkqe";
        // 证书链
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(publicCertSm2));
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "MCS_BC");
        Certificate[] certificateChain = new Certificate[1];
        certificateChain[0] = certificateFactory.generateCertificate(bis);
        // 签名/摘要
        String digest;
        String encryption;
        String algorithm = CertUtil.parseCert(publicCertSm2, CertUtil.PUBLIC_KEY_ALGORITHM);
        if (SecurityIDs.ID_RSA.equals(algorithm)) {
            encryption = "RSA";
            digest = "SHA256";
        } else if (SecurityIDs.ID_ECDSA.equals(algorithm)) {
            encryption = "SM2";
            digest = "SM3";
        } else {
            throw new IllegalArgumentException("签名/摘要，算法错误");
        }
        // thk's todo 2024/1/15 10:04 原文摘要
        byte[] oriDigest;
        oriDigest = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] sealBytes = FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\resources\\file\\image\\页眉log.png");
        PdfPKCS7 pdfPKCS7 = new PdfPKCS7(null, certificateChain, digest, "MCS_BC", null, false);
        // Attribute
        byte[] attributeBytes = pdfPKCS7.getAuthenticatedAttributeBytes(oriDigest, Calendar.getInstance(), null, null, null, sealBytes, null);
        System.out.println(Hex.toHexString(attributeBytes));

        // 对attr签名
        byte[] signValue;
        SecuEngine secuEngine = new SecuEngine();
        if ("RSA".equals(encryption)) {
            signValue = Base64.decode(secuEngine.SignDataByRSA(privateKeySm2, attributeBytes, digest));
        } else if ("SM2".equals(encryption)) {
            signValue = Base64.decode(secuEngine.SignDataBySM2(privateKeySm2, attributeBytes));
        } else {
            throw new IllegalArgumentException("签名算法错误");
        }

        // contentInfo
        pdfPKCS7.setExternalDigest(signValue, null, encryption);
        byte[] pkcs7 = pdfPKCS7.getEncodedPKCS7(oriDigest, Calendar.getInstance(), null, null);
        System.out.println(Hex.toHexString(pkcs7));

        // 替换值
        byte[] replaceBytes = new byte[10240];
        System.arraycopy(pkcs7, 0, replaceBytes, 0, pkcs7.length);

    }


}
