package pdfTest.verifyReportTest;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * 合同加强报告生成工具
 * thk's todo 生僻字暂不支持
 *
 * @author TangHaoKai
 * @version V1.0 2024/3/25 10:08
 */
public class AdEPactReportUtil {
    /**
     * 封面标题字体
     */
    private static Font coverTitleFont;
    /**
     * 封面副标题字体
     */
    private static Font coverSubTitleFont;
    /**
     * 封面文本字体
     */
    private static Font coverTextFont;
    /**
     * 标题字体
     */
    private static Font titleFont;
    /**
     * 副标题字体
     */
    private static Font subTitleFont;
    /**
     * 正文标准字体
     */
    private static Font textFontNormal;
    /**
     * 正文加粗字体
     */
    private static Font textFontBold;

    static {
        try {
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            coverTitleFont = new Font(bfChinese, 52, Font.BOLD);
            coverSubTitleFont = new Font(bfChinese, 20, Font.NORMAL);
            coverTextFont = new Font(bfChinese, 15, Font.NORMAL);

            titleFont = new Font(bfChinese, 17, Font.BOLD);
            subTitleFont = new Font(bfChinese, 14, Font.BOLD);
            textFontNormal = new Font(bfChinese, 12, Font.NORMAL);
            textFontBold = new Font(bfChinese, 12, Font.BOLD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成电子签名验证报告
     */
    public static byte[] genReport(ReportEntity reportEntity) throws Exception {
        java.util.List<byte[]> list = new ArrayList<>();
        list.add(generateCover(reportEntity));
        list.add(generateContent(reportEntity));
        return PdfUtil.mergePdfFiles(list);
    }

    /**
     * 生成封面
     */
    public static byte[] generateCover(ReportEntity reportEntity) throws Exception {
        // 创建文件接收
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 新建document对象
        Document document = new Document(PageSize.A4, 50.0F, 50.0F, 50.0F, 50.0F);
        PdfWriter.getInstance(document, out);
        document.open();
        //添加文档的基本信息
        addBaseInfo(document);

        Paragraph paragraph = new Paragraph("\n\n电子签名验证报告", coverTitleFont);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(100f);
        paragraph.setSpacingAfter(10f);
        document.add(paragraph);

        Paragraph paragraphEnglish = new Paragraph("Digital Certificate Signature Verification Report", coverSubTitleFont);
        paragraphEnglish.setAlignment(Element.ALIGN_CENTER);
        paragraphEnglish.setSpacingAfter(330f);
        document.add(paragraphEnglish);

        PdfPTable table = createTable(new float[]{95, 270}, 450);
        table.addCell(createCell("报告出具机构： ", coverTextFont, Element.ALIGN_RIGHT, 6.0f, 6.0f));
        table.addCell(createCell("大陆云盾电子认证服务有限公司", coverTextFont, Element.ALIGN_LEFT, 6.0f, 6.0f));
        table.addCell(createCell("报告申请机构： ", coverTextFont, Element.ALIGN_RIGHT, 6.0f, 6.0f));
        table.addCell(createCell(reportEntity.getOrgName(), coverTextFont, Element.ALIGN_LEFT, 6.0f, 6.0f));
        table.addCell(createCell("报告出具时间： ", coverTextFont, Element.ALIGN_RIGHT, 6.0f, 6.0f));
        table.addCell(createCell(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_PATTERN), coverTextFont, Element.ALIGN_LEFT, 6.0f, 6.0f));
        // thk's todo 2024/3/19 16:08 高度是否调整?
        table.addCell(createCell("报告验证编号： ", coverTextFont, Element.ALIGN_RIGHT, 6.0f, 6.0f));
        table.addCell(createCell(reportEntity.getEsvIndexId(), coverTextFont, Element.ALIGN_LEFT, 6.0f, 6.0f));
        document.add(table);

        document.close();
        return out.toByteArray();
    }

    /**
     * 生成报告内容
     */
    public static byte[] generateContent(ReportEntity reportEntity) throws Exception {
        // 创建文件接收
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 新建document对象
        Document document = new Document(PageSize.A4, 50.0F, 50.0F, 50.0F, 50.0F);
        PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
        //添加页眉页脚
        // 这是之前的
        // pdfWriter.setPageEvent(new HeaderFooter("大       陆       云       盾       电       子       认       证       服       务       有       限       公       司", 20, PageSize.A4));
        // 修改后的
        pdfWriter.setPageEvent(new HeaderFooter2(reportEntity.getEsvIndexId(), reportEntity.getFileName(), 20, PageSize.A4));
        // 打开文档
        document.open();
        //添加文档的基本信息
        addBaseInfo(document);

        //添加大陆云盾的描述
        String paragraphOne = "大陆云盾电子认证服务有限公司（简称：大陆云盾），是经中华人民共和国工业和信息化部批准的合法第三方电子认证服务机构，是国家重要的信息安全基础设施之一，具备国家密码管理局颁发的电子认证服务使用密码许可（证书编号：0053），以及工业和信息化部颁发的电子认证服务许可（许可证编号：ECP50010819050）。";
        addParagraph(document, paragraphOne);

        Phrase pg1 = new Phrase("大陆云盾本着独立、客观、公正的原则，根据《中华人民共和国电子签名法》、《电子认证服务管理办法》等相关法律规定出具电子签名验证报告，", textFontNormal);
        Phrase pg2 = new Phrase("本验证报告仅对数字证书签名结果的技术验证负责", textFontBold);
        Phrase pg3 = new Phrase("，不涉及对文件内容的效力判断。", textFontNormal);
        addParagraph(document, pg1, pg2, pg3);

        String titleOne = "一、基本情况";
        addTitle(document, titleOne, titleFont);

        List orderedList = new List(List.ORDERED);
        orderedList.setIndentationLeft(24f);
        orderedList.add(new ListItem(30f, "申请机构： " + reportEntity.getOrgName(), textFontNormal));
        // orderedList.add(new ListItem(30f, "验证编号： " + reportEntity.getEsvIndexId(), textFontNormal));
        orderedList.add(new ListItem(30f, "文档格式： " + reportEntity.getFileType(), textFontNormal));
        orderedList.add(new ListItem(30f, "文档名称： " + reportEntity.getFileName(), textFontNormal));
        orderedList.add(new ListItem(30f, "杂凑算法： " + reportEntity.getAlgType().toUpperCase(), textFontNormal));
        orderedList.add(new ListItem(30f, "杂凑值： " + reportEntity.getFileHash().toUpperCase(), textFontNormal));
        document.add(orderedList);

        String titleTwo = "二、验证说明";
        addTitle(document, titleTwo, titleFont);

        String paragraphThere = "根据" + reportEntity.getOrgName() + "提供的电子文件，经验证该文件中共有 " + reportEntity.getSignTotalNum() + "个 电子签名信息，详细信息如下。";
        addParagraph(document, paragraphThere);

        ArrayList<ReportEntity.ReportSignEntity> signInfoList = reportEntity.getSignInfoList();
        addSigInfo(document, signInfoList);

        String titleThree = "三、电子签名验证结论";
        addTitle(document, titleThree, titleFont);
        addConclusion(document, reportEntity);


        addOrg(document);
        // thk's todo 2024/3/19 16:17 因资质更新不添加附件
        document.newPage();
        // addAttachments(document);
        // 附录一
        addAppendix(document, reportEntity);


        document.close();
        return out.toByteArray();
    }

    /**
     * 添加pdf的基本信息
     *
     * @param document pdf文档
     */
    public static void addBaseInfo(Document document) {
        // 标题
        document.addTitle("电子签章验证报告");
        // 作者
        document.addAuthor("大陆云盾电子认证服务有限公司");
        // 主题
        document.addSubject("电子签章验证报告");
        // 关键字
        document.addKeywords("MCSCA、电子签名验签");
        // 创建者
        document.addCreator("大陆云盾电子认证服务有限公司");
    }


    /**
     * 添加报告的标题
     */
    private static void addTitle(Document document, String value, Font font) throws Exception {
        Paragraph paragraph = new Paragraph(value, font);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.setSpacingBefore(25f);
        paragraph.setSpacingAfter(2f);
        document.add(paragraph);
    }

    /**
     * 添加报告的副标题
     */
    private static void addSubTitle(Document document, String value, Font font) throws Exception {
        if (StrUtil.isEmpty(value)) return;
        Paragraph paragraph = new Paragraph(value, font);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.setSpacingBefore(10f);
        paragraph.setSpacingAfter(10f);
        document.add(paragraph);
    }

    /**
     * 添加正文
     */
    private static void addParagraph(Document document, String value) throws Exception {
        if (StrUtil.isEmpty(value)) return;
        Paragraph paragraph = new Paragraph(value, textFontNormal);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.setFirstLineIndent(24);
        paragraph.setLeading(27f);
        paragraph.setSpacingBefore(5f);
        paragraph.setSpacingAfter(5f);
        document.add(paragraph);
    }

    /**
     * 添加中文(可以自定义正文中任意一段样式)
     */
    private static void addParagraph(Document document, Phrase... phrase) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.setFirstLineIndent(24);
        paragraph.setLeading(27f);
        paragraph.setSpacingBefore(5f);
        paragraph.setSpacingAfter(5f);
        paragraph.addAll(Arrays.asList(phrase));
        document.add(paragraph);
    }

    /**
     * 添加加粗的正文
     */
    private static void addParagraphBold(Document document, String value) throws Exception {
        if (StrUtil.isEmpty(value)) return;
        Paragraph paragraph = new Paragraph(value, textFontBold);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.setFirstLineIndent(24);
        paragraph.setLeading(26f);
        paragraph.setSpacingBefore(5f);
        paragraph.setSpacingAfter(5f);
        document.add(paragraph);
    }

    /**
     * 添加报告的署名
     */
    private static void addOrg(Document document) throws Exception {
        Paragraph orgName = new Paragraph("\n大陆云盾电子认证服务有限公司", textFontNormal);
        orgName.setSpacingBefore(40f);
        orgName.setAlignment(Element.ALIGN_RIGHT);
        orgName.setIndentationRight(12);
        orgName.setSpacingAfter(5f);
        document.add(orgName);

        Paragraph time = new Paragraph(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_PATTERN), textFontNormal);
        time.setAlignment(Element.ALIGN_RIGHT);
        time.setIndentationRight(12);
        time.setSpacingBefore(5f);
        time.setSpacingAfter(5f);
        document.add(time);
    }

