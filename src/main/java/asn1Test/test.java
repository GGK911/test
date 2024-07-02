package asn1Test;

import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Random;

/**
 * @author TangHaoKai
 * @version V1.0 2024/7/2 14:23
 */
public class test {

    @Test
    @SneakyThrows
    public void asn1Object() {
        BigInteger randomBI = new BigInteger(255, new Random());
        System.out.println(randomBI);
        ASN1Object asn1Object = new ASN1Integer(randomBI);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        asn1Object.encodeTo(byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        System.out.println(Hex.toHexString(byteArray));
    }

    @Test
    @SneakyThrows
    public void asn1Integer() {
        String xCoord = "318f02b9f4a07e56696909da2eaa2680";
        ASN1Integer xCoordAsn1 = new ASN1Integer(Hex.decode(xCoord));
    }

}
