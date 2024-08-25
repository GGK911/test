package PkcsTest.CA互认;

import certTest.KeyUtil2;
import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Base64;
import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Hex;
import cn.com.mcsca.pki.core.util.CertRequestUtil;
import cn.com.mcsca.pki.core.util.CertUtil;
import cn.com.mcsca.pki.core.util.KeyUtil;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

/**
 * 这里将证书，私钥，全放本地，目录结构：
 * ROOT_PATH/
 * ├── PRI/
 * │   ├── ORI-PIN.key
 * │   ├── 摘要处理1后的字符串(PIN)/
 * │   │   └── PIN.key
 * │   └── 摘要处理1后的字符串(PIN2)/
 * │       └── PIN2.key
 * └── certs/
 * x   ├── 摘要处理1后的字符串(PIN)/
 * x   │   ├── SINGLE,SN1/
 * x   │   │   └── SN1.cer
 * x   │   └── DOUBLE,SN2/
 * x   │       ├── SN2.cer
 * x   │       ├── SN3.key
 * x   │       └── SN3.cer
 * x   └── 摘要处理1后的字符串(PIN2)/
 * x       ├── SINGLE,SN1/
 * x       │   └── SN1.cer
 * x       └── DOUBLE,SN2/
 * x           ├── SN2.cer
 * x           ├── SN3.key
 * x           └── SN3.cer
 *
 * @author TangHaoKai
 * @version V1.0 2024/8/21 16:31
 */
public class ScapCertificateSource3 {
    private String ROOT_PATH;
    private static final String PRI_FILE_PATH = "PRI\\";
    private static final String CERTS_PATH = "CERTS\\";

    public ScapCertificateSource3(String root) {
        ROOT_PATH = root;
    }

    public static void main(String[] args) {
        ScapCertificateSource3 scapCertificateSource2 = new ScapCertificateSource3("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\PkcsTest\\CA互认\\");
        // 检查pin码，获取私钥及公钥
        String priAndPub = scapCertificateSource2.checkPinCode("654322", "sm2256");
        System.out.println(String.format("%-16s", "priAndPub>> ") + priAndPub);

    }

    /**
     * 检查PIN码（如果没有PIN码工具certType在 摘要处理1后的字符串(PIN)目录 下生成PIN.key）
     *
     * @param PIN      pin码
     * @param certType 要操作证书类型
     * @return 私钥和公钥
     */
    public String checkPinCode(String PIN, String certType) {
        // 找pin目录
        byte[] aesKey = pinToKey(PIN);
        String hexString = Hex.toHexString(aesKey);
        File priDirFile = new File(ROOT_PATH, PRI_FILE_PATH);
        if (!priDirFile.exists()) {
            if (!priDirFile.mkdir()) {
                System.out.println("创建priDir目录异常");
                throw new RuntimeException("创建priDir目录异常");
            }
        }
        File pinDirFile = new File(priDirFile, hexString);
        if (!pinDirFile.exists()) {
            if (!pinDirFile.mkdir()) {
                System.out.println("创建pinDirFile目录异常");
                throw new RuntimeException("创建pinDirFile目录异常");
            }
            // 不存在此PIN码
            if (certType.isEmpty()) {
                throw new IllegalArgumentException("certType为空，不存在现有PIN码，无法操作");
            }
            // 生成私钥
            String alg = certType.substring(0, 3);
            String keyLength = certType.substring(3);
            KeyPair keyPair = KeyUtil.generateKeyPair(alg, Integer.parseInt(keyLength));
            PrivateKey aPrivate = keyPair.getPrivate();
            PublicKey aPublic = keyPair.getPublic();
            String priBase64 = Base64.toBase64String(aPrivate.getEncoded());
            String pubBase64 = Base64.toBase64String(aPublic.getEncoded());
            String priAndPub = certType + "," + priBase64 + "," + pubBase64;
            // 用pin码加密priAndPub
            // DESede
            SecretKey secretKey = new SecretKeySpec(aesKey, "DESede");
            // 对称加密
            byte[] encrypt = DESencrypt(priAndPub.getBytes(StandardCharsets.UTF_8), secretKey);
            // 验证下
            // System.out.println("验证下>> " + new String(DESdecrypt(encrypt, secretKey), StandardCharsets.UTF_8));
            File pinPriFile = new File(pinDirFile, "PIN.key");
            write(pinPriFile.getParentFile(), pinPriFile.getName(), encrypt);
            return priAndPub;
        } else {
            // 存在此PIN码
            File pinPriFile = new File(pinDirFile, "PIN.key");
            byte[] encData = read(pinPriFile.getParentFile(), pinPriFile.getName());
            // 用pin码解密priAndPub
            // DESede
            SecretKey secretKey = new SecretKeySpec(aesKey, "DESede");
            byte[] decrypt = new byte[0];
            try {
                decrypt = DESdecrypt(encData, secretKey);
            } catch (Exception e) {
                throw new IllegalArgumentException("输入PIN错误");
            }
            String priAndPub = new String(decrypt);
            String keyType = priAndPub.split(",")[0];
            if (!certType.isEmpty() && !keyType.equalsIgnoreCase(certType)) {
                throw new IllegalArgumentException("要操作的密钥类型跟现有密钥类型不一致");
            }
            // System.out.println("解密>> " + priAndPub);
            return priAndPub;
        }
    }

