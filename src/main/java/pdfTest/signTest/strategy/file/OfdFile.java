package pdfTest.signTest.strategy.file;


import pdfTest.signTest.strategy.behavior.OfdCoordinateSignBehabior;
import pdfTest.signTest.strategy.behavior.OfdKeyWordSignBehavior;

public class OfdFile extends SignFile {

    public OfdFile(String signWay, String pubKey, String priKey, byte[] sealBytes,
                   byte[] contractBytes, String name, String coordinate, String keyWord, boolean isMerge) {
        if ("1".equals(signWay)) {
            signBehavior = new OfdCoordinateSignBehabior(pubKey, priKey, sealBytes,
                contractBytes, name, coordinate, isMerge);
        } else if ("2".equals(signWay)) {
            signBehavior = new OfdKeyWordSignBehavior(pubKey, priKey, sealBytes,
                contractBytes, name, keyWord, isMerge);
        } else {
            System.out.println("可拓展其他签署方式，如骑缝章，签署域");
        }
    }

    @Override
    public void disPlay() {
        System.out.println("ofd签署.....");
    }
}
