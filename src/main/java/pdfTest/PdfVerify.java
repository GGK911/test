package pdfTest;


import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TangHaoKai
 * @version V1.0 2024/1/18 12:23
 **/
public class PdfVerify {

    @Test
    public void test() {
        Security.addProvider(new BouncyCastleProvider());
        // byte[] pdf = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\个人信息授权书(2).pdf");
        // byte[] pdf = FileUtil.readBytes("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\createAnnot.pdf");
        byte[] pdf = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\test01-1.pdf");
        // byte[] pdf = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\cfca.pdf");
        // byte[] pdf = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\replace.pdf");
        // byte[] pdf = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\test01.pdf");
        try {
            List<VerifyResult> verifyResults = verify(pdf, null);
            for (VerifyResult result : verifyResults) {
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<VerifyResult> verify(byte[] pdfBytes, String pwd) throws Exception {
        List<VerifyResult> resultList = new ArrayList<>();
        PdfReader pdfReader = new PdfReader(pdfBytes, pwd == null ? null : pwd.getBytes(StandardCharsets.UTF_8));
        for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
            PdfDictionary page = pdfReader.getPageNRelease(i);
            PdfArray annotArray = (PdfArray) PdfReader.getPdfObjectRelease(page.get(PdfName.ANNOTS));
            for (int j = 0; annotArray != null && j < annotArray.size(); j++) {
                VerifyResult result = new VerifyResult();
                PdfDictionary annot = annotArray.getAsDict(j);
                if (annot.contains(PdfName.V)) {
                    PdfDictionary signDic = (PdfDictionary) pdfReader.getPdfObject(annot.get(PdfName.V));
                    verifyCfCa(result, signDic, pdfBytes);
                } else {
                    // thk's todo 2024/1/12 17:41 SM2?
                }
                result.setSignaturePage(i);
                resultList.add(result);
            }
        }
        return resultList;
    }


    private VerifyResult verifyCfCa(VerifyResult result, PdfDictionary annot, byte[] pdfBytes) throws CertificateException, OperatorCreationException, CMSException, TSPException, IOException {
        byte[] pkcs7 = TLV(annot.getAsString(PdfName.CONTENTS).getBytes());
        long[] range = annot.getAsArray(PdfName.BYTERANGE).asLongArray();
        byte[] contents = new byte[(int) (range[1] + range[3])];
        System.arraycopy(pdfBytes, 0, contents, 0, (int) range[1]);
        System.arraycopy(pdfBytes, (int) range[2], contents, (int) range[1], (int) range[3]);
        // 签署时间来源(本地或者时间戳)
        result.setTimeFrom("local");
        // thk's todo 2024/1/18 11:39 多签名验证这里会出错
        // 验证篡改
        result.setModify(pdfBytes.length != (range[2] + range[3]));
        // 验证签名+证书+时间戳
        analysisPkcs7(result, contents, pkcs7);
        // 签名名称
        PdfString sigName = annot.getAsString(PdfName.T);
        if (sigName != null) {
            result.setSignatureName(sigName.toString());
        }
        // 签名时间
        // PdfString sigTime = annot.getAsString(PdfName.SIG_TIME);
        PdfString sigTime = annot.getAsString(new PdfName("SigTime"));
        if (sigTime != null) {
            result.setSignDate(sigTime.toString());
        }
        return result;
    }

    /**
     * 解析pkcs7 signDate数据
     *
     * @param result   信息
     * @param contents 原文
     * @param pkcs7    pkcs7数据
     * @return 信息
     * @throws CMSException              signDate构造转换异常
     * @throws CertificateException      证书异常
     * @throws OperatorCreationException 证书链异常
     * @throws TSPException              时间戳验证异常
     * @throws IOException               证书转换异常
     */
    private VerifyResult analysisPkcs7(VerifyResult result, byte[] contents, byte[] pkcs7) throws CMSException, CertificateException, OperatorCreationException, TSPException, IOException, org.bouncycastle.operator.OperatorCreationException {
        CMSSignedData cmsSignedData = new CMSSignedData(new CMSProcessableByteArray(contents), ContentInfo.getInstance(pkcs7));
        Store<X509CertificateHolder> certStore = cmsSignedData.getCertificates();
        Collection<SignerInformation> signerInfoS = cmsSignedData.getSignerInfos().getSigners();
        if (signerInfoS.size() != 1) {
            throw new IllegalArgumentException("SignerInfo Size Not Support: " + signerInfoS.size());
        }
        SignerInformation signerInfo = signerInfoS.iterator().next();
        Iterator<?> iterator = certStore.getMatches(signerInfo.getSID()).iterator();
        X509CertificateHolder cert = (X509CertificateHolder) iterator.next();
        X509CertificateObject x509CertificateObject = new X509CertificateObject(cert.toASN1Structure());
        Certificate[] certificates = new Certificate[]{x509CertificateObject};
        // 证书链
        result.setPublicCertChain(certificates);
        SignerInformationVerifier signerInformationVerifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build(cert);
        // 验证签名
        result.setValidate(signerInfo.verify(signerInformationVerifier));
        // 时间戳
        AttributeTable unsignedAttributes = signerInfo.getUnsignedAttributes();
        if (unsignedAttributes != null) {
            Attribute attribute = unsignedAttributes.get(PKCSObjectIdentifiers.id_aa.branch("14"));
            ASN1Encodable timeStampSignDataAsn1 = attribute.getAttrValues().getObjectAt(0);
            CMSSignedData timeStampSignData = new CMSSignedData(timeStampSignDataAsn1.toASN1Primitive().getEncoded());
            TimeStampToken timeStampToken = new TimeStampToken(timeStampSignData);
            RDN CN = Arrays.stream(timeStampToken.getSID().getIssuer().getRDNs(BCStyle.CN)).findFirst().orElse(null);
            if (CN != null) {
                String timeStampCN = CN.getFirst().getValue().toString();
                result.setTimeFrom("tsa");
                result.setTimeStamper(timeStampCN);
            }
            Store<X509CertificateHolder> timeStampTokenCertificates = timeStampToken.getCertificates();
            Iterator<?> timeStampIt = timeStampTokenCertificates.getMatches(timeStampToken.getSID()).iterator();
            X509CertificateHolder timeStampCert = (X509CertificateHolder) timeStampIt.next();
            result.setTimeStampCert(timeStampCert.toASN1Structure().getEncoded());
            SignerInformationVerifier timeStampSignerInfoVerifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build(timeStampCert);
            try {
                timeStampToken.validate(timeStampSignerInfoVerifier);
            } catch (TSPException e) {
                e.printStackTrace();
                // thk's todo 2024/1/11 14:36 时间戳没验过

            }
        }
        return result;
    }

    /**
     * 构建TLV
     *
     * @param tlvBytes tlv16进制
     * @return 标准TLV结构
     */
    private static byte[] TLV(byte[] tlvBytes) {
        int length = tlvBytes[1] & 0xFF;
        // 判断长度字段的最高位
        if ((length & 0x80) == 0) {
            // 单字节长度表示
            byte[] tagLengthValue = new byte[length + 2];
            System.arraycopy(tlvBytes, 0, tagLengthValue, 0, length + 2);
            return tagLengthValue;
        } else {
            // 多字节长度表示
            int lengthBytesCount = length & 0x7F;
            byte[] lengthBytes = new byte[lengthBytesCount];
            System.arraycopy(tlvBytes, 2, lengthBytes, 0, lengthBytesCount);
            int valueStartIndex = 2 + lengthBytesCount;
            // 转成Value字节的个数
            int valueLength = byteArrayToInt(lengthBytes);
            byte[] tagLengthValue = new byte[valueLength + valueStartIndex];
            System.arraycopy(tlvBytes, 0, tagLengthValue, 0, valueLength + valueStartIndex);
            return tagLengthValue;
        }
    }

    /**
     * 将16进制字符转为int
     *
     * @param byteArray 16进制字符
     * @return 16进制字符转为int
     */
    private static int byteArrayToInt(byte[] byteArray) {
        int value = 0;
        for (byte b : byteArray) {
            value = (value << 8) | (b & 0xFF);
        }
        return value;
    }

    public static class VerifyResult {
        /**
         * 签署名
         */
        private String signatureName;

        /**
         * 签署页
         */
        private int signaturePage;

        /**
         * 哈希值
         */
        private byte[] hash;

        /**
         * 签署时间
         */
        private String signDate;

        /**
         * 签署时间来源
         */
        private String timeFrom;

        /**
         * 时间戳证书
         */
        private byte[] timeStampCert;

        /**
         * 时间戳机构
         */
        private String timeStamper;

        /**
         * 签名验证
         */
        private boolean validate;

        /**
         * 文档是否修改
         */
        private boolean modify;

        /**
         * 签名证书链
         */
        private Certificate[] publicCertChain;

        /**
         * CRL集合
         */
        private List<byte[]> crlList;

        /**
         * 印章数据
         */
        private byte[] sealData;

        /**
         * 印章类型
         */
        private String sealType;

        /**
         * 签名的理由
         */
        private String reason;

        /**
         * 签名的位置
         */
        private String location;

        /**
         * 签名的签署者联络信息
         */
        private String contact;

        public String getSignatureName() {
            return signatureName;
        }

        public void setSignatureName(String signatureName) {
            this.signatureName = signatureName;
        }

        public int getSignaturePage() {
            return signaturePage;
        }

        public void setSignaturePage(int signaturePage) {
            this.signaturePage = signaturePage;
        }

        public byte[] getHash() {
            return hash;
        }

        public void setHash(byte[] hash) {
            this.hash = hash;
        }

        public String getSignDate() {
            return signDate;
        }

        public void setSignDate(String signDate) {
            this.signDate = signDate;
        }

        public String getTimeFrom() {
            return timeFrom;
        }

        public void setTimeFrom(String timeFrom) {
            this.timeFrom = timeFrom;
        }

        public byte[] getTimeStampCert() {
            return timeStampCert;
        }

        public void setTimeStampCert(byte[] timeStampCert) {
            this.timeStampCert = timeStampCert;
        }

        public String getTimeStamper() {
            return timeStamper;
        }

        public void setTimeStamper(String timeStamper) {
            this.timeStamper = timeStamper;
        }

        public boolean isValidate() {
            return validate;
        }

        public void setValidate(boolean validate) {
            this.validate = validate;
        }

        public boolean isModify() {
            return modify;
        }

        public void setModify(boolean modify) {
            this.modify = modify;
        }

        public Certificate[] getPublicCertChain() {
            return publicCertChain;
        }

        public void setPublicCertChain(Certificate[] publicCertChain) {
            this.publicCertChain = publicCertChain;
        }

        public List<byte[]> getCrlList() {
            return crlList;
        }

        public void setCrlList(List<byte[]> crlList) {
            this.crlList = crlList;
        }

        public byte[] getSealData() {
            return sealData;
        }

        public void setSealData(byte[] sealData) {
            this.sealData = sealData;
        }

        public String getSealType() {
            return sealType;
        }

        public void setSealType(String sealType) {
            this.sealType = sealType;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        @Override
        public String toString() {
            return "SignatureInfo {" +
                    "\nsignatureName=" + signatureName +
                    "\nsignaturePage=" + signaturePage +
                    "\nhash=" + (hash != null ? Hex.toHexString(hash) : null) +
                    "\nsignDate=" + signDate +
                    "\ntimeFrom=" + timeFrom +
                    "\ntimeStampCert=" + (timeStampCert != null ? Hex.toHexString(timeStampCert) : null) +
                    "\ntimeStamper=" + timeStamper +
                    "\nvalidate=" + validate +
                    "\nmodify=" + modify +
                    "\npublicCertChain=" + Arrays.toString(publicCertChain) +
                    "\ncrlList=" + (crlList != null ? JSONUtil.toJsonStr(crlList.stream().map(Hex::toHexString).collect(Collectors.toList())) : null) +
                    "\nsealData=" + (sealData != null ? Hex.toHexString(sealData) : null) +
                    "\nsealType=" + sealType +
                    "\nreason=" + reason +
                    "\nlocation=" + location +
                    "\ncontact=" + contact +
                    "\n}";
        }
    }
}
