package pdfTest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfAppearance;
import com.itextpdf.text.pdf.PdfBorderDictionary;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.RadioCheckField;
import com.itextpdf.text.pdf.TextField;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author TangHaoKai
 * @version V1.0 2024/2/1 9:12
 **/
public class UpdatePdfFieldTest {

    @Test
    public void fieldTest() throws Exception {
        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

        Path path = Paths.get("src/main/java/pdfTest", "合同测试模板.pdf");
        Path fieldChange = Paths.get("src\\main\\java\\pdfTest\\fieldTest.pdf");
        ByteOutputStream byteOutputStream = new ByteOutputStream();
        PdfReader pdfReader = new PdfReader(Files.readAllBytes(path));
        PdfStamper pdfStamper = new PdfStamper(pdfReader, byteOutputStream);
        AcroFields acroFields = pdfStamper.getAcroFields();
        int pdfPage = pdfReader.getNumberOfPages();

        if (ObjectUtil.isNotNull(acroFields)) {
            //删除域文件
            Map<String, AcroFields.Item> acroFieldMap = acroFields.getFields();
            Set<String> keySet = acroFieldMap.keySet();
            Object[] keySetStr = keySet.toArray();

            //循环删除PDF域
            for (Object domain : keySetStr) {
                System.out.println("删除域名>> " + domain.toString());
                acroFields.removeField(domain.toString());
            }
        }

        // 添加一个文本框
        TextField field = new TextField(pdfStamper.getWriter(), new Rectangle(100, 100, 200, 200), "createField01");
        field.setFont(baseFont);
        // 多行
        field.setOptions(TextField.MULTILINE);
        PdfFormField textField = field.getTextField();
        textField.setPlaceInPage(1);
        pdfStamper.addAnnotation(textField, 1);

        // 再添加一个签名域
        final Rectangle rec = new Rectangle(200, 200, 300, 300);
        PdfFormField signature = PdfFormField.createSignature(pdfStamper.getWriter());
        signature.setFieldName("sign01");
        signature.setWidget(rec, PdfAnnotation.HIGHLIGHT_NONE);
        signature.setFlags(PdfAnnotation.FLAGS_PRINT);
        // 设置区域宽高和边框厚度，以及边框颜色，填充颜色
        PdfAppearance appearance = PdfAppearance.createAppearance(pdfStamper.getWriter(), rec.getWidth(), rec.getHeight());
        appearance.fillStroke();
        signature.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, appearance);
        pdfStamper.addAnnotation(signature, 1);

        // 此添加方式错误
        // 再添加一个复选框
        // final Rectangle rec1 = new Rectangle(300, 300, 318, 318);
        // PdfFormField checkBox = PdfFormField.createCheckBox(pdfStamper.getWriter());
        // checkBox.setFieldName("checkbox01");
        // checkBox.setWidget(rec1, PdfAnnotation.HIGHLIGHT_NONE);
        // checkBox.setMKBorderColor(BaseColor.BLACK);
        // checkBox.setFlags(PdfAnnotation.FLAGS_PRINT);
        // // 设置区域宽高和边框厚度，以及边框颜色，填充颜色
        // PdfAppearance appearance1 = PdfAppearance.createAppearance(pdfStamper.getWriter(), rec1.getWidth(), rec1.getHeight());
        // appearance1.fillStroke();
        // // 这里的state就是导出值
        // checkBox.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "true", appearance1);
        // pdfStamper.addAnnotation(checkBox, 1);

        //
        Rectangle rec1 = new Rectangle(300, 300, 320, 320);
        Rectangle rec2 = new Rectangle(320, 320, 400, 400);
        Rectangle[] recs = {rec1, rec2};
        for (int i = 0; i < recs.length; i++) {
            Rectangle rectangle = recs[i];
            PdfContentByte directContent = pdfStamper.getUnderContent(1);
            directContent.moveTo(0, 0);
            PdfAppearance tpOff = directContent.createAppearance(20, 20);
            tpOff.rectangle(1, 1, 18, 18);
            tpOff.stroke();
            RadioCheckField checkField = new RadioCheckField(pdfStamper.getWriter(), rectangle, "checkbox0" + (i + 1), "On");
            checkField.setCheckType(RadioCheckField.TYPE_CHECK);
            checkField.setBorderColor(BaseColor.BLACK);
            checkField.setBorderStyle(PdfBorderDictionary.STYLE_SOLID);
            checkField.setBorderWidth(1);
            // 字体大小
            checkField.setFontSize(rectangle.getHeight() - 4);

            // 勾选样式
            // checkField.setCheckType(RadioCheckField.TYPE_STAR);
            // checkField.setCheckType(RadioCheckField.TYPE_CROSS);
            PdfFormField checkBox = checkField.getCheckField();
            checkBox.setFlags(5);
            checkBox.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff);
            checkBox.setMKBorderColor(BaseColor.BLACK);
            checkBox.setMKBackgroundColor(BaseColor.WHITE);
            pdfStamper.addAnnotation(checkBox, 1);
        }

        // PdfFormField checkBox = PdfFormField.createCheckBox(pdfStamper.getWriter());
        // PdfContentByte directContent = pdfStamper.getUnderContent(1);
        // directContent.moveTo(0, 0);
        // PdfAppearance tpOff = directContent.createAppearance(20, 20);
        // tpOff.rectangle(1, 1, 18, 18);
        // tpOff.stroke();
        // PdfAppearance tpOn = directContent.createAppearance(20, 20);
        // tpOn.setRGBColorFill(255, 128, 128);
        // tpOn.rectangle(1, 1, 18, 18);
        // tpOn.fillStroke();
        // tpOn.moveTo(1, 1);
        // tpOn.lineTo(19, 19);
        // tpOn.moveTo(1, 19);
        // tpOn.lineTo(19, 1);
        // tpOn.stroke();
        // checkBox.setWidget(new Rectangle(300, 300, 318, 318), PdfAnnotation.HIGHLIGHT_INVERT);
        // checkBox.setFieldName("checkbox01");
        // checkBox.setValueAsName("Off");
        // checkBox.setAppearanceState("Off");
        // checkBox.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff);
        // checkBox.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "On", tpOn);
        // checkBox.setMKBorderColor(BaseColor.BLACK);
        // checkBox.setMKBackgroundColor(BaseColor.WHITE);
        // checkBox.setFlags(5);
        // pdfStamper.addAnnotation(checkBox, 1);

        pdfStamper.setFormFlattening(true);
        pdfStamper.close();
        FileUtil.writeBytes(byteOutputStream.getBytes(), fieldChange.toAbsolutePath().toString());
        byteOutputStream.close();
        System.out.println(">> " + fieldChange.toAbsolutePath());
    }

    @Test
    public void fillTest() throws Exception {
        Path path = Paths.get("src/main/java/pdfTest", "fieldTest.pdf");
        Path out = Paths.get("src\\main\\java\\pdfTest\\fillTest.pdf");
        Map<String, String> key = new HashMap<>();
        key.put("createField01", "1");
        key.put("checkbox01", "1");
        key.put("checkbox02", "1");
        // key.put("Check Box3", "1");
        // key.put("Check Box4", "1");
        final byte[] bytes = PdfUtil.pdfFill(Files.readAllBytes(path), key);
        FileUtil.writeBytes(bytes, out.toAbsolutePath().toString());
        System.out.println(">> " + out.toAbsolutePath());
    }

}
