package PkcsTest.CAShare;


import cfca.mobile.constant.CFCAPublicConstant;

import java.util.List;
import java.util.Map;

/**
 * Created by wufan on 2017/5/16.
 */
public interface CertificateSource {

    /**
     * 生成证书请求
     *
     * @param certType 证书类型：单、双证
     * @param pin      pin码
     * @param certSys  证书加密类型
     * @return 返回值为null时表示失败
     */
    String generateCertReq(String certType, String pin, String certSys);

    /**
     * 导入证书，单证
     *
     * @param strCert 签名证书
     */
    boolean importCertificate(String strCert);

    /**
     * 导入证书双证
     *
     * @param strSignCert    签名证书 Base64 编码字符串
     * @param strEncryptCert 加密证书 Base64 编码字符串
     * @param strPri         加密证书私钥密文结构字符串
     * @return 是否成功
     */
    boolean importDoubleCertificate(String strSignCert, String strEncryptCert, String strPri);

    /**
     * 获取所有证书
     *
     * @return 证书集合
     */
    List<String> getCertificates();


    /**
     * 获取所有证书序列号
     *
     * @return 证书序列号
     */
    List<String> getCertificatesSn();

    /**
     * 添加证书？？
     */
    boolean addCertificate(CFCAPublicConstant.CERT_TYPE certType, String pin, CFCAPublicConstant.CERT_SYS certSys);

    void deleteCertificate(String certificate);

    void clearCertificates();

    /**
     * 修改PIN码
     *
     * @param oldPin   原PIN码
     * @param newPin   新PIN码
     * @param serialNo 证书序列号
     * @return 错误信息，如果没有错误将返回null
     */
    String changePassword(String oldPin, String newPin, String serialNo);

    void cancelAddCertificate();

    /**
     * P1签名？？
     */
    String signMessage(String pinCode, byte[] srcData, CFCAPublicConstant.HASH_TYPE hashType, CFCAPublicConstant.SIGN_FORMAT signType, String certBase64);

    /**
     * 签名hash？？
     */
    String signHash(String pinCode, byte[] hashData, CFCAPublicConstant.HASH_TYPE hashType, CFCAPublicConstant.SIGN_FORMAT signType, String certBase64);

    /**
     * 信封？？
     */
    String envelopeEncryptMessage(byte[] plaintext, String certBase64, CFCAPublicConstant.SYMMETRIC_ALGORITHM alg);

    /**
     * 解数字信封？？
     */
    byte[] envelopeDecryptMessage(String pin, String ciphertext, String certBase64);

    /**
     * 解析证书Base64
     */
    Map<String, Object> parseCertificateBase64(String certificateBase64);

    String generateTimestampReq(byte[] src, CFCAPublicConstant.HASH_TYPE hashType);

    boolean generateTimestampResp(String req);

    String updateTimestampInPKCS7Signature(byte[] pkcs7Signature, byte[] timestampResp);

    String encodePKCS7SignatureWithTimestamp(byte[] pkcs1Signature, byte[] certificate, byte[] timestampResp, byte[] src, boolean withSrc, CFCAPublicConstant.HASH_TYPE hash);

    /**
     * 依据序列号查询证书
     */
    Map<String, Object> getCertificateWithSn(String sn);

    byte[] sm2dh(String certificate, String pin, byte[] ra, byte[] Pb, byte[] Rb, int keylen);

    void importCertificate(String sign, String encryption, String key);

}
