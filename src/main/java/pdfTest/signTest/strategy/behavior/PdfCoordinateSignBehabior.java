package pdfTest.signTest.strategy.behavior;


import pdfTest.signTest.SignInfoUtil;
import pdfTest.signTest.strategy.signsuper.PdfSignSuper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PdfCoordinateSignBehabior extends PdfSignSuper implements SignBehavior {

    private final String coordinate;

    private final byte[] timeSeal;

    private final String timeSealCoordinate;


    public PdfCoordinateSignBehabior(String pubKey, String priKey, byte[] sealBytes,
                                     byte[] contractBytes, String tsaUrl, String coordinate, byte[] timeSeal, String timeSealCoordinate) {
        super(pubKey, priKey, sealBytes, contractBytes, tsaUrl);
        this.coordinate = coordinate;
        this.timeSeal = timeSeal;
        this.timeSealCoordinate = timeSealCoordinate;
    }

    @Override
    public byte[] sign() {
        logger.info("pdf坐标签署开始...");
        if (timeSeal == null) {
            // 无时间章
            return super.signSuper(analysisPositions());
        } else {
            // 有时间章
            List<float[]> positions = analysisPositions();
            Map<byte[], List<float[]>> mulSealMulPosition = new HashMap<>();
            mulSealMulPosition.put(sealBytes, positions);
            List<float[]> timeSealPosition = SignInfoUtil.analysisCoordinate(timeSealCoordinate);
            mulSealMulPosition.put(timeSeal, timeSealPosition);
            return super.signSuper(mulSealMulPosition);
        }
    }

    @Override
    public String getSignParam() {
        return coordinate;
    }


    @Override
    public List<float[]> analysisPositions() {
        return SignInfoUtil.analysisCoordinate(coordinate);
    }
}
