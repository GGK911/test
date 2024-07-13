package excelTest;

import java.io.Serializable;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/4 11:15
 */
public class RowData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String colA;
    private String colB;
    private String colC;
    private String colD;

    // 构造函数
    public RowData(String colA, String colB, String colC, String colD) {
        this.colA = colA;
        this.colB = colB;
        this.colC = colC;
        this.colD = colD;
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

    @Override
    public String toString() {
        return "RowData{" +
                "colA='" + colA + '\'' +
                ", colB='" + colB + '\'' +
                ", colC='" + colC + '\'' +
                ", colD='" + colD + '\'' +
                '}';
    }
}
