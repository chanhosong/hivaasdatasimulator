package com.hhi.vaas.platform.vdm.handler.crypto;

import com.hhi.vaas.platform.middleware.common.util.XMLUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
 
/**
 * A utility class that encrypts or decrypts a file.
 * @author www.codejava.net
 *
 */
public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String key = "xlvkslWkd!WkdWkd";
 
    /**
     * encrypt inputFile
     * 
     * @param inputFile
     * @return
     * @throws CryptoException
     */
    public static byte[] encrypt(File inputFile) throws CryptoException {
        return doCrypto(Cipher.ENCRYPT_MODE, inputFile);
    }
 
    /**
     * decrypt inputFile
     * 
     * @param inputFile
     * @return
     * @throws CryptoException
     */
    public static byte[] decrypt(File inputFile) throws CryptoException {
        return doCrypto(Cipher.DECRYPT_MODE, inputFile);
    }
    
    /**
     * encrypt inputBytes
     * 
     * @param inputBytes
     * @return
     * @throws CryptoException
     */
    public static byte[] encrypt(String orginStr, String charsetName) throws CryptoException, UnsupportedEncodingException {
    	byte[] outputBytes = doCrypto(Cipher.ENCRYPT_MODE, orginStr.getBytes(charsetName));
    	
    	return outputBytes;
    }
    
    public static String decrypt(byte[] inputBytes, String charsetName) throws CryptoException, UnsupportedEncodingException {
    	byte[] outputBytes =  doCrypto(Cipher.DECRYPT_MODE, inputBytes);
    	
    	return new String(outputBytes, charsetName);
    }
 
    public static byte[] doCrypto(int cipherMode, File inputFile) throws CryptoException {
    	
    	FileInputStream inputStream = null;
        try {
             
            inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            
            String first = new String(inputBytes, 0, 3);
            byte[] copyBytes = null;
            if(Cipher.ENCRYPT_MODE == cipherMode && XMLUtil.hasUTF8BOM(first)){
            	copyBytes = new byte[inputBytes.length-3];
            	System.arraycopy(inputBytes, 3, copyBytes, 0, copyBytes.length);
            }else{
            	copyBytes = inputBytes;
            }
             
            return doCrypto(cipherMode, copyBytes);
            
        } catch (IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        } finally {
        	IOUtils.closeQuietly(inputStream);
        }
    }
    
    public static byte[] doCrypto(int cipherMode, byte[] inputBytes) throws CryptoException {
    	
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
             
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            return outputBytes;
             
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        } 
    }
    
    public static void main(String[] args) throws Exception {
        
    	
    	String inputFilePath = "src/main/resources/VCD_for_ACONIS_LC_VDR_ver0.6.vcd";
    	
    	if (args.length == 1) {
    		inputFilePath = System.getProperty("user.dir") + File.separator + args[0];
		}
    	
        File inputFile = new File(inputFilePath);
        File encryptedFile = new File(inputFilePath +".enc");
        File decryptedFile = new File(inputFilePath +".dec");
         
        try {
            byte[] outputBytes = CryptoUtils.encrypt(inputFile);
            FileUtils.writeByteArrayToFile(encryptedFile, outputBytes);
            //inputFile.delete();
            //FileUtils.moveFile(encryptedFile, inputFile);
            outputBytes = CryptoUtils.decrypt(encryptedFile);
            FileUtils.writeByteArrayToFile(decryptedFile, outputBytes);
            
            System.out.println("finshed!!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
}