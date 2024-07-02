package pdfTest.signTest.strategy.behavior;

import cn.com.mcsca.pdf.signature.OfdSignBox;
import cn.hutool.core.codec.Base64;
import pdfTest.signTest.SignInfoUtil;
import pdfTest.signTest.strategy.signsuper.OfdSignSuper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OfdCoordinateSignBehabior extends OfdSignSuper implements SignBehavior {

    private final String coordinate;

    public OfdCoordinateSignBehabior(String pubKey, String priKey, byte[] sealBytes,
                                     byte[] contractBytes, String name, String coordinate, boolean isMerge) {
        super(pubKey, priKey, sealBytes, contractBytes, name, isMerge);
        this.coordinate = coordinate;
    }

    @Override
    public byte[] sign() {
        logger.info("ofd坐标签署开始...");

        super.initBeforeSign();
        byte[] signFile = contractBytes;
        List<float[]> positions = SignInfoUtil.analysisCoordinate(coordinate);
        for (float[] position : positions) {
            int page = (int) position[0];
            position = Arrays.copyOfRange(position, 1, position.length);
            try {
                float[] finalPosition = position;
                signFile = ofdSign.doSignature(signFile, Base64.decode(seSeal),
                    () -> {
                        List<OfdSignBox> boxes = new ArrayList<>();
                        boxes.add(new OfdSignBox(page, finalPosition[0], finalPosition[1], finalPosition[2], finalPosition[3]));
                        return boxes;
                    }, priKey);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
        return signFile;
    }

    @Override
    public String getSignParam() {
        return coordinate;
    }


}
