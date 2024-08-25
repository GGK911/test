package coorSign;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.SecureRandom;


public class SM2SeparateSign {
    public static BigInteger p;
    public static BigInteger a;
    public static BigInteger b;
    public static BigInteger n;
    public static BigInteger gx;
    public static BigInteger gy;
    // 获取SM2曲线参数
    static ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("sm2p256v1");

    private static BigInteger[] sm2_param = {
            new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF", 16), // p,0
            new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC", 16), // a,1
            new BigInteger("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93", 16), // b,2
            new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123", 16), // n,3
            new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16), // gx,4
            new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16) // gy,5
    };

    public static void main(String[] args) throws Exception {
        ECPoint ecc_point_g = ecSpec.getG();
        p = sm2_param[0];
        a = sm2_param[1];
        b = sm2_param[2];
        n = sm2_param[3];
        gx = sm2_param[4];
        gy = sm2_param[5];

        try {
            BigInteger d1;
            BigInteger d2;
            BigInteger d3;
            BigInteger d4;
            BigInteger d5;
            BigInteger d6;
            BigInteger d7;
            BigInteger t1;
            BigInteger t2;
            BigInteger t3;
            BigInteger t4;
            BigInteger t5;
            BigInteger t6;
            BigInteger t7;
            BigInteger D;
            BigInteger t;

            BigInteger w1;
            BigInteger w2;
            BigInteger w3;
            BigInteger w4;
            BigInteger w5;

            BigInteger P1;
            BigInteger M;

            ECPoint P = null;

            BigInteger r = null;
            BigInteger s = null;

            while (true) {

                try {
                    // 提取设备信息并生成设备因子d2、d4、d6
                    d2 = new BigInteger("12336A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0", 16);
                    d4 = new BigInteger("345736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E5210A0", 16);
                    d6 = new BigInteger("12336A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52130A0", 16);
                    // 提示用户输入PIN 码并生成PIN 码因子d1、d5
                    d1 = generateRandomKeyInRange();
                    d5 = generateRandomKeyInRange();
                    // 产生随机数(有验证，不成功重新生成)
                    d3 = generateRandomKeyInRange();
                    d7 = generateRandomKeyInRange();
                    t1 = generateRandomKeyInRange();

                    t2 = generateRandomKeyInRange();
                    t4 = generateRandomKeyInRange();
                    t5 = generateRandomKeyInRange();
                    t6 = generateRandomKeyInRange();
                    t7 = generateRandomKeyInRange();

                    BigInteger t1Inv = t1.modInverse(n);

                    D = d1.add(d2.pow(2))
                            .add(d3.multiply(t1).multiply(t1Inv).multiply(d4.add(d5.multiply(d6))))
                            .add(d7.multiply(t1).multiply(t1Inv))
                            .mod(n);
                    // D ∈ [1, n-2]
                    if (D.compareTo(BigInteger.ONE) < 0 || D.compareTo(n.subtract(new BigInteger("2"))) > 0) {
                        System.out.println("drop");
                        continue;
                    } else {
                        w1 = t1Inv;
                        w2 = d3.multiply(t1);
                        w3 = d7.multiply(t1);
                    }

                    BigInteger t7Inv = t7.modInverse(n);
                    t = D.modInverse(n);
                    BigInteger T = t.subtract(t7Inv.multiply(w3))
                            .subtract(d1.add(d2.pow(2))
                                    .subtract(w1.multiply(w3)));

                    t3 = T.subtract(w1.multiply(w2).multiply(d4.add(d5.multiply(d6))));

                    // 拆分因子公钥生成
                    ECPoint subM1 = ecc_point_g.multiply(w3).multiply(t7Inv);
                    ECPoint subM2 = ecc_point_g.multiply(d1);
                    ECPoint subM3 = ecc_point_g.multiply(d2.pow(2));
                    ECPoint M_2 = subM1.add(subM2).add(subM3);
                    ECPoint p1 = M_2.add(ecc_point_g.multiply(t3)).add(ecc_point_g.multiply(w1.multiply(w2.multiply(d4).add(w3).add(w2.multiply(d5).multiply(d6)))));
                    ECPoint p0 = M_2.add(p1.multiply(w1.multiply(w2.multiply(d4).add(w3).add(w2.multiply(d5).multiply(d6))))).add(p1.multiply(t3));
                    ECPoint P_2 = p0.subtract(ecc_point_g);







                    P = ecc_point_g.multiply(D);

                    //计算t
                    t = D.and(new BigInteger("1")).modInverse(n);
                    // 计算t4的平方
                    BigInteger t4Squared = t4.pow(2).mod(n);
                    // 计算t5与t6的乘积
                    BigInteger t6TimesT7 = t6.multiply(t7).mod(n);
                    // 计算t4^2*(t5+t6*t7)
                    BigInteger t4SquaredTimesSum = t4Squared.multiply(t5.add(t6TimesT7)).mod(n);
                    // 计算t-t1+t4^2*(t5+t6*t7)
                    BigInteger intermediateResult = t.subtract(t1).add(t4SquaredTimesSum).mod(n);
                    // 计算t2的模逆元
                    BigInteger t2Inverse = t2.modInverse(n);
                    // 计算t3
                    t3 = intermediateResult.multiply(t2Inverse).mod(n);

                    break;

                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }


            }

            BigInteger K;
            BigInteger k;

            do {
                K = new BigInteger(n.bitLength(), new SecureRandom()).mod(n).add(BigInteger.ONE);
                k = K.multiply(w1).mod(n);
            } while (k.compareTo(n) >= 0);


            byte[] userId = "1234567812345678".getBytes();
            String msg = "message digest";
            byte[] z = Sm2GetZ(userId, P);
            System.out.println("z:" + Hex.toHexString(z));
            SM3Digest sm3 = new SM3Digest();
            sm3.update(z, 0, z.length);
            byte[] p = msg.getBytes("utf-8");
            sm3.update(p, 0, p.length);
            byte[] md = new byte[32];
            sm3.doFinal(md, 0);
            System.out.println("md:" + Hex.toHexString(md));

            BigInteger e = new BigInteger(1, md);

            ECPoint kp = ecc_point_g.multiply(k);
            r = e.add(kp.getXCoord().toBigInteger());
            r = r.mod(n);

            w4 = d1.add(d2.pow(2)).multiply(t1).mod(n);
            w5 = t2.multiply(t3).and(t4.pow(2).multiply(t5.add(t6.multiply(t7)))).mod(n);


            BigInteger temp1 = d4.add(d5.multiply(d6));
            BigInteger temp2 = t2.multiply(t3);
            BigInteger temp3 = t4.pow(2);
            BigInteger temp4 = t5.add(t6.multiply(t7));
            BigInteger temp5 = temp3.multiply(temp4);
            BigInteger temp6 = temp2.add(temp5);
            BigInteger temp7 = d1.multiply(temp6);
            BigInteger temp8 = d2.pow(2).multiply(temp6);
            BigInteger temp9 = w2.multiply(temp1);
            BigInteger temp10 = temp7.add(temp8).add(temp9).add(w3).add(w4);
            BigInteger temp11 = k.subtract(r.multiply(w1.multiply(w2.multiply(temp1).add(w3))));
            BigInteger temp12 = w5.multiply(temp11);
            BigInteger temp13 = r.multiply(temp10);
            s = K.add(temp12).subtract(temp13).mod(n);

            BigInteger R = null;
            // e_
            //BigInteger e = new BigInteger(1, md);
            // t
            BigInteger tt = r.add(s).mod(n);

            if (tt.equals(BigInteger.ZERO)) {
                return;
            }
            // x1y1
            ECPoint x1y1 = ecc_point_g.multiply(s);
            x1y1 = x1y1.add(P.multiply(tt));

            // R
            R = e.add(x1y1.getXCoord().toBigInteger()).mod(n);


            System.out.println("D:" + D.toString(16));
            System.out.println("P x1:" + P.getXCoord().toBigInteger().toString(16));
            System.out.println("P y1:" + P.getYCoord().toBigInteger().toString(16));
            System.out.println("d1:" + d1.toString(16));
            System.out.println("d2:" + d2.toString(16));
            System.out.println("d3:" + d3.toString(16));
            System.out.println("d4:" + d4.toString(16));
            System.out.println("d5:" + d5.toString(16));
            System.out.println("d6:" + d6.toString(16));
            System.out.println("d7:" + d7.toString(16));
            System.out.println("t1:" + t1.toString(16));
            System.out.println("t2:" + t2.toString(16));
            System.out.println("t3:" + t3.toString(16));
            System.out.println("t4:" + t4.toString(16));
            System.out.println("t5:" + t5.toString(16));
            System.out.println("t6:" + t6.toString(16));
            System.out.println("t7:" + t7.toString(16));
            System.out.println("w1:" + w1.toString(16));
            System.out.println("w2:" + w2.toString(16));
            System.out.println("w3:" + w3.toString(16));

            System.out.println("K:" + K.toString(16));
            System.out.println("k:" + k.toString(16));

            System.out.println("r:" + r.toString(16));
            System.out.println("s:" + s.toString(16));

            System.out.println("R:" + R.toString(16));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static BigInteger generateRandomKeyInRange() {
        // Generate random number in the range [1, n-1]
        return new BigInteger(n.bitLength(), new SecureRandom()).mod(n).add(BigInteger.ONE);
    }

    public static byte[] Sm2GetZ(byte[] userId, ECPoint userKey) {
        SM3Digest sm3 = new SM3Digest();
        int len = userId.length * 8;
        sm3.update((byte) (len >> 8 & 0xFF));
        sm3.update((byte) (len & 0xFF));
        sm3.update(userId, 0, userId.length);

        byte[] p = a.toByteArray();
        sm3.update(p, 0, p.length);

        p = b.toByteArray();
        sm3.update(p, 0, p.length);

        p = gx.toByteArray();
        sm3.update(p, 0, p.length);

        p = gy.toByteArray();
        sm3.update(p, 0, p.length);

        p = userKey.getXCoord().toBigInteger().toByteArray();
        sm3.update(p, 0, p.length);

        p = userKey.getYCoord().toBigInteger().toByteArray();
        sm3.update(p, 0, p.length);

        byte[] md = new byte[sm3.getDigestSize()];
        sm3.doFinal(md, 0);
        return md;
    }

}
