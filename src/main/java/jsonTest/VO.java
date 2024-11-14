package jsonTest;

/**
 * 下载证书对象vo
 */
public class VO {
    private String certDN;//证书DN
    private String notBefore;//证书过期时间
    private String validity;//有效期
    private String p10;//p10
    private boolean flag;//证书类型 单证还是双证  true单证 false双证
    private String oldSignCertSN;//原来证书序列号
    private String oldEncCertSN;//原来证书DN
    private String certType;//证书算法类型
    private String certSN;//第一个证书（签名证书）序列号
    private String signCert;//第一个证书（签名证书）cer文件
    private String p7b;//第一个证书（签名证书）p7b文件
    private String encCertSN;//第二个证书（加密证书)序列号
    private String encCert;//第二个证书（加密证书）cer文件
    private String doubleP7b;//第二个证书（加密证书）p7b文件
    private String encSessionKey;//RSA：由签名公钥加密
    private String encPrivateKey;//RSA:由encSessionKey加密
    private String notAfter;//证书过期时间
    private String ipAddress;//请求IP
    private String raCaId;//
    private String raCertid;//
    private String ctmlName;//
    private String extensionField;//
    private String modelRoot;//
    private String modelKeytype;//
    private String modelKeylength;//
    private String modelUse;//
    private String certFunction;//

    public VO() {
    }

    public String getModelUse() {
        return modelUse;
    }

    public void setModelUse(String modelUse) {
        this.modelUse = modelUse;
    }

    public String getCertFunction() {
        return certFunction;
    }

    public void setCertFunction(String certFunction) {
        this.certFunction = certFunction;
    }

    public String getModelKeylength() {
        return modelKeylength;
    }

    public void setModelKeylength(String modelKeylength) {
        this.modelKeylength = modelKeylength;
    }

    public String getModelKeytype() {
        return modelKeytype;
    }

    public void setModelKeytype(String modelKeytype) {
        this.modelKeytype = modelKeytype;
    }

    public String getModelRoot() {
        return modelRoot;
    }

    public void setModelRoot(String modelRoot) {
        this.modelRoot = modelRoot;
    }

    public String getExtensionField() {
        return extensionField;
    }

    public void setExtensionField(String extensionField) {
        this.extensionField = extensionField;
    }

    public String getCtmlName() {
        return ctmlName;
    }

    public void setCtmlName(String ctmlName) {
        this.ctmlName = ctmlName;
    }

    public String getRaCertid() {
        return raCertid;
    }

    public void setRaCertid(String raCertid) {
        this.raCertid = raCertid;
    }

    public String getRaCaId() {
        return raCaId;
    }

    public void setRaCaId(String raCaId) {
        this.raCaId = raCaId;
    }

    public String getCertDN() {
        return certDN;
    }

    public void setCertDN(String certDN) {
        this.certDN = certDN;
    }

    public String getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(String notBefore) {
        this.notBefore = notBefore;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getP10() {
        return p10;
    }

    public void setP10(String p10) {
        this.p10 = p10;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getOldSignCertSN() {
        return oldSignCertSN;
    }

    public void setOldSignCertSN(String oldSignCertSN) {
        this.oldSignCertSN = oldSignCertSN;
    }

    public String getOldEncCertSN() {
        return oldEncCertSN;
    }

    public void setOldEncCertSN(String oldEncCertSN) {
        this.oldEncCertSN = oldEncCertSN;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCertSN() {
        return certSN;
    }

    public void setCertSN(String certSN) {
        this.certSN = certSN;
    }

    public String getSignCert() {
        return signCert;
    }

    public void setSignCert(String signCert) {
        this.signCert = signCert;
    }

    public String getP7b() {
        return p7b;
    }

    public void setP7b(String p7b) {
        this.p7b = p7b;
    }

    public String getEncCertSN() {
        return encCertSN;
    }

    public void setEncCertSN(String encCertSN) {
        this.encCertSN = encCertSN;
    }

    public String getEncCert() {
        return encCert;
    }

    public void setEncCert(String encCert) {
        this.encCert = encCert;
    }

    public String getDoubleP7b() {
        return doubleP7b;
    }

    public void setDoubleP7b(String doubleP7b) {
        this.doubleP7b = doubleP7b;
    }

    public String getEncSessionKey() {
        return encSessionKey;
    }

    public void setEncSessionKey(String encSessionKey) {
        this.encSessionKey = encSessionKey;
    }

    public String getEncPrivateKey() {
        return encPrivateKey;
    }

    public void setEncPrivateKey(String encPrivateKey) {
        this.encPrivateKey = encPrivateKey;
    }

    public String getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(String notAfter) {
        this.notAfter = notAfter;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
