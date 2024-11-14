package pdfTest.parse;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/30 16:29
 */
public class BoxKeyPosition extends PDFTextStripper {

    private char[] key;
    private byte[] src;
    private List<float[]> list = new ArrayList<>();
    private List<float[]> pagelist = new ArrayList<>();

    public BoxKeyPosition(String keyWords, byte[] src) throws IOException {
        super();
        super.setSortByPosition(true);
        this.src = src;

        char[] key = new char[keyWords.length()];
        for (int i = 0; i < keyWords.length(); i++) {
            key[i] = keyWords.charAt(i);
        }
        this.key = key;
    }

    public char[] getKey() {
        return key;
    }

    public void setKey(char[] key) {
        this.key = key;
    }

    public byte[] getSrc() {
        return src;
    }

    public void setSrc(byte[] src) {
        this.src = src;
    }

    public List<float[]> getPosition() throws IOException {
        try {
            document = PDDocument.load(src);
            int pages = document.getNumberOfPages();

            for (int i = 1; i <= pages; i++) {
                pagelist.clear();
                super.setSortByPosition(true);
                super.setStartPage(i);
                super.setEndPage(i);
                Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
                super.writeText(document, dummy);
                for (float[] li : pagelist) {
                    li[2] = i;
                }
                list.addAll(pagelist);
            }
            return list;

        } finally {
            if (document != null) {
                document.close();
            }
        }

    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        for (int i = 0; i < textPositions.size(); i++) {

            String str = textPositions.get(i).getUnicode();
            if (str.equals(key[0] + "")) {
                int count = 0;
                for (int j = 1; j < key.length; j++) {
                    String s = "";
                    try {
                        s = textPositions.get(i + j).getUnicode();
                    } catch (Exception e) {
                        s = "";
                    }
                    if (s.equals(key[j] + "")) {
                        count++;
                    }

                }
                if (count == key.length - 1) {
                    float[] idx = new float[3];
                    idx[0] = textPositions.get(i).getX() + key.length * textPositions.get(i).getWidth() / 2;
                    idx[1] = textPositions.get(i).getY() - textPositions.get(i).getHeight();
                    //	idx[3] = textPositions.get(i).getUnicode();
                    pagelist.add(idx);
                }
            }

        }
    }
}
