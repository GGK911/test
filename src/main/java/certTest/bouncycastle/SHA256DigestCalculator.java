package certTest.bouncycastle;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.operator.DigestCalculator;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


public class SHA256DigestCalculator implements DigestCalculator {
    private ByteArrayOutputStream bOut = new ByteArrayOutputStream();

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
    }

    @Override
    public OutputStream getOutputStream() {
        return bOut;
    }

    @Override
    public byte[] getDigest() {
        byte[] bytes = bOut.toByteArray();

        bOut.reset();

        Digest sha256 = SHA256Digest.newInstance();

        sha256.update(bytes, 0, bytes.length);

        byte[] digest = new byte[sha256.getDigestSize()];

        sha256.doFinal(digest, 0);

        return digest;
    }
}