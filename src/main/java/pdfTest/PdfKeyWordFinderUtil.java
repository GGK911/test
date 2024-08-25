package pdfTest;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.awt.geom.Rectangle2D.Float;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ContentByteUtils;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询PDF关键字坐标工具
 *
 * @author TangHaoKai
 * @version V1.0 2024/5/30 10:16
 */
public class PdfKeyWordFinderUtil {

    public static void main(String[] args) throws Exception {
        byte[] pdfBytes = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\aip填充.pdf");
        findKeywordPositions(pdfBytes, "测试");
    }

    public static List<float[]> findKeywordPositions(byte[] pdfData, String keyword) throws IOException {
        List<float[]> result = new ArrayList<>();
        List<PdfPageContentPositions> pdfPageContentPositions = getPdfContentPostionsList(pdfData);

        for (PdfPageContentPositions pdfPageContentPosition : pdfPageContentPositions) {
            List<float[]> charPositions = findPositions(keyword, pdfPageContentPosition, 0, 0);
            if (charPositions.isEmpty()) {
                continue;
            }
            result.addAll(charPositions);
        }
        return result;
    }

    public static List<float[]> findKeywordPositions(byte[] pdfData, String keyword, float width, float height) throws IOException {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("keyword can not be null or empty");
        }
        List<float[]> result = new ArrayList<>();
        List<PdfPageContentPositions> pdfPageContentPositions = getPdfContentPostionsList(pdfData);

