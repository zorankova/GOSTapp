package com.zoran.gostapp.gost.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.zoran.gostapp.gost.auxillary.ProgressInterface;
import com.zoran.gostapp.gost.auxillary.StartInterface;

import android.util.Log;

public class ZipCompress  implements StartInterface { 
	private static final int BUFFER = 2048;
	private String[] _files; 
	  private String _zipFile; 
	  private long zipSize;
	  private long currentSize;
	  public long MY_ID;
	  private ProgressInterface interfaceProgress;
	  
	
	  
	
	  public ZipCompress(String[] files, String zipFile,  long MY_ID) { 
	    _files = files; 
	    _zipFile = zipFile;
	   // inter = ((myInterface) a);
	    
	    currentSize=0;
	    (new File(_zipFile)).delete();
	    this.MY_ID=MY_ID;
	  } 
	  

	
	  public void zip() throws IOException {
		 
	    
	    	(new File(_zipFile)).delete();
	      BufferedInputStream origin = null; 
	      FileOutputStream dest = new FileOutputStream(_zipFile); 
	 
	      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 
	 
	      byte data[] = new byte[BUFFER]; 
	 
	      for(int i=0; i < _files.length; i++) { 
	        Log.v("Compress", "Adding: " + _files[i]); 
	        FileInputStream fi = new FileInputStream(_files[i]); 
	        origin = new BufferedInputStream(fi, BUFFER); 
	        ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1)); 
	        out.putNextEntry(entry); 
	        int count; 
	        while ((count = origin.read(data, 0, BUFFER)) != -1) { 
	          out.write(data, 0, count); 
	          currentSize+=count;
	          if(interfaceProgress.workCancelled())
	    	   {
	        	  origin.close(); 
	        	  interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL, ProgressInterface.CANCELLED);
	    		   out.close();
	    		   (new File(_zipFile)).delete();
	    		   return;
	    	   }
	          interfaceProgress.setProgress(MY_ID, ProgressInterface.PROGRESS_UPDATE, (currentSize*100)/zipSize);
	          //publishProgress(count);
	        } 
	        origin.close(); 
	      } 
	      out.close(); 
	      interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_DONE, 0);
	    
	    
		return;
	}




	protected void prepare() throws DataFormatException {
		zipSize = 0;
		currentSize = 0;
		
		for(int i=0; i<_files.length;i++)
		{
			File file = new File (_files[i]);
			zipSize += file.length();
		}
		if(zipSize == 0)
		{ 
			throw new DataFormatException("zipSize is null");
			//cancel(true);
		}
		
	}


	


	@Override
	public void start() {
		try {
			prepare();
			try {
				zip();
			} catch (IOException e) {
				interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL, ProgressInterface.EXCEPTION_ERROR);
				e.printStackTrace();
			}
		} catch (DataFormatException e) {
			interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL, ProgressInterface.EXCEPTION_ERROR);
			e.printStackTrace();
		}
		
		
	}


	@Override
	public void setProgressInterface(ProgressInterface pi) {
		interfaceProgress = pi;
		
	} 
	 
	} 