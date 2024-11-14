package certTest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
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

        // rsa2048
        String P10 = "MIICVTCCAT0CAQAwEDEOMAwGA1UEAxMFTUNTQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCC8Tx3UYGtOD/t9lKs/JKXFebk64lQmDi9kwq8xd5TzBr3nHfQodXWMzLRSj9zmNuFjqJXmNWHL82Sgw4ZdHasLrw15mMm3Q6SszhwDV98yFX+8LV2aJYiRZFLTHNfZoqsW92VxY8QjONtgy4bL8sw9d3WaAhtVB0W94UghpsxVHzoziBnu+4qMU3nM293jj2CLTOj7cm21gIVsmEwWxRj9Vppm0Qew7W/UR61kXCjXZTElAqFhT0PIuF2y+UePDEYTep1c4haHAGehQm3PKvjaUq77eLJYt1MCE353CX0TklP3UMpHG11WySqgYlXdW5cHOfbKeL/cLy7hWTE0/CxAgMBAAGgADANBgkqhkiG9w0BAQsFAAOCAQEAMwOInIj6RgViE/YtudpOVeB5CdmrRbA5Fzo6WMwbEcQwiQhU2jZMaa8U6AAsZn3cMztDqVDUvpErr9ufg7YIU6MrXp++1f51GBc+ruDD3hfcgDwPRn3H1tKyXUaSN3l0x/AtBRABNkXe+l+r49Wj73IsIcjdhufhhTDy8TpaxJ3Q6aNJ1OnDULCNKxwX4Z2odfefHE7MRAtrbI0nNY5EG6+OvQp/32N4Nj2P4110bUxt6YFBHDNKMR0Dxb02sNT7TAYRL0+TCxeGfCw/2fMzmR5sLR1eEkXRoJ10VmN6QFhZGo+hFhBn4ni6wJx4b+eOxea9XRuyr6R409rmgLoi2g==";
        // cfca
        P10 = "MIIDxjCCAq4CAQAwPjEYMBYGA1UEAwwPY2VydFJlcXVpc2l0aW9uMRUwEwYDVQQKDAxDRkNBIFRFU1QgQ0ExCzAJBgNVBAYTAkNOMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAywS1xj2opzbCDpRkINsgwLlbufJs9ly7qmzxoylbnBRPINJpEk3pF1+fu/JJwM6/3fctSVFDeK4PzEEcXgi+2h6TYOtJH2RPR10FZB3lIjH1HRUAYr/xgrqm0GzOT5kqeXSKCu8gKY8Eh6ufGyQrbe5tr8Lp1/hEbN5qOwOSDMae+ThGbgpfwwBxazWW7yN6b1u46E+asZXnLDNDzFQnoxBTR3LbWugmQfuXcUOHHxwxqHNAgChzYERISv2VOVQwUqxKGV2vN+tIH9DbQ++FTgm8g4eONM797kc4UvauNvmD00Lb0TZRB+HTfmcigrOonzfyLgdQpeGw9nyA3+wloQIDAQABoIIBQTATBgkqhkiG9w0BCQcTBjExMTExMTCCASgGCSqGSIb3DQEJPwSCARkwggEVAgEBBIIBDjCCAQoCggEBAJ/sI70ac8EB7732B0e1CdYT1SG6/giJGAMhM8qBnlzGRWcSybEF1w99vCduLq5R9hWeex7h3zDwajGFjsVAl/VnUb5amvIR2/XZnUuUIk8UtFdwPg1ij80zG1caoT6BSMPbp5cEbYUawdCVjJ2yJNFJ4Z/ukAeife0g9sSwCFuzwcBM651tJrFlX9FhEMZe7BOkTRWqhhtkxTVZ7EjLdTgUlkTkTBogDVK2zJF38Kt2IU0qlaMhrX2XVfY81g2H4RuRS+/2vHkWvoOeJPWt57GwRmZF4B4Dyko/e+ggi3Apb5FmM+q1ke9LRCn7NW9QwwsiubxyEAPiK3CMECNbfe8CAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAJvk/ftBNyIBBzWRRk928dTj1wAp9p8CTkLSBq1BXUkijgaUejeQb+urxZZyiv2lgTjb1OVlXzskaLhu9fBBkJmP8j3qVawBVJ9t0IKja3rRL+i5AHYjCDtl24c+Ju8xEOOAk9YxOzVUFAcrtgXaTKCwz4zsJzjDkP6p6tRYLdp1pv+hKfSvyfERYx1eSCabs5xDhSuAOiqW3S0cNNVeV0Z54JWFGOy6W8D61lFlXj36pj1v8KgIUEV97xSd4h4xA4U4+4HeqhXRhsW92t6ifR4KBe1Lk/nbWdqC6n7qb8vLJN4lIgsqJtnZ1Ee4nIFfaaYu3CjPYLKEDGDLX86IP2w==";
        getPubCfca(Base64.decode(P10));

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

    public static PublicKey getPubCfca(byte[] csrBytes) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ASN1Sequence certRequest = ASN1Sequence.getInstance(csrBytes);
        ASN1Sequence certRequestAInfo = ASN1Sequence.getInstance(certRequest.getObjectAt(0));
        ASN1Sequence subjectPKInfo = ASN1Sequence.getInstance(certRequestAInfo.getObjectAt(2));
        ASN1Sequence algIdentifier = ASN1Sequence.getInstance(subjectPKInfo.getObjectAt(0));
        ASN1ObjectIdentifier instance = ASN1ObjectIdentifier.getInstance(algIdentifier.getObjectAt(0));
        if (instance.toString().equalsIgnoreCase("1.2.840.113549.1.1.1")) {
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(subjectPKInfo);
            KeyFactory keyFact = KeyFactory.getInstance("RSA", BC);
            return keyFact.generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        } else {
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(subjectPKInfo);
            KeyFactory keyFact = KeyFactory.getInstance("EC", BC);
            return keyFact.generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        }
    }

}
