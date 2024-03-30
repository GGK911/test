package pdfTest;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfAnnotation;
import com.itextpdf.text.pdf.PdfAppearance;
import com.itextpdf.text.pdf.PdfBorderDictionary;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.RadioCheckField;
import com.itextpdf.text.pdf.TextField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * pdf操作工具类
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-19 11:19
 **/
public class PdfUtil {

    /**
     * 基础字体
     */
    private static final BaseFont BASE_FONT;
    /**
     * 备用字体-金标宋
     */
    private static final BaseFont SPARE_FONT;
    /**
     * 备用字体2-宋-extB
     */
    private static final BaseFont SPARE_FONT2;

    static {
        try {
            BASE_FONT = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font font1 = FontFactory.getFont("src/main/resources/font/JinbiaoSong.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 12f, Font.NORMAL, BaseColor.BLACK);
            Font font2 = FontFactory.getFont("src/main/resources/font/simsunb.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 12f, Font.NORMAL, BaseColor.BLACK);
            SPARE_FONT = font1.getBaseFont();
            SPARE_FONT2 = font2.getBaseFont();
        } catch (Exception e) {
            throw new RuntimeException("加载字体异常");
        }
    }

    /**
     * 图片填充参数
     */
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FillImageParam {
        /**
         * KEY(域名)
         * 暂无用，一般在外部有KEY
         */
        String key;
        /**
         * 图片base64
         */
        String base64;
        /**
         * 旋转角度
         */
        String rotate;
        /**
         * 是否铺满域
         */
        String isTile;
        /**
         * 是否居中域
         */
        String isCenter;
    }


    /**
     * 模板参数表
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PdfParameterEntity {

        /**
         * Column -- 页数 --length:3
         */
        private String pageNo;

        /**
         * Column -- 位置X1 --length:32
         */
        private String positionXl;

        /**
         * Column -- 位置Y1 --length:32
         */
        private String positionYl;

        /**
         * Column -- 位置X2 --length:32
         */
        private String positionXr;

        /**
         * Column -- 位置Y2 --length:32
         */
        private String positionYr;

        /**
         * Column -- 模板域key值 --length:64
         */
        private String keyword;

        /**
         * Column -- 类型：1:表单文本域，2：签名域，3：复选框 --length:2
         */
        private String type;

        /**
         * Column -- 文本域字体 --length:32
         */
        private String textFont;

        /**
         * Column -- 文本域字体大小 --length:32
         */
        private String textSize;

        /**
         * Column -- 文本域长度 --length:32
         */
        private String textLength;

        public PdfParameterEntity(String pageNo, String positionXl, String positionYl, String positionXr, String positionYr, String keyword, String type) {
            this.pageNo = pageNo;
            this.positionXl = positionXl;
            this.positionYl = positionYl;
            this.positionXr = positionXr;
            this.positionYr = positionYr;
            this.keyword = keyword;
            this.type = type;
        }
    }


    /**
     * 文本域、复选框填充
     * 遍历要填充的key，非法key抛出异常
     *
     * @param pdfByte          pdf
     * @param textDomainParams 填充数据JSON<key,value>格式
     * @return 待签署文件
     */
    public static byte[] pdfFill(byte[] pdfByte, String textDomainParams) {
        JSONObject jsonObject = JSON.parseObject(textDomainParams);
        Map<String, String> toStringMap = jsonObject.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue())));
        return pdfFill(pdfByte, toStringMap);
    }

    /**
     * 文本域、复选框填充
     * 根据key填充pdf模板中的文本域，非法key抛出异常
     *
     * @param pdfByte  pdf
     * @param fillData <key, value> 填充数据
     * @return 填充后的pdf
     */
    public static byte[] pdfFill(byte[] pdfByte, Map<String, String> fillData) {
        Map<String, Object> stringObjectMap = fillData.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return pdfFill(pdfByte, stringObjectMap, null);
    }

    /**
     * 文本域、复选框、图片填充
     * 单行文本域如文本宽度超过设置文本域宽度，字段缩小fontsize，直至为6报异常超限
     * 多行文本域如行数超限，如上
     * 生僻字支持
     * 复选框支持
     * 图片支持
     *
     * @param pdfByte  pdf
     * @param fillData 填充MAP value可以是普通字符串，也可以是图片填充对象FillImageParam
     * @param picByte  填充图片 填充图片默认使用FillImageParam中的Base64，如为空则用此
     * @return 填充后的pdf
     */
    // thk's todo 2024/3/27 16:42 这里做复杂了
    public static byte[] pdfFill(byte[] pdfByte, Map<String, Object> fillData, byte[] picByte) {
        PdfReader reader;
        try {
            reader = new PdfReader(new PdfReader(pdfByte));
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
        fontList.add(BASE_FONT);
        fontList.add(SPARE_FONT);
        fontList.add(SPARE_FONT2);
        AcroFields form = stamper.getAcroFields();
        // if (form.getFields().size() == 0) {
        //     throw new RuntimeException("无域信息");
        // }
        form.setSubstitutionFonts(fontList);
        // if (MapUtil.isEmpty(fillData)) {
        //     throw new RuntimeException("填充参数为空");
        // }
        try {
            for (Map.Entry<String, Object> next : fillData.entrySet()) {
                String key = next.getKey();
                if (form.getFieldItem(key) == null) {
                    throw new IllegalArgumentException("未知参数" + key);
                }
                Object value = next.getValue();
                // 文本类型
                if (value instanceof String) {
                    fillText(form, key, String.valueOf(value));
                }
                // 填充的是图片类型
                if (value instanceof FillImageParam) {
                    FillImageParam fillImage = (FillImageParam) value;
                    if (StrUtil.isEmpty(fillImage.getBase64())) {
                        if (picByte == null) {
                            throw new RuntimeException("此域无图片数据KEY：" + key);
                        }
                        fillImage(stamper, picByte, form, key, Boolean.parseBoolean(fillImage.getIsTile()), Boolean.parseBoolean(fillImage.getIsCenter()), StrUtil.isEmpty(fillImage.getRotate()) ? 0D : Double.parseDouble(fillImage.getRotate()));
                    } else {
                        byte[] picBase64Decode;
                        try {
                            picBase64Decode = Base64.decode(fillImage.getBase64());
                        } catch (Exception e) {
                            throw new RuntimeException("图片Base64转换失败");
                        }
                        fillImage(stamper, picBase64Decode, form, key, Boolean.parseBoolean(fillImage.getIsTile()), Boolean.parseBoolean(fillImage.getIsCenter()), StrUtil.isEmpty(fillImage.getRotate()) ? 0D : Double.parseDouble(fillImage.getRotate()));
                    }
                }
            }
        } catch (IOException | DocumentException e) {
            throw new RuntimeException("设置文本域内容异常");
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
     * @param bytes 模板
     */
    public static List<PdfParameterEntity> getPdfDomain(byte[] bytes) {
        List<PdfParameterEntity> templateParameterList = new ArrayList<>();
        PdfReader reader;
        PdfStamper stamper;
        try {
            reader = new PdfReader(new PdfReader(bytes));
        } catch (Exception e) {
            throw new RuntimeException("获取PDF参数-解析文本域-读取文件数据异常");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            stamper = new PdfStamper(reader, bos);
        } catch (Exception e) {
            throw new RuntimeException("获取PDF参数-解析文本域-pdf读取异常");
        }
        AcroFields form = stamper.getAcroFields();
        if (ObjectUtil.isNull(form)) {
            throw new RuntimeException("获取PDF参数-解析文本域-请根据模板要求上传数据");
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
                        .build();
                templateParameterList.add(templateParameter);
            }
        } catch (Exception e) {
            throw new RuntimeException("获取PDF参数-解析文本域异常");
        }
        try {
            stamper.close();
            bos.close();
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("获取PDF参数-解析文本域-关闭pdf解析工具异常");
        }
        return templateParameterList;
    }

    /**
     * 去除PDF所有域
     *
     * @param pdfBytes PDF文件
     * @return 文件
     */
    public static byte[] removeAllField(byte[] pdfBytes) {
        return removeField(pdfBytes, null);
    }

    /**
     * 去除指定PDF域
     *
     * @param pdfBytes      域文件
     * @param fieldNameList 要去除的域名称
     * @return 文件
     */
    public static byte[] removeFieldByNames(byte[] pdfBytes, List<String> fieldNameList) {
        if (fieldNameList == null || fieldNameList.size() == 0) {
            throw new IllegalArgumentException("要去除的字段为空");
        }
        return removeField(pdfBytes, fieldNameList);
    }

    /**
     * PDF添加域(删除之前所有域)
     *
     * @param pdfBytes  PDF文件
     * @param fieldName 域名称
     * @param fieldType 类型(文本/签名域/复选框)
     * @param rectangle 位置
     * @param page      页数
     * @return 添加后文件
     */
    public static byte[] addField(byte[] pdfBytes, String fieldName, String fieldType, Rectangle rectangle, int page) {
        PdfParameterEntity pdfParameterEntity = new PdfParameterEntity(String.valueOf(page), String.valueOf(rectangle.getLeft()), String.valueOf(rectangle.getBottom()), String.valueOf(rectangle.getRight()), String.valueOf(rectangle.getTop()), fieldName, fieldType);
        ArrayList<PdfParameterEntity> paramList = new ArrayList<>();
        paramList.add(pdfParameterEntity);
        return addFields(pdfBytes, paramList);
    }

    /**
     * PDF添加域(删除之前所有域)
     *
     * @param pdfBytes            PDF文件
     * @param parameterEntityList 参数
     * @return 添加后文件
     */
    public static byte[] addFields(byte[] pdfBytes, List<PdfParameterEntity> parameterEntityList) {
        byte[] bytes = removeAllField(pdfBytes);
        PdfReader reader;
        PdfStamper stamper;
        try {
            reader = new PdfReader(new PdfReader(bytes));
        } catch (Exception e) {
            throw new RuntimeException("PDF添加域-解析文本域-读取文件数据异常");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            stamper = new PdfStamper(reader, bos);
        } catch (Exception e) {
            throw new RuntimeException("PDF添加域-解析文本域-pdf读取异常");
        }
        AcroFields form = stamper.getAcroFields();
        if (ObjectUtil.isNull(form)) {
            throw new RuntimeException("PDF添加域-解析文本域-请根据模板要求上传数据");
        }
        for (PdfParameterEntity parameter : parameterEntityList) {
            int page = Integer.parseInt(parameter.getPageNo());
            String fieldType = parameter.getType();
            String fieldName = parameter.getKeyword();
            float lx = Float.parseFloat(parameter.getPositionXl());
            float ly = Float.parseFloat(parameter.getPositionYl());
            float rx = Float.parseFloat(parameter.getPositionXr());
            float ry = Float.parseFloat(parameter.getPositionYr());
            Rectangle rectangle = new Rectangle(lx, ly, rx, ry);
            PdfFormField field;
            try {
                if ("1".equals(fieldType)) {
                    final TextField textField1 = new TextField(stamper.getWriter(), rectangle, fieldName);
                    textField1.setFont(BASE_FONT);
                    // 默认多行
                    textField1.setOptions(TextField.MULTILINE);
                    field = textField1.getTextField();
                    field.setPlaceInPage(page);
                } else if ("2".equals(fieldType)) {
                    field = PdfFormField.createSignature(stamper.getWriter());
                    field.setFieldName(fieldName);
                    field.setWidget(rectangle, PdfAnnotation.HIGHLIGHT_NONE);
                    field.setFlags(PdfAnnotation.FLAGS_PRINT);
                    // 设置区域宽高和边框厚度，以及边框颜色，填充颜色
                    PdfAppearance appearance = PdfAppearance.createAppearance(stamper.getWriter(), rectangle.getWidth(), rectangle.getHeight());
                    appearance.fillStroke();
                    field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, appearance);
                } else if ("3".equals(fieldType)) {
                    PdfContentByte directContent = stamper.getUnderContent(page);
                    if (directContent == null) {
                        throw new IllegalArgumentException("页码非法");
                    }
                    directContent.moveTo(0, 0);
                    PdfAppearance tpOff = directContent.createAppearance(rectangle.getWidth(), rectangle.getHeight());
                    tpOff.rectangle(1, 1, 18, 18);
                    tpOff.stroke();
                    RadioCheckField checkField = new RadioCheckField(stamper.getWriter(), rectangle, fieldName, "On");
                    checkField.setCheckType(RadioCheckField.TYPE_CHECK);
                    checkField.setBorderColor(BaseColor.BLACK);
                    checkField.setBorderStyle(PdfBorderDictionary.STYLE_SOLID);
                    checkField.setBorderWidth(1);
                    // 字体大小
                    checkField.setFontSize(rectangle.getHeight() - 4);
                    // 勾选样式
                    // checkField.setCheckType(RadioCheckField.TYPE_STAR);
                    // checkField.setCheckType(RadioCheckField.TYPE_CROSS);
                    field = checkField.getCheckField();
                    field.setFlags(5);
                    field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff);
                    field.setMKBorderColor(BaseColor.BLACK);
                    field.setMKBackgroundColor(BaseColor.WHITE);
                } else {
                    throw new IllegalArgumentException("域类型错误");
                }
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
                throw new RuntimeException("创建域异常");
            }
            stamper.addAnnotation(field, page);
        }
        try {
            stamper.close();
            bos.close();
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("PDF添加域-解析文本域-关闭pdf解析工具异常");
        }
        return bos.toByteArray();
    }

    /**
     * 获取PDF总页数
     *
     * @param pdfBytes PDF文件
     * @return PDF总页数
     */
    public static int getTotalPage(byte[] pdfBytes) {
        PdfReader reader = null;
        try {
            reader = new PdfReader(new PdfReader(pdfBytes));
            return reader.getNumberOfPages();
        } catch (Exception e) {
            throw new RuntimeException("参数上传模板-解析文本域-读取文件数据异常");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }



    //***************************************************私有方法*********************************************************//

    private static byte[] copyAnyPage(byte[] pdfBytes, List<Integer> pages) {
        PdfReader reader;
        try {
            reader = new PdfReader(new PdfReader(pdfBytes));
        } catch (Exception e) {
            throw new RuntimeException("参数上传模板-解析文本域-读取文件数据异常");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 总页数
        int totalPage = reader.getNumberOfPages();
        // if (pageStart > pageNum || pageEnd > pageNum) {
        //     throw new IllegalArgumentException("页码越界");
        // }
        Document document = new Document();
        PdfCopy copy = null;
        try {
            copy = new PdfCopy(document, bos);
        } catch (DocumentException e) {
            throw new RuntimeException("创建复制类异常");
        }

        return null;
    }

    /**
     * 去除域(域名称)
     *
     * @param pdfBytes      PDF文件
     * @param fieldNameList 要去除的域名称
     * @return 文件
     */
    private static byte[] removeField(byte[] pdfBytes, List<String> fieldNameList) {
        PdfReader reader;
        PdfStamper stamper;
        try {
            reader = new PdfReader(new PdfReader(pdfBytes));
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
            throw new RuntimeException("参数上传模板-解析文本域-空模板");
        }
        if (fieldNameList == null || fieldNameList.size() == 0) {
            // 所有
            final Map<String, AcroFields.Item> fields = form.getFields();
            final List<String> keys = new ArrayList<>(fields.keySet());
            for (String key : keys) {
                form.removeField(key);
            }
        } else {
            // 指定
            for (String name : fieldNameList) {
                form.removeField(name);
                System.out.println("DELETE>>" + name);
            }
        }
        try {
            stamper.close();
            bos.close();
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("参数上传模板-解析文本域-关闭pdf解析工具异常");
        }
        return bos.toByteArray();
    }

    /**
     * PDF填充图片通过域
     *
     * @param stamper  pdf文件
     * @param picByte  图片
     * @param form     pdf所有文本域
     * @param key      要填充的文本域
     * @param isScale  是否铺满整个文本域
     * @param isCenter 是否居中文本域
     * @param rotate   旋转角度 正数：顺时针 负数：逆时针
     * @throws IOException         旋转图片异常
     * @throws BadElementException 读取图片异常/PdfContentByte添加图片异常
     */
    private static void fillImage(PdfStamper stamper, byte[] picByte, AcroFields form, String key, boolean isScale, boolean isCenter, Double rotate) throws DocumentException, IOException {
        // 旋转
        if (rotate != null && rotate != 0) {
            picByte = rotate(picByte, rotate);
        }
        fillImageByField(stamper, picByte, form, key, isScale, isCenter);
    }

    /**
     * PDF填充图片通过域
     *
     * @param stamper  pdf文件
     * @param picByte  图片
     * @param form     pdf所有文本域
     * @param key      要填充的文本域
     * @param isScale  是否铺满整个文本域
     * @param isCenter 是否居中文本域
     * @throws IOException       读取图片异常
     * @throws DocumentException 读取图片异常/PdfContentByte添加图片异常
     */
    private static void fillImageByField(PdfStamper stamper, byte[] picByte, AcroFields form, String key, boolean isScale, boolean isCenter) throws DocumentException, IOException {
        // 通过域名获取所在页和坐标，左下角为起点
        int pageNo = form.getFieldPositions(key).get(0).page;
        Rectangle signRect = form.getFieldPositions(key).get(0).position;
        // 图片位置默认与左下角对齐
        float x = signRect.getLeft();
        float y = signRect.getBottom();
        // 缩放
        if (isScale) {
            picByte = scale(picByte, signRect.getWidth(), signRect.getHeight());
        }
        // 计算居中坐标
        if (isCenter) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(picByte);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageUtil.scaleBySizeKeepRatio(inputStream, outputStream, (int) signRect.getWidth(), (int) signRect.getHeight());
            Image image = Image.getInstance(outputStream.toByteArray());
            x = signRect.getLeft() + (signRect.getWidth() / 2 - image.getWidth() / 2);
            y = signRect.getBottom() + (signRect.getHeight() / 2 - image.getHeight() / 2);
        }
        // 有了坐标
        fillImageByCoordinate(stamper, picByte, pageNo, x, y, signRect.getWidth(), signRect.getHeight());
    }

    /**
     * PDF填充图片通过坐标
     *
     * @param stamper pdf文件
     * @param picByte 文件
     * @param page    页数
     * @param x       图片左下坐标X
     * @param y       图片左下坐标Y
     * @param width   宽
     * @param height  高
     * @throws IOException       读取图片异常
     * @throws DocumentException PdfContentByte添加图片异常/旋转图片异常
     */
    private static void fillImageByCoordinate(PdfStamper stamper, byte[] picByte, int page, float x, float y, Float width, Float height) throws DocumentException, IOException {
        // 获取操作的页面
        PdfContentByte under = stamper.getOverContent(page);
        Image image = Image.getInstance(picByte);
        // 根据高宽缩放图片
        if (width != null && height != null) {
            image.scaleToFit(width, height);
        } else {
            image.scaleToFit(image.getWidth(), image.getHeight());
        }
        image.setAbsolutePosition(x, y);
        under.addImage(image);
    }

    /**
     * 旋转图片
     *
     * @param picByte 图片
     * @param rotate  旋转角度 正数：顺时针 负数：逆时针
     * @return 旋转后图片
     * @throws IOException         读取图片异常
     * @throws BadElementException 读取图片异常
     */
    private static byte[] rotate(byte[] picByte, Double rotate) throws BadElementException, IOException {
        ByteArrayOutputStream imageByte = new ByteArrayOutputStream();
        Image instance = Image.getInstance(picByte);
        ImageUtil.rotateByAngle(new ByteArrayInputStream(picByte), imageByte, (int) instance.getWidth(), (int) instance.getHeight(), rotate);
        return imageByte.toByteArray();
    }

    /**
     * 调整图片宽高
     *
     * @param picByte 图片
     * @param width   调整后宽度
     * @param height  调整后高度
     * @return 调整宽高后图片
     */
    private static byte[] scale(byte[] picByte, float width, float height) {
        ByteArrayOutputStream imageByte = new ByteArrayOutputStream();
        ImageUtil.scaleBySize(new ByteArrayInputStream(picByte), imageByte, (int) width, (int) height);
        return imageByte.toByteArray();
    }

    /**
     * 文本填充
     *
     * @param form  所有pdf域
     * @param key   KEY
     * @param value VALUE
     */
    private static void fillText(AcroFields form, String key, String value) throws IOException, DocumentException {
        // 获取设置字体大小，默认12
        float fontSize = getFontSize(form, key);
        // 字体选择器
        FontSelector fs = new FontSelector();
        ArrayList<BaseFont> substitutionFonts = form.getSubstitutionFonts();
        if (substitutionFonts != null) {
            for (BaseFont substitutionFont : substitutionFonts) {
                fs.addFont(new Font(substitutionFont, fontSize, 0, GrayColor.GRAYBLACK));
            }
        }
        Phrase phrase = fs.process(value);
        fontSize = judgeFontSize(fontSize, form, key, phrase);
        form.setFieldProperty(key, "textsize", fontSize, null);
        // 域类型
        int fieldType = form.getFieldType(key);
        EnumPdfDomainType pdfType = EnumPdfDomainType.getEnumByPdfCode(String.valueOf(fieldType));
        // 复选框
        if (pdfType == EnumPdfDomainType.CHECKBOX_DOMAIN) {
            // 参考 iText in Action 2nd edition (Bruno Lowagie) PAGE-240 8.2.2
            String[] states = form.getAppearanceStates(key);
            final String trueState = Arrays.stream(states).filter(state -> state.equalsIgnoreCase("on") || state.equalsIgnoreCase("true") || state.equalsIgnoreCase("是") || state.equalsIgnoreCase("1")).findFirst().orElse(null);
            final String falseState = Arrays.stream(states).filter(state -> state.equalsIgnoreCase("off") || state.equalsIgnoreCase("false") || state.equalsIgnoreCase("否") || state.equalsIgnoreCase("0")).findFirst().orElse(null);
            if (StrUtil.isEmpty(trueState) || StrUtil.isEmpty(falseState)) {
                throw new IllegalArgumentException("复选框导出值错误");
            }
            if ("是".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
                form.setField(key, trueState, true);
            } else if ("否".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                form.setField(key, falseState, false);
            } else {
                System.out.println("此复选框：" + key + "，值错误");
                // thk's todo 是否抛出
                // throw new RuntimeException(EnumException.TEMP_FILL_ERROR).setMessage("此复选框：" + key + "，值错误，");
            }
        } else {
            form.setField(key, value);
        }
    }

    /**
     * 获取带字体的Phrase的宽度
     *
     * @param phrase   要填充的文本(由多个chunk组成的文本)
     * @param fontSize 字体大小
     * @return 总长度
     */
    private static float getPhraseAllChunkWidth(Phrase phrase, float fontSize) {
        float widthPoint = 0;
        List<Chunk> chunkList = phrase.getChunks();
        for (Chunk chunk : chunkList) {
            BaseFont baseFont = chunk.getFont().getBaseFont();
            widthPoint += baseFont.getWidthPoint(chunk.getContent(), fontSize);
        }
        return widthPoint;
    }

    /**
     * 获取设置字体大小
     *
     * @param form 所有文本域
     * @param key  域
     * @return 字体大小
     */
    private static float getFontSize(AcroFields form, String key) {
        PdfString da = form.getFieldItem(key).getMerged(0).getAsString(PdfName.DA);
        if (da != null) {
            Object[] dab = AcroFields.splitDAelements(da.toUnicodeString());
            if (dab[AcroFields.DA_SIZE] != null) {
                return (float) dab[AcroFields.DA_SIZE];
            }
        }
        // 默认字体大小
        return 12;
    }

    /**
     * 判断字体适合大小（针对文本框）
     *
     * @param fontSize 字体大小
     * @param form     文本域
     * @param key      KEY
     * @param phrase   VALUE
     * @return 适合大小
     */
    private static float judgeFontSize(float fontSize, AcroFields form, String key, Phrase phrase) {
        // 渲染后文字宽度
        float textWidth = getPhraseAllChunkWidth(phrase, fontSize);
        // 文本单行高度(只算上升ASCENT，不算下降)
        float ascent = BASE_FONT.getFontDescriptor(BaseFont.ASCENT, fontSize);
        // float deascent = BASE_FONT.getFontDescriptor(BaseFont.DESCENT, fontSize);
        // 文本框宽度
        Rectangle position = form.getFieldPositions(key).get(0).position;
        float textBoxWidth = position.getWidth();
        // 文本框高度
        float textBoxHeight = position.getHeight();
        // 理想多行文本框行数
        double line = Math.ceil(textWidth / textBoxWidth);
        // 总的文字宽度(因为实际渲染出来每行始终有一个字的误差)
        float totalTextWidth = (float) (textWidth + line * fontSize);
        // 实际行数
        double realLine = Math.ceil(totalTextWidth / textBoxWidth);
        // 文本框最多显示全行数
        double boxMaxLine = Math.floor(textBoxHeight / (ascent * 1.25));
        // PDF Reference PAGE-692 Multiline flag
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
                textWidth = getPhraseAllChunkWidth(phrase, fontSize);
            }
        } else {
            // 多行文本框
            while (boxMaxLine <= realLine) {
                fontSize--;
                if (fontSize < 6) {
                    System.out.println("字体长度超限");
                    throw new RuntimeException("文本域：" + key + "文本长度超限，请调整");
                }
                ascent = BASE_FONT.getFontDescriptor(BaseFont.ASCENT, fontSize);
                // 字体减小后的理想总长度
                textWidth = getPhraseAllChunkWidth(phrase, fontSize);
                // 字体减小后的文本框的最大行数
                boxMaxLine = Math.floor(textBoxHeight / (ascent * 1.25));
                // 字体减小后的理想行数
                line = Math.ceil(textWidth / textBoxWidth);
                // 字体减小后实际总长度
                totalTextWidth = (float) (textWidth + line * fontSize);
                // 实际行数
                realLine = Math.ceil(totalTextWidth / textBoxWidth);
            }
        }
        return fontSize;
    }
}
