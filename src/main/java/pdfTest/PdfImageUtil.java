package pdfTest;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PDF图片工具
 *
 * @author TangHaoKai
 * @version V1.0 2024/5/29 16:52
 */
public class PdfImageUtil {

    /**
     * 单图片多位置
     *
     * @param pdfBytes     PDF文件
     * @param imageBytes   图片
     * @param positionList 位置
     * @return 添加图片后PDF
     */
    @SneakyThrows
    public static byte[] addImage(byte[] pdfBytes, byte[] imageBytes, List<float[]> positionList) {
        PdfReader pdfReader = new PdfReader(pdfBytes);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfStamper pdfStamper = new PdfStamper(pdfReader, bos);
        for (float[] position : positionList) {
            addImage(pdfStamper, imageBytes, position);
        }
        pdfStamper.close();
        return bos.toByteArray();
    }

    /**
     * 多图片，图片多位置
     *
     * @param pdfBytes           PDF文件
     * @param mulSealMulPosition 多图章，图章多位置Map<图片，多位置>
     * @return 添加图片后PDF
     */
    @SneakyThrows
    public static byte[] addImage(byte[] pdfBytes, Map<byte[], List<float[]>> mulSealMulPosition) {
        PdfReader pdfReader = new PdfReader(pdfBytes);
        return addImage(pdfReader, mulSealMulPosition);
    }