        for (PdfPageContentPositions pdfPageContentPosition : pdfPageContentPositions) {
            List<float[]> charPositions = findPositions(keyword, pdfPageContentPosition, width, height);
            if (charPositions.isEmpty()) {
                continue;
            }
            result.addAll(charPositions);
        }
        return result;
    }


    private static List<PdfPageContentPositions> getPdfContentPostionsList(byte[] pdfData) throws IOException {
        PdfReader reader = new PdfReader(pdfData);

        List<PdfPageContentPositions> result = new ArrayList<>();

        int pages = reader.getNumberOfPages();
        for (int pageNum = 1; pageNum <= pages; pageNum++) {
            float width = reader.getPageSize(pageNum).getWidth();
            float height = reader.getPageSize(pageNum).getHeight();

            PdfRenderListener pdfRenderListener = new PdfRenderListener(pageNum);

            //解析pdf，定位位置
            PdfContentStreamProcessor processor = new PdfContentStreamProcessor(pdfRenderListener);
            PdfDictionary pageDic = reader.getPageN(pageNum);
            PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
            try {
                processor.processContent(ContentByteUtils.getContentBytesForPage(reader, pageNum), resourcesDic);
            } catch (IOException e) {
                reader.close();
                throw e;
            }

            String content = pdfRenderListener.getContent();
            List<CharPosition> charPositions = pdfRenderListener.getCharPositions();

            List<float[]> positionsList = new ArrayList<>();
            for (CharPosition charPosition : charPositions) {
                float[] positions = new float[]{charPosition.getPageNum(), charPosition.getLbx(), charPosition.getLby(), charPosition.getRtx(), charPosition.getRty()};
                positionsList.add(positions);
            }

            PdfPageContentPositions pdfPageContentPositions = new PdfPageContentPositions();
            pdfPageContentPositions.setWidth(width);
            pdfPageContentPositions.setHeight(height);
            PdfNumber pageRotate = pageDic.getAsNumber(PdfName.ROTATE);
            if (pageRotate != null) {
                pdfPageContentPositions.setRotate(pageRotate.doubleValue());
            }
            pdfPageContentPositions.setContent(content);
            pdfPageContentPositions.setPositions(positionsList);

            result.add(pdfPageContentPositions);
        }
        reader.close();
        return result;
    }

    private static List<float[]> findPositions(String keyword, PdfPageContentPositions pdfPageContentPositions, float w, float h) {
        List<float[]> result = new ArrayList<>();
        // 当前页宽度
        float width = pdfPageContentPositions.getWidth();
        String content = pdfPageContentPositions.getContent();
        List<float[]> charPositions = pdfPageContentPositions.getPositions();

        // 关键字个数
        int allPointCount = keyword.codePointCount(0, keyword.length());
        for (int pos = 0; pos < content.length(); ) {
            int positionIndex = content.indexOf(keyword, pos);
            if (positionIndex == -1) {
                break;
            }
            float[] positions = charPositions.get(positionIndex);
            // 中心点X
            float mx;
            // 中心点Y
            float my;
            if (pdfPageContentPositions.getRotate() == 0 || pdfPageContentPositions.getRotate() == 180) {
                // 关键字第一个字符的宽度乘以关键字个数加上自身X坐标即为关键字最后一个X坐标
                positions[3] = positions[1] + (positions[3] - positions[1]) * allPointCount;
                mx = positions[1] + (positions[3] - positions[1]) / 2;
                my = positions[2] + (positions[4] - positions[2]) / 2;
            } else if (pdfPageContentPositions.getRotate() == 90 || pdfPageContentPositions.getRotate() == 270) {
                // 关键字第一个字符的宽度乘以关键字个数加上自身Y坐标即为关键字最后一个Y坐标
                positions[4] = positions[2] + (positions[4] - positions[2]) * allPointCount;
                mx = positions[1] + (positions[3] - positions[1]) / 2;
                my = positions[2] + (positions[4] - positions[2]) / 2;
                // 因PDF为横向排版, 当前算出的关键字坐标的原点与签署时的坐标原点不是同一个, 所以需要相应转换
                float temp = mx;
                mx = my;
                my = width - temp;
            } else {
                throw new RuntimeException("unsupported rotation angles: " + pdfPageContentPositions.getRotate());
            }
            positions[1] = mx - w / 2;
            positions[2] = my - h / 2;
            positions[3] = mx + w / 2;
            positions[4] = my + h / 2;
            result.add(positions);
            pos = positionIndex + 1;
        }
        return result;
    }

    @Getter
    @Setter
    private static class PdfPageContentPositions {
        // 当前页宽度
        private float width;
        // 当前页高度
        private float height;
        // 当前页旋转角度
        private double rotate;
        private String content;
        private List<float[]> positions;
    }

    private static class PdfRenderListener implements RenderListener {
        private int pageNum;
        private StringBuilder contentBuilder = new StringBuilder();
        @Getter
        private List<CharPosition> charPositions = new ArrayList<>();

        public PdfRenderListener(int pageNum) {
            this.pageNum = pageNum;
        }

        @Override
        public void beginTextBlock() {
        }

        @Override
        public void renderText(TextRenderInfo renderInfo) {
            List<TextRenderInfo> characterRenderInfos = renderInfo.getCharacterRenderInfos();
            for (TextRenderInfo textRenderInfo : characterRenderInfos) {
                String word = textRenderInfo.getText();
                if (word.length() > 1) {
                    word = word.substring(word.length() - 1);
                }
                Float rectangle = textRenderInfo.getAscentLine().getBoundingRectange();
                Float rectBase = textRenderInfo.getBaseline().getBoundingRectange();

                float lbx = (float) rectBase.getMinX();
                float lby = (float) rectBase.getMinY();
                float rtx = (float) rectangle.getMaxX();
                float rty = (float) rectangle.getMaxY();

                CharPosition charPosition = new CharPosition(pageNum, lbx, lby, rtx, rty);
                charPositions.add(charPosition);
                contentBuilder.append(word);
            }
        }

        @Override
        public void endTextBlock() {
        }

        @Override
        public void renderImage(ImageRenderInfo renderInfo) {
        }

        public String getContent() {
            return contentBuilder.toString();
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class CharPosition {
        private int pageNum = 0;
        private float lbx = 0;
        private float lby = 0;
        private float rtx = 0;
        private float rty = 0;

        public CharPosition(int pageNum, float lbx, float lby) {
            this.pageNum = pageNum;
            this.lbx = lbx;
            this.lby = lby;
        }
    }
}
