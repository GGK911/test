package pdfTest.signTest.strategy.behavior;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import pdfTest.signTest.SignInfoUtil;
import pdfTest.signTest.strategy.signsuper.PdfSignSuper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfSignAreaSignBehavior extends PdfSignSuper implements SignBehavior {

    private final String textDomainParams;

    private final String signAreaKey;

    private final Map<String, Object> signPosition;

    private final byte[] timeSeal;

    private final String timeSealCoordinate;

    public PdfSignAreaSignBehavior(String pubKey, String priKey, byte[] sealBytes, byte[] contractBytes, String tsaUrl,
                                   String textDomainParams, String signAreaKey, Map<String, Object> signPosition, byte[] timeSeal, String timeSealCoordinate) {
        super(pubKey, priKey, sealBytes, contractBytes, tsaUrl);
        this.textDomainParams = textDomainParams;
        this.signAreaKey = signAreaKey;
        this.signPosition = signPosition;
        this.timeSeal = timeSeal;
        this.timeSealCoordinate = timeSealCoordinate;
    }

    @Override
    public byte[] sign() {
        logger.info("pdf签署域签署开始...");
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
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.put("textDomainParams", textDomainParams);
        jsonObject.put("signAreaKey", signAreaKey);
        return JSONUtil.toJsonStr(jsonObject);
    }

    @Override
    public List<float[]> analysisPositions() {
        return SignInfoUtil.findBySignArea(signAreaKey, signPosition);
    }

}
