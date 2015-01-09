package com.zoran.gostapp.gost.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import com.zoran.gostapp.gost.auxillary.FileItem;
import com.zoran.gostapp.gost.auxillary.ProgressInterface;
import com.zoran.gostapp.gost.auxillary.StartInterface;


import android.util.Log;

public class ZipDecompress  implements StartInterface{ 
	ProgressInterface interfaceProgress;  
	private static final int BUFFER = 2048;
	private static final int END = -5; 
	
	  private String[] _files; 
	  private String _zipFile; 
	  private String _location; 
	  private int zipSize=1;
	  private int currentSize=0;

	  private ArrayList<FileItem> list = new ArrayList<FileItem>();
	private long MY_ID;
	  

	  
	  
	  public ZipDecompress(String zipFile, String location, long MY_ID) { 
		    _zipFile = zipFile; 
		    _location = location; 
		    _dirChecker(""); 
	    this.MY_ID=MY_ID;
	  } 
	  


	public void unzip() throws IOException {
		 list.clear();
		   
		    	 byte data[] = new byte[BUFFER]; 
		      FileInputStream fin = new FileInputStream(_zipFile); 
		      ZipInputStream zin = new ZipInputStream(fin); 
		      ZipEntry ze = null; 
		      while ((ze = zin.getNextEntry()) != null) { 
		        Log.v("Decompress", "Unzipping " + ze.getName()); 
		        interfaceProgress.setProgress(MY_ID, ProgressInterface.PROGRESS_UPDATE, (currentSize*100)/zipSize);
		        currentSize++;
		        if(ze.isDirectory()) { 
		          _dirChecker(ze.getName()); 
		        } else { 
		        	list.add(new FileItem(ze.getName(), _location));
		        	(new File(_location + ze.getName())).delete();
		          FileOutputStream fout = new FileOutputStream(_location + ze.getName()); 
		          int count; 
			       while ((count = zin.read(data, 0, BUFFER)) != -1) { 
			    	   fout.write(data, 0, count); 
//		          for (int c = zin.read(); c != -1; c = zin.read()) { 
//		            fout.write(c); 
			    	   if(interfaceProgress.workCancelled())
			    	   {
					       fout.close(); 
					       (new File(_location + ze.getName())).delete();
					       zin.closeEntry(); 
			    		   zin.close();
			    		   interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL, ProgressInterface.CANCELLED);
			    		   return;
			    	   }
//		            if(i==0)
//		            {
//		            	i=zipSize /1000;
//		            	publishProgress((int)i);
//		            }
		          } 
		 
		          zin.closeEntry(); 
		          fout.close(); 
		        } 
		         
		      } 
		      zin.close();
		      interfaceProgress.setProgress(MY_ID, ProgressInterface.PROGRESS_UPDATE, (currentSize*100)/zipSize);
		      interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_DONE, 0);
		    
		    return;
	}




	
	private void prepare() throws IOException {
		
			zipSize = (new ZipFile(_zipFile)).size();
		
		currentSize = 0;
	} 
	
	public ArrayList<FileItem> getFiles()
	  {
		
	      return list;
		  
	  }
	  private void _dirChecker(String dir) { 
	    File f = new File(_location + dir); 
	 
	    if(!f.isDirectory()) { 
	      f.mkdirs(); 
	    } 
	  }



	@Override
	public void start() {
		try {
			prepare();
			
			try {
				unzip();
			} catch (IOException e) {
				interfaceProgress.setProgress(MY_ID,
						ProgressInterface.JOB_FAIL,
						ProgressInterface.EXCEPTION_ERROR);
				e.printStackTrace();
			}
			
			
		} catch (IOException e) {
			interfaceProgress.setProgress(MY_ID, ProgressInterface.JOB_FAIL,
					ProgressInterface.EXCEPTION_ERROR);
			e.printStackTrace();
		}
	}



	@Override
	public void setProgressInterface(ProgressInterface pi) {
		interfaceProgress = pi;
		
	} 
	 
	} 