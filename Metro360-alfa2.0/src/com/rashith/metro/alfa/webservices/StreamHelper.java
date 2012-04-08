package com.rashith.metro.alfa.webservices;

import java.io.IOException;
import java.io.InputStream;

public class StreamHelper {

	public static String convertInputStreamToString(InputStream is) throws IOException {
		 StringBuffer sb = new StringBuffer();
  
           while (true) {
               final int ch = is.read();
               if (ch < 0) {
                   break;
               } else {
                   sb.append((char)ch);
               }
           }
           is.close();
       return sb.toString();  
	 }
}
