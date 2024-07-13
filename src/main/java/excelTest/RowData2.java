package excelTest;

import java.io.Serializable;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/4 11:15
 */
public class RowData2 implements Serializable {
    private static final long serialVersionUID = 1L;
    private String colA;
    private String colB;
    private String colC;
    private String colD;
    private String colE;
    private String colF;

    // 构造函数
    public RowData2(String colA, String colB, String colC, String colD) {
        this.colA = colA;
        this.colB = colB;
        this.colC = colC;
        this.colD = colD;
    }

    public RowData2(String colA, String colB, String colC, String colD, String colE, String colF) {
        this.colA = colA;
        this.colB = colB;
        this.colC = colC;
        this.colD = colD;
        this.colE = colE;
        this.colF = colF;
    }

    // Getter和Setter方法
    public String getColA() { return colA; }
    public void setColA(String colA) { this.colA = colA; }

    public String getColB() { return colB; }
    public void setColB(String colB) { this.colB = colB; }

    public String getColC() { return colC; }
    public void setColC(String colC) { this.colC = colC; }

    public String getColD() { return colD; }
    public void setColD(String colD) { this.colD = colD; }

    public String getColE() {
        return colE;
    }

    public void setColE(String colE) {
        this.colE = colE;
    }

    public String getColF() {
        return colF;
    }

    public void setColF(String colF) {
        this.colF = colF;
    }

    @Override
    public String toString() {
        return "RowData2{" +
                "colA='" + colA + '\'' +
                ", colB='" + colB + '\'' +
                ", colC='" + colC + '\'' +
                ", colD='" + colD + '\'' +
                ", colE='" + colE + '\'' +
                ", colF='" + colF + '\'' +
                '}';
    }
}
