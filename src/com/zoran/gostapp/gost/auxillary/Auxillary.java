package com.zoran.gostapp.gost.auxillary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.zoran.gostapp.MainActivity;
import com.zoran.gostapp2.R;

public class Auxillary {
	public static List<CharSequence> ListDirFiles(File f){
		List<CharSequence>  fileList = new ArrayList<CharSequence>();
		File[] files = f.listFiles();
	     
		for (File file : files){
			if(file.getPath().endsWith(MainActivity.keyfile_extension) )
			{
				fileList.add(file.getPath().substring(file.getPath().lastIndexOf('/') + 1, file.getPath().length()-4));  
			}
	     }
		return fileList;
	}
	
	public static long modulo32(long tmp)
	{
		long zero=0;
		return tmp & (~((~zero) << 32));
	}
	
	public static long moduloX(long tmp, int x)
	{
		long zero=0;
		return tmp & (~((~zero) << x));
	}
	
	public static long modulo32(long base, long add)
	{
		long zero=0;
		return (base + add) & (~((~zero) << 32));
	}
	public static long modulo32_1(long base, long add)
	{
		base = (base) & (0xFFFFFFFF);
		add = (add) & (0xFFFFFFFF);
		long tmp= base + add;
		tmp = (tmp) & (0xFFFFFFFF);
		if(tmp > base) return tmp;
		else return tmp+1;
	}
	public static String getFileSizeLabel(long size)
    {
    	String sizeString = "";
    	if(size < 1024) 			sizeString = size + " B";
    	else if(size < 1024*1024) 	sizeString = size/1024+" KB";
    	else 						sizeString = size/(1024*1024) + "." + Long.toString((size%1024)/100)+ " MB";
    	return sizeString;
    }
	public static int getIconId(String extension)
	{
		if(extension.equals("aac")) return R.drawable.aac;
		if(extension.equals("ai")) return R.drawable.ai;
		if(extension.equals("aiff")) return R.drawable.aiff;
		if(extension.equals("avi")) return R.drawable.avi;
		if(extension.equals("bmp")) return R.drawable.bmp;
		if(extension.equals("c")) return R.drawable.c;
		if(extension.equals("cpp")) return R.drawable.cpp;
		if(extension.equals("css")) return R.drawable.css;
		if(extension.equals("dat")) return R.drawable.dat;
		if(extension.equals("dmg")) return R.drawable.dmg;
		if(extension.equals("doc")) return R.drawable.doc;
		if(extension.equals("dotx")) return R.drawable.dotx;
		if(extension.equals("dwg")) return R.drawable.dwg;
		if(extension.equals("eps")) return R.drawable.eps;
		if(extension.equals("exe")) return R.drawable.exe;
		if(extension.equals("flv")) return R.drawable.flv;
		if(extension.equals("gif")) return R.drawable.gif;
		if(extension.equals("h")) return R.drawable.h;
		if(extension.equals("hpp")) return R.drawable.hpp;
		if(extension.equals("html")) return R.drawable.html;
		if(extension.equals("ics")) return R.drawable.ics;
		if(extension.equals("iso")) return R.drawable.iso;
		if(extension.equals("java")) return R.drawable.java;
		if(extension.equals("jpg")) return R.drawable.jpg;
		if(extension.equals("key")) return R.drawable.key;
		if(extension.equals("mid")) return R.drawable.mid;
		if(extension.equals("mp3")) return R.drawable.mp3;
		if(extension.equals("mp4")) return R.drawable.mp4;
		if(extension.equals("mpg")) return R.drawable.mpg;
		if(extension.equals("odf")) return R.drawable.odf;
		if(extension.equals("ods")) return R.drawable.ods;
		if(extension.equals("odt")) return R.drawable.odt;
		if(extension.equals("otp")) return R.drawable.otp;
		if(extension.equals("ots")) return R.drawable.ots;
		if(extension.equals("ott")) return R.drawable.ott;
		if(extension.equals("pdf")) return R.drawable.pdf;
		if(extension.equals("php")) return R.drawable.php;
		if(extension.equals("png")) return R.drawable.png;
		if(extension.equals("ppt")) return R.drawable.ppt;
		if(extension.equals("psd")) return R.drawable.psd;
		if(extension.equals("py")) return R.drawable.py;
		if(extension.equals("qt")) return R.drawable.qt;
		if(extension.equals("rar")) return R.drawable.rar;
		if(extension.equals("rb")) return R.drawable.rb;
		if(extension.equals("rtf")) return R.drawable.rtf;
		if(extension.equals("sql")) return R.drawable.sql;
		if(extension.equals("tga")) return R.drawable.tga;
		if(extension.equals("tgz")) return R.drawable.tgz;
		if(extension.equals("tiff")) return R.drawable.tiff;
		if(extension.equals("txt")) return R.drawable.txt;
		if(extension.equals("wav")) return R.drawable.wav;
		if(extension.equals("xls")) return R.drawable.xls;
		if(extension.equals("xlsx")) return R.drawable.xlsx;
		if(extension.equals("xml")) return R.drawable.xml;
		if(extension.equals("zip")) return R.drawable.zip;
		
		return R.drawable._blank;
	}
}
