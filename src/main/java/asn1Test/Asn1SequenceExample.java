package asn1Test;


import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author TangHaoKai
 * @version V1.0 2024/6/23 11:48
 */
public class Asn1SequenceExample {

    public static void main(String[] args) {
        try {
            // 创建两个 ASN1Integer 对象
            ASN1Integer asn1Int1 = new ASN1Integer(123);
            ASN1Integer asn1Int2 = new ASN1Integer(456);

            // 将两个 ASN1Integer 对象加入 ASN1EncodableVector
            ASN1EncodableVector vector = new ASN1EncodableVector();
            vector.add(asn1Int1);
            vector.add(asn1Int2);

            // 创建 ASN1Sequence
            ASN1Sequence sequence = new DERSequence(vector);

            // 编码为字节数组
            byte[] encoded = sequence.getEncoded();

            // 输出结果
            System.out.println("ASN1 Sequence: " + Hex.toHexString(encoded));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
