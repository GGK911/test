package certTest.pkiCoreTest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;


public class SM4Utils
{
	public String secretKey = "";
	
	public String iv = "";
	
	public boolean hexString = false;
	
	public boolean padding= false;
	
	public boolean hexData=false;
	
	public SM4Utils()
	{
	}
	
	public String encryptData_ECB(String plainText)
	{
		try 
		{
		
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = padding;
			ctx.mode = SM4.SM4_ENCRYPT;
			//secretKey="0123456789abcdeffedcba9876543210".toUpperCase();
			
			byte[] keyBytes;
			if (hexString)
			{
				//keyBytes = Util.hexStringToBytes(secretKey);
				keyBytes=Hex.decode(secretKey);
			}
			else
			{
				keyBytes = secretKey.getBytes();
			}
			
			//keyBytes=Hex.decode(secretKey);
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_enc(ctx, keyBytes);
			//byte[] encrypted = sm4.sm4_crypt_ecb(ctx,keyBytes);
			byte[] encrypted = null;
			if(hexData)
			{
				encrypted=sm4.sm4_crypt_ecb(ctx, Hex.decode(plainText));
			}else
			{
				encrypted=sm4.sm4_crypt_ecb(ctx, plainText.getBytes("UTF-8"));
			}
			
			//System.out.println(Hex.toHexString(encrypted));
			String cipherText = null;
			if(hexString)
			{
				cipherText=new String(Hex.encode(encrypted));
			}else
			{
				cipherText=new String(Base64.encode(encrypted));
			}
			
			if (cipherText != null && cipherText.trim().length() > 0)
			{
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(cipherText);
				cipherText = m.replaceAll("");
			}
			return cipherText;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String decryptData_ECB(String cipherText)
	{
		try 
		{
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = padding;
			ctx.mode = SM4.SM4_DECRYPT;
			
			byte[] keyBytes;
			
			if (hexString)
			{
				keyBytes = Hex.decode(secretKey);
			}else{
				keyBytes = secretKey.getBytes();
			}
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_dec(ctx, keyBytes);
			byte[] decrypted =null;
			if(hexString)
			{
				decrypted = sm4.sm4_crypt_ecb(ctx, Hex.decode(cipherText));
			}else
			{
				decrypted = sm4.sm4_crypt_ecb(ctx, Base64.decode(cipherText));
			}
			return new String(decrypted, "UTF-8");

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String encryptData_CBC(String plainText)
	{
		try 
		{
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = padding;
			ctx.mode = SM4.SM4_ENCRYPT;
			
			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString)
			{
				keyBytes = Hex.decode(secretKey);
				ivBytes = Hex.decode(iv);
			}
			else
			{
				keyBytes = secretKey.getBytes();
				ivBytes = iv.getBytes();
			}
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_enc(ctx, keyBytes);
			byte[] encrypted = null;
			if(iv==null)
			{
				encrypted=sm4.sm4_crypt_cbc(ctx, null, plainText.getBytes("UTF-8"));
			}else
			{
				encrypted=sm4.sm4_crypt_cbc(ctx, ivBytes, plainText.getBytes("UTF-8"));
			}

			String cipherText = null;
			if(hexString)
			{
				cipherText=new String(Hex.encode(encrypted));
			}else
			{
				cipherText=new String(Base64.encode(encrypted));
			}
			if (cipherText != null && cipherText.trim().length() > 0)
			{
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(cipherText);
				cipherText = m.replaceAll("");
			}
			return cipherText;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String decryptData_CBC(String cipherText)
	{
		try 
		{
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = padding;
			ctx.mode = SM4.SM4_DECRYPT;
			
			byte[] keyBytes;
			byte[] ivBytes;
			if (hexString)
			{
				keyBytes = Hex.decode(secretKey);
				ivBytes = Hex.decode(iv);
			}
			else
			{
				keyBytes = secretKey.getBytes();
				ivBytes = iv.getBytes();
			}
			
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_dec(ctx, keyBytes);
			byte[] decrypted =null;
			if(hexString)
			{
				decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, Hex.decode(cipherText));
			}else
			{
				decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, Base64.decode(cipherText));
			}
			return new String(decrypted, "UTF-8");
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) throws IOException 
	{
		
		//规范数据测试确认
		try {
			String plainText = "0123456789abcdeffedcba9876543210";		
			SM4Utils sm4 = new SM4Utils();
			sm4.hexString = true;
			sm4.padding=false;
			sm4.secretKey = "123123";
			
			sm4.hexData=true;
			
			String cipherText = sm4.encryptData_ECB(plainText);
			System.out.println("ECB模式 运算实例验证 密文: " + cipherText);
			for(int k=0;k<1000000;k++)
			{
				plainText = sm4.encryptData_ECB(plainText);
			}
			
			System.out.println("ECB模式 运算实例验证 1000000 次 密文: " + plainText);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		String plainText = "0123456789abcdeffedcba9876543210测试";		
		SM4Utils sm4 = new SM4Utils();
		sm4.hexString = true;
		sm4.padding=true;

		//key.length ==16
		if(sm4.hexString)
		{
			sm4.secretKey = "0123456789abcdeffedcba9876543210";
			sm4.iv = "0123456789abcdeffedcba9876543210";
			
		}else
		{
			sm4.secretKey= new String(Base64.encode(Hex.decode("25e17c53dc0714e31fb3663c")));
			sm4.iv = "UISwD9fW6cFh9SNS";
		}
		
		System.out.println("ECB模式");
		String cipherText = sm4.encryptData_ECB(plainText);
		System.out.println("密文: " + cipherText);
		
		plainText = sm4.decryptData_ECB(cipherText);
		System.out.println("明文: " + plainText);
		
		System.out.println("CBC模式");

		cipherText = sm4.encryptData_CBC(plainText);
		System.out.println("密文: " + cipherText);
		System.out.println("");
		
		plainText = sm4.decryptData_CBC(cipherText);
		System.out.println("明文: " + plainText);
	}
}
