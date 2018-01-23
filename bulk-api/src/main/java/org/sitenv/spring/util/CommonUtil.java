package org.sitenv.spring.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

public class CommonUtil {
    private static final String CHAR_LIST =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_";

    // private static final int RANDOM_STRING_LENGTH = 250;

    public static String generateRandomString(int length) {

        StringBuffer randStr = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = getRandomNumber();
            char ch = CHAR_LIST.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }

    private static int getRandomNumber() {
        int randomInt = 0;
        Random randomGenerator = new Random();
        randomInt = randomGenerator.nextInt(CHAR_LIST.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }

    public static String base64Encoder(String string) {

        //encoding  byte array into base 64
        byte[] encoded = Base64.encodeBase64(string.getBytes());

        return new String(encoded);
    }

    public static String base64Decoder(String encodedString) {

        //decoding byte array into base64
        byte[] decoded = Base64.decodeBase64(encodedString);

        return new String(decoded);

    }
    
	public static int downloadFIleByName(File downloadFile, HttpServletResponse response) throws IOException{
		 
		if(downloadFile.exists()){
		FileInputStream inputStream = null;
		OutputStream outStream = null;
		
		try {
			inputStream = new FileInputStream(downloadFile);
 
			response.setContentLength((int) downloadFile.length());
			//response.setContentType(context.getMimeType("C:/JavaHonk/CustomJar.jar"));			
 
			// response header
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",downloadFile.getName());
			response.setHeader(headerKey, headerValue);
 
			// Write response
			outStream = response.getOutputStream();
			return IOUtils.copy(inputStream, outStream);
			
 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != inputStream)
					inputStream.close();
				if (null != inputStream)
					outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
 
		}
		return 1;
		}else{
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found.");
		return 404;	
		}
}

}