    /**
     * 多图片，图片多位置
     *
     * @param pdfReader          PDF对象
     * @param mulSealMulPosition 多图章，图章多位置Map<图片，多位置>
     * @return 添加图片后PDF
     */
    @SneakyThrows
    private static byte[] addImage(PdfReader pdfReader, Map<byte[], List<float[]>> mulSealMulPosition) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfStamper pdfStamper = new PdfStamper(pdfReader, bos);
        for (Map.Entry<byte[], List<float[]>> entry : mulSealMulPosition.entrySet()) {
            byte[] imageBytes = entry.getKey();
            for (float[] position : entry.getValue()) {
                addImage(pdfStamper, imageBytes, position);
            }
        }
        pdfStamper.close();
        pdfReader.close();
        return bos.toByteArray();
    }

    /**
     * 骑缝章图片
     *
     * @param pdfBytes   PDF文件
     * @param imageBytes 图片
     * @param position   图片位置
     * @param pageRange  页范围
     * @param direction  方向
     * @return 添加图片后PDF
     */
    @SneakyThrows
    public static byte[] addImage(byte[] pdfBytes, byte[] imageBytes, float[] position, String pageRange, String direction) {
        PdfReader pdfReader = new PdfReader(pdfBytes);
        BufferedImage sealImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        List<float[]> positionList = checkSealPositionList(position, pdfReader, pageRange, direction, sealImage.getWidth(), sealImage.getHeight(), false);
        List<BufferedImage> subSealList;
        try {
            subSealList = cutSeal(imageBytes, direction, positionList.size());
            // 如果是左/上骑缝章,图片应该是倒着的
            if ("L".equals(direction) || "T".equals(direction)) {
                Collections.reverse(subSealList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("切分图片时错误");
        }
        Map<byte[], List<float[]>> mulSealMulPosition = new HashMap<>();
        for (int i = 0; i < positionList.size(); i++) {
            float[] subPosition = positionList.get(i);
            byte[] subPicBytes;
            try (ByteArrayOutputStream subSealBytes = new ByteArrayOutputStream()) {
                final int pageRotation = pdfReader.getPageRotation((int) subPosition[0]);

                BufferedImage subBufferedImage = subSealList.get(i);
                // 这里如果是横着的页面，图片显示会有问题，跟FieldSign不一样；如果是横着的图片要将图片缩放下
                if (pageRotation == 90 || pageRotation == 270) {
                    subBufferedImage = imageStretch(subBufferedImage, subBufferedImage.getHeight(), subBufferedImage.getWidth());
                }
                ImageIO.write(subBufferedImage, "PNG", subSealBytes);
                subPicBytes = subSealBytes.toByteArray();
                List<float[]> subPositionList = new ArrayList<>();
                subPositionList.add(subPosition);
                mulSealMulPosition.put(subPicBytes, subPositionList);
            }
        }
        return addImage(pdfReader, mulSealMulPosition);
    }

    /**
     * 切印章图片
     *
     * @param sealBytes 印章图片
     * @param direction 签名方向
     * @param size      份数
     * @return 切好的印章图片集合
     */
    public static List<BufferedImage> cutSeal(byte[] sealBytes, String direction, int size) throws IOException {
        BufferedImage sealImage = ImageIO.read(new ByteArrayInputStream(sealBytes));
        List<BufferedImage> subSealList = new ArrayList<>();
        if (size == 1) {
            subSealList.add(sealImage);
            return subSealList;
        }
        int width = sealImage.getWidth();
        int height = sealImage.getHeight();
        if ("H".equals(direction) || "T".equals(direction) || "B".equals(direction)) {
            int h = height / size;
            for (int i = 0; i < size; i++) {
                if (i == size - 1) {
                    subSealList.add(sealImage.getSubimage(0, i * h, width, height - i * h));
                } else {
                    subSealList.add(sealImage.getSubimage(0, i * h, width, h));
                }
            }
        } else {
            int w = width / size;
            for (int i = 0; i < size; i++) {
                if (i == size - 1) {
                    subSealList.add(sealImage.getSubimage(i * w, 0, width - i * w, height));
                } else {
                    subSealList.add(sealImage.getSubimage(i * w, 0, w, height));
                }
            }
        }
        return subSealList;
    }

    /**
     * 校验签署位置
     *
     * @param position                签署位置
     * @param pdfReader               pdf对象
     * @param pageRange               骑缝章签署范围 1-?
     * @param direction               骑缝章:V垂直|H平行
     * @param sealWidth               图章宽
     * @param sealHeight              图章高
     * @param ridSignAngleAbsPosition 关于骑缝章，在有旋转角度的页面，章依旧盖在固定位置，还是按竖着的位置来调整:为true时，依旧按固定坐标来盖章/为false时，自动调整为竖着的坐标
     */
    private static List<float[]> checkSealPositionList(float[] position, PdfReader pdfReader, String pageRange, String direction, int sealWidth, int sealHeight, boolean ridSignAngleAbsPosition) {
        // 总页数
        final int pages = pdfReader.getNumberOfPages();
        // 骑缝章
        final float[] floats = position;
        // 要盖章的位置
        List<Integer> signPageList = parsePageRange(pageRange, pages);
        // 构建坐标
        List<float[]> convertPositionList = new ArrayList<>(signPageList.size());
        for (int i = 0; i < signPageList.size(); i++) {
            Rectangle pageRect;
            Rectangle totalPageRec = pdfReader.getPageSize(signPageList.get(i));
            final int pageRotation = pdfReader.getPageRotation(signPageList.get(i));
            if (ridSignAngleAbsPosition) {
                // 章依旧盖在固定位置(也就是页面旋转多少度，这个框就旋转多少度)
                if (pageRotation == 90 || pageRotation == 270) {
                    totalPageRec = totalPageRec.rotate();
                }
                pageRect = getPageRect(floats, direction, signPageList.size(), sealWidth, sealHeight, totalPageRec.getWidth(), totalPageRec.getHeight());
            } else {
                pageRect = getPageRect(floats, direction, signPageList.size(), sealWidth, sealHeight, totalPageRec.getWidth(), totalPageRec.getHeight());
                float height = pageRect.getHeight();
                float width = pageRect.getWidth();
                float left = pageRect.getLeft();
                float bottom = pageRect.getBottom();
                float right = pageRect.getRight();
                float top = pageRect.getTop();
                // 按竖着的位置来调整(这里要手动调整坐标)
                if ("R".equalsIgnoreCase(direction) || "V".equalsIgnoreCase(direction)) {
                    if (pageRotation == 90) {
                        pageRect.setLeft(bottom);
                        pageRect.setBottom(0);
                        pageRect.setRight(bottom + height);
                        pageRect.setTop(width);
                    } else if (pageRotation == 180) {
                        pageRect.setLeft(0);
                        pageRect.setBottom(totalPageRec.getTop() - top);
                        pageRect.setRight(width);
                        pageRect.setTop(totalPageRec.getTop() - top + height);
                    } else if (pageRotation == 270) {
                        pageRect.setLeft(totalPageRec.getTop() - top);
                        pageRect.setBottom(totalPageRec.getRight() - width);
                        pageRect.setRight(totalPageRec.getTop() - top + height);
                        pageRect.setTop(totalPageRec.getRight());
                    }
                } else if ("B".equalsIgnoreCase(direction) || "H".equalsIgnoreCase(direction)) {
                    if (pageRotation == 90) {
                        pageRect.setLeft(0);
                        pageRect.setBottom(totalPageRec.getRight() - left - width);
                        pageRect.setRight(height);
                        pageRect.setTop(totalPageRec.getRight() - left - width + width);
                    } else if (pageRotation == 180) {
                        pageRect.setLeft(totalPageRec.getRight() - right);
                        pageRect.setBottom(totalPageRec.getTop() - height);
                        pageRect.setRight(totalPageRec.getRight() - right + width);
                        pageRect.setTop(totalPageRec.getTop());
                    } else if (pageRotation == 270) {
                        pageRect.setLeft(totalPageRec.getTop() - height);
                        pageRect.setBottom(left);
                        pageRect.setRight(totalPageRec.getTop());
                        pageRect.setTop(left + width);
                    }
                } else if ("L".equalsIgnoreCase(direction)) {
                    if (pageRotation == 90) {
                        pageRect.setLeft(bottom);
                        pageRect.setBottom(totalPageRec.getRight() - width);
                        pageRect.setRight(bottom + height);
                        pageRect.setTop(totalPageRec.getRight());
                    } else if (pageRotation == 180) {
                        pageRect.setLeft(totalPageRec.getRight() - width);
                        pageRect.setBottom(totalPageRec.getTop() - top);
                        pageRect.setRight(totalPageRec.getRight());
                        pageRect.setTop(totalPageRec.getTop() - top + height);
                    } else if (pageRotation == 270) {
                        pageRect.setLeft(totalPageRec.getTop() - top);
                        pageRect.setBottom(0);
                        pageRect.setRight(totalPageRec.getTop() - top + height);
                        pageRect.setTop(width);
                    }
                } else if ("T".equalsIgnoreCase(direction)) {
                    if (pageRotation == 90) {
                        pageRect.setLeft(totalPageRec.getTop() - height);
                        pageRect.setBottom(totalPageRec.getRight() - right);
                        pageRect.setRight(totalPageRec.getTop());
                        pageRect.setTop(totalPageRec.getRight() - right + width);
                    } else if (pageRotation == 180) {
                        pageRect.setLeft(totalPageRec.getRight() - right);
                        pageRect.setBottom(0);
                        pageRect.setRight(totalPageRec.getRight() - right + width);
                        pageRect.setTop(height);
                    } else if (pageRotation == 270) {
                        pageRect.setLeft(0);
                        pageRect.setBottom(left);
                        pageRect.setRight(height);
                        pageRect.setTop(right);
                    }
                }
            }
            convertPositionList.add(new float[]{signPageList.get(i), pageRect.getLeft(), pageRect.getBottom(), pageRect.getRight(), pageRect.getTop()});
        }
        return convertPositionList;
    }

    /**
     * 解析签名页范围
     *
     * @param pageRange 签名页范围
     * @param totalPage 总页数
     * @return 签名页集合
     */
    public static List<Integer> parsePageRange(String pageRange, int totalPage) {
        List<Integer> pageList = new ArrayList<>();
        if (pageRange == null || "".equals(pageRange.trim())) {
            // throw new IllegalArgumentException("the parameter pageRange cannot be empty");
            pageRange = "1-";
        }
        pageRange = pageRange.replaceAll(" ", "");
        String[] pageRangeArr = pageRange.split(",");
        for (String pageRangeItem : pageRangeArr) {
            if ("".equals(pageRangeItem)) {
                continue;
            }
            if (pageRangeItem.matches("^\\d+$")) {
                addPageNumber(pageList, Integer.parseInt(pageRangeItem), totalPage);
            } else if (pageRangeItem.matches("^\\d+(-)\\d+$")) {
                String[] startAndEnd = pageRangeItem.split("-");
                int start = Integer.parseInt(startAndEnd[0]);
                int end = Integer.parseInt(startAndEnd[1]);
                for (int i = start; i <= end; i++) {
                    addPageNumber(pageList, i, totalPage);
                }
            } else if (pageRangeItem.matches("^\\d+(-)$")) {
                int start = Integer.parseInt(pageRangeItem.split("-")[0]);
                for (int i = start; i <= totalPage; i++) {
                    addPageNumber(pageList, i, totalPage);
                }
            } else {
                throw new IllegalArgumentException("the parameter pageRange is a invalid parameter");
            }
        }
        if (pageList.isEmpty()) {
            throw new IllegalArgumentException("the parameter pageRange is a invalid parameter");
        }
        // 排序
        Collections.sort(pageList);
        return pageList;
    }

    /**
     * 添加待签名页到签名页集合
     *
     * @param pageList  签名页集合
     * @param page      待签名页
     * @param totalPage 总页数
     */
    private static void addPageNumber(List<Integer> pageList, int page, int totalPage) {
        if (page <= 0) {
            throw new IllegalArgumentException("骑缝章指定范围小于0");
        }
        if (page > totalPage) {
            throw new IllegalArgumentException("骑缝章指定范围大于总页数");
        }
        if (pageList.contains(page)) {
            return;
        }
        pageList.add(page);
    }

    /**
     * 解析双页双开与骑缝章签名坐标
     *
     * @param position   签名坐标
     * @param direction  签名方向
     * @param number     份数
     * @param sealWidth  印章宽
     * @param sealHeight 印章高
     * @param pdfWidth   页面宽
     * @param pdfHeight  页面高
     * @return 签名坐标矩阵
     */
    public static Rectangle getPageRect(float[] position, String direction, int number, int sealWidth, int sealHeight, float pdfWidth, float pdfHeight) {
        Rectangle pageRect;
        if (position.length == 1) {
            float center = position[0];
            if ("H".equals(direction) || "B".equals(direction)) {
                float llx = center - sealWidth / 2.0F;
                float lly = 0;
                float urx = llx + sealWidth;
                float ury = sealHeight / number;
                pageRect = new Rectangle(llx, lly, urx, ury);
            } else if ("T".equals(direction)) {
                float llx = center - sealWidth / 2.0F;
                float lly = pdfHeight - sealHeight / number;
                float urx = llx + sealWidth;
                float ury = pdfHeight;
                pageRect = new Rectangle(llx, lly, urx, ury);
            } else if ("L".equals(direction)) {
                float llx = 0;
                float lly = center - sealHeight / 2.0F;
                float urx = sealWidth / number;
                float ury = lly + sealHeight;
                pageRect = new Rectangle(llx, lly, urx, ury);
            } else {
                // 默认
                float llx = pdfWidth - sealWidth / number;
                float lly = center - sealHeight / 2.0F;
                float urx = pdfWidth;
                float ury = lly + sealHeight;
                pageRect = new Rectangle(llx, lly, urx, ury);
            }
        } else if (position.length == 2) {
            if (position[0] > position[1]) {
                Arrays.sort(position);
            }
            float centerStart = position[0];
            float centerEnd = position[1];
            float diameter = centerEnd - centerStart;
            if ("H".equals(direction) || "B".equals(direction)) {
                float scale = diameter / sealWidth;
                float scaleSealHeight = sealHeight * scale;
                float llx = centerStart;
                float lly = 0;
                float urx = centerEnd;
                float ury = scaleSealHeight / number;
                pageRect = new Rectangle(llx, lly, urx, ury);
            } else if ("T".equals(direction)) {
                float llx = centerStart;
                float lly = pdfHeight - sealHeight / number;
                float urx = centerEnd;
                float ury = pdfHeight;
                pageRect = new Rectangle(llx, lly, urx, ury);
            } else if ("L".equals(direction)) {
                float llx = 0;
                float lly = centerStart;
                float urx = sealWidth / number;
                float ury = centerEnd;
                pageRect = new Rectangle(llx, lly, urx, ury);
            } else {
                // 默认
                float scale = diameter / sealHeight;
                float scaleSealWidth = sealWidth * scale;
                float llx = pdfWidth - scaleSealWidth / number;
                float lly = centerStart;
                float urx = pdfWidth;
                float ury = centerEnd;
                pageRect = new Rectangle(llx, lly, urx, ury);
            }
        } else {
            throw new IllegalArgumentException("the parameter position length is not supported");
        }

        // 判断签名域矩阵是否越界
        if (judgePageRect(pageRect, pdfWidth, pdfHeight)) {
            throw new RuntimeException("the signature rectangle out of document margin");
        }

        return pageRect;
    }

    /**
     * 判断签名矩阵是否越界 当 selfAdaption = true 时, 在超出边界后将自动调整
     *
     * @param pageRect  签名矩阵
     * @param pdfWidth  文档宽度
     * @param pdfHeight 文档高度
     * @return 是否越界
     */
    public static boolean judgePageRect(Rectangle pageRect, float pdfWidth, float pdfHeight) {
        float[] positions = {0, pageRect.getLeft(), pageRect.getBottom(), pageRect.getRight(), pageRect.getTop()};
        return judgePageRect(positions, pdfWidth, pdfHeight);
    }

    /**
     * 判断签名矩阵是否越界
     *
     * @param positions 签名位置
     * @param pdfWidth  文档宽度
     * @param pdfHeight 文档高度
     * @return 是否越界
     */
    private static boolean judgePageRect(float[] positions, float pdfWidth, float pdfHeight) {
        float llx = positions[1];
        float lly = positions[2];
        float urx = positions[3];
        float ury = positions[4];
        if (llx < 0 || lly < 0 || urx < 0 || ury < 0) {
            return true;
        }
        return llx > pdfWidth || lly > pdfHeight || urx > pdfWidth || ury > pdfHeight;
    }

    /**
     * 基础添加图片方法
     *
     * @param pdfStamper PDF对象
     * @param imageBytes 图片
     * @param position   位置
     */
    @SneakyThrows
    private static void addImage(PdfStamper pdfStamper, byte[] imageBytes, float[] position) {
        try (ByteArrayOutputStream subSealBytes = new ByteArrayOutputStream()) {
            Rectangle rectangle = new Rectangle(position[1], position[2], position[3], position[4]);
            float height = rectangle.getHeight();
            float width = rectangle.getWidth();
            BufferedImage sealImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            sealImage = imageStretch(sealImage, (int) width, (int) height);
            ImageIO.write(sealImage, "PNG", subSealBytes);
            imageBytes = subSealBytes.toByteArray();

            Image image = Image.getInstance(imageBytes);
            image.setAbsolutePosition(position[1], position[2]);
            PdfContentByte overContent = pdfStamper.getOverContent((int) position[0]);
            overContent.addImage(image);
        }
    }

    /**
     * 拉伸图像
     * +---------+
     * | +-----+ |
     * | |  A  | |
     * | +-----+ |
     * +---------+
     *
     * @param bufferedImageOriginal 原始图像
     * @param width                 拉伸后宽
     * @param height                拉伸后高
     * @return 图像
     */
    public static BufferedImage imageStretch(BufferedImage bufferedImageOriginal, int width, int height) {
        // 画布
        BufferedImage bufferedImageAfter = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        // 画笔
        Graphics2D graphics = bufferedImageAfter.createGraphics();
        // 抗锯齿设置
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 画字
        graphics.drawImage(bufferedImageOriginal, 0, 0, width, height, null);
        // 释放
        graphics.dispose();
        return bufferedImageAfter;
    }

}
