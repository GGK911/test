package certTest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * 证书请求工具类
 *
 * @author TangHaoKai
 * @version V1.0 2024/8/19 16:34
 */
public class CsrUtil {
    private static final Provider BC = new BouncyCastleProvider();

    public static void main(String[] args) throws Exception {
        // sm2
        String csr = "MIIBQTCB6QIBADCBhjEPMA0GA1UECAwG6YeN5bqGMQ8wDQYDVQQHDAbph43luoYxIjAgBgkqhkiG9w0BCQEWEzEzOTgzMDUzNDU1QDE2My5jb20xCzAJBgNVBAYTAkNOMQ8wDQYDVQQKEwZHR0s5MTExDzANBgNVBAsTBkdHSzkxMTEPMA0GA1UEAxMGR0dLOTExMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEAcTR849FfyNHT+z8uSnw+WnKCXGchlSrYd+/nSVzf3UUrGq8ial7/CrL2zIeeLko2CK0O02hxcSJt3uIJDBrJaAAMAoGCCqBHM9VAYN1A0cAMEQCIAIxfm9mqruy42389VWs6ykfamTEkz0B1/NRQimgTH8lAiBcF9tmGmrHvqZWLswJTa/JSOABXgGO1fs5R/byiYOAjg==";
        // rsa
        csr = "MIICVTCCAT0CAQAwEDEOMAwGA1UEAxMFTUNTQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCag9sRS+sKbJ3O+XnRnVi2bmzpfnNugXhoSaOQ5StorZFaVP2I+IyQZ2x/Zsgy6v9hQ9eq9ENkAC4dtCU6XpMZ3ddm4ZLnWINJzo65jEtPsHaWEBnAUNUCLbHCALCs7Zj3x5DUnIdz+jsjXr+SGUuS9e3e3oH8BpmcAD8ShfTTRUpAV2UDCHukpsKN6BBx12F5tKdrrX2FTdQiC+G4LAFBlC73a0CUu8LWBeJTcSzuV0Prw6/14yts07p0pg8SBYDnOWyfvdp/gBwBmCjPnee5oLsE0lqPURmz29zAYqKyEjoPzwGOPcoOFMK9yXRFFrreZoKYzvAQs7gpepoHMreVAgMBAAGgADANBgkqhkiG9w0BAQsFAAOCAQEAlXgGUb8NWX/JwQpzxlQRz79QUsVLqNwdp3fpgsp2nvyoqyRqYcHmUEYnDvhAvg0l/ixBDkb5amxPKqN5P643/swxCZJDddR63cgtX9lnLfwR0Qs3A8OVJYRZJ7trJgyR5NhVFTRP1uuncPC86tCbkfj1CYqMAIAyftLqogzBI+MtpHknSn8PKXPbcawUL0JcnmdRmBNb5DI512sC5U3+yup5KRF0bQlhYvk7oFlQpvtS/6UWPJ76U8s3ssetTQPjsOMshUv0kUNxEdvsO9ClQNLmHvmmN/MQSRqcyIqLQvw/JzAJVbvHK7LrpFxT5H6t2XcPBFXAGYAs9PS8mQT2Ag==";
        String alg = getAlgFromCSR(Base64.decode(csr));
        System.out.println(String.format("%-16s", "alg>> ") + alg);
    }

    public static String getAlgFromCSR(byte[] csrBytes) throws IOException {
        ASN1Sequence csrSequence = ASN1Sequence.getInstance(csrBytes);
        ASN1Sequence oidSequence = ASN1Sequence.getInstance(csrSequence.getObjectAt(1));
        ASN1ObjectIdentifier objectIdentifier = ASN1ObjectIdentifier.getInstance(oidSequence.getObjectAt(0));
        String objectIdentifierString = objectIdentifier.toString();
        // System.out.println(objectIdentifierString);
        if (objectIdentifierString.equals("1.2.840.113549.1.1.11")) {
            return "RSA";
        } else if (objectIdentifierString.equals("1.2.156.10197.1.501")) {
            return "SM2";
        }
        return null;
    }

    public static PublicKey getPub(byte[] csrBytes) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS10CertificationRequest pkcs10CertificationRequest = new PKCS10CertificationRequest(csrBytes);
        SubjectPublicKeyInfo subjectPublicKeyInfo = pkcs10CertificationRequest.getSubjectPublicKeyInfo();
        String algFromCSR = getAlgFromCSR(csrBytes);
        KeyFactory keyFact;
        if ("SM2".equalsIgnoreCase(algFromCSR)) {
            keyFact = KeyFactory.getInstance("EC", BC);
        } else {
            keyFact = KeyFactory.getInstance("RSA", BC);
        }
        return keyFact.generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
    }

}
