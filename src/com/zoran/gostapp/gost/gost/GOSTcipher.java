package com.zoran.gostapp.gost.gost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;

import com.zoran.gostapp.gost.auxillary.Auxillary;
import com.zoran.gostapp.gost.auxillary.ProgressInterface;
import com.zoran.gostapp.gost.auxillary.StartInterface;
import android.util.Log;

public class GOSTcipher  implements StartInterface{
	

	
	public static final String MODE_ECB = "785136";
	public static final String MODE_CTR = "692353";
	public static final String MODE_CFB = "312457";
	public static final String MODE_MAC = "703280";
	public long MY_ID;
	
	public static final long JOB_DONE = 1;
	public static final long JOB_FAIL = 2;
	public static final long CANCELLED =3;
	public static final long EXCEPTION_ERROR =4;
	public static final long UPDATE_PROGRESS =5;
	
	
	
	private static final String TAG = null;
	private long validFileSize;
	private long counterFileSize;
	private long stepFileSize=10;
	public long c1;
	public long c2;
	public long[][] sBox = new long [8][16];
	public long []key = new long [8];
	public boolean keySet;
	public boolean icSet;
	public boolean sBoxSet;
	public long [] ic = new long [2];
	//public Old_ProgressInterface jobProgress;
	public ProgressInterface interfaceProgress;
	private FileInputStream inputStream;
	private FileOutputStream outputStream;
	
	private boolean enc; 
	private File in;
	private File out;
	private String mode;
	private boolean status = true;
	
	
	public GOSTcipher (long ID)
	{
		c1=( 1 << 24 ) | (1 << 16) | (1 << 8 ) | (1 << 2 );
		c2=( 1 << 24 ) | (1 << 16) | (1 << 8 ) | (1 << 0 );
		keySet = false;
		icSet=false;
		sBoxSet=false;
		ic[0]=ic[1]=0;
		
		inputStream = null;
		outputStream = null;
		mode ="null";
		MY_ID = ID;
		
		
	}
	
	public void setParameters(long [][] SBox, long [] Key, long [] Ic) throws DataFormatException
	{
		setIc(Ic);
		setKey(Key);
		setSBoxFull(SBox);
	}
	
	public void setKeyfile (GOSTkeyfile keyfile) throws DataFormatException
	{
		setParameters(
				keyfile.getSbox(), 
				keyfile.getKey(), 
				keyfile.getIc()
				);
		
	}
	private void setParameters (File in, File out, String mode, boolean enc) throws DataFormatException
	{
		this.in = in;
		this.out = out;
		this.mode = mode;
		this.enc = enc;

	}
	private void checkParameters() throws DataFormatException, FileNotFoundException
	{
		if( !checkIfVaildKey() ) 		
			throw new DataFormatException("Key is missing");
		
		if( !checkIfVaildSboxFull() ) 	
			throw new DataFormatException("Sbox is missing");
		
		if( !checkIfVaildIc() && (mode.equals(MODE_CFB) || mode.equals(MODE_CTR)) )
			throw new DataFormatException("IC is missing");
		
		if(mode == "null")
			throw new DataFormatException("Mode is missing");
		
		if(out.exists()) out.delete();
		inputStream = new FileInputStream(in);
		outputStream = new FileOutputStream(out);
		
		
		
	}
//	public void encryptString ( String in, String out , String mode)
//	{
//		cryptString (  in, out ,  mode, true);
//		
//	}
//	
//	public void decryptString ( String in, String out , String mode)
//	{
//		cryptString (  in, out ,  mode, false);
//		
//	}
//	public void encrypt () throws FileNotFoundException, DataFormatException 
//	{
//		this.enc = true;
//		checkParameters();
//		execute(0);
//		
//	}
//	
//	public void decrypt ( ) throws FileNotFoundException, DataFormatException 
//	{
//		this.enc = false;
//		checkParameters();
//		execute(0);
//		
//	}
	public void encrypt ( File in, File out , String mode) throws DataFormatException, IOException
	{
		
		setParameters (in, out, mode, true);
		//encrypt();
		
	}
	
	public void decrypt ( File in, File out , String mode) throws DataFormatException, IOException
	{
		setParameters (in, out, mode, false);
	//	decrypt();
		
	}
	
