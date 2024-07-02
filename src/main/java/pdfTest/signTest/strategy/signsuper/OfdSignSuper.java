package pdfTest.signTest.strategy.signsuper;

import cn.com.mcsca.pdf.signature.OfdSign;
import pdfTest.signTest.SignInfoUtil;

import java.util.ArrayList;
import java.util.List;

public class OfdSignSuper extends SignSuper {

    protected final String name;
    protected OfdSign ofdSign;
    protected String seSeal;
    protected boolean isMerge;

    public OfdSignSuper(String pubKey, String priKey, byte[] sealBytes, byte[] contractBytes, String name, boolean isMerge) {
        super(pubKey, priKey, sealBytes, contractBytes);
        this.name = name;
        this.isMerge = isMerge;
    }

    public void initBeforeSign() {
        ofdSign = new OfdSign();
        ofdSign.setPublicCert(pubKey);
        if (isMerge) {
            ofdSign.setMerge(isMerge);
            //设置只有page目录下文件受保护
            //ofdSign.setSignFileRegx("^/Doc_0/Pages/.+$");
            //ofdSign.setSignFileRegx("^(/Doc_0/Pages/.+)|(/Doc_0/Tags/CustomTags.xml)$");
            //设置不被保护的文件
            List<String> ignoreSignFiles = new ArrayList<>();
            ignoreSignFiles.add("/Doc_0/Tags/CustomTags.xml");
            ignoreSignFiles.add("/Doc_0/Document.xml");
            ignoreSignFiles.add("/Doc_0/PublicRes.xml");
            ignoreSignFiles.add("/Doc_0/DocumentRes.xml");
            ofdSign.setIgnoreSignFiles(ignoreSignFiles);
        }
        seSeal = SignInfoUtil.createSESeal(sealBytes, pubKey, priKey, name);
    }
}
