package com.tramisalud;

import java.io.UnsupportedEncodingException;

public class StringUtils {
	
	static String convetToUTF8(String value) throws UnsupportedEncodingException{
		 return new String( value.getBytes("UTF-8"), "ISO-8859-1");
	}

}
