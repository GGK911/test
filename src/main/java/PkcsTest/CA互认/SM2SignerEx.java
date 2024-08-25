package PkcsTest.CA互认;


import cn.com.mcsca.pki.core.bouncycastle.crypto.CipherParameters;
import cn.com.mcsca.pki.core.bouncycastle.crypto.CryptoException;
import cn.com.mcsca.pki.core.bouncycastle.crypto.CryptoServicesRegistrar;
import cn.com.mcsca.pki.core.bouncycastle.crypto.Digest;
import cn.com.mcsca.pki.core.bouncycastle.crypto.Signer;
import cn.com.mcsca.pki.core.bouncycastle.crypto.digests.SM3Digest;
import cn.com.mcsca.pki.core.bouncycastle.crypto.params.ECDomainParameters;
import cn.com.mcsca.pki.core.bouncycastle.crypto.params.ECKeyParameters;
import cn.com.mcsca.pki.core.bouncycastle.crypto.params.ECPrivateKeyParameters;
import cn.com.mcsca.pki.core.bouncycastle.crypto.params.ECPublicKeyParameters;
import cn.com.mcsca.pki.core.bouncycastle.crypto.params.ParametersWithID;
import cn.com.mcsca.pki.core.bouncycastle.crypto.params.ParametersWithRandom;
import cn.com.mcsca.pki.core.bouncycastle.crypto.signers.DSAEncoding;
import cn.com.mcsca.pki.core.bouncycastle.crypto.signers.DSAKCalculator;
import cn.com.mcsca.pki.core.bouncycastle.crypto.signers.RandomDSAKCalculator;
import cn.com.mcsca.pki.core.bouncycastle.crypto.signers.StandardDSAEncoding;
import cn.com.mcsca.pki.core.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import cn.com.mcsca.pki.core.bouncycastle.jce.ECNamedCurveTable;
import cn.com.mcsca.pki.core.bouncycastle.jce.provider.BouncyCastleProvider;
import cn.com.mcsca.pki.core.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import cn.com.mcsca.pki.core.bouncycastle.jce.spec.ECParameterSpec;
import cn.com.mcsca.pki.core.bouncycastle.jce.spec.ECPrivateKeySpec;
import cn.com.mcsca.pki.core.bouncycastle.math.ec.ECAlgorithms;
import cn.com.mcsca.pki.core.bouncycastle.math.ec.ECConstants;
import cn.com.mcsca.pki.core.bouncycastle.math.ec.ECFieldElement;
import cn.com.mcsca.pki.core.bouncycastle.math.ec.ECMultiplier;
import cn.com.mcsca.pki.core.bouncycastle.math.ec.ECPoint;
import cn.com.mcsca.pki.core.bouncycastle.math.ec.FixedPointCombMultiplier;
import cn.com.mcsca.pki.core.bouncycastle.util.BigIntegers;
import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Base64;
import cn.com.mcsca.pki.core.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/13 9:49
 */
public class SM2SignerEx implements Signer, ECConstants {
    private static final Provider BC = new BouncyCastleProvider();

    public static void main(String[] args) throws Exception {
        byte[] message = "abc".getBytes(StandardCharsets.UTF_8);
        // message = Hex.decode("66c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0");

        System.out.println("messageHEX>> " + Hex.toHexString(message));
        KeyFactory keyFact = KeyFactory.getInstance("EC", BC);
        BCECPrivateKey privateKey = (BCECPrivateKey) keyFact.generatePrivate(new PKCS8EncodedKeySpec(Hex.decode("308193020100301306072a8648ce3d020106082a811ccf5501822d0479307702010104202b3818d2c18042571bf1c5036633c57a8f7db85e3594c2a034c7b0d56bba8193a00a06082a811ccf5501822da1440342000403c97f54590b52fbd3d4e7beebf6e8b777e774c908ebf845875bf254bdbfdef0daa317e184ee64f65712f1b6d5bb98ae4c9371e43364791441d7712ee79e7f9a")));
        privateKey = parsePrivateFromD();
        // privateKey = (BCECPrivateKey) sm2_demo.privateKey;

        ECParameterSpec parameterSpec = privateKey.getParameters();
        ECDomainParameters domainParameters = new ECDomainParameters(parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH());

        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(privateKey.getD(), domainParameters);
        ParametersWithID parametersWithIDPri = new ParametersWithID(ecPrivateKeyParameters, Hex.decodeStrict("31323334353637383132333435363738"));

        SM2SignerEx signer = new SM2SignerEx(StandardDSAEncoding.INSTANCE);
        // SM2Signer signer = new SM2Signer(PlainDSAEncoding.INSTANCE);
        signer.init(true, ecPrivateKeyParameters);
        // signer.update(message, 0, message.length);
        // byte[] signBytes = signer.generateSignature();
        byte[] signBytes = signer.generateSignature(Hex.decode("d77baa524888f0bc9706a850c35de891e61d281e3039939c72ae52b781d37431"));
        System.out.println("signBase64>> " + Base64.toBase64String(signBytes));
        System.out.println("signHex>> " + Hex.toHexString(signBytes));
    }

