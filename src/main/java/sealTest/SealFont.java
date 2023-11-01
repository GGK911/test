package sealTest;

import java.awt.*;

/**
 * 印章字体类
 */
public class SealFont {

    //字体内容
    private String fontText;

    //是否加粗
    private Integer isBold;

    //字形名，默认为宋体
    private String fontFamily;

    //字体大小
    private Integer fontSize;

    //字距
    private Double fontSpace;

    //边距（环边距或上边距）
    private Integer marginSize;

    public SealFont() {

    }

    public SealFont(String fontText) {
        this.fontText = fontText;
    }

    public static String[] getSupportFontNames() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    }

    public SealFont setFontSpace(Double fontSpace) {
        this.fontSpace = fontSpace;
        return this;
    }

    public SealFont setMarginSize(Integer marginSize) {
        this.marginSize = marginSize;
        return this;
    }

    public SealFont setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    public SealFont setFontText(String fontText) {
        this.fontText = fontText;
        return this;
    }

    public SealFont setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public SealFont setBold(Integer bold) {
        isBold = bold;
        return this;
    }

    public String getFontText() {
        return fontText;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public Double getFontSpace() {
        return fontSpace;
    }

    public Integer getMarginSize() {
        return marginSize;
    }

    public Integer isBold() {
        return isBold;
    }
}
