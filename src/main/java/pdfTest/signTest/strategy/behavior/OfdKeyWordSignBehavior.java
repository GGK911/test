package pdfTest.signTest.strategy.behavior;

import cn.hutool.core.codec.Base64;
import pdfTest.signTest.strategy.signsuper.OfdSignSuper;

public class OfdKeyWordSignBehavior extends OfdSignSuper implements SignBehavior {

    private final String keyWord;

    public OfdKeyWordSignBehavior(String pubKey, String priKey, byte[] sealBytes,
                                  byte[] contractBytes, String name, String keyWord,boolean isMerge) {
        super(pubKey, priKey, sealBytes, contractBytes, name,isMerge);
        this.keyWord = keyWord;
    }


    @Override
    public byte[] sign() {
        logger.info("ofd关键字签署开始...");

        super.initBeforeSign();
        float[] size = {50F, 50F, -1};
        try {
            return ofdSign.doSignature(contractBytes, Base64.decode(seSeal),
                keyWord, size, priKey);
        } catch (Exception e) {
            if (e.getMessage().contains("not find in the document")) {
                throw new RuntimeException();
            } else {
                throw new RuntimeException();
            }

        }

    }

    @Override
    public String getSignParam() {
        return keyWord;
    }

}
