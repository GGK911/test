package certTest.saxon;


import certTest.saxon.rsa.RSASignUtil;
import certTest.saxon.rsa.RSAUtils;
import certTest.saxon.sm2.CertificateUtils;
import certTest.saxon.sm2.SM2PublicKey;
import certTest.saxon.sm2.Sm2Utils;
import certTest.saxon.utils.Base64Utils;
import certTest.saxon.utils.RandomUtils;
import certTest.saxon.utils.ResultUtils;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/****
 * 软证书生成工具类
 * @author xiaozhong
 *
 */
public class CertUtils {


    /***
     * sm2证书生成
     * @param c C项 国家
     * @param cn CN项 公用名/CN
     * @param ou OU项 部门/OU
     * @param o O项 公用信息/O 单位统一社会信用代码、个人身份证
     * @param st ST项 省份
     * @param l L项 城市
     * @param certExpire 证书有效期 单位天
     * @param password 证书密码
     * @return
     */
    public static Map<String, byte[]> createSM2CertToOne(String c, String cn, String ou, String o, String st, String l, long certExpire, String password) {
        Map<String, byte[]> resultMap = new HashMap<>();
        byte[] pfxDER;
        try {
            KeyPair keyPair = Sm2Utils.generateKeyPair();
            // 证书对象生成
            X500NameBuilder x500NameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
            x500NameBuilder.addRDN(BCStyle.C, c);
            x500NameBuilder.addRDN(BCStyle.CN, cn);
            x500NameBuilder.addRDN(BCStyle.OU, ou);
            x500NameBuilder.addRDN(BCStyle.ST, st);
            x500NameBuilder.addRDN(BCStyle.L, l);
            x500NameBuilder.addRDN(BCStyle.O, o);
            X500Name x500Name = x500NameBuilder.build();
            SM2PublicKey sm2PublicKey = new SM2PublicKey(keyPair.getPublic().getAlgorithm(), (BCECPublicKey) keyPair.getPublic());
            // 使用使用者证书公钥生成证书签发请求
            PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(x500Name, sm2PublicKey);
            ContentSigner signerBuilder = new JcaContentSignerBuilder("SM3withSM2").setProvider(BouncyCastleProvider.PROVIDER_NAME).build(keyPair.getPrivate());
            byte[] csr = csrBuilder.build(signerBuilder).getEncoded();
            // 生成证书
            X509Certificate x509Certificate = CertificateUtils.makeCertificate(csr, certExpire);
            //*保存证书
            // 获取ASN.1编码的证书字节码
            byte[] asn1BinCert = x509Certificate.getEncoded();
            System.out.println("证书信息:\n" + x509Certificate);
            PKCS10CertificationRequest issuerPkcs10CertificationRequest = new PKCS10CertificationRequest(csr);
            PublicKey bcecPublicKey = CertificateUtils.convertX509ToECPublicKey(issuerPkcs10CertificationRequest.getSubjectPublicKeyInfo());
            // 制作证书 证书私钥,签发证
            PKCS12PfxPdu pkcs12PfxPdu = CertificateUtils.makePfx(keyPair.getPrivate(), bcecPublicKey, x509Certificate, password);
            // 证书序列化
            pfxDER = pkcs12PfxPdu.getEncoded(ASN1Encoding.DER);
            // 证书数据
            resultMap.put("certData", pfxDER);
            // 公钥
            resultMap.put("publicKey", keyPair.getPublic().getEncoded());
            // 私钥
            resultMap.put("privateKey", keyPair.getPrivate().getEncoded());
            // 证书cer
            resultMap.put("cer", asn1BinCert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }


    /****
     *SM2证书 验签
     * @param publicKey
     * @param originalText
     * @param signData
     * @return
     * @throws Exception
     */
    public static ResultUtils verifySM2Cert(String publicKey, String originalText, String signData) {
        try {

            //去除换行
            publicKey = publicKey.replaceAll("\r|\n", "");
            byte[] decode = Base64Utils.decode(publicKey);
            // 解析公钥
            Reader publicKeyReader = new StringReader(publicKey);
            PEMParser pemParser = new PEMParser(publicKeyReader);
            Object pemObj = pemParser.readObject();
            if (pemObj instanceof SubjectPublicKeyInfo) {
                SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) pemObj;
                byte[] x509Bytes = subjectPublicKeyInfo.toASN1Primitive().getEncoded(ASN1Encoding.DER);
                X509EncodedKeySpec eks = new X509EncodedKeySpec(x509Bytes);
                KeyFactory kf = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
                BCECPublicKey bcecPublicKey = (BCECPublicKey) kf.generatePublic(eks);
                SM2PublicKey sm2PublicKey = new SM2PublicKey(bcecPublicKey);
                ECParameterSpec parameterSpec = sm2PublicKey.getParameters();
                ECDomainParameters domainParameters = new ECDomainParameters(parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH());
                ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(sm2PublicKey.getQ(), domainParameters);
                // 签名验证
                SM2Signer signer = new SM2Signer();
                CipherParameters param = ecPublicKeyParameters;
                signer.init(false, param);
                signer.update(originalText.getBytes(), 0, originalText.length());
                byte[] sign = Base64Utils.decode(signData);
                boolean verifyResult = signer.verifySignature(sign);
                Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("verifyResult", verifyResult);
                return new ResultUtils(200, resultMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultUtils(200, false);
    }

    /****
     *SM2证书 签名
     * @param certBytes
     * @param originalText
     * @param passWord
     * @return
     * @throws Exception
     */
    public static ResultUtils signSM2Cert(byte[] certBytes, String originalText, String passWord) {
        ResultUtils result = new ResultUtils();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            X509Certificate x509Certificate = CertificateUtils.getX509CertificateFromPfx(certBytes, passWord);
            System.out.println(x509Certificate);
            BCECPublicKey bcecPublicKey = CertificateUtils.getBCECPublicKey(x509Certificate);
            StringWriter stringWriter = new StringWriter();
            PemObject pemObject = new PemObject("PUBLIC KEY", bcecPublicKey.getEncoded());
            JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
            pemWriter.writeObject(pemObject);
            pemWriter.close();
            BCECPrivateKey bcecPrivateKey = CertificateUtils.getBCECPrivateKeyFromPfx(certBytes, passWord);
            // 使用私钥签名
            byte[] signBytes = Sm2Utils.sign(bcecPrivateKey, originalText.getBytes());
            resultMap.put("signData", Base64Utils.encode(signBytes));
            resultMap.put("publicKey", stringWriter.toString());
            result.setCode(ResultUtils.CODE_SUCCESS);
            result.setObject(resultMap);
            result.setMsg("签名成功！");
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(ResultUtils.CODE_EXCEPTION);
            result.setObject(e);
            result.setMsg("签名出错！");
        }
        return result;
    }


    /**
     * 将InputStream写入本地文件
     *
     * @param destination 写入本地目录
     * @param input       输入流
     * @throws IOException
     */
    private static void writeToLocal(String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        downloadFile.close();
        input.close();
    }


    /***
     * RSA证书生成
     * @param c C项 国家
     * @param cn CN项 公用名/CN
     * @param ou OU项 部门/OU
     * @param o O项 公用信息/O 单位统一社会信用代码、个人身份证
     * @param st ST项 省份
     * @param l L项 城市
     * @param certExpire 证书有效期 单位天
     * @param password 证书密码
     * @return 返回私钥
     */
    public static Map<String, byte[]> createRSACertToOne(String c, String cn, String ou, String o, String st, String l, long certExpire, String password) {
        byte[] p12DER = null;
        //颁发者固定写死
        String issuerStr = "CN=ZHONGLZ,OU=ZHONGLZ,O=知录API,C=CN,E=1260649342@qq.com,L=长沙,ST=湖南";
        //使用者，需自定义传参带入
        String subjectStr = "CN=" + c + ",OU=" + ou + ",O=" + o + ",C=" + cn + ",E=" + (cn + RandomUtils.random(6)) + ",L=" + l + ",ST=" + st;
        //颁发地址，后续用到什么项目，公司地址等
        String certificateCRL = "https://gitee.com/zhong-zhuang";
        //证书位数
        int certNum = 2048;
        Map<String, byte[]> result = RSAUtils.createCert(password, issuerStr, subjectStr, certificateCRL, certExpire, certNum);
        return result;
    }

    /***
     * rsa验签
     * @param certBytes    证书文件
     * @param passWord    证书密码
     * @param originalText 签名原文
     * @param signData 签名值
     * @return
     */
    public static ResultUtils verifyRSACert(byte[] certBytes, String passWord, String originalText, String signData) {
        try {
            String KEYSTORE_ALIAS = "alias";
            KeyStore ks = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
            ks.load(new ByteArrayInputStream(certBytes), passWord.toCharArray());
            System.out.println("keystore type=" + ks.getType());
            Enumeration enums = ks.aliases();
            String keyAlias = null;
            if (enums.hasMoreElements()) {
                keyAlias = (String) enums.nextElement();
            }
            System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, passWord.toCharArray());
            Certificate cert = ks.getCertificate(keyAlias);
            PublicKey pubkey = cert.getPublicKey();
            System.out.println("cert class = " + cert.getClass().getName());
            System.out.println("cert = " + cert);
            System.out.println("public key = " + pubkey);
            System.out.println("private key = " + prikey);
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(pubkey);
            sign.update(originalText.getBytes("UTF-8"));
            boolean verifyResult = sign.verify(Base64.getDecoder().decode(signData));
            System.out.println("验证结果" + verifyResult);
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("verifyResult", verifyResult);
            return new ResultUtils(200, resultMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultUtils(200, false);
    }

    /****
     *RSA证书 签名
     * @param certBytes
     * @param originalText
     * @param passWord
     * @return
     * @throws Exception
     */
    public static ResultUtils signRSACert(byte[] certBytes, String originalText, String passWord) {
        ResultUtils result = new ResultUtils();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String signData;
        try {
            signData = RSASignUtil.sign(originalText, new ByteArrayInputStream(certBytes), passWord);
            KeyStore ks = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
            char[] nPassword = null;
            if ((passWord == null) || passWord.trim().equals("")) {
                nPassword = null;
            } else {
                nPassword = passWord.toCharArray();
            }
            ks.load(new ByteArrayInputStream(certBytes), nPassword);
            System.out.println("keystore type=" + ks.getType());
            Enumeration enums = ks.aliases();
            String keyAlias = null;
            if (enums.hasMoreElements()) {
                keyAlias = (String) enums.nextElement();
            }
            System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
            Certificate cert = ks.getCertificate(keyAlias);
            PublicKey pubkey = cert.getPublicKey();

            byte[] ss = cert.getEncoded();

            System.out.println("cert class = " + cert.getClass().getName());
            System.out.println("cert = " + cert);
            System.out.println("public key = " + pubkey);
            System.out.println("private key = " + prikey);


            resultMap.put("signData", signData);
            resultMap.put("publicKey", pubkey.getEncoded());
            result.setCode(ResultUtils.CODE_SUCCESS);
            result.setObject(resultMap);
            result.setMsg("签名成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
        //证书类型:SM2
        String certIden = UUID.randomUUID().toString().replace("-", "");//证书uuid
        String password = "123456";//证书默认密码
        Integer certExpire = 365;//证书有效期
        String c = "CN";//
        String cn = "张三" + RandomUtils.randomNumber(6);
        String ou = "张三";
        String o = "测试项目";//项目名称
        String st = "湖南省";
        String l = "长沙市";
        Map<String, byte[]> result = createSM2CertToOne(c, cn, ou, o, st, l, certExpire, password);

        //InputStream is = new ByteArrayInputStream(result.get("keyStoreData"));
        InputStream is = new ByteArrayInputStream(result.get("certData"));

        String path = "D:\\" + certIden + ".pfx";
        writeToLocal(path, is);


    }


}
