package asn1Test;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/3 14:26
 */
public class ASN1Parser {

    public static void main(String[] args) {
        String hexString = "30450220803fc5d4d57ee936a90be2c7791d151b9c9ab02dde3194898d08585283a922a302205870c95ec66df687aed699bc13ef60b749b358a002517b2cbcf07f2f8d0c29be00";
        // hexString = "3045022100803fc5d4d57ee936a90be2c7791d151b9c9ab02dde3194898d08585283a922a302205870c95ec66df687aed699bc13ef60b749b358a002517b2cbcf07f2f8d0c29be";
        // hexString = "022100803fc5d4d57ee936a90be2c7791d151b9c9ab02dde3194898d08585283a922a3";
        byte[] data = Hex.decode(hexString);

        try {
            parseASN12(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ASN.1 structure is invalid: " + e.getMessage());
        }
    }

    public static int parseASN1(byte[] data, int startIndex) {
        int index = startIndex;

        if (index >= data.length) {
            throw new IllegalArgumentException("Index out of bounds");
        }

        // Parse Tag
        int tag = data[index] & 0xFF;
        index++;

        String tagName = getTagName(tag);
        System.out.println("Tag: " + tag + " (" + tagName + ")");

        // Parse Length
        int length = data[index] & 0xFF;
        index++;

        // If length is more than 127, it's in long form
        if (length > 127) {
            int numBytes = length & 0x7F;
            length = 0;
            for (int i = 0; i < numBytes; i++) {
                length = (length << 8) | (data[index] & 0xFF);
                index++;
            }
        }

        System.out.println("Length: " + length);

        // Parse Value
        if (index + length > data.length) {
            throw new IllegalArgumentException("Length exceeds data size");
        }
        byte[] value = Arrays.copyOfRange(data, index, index + length);
        index += length;

        System.out.println("Value: " + Hex.toHexString(value));

        // If tag is a constructed type, parse inner elements
        if ((tag & 0x20) != 0) {
            int innerIndex = 0;
            while (innerIndex < value.length) {
                innerIndex += parseASN1(value, innerIndex);
            }
        }

        return index - startIndex;
    }

    public static void parseASN12(byte[] data) throws Exception {
        int index = 0;

        // Parse SEQUENCE
        if (data[index] != 0x30) {
            throw new IllegalArgumentException("Expected SEQUENCE (0x30), but found: " + data[index]);
        }
        index++;

        // Get SEQUENCE length
        int seqLength = data[index];
        index++;

        System.out.println("SEQUENCE Length: " + seqLength);

        // Parse first INTEGER
        if (data[index] != 0x02) {
            throw new IllegalArgumentException("Expected INTEGER (0x02), but found: " + data[index]);
        }
        index++;

        // Get first INTEGER length
        int int1Length = data[index];
        index++;

        System.out.println("First INTEGER Length: " + int1Length);

        byte[] int1Value = Arrays.copyOfRange(data, index, index + int1Length);
        index += int1Length;

        System.out.println("First INTEGER Value: " + Hex.toHexString(int1Value));

        int1Value = ensurePositiveInteger(int1Value);
        BigInteger bigIntegerR = new BigInteger(int1Value);
        ASN1Integer asn1IntegerR = new ASN1Integer(bigIntegerR);

        System.out.println("asn1IntegerR>> " + Hex.toHexString(asn1IntegerR.getEncoded()));

        // Parse second INTEGER
        if (data[index] != 0x02) {
            throw new IllegalArgumentException("Expected INTEGER (0x02), but found: " + data[index]);
        }
        index++;

        // Get second INTEGER length
        int int2Length = data[index];
        index++;

        System.out.println("Second INTEGER Length: " + int2Length);

        byte[] int2Value = Arrays.copyOfRange(data, index, index + int2Length);
        index += int2Length;

        int2Value = ensurePositiveInteger(int2Value);
        System.out.println("Second INTEGER Value: " + Hex.toHexString(int2Value));
        BigInteger bigIntegerS = new BigInteger(int2Value);
        ASN1Integer asn1IntegerS = new ASN1Integer(bigIntegerS);

        System.out.println("asn1IntegerS>> " + Hex.toHexString(asn1IntegerS.getEncoded()));


        //************************************************************************************///
        // 将两个 ASN1Integer 对象加入 ASN1EncodableVector
        ASN1EncodableVector vector = new ASN1EncodableVector();
        vector.add(asn1IntegerR);
        vector.add(asn1IntegerS);

        // 创建 ASN1Sequence
        ASN1Sequence sequence = new DERSequence(vector);

        // 编码为字节数组
        byte[] encoded = sequence.getEncoded();

        // 输出结果
        System.out.println("ASN1 Sequence: " + Hex.toHexString(encoded));
    }

    // 确保整数为正数的函数
    private static byte[] ensurePositiveInteger(byte[] value) {
        if ((value[0] & 0x80) != 0) { // 检查最高位是否为1
            byte[] fixedValue = new byte[value.length + 1];
            fixedValue[0] = 0x00; // 添加前置零字节
            System.arraycopy(value, 0, fixedValue, 1, value.length);
            return fixedValue;
        }
        return value; // 如果已经是正数，直接返回
    }

    public static String getTagName(int tag) {
        switch (tag) {
            case 0x01:
                return "BOOLEAN";
            case 0x02:
                return "INTEGER";
            case 0x03:
                return "BIT STRING";
            case 0x04:
                return "OCTET STRING";
            case 0x05:
                return "NULL";
            case 0x06:
                return "OBJECT IDENTIFIER";
            case 0x0C:
                return "UTF8String";
            case 0x10:
                return "SEQUENCE";
            case 0x11:
                return "SET";
            case 0x13:
                return "PrintableString";
            case 0x16:
                return "IA5String";
            case 0x17:
                return "UTCTime";
            case 0x30:
                return "SEQUENCE (constructed)";
            case 0x31:
                return "SET (constructed)";
            default:
                return "UNKNOWN";
        }
    }
}
