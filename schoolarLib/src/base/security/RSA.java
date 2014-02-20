package base.security;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

public class RSA {
	private static final String dataFolder = System.getenv("APPDATA")
			+ "\\.Schoolar\\";
	private static String PUBLIC_KEY_FILE = null;
	private static String PRIVATE_KEY_FILE = null;

	public static void main(String[] args) throws IOException {
	}

	public RSA() {

	}

	public RSA(String clientName) throws IOException {
		PUBLIC_KEY_FILE = dataFolder + System.getenv("username") + "Public.key";
		PRIVATE_KEY_FILE = dataFolder + System.getenv("username")
				+ "Private.key";
		if (!((new File(PUBLIC_KEY_FILE).exists()) && (new File(
				PRIVATE_KEY_FILE)).exists())) {
			try {
				KeyPairGenerator keyPairGenerator = KeyPairGenerator
						.getInstance("RSA");
				keyPairGenerator.initialize(512);
				KeyPair keyPair = keyPairGenerator.generateKeyPair();
				PublicKey publicKey = keyPair.getPublic();
				PrivateKey privateKey = keyPair.getPrivate();
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				RSAPublicKeySpec rsaPubKeySpec = keyFactory.getKeySpec(
						publicKey, RSAPublicKeySpec.class);
				RSAPrivateKeySpec rsaPrivKeySpec = keyFactory.getKeySpec(
						privateKey, RSAPrivateKeySpec.class);
				this.saveKeys(PUBLIC_KEY_FILE, rsaPubKeySpec.getModulus(),
						rsaPubKeySpec.getPublicExponent());
				this.saveKeys(PRIVATE_KEY_FILE, rsaPrivKeySpec.getModulus(),
						rsaPrivKeySpec.getPrivateExponent());

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveKeys(String fileName, BigInteger mod, BigInteger exp)
			throws IOException {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(new BufferedOutputStream(fos));

			oos.writeObject(mod);
			oos.writeObject(exp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				oos.close();

				if (fos != null) {
					fos.close();
				}
			}
		}
	}

	public byte[] encryptData(String data) throws IOException {
		byte[] dataToEncrypt = data.getBytes();
		byte[] encryptedData = null;
		try {
			PublicKey pubKey = readPublicKeyFromFile(PUBLIC_KEY_FILE);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			encryptedData = cipher.doFinal(dataToEncrypt);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedData;
	}

	public byte[] encryptData(String data, BigInteger modulus,
			BigInteger exponent) throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus,
				exponent);
		KeyFactory fact = KeyFactory.getInstance("RSA");
		PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);
		byte[] dataToEncrypt = data.getBytes();
		byte[] encryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encryptedData = cipher.doFinal(dataToEncrypt);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedData;
	}

	public String decryptData(byte[] data) throws IOException {
		byte[] decryptedData = null;
		try {
			PrivateKey privateKey = readPrivateKeyFromFile(PRIVATE_KEY_FILE);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			decryptedData = cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(decryptedData);
	}

	public PublicKey readPublicKeyFromFile(String fileName) throws IOException {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(new File(fileName));
			ois = new ObjectInputStream(fis);

			BigInteger modulus = (BigInteger) ois.readObject();
			BigInteger exponent = (BigInteger) ois.readObject();
			RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus,
					exponent);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);
			return publicKey;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				ois.close();
				if (fis != null) {
					fis.close();
				}
			}
		}
		return null;
	}

	public BigInteger[] readModulusAndExponent() throws IOException {
		String fileName = PUBLIC_KEY_FILE;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(new File(fileName));
			ois = new ObjectInputStream(fis);

			BigInteger modulus = (BigInteger) ois.readObject();
			BigInteger exponent = (BigInteger) ois.readObject();
			BigInteger[] me = new BigInteger[] { modulus, exponent };
			return me;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				ois.close();
				if (fis != null) {
					fis.close();
				}
			}
		}
		return null;
	}

	public PrivateKey readPrivateKeyFromFile(String fileName)
			throws IOException {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(new File(fileName));
			ois = new ObjectInputStream(fis);

			BigInteger modulus = (BigInteger) ois.readObject();
			BigInteger exponent = (BigInteger) ois.readObject();
			RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(
					modulus, exponent);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = fact.generatePrivate(rsaPrivateKeySpec);

			return privateKey;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				ois.close();
				if (fis != null) {
					fis.close();
				}
			}
		}
		return null;
	}
}