	private void crypt () throws IOException, GOSTexception 
	{
		Log.d(TAG, "crypt "+ mode);

			parseFirstBlock(!mode.equals(MODE_MAC)); //if mode == MODE_MAC false, else true
													//if true and enc==true, save block with parameters, else dont
			
			interfaceProgress.setProgress(MY_ID, ProgressInterface.PROGRESS_UPDATE, 0);
			
			if(mode.equals(MODE_ECB)) 	crypt_ECB(enc);
			if(mode.equals(MODE_CTR)) 	crypt_CTR();
			if(mode.equals(MODE_CFB)) 	crypt_CFB(enc);
			if(mode.equals(MODE_MAC))	crypt_MAC();
			
			interfaceProgress.setProgress(MY_ID, ProgressInterface.PROGRESS_UPDATE, 100);
			
		//publishProgress(100);
		inputStream.close();
		outputStream.close();
	}

	
	
	
	private long  roundFunction( long base, long key)
	{
		long tmp=base + key;
		//long zero = 0;
//		Log.d(TAG, ""+Long.toHexString( tmp));
//		tmp= tmp & (~((~zero) << 32));
//		Log.d(TAG, ""+Integer.toHexString( (int) (tmp >>> 28 & 15) )+ " "+ Long.toHexString(tmp));
		tmp = (	sBox[0][(int) (tmp & 15)] |
				sBox[1][(int) (tmp >>> 4 & 15)] << 4 |
				sBox[2][(int) (tmp >>> 8 & 15)] << 8 |
				sBox[3][(int) (tmp >>> 12 & 15)] << 12 |
				sBox[4][(int) (tmp >>> 16 & 15)] << 16 |
				sBox[5][(int) (tmp >>> 20 & 15)] << 20 |
				sBox[6][(int) (tmp >>> 24 & 15)] << 24 |
				sBox[7][(int) (tmp >>> 28 & 15)] << 28 ); 
		
		
//		long tmp2 = tmp;
//		tmp2= modulo32(tmp2);
//		tmp2=(tmp2 << 11) | (tmp2 >>> 21);
//		
//		Log.d(TAG, ""+Long.toHexString(tmp));
		tmp= Auxillary.modulo32(tmp);
		tmp=(tmp << 11) | (tmp >>> 21);
		//Log.d(TAG, ""+Long.toHexString(modulo32(tmp^tmp2)) + " ?? "+Long.toHexString(tmp2));
		
		return tmp;
	}
	
	private boolean checkIfVaildSBox (long[] in)
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
	
	private int setSBoxFull (long in[][]) throws DataFormatException
	{
		
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

	private int setSBox (long in[], int n) throws DataFormatException
	{
		
			if( !checkIfVaildSBox(in)) throw new DataFormatException(", "+ n);
			
			for(int i=0;i<16;i++)
			{
				sBox[n][i]=in[i];
			}
	
		return 0;
	}
	
	private int setKey (long in[])
	{
		
		for(int i=0;i<8;i++) 
			key[i]=Auxillary.modulo32(in[i]) ;
		
		keySet = true;
		return 0;
	}

	private int setIc(long in[])
	{
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
	
	private GOSTblock encryptBlock (GOSTblock in) 
	{	
		return doGOSTrounds(in, 3, 1);
	}
	
	private GOSTblock  decryptBlock (GOSTblock in) 
	{
		
		return doGOSTrounds(in, 1, 3);
	}
	

	
	
	private int  crypt_ECB( boolean enc)
	{
		GOSTblock 	block = new GOSTblock();
		//out.clear();
		//out = "";
		try {
			while(inputStream.available() > 0)
			{
				if(interfaceProgress.workCancelled())
				{
					inputStream.close();
					outputStream.close();
					interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL, ProgressInterface.CANCELLED);
					return 0;
					
				}
				block=getNextBlock();
				//stringIntoBlock(in,block, i); 
				//block.size=8;
				if(enc == true) 	block = encryptBlock(block);
				else 				block = decryptBlock(block);
				putBlock(block);
			}
			interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_DONE, 0);
		} catch (IOException e) {
			Log.d(TAG, "end of file");
			e.printStackTrace();
		}
		return 0;
	}
	
