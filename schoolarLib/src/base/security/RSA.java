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
		RSA rsa = new RSA("NICBA1010WDCB");
		byte[] encryptedData = rsa.encryptData("Banic");
		for (byte b : encryptedData) {
			System.out.print(Byte.toString(b));
		}
		System.out.println("");
		String decrypted = rsa.decryptData(encryptedData);
	}

	public RSA(String clientName) throws IOException {
		PUBLIC_KEY_FILE = dataFolder + "Public.key";
		PRIVATE_KEY_FILE = dataFolder + "Private.key";
		try {
			System.out
					.println("-------GENERATING PUBLIC and PRIVATE KEY-------------");
			KeyPairGenerator keyPairGenerator = KeyPairGenerator
					.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();

			System.out
					.println("\n------- PULLING OUT PARAMETERS WHICH MAKES KEYPAIR----------\n");
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec rsaPubKeySpec = keyFactory.getKeySpec(publicKey,
					RSAPublicKeySpec.class);
			RSAPrivateKeySpec rsaPrivKeySpec = keyFactory.getKeySpec(
					privateKey, RSAPrivateKeySpec.class);

			System.out
					.println("\n--------SAVING PUBLIC KEY AND PRIVATE KEY TO FILES-------\n");
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

	private void saveKeys(String fileName, BigInteger mod, BigInteger exp)
			throws IOException {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			System.out.println("Generating " + fileName + "...");
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(new BufferedOutputStream(fos));

			oos.writeObject(mod);
			oos.writeObject(exp);

			System.out.println(fileName + " generated successfully");
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

	private byte[] encryptData(String data) throws IOException {
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

	private String decryptData(byte[] data) throws IOException {
		byte[] decryptedData = null;
		System.out.println("ENCRYPTED DATA");
		System.out.println(new String(data));
		System.out.println("");
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
