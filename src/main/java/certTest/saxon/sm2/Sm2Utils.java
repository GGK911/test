package certTest.saxon.sm2;


import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.gm.SM2P256V1Curve;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

public class Sm2Utils {

	// 国密推荐曲线
	public static BigInteger gx = new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
	public static BigInteger gy = new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);

	// 密码参数对象
	public static SM2P256V1Curve curve = new SM2P256V1Curve();

	public static BigInteger n = curve.getOrder();
	public static BigInteger h = curve.getCofactor();

	// 通过国密推荐曲线计算出g点
	public static ECPoint point = curve.createPoint(gx, gy);

	public static KeyPair generateKeyPair() throws Exception {

		// BigInteger a = curve.getA().toBigInteger();
		// BigInteger b = curve.getB().toBigInteger();
		ECPoint point = curve.createPoint(gx, gy);
		// SM2椭圆曲线参数
		ECDomainParameters domainParameters = new ECDomainParameters(curve, point, n, h);
		System.out.println(domainParameters);
		// 随机数
		SecureRandom random = new SecureRandom();
		Security.addProvider(new BouncyCastleProvider());
		// 密钥生成
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
		ECParameterSpec parameterSpec = new ECParameterSpec(domainParameters.getCurve(), domainParameters.getG(), domainParameters.getN(), domainParameters.getH());
		kpg.initialize(parameterSpec, random);
		// 密钥对生成
		KeyPair keyPair = kpg.generateKeyPair();
		System.out.println(keyPair);
		return keyPair;
	}

	// 数据签名
	public static byte[] sign(BCECPrivateKey bcecPrivateKey, byte[] srcData) throws Exception {
		ECParameterSpec parameterSpec = bcecPrivateKey.getParameters();
        ECDomainParameters domainParameters = new ECDomainParameters(parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH());
        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(bcecPrivateKey.getD(), domainParameters);
        SM2Signer signer = new SM2Signer();
        ParametersWithRandom pwr = new ParametersWithRandom(ecPrivateKeyParameters, new SecureRandom());
        signer.init(true, pwr);
        signer.update(srcData, 0, srcData.length);
        return signer.generateSignature();
	}

}
