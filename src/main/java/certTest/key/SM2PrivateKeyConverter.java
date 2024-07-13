package certTest.key;

/**
 * @author TangHaoKai
 * @version V1.0 2024/5/15 11:52
 */
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Security;

public class SM2PrivateKeyConverter {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {
        // 示例私钥，假设它是一个包含完整SM2曲线参数的ASN.1结构
        String data = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIGFYOgDQ0HXmSv/rQPuw3WiYImnO8CUwBq0t1Mrb4MHloIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        // data = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIFVHQit4CxN1xEssh7zqPAK0NhE+KGIiZRKH+tKiMPNYoIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
        byte[] originalPrivateKey = Base64.decode(data); // 你的私钥字节数组

        // 解析私钥
        ASN1InputStream asn1InputStream = new ASN1InputStream(new ByteArrayInputStream(originalPrivateKey));
        ASN1Primitive asn1Primitive = asn1InputStream.readObject();
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(asn1Primitive);

        // 获取私钥参数
        ECPrivateKeyParameters ecPrivateKeyParameters = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(privateKeyInfo);

        // 创建新的私钥信息，只包含SM2的OID标识
        ASN1EncodableVector vector = new ASN1EncodableVector();
        vector.add(new ASN1Integer(1));
        vector.add(new DEROctetString(ecPrivateKeyParameters.getD().toByteArray()));
        DERTaggedObject taggedObject = new DERTaggedObject(true, 0, new ASN1ObjectIdentifier("1.2.156.10197.1.301"));
        DERTaggedObject taggedObject1 = new DERTaggedObject(true, 1, new DERBitString(Base64.decode("RkoqC/RZjmedSuMUmlQ/y4TTmeYTHHyT1okM0KW67wkn/ys/VmXRnuFIzy6thS98Xk4m/+JgCVa/NDnUz8J5hQ==")));
        vector.add(taggedObject);
        vector.add(taggedObject1);

        ASN1Sequence privateKeySequence = new DERSequence(vector);

        // 构造新的PrivateKeyInfo对象，只包含SM2的OID标识
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new ASN1ObjectIdentifier("1.2.156.10197.1.301"));
        PrivateKeyInfo newPrivateKeyInfo = new PrivateKeyInfo(algorithmIdentifier, privateKeySequence);

        // 输出新的私钥字节数组
        byte[] newPrivateKey = newPrivateKeyInfo.getEncoded();

        // 打印新的私钥
        System.out.println("新的私钥ASN.1结构:");
        for (byte b : newPrivateKey) {
            System.out.printf("%02X", b);
        }
        System.out.println();
        System.out.println(Base64.toBase64String(newPrivateKey));
    }
}

