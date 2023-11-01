package sealTest;

/**
 * 印章配置类
 */
public class SealConfiguration {

    //主文字
    private SealFont mainFont;

    //副文字
    private SealFont viceFont;

    //抬头文字
    private SealFont titleFont;

    //中心文字
    private SealFont centerFont;

    //边线圆
    private SealCircle borderCircle;

    //内边线圆
    private SealCircle borderInnerCircle;

    //内环线圆
    private SealCircle innerCircle;

    public SealConfiguration setMainFont(SealFont mainFont) {
        this.mainFont = mainFont;
        return this;
    }

    public SealConfiguration setViceFont(SealFont viceFont) {
        this.viceFont = viceFont;
        return this;
    }

    public SealConfiguration setTitleFont(SealFont titleFont) {
        this.titleFont = titleFont;
        return this;
    }

    public SealConfiguration setCenterFont(SealFont centerFont) {
        this.centerFont = centerFont;
        return this;
    }

    public SealConfiguration setBorderCircle(SealCircle borderCircle) {
        this.borderCircle = borderCircle;
        return this;
    }

    public void setBorderInnerCircle(SealCircle borderInnerCircle) {
        this.borderInnerCircle = borderInnerCircle;
    }

    public void setInnerCircle(SealCircle innerCircle) {
        this.innerCircle = innerCircle;
    }

    public SealFont getMainFont() {
        return mainFont;
    }

    public SealFont getViceFont() {
        return viceFont;
    }

    public SealFont getTitleFont() {
        return titleFont;
    }

    public SealFont getCenterFont() {
        return centerFont;
    }

    public SealCircle getBorderCircle() {
        return borderCircle;
    }

    public SealCircle getBorderInnerCircle() {
        return borderInnerCircle;
    }

    public SealCircle getInnerCircle() {
        return innerCircle;
    }
}
