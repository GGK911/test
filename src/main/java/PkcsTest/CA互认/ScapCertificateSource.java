// package PkcsTest.CA互认;
//
// import cfca.mobile.constant.CFCAPublicConstant;
// import cfca.mobile.exception.CodeException;
// import cfca.mobile.scap.CFCACertificate;
// import cfca.mobile.scap.SCAP;
//
// import java.util.ArrayList;
// import java.util.List;
//
// /**
//  * Created by wufan on 2017/5/16.
//  */
// public class ScapCertificateSource implements CertificateSource {
//     private static final String TAG = ScapCertificateSource.class.getSimpleName();
//
//     private final SCAP scap;
//
//     public static ScapCertificateSource newInstance(SCAP scap) {
//         return new ScapCertificateSource(scap);
//     }
//
//     private ScapCertificateSource(SCAP scap) {
//         this.scap = scap;
//     }
//
//     @Override
//     public String generateCertReq(String certType, String pin, String certSys) {
//         String pkcs10 = null;
//         try {
//             pkcs10 = scap.generateCertReq(getCertType(certType), pin, getCertSys(certSys));
//         } catch (CodeException e) {
//             e.printStackTrace();
//         }
//
//         return pkcs10;
//     }
//
//     /**
//      * 申请csr时证书加密类型
//      *
//      * @param certType 后端配置的证书类型
//      */
//     private CFCAPublicConstant.CERT_TYPE getCertType(String certType) {
//         CFCAPublicConstant.CERT_TYPE type = null;
//         switch (certType) {
//             case "RSA_2048":
//                 type = CFCAPublicConstant.CERT_TYPE.CERT_RSA2048;
//                 break;
//             case "RSA_1024":
//                 type = CFCAPublicConstant.CERT_TYPE.CERT_RSA1024;
//                 break;
//             default:
//                 type = CFCAPublicConstant.CERT_TYPE.CERT_SM2;
//                 break;
//         }
//         return type;
//     }
//
//     /**
//      * 申请csr时证书单双证
//      *
//      * @param certSys 后端配置的
//      */
//     private CFCAPublicConstant.CERT_SYS getCertSys(String certSys) {
//         CFCAPublicConstant.CERT_SYS sys = null;
//         if ("SINGLE".equals(certSys)) {
//             sys = CFCAPublicConstant.CERT_SYS.SINGLE_CERT;
//         } else {
//             sys = CFCAPublicConstant.CERT_SYS.DUAL_CERT;
//         }
//         return sys;
//     }
//
//     @Override
//     public boolean importCertificate(String strCert) {
//         boolean isSuccess;
//         try {
//             scap.importCertificate(strCert);
//             isSuccess = true;
//         } catch (CodeException e) {
//             e.printStackTrace();
//             isSuccess = false;
//         }
//         return isSuccess;
//     }
//
//     @Override
//     public boolean importDoubleCertificate(String strSignCert, String strEncryptCert, String strPri) {
//         boolean isSuccess;
//         try {
//             scap.importDoubleCertificate(strSignCert, strEncryptCert, strPri);
//             isSuccess = true;
//         } catch (CodeException e) {
//             e.printStackTrace();
//             isSuccess = false;
//         }
//         return isSuccess;
//     }
//
//     @Override
//     public List<CFCACertificate> getCertificates() {
//         return scap.getCertificates();
//     }
//
//     @Override
//     public List<String> getCertificatesSn() {
//         List<String> snList = new ArrayList<>();
//         for (CFCACertificate cert : scap.getCertificates()) {
//             snList.add(cert.getSerialNumber());
//         }
//         return snList;
//     }
//
//     @Override
//     public boolean addCertificate(CFCAPublicConstant.CERT_TYPE certType, String pin, CFCAPublicConstant.CERT_SYS certSys) {
//         return false;
//     }
//
//     @Override
//     public void deleteCertificate(CFCACertificate certificate) {
//
//     }
//
//     @Override
//     public void clearCertificates() {
//
//     }
//
//     @Override
//     public String changePassword(String oldPin, String newPin, String serialNo) {
//         String errorMsg = null;
//         try {
//             scap.changePin(oldPin, newPin, scap.getCertificateWithSn(serialNo));
//         } catch (CodeException e) {
//             errorMsg = e.getMessage();
//         }
//         return errorMsg;
//     }
//
//     @Override
//     public void cancelAddCertificate() {
//
//     }
//
//     @Override
//     public String signMessage(String pinCode, byte[] srcData, CFCAPublicConstant.HASH_TYPE hashType, CFCAPublicConstant.SIGN_FORMAT signType, CFCACertificate cert) {
//         try {
//             return scap.signMessage(pinCode, srcData, hashType, signType, cert);
//         } catch (CodeException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }
//
//     @Override
//     public String signHash(String pinCode, byte[] hashData, CFCAPublicConstant.HASH_TYPE hashType, CFCAPublicConstant.SIGN_FORMAT signType, CFCACertificate cert) {
//         try {
//             return scap.signHashData(pinCode, hashData, hashType, signType, cert);
//         } catch (CodeException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }
//
//     @Override
//     public String envelopeEncryptMessage(byte[] plaintext, CFCACertificate certificate, CFCAPublicConstant.SYMMETRIC_ALGORITHM alg) {
//         try {
//             return scap.envelopeEncryptMessage(plaintext, certificate, alg);
//         } catch (CodeException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }
//
//     @Override
//     public byte[] envelopeDecryptMessage(String pin, String ciphertext, CFCACertificate certificate) {
//         try {
//             return scap.envelopeDecryptMessage(pin, ciphertext, certificate);
//         } catch (CodeException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }
//
//     @Override
//     public CFCACertificate parseCertificateBase64(String certificateBase64) {
//         try {
//             return scap.parseCertificateBase64(certificateBase64);
//         } catch (CodeException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }
//
//     @Override
//     public String generateTimestampReq(byte[] src, CFCAPublicConstant.HASH_TYPE hashType) {
//         return null;
//     }
//
//     @Override
//     public boolean generateTimestampResp(String req) {
//         return false;
//     }
//
//     @Override
//     public String updateTimestampInPKCS7Signature(byte[] pkcs7Signature, byte[] timestampResp) {
//         return null;
//     }
//
//     @Override
//     public String encodePKCS7SignatureWithTimestamp(byte[] pkcs1Signature, byte[] certificate, byte[] timestampResp, byte[] src, boolean withSrc, CFCAPublicConstant.HASH_TYPE hash) {
//         return null;
//     }
//
//     @Override
//     public CFCACertificate getCertificateWithSn(String sn) {
//         return scap.getCertificateWithSn(sn);
//     }
//
//     @Override
//     public byte[] sm2dh(CFCACertificate certificate, String pin, byte[] ra, byte[] Pb, byte[] Rb, int keylen) {
//         return new byte[0];
//     }
//
//     @Override
//     public void importCertificate(String sign, String encryption, String key) {
//
//     }
// }