	private int crypt_CFB (boolean enc) 
	{
		
		
			
			GOSTblock r, rInput, rOutput;
			r = new GOSTblock();
			rInput= new GOSTblock();
			rOutput= new GOSTblock();
			//set initial conditions
			r.n1 = ic[0];
			r.n2 = ic[1];
			r.size=8;
			//Manage blocks
			try {
				while(inputStream.available() > 0)
				{
					if(interfaceProgress.workCancelled())
					{
						inputStream.close();
						outputStream.close();
						interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL, ProgressInterface.CANCELLED);
						return 0;
						
					}
					r = encryptBlock(r);
					rInput = getNextBlock();
					
					rOutput.n1 = r.n1 ^ rInput.n1;
					rOutput.n2 = r.n2 ^ rInput.n2;
					rOutput.size = 8;
					
					putBlock(rOutput);
					
					if(enc == true)
					{
						r.n1 = rOutput.n1;
						r.n2 = rOutput.n2;
					}
					else if(enc == false)
					{
						r.n1 = rInput.n1;
						r.n2 = rInput.n2;
					}
				}
				interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_DONE, 0);
			} catch (IOException e) {
				Log.d(TAG, "end of file");
				e.printStackTrace();
			}
			
		
		return 0;
	}
	private int crypt_CTR() 
	{
		
		Log.d(TAG, "crypt_CTR");
			GOSTblock r, rEncrypted, rInput;
			r = new GOSTblock();
			rEncrypted = new GOSTblock();
			rInput = new GOSTblock();
			//set initial conditions
			r.n1 = ic[0];
			r.n2 = ic[1];
			r.size = 8;
			
			r = encryptBlock(r);
			
			//encrypt blocks
			try {
				while(inputStream.available() > 0)
				{
					Log.d(TAG, "step");
					if(interfaceProgress.workCancelled())
					{
						inputStream.close();
						outputStream.close();
						interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL, ProgressInterface.CANCELLED);
						return 0;	
					}
					
					r.n2= Auxillary.modulo32_1(r.n2, c1); 
					r.n1= Auxillary.modulo32	(r.n1, c2); 
					
					rEncrypted = encryptBlock(r);
					rInput = getNextBlock();
					//stringIntoBlock(in, rInput, i);
					
				 	rInput.n1 = rEncrypted.n1 ^ rInput.n1;
					rInput.n2 = rEncrypted.n2 ^ rInput.n2;
					
					//blockIntoString(out, rInput, i);
					putBlock(rInput);
					
				}
				interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_DONE, 0);
			} catch (IOException e) {
				Log.d(TAG, "end of file");
				e.printStackTrace();
			}
			return 0;
	}
	
	private int crypt_MAC () throws GOSTexception
	{

		GOSTblock r, rInput;
		r= new GOSTblock();;
		rInput = new GOSTblock();
		//out.clear();
		if(enc == false)
			throw new GOSTexception("cant decrypt in MAC mode");
		try {
			while(inputStream.available() > 0)
			{ 
				if(interfaceProgress.workCancelled())
				{
					inputStream.close();
					outputStream.close();
					interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL, CANCELLED);
					return 0;
					
				}
				rInput = getNextBlock();
			
				//stringIntoBlock(in, rInput, i);
				if(r == null)
				{
					r.set(rInput);
				}
				else
				{
					r.n1 = 	r.n1 ^ rInput.n1;
					r.n2 = 	r.n2 ^ rInput.n2;
				}
				r = encryptBlockHalfRounds(r);
			}
			interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_DONE, 0);
		} catch (IOException e) {
			Log.d(TAG, "end of file");
			e.printStackTrace();
		}
		r.size=4;
		//blockIntoString(out, r, 0);
		putBlock(r);
			return 0;	
	}
	
private GOSTblock doGOSTrounds(GOSTblock in, int upCount, int downCount)
{
	GOSTblock block = new GOSTblock();
	block.set(in);
	
	for(int j=0;j<upCount;j++)
	{
		for(int i=0;i<4;i++)	// 0 1 2 3 4 5 6 7
		{
			block.n2 	^= roundFunction(block.n1, 	key[i*2]);
			block.n1 	^= roundFunction(block.n2, 	key[i*2+1]);
		}
	}
	for(int j=0;j<downCount;j++)
	{
		for(int i=3;i>=0;i--)	// 7 6 5 4 3 2 1 0
		{
			block.n2 	^= roundFunction(block.n1, 	key[i*2+1]);
			block.n1 	^= roundFunction(block.n2, 	key[i*2]);
		}
	}
	
	long tmp=block.n1;
	block.n1=Auxillary.modulo32(block.n2);
	block.n2=Auxillary.modulo32(tmp);
	
	return block;
}
	
	private GOSTblock encryptBlockHalfRounds (GOSTblock in)
	{
			return doGOSTrounds(in, 2, 0);
	}
	
	
	