    /**
     * 添加附件信息
     */
    private static void addAttachments(Document document) throws Exception {
        String text = "附件一：大陆云盾电子认证服务有限公司相关资质";
        addTitle(document, text, subTitleFont);

        String text1 = "1.电子认证服务使用密码许可证";
        addTitle(document, text1, textFontNormal);
        Image image1 = Image.getInstance(IOUtils.toByteArray(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("static/images/pleas.jpg"))));
        image1.setAlignment(Image.ALIGN_CENTER);
        //依照比例缩放
        image1.scalePercent(50);
        document.add(image1);

        String text2 = "\n" + "2.电子认证服务许可";
        addTitle(document, text2, textFontNormal);
        Image image2 = Image.getInstance(IOUtils.toByteArray(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("static/images/easl.jpg"))));
        image2.setAlignment(Image.ALIGN_CENTER);
        //依照比例缩放
        image2.scalePercent(53);
        document.add(image2);

    }

    /**
     * 附录一（HASH）
     */
    private static void addAppendix(Document document, ReportEntity reportEntity) throws Exception {
        String text = "附录一：电子签名验证原始文档信息";
        addTitle(document, text, subTitleFont);
        PdfPTable table = new PdfPTable(2);
        // 表格前后
        table.setSpacingBefore(20);
        // table.setSpacingAfter(20);
        // 单个高度
        table.getDefaultCell().setFixedHeight(28);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 5});
        table.addCell(new Phrase("文档名称", textFontNormal));
        table.addCell(new Phrase(reportEntity.getFileName(), textFontNormal));
        table.addCell(new Phrase("杂凑算法", textFontNormal));
        table.addCell(new Phrase(reportEntity.getAlgType().toUpperCase(), textFontNormal));
        table.addCell(new Phrase("杂凑值", textFontNormal));
        table.addCell(new Phrase(reportEntity.getFileHash().toUpperCase(), textFontNormal));
        document.add(table);
        addParagraph(document, "使用杂凑算法对原始文档进行运算得到其杂凑值，可与上述杂凑值进行比对，进行文档一致性校验。");

    }

    /**
     * 添加签章的信息
     */
    private static void addSigInfo(Document document, ArrayList<ReportEntity.ReportSignEntity> signInfoList) throws Exception {
        CollectionUtil.reverse(signInfoList);
        for (int i = 0; i < signInfoList.size(); i++) {
            ReportEntity.ReportSignEntity reportSignEntity = signInfoList.get(i);
            addSubTitle(document, "第" + NumberChineseFormatter.format(i + 1, false, false) + "个电子签名信息：", subTitleFont);
            List orderedList = new List(List.ORDERED);
            orderedList.setIndentationLeft(24);
            orderedList.add(new ListItem(30f, "签名域名称： " + reportSignEntity.getSignatureName(), textFontNormal));
            orderedList.add(new ListItem(30f, "签名域位置： 位于文件第" + reportSignEntity.getSignaturePage() + "页", textFontNormal));
            orderedList.add(new ListItem(30f, "数字证书主题： " + reverseString(reportSignEntity.getSignCertSubject()), textFontNormal));
            orderedList.add(new ListItem(30f, "数字证书序列号： " + reportSignEntity.getSignCertSn(), textFontNormal));
            orderedList.add(new ListItem(30f, "数字证书有效期： " + reportSignEntity.getSignCertStartData() + "至" + reportSignEntity.getSignCertEndData(), textFontNormal));
            orderedList.add(new ListItem(30f, "数字证书颁发者： " + reverseString(reportSignEntity.getSignCertIssuer()), textFontNormal));
            // 合同的加强信息
            // 认证信息
            if (StrUtil.isNotEmpty(reportSignEntity.getAuthType())) {
                orderedList.add(new ListItem(30f, "所属用户认证类型： " + reportSignEntity.getAuthType(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户认证名称： " + reportSignEntity.getUserName(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户认证证件号码： " + reportSignEntity.getUserCardNo(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户认证电话： " + reportSignEntity.getUserPhone(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户认证短信验证码： " + reportSignEntity.getAuthSmsCode(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户认证短信发送时间： " + reportSignEntity.getAuthSmsSendTime(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户认证短信回填时间： " + reportSignEntity.getAuthSmsBackFillTime(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户认证结果： " + reportSignEntity.getAuthResult(), textFontNormal));
            }

            // 证书协议信息
            if (StrUtil.isNotEmpty(reportSignEntity.getReadProtocolEndTime())) {
                orderedList.add(new ListItem(30f, "所属用户订户协议勾选时间： " + reportSignEntity.getReadProtocolEndTime(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户订户协议内容杂凑值： " + reportSignEntity.getReadProtocolContent(), textFontNormal));
            }

            // 意愿信息
            if (StrUtil.isNotEmpty(reportSignEntity.getWillType())) {
                orderedList.add(new ListItem(30f, "所属用户意愿类型： " + reportSignEntity.getWillType(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户意愿时间： " + reportSignEntity.getWillTime(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户意愿短信验证码： " + reportSignEntity.getWillSmsCode(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户意愿短信发送时间： " + reportSignEntity.getWillSmsSendTime(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户意愿短信回填时间： " + reportSignEntity.getWillSmsBackFillTime(), textFontNormal));
                orderedList.add(new ListItem(30f, "所属用户意愿结果： " + reportSignEntity.getWillResult(), textFontNormal));
            }

            //添加签署时间验证
            Phrase signDateTitle = new Phrase("签署时间： " + reportSignEntity.getSignDate() + "，", textFontNormal);
            Phrase signDateText = new Phrase("签署时间" + (reportSignEntity.isSignDateVerifyBySignCert() ? "在" : "不在") + "数字证书有效期内。", textFontBold);
            ListItem signDateListItem = new ListItem(30f, "");
            signDateListItem.add(signDateTitle);
            signDateListItem.add(signDateText);
            orderedList.add(signDateListItem);

            //添加签章证书信任链验证
            Phrase signCertRootVerifyTitle = new Phrase("数字证书信任链： ", textFontNormal);
            Phrase signCertRootVerifyText = new Phrase(reportSignEntity.isSignCertVerifyRootCert() ? "数字证书信任链验证通过。" : "数字证书信任链验证未通过。", textFontBold);
            ListItem signCertRootVerifyListItem = new ListItem(30f, "");
            signCertRootVerifyListItem.add(signCertRootVerifyTitle);
            signCertRootVerifyListItem.add(signCertRootVerifyText);
            orderedList.add(signCertRootVerifyListItem);

            //添加签章证书crl验证,只有大陆云盾的证书才会验证crl
            if (reportSignEntity.isMcscaCert()) {
                Phrase signCertCrlVerifyTitle = new Phrase("数字证书吊销列表： ", textFontNormal);
                Phrase signCertCrlVerifyText = new Phrase(reportSignEntity.isSignCertVerifyCrl() ? "数字证书吊销列表验证通过。" : "数字证书吊销列表验证未通过。", textFontBold);
                ListItem signCertCrlVerifyListItem = new ListItem(30f, "");
                signCertCrlVerifyListItem.add(signCertCrlVerifyTitle);
                signCertCrlVerifyListItem.add(signCertCrlVerifyText);
                orderedList.add(signCertCrlVerifyListItem);
            }


            if (reportSignEntity.isContainTs()) {
                orderedList.add(new ListItem(30f, "时间戳证书主题： " + reverseString(reportSignEntity.getTsCertSubject()), textFontNormal));
                orderedList.add(new ListItem(30f, "时间戳证书序列号： " + reportSignEntity.getTsCertSn(), textFontNormal));
                orderedList.add(new ListItem(30f, "时间戳证书有效期： " + reportSignEntity.getTsCertStartData() + "至" + reportSignEntity.getTsCertEndData(), textFontNormal));
                orderedList.add(new ListItem(30f, "时间戳证书颁发者： " + reverseString(reportSignEntity.getTsCertIssuer()), textFontNormal));

                Phrase tsDateTitle = new Phrase("时间戳： " + reportSignEntity.getTsSignDate() + "，", textFontNormal);
                Phrase tsDateText = new Phrase("时间戳" + (reportSignEntity.getSignDateVerifyByTsCert() ? "在" : "不在") + "时间戳证书有效期内。", textFontBold);
                ListItem tsDateListItem = new ListItem(30f, "");
                tsDateListItem.add(tsDateTitle);
                tsDateListItem.add(tsDateText);
                orderedList.add(tsDateListItem);

                if (reportSignEntity.getMcscaTs()) {
                    orderedList.add(new ListItem(30f, "时间戳时间来源： " + "经验证该时间戳为大陆云盾时间戳 ，大陆云盾时间戳源自国家标准时间源。", textFontNormal));
                } else {
                    orderedList.add(new ListItem(30f, "时间戳时间来源： " + "经验证该时间戳非大陆云盾时间戳。", textFontNormal));
                }
            }
            orderedList.add(new ListItem(30f, "电子文档在该电子签名后" + (reportSignEntity.isModify() ? "已" : "未") + "被篡改。", textFontNormal));
            document.add(orderedList);
        }

    }

    /**
     * 添加结论
     */
    private static void addConclusion(Document document, ReportEntity reportEntity) throws Exception {
        List orderedList = new List(List.ORDERED);
        orderedList.setIndentationLeft(24f);
        if (reportEntity.isOnlyMcscaCert()) {
            orderedList.add(new ListItem(30f, "电子签名验证" + (reportEntity.isPass() ? "通过" : "不通过") + "。本次验证的电子文档中包含 " + reportEntity.getSignTotalNum() + " 个电子签名信息，" + (reportEntity.isPass() ? " 电子签名有效。" : reportEntity.getSignFalseNum() + " 个电子签名无效。"), textFontNormal));
        } else {
            orderedList.add(new ListItem(30f, reportEntity.isPass() ? "本次验证的电子文档中包含 " + reportEntity.getSignTotalNum() + " 个电子签名信息。" : "本次验证的电子文档中包含 " + reportEntity.getSignTotalNum() + " 个电子签名信息，" + reportEntity.getSignFalseNum() + " 个电子签名无效。", textFontNormal));
        }
        orderedList.add(new ListItem(30f, "该电子文档在电子签名后" + (reportEntity.isVerifyModify() ? "已" : "未") + "被篡改。", textFontNormal));
        document.add(orderedList);
    }

    /**
     * 创建指定列宽、列数的表格
     *
     * @param widths
     * @return
     */
    public static PdfPTable createTable(float[] widths, int width) {
        PdfPTable table = new PdfPTable(widths);
        try {
            table.setTotalWidth(width);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBorder(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * 创建单元格（指定字体、水平..）
     *
     * @param value
     * @param font
     * @param align
     * @return
     */
    public static PdfPCell createCell(String value, Font font, int align, float paddingTop, float paddingBottom) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        cell.setPadding(3f);
        cell.setBorder(0);
        cell.setLeading(18, 0);
        cell.setPaddingTop(paddingTop);
        cell.setPaddingBottom(paddingBottom);
        return cell;
    }

    /**
     * 证书主题和证书颁发者倒叙展示
     *
     * @param text 需要反转的字符串
     * @return 返回返回反转的数据
     */
    public static String reverseString(String text) {
        String[] split = text.split(",");
        ArrayUtil.reverse(split);
        String string = Arrays.toString(split);
        return string.substring(1, string.length() - 1);
    }

}
