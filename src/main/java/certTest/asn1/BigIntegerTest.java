package certTest.asn1;

import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/13 15:53
 */
public class BigIntegerTest {
    @Test
    @SneakyThrows
    public void Test() {
        BigInteger bigInteger = new BigInteger(Hex.decode("00695DC38F417468BFFD3BE62423E0DC7B1EFA9CFABEF3793FB0208F29DD3E67"));
        System.out.println(String.format("%-16s", "bigInteger>> ") + Hex.toHexString(bigInteger.toByteArray()));

        BigInteger bigInteger2 = new BigInteger(1, Hex.decode("00695DC38F417468BFFD3BE62423E0DC7B1EFA9CFABEF3793FB0208F29DD3E67"));
        System.out.println(String.format("%-16s", "bigInteger2>> ") + Hex.toHexString(bigInteger2.toByteArray()));

        BigInteger bigInteger3 = new BigInteger(Hex.decode("695DC38F417468BFFD3BE62423E0DC7B1EFA9CFABEF3793FB0208F29DD3E6700"));
        System.out.println(String.format("%-16s", "bigInteger3>> ") + Hex.toHexString(bigInteger3.toByteArray()));

        BigInteger bigInteger4 = new BigInteger(-1, Hex.decode("695DC38F417468BFFD3BE62423E0DC7B1EFA9CFABEF3793FB0208F29DD3E67"));
        System.out.println(String.format("%-16s", "bigInteger4>> ") + Hex.toHexString(bigInteger4.toByteArray()));

        BigInteger bigInteger5 = new BigInteger("00695DC38F417468BFFD3BE62423E0DC7B1EFA9CFABEF3793FB0208F29DD3E67", 16);
        System.out.println(String.format("%-16s", "bigInteger5>> ") + Hex.toHexString(bigInteger5.toByteArray()));


    }
}