//	public void stringIntoBlock (String s, block64 block, int cord)
//	{
//		//BLOCK block;
//		block.clear();
//		long k=0;
//		long tmp=0;
//		
//		while(k<4 && cord + k < s.length() )
//		{
//			tmp=s.charAt((int) (cord+k)) & 255;
//			block.n1 += ( tmp << k*8 );
//			k++;
//		}
//		while(k<8 && cord + k < s.length() )
//		{
//			tmp=s.charAt((int) (cord+k)) & 255;
//			block.n2 += ( tmp << ((k-4)*8) );
//			k++;
//		}
//		block.size = k ;
//		//cout<<k<<endl;
//	}
	
	private GOSTblock getNextBlock ()
	{
		
		//Log.d(TAG,"getNextBlock");
		GOSTblock block=new GOSTblock();
		block.clear();
		long k=0;
		int tmp=0;
		block.size = 8;
		
		
		stepFileSize--;
		if(stepFileSize < 8)
		{
			interfaceProgress.setProgress(MY_ID, ProgressInterface.PROGRESS_UPDATE,  (counterFileSize*100)/validFileSize);	//publishProgress((int)((counterFileSize*100)/validFileSize));
			stepFileSize = validFileSize /1000;
		}
		
		
		while(k<8)
		{
			try {
				tmp=inputStream.read();
			//	Log.d(TAG, k+ ": "+Integer.toHexString(tmp));
			} catch (IOException e) {
				tmp = 0;
				e.printStackTrace();
			}
			if(tmp == -1)
			{
				tmp=0;
				if(k < block.size) 
					block.size=k;
			}
			tmp = tmp & 255;
			if(k<4) block.n1 += ( tmp << k*8 );
			else 	block.n2 += ( tmp << (k-4)*8 );
			
//			if(k<4) Log.d(TAG, k + "= "+Long.toHexString(block.n1));
//			else 	Log.d(TAG, k + "= "+Long.toHexString(block.n2));
			
			k++;
		}
		//Log.d(TAG, "get: " + Long.toHexString(block.n1) + " " +Long.toHexString(block.n2));
		return block;
	}

	private void putBlock (GOSTblock block)
	{
		long k=0;
		int buffer;
		//Log.d(TAG,"PutBlock");
		//Log.d(TAG, "put: " + Long.toHexString(block.n1) + " " +Long.toHexString(block.n2));
		while(k<8)
		{
			if(k<4) buffer=(int) (( block.n1 >> k*8) & 255);
			else 	buffer=(int) (( block.n2 >> (k-4)*8) & 255);
//			Log.d(TAG, k+" 1: "+Integer.toHexString(buffer));
//			if(k<4) Log.d(TAG, k+ " 2: "+Integer.toHexString((int) ( block.n1 >> k*8)));
//			else 	Log.d(TAG, k+" 2: "+Integer.toHexString((int) ( block.n2 >> (k-4)*8)));
			
			try {
				if(counterFileSize != validFileSize)
				{
				outputStream.write(buffer);
				counterFileSize++;

				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			k++;
		}
	}
	
	private boolean parseFirstBlock(boolean putBlock) throws IOException, GOSTexception
	{
		validFileSize=0;
		stepFileSize= 10 ;
		if(enc == true)
		{
			validFileSize = in.length();	
			
			GOSTblock block = new GOSTblock();
			//block.set(validFileSize);
			
			block.n1=validFileSize;
			block.n2=Long.parseLong(mode); 
			
			block = encryptBlock(block);
			block = encryptBlock(block);
			if(putBlock) putBlock(block);
			Log.d(TAG, "filesize_en: " + validFileSize);
		}
		else
		{
			GOSTblock block = new GOSTblock();
			block = getNextBlock();
			block = decryptBlock(block);
			block = decryptBlock(block);
			validFileSize = block.n1;
			Log.d(TAG, "filesize_de: " + validFileSize);
	
			if(block.n2 == Long.parseLong(mode))
				{
				Log.d(TAG, "valja parse");
				}
			else throw new GOSTexception("Decryption mode wrong?");
			
			
		}
		counterFileSize=0;
		return true;
	}

	
	


	

	@Override
	public void start() {
		try {
			checkParameters();
			crypt();
		} catch (IOException e) {
			interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL, ProgressInterface.EXCEPTION_ERROR);
			//complete fail
			e.printStackTrace();
		} catch (GOSTexception e) {
			interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL, ProgressInterface.EXCEPTION_ERROR);	//complete fail
			e.printStackTrace();
		} catch (DataFormatException e) {
			interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL, ProgressInterface.EXCEPTION_ERROR);
			e.printStackTrace();
		}
		
	}

	@Override
	public void setProgressInterface(ProgressInterface pi) {
		this.interfaceProgress = pi;
		
	}


}
