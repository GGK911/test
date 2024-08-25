package PkcsTest;

import cn.com.mcsca.pki.core.crypto.asymmetric.SM2CipherUtil;
import cn.hutool.core.util.StrUtil;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.jcajce.JcaX509ContentVerifierProviderBuilder;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.PlainDSAEncoding;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 针对cfca的pkcs#10问题
 *
 * @author TangHaoKai
 * @version V1.0 2024/2/28 13:55
 */
public class CsrUtil {

    /**
     * 针对cfca特殊Pkcs#10将Mcsca的返回私钥进行转换
     *
     * @param pri           签名私钥
     * @param encSessionKey 对称密钥(被签名公钥加了密的)
     * @param encPrivateKey 加密证书的私钥(被对称密钥加了密的)
     * @param publicKey     加密公钥
     * @return 签名私钥
     * @throws NoSuchAlgorithmException  对称/pkcs1加密算法异常
     * @throws InvalidKeySpecException   签名私钥Parse异常
     * @throws IOException               获取流
     * @throws NoSuchPaddingException    加密算法异常
     * @throws InvalidKeyException       加密对称密钥是异常
     * @throws IllegalBlockSizeException 加密对称密钥是异常
     * @throws BadPaddingException       加密对称密钥是异常
     */
    public static String cfcaRsaSpecial(String pri, String encSessionKey, String encPrivateKey, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        KeyFactory kf = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Hex.decode(pri)));
        // 解出对称密钥
        String deSessionKey = rsaDecrypt(encSessionKey, privateKey);
        SecretKeySpec rc4key = new SecretKeySpec(Hex.decode(deSessionKey), "RC4");
        String encCertPriHex = RC4Decrypt(encPrivateKey, rc4key);
        // 私钥参数
        ASN1Sequence seq1 = ASN1Sequence.getInstance(Hex.decode(encCertPriHex));
        ASN1Sequence encPriParamAsn1 = ASN1Sequence.getInstance(ASN1OctetString.getInstance(seq1.getObjectAt(2)).getOctets());
        String priParamHex = Hex.toHexString(encPriParamAsn1.getEncoded());
        // DESede加密 这个KEY是要加密反给用户的
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
        keyGenerator.init(168); // 168位密钥
        SecretKey secretKey = keyGenerator.generateKey();
        String encPriParamHex = DESedeEncrypt(priParamHex, secretKey);
        // KEY也要加密
        Cipher cipher3 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher3.init(Cipher.ENCRYPT_MODE, publicKey);
        String encSecreKey = Hex.toHexString(cipher3.doFinal(secretKey.getEncoded()));
        // 组装ASN1 Sequence
        String secretKeyBase64 = Base64.toBase64String(packAsn1Seq(91, encSecreKey).getEncoded());
        String encPriParamBase64 = Base64.toBase64String(packAsn1Seq(91, encPriParamHex).getEncoded());
        return cfcaRsaSpecial(secretKeyBase64, encPriParamBase64);
    }

    /**
     * 针对cfca特殊Pkcs#10将Mcsca的返回私钥进行转换
     *
     * @param encCert       Mcsca生成的加密证书
     * @param encPrivateKey 加密证书的私钥(被签名公钥加了密的)
     * @param pri           签名私钥
     * @param publicKey     加密公钥
     * @return 特殊结构
     * @throws Exception 加密证书中取出公钥/解出私钥参数d
     */
    public static String cfcaSm2Special(String encCert, String encPrivateKey, String pri, PublicKey publicKey) throws Exception {
        // 解出私钥参数d
        String decodeCreatePkcs10EncPrivateKeyD = analysisSM2EncKey(encPrivateKey, pri);
        // 这里的XY得从加密证书中取出
        CertificateFactory fact = CertificateFactory.getInstance("X.509", new BouncyCastleProvider());
        X509Certificate cert3 = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(Base64.decode(encCert)));
        BCECPublicKey encCertPublicKey = (BCECPublicKey) cert3.getPublicKey();
        org.bouncycastle.math.ec.ECPoint encCertPublicKeyQ = encCertPublicKey.getQ();
        // 组成：XYD,这里bigInt转字节判断要去掉首位
        byte[] X = new byte[32];
        byte[] pubXBytes = encCertPublicKeyQ.getRawXCoord().toBigInteger().toByteArray();
        System.arraycopy(pubXBytes, pubXBytes.length == 32 ? 0 : 1, X, 0, X.length);
        byte[] Y = new byte[32];
        byte[] pubYBytes = encCertPublicKeyQ.getAffineYCoord().toBigInteger().toByteArray();
        System.arraycopy(pubYBytes, pubYBytes.length == 32 ? 0 : 1, Y, 0, Y.length);
        byte[] XYD = Arrays.concatenate(X, Y, Hex.decode(decodeCreatePkcs10EncPrivateKeyD));
        String XYDHex = Hex.toHexString(XYD);
        // 加密
        String encData = Hex.toHexString(SM2CipherUtil.encryptWithOld(Hex.decode(XYDHex), publicKey));
        return cfcaSpecial(encData);
    }

    /**
     * 判断Pkcs#10来源
     *
     * @param pkcs10 证书请求
     * @return 来源-Pkcs10SourceEnum
     */
    public static String judgePkcs10Source(String pkcs10) {
        try {
            // RSA
            ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(pkcs10));
            ASN1Primitive asn1Primitive;
            while ((asn1Primitive = asn1InputStream.readObject()) != null) {
                ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
                ASN1Sequence requestInfo = (ASN1Sequence) sequence.getObjectAt(0);
                if (requestInfo.size() < 4) {
                    return "NORMAL";
                }
                ASN1TaggedObject attr = (ASN1TaggedObject) requestInfo.getObjectAt(3);
                ASN1Sequence attrSeq = (ASN1Sequence) attr.getBaseObject();
                // ASN1Sequence attrSeq = (ASN1Sequence) attr.getObject();
                System.out.println(attrSeq.size());
                if (attrSeq.size() != 0 && (attrSeq.getObjectAt(1) instanceof ASN1Sequence) && (attrSeq.getObjectAt(0) instanceof ASN1Sequence)) {
                    return "CFCA";
                } else {
                    return "NORMAL";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取pkcs10结构异常");
        }
        throw new RuntimeException("非法的pkcs10结构");
    }

    /**
     * 获取CSR自带的公钥
     *
     * @param pkcs10 CSR
     * @return 公钥
     */
    public static PublicKey getSm2PubFromCsr(String pkcs10) {
        try {
            ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(pkcs10));
            ASN1Primitive asn1Primitive;
            while ((asn1Primitive = asn1InputStream.readObject()) != null) {
                ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
                ASN1Sequence requestInfo = (ASN1Sequence) sequence.getObjectAt(0);
                ASN1Sequence sequence1 = ASN1Sequence.getInstance(requestInfo.getObjectAt(2));
                ASN1BitString instance = (ASN1BitString) sequence1.getObjectAt(1);
                ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
                ECDomainParameters domainParameters = new ECDomainParameters(sm2Spec.getCurve(), sm2Spec.getG(), sm2Spec.getN(), sm2Spec.getH());
                // 使用 domainParameters 创建椭圆曲线点对象
                org.bouncycastle.math.ec.ECPoint ecPoint = domainParameters.getCurve().decodePoint(instance.getBytes());
                // 使用公钥点创建 ECPublicKeyParameters 对象
                ECParameterSpec ecParameterSpec = new ECParameterSpec(sm2Spec.getCurve(), sm2Spec.getG(), sm2Spec.getN(), sm2Spec.getH());
                KeyFactory keyFact = KeyFactory.getInstance("EC", new BouncyCastleProvider());
                return (BCECPublicKey) keyFact.generatePublic(new org.bouncycastle.jce.spec.ECPublicKeySpec(ecPoint, ecParameterSpec));
            }
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("解析CSASN1结构异常");
        }
        throw new RuntimeException("读取CSRASN1结构异常");
    }

    public static PublicKey getRsaPubFromCsr(String pkcs10) {
        try {
            ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(pkcs10));
            ASN1Primitive asn1Primitive;
            while ((asn1Primitive = asn1InputStream.readObject()) != null) {
                ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
                ASN1Sequence requestInfo = (ASN1Sequence) sequence.getObjectAt(0);
                ASN1Sequence sequence1 = ASN1Sequence.getInstance(requestInfo.getObjectAt(2));
                ASN1BitString instance = (ASN1BitString) sequence1.getObjectAt(1);
                ASN1Sequence sequence2 = ASN1Sequence.getInstance(instance.getBytes());
                BigInteger n = ASN1Integer.getInstance(sequence2.getObjectAt(0)).getValue();
                BigInteger e = ASN1Integer.getInstance(sequence2.getObjectAt(1)).getValue();
                RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
                PublicKey publicKey = keyFactory.generatePublic(rsaPublicKeySpec);
                return publicKey;
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException("解析CSASN1结构异常");
        }
        throw new RuntimeException("读取CSRASN1结构异常");
    }

    /*****************************私有方法********************************/

    /**
     * 修改cfca的pkcs结构
     *
     * @param pkcs10 cfca
     * @return 符合标准的结构pkcs
     * @throws IOException pkcs对象转转字节
     */
    private static String changePkcsStructure(String pkcs10) throws IOException {
        ASN1Sequence pkcsSeq = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(Base64.decode(pkcs10)));
        ASN1Sequence cerResInfo = ASN1Sequence.getInstance(pkcsSeq.getObjectAt(0));

        ASN1Sequence passwordAttrSet = ASN1Sequence.getInstance(ASN1TaggedObject.getInstance(cerResInfo.getObjectAt(3)).getBaseObject());
        final ASN1Sequence passwordAttr = ASN1Sequence.getInstance(passwordAttrSet.getObjectAt(0));
        ASN1ObjectIdentifier pwdAttrOid = ASN1ObjectIdentifier.getInstance(passwordAttr.getObjectAt(0));

        ASN1PrintableString pwdAttrValue = DERPrintableString.getInstance(passwordAttr.getObjectAt(1));

        final Attribute attribute = new Attribute(pwdAttrOid, new DERSet(pwdAttrValue));

        final CertificationRequestInfo certificationRequestInfo = new CertificationRequestInfo(X500Name.getInstance(cerResInfo.getObjectAt(1)), SubjectPublicKeyInfo.getInstance(cerResInfo.getObjectAt(2)), new DERSet(attribute));
        final CertificationRequest pkcs10After = new CertificationRequest(certificationRequestInfo, AlgorithmIdentifier.getInstance(pkcsSeq.getObjectAt(1)), DERBitString.getInstance(pkcsSeq.getObjectAt(2)));
        return Base64.toBase64String(pkcs10After.getEncoded());
    }

    private static String cfcaRsaSpecial(String secretKey, String encData) {
        String model = "0000000000000000";
        String secretKeyLine = lineFeed(secretKey);
        String secretKeyLineLength = model + secretKeyLine.length();
        String encDataLine = lineFeed(encData);
        String encDataLineLength = model + encDataLine.length();
        return "0000000000000001" +
                "0000000000000001" +
                "0000000000000306" +
                secretKeyLineLength.substring(secretKeyLineLength.length() - 16) +
                secretKeyLine +
                encDataLineLength.substring(encDataLineLength.length() - 16) +
                encDataLine;
    }

    private static ASN1Sequence packAsn1Seq(int version, String octetString) {
        ASN1Integer version2 = new ASN1Integer(version);
        ASN1OctetString octetString2 = new DEROctetString(Hex.decode(octetString));
        ASN1Encodable[] sequenceElements2 = new ASN1Encodable[]{version2, octetString2};
        return new DERSequence(sequenceElements2);
    }

    private static String DESedeEncrypt(String data, SecretKey secretKey) {
        try {
            String secreKeyHex = Hex.toHexString(secretKey.getEncoded());
            Cipher instance3 = Cipher.getInstance("DESede/ECB/PKCS7Padding", new BouncyCastleProvider()); // PKCS7Padding
            instance3.init(Cipher.ENCRYPT_MODE, secretKey);
            return Hex.toHexString(instance3.doFinal(Hex.decode(data)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException e) {
            throw new RuntimeException("解密算法错误");
        } catch (InvalidKeyException | IllegalBlockSizeException e) {
            throw new RuntimeException("RSA解密KEY错误");
        }
    }

    private static String RC4Decrypt(String encData, SecretKeySpec keySpec) {
        try {
            // 解密文encPrivateKey
            Cipher instance2 = Cipher.getInstance("RC4");
            instance2.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] deEncPri = instance2.doFinal(Base64.decode(encData));
            return Hex.toHexString(deEncPri);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException e) {
            throw new RuntimeException("解密算法错误");
        } catch (InvalidKeyException | IllegalBlockSizeException e) {
            throw new RuntimeException("RSA解密KEY错误");
        }
    }

    private static String rsaDecrypt(String encData, PrivateKey privateKey) {
        try {
            // 解密sessionKey
            Cipher instance1 = Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
            instance1.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] sessionKey = instance1.doFinal(Base64.decode(encData));
            return Hex.toHexString(sessionKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException e) {
            throw new RuntimeException("解密算法错误");

        } catch (InvalidKeyException | IllegalBlockSizeException e) {
            throw new RuntimeException("RSA解密KEY错误");
        }
    }

    private static String cfcaSpecial(String encData) {
        try {
            byte[] encDataBytes = Hex.decode(encData);
            byte[] encDataX = new byte[32];
            System.arraycopy(encDataBytes, 0, encDataX, 0, encDataX.length);
            byte[] encDataY = new byte[32];
            System.arraycopy(encDataBytes, 32, encDataY, 0, encDataY.length);
            byte[] encDataDecode = new byte[encDataBytes.length - 96];
            System.arraycopy(encDataBytes, 64, encDataDecode, 0, encDataDecode.length);
            byte[] encDataHash = new byte[32];
            System.arraycopy(encDataBytes, 64 + encDataBytes.length - 96, encDataHash, 0, encDataHash.length);
            String concatenateHex = Hex.toHexString(Arrays.concatenate(encDataX, encDataY, encDataHash, encDataDecode));

            byte[] concatenate = Hex.decode(concatenateHex);
            // 版本号
            ASN1Integer version = new ASN1Integer(2);

            ASN1OctetString octetString = new DEROctetString(concatenate);

            // 组装ASN1Sequence
            ASN1Encodable[] sequenceElements = new ASN1Encodable[]{version, octetString};
            ASN1Sequence sequence = new DERSequence(sequenceElements);
            String base64String = Base64.toBase64String(sequence.getEncoded());
            String addLine = lineFeed(base64String);
            int length = addLine.length();

            final String lengthStr = "0000000000000000" + length;
            String suffix = "0000000000000001" +
                    "0000000000000001" +
                    "0000000000000000" +
                    "0000000000000000" +
                    lengthStr.substring(lengthStr.length() - 16);

            return suffix + addLine;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("sequence对象异常");
        }
    }

    /**
     * SM2
     * 使用签名证书的私钥解出 被签名证书公钥加了密的 加密证书私钥
     *
     * @param encryptedPrivateKey 被签名证书公钥加了密的 加密证书私钥
     * @param signPri             签名证书私钥
     * @return 加密证书私钥参数D
     * @throws Exception
     */
    public static String analysisSM2EncKey(String encryptedPrivateKey, String signPri) throws Exception {
        ASN1Sequence asn1_epk = (ASN1Sequence) ASN1Sequence.fromByteArray(Base64.decode(encryptedPrivateKey));
        ASN1ObjectIdentifier pkcs7type = (ASN1ObjectIdentifier) asn1_epk.getObjectAt(0);
        if (pkcs7type.getId().equals("1.2.156.10197.6.1.4.2.4")) {
            KeyFactory kf = KeyFactory.getInstance("EC", new BouncyCastleProvider());
            //初始化sm2解密
            BCECPrivateKey pri = (BCECPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Hex.decode(signPri)));
            ECPrivateKeyParameters aPriv = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter(pri);
            SM2Engine sm2Engine = new SM2Engine();
            sm2Engine.init(false, aPriv);

            ASN1TaggedObject dtos = (ASN1TaggedObject) asn1_epk.getObjectAt(1);
            ASN1Sequence asn1_encdata = (ASN1Sequence) dtos.getBaseObject();
            // ASN1Sequence asn1_encdata = (ASN1Sequence) dtos.getObjectParser(0, true);

            ASN1Set d1_pubkeyencs = (ASN1Set) asn1_encdata.getObjectAt(1);
            ASN1Sequence asn1_pubkeyenc = (ASN1Sequence) d1_pubkeyencs.getObjectAt(0);
            DEROctetString dertostr = (DEROctetString) asn1_pubkeyenc.getObjectAt(3);
            ASN1Sequence dd = (ASN1Sequence) ASN1Sequence.fromByteArray(dertostr.getOctets());
            ASN1Integer x = (ASN1Integer) dd.getObjectAt(0);
            byte[] xbyte = x.getPositiveValue().toByteArray();
            ASN1Integer y = (ASN1Integer) dd.getObjectAt(1);
            byte[] ybyte = y.getPositiveValue().toByteArray();
            DEROctetString hash = (DEROctetString) dd.getObjectAt(2);
            DEROctetString pubencdata = (DEROctetString) dd.getObjectAt(3);

            byte[] xy = new byte[65];
            xy[0] = 4;
            System.arraycopy(xbyte, xbyte.length == 32 ? 0 : 1, xy, 1, 32);
            System.arraycopy(ybyte, ybyte.length == 32 ? 0 : 1, xy, 1 + 32, 32);
            byte[] c2 = new byte[16];
            System.arraycopy(pubencdata.getOctets(), 0, c2, 0, 16);
            byte[] c3 = new byte[32];
            System.arraycopy(hash.getOctets(), 0, c3, 0, 32);
            byte[] c4 = Arrays.concatenate(xy, c2, c3);
            // sm2解密得到对称密钥的key
            byte[] sm4key = sm2Engine.processBlock(c4, 0, c4.length);

            ASN1Sequence encdata = (ASN1Sequence) asn1_encdata.getObjectAt(3);
            ASN1TaggedObject sm4encdatadto = (ASN1TaggedObject) encdata.getObjectAt(2);
            DEROctetString dstr_sm4encdata = (DEROctetString) sm4encdatadto.getBaseObject();
            // DEROctetString dstr_sm4encdata = (DEROctetString) sm4encdatadto.getObjectParser(0, true);
            byte[] sm4encdata = dstr_sm4encdata.getOctets();
            // sm4解密
            Cipher cipher = Cipher.getInstance("SM4/ECB/NoPadding", new BouncyCastleProvider());
            SecretKeySpec newKey = new SecretKeySpec(sm4key, "SM4");
            cipher.init(Cipher.DECRYPT_MODE, newKey);
            sm4encdata = cipher.doFinal(sm4encdata);
            return Hex.toHexString(Arrays.copyOfRange(sm4encdata, 32, sm4encdata.length));
        } else {
            throw new Exception("无效的SM2数字信封格式");
        }
    }

    /**
     * 从cfca的特殊Pkcs#10中获取加密公钥 SM2
     *
     * @param pkcs10 cfca的特殊Pkcs#10
     * @return 加密公钥XY HEX
     */
    public static String getQFromCfcaSm2Pkcs10(String pkcs10) {
        ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(pkcs10));
        byte[] x = new byte[32];
        byte[] y = new byte[32];
        ASN1Primitive asn1Primitive;
        try {
            while ((asn1Primitive = asn1InputStream.readObject()) != null) {
                ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
                ASN1Sequence requestInfo = (ASN1Sequence) sequence.getObjectAt(0);
                ASN1TaggedObject attr = (ASN1TaggedObject) requestInfo.getObjectAt(3);
                ASN1Sequence attrSeq = (ASN1Sequence) attr.getBaseObject();
                ASN1Sequence diySeq = (ASN1Sequence) attrSeq.getObjectAt(1);
                ASN1OctetString diySeqOctetStr = (ASN1OctetString) diySeq.getObjectAt(1);
                ASN1Sequence diyOctetStrSeq = (ASN1Sequence) new ASN1InputStream(diySeqOctetStr.getOctets()).readObject();
                ASN1OctetString tempPublic = (ASN1OctetString) diyOctetStrSeq.getObjectAt(1);

                System.arraycopy(tempPublic.getOctets(), 8, x, 0, 32);
                System.arraycopy(tempPublic.getOctets(), 72, y, 0, 32);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("读取asn1错误");
        }
        return Hex.toHexString(x) + Hex.toHexString(y);
    }

    /**
     * cfca的特殊Pkcs#10中获取加密公钥 RSA
     *
     * @param pkcs10 cfca的特殊Pkcs#10
     * @return 加密公钥对象
     */
    public static PublicKey parsePubKeyFromCfcaRsaPkcs10(String pkcs10) {
        try {
            ASN1InputStream asn1InputStream = new ASN1InputStream(Base64.decode(pkcs10));
            ASN1Primitive asn1Primitive;
            while ((asn1Primitive = asn1InputStream.readObject()) != null) {
                ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
                ASN1Sequence requestInfo = (ASN1Sequence) sequence.getObjectAt(0);
                ASN1TaggedObject attr = (ASN1TaggedObject) requestInfo.getObjectAt(3);
                ASN1Sequence attrSeq = (ASN1Sequence) attr.getBaseObject();
                ASN1Sequence diySeq = ASN1Sequence.getInstance(attrSeq.getObjectAt(1));
                ASN1OctetString diySeqOctetStr = (ASN1OctetString) diySeq.getObjectAt(1);
                ASN1Sequence diyOctetStrSeq = (ASN1Sequence) new ASN1InputStream(diySeqOctetStr.getOctets()).readObject();
                ASN1OctetString tempPublic = (ASN1OctetString) diyOctetStrSeq.getObjectAt(1);
                ASN1Sequence cfcaPubAsn1 = ASN1Sequence.getInstance(tempPublic.getOctets());
                BigInteger n = ASN1Integer.getInstance(cfcaPubAsn1.getObjectAt(0)).getValue();
                BigInteger e = ASN1Integer.getInstance(cfcaPubAsn1.getObjectAt(1)).getValue();
                RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
                return keyFactory.generatePublic(rsaPublicKeySpec);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("转换PublicKey错误");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException("读取n/e公钥参数错误");
        }
        throw new RuntimeException("n/e转换PublicKey错误");
    }

    /**
     * 用已知公钥参数Q计算SM2公钥对象
     *
     * @param paramQ 公钥参数Q
     * @return 公钥对象
     * @throws NoSuchAlgorithmException KeyFactory创建异常
     * @throws InvalidKeySpecException  生成公钥对象异常
     */
    public static PublicKey paramQToPub(String paramQ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final byte[] xy = Hex.decode(paramQ);
        // 创建一个新的字节数组，长度比原始公钥字节数组大1
        byte[] newBytes = new byte[xy.length + 1];
        // 在新数组的第一个位置插入 0x04
        newBytes[0] = 0x04;
        System.arraycopy(xy, 0, newBytes, 1, xy.length);

        KeyFactory fact = KeyFactory.getInstance("EC", new BouncyCastleProvider());
        // 使用BouncyCastle提供的ECNamedCurveTable类获取SM2的参数规范
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");

        // 使用SM2的参数规范创建EC参数规范
        ECNamedCurveSpec ecSpec = new ECNamedCurveSpec(
                sm2Spec.getName(),
                sm2Spec.getCurve(),
                sm2Spec.getG(),
                sm2Spec.getN(),
                sm2Spec.getH(),
                sm2Spec.getSeed()
        );

        ECPoint ecPoint = ECPointUtil.decodePoint(ecSpec.getCurve(), newBytes);

        // 使用SM2算法创建EC公钥规范
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(ecPoint, ecSpec);

        // 使用BouncyCastle提供的KeyFactory类根据EC公钥规范生成公钥对象
        return fact.generatePublic(ecPublicKeySpec);
    }

    private static String lineFeed(String originalString) {
        int segmentLength = 64; // 每个分割段的长度

        StringBuilder modifiedString = new StringBuilder(); // 用于存储修改后的字符串

        // 循环遍历原始字符串，每次取出64个字符进行处理
        for (int i = 0; i < originalString.length(); i += segmentLength) {
            // 获取当前分割段的结束索引
            int endIndex = Math.min(i + segmentLength, originalString.length());
            // 截取当前分割段的子字符串
            String segment = originalString.substring(i, endIndex);
            // 在子字符串末尾添加逗号
            modifiedString.append(segment).append(",");
        }
        return modifiedString.toString();
    }

    /**
     * 验证P10格式和签名信息
     *
     * @param p10 CSR
     */
    public static void verifyP10(String p10, String algTypeEnum) {
        if (StrUtil.isNotEmpty(p10)) {
            boolean isP10;
            try {
                PKCS10CertificationRequest pkcs10CertificationRequest = new PKCS10CertificationRequest(Base64.decode(p10));
                SubjectPublicKeyInfo subjectPublicKeyInfo = pkcs10CertificationRequest.getSubjectPublicKeyInfo();
                if ("RSA".equals(algTypeEnum)) {
                    // RSA
                    isP10 = pkcs10CertificationRequest.isSignatureValid((new JcaX509ContentVerifierProviderBuilder()).setProvider(new BouncyCastleProvider()).build(subjectPublicKeyInfo));
                } else {
                    // SM2
                    byte[] signature = pkcs10CertificationRequest.getSignature();
                    SM2Signer sm2Signer;
                    if (signature.length == 64) {
                        sm2Signer = new SM2Signer(PlainDSAEncoding.INSTANCE, new SM3Digest());
                    } else {
                        sm2Signer = new SM2Signer(StandardDSAEncoding.INSTANCE, new SM3Digest());
                    }
                    KeyFactory keyFact = KeyFactory.getInstance("EC", new BouncyCastleProvider());
                    BCECPublicKey p10Pub = (BCECPublicKey) keyFact.generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));

                    ECParameterSpec parameterSpec = p10Pub.getParameters();
                    ECDomainParameters domainParameters = new ECDomainParameters(parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH());
                    ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(p10Pub.getQ(), domainParameters);

                    sm2Signer.init(false, publicKeyParameters);
                    byte[] requestInfo = pkcs10CertificationRequest.toASN1Structure().getCertificationRequestInfo().getEncoded();
                    sm2Signer.update(requestInfo, 0, requestInfo.length);
                    isP10 = sm2Signer.verifySignature(signature);
                }
            } catch (Exception e) {
                isP10 = false;
            }
            if (!isP10) {
                StringBuilder stringBuilder = new StringBuilder("申请");
                stringBuilder.append(algTypeEnum);
                stringBuilder.append("证书,P10格式不正确无法正确解析");
                throw new RuntimeException(stringBuilder.toString());
            }
        }
    }

}
