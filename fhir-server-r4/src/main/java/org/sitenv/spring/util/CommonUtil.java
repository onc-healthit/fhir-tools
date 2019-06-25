package org.sitenv.spring.util;

import org.apache.commons.codec.binary.Base64;
import org.hl7.fhir.r4.model.DateTimeType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

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

    public static Date convertStringToDate(String dateInString) {
		Date date = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			date = formatter.parse(dateInString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
    public static Date convertStringToDateYear(String dateInString) {
 		Date dateYear = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
			dateYear = formatter.parse(dateInString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateYear;
	}

	public static DateTimeType convertStringToDateTimeType(String dateInStr) {
		DateTimeType dateTimeType = null;
		try {
			dateTimeType =	new	DateTimeType(dateInStr);
			} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeType;
	}
    
	public static String base64Decoder(String encodedString) {

        //decoding byte array into base64
        byte[] decoded = Base64.decodeBase64(encodedString);

        return new String(decoded);

    }
}
