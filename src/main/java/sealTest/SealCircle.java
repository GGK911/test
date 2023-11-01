package sealTest;

/**
 * 印章圆圈类
 */
public class SealCircle {

    public SealCircle(Integer lineSize, Integer width, Integer height) {
        this.lineSize = lineSize;
        this.width = width;
        this.height = height;
    }
    //线宽
    private Integer lineSize;

    //半径
    private Integer width;

    //半径
    private Integer height;

    public Integer getLineSize() {
        return lineSize;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }
}
