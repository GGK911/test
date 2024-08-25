package coorSign;

import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/6 9:27
 */
public class thresholdSM2 {
    private static final ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
    private static final BigInteger n;
    private static final ECCurve sm2Curve;
    private static final ECPoint gPoint;

    static {
        sm2Curve = ecSpec.getCurve();
        gPoint = ecSpec.getG();
        n = sm2Curve.getOrder();
    }

    @Test
    @SneakyThrows
    public void test() {
        BigInteger d2, d4, d6;
        BigInteger d1, d5;
        BigInteger d3, d7, t1;
        BigInteger w1, w2, w3;
        BigInteger t2, t4, t5, t6, t7;
        BigInteger t, T, t3;
        BigInteger k1, k2, k3;
        BigInteger s2, s3;

        BigInteger a = sm2Curve.getA().toBigInteger();
        BigInteger b = sm2Curve.getB().toBigInteger();

        BigInteger p = sm2Curve.getField().getCharacteristic();
        BigInteger gx = gPoint.getXCoord().toBigInteger();
        BigInteger gy = gPoint.getYCoord().toBigInteger();

        // 提取设备信息并生成设备因子d2、d4、d6
        d2 = new BigInteger("12336A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0", 16);
        d4 = new BigInteger("345736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E5210A0", 16);
        d6 = new BigInteger("12336A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52130A0", 16);
        // 提示用户输入PIN 码并生成PIN 码因子d1、d5
        d1 = generateRandomKeyInRange();
        d5 = generateRandomKeyInRange();


        // 客户方的部分私钥
        BigInteger D1;
        while (true) {
            // 产生随机数(有验证，不成功重新生成)
            d3 = generateRandomKeyInRange();
            d7 = generateRandomKeyInRange();
            t1 = generateRandomKeyInRange();
            BigInteger t1Inv = t1.modInverse(n);

            D1 = d1.add(d2.pow(2))
                    .add(d3.multiply(t1).multiply(t1Inv).multiply(d4.add(d5.multiply(d6))))
                    .add(d7.multiply(t1).multiply(t1Inv))
                    .mod(n);
            // D ∈ [1, n-2]
            if (D1.compareTo(BigInteger.ONE) < 0 || D1.compareTo(n.subtract(new BigInteger("2"))) > 0) {
                continue;
            }
            w1 = t1Inv;
            w2 = d3.multiply(t1);
            w3 = d7.multiply(t1);
            break;
        }
        // 提取设备信息并生成设备因子 t2、t4、t6；采用步骤【2】输入的PIN 码并生成PIN 码因子 t5；产生随机数 t7；
        t2 = generateRandomKeyInRange();
        t4 = generateRandomKeyInRange();
        t5 = generateRandomKeyInRange();
        t6 = generateRandomKeyInRange();
        t7 = generateRandomKeyInRange();

        BigInteger t7Inv = t7.modInverse(n);

        t = D1.modInverse(n);
        T = t.subtract(t7Inv.multiply(w3))
                .subtract(d1.add(d2.pow(2)))
                .subtract(w1.multiply(w3));
        t3 = T.subtract(w1.multiply(w2).multiply(d4.add(d5.multiply(d6))));

        System.out.println("t7Hex>> " + Hex.toHexString(t7.toByteArray()));
        System.out.println("t3Hex>> " + Hex.toHexString(t3.toByteArray()));

        //***********************************************************************//

        BigInteger D2;
        while (true) {
            // 产生随机数(有验证，不成功重新生成)
            d3 = generateRandomKeyInRange();
            d7 = generateRandomKeyInRange();
            t1 = generateRandomKeyInRange();
            BigInteger t1Inv = t1.modInverse(n);

            D2 = d1.add(d2.pow(2))
                    .add(d3.multiply(t1).multiply(t1Inv).multiply(d4.add(d5.multiply(d6))))
                    .add(d7.multiply(t1).multiply(t1Inv));
            // D ∈ [1, n-2]
            if (D2.compareTo(BigInteger.ONE) < 0 || D1.compareTo(n.subtract(new BigInteger("2"))) > 0) {
                continue;
            }
            w1 = t1Inv;
            w2 = d3.multiply(t1);
            w3 = d7.multiply(t1);
            break;
        }
        // 提取设备信息并生成设备因子 t2、t4、t6；采用步骤【2】输入的PIN 码并生成PIN 码因子 t5；产生随机数 t7；
        t2 = generateRandomKeyInRange();
        t4 = generateRandomKeyInRange();
        t5 = generateRandomKeyInRange();
        t6 = generateRandomKeyInRange();
        t7 = generateRandomKeyInRange();

        t7Inv = t7.modInverse(n);

        t = D2.modInverse(n);
        T = t.subtract(t7Inv.multiply(w3))
                .subtract(d1.add(d2.pow(2)))
                .subtract(w1.multiply(w3));
        t3 = T.subtract(w1.multiply(w2).multiply(d4.add(d5.multiply(d6))));

        System.out.println("t7Hex>> " + Hex.toHexString(t7.toByteArray()));
        System.out.println("t3Hex>> " + Hex.toHexString(t3.toByteArray()));

        //***********************************************************************//

        // 签名
        BigInteger r;
        ECPoint Q1;
        byte[] e = "1234567812345678".getBytes(StandardCharsets.UTF_8);
        while (true) {
            k1 = generateRandomKeyInRange();
            k2 = generateRandomKeyInRange();
            k3 = generateRandomKeyInRange();
            Q1 = gPoint.multiply(k1);
            ECPoint x1y1point = Q1.multiply(k2).add(gPoint.multiply(k3));
            BigInteger x1 = x1y1point.getXCoord().toBigInteger();
            r = x1.add(new BigInteger(e));
            // thk's todo 2024/8/6 10:08 这里的r+k不知道是哪个k？
            if (r.compareTo(BigInteger.ZERO) == 0 || r.add(k1).compareTo(n) == 0) {
                continue;
            }
            break;
        }
        s2 = D2.multiply(k2);
        s3 = D2.multiply(r.add(k3));

        BigInteger A = d1.multiply(s3).add(d2.pow(2).multiply(s3));
        BigInteger M = A.add(s3.multiply(w1.multiply(w2.multiply(d4).add(w3).add(w2.multiply(d5).multiply(d6)))));
        BigInteger B = d1.multiply(k1).add(d2.pow(2).multiply(k1));
        BigInteger N0 = B.add(k1.multiply(w1.multiply(w2.multiply(d4).add(w3).add(w2.multiply(d5).multiply(d6)))));
        BigInteger N = N0.multiply(s2);
        BigInteger s = M.add(N).subtract(r);

        ASN1Integer rInteger = new ASN1Integer(r);
        ASN1Integer sInteger = new ASN1Integer(s);
        // 将两个 ASN1Integer 对象加入 ASN1EncodableVector
        ASN1EncodableVector vector = new ASN1EncodableVector();
        vector.add(rInteger);
        vector.add(sInteger);

        // 创建 ASN1Sequence
        ASN1Sequence sequence = new DERSequence(vector);

        // 编码为字节数组
        byte[] encoded = sequence.getEncoded();

        // 输出结果
        System.out.println("RS>> " + Hex.toHexString(encoded));


    }

    private static BigInteger generateRandomKeyInRange() {
        // Generate random number in the range [1, n-1]
        return new BigInteger(n.bitLength(), new SecureRandom()).mod(n).add(BigInteger.ONE);
    }

}
