package pdfTest.signTest.strategy.behavior;


import pdfTest.signTest.SignInfoUtil;
import pdfTest.signTest.strategy.signsuper.PdfSignSuper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfKeyWordSignBehavior extends PdfSignSuper implements SignBehavior {

    private final String keyWord;

    private long userType;

    private final byte[] timeSeal;

    private final String timeSealCoordinate;
    
    public PdfKeyWordSignBehavior(String pubKey, String priKey, byte[] sealBytes,
                                  byte[] contractBytes, String tsaUrl, String keyWord, long userType, byte[] timeSeal, String timeSealCoordinate) {
        super(pubKey, priKey, sealBytes, contractBytes, tsaUrl);
        this.keyWord = keyWord;
        this.userType = userType;
        this.timeSeal = timeSeal;
        this.timeSealCoordinate = timeSealCoordinate;
    }


    @Override
    public byte[] sign() {
        logger.info("pdf关键字签署开始...");
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
        return keyWord;
    }

    @Override
    public List<float[]> analysisPositions() {
        return SignInfoUtil.findByKeyWord(keyWord, contractBytes, userType);
    }
}
