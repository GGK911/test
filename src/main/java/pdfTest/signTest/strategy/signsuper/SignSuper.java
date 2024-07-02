package pdfTest.signTest.strategy.signsuper;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

public class SignSuper {
    protected final Log logger = LogFactory.get();

    protected final String pubKey;
    protected final String priKey;
    protected final byte[] sealBytes;
    protected final byte[] contractBytes;

    public SignSuper(String pubKey, String priKey, byte[] sealBytes, byte[] contractBytes){
        this.pubKey = pubKey;
        this.priKey = priKey;
        this.sealBytes = sealBytes;
        this.contractBytes = contractBytes;
    }
}
