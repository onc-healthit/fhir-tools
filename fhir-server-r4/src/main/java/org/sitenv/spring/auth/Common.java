package org.sitenv.spring.auth;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Common {
    public static Integer convertTimestampToUnixTime(String timestamp) throws ParseException {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

            int epoch = (int) (sdf.parse(timestamp).getTime() / 1000);

            return epoch;
        } else {
            return null;
        }
    }
    
    public static Date convertToDateFormat(String timeStamp) {
     	SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                 Locale.ENGLISH);
     	
                 Date parsedDate=null;
				try {
					parsedDate = sdf.parse(timeStamp);
				} catch (ParseException e) {
					e.printStackTrace();
				}
                 return parsedDate;
	     }
    
    public static String getBaseUrl(HttpServletRequest request) {
    	String baseUrl = request.getScheme() + "://" + request.getServerName() + ("http".equals(request.getScheme()) && 
    			request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":"  
    			+ request.getServerPort()) + request.getContextPath();
    	
    	if (!baseUrl.endsWith("/")) {
			baseUrl = baseUrl.concat("/");
		}

    	return baseUrl;
    	
    }
    
}
