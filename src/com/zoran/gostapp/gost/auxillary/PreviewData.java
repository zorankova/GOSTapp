package com.zoran.gostapp.gost.auxillary;

import java.util.Locale;


public class PreviewData {

	public long first;
	public long second;
	public long position;
	private String nula = "000000000000000"; 
	
	public String getAsString(long in)
	{
		String s;
		s=Long.toHexString(in);
		s = nula.substring(0, 8 - s.length()) + s;
		s=s.substring(0, 4) + " " + s.substring(4, 8);
		return s.toUpperCase(Locale.US);
	}
}