    public static BCECPrivateKey parsePrivateFromD() {
        String dHex = "85C951B440045BB1F5CB81BED431AA1404ED8DBF11914EAD33EC16E7B1BDAD1B";
        dHex = "32ecf90216b997b83fb00ab838b1dbf985d714c5e95451a9f2e8a43c6d5c9e6d";
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        ECParameterSpec ecSpec = new ECParameterSpec(
                sm2Spec.getCurve(),
                sm2Spec.getG(),
                sm2Spec.getN(),
                sm2Spec.getH());
        BigInteger d2 = new BigInteger(1, Hex.decode(dHex));
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(d2, ecSpec);
        BCECPrivateKey ecPriFromD = new BCECPrivateKey("EC", ecPrivateKeySpec, BouncyCastleProvider.CONFIGURATION);
        System.out.println("ecPriFromD>> " + Base64.toBase64String(ecPriFromD.getEncoded()));
        return ecPriFromD;
    }

    private final DSAKCalculator kCalculator;
    private final Digest digest;
    private final DSAEncoding encoding;
    private ECDomainParameters ecParams;
    private ECPoint pubPoint;
    private ECKeyParameters ecKey;
    private byte[] z;

    public SM2SignerEx() {
        this(StandardDSAEncoding.INSTANCE, new SM3Digest());
    }

    public SM2SignerEx(Digest digest) {
        this(StandardDSAEncoding.INSTANCE, digest);
    }

    public SM2SignerEx(DSAEncoding encoding) {
        this.kCalculator = new RandomDSAKCalculator();
        this.encoding = encoding;
        this.digest = new SM3Digest();
    }

    public SM2SignerEx(DSAEncoding encoding, Digest digest) {
        this.kCalculator = new RandomDSAKCalculator();
        this.encoding = encoding;
        this.digest = digest;
    }

    public void init(boolean forSigning, CipherParameters param) {
        CipherParameters baseParam;
        byte[] userID;
        if (param instanceof ParametersWithID) {
            baseParam = ((ParametersWithID) param).getParameters();
            userID = ((ParametersWithID) param).getID();
            if (userID.length >= 8192) {
                throw new IllegalArgumentException("SM2 user ID must be less than 2^16 bits long");
            }
        } else {
            baseParam = param;
            userID = Hex.decodeStrict("31323334353637383132333435363738");
        }

        if (forSigning) {
            if (baseParam instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) baseParam;
                this.ecKey = (ECKeyParameters) rParam.getParameters();
                this.ecParams = this.ecKey.getParameters();
                this.kCalculator.init(this.ecParams.getN(), rParam.getRandom());
            } else {
                this.ecKey = (ECKeyParameters) baseParam;
                this.ecParams = this.ecKey.getParameters();
                this.kCalculator.init(this.ecParams.getN(), CryptoServicesRegistrar.getSecureRandom());
            }

            this.pubPoint = this.createBasePointMultiplier().multiply(this.ecParams.getG(), ((ECPrivateKeyParameters) this.ecKey).getD()).normalize();
        } else {
            this.ecKey = (ECKeyParameters) baseParam;
            this.ecParams = this.ecKey.getParameters();
            this.pubPoint = ((ECPublicKeyParameters) this.ecKey).getQ();
        }

