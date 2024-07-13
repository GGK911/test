package certTest.pkiCoreTest;

import cn.com.mcsca.pki.core.bouncycastle.jce.provider.BouncyCastleProvider;
import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Base64;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author TangHaoKai
 * @version V1.0 2024/4/12 16:42
 */
public class PkiBaseJARTest {

    @SneakyThrows
    public static void main(String[] args) {
        String pub = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkTiO1JTaZKmSPON9kSG92pJfc873JSLJpb6wyA2bkwEDao9HjJLiCIZ+4RG4KWWpCWyL2Yql4C4tKqC7znnk6GV+nnq9sCTyIB8yGEoDxWslHaDyu2BhgfNyCe4F8tdzad/iGMyTlw1vKZj618Cd+L4uGPgQVb52OwK2WyOKAx4JM5K+kE+miwZC9+2dPQrgpYJnhRR2w0qIPNjOg45ouhTnNQ5XyWNM/0SOEkarb2HV0/H3ikiewDDzk/hYdpWTOTXB50hOSlB3NtXsv0Q4LMZMigFM+CBw/Kb4RPa9bb01zcM7Og86l1qt+StWb7Zk7gDagTrh+WlIcBtm0j7pTwIDAQAB";

        KeyFactory kf = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
        PublicKey aPublic = kf.generatePublic(new X509EncodedKeySpec(Base64.decode(pub)));
        Cipher cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());

    }
}
