package pdfTest;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.TextField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * pdf操作工具类
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-19 11:19
 **/
public class PdfUtil {
    /**
     * 生成待签署合同（填充文本域、复选框），遍历要填充的key，非法key抛出异常
     * 不适应框大小
     * 不支持生僻字
     * 复选框支持
     *
     * @param contractBytes    pdf
     * @param textDomainParams 填充数据JSON<key,value>格式
     * @return 待签署文件
     */
    public static byte[] pdfFill(byte[] contractBytes, String textDomainParams) {
        PdfReader pdfReader;
        try {
            pdfReader = new PdfReader(contractBytes);
        } catch (IOException e) {
            System.out.println("PdfReader异常");
            throw new RuntimeException("PdfReader异常");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfStamper pdfStamper;
        try {
            pdfStamper = new PdfStamper(pdfReader, bos);
        } catch (DocumentException | IOException e) {
            System.out.println("PdfStamper异常");
            throw new RuntimeException("PdfStamper异常");
        }
        AcroFields acroFields = pdfStamper.getAcroFields();
        if (acroFields.getFields().size() == 0) {
            System.out.println("无域信息");
            throw new RuntimeException("无域信息");
        }
        BaseFont font;
        try {
            font = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        } catch (DocumentException | IOException e) {
            System.out.println("PdfStamper异常");
            throw new RuntimeException("PdfStamper异常");
        }
        try {
            //文本域内容
            JSONObject textDomainObj = JSONUtil.parseObj(textDomainParams);
            for (String key : textDomainObj.keySet()) {
                if (StrUtil.isEmpty(key)) {
                    pdfStamper.close();
                    pdfReader.close();
                    System.out.println("有参数name为空！");
                    throw new RuntimeException("有参数name为空！");
                }
                String value = String.valueOf(textDomainObj.get(key));
                AcroFields.Item fieldItem = acroFields.getFieldItem(key);
                if (fieldItem == null) {
                    pdfStamper.close();
                    pdfReader.close();
                    System.out.println("非法参数：" + key);
                    throw new RuntimeException("非法参数：" + key);
                }
                // 文本框宽度
                List<AcroFields.FieldPosition> list = acroFields.getFieldPositions(key);
                AcroFields.FieldPosition fieldPosition = list.get(0);
                float domainWidth = fieldPosition.position.getRight() - fieldPosition.position.getLeft();

                // 字体大小
                PdfDictionary merged = fieldItem.getMerged(0);
                TextField textField = new TextField(null, null, key);
                acroFields.decodeGenericDictionary(merged, textField);
                float fontSize = textField.getFontSize();

                // 渲染文本宽度
                float widthPoint = font.getWidthPoint(value, fontSize);
                if (widthPoint > domainWidth) {
                    System.out.println("文本框:" + key + "长度超限，文本框宽度：" + domainWidth + "，渲染宽度：" + widthPoint);
                    throw new RuntimeException(key + "文本长度超限，建议扩大文本域宽度或缩小文本域填充字体大小");
                }

                // 字体
                acroFields.setFieldProperty(key, "textfont", font, null);
                // 域类型
                int fieldType = pdfReader.getAcroFields().getFieldType(key);
                EnumPdfDomainType pdfType = EnumPdfDomainType.getEnumByPdfCode(String.valueOf(fieldType));
                // 复选框
                if (pdfType == EnumPdfDomainType.CHECKBOX_DOMAIN) {
                    if (Boolean.TRUE.toString().equalsIgnoreCase(value) || "是".equals(value) || "1".equals(value)) {
                        acroFields.setField(key, "true", value);
                    } else {
                        System.out.println("此复选框：" + key + "值错误");
                        throw new RuntimeException("此复选框：" + key + "值错误，");
                    }
                } else {
                    // 其他填充域
                    acroFields.setField(key, value);
                }
            }
            // 如果为false那么生成的PDF文件还能编辑，一定要设为true
            pdfStamper.setFormFlattening(true);
            pdfStamper.close();
            pdfReader.close();
        } catch (DocumentException | IOException e) {
            System.out.println("填充异常");
            throw new RuntimeException("填充异常");
        }
        return bos.toByteArray();
    }

    /**
     * 工具key填充pdf模板中的文本域，非法key抛出异常
     * 单行文本域如文本宽度超过设置文本域宽度，字段缩小fontsize，直至为6报异常超限
     * 多行文本域如行数超限，如上
     * 生僻字支持
     * 复选框支持
     *
     * @param bytes pdf
     * @param date  <key, value> 填充数据
     * @return 填充后的pdf
     */
    public static byte[] pdfFill(byte[] bytes, Map<String, String> date) {
        // 字体
        BaseFont baseFont;
        // 备用字体
        BaseFont spareFont;
        try {
            baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font font2 = FontFactory.getFont("/font/JinbiaoSong.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 12f, Font.NORMAL, BaseColor.BLACK);
            spareFont = font2.getBaseFont();
        } catch (Exception e) {
            throw new RuntimeException("解析读取PDF文件异常createFont");
        }
        PdfReader reader;
        try {
            reader = new PdfReader(new PdfReader(bytes));
        } catch (IOException e) {
            throw new RuntimeException("读取文件数据异常");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfStamper stamper;
        try {
            stamper = new PdfStamper(reader, bos);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("pdf读取异常");
        }
        ArrayList<BaseFont> fontList = new ArrayList<>();
        fontList.add(baseFont);
        fontList.add(spareFont);
        AcroFields form = stamper.getAcroFields();
        form.setSubstitutionFonts(fontList);
        // 填充
        // 文本填充
        if (MapUtil.isNotEmpty(date)) {
            try {
                for (Map.Entry<String, String> next : date.entrySet()) {
                    String key = next.getKey();
                    String value = next.getValue();
                    BaseFont bf;
                    bf = baseFont;
                    // 默认12号字体
                    float fontSize = 12f;
                    PdfString da = form.getFieldItem(key).getMerged(0).getAsString(PdfName.DA);
                    if (da != null) {
                        Object[] dab = AcroFields.splitDAelements(da.toUnicodeString());
                        if (dab[AcroFields.DA_SIZE] != null) {
                            fontSize = (float) dab[AcroFields.DA_SIZE];
                        }
                    }
                    // 判断此表单是否有生僻字，有生僻字启用备用字体
                    boolean badWord = false;
                    StringBuilder removeLetterAndNumber = new StringBuilder();
                    for (char c : value.toCharArray()) {
                        if (CharUtil.isLetterOrNumber(c)) {
                            continue;
                        }
                        removeLetterAndNumber.append(c);
                    }
                    if (StrUtil.isNotEmpty(removeLetterAndNumber.toString()) && (bf.getWidthPoint(removeLetterAndNumber.toString(), fontSize) < spareFont.getWidthPoint(removeLetterAndNumber.toString(), fontSize))) {
                        bf = spareFont;
                        badWord = true;
                        System.out.println("字段：" + UnicodeUtil.toUnicode(key) + "，此填充字段有生僻字，使用备用字体");
                    }
                    // 渲染后文字宽度
                    float textWidth = bf.getWidthPoint(value, fontSize);
                    // 文本单行高度
                    float ascent = bf.getFontDescriptor(BaseFont.ASCENT, fontSize);
                    // 文本框宽度
                    Rectangle position = form.getFieldPositions(key).get(0).position;
                    float textBoxWidth = position.getWidth();
                    // 文本框高度
                    float textBoxHeight = position.getHeight();
                    // 理想多行文本框行数
                    double line = Math.ceil(textWidth / textBoxWidth);
                    // 总的文字宽度
                    float totalTextWidth = (float) (textWidth + line * fontSize);
                    // 实际行数
                    double realLine = Math.ceil(totalTextWidth / textBoxWidth);
                    // 文本框最多显示全行数
                    double boxMaxLine = Math.floor(textBoxHeight / (ascent * 1.25));
                    PdfObject directObject = form.getFieldItem(key).getMerged(0).getDirectObject(PdfName.FF);
                    // 文本框高度只够写一行，并且文字宽度大于文本宽度，则缩小字体
                    // 判断单行文本
                    if (directObject == null) {
                        // 单行文本框
                        while (textWidth > textBoxWidth) {
                            fontSize--;
                            if (fontSize < 6) {
                                System.out.println("字体长度超限");
                                throw new RuntimeException("文本域：" + key + "文本长度超限，请调整");
                            }
                            textWidth = bf.getWidthPoint(value, fontSize);
                        }
                    } else {
                        // 多行文本框
                        while (boxMaxLine <= realLine) {
                            fontSize--;
                            if (fontSize < 6) {
                                System.out.println("字体长度超限");
                                throw new RuntimeException("文本域：" + key + "文本长度超限，请调整");
                            }
                            ascent = bf.getFontDescriptor(BaseFont.ASCENT, fontSize);
                            // 字体减小后的理想总长度
                            textWidth = bf.getWidthPoint(value, fontSize);
                            // 字体减小后的文本框的最大行数
                            boxMaxLine = Math.floor(textBoxHeight / (ascent * 1.25));
                            // 字体减小后的理想行数
                            line = Math.ceil(textWidth / textBoxHeight);
                            // 字体减小后实际总长度
                            totalTextWidth = (float) (textWidth + line * fontSize);
                            // 实际行数
                            realLine = Math.ceil(totalTextWidth / textBoxWidth);
                        }
                    }
                    form.setFieldProperty(key, "textsize", fontSize, null);
                    // 是否重设字体样式（针对生僻字）
                    if (badWord) {
                        form.setFieldProperty(key, "textfont", spareFont, null);
                    }
                    // 域类型
                    int fieldType = reader.getAcroFields().getFieldType(key);
                    EnumPdfDomainType pdfType = EnumPdfDomainType.getEnumByPdfCode(String.valueOf(fieldType));
                    // 复选框
                    if (pdfType == EnumPdfDomainType.CHECKBOX_DOMAIN) {
                        if (Boolean.TRUE.toString().equalsIgnoreCase(value) || "是".equals(value) || "1".equals(value)) {
                            form.setField(key, "true", value);
                        } else {
                            System.out.println("此复选框：" + key + "，值错误");
                            // thk's todo 是否抛出
                            // throw new RuntimeException(EnumException.TEMP_FILL_ERROR).setMessage("此复选框：" + key + "，值错误，");
                        }
                    } else {
                        form.setField(key, value);
                    }
                }
            } catch (IOException | DocumentException e) {
                throw new RuntimeException("设置文本域内容异常");
            }
        }
        // 如果为false那么生成的PDF文件还能编辑
        stamper.setFormFlattening(true);
        try {
            stamper.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("关闭pdf解析工具异常");
        }
        byte[] returnBytes = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException("解析读取PDF复制PDF异常PdfCopy");
        }
        reader.close();
        return returnBytes;
    }

    /**
     * 获取PDF参数
     *
     * @param bytes                 模板
     * @param templateParameterList 参数list
     * @param templateId            模板ID
     */
    public static void getPdfDomain(byte[] bytes, List<PdfParameterEntity> templateParameterList, String templateId) {
        PdfReader reader;
        PdfStamper stamper;
        try {
            reader = new PdfReader(new PdfReader(bytes));
        } catch (Exception e) {
            throw new RuntimeException("参数上传模板-解析文本域-读取文件数据异常");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            stamper = new PdfStamper(reader, bos);
        } catch (Exception e) {
            throw new RuntimeException("参数上传模板-解析文本域-pdf读取异常");
        }
        AcroFields form = stamper.getAcroFields();
        if (ObjectUtil.isNull(form)) {
            throw new RuntimeException("参数上传模板-解析文本域-请根据模板要求上传数据");
        }
        try {
            for (String name : form.getFields().keySet()) {
                //获取key值
                //获取域的X轴，Y轴
                List<AcroFields.FieldPosition> list = form.getFieldPositions(name);
                AcroFields.FieldPosition fieldPosition = list.get(0);
                //所在页数
                int page = fieldPosition.page;
                //域名类型
                int fieldType = reader.getAcroFields().getFieldType(name);
                EnumPdfDomainType pdfType = EnumPdfDomainType.getEnumByPdfCode(String.valueOf(fieldType));
                TextField textField = null;
                //字体大小
                float size = 0;
                //这里是筛选表单文本域的
                if (EnumPdfDomainType.TEXT_DOMAIN.getCode().equals(pdfType.getCode())) {
                    PdfDictionary merged = form.getFieldItem(name).getMerged(0);
                    textField = new TextField(null, null, name);
                    form.decodeGenericDictionary(merged, textField);
                    size = textField.getFontSize();
                }
                // 模板参数构造
                PdfParameterEntity templateParameter = PdfParameterEntity.builder()
                        .templateId(templateId)
                        .pageNo(page + "")
                        .positionXl(fieldPosition.position.getLeft() + "")
                        .positionXr(fieldPosition.position.getRight() + "")
                        .positionYl(fieldPosition.position.getBottom() + "")
                        .positionYr(fieldPosition.position.getTop() + "")
                        .keyword(name)
                        .type(pdfType.getCode())
                        .textFont(textField == null ? null : textField.getFont().getFullFontName()[0][3])
                        .textSize(String.valueOf(size))
                        .textLength(String.valueOf(fieldPosition.position.getRight() - fieldPosition.position.getLeft()))
                        .createTime(DateUtil.date())
                        .build();
                templateParameterList.add(templateParameter);
            }
        } catch (Exception e) {
            throw new RuntimeException("参数上传模板-解析文本域-设置文本域内容异常");
        }
        try {
            stamper.close();
            bos.close();
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("参数上传模板-解析文本域-关闭pdf解析工具异常");
        }
    }

}