        this.z = this.getZ(userID);
        this.digest.update(this.z, 0, this.z.length);
    }

    public void update(byte b) {
        this.digest.update(b);
    }

    public void update(byte[] in, int off, int len) {
        this.digest.update(in, off, len);
    }

    public boolean verifySignature(byte[] signature) {
        try {
            BigInteger[] rs = this.encoding.decode(this.ecParams.getN(), signature);
            return this.verifySignature(rs[0], rs[1]);
        } catch (Exception var3) {
            return false;
        }
    }

    public void reset() {
        this.digest.reset();
        if (this.z != null) {
            this.digest.update(this.z, 0, this.z.length);
        }

    }

    public byte[] generateSignature() throws CryptoException {
        byte[] eHash = this.digestDoFinal();
        BigInteger n = this.ecParams.getN();
        BigInteger e = this.calculateE(n, eHash);
        BigInteger d = ((ECPrivateKeyParameters) this.ecKey).getD();
        ECMultiplier basePointMultiplier = this.createBasePointMultiplier();

        while (true) {
            BigInteger r;
            BigInteger k;
            do {
                k = this.kCalculator.nextK();
                ECPoint p = basePointMultiplier.multiply(this.ecParams.getG(), k).normalize();
                r = e.add(p.getAffineXCoord().toBigInteger()).mod(n);
            } while (r.equals(ZERO));

            if (!r.add(k).equals(n)) {
                BigInteger dPlus1ModN = BigIntegers.modOddInverse(n, d.add(ONE));
                BigInteger s = k.subtract(r.multiply(d)).mod(n);
                s = dPlus1ModN.multiply(s).mod(n);
                if (!s.equals(ZERO)) {
                    try {
                        return this.encoding.encode(this.ecParams.getN(), r, s);
                    } catch (Exception var10) {
                        throw new CryptoException("unable to encode signature: " + var10.getMessage(), var10);
                    }
                }
            }
        }
    }

    public byte[] generateSignature(byte[] eHash) throws CryptoException {
        BigInteger n = this.ecParams.getN();
        BigInteger e = this.calculateE(n, eHash);
        BigInteger d = ((ECPrivateKeyParameters) this.ecKey).getD();
        ECMultiplier basePointMultiplier = this.createBasePointMultiplier();

        while (true) {
            BigInteger r;
            BigInteger k;
            do {
                k = this.kCalculator.nextK();
                ECPoint p = basePointMultiplier.multiply(this.ecParams.getG(), k).normalize();
                r = e.add(p.getAffineXCoord().toBigInteger()).mod(n);
            } while (r.equals(ZERO));

            if (!r.add(k).equals(n)) {
                BigInteger dPlus1ModN = BigIntegers.modOddInverse(n, d.add(ONE));
                BigInteger s = k.subtract(r.multiply(d)).mod(n);
                s = dPlus1ModN.multiply(s).mod(n);
                if (!s.equals(ZERO)) {
                    try {
                        return this.encoding.encode(this.ecParams.getN(), r, s);
                    } catch (Exception var10) {
                        throw new CryptoException("unable to encode signature: " + var10.getMessage(), var10);
                    }
                }
            }
        }
    }

    private boolean verifySignature(BigInteger r, BigInteger s) {
        BigInteger n = this.ecParams.getN();
        if (r.compareTo(ONE) >= 0 && r.compareTo(n) < 0) {
            if (s.compareTo(ONE) >= 0 && s.compareTo(n) < 0) {
                byte[] eHash = this.digestDoFinal();
                BigInteger e = this.calculateE(n, eHash);
                BigInteger t = r.add(s).mod(n);
                if (t.equals(ZERO)) {
                    return false;
                } else {
                    ECPoint q = ((ECPublicKeyParameters) this.ecKey).getQ();
                    ECPoint x1y1 = ECAlgorithms.sumOfTwoMultiplies(this.ecParams.getG(), s, q, t).normalize();
                    if (x1y1.isInfinity()) {
                        return false;
                    } else {
                        BigInteger expectedR = e.add(x1y1.getAffineXCoord().toBigInteger()).mod(n);
                        return expectedR.equals(r);
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private byte[] digestDoFinal() {
        byte[] result = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(result, 0);
        this.reset();
        return result;
    }

    private byte[] getZ(byte[] userID) {
        this.digest.reset();
        this.addUserID(this.digest, userID);
        this.addFieldElement(this.digest, this.ecParams.getCurve().getA());
        this.addFieldElement(this.digest, this.ecParams.getCurve().getB());
        this.addFieldElement(this.digest, this.ecParams.getG().getAffineXCoord());
        this.addFieldElement(this.digest, this.ecParams.getG().getAffineYCoord());
        this.addFieldElement(this.digest, this.pubPoint.getAffineXCoord());
        this.addFieldElement(this.digest, this.pubPoint.getAffineYCoord());
        byte[] result = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(result, 0);
        return result;
    }

    private void addUserID(Digest digest, byte[] userID) {
        int len = userID.length * 8;
        digest.update((byte) (len >> 8 & 255));
        digest.update((byte) (len & 255));
        digest.update(userID, 0, userID.length);
    }

    private void addFieldElement(Digest digest, ECFieldElement v) {
        byte[] p = v.getEncoded();
        digest.update(p, 0, p.length);
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    protected BigInteger calculateE(BigInteger n, byte[] message) {
        return new BigInteger(1, message);
    }
}


