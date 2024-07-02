package pdfTest.signTest.strategy.signsuper;

import cn.com.mcsca.pdf.signature.PdfSign;

import java.util.List;
import java.util.Map;

public abstract class PdfSignSuper extends SignSuper {

    protected final String tsaUrl;

    public PdfSignSuper(String pubKey, String priKey, byte[] sealBytes, byte[] contractBytes, String tsaUrl) {
        super(pubKey, priKey, sealBytes, contractBytes);
        this.tsaUrl = tsaUrl;
    }

    public abstract List<float[]> analysisPositions();

    public byte[] signSuper(List<float[]> positions) {
        byte[] signFile;
        PdfSign pdfSign = new PdfSign();
        pdfSign.setTsaUrl(tsaUrl);
        pdfSign.setPublicCert(pubKey);
        
        // 自适应签署
        pdfSign.setSelfAdaption(true);
        
        try {
            signFile = pdfSign.doSignature(contractBytes, sealBytes, positions, priKey);
        } catch (Exception e) {
            logger.error("合同签署异常： 异常信息：{}", e);
            throw new RuntimeException("合同签署异常");
        }
        logger.info("pdf合同签署结束...");
        return signFile;
    }

    public byte[] signSuper(Map<byte[], List<float[]>> mulSealMulPosition) {
        byte[] signFile;
        PdfSign pdfSign = new PdfSign();
        pdfSign.setTsaUrl(tsaUrl);
        pdfSign.setPublicCert(pubKey);
        try {
            signFile = pdfSign.doSignature(contractBytes, mulSealMulPosition, priKey);
        } catch (Exception e) {
            logger.error("合同签署异常： 异常信息：{}", e);
            throw new RuntimeException("合同签署异常");
        }
        logger.info("pdf合同签署结束...");
        return signFile;
    }

}
