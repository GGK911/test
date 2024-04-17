package PkcsTest.PdfPkcs7;

import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/1 15:49
 */
public class PdfPkcs7Test {

    @Test
    @SneakyThrows
    public void analysisTest() {
        String inputFilePath = "src/main/java/PkcsTest/PdfPkcs7/1.txt"; // 输入文件路径
        String pdfPkcs7Str = new String(Files.readAllBytes(Paths.get(inputFilePath)));
        byte[] pdfPkcs7Bytes = Hex.decode(pdfPkcs7Str);
        ASN1InputStream asn1InputStream = new ASN1InputStream(pdfPkcs7Bytes);
        ASN1Primitive asn1Primitive;
        while ((asn1Primitive = asn1InputStream.readObject()) != null) {
            ASN1Sequence contentInfo = ASN1Sequence.getInstance(asn1Primitive);
            ASN1TaggedObject content = ASN1TaggedObject.getInstance(contentInfo.getObjectAt(1));
            ASN1Sequence signedData = ASN1Sequence.getInstance(content.getBaseObject());
            System.out.println("version>> " + ASN1Integer.getInstance(signedData.getObjectAt(0)).toString());
            ASN1Set singerInfos = ASN1Set.getInstance(signedData.getObjectAt(4));
            ASN1TaggedObject unSignedAttributes = ASN1TaggedObject.getInstance(ASN1Sequence.getInstance(singerInfos.getObjectAt(0)).getObjectAt(6));



        }
    }
}
