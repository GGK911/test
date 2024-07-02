package pdfTest.signTest.strategy.file;


import pdfTest.signTest.strategy.behavior.PdfCoordinateSignBehabior;
import pdfTest.signTest.strategy.behavior.PdfKeyWordSignBehavior;
import pdfTest.signTest.strategy.behavior.PdfSignAreaSignBehavior;

import java.util.Map;

public class PdfFile extends SignFile {

    //签署方式 1为坐标 2为关键字 3为坐标域签署
    public PdfFile(String signWay, String pubKey, String priKey, byte[] sealBytes, byte[] contractBytes, String coordinate,
                   String keyWord, String tsaUrl, String textDomainParams, String signAreaKey, Map<String, Object> signPosition, long userType, byte[] timeSealBytes, String timeSealCoordinate) {
        if ("1".equals(signWay)) {
            signBehavior = new PdfCoordinateSignBehabior(pubKey, priKey, sealBytes, contractBytes, tsaUrl, coordinate, timeSealBytes, timeSealCoordinate);
        } else if ("2".equals(signWay)) {
            // 关键字签署分企业图章和个人签章
            signBehavior = new PdfKeyWordSignBehavior(pubKey, priKey, sealBytes, contractBytes, tsaUrl, keyWord, userType, timeSealBytes, timeSealCoordinate);
        } else if ("3".equals(signWay)) {
            signBehavior = new PdfSignAreaSignBehavior(pubKey, priKey, sealBytes, contractBytes, tsaUrl,
                textDomainParams, signAreaKey, signPosition, timeSealBytes, timeSealCoordinate);
        } else {
            System.out.println("可拓展其他签署方式，如骑缝章");
        }
    }


    @Override
    public void disPlay() {
        System.out.println("pdf签署...");
    }
}
