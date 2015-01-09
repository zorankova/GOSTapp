package com.zoran.gostapp.gost.gost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;

import com.zoran.gostapp.gost.auxillary.Auxillary;

public class GOSTkeyfile {
	public long[][] sBox = new long [8][16];
	public long []key = new long [8];
	public boolean keySet;
	public boolean icSet;
	public boolean sBoxSet;
	public long [] ic = new long [2];
	
	private FileInputStream inputStream;
	private FileOutputStream outputStream;
	public GOSTkeyfile()
	{
		
	}
	public GOSTkeyfile(File in) throws IOException
	{
		inputStream = new FileInputStream(in);
		for(int i=0;i<8;i++)
			for(int j=0;j<16;j++)
				sBox[i][j]=(long) inputStream.read();
		
		for(int i=0;i<8;i++)
		{
			key[i]=0;
			for(int k = 0; k <4; k++)
				key[i] += (((long) inputStream.read()) << (k*8));
		}
		
		for(int i=0;i<2;i++)
		{
			ic[i]=0;
			for(int k = 0; k <4; k++)
				ic[i] += (((long) inputStream.read()) << (k*8));
		}
		inputStream.close();
	}
	public long[][] getSbox(){return sBox;}
	public long[] getKey(){return key;}
	public long[] getIc(){return ic;}
	
	public void saveToFile(File out) throws IOException
	{
		if(out.exists()) out.delete();
		
		outputStream = new FileOutputStream(out);
		for(int i=0;i<8;i++)
			for(int j=0;j<16;j++)
				outputStream.write((int) sBox[i][j]);
		
		for(int i=0;i<8;i++)
		{
			for(int k = 0; k <4; k++)
				outputStream.write((int)(key[i] >>> (k*8)));
		}
		
		for(int i=0;i<2;i++)
		{
			for(int k = 0; k <4; k++)
				outputStream.write((int)(ic[i] >>> (k*8)));
		}
		outputStream.close();
		
	}
	public boolean checkIfVaildSBox (long[] in)
	{
		
		long tmp[] = new long[16];
		
		for(int i=0;i<16;i++) tmp[i]=0;
		
		for(int i=0;i<16;i++)
		{
			if(in[i] < 16 && tmp[(int) in[i]] == 0)
			{
				tmp[(int) in[i]]=1;
			}
			else return false;
		}
		return true;
	}
	
	public int setSBoxFull (long in[][]) throws DataFormatException
	{
		sBoxSet = false;
			String notValidLines = "";
			for(int i=0;i<8;i++)
			{
				try 
				{
					setSBox (in[i], i);
				} 
				catch (DataFormatException e) 
				{
					notValidLines += e.getMessage();
					sBoxSet = false;
				}
			}
			if( !notValidLines.isEmpty())
			{
				throw new DataFormatException("SBox lines:" + notValidLines.substring(1) + "are not vaild");
				
			}
				sBoxSet = true;
				
		
		return 0;
	}

	public int setSBox (long in[], int n) throws DataFormatException
	{
		
			if( !checkIfVaildSBox(in)) throw new DataFormatException(", "+ n);
			
			for(int i=0;i<16;i++)
			{
				sBox[n][i]=in[i];
			}
	
		return 0;
	}
	
	public int setKey (long in[])
	{
		keySet = false;
		for(int i=0;i<8;i++) 
			key[i]=Auxillary.modulo32(in[i]) ;
		
		keySet = true;
		return 0;
	}

	public int setIc(long in[])
	{
		icSet=false;
		ic[0]=Auxillary.modulo32(in[0]);
		ic[1]=Auxillary.modulo32(in[1]);
		
		icSet=true;
		return 0;
		
	}
	
	private boolean checkIfVaildKey ()
	{
		return keySet;
	}
	private boolean checkIfVaildIc ()
	{
		return icSet;
	}
	
	private boolean checkIfVaildSboxFull ()
	{
		return sBoxSet;
	}
	
	public void setParameters(long [][] SBox, long [] Key, long [] Ic) throws DataFormatException
	{
		setIc(Ic);
		setKey(Key);
		setSBoxFull(SBox);
	}
	
	public boolean isParametersOk()
	{
		
		return checkIfVaildIc() & checkIfVaildKey() & checkIfVaildSboxFull();
	}
}