    //**********************************************************************//

    private static byte[] md5(String pin) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("摘要异常" + e.getMessage());
        }
        byte[] hashInBytes = md.digest(pin.getBytes(StandardCharsets.UTF_8));
        return hashInBytes;
    }

    private static byte[] pinToKey(String pin) {
        // 摘要一下
        byte[] hashInBytes = md5(pin);
        // 将摘要作为密钥（这里位数不足，循环填充）
        // 填充24位的DESede
        byte[] aesKey = new byte[24];
        fillArray(aesKey, hashInBytes);
        return aesKey;
    }

    public static void fillArray(byte[] A, byte[] B) {
        int lengthB = B.length;
        int lengthA = A.length;

        // 逐个填充A
        for (int i = 0; i < lengthA; i++) {
            A[i] = B[i % lengthB];
        }
    }

    private static byte[] DESencrypt(byte[] text, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(text);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException("加密异常" + e.getMessage());
        }
    }

    private static byte[] DESdecrypt(byte[] encryptedText, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encryptedText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException("解密异常" + e.getMessage());
        }
    }

    private static byte[] genP10(String priAndPub) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        final String[] priAndPubArray = priAndPub.split(",");
        final String certType = priAndPubArray[0];
        final String priBase64 = priAndPubArray[1];
        final String pubBase64 = priAndPubArray[2];
        String alg;
        if (certType.toUpperCase().contains("RSA")) {
            alg = "SHA1withRSA";
        } else {
            alg = "SM3withSm2";
        }
        final PublicKey aPublic = certTest.KeyUtil.parsePubKey(pubBase64);
        final PrivateKey aPrivate = certTest.KeyUtil.parsePriKey(priBase64);
        String subjectParam = "CN=MCSCA";
        return Base64.decode(CertRequestUtil.generateP10(alg, subjectParam, new KeyPair(aPublic, aPrivate)));
    }

    private static Map<String, Object> parseCert(String certificateBase64) {
        Map<String, Object> parseCertMap = new HashMap<>();
        byte[] certBytes = Base64.decode(certificateBase64);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(certBytes);
        java.security.cert.X509Certificate x509Certificate = CertUtil.getX509Certificate(inputStream);
        parseCertMap.put("notBefore", x509Certificate.getNotBefore());
        parseCertMap.put("notAfter", x509Certificate.getNotAfter());
        try {
            parseCertMap.put("certEncode", Base64.toBase64String(x509Certificate.getEncoded()));
        } catch (CertificateEncodingException e) {
            throw new RuntimeException("getEncoded异常" + e.getMessage());
        }
        parseCertMap.put("serialNumber", Hex.toHexString(x509Certificate.getSerialNumber().toByteArray()));
        parseCertMap.put("issuerDN", x509Certificate.getIssuerDN().toString());
        String subjectDN = x509Certificate.getSubjectDN().toString();
        parseCertMap.put("subjectDN", subjectDN);
        parseCertMap.put("publicKey", Base64.toBase64String(x509Certificate.getPublicKey().getEncoded()));
        String sigAlgName = x509Certificate.getSigAlgName();
        if (sigAlgName.toLowerCase().contains("sm2")) {
            parseCertMap.put("certType", "SM2");
        } else {
            int keyLength = KeyUtil2.getKeyLengthFromPublicKey(x509Certificate.getPublicKey());
            parseCertMap.put("certType", "RSA" + keyLength);
        }
        parseCertMap.put("subjectCN", getCNFromDN(subjectDN));

        parseCertMap.put("certBase64", certificateBase64);
        return parseCertMap;
    }

    /**
     * 从证书DN中获取到CN项
     *
     * @param DN DN字符串
     * @return CN项
     */
    public static String getCNFromDN(String DN) {
        X500Name x500Name = new X500Name(DN);
        return getCNFromX500Name(x500Name);
    }

    /**
     * 从证书DN中获取到CN项
     *
     * @param subject X500Name对象
     * @return CN项
     */
    public static String getCNFromX500Name(X500Name subject) {
        RDN[] rdNs = subject.getRDNs(BCStyle.CN);
        if (rdNs.length > 0) {
            return rdNs[0].getFirst().getValue().toString();
        }
        return subject.toString();
    }

    public static void write(File dir, String fileName, byte[] content) {
        if (dir != null) {
            File file = new File(dir, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("写入文件异常");
            }
        }
    }

    public static byte[] read(File dir, String fileName) {
        if (dir == null) {
            return null;
        }
        File file = new File(dir, fileName);
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
