package com.zoran.gostapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import com.zoran.gostapp.gost.auxillary.Auxillary;
import com.zoran.gostapp.gost.auxillary.FileDifferenceAdapter;
import com.zoran.gostapp.gost.auxillary.FileItem;
import com.zoran.gostapp.gost.auxillary.FileItemAdapter;
import com.zoran.gostapp.gost.auxillary.MainActivityInterface;
import com.zoran.gostapp.gost.auxillary.PreviewData;
import com.zoran.gostapp.gost.auxillary.ProgressInterface;
import com.zoran.gostapp.gost.auxillary.StartInterface;
import com.zoran.gostapp.gost.auxillary.TaskWorker;
import com.zoran.gostapp.gost.gost.GOSTcipher;
import com.zoran.gostapp.gost.gost.GOSTkeyfile;
import com.zoran.gostapp.gost.zip.ZipCompress;
import com.zoran.gostapp.gost.zip.ZipDecompress;
import com.zoran.gostapp2.R;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class JobActivity extends Activity implements ProgressInterface, OnClickListener{

	private static final String TAG = "asdasdasdasd";
	private TextView textViewFirst;
	private TextView textViewSecond;
	private TextView textViewStop;
	private ProgressBar progressBar;
	private Button buttonCancel;
	private ListView listView;
	private RelativeLayout relative8;
	public SharedPreferences prefs;
	public SharedPreferences.Editor editor;
	private String progress1="";
	private String progress2 = "";
	private boolean enc;
	private boolean isJobDone;
	private boolean isJobInterrupted=false;
	private ImageView imageView;
	private GOSTcipher cipher;
	private ZipCompress compressTask;
	private ZipDecompress decompressTask;
	private TaskWorker worker;
	private boolean isDecryptionError=false;
	private boolean isCompressError = false;
	private boolean isInterruptedbyUser = false;
	
	private String firstFilePath;
	private int firstFileShift;
	private String secondFilePath;
	private int secondFileShift;
	
	private int positionInFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle("Work");
		textViewFirst = (TextView) findViewById(R.id.textViewFirst);
		textViewSecond = (TextView) findViewById(R.id.textViewSecond);
		textViewStop = (TextView) findViewById(R.id.textViewStop);
		buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setText(getResources().getString(R.string.job_stop));
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		relative8 = (RelativeLayout) findViewById(R.id.relative8);
		buttonCancel.setOnClickListener(this);
		prefs=PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();
		enc = getIntent().getBooleanExtra(MainActivity.JOB_TYPE, true);
		imageView = (ImageView) findViewById(R.id.imageViewJob);
		listView = (ListView) findViewById(R.id.listView2);
		BitmapFactory.Options opt = new Options();
        opt.inDensity = 600;
        opt.inScaled = true;
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.yes, opt));
		imageView.setVisibility(ImageView.INVISIBLE);
		isJobDone= false;
		
		if(enc == true)
		{
			createZip();
		}
		else decrypt();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.job, menu);
		return true;
	}

	private void unZip()
	{
		String cryptFileName = prefs.getString(MainActivity.FILENAME, "");
		cryptFileName = cryptFileName.substring(0, cryptFileName.lastIndexOf('.'));
		String cryptFilePath = MainActivity.PATH + cryptFileName + "_de.zip";
	
		decompressTask = new ZipDecompress(cryptFilePath, MainActivity.PATH + cryptFileName+"/", MainActivity.DECOMPRESS);
		worker = new TaskWorker(decompressTask, this);
		worker.execute(0);
	
		
	}
	private int createZip()
	{
		
		String content = prefs.getString(MainActivity.DESCRIPTION, "123");
		String zipFile;
		
		createDescriptionFile(content);
		
		zipFile = MainActivity.PATH_TEMPORAL + MainActivity.ZIP_ARCHIVE_NAME;
		
		//fill files list
		List<String> stringList = new ArrayList<String>();
		
		for(int i=prefs.getInt(MainActivity.NUMBER_OF_FILES, 0)-1;i >=0;i--)
		{
			stringList.add( prefs.getString(MainActivity.FILE + Integer.toString(i), ""));
			Log.d(TAG, prefs.getString(MainActivity.FILE + Integer.toString(i), ""));
		}
		
		stringList.add(MainActivity.PATH_TEMPORAL+ MainActivity.FILENAME_DESCRIPTION);
		String[] files = new String [stringList.size() ];
		stringList.toArray(files);
		compressTask = new ZipCompress(files, zipFile, MainActivity.COMPRESS);
		worker = new TaskWorker(compressTask, this);
		worker.execute(0);
		
		return 0;
	}
	
	private int createDescriptionFile(String content)
	{
		try
	    {
	        File dir = new File(MainActivity.PATH_TEMPORAL);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }
	        File file = new File(dir, MainActivity.FILENAME_DESCRIPTION);
	        file.delete();
	        FileWriter writer = new FileWriter(file);
	        writer.append(content);
	        writer.flush();
	        writer.close();  
	    }
	    catch(IOException e)
	    {
	    	Log.d(TAG, "createDescriptionFile");
	         e.printStackTrace();  
	    }
		return 0;
	}
	


	private void createCrypted() {
		File zipFile = new File(MainActivity.PATH_TEMPORAL+MainActivity.ZIP_ARCHIVE_NAME);
		String cryptFileName = prefs.getString(MainActivity.FILENAME, "");
		File cryptFile = new File(MainActivity.PATH + cryptFileName+".gost");
		String mode = prefs.getString(MainActivity.CIPHER_MODE_SELECTED, "ECB");
		try {
			
			
			cipher = new GOSTcipher(MainActivity.ENCRYPTING);

			String keyfilePath = MainActivity.PATH_TEMPORAL_KEYS 
					+ prefs.getString(MainActivity.KEYFILE_SELECTED, "Default") 
					+ MainActivity.keyfile_extension;
			GOSTkeyfile keyfile = new GOSTkeyfile(new File(keyfilePath));
			
			
			if(mode == "ECB") mode = GOSTcipher.MODE_ECB;
			if(mode == "CTR") mode = GOSTcipher.MODE_CTR;
			if(mode == "CFB") mode = GOSTcipher.MODE_CFB;
			if(mode == "MAC") mode = GOSTcipher.MODE_MAC;
			
			cipher.setKeyfile(keyfile);	
			cipher.encrypt(zipFile, cryptFile, mode);
			worker = new TaskWorker(cipher, this);
			worker.execute(0);
			
			
		} catch (FileNotFoundException e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		}catch (DataFormatException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		firstFilePath = MainActivity.PATH_TEMPORAL+MainActivity.ZIP_ARCHIVE_NAME;
		secondFilePath = MainActivity.PATH + cryptFileName+".gost";
		firstFileShift = 0;
		secondFileShift = 8;
		
	}
	
	private void decrypt() {
		
		String cryptFilePath = prefs.getString(MainActivity.FILENAME_PATH, "");
		
		File cryptFile = new File(cryptFilePath);
		
		String cryptFileName = prefs.getString(MainActivity.FILENAME, "");
		cryptFileName = cryptFileName.substring(0, cryptFileName.lastIndexOf('.'));
		
		File cryptFile_de = new File(MainActivity.PATH + cryptFileName + "_de.zip");
		try {
			
			
			cipher = new GOSTcipher(MainActivity.DECRYPTING);

			String keyfilePath = MainActivity.PATH_TEMPORAL_KEYS 
					+ prefs.getString(MainActivity.KEYFILE_SELECTED, "Default") 
					+ MainActivity.keyfile_extension;
			GOSTkeyfile keyfile = new GOSTkeyfile(new File(keyfilePath));
			
			String mode = prefs.getString(MainActivity.CIPHER_MODE_SELECTED, "ECB");
			if(mode == "ECB") mode = GOSTcipher.MODE_ECB;
			if(mode == "CTR") mode = GOSTcipher.MODE_CTR;
			if(mode == "CFB") mode = GOSTcipher.MODE_CFB;
			if(mode == "MAC") mode = GOSTcipher.MODE_MAC;
			
			cipher.setKeyfile(keyfile);	
			cipher.decrypt(cryptFile, cryptFile_de, mode);
			worker = new TaskWorker(cipher, this);
			worker.execute(0);

		} catch (FileNotFoundException e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		}catch (DataFormatException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void setProgress(long id, long what, long progress) {
		if(what == ProgressInterface.PROGRESS_UPDATE)
		{
			if(id == MainActivity.COMPRESS)  	progress1 = "Zipping... " + Long.toString(progress) + "%";
			if(id == MainActivity.DECOMPRESS) 	progress2 = "Unzipping... " + Long.toString(progress) + "%";
			if(id == MainActivity.ENCRYPTING)  	progress2 = "Encrypting... " + Long.toString(progress) + "%";
			if(id == MainActivity.DECRYPTING) 	progress1 = "Decrypting... " + Long.toString(progress) + "%";
			textViewFirst.setText(progress1);
			textViewSecond.setText(progress2);
			return;
		}
		if(what == ProgressInterface.JOB_FAIL)
		{
			stopBackgroundProcess();
			isJobInterrupted = true;
			if(id == MainActivity.ENCRYPTING || id == MainActivity.DECRYPTING) isDecryptionError = true;
			if(id == MainActivity.COMPRESS) isCompressError = true;
			updateUI();
		}
		if(what == ProgressInterface.JOB_DONE)
		{
			if (id == MainActivity.ENCRYPTING) 
			{
				Log.d(TAG, "complete ENCRYPTING");
				isJobDone = true;
				String mode2 = prefs.getString(MainActivity.CIPHER_MODE_SELECTED,	"ECB");
				Log.d(TAG, mode2);
				updateUI();
				if (mode2 != GOSTcipher.MODE_MAC)	setDataPreview();
				else
					try {
						setMACResult();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
			}
			if (id == MainActivity.DECRYPTING)
			{
				Log.d(TAG, "complete DECRYPTING");
				unZip();
				
			}
			if(id == MainActivity.COMPRESS)
			{
				Log.d(TAG, "complete COMPRESS");
				createCrypted();
			
			}
			if(id == MainActivity.DECOMPRESS)
			{
				
				Log.d(TAG, "complete DECOMPRESS");
				isJobDone=true;
				ArrayList<FileItem> list =  decompressTask.getFiles();
				editor.putInt(MainActivity.NUMBER_OF_FILES, list.size());
				updateUI();
				
				for(int i=0;i<list.size();i++)
				{
					editor.putString(MainActivity.FILE+Integer.toString(i), list.get(i).path+list.get(i).name);
					
				}
				editor.commit();
						
			}
		}
		
	}



//	@Override
//	public void Complete(long id, boolean status) 
//	{
//		if(id == MainActivity.COMPRESS && status == true)
//		{
//				Log.d(TAG, "complete COMPRESS");
//				createCrypted();
//	
//		}
//		if(id == MainActivity.DECRYPTING  && status == true)
//		{
//			if(enc==false)
//			{
//				Log.d(TAG, "complete DECRYPTING");
//				unZip();
//			}
//		}
//		if(id == MainActivity.ENCRYPTING && status == true)
//		{
//				Log.d(TAG, "complete ENCRYPTING");
//				isJobDone=true;
//				String mode2 = prefs.getString(MainActivity.CIPHER_MODE_SELECTED, "ECB");
//				Log.d(TAG, mode2);
//				updateUI();
//				if(mode2 != GOSTcipherAsyncTask.MODE_MAC) setDataPreview();
//				else
//					try {
//						setMACResult();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				
//		}
//		if(id == MainActivity.DECOMPRESS && status == true)
//		{
//			
//				Log.d(TAG, "complete DECOMPRESS");
//				isJobDone=true;
//				ArrayList<FileItem> list =  decompressTask.getFiles();
//				editor.putInt(MainActivity.NUMBER_OF_FILES, list.size());
//				updateUI();
//				
//				for(int i=0;i<list.size();i++)
//				{
//					editor.putString(MainActivity.FILE+Integer.toString(i), list.get(i).path+list.get(i).name);
//					
//				}
//				editor.commit();
//				
//		}
//		
//		
//		
//	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.buttonCancel:
			buttonCancel.setEnabled(false);
			Intent intent = new Intent();
			intent.putExtra(MainActivity.JOB_TYPE, enc);
			if(isJobInterrupted)
			{
				setResult(RESULT_CANCELED, intent);
				finish();
			}
			if(isJobDone)
			{
				
				setResult(RESULT_OK, intent);
				finish();
			}
			else
			{
				stopBackgroundProcess();
				isInterruptedbyUser=true;
				isDecryptionError = false;
				isJobInterrupted = true;
				updateUI();
				
			}
			buttonCancel.setEnabled(true);
			break;
		case android.R.id.home:
			onBackPressed();
			break;
		}
		
	}
	private void updateUI()
	{
		buttonCancel.setText(getResources().getString(R.string.job_close));
		buttonCancel.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable._navigation_accept), null,null, null);
		progressBar.setVisibility(ProgressBar.INVISIBLE);
		
		
        
		if(isJobInterrupted)
		{
			String stopText = "";
			if(isDecryptionError)
			{
				stopText = getResources().getString(R.string.job_wrong_key);
				textViewSecond.setTypeface(null, Typeface.BOLD);
				textViewStop.setTypeface(null, Typeface.NORMAL);
				textViewSecond.setText(getResources().getString(R.string.job_decrypt_fail));
			}
			if(isInterruptedbyUser)
			{
				stopText = getResources().getString(R.string.job_interupt);
				textViewStop.setTypeface(null, Typeface.BOLD);
				
			}
			
			if(isCompressError)
			{
				stopText = getResources().getString(R.string.job_no_files);
				textViewSecond.setTypeface(null, Typeface.BOLD);
				textViewStop.setTypeface(null, Typeface.NORMAL);
				textViewSecond.setText(getResources().getString(R.string.job_compress_fail));
			}
//			else 
//			{
//				stopText = getResources().getString(R.string.job_interupt);
//				
//			}
			textViewStop.setText(stopText);
			BitmapFactory.Options opt = new Options();
	        opt.inDensity = 600;
	        opt.inScaled = true;
			imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no, opt));
		}
		else 
		{
			textViewStop.setText(getResources().getString(R.string.job_done));
			//default image is yes
			//imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no, opt));
		}
		textViewStop.setVisibility(TextView.VISIBLE);
		imageView.setVisibility(ImageView.VISIBLE);
		
	}

	@Override
	public void onBackPressed() {
		stopBackgroundProcess();
		Intent intent = new Intent();
		intent.putExtra(MainActivity.JOB_TYPE, enc);
		
			setResult(RESULT_CANCELED, intent);
			finish();
		super.onBackPressed();
	}
	
	private void stopBackgroundProcess()
	{
		
		if(worker != null)		worker.cancel(true);
	
		
	}
	
	private void setDataPreview()
	{
		positionInFile = 0;
		try {
			populateList();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		relative8.setVisibility(RelativeLayout.VISIBLE);
		((ImageButton) findViewById(R.id.buttonLeftJob))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						if (positionInFile - (200 * 4) >= 0) {
							positionInFile -= (200 * 4);
							try {
								populateList();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
		((ImageButton) findViewById(R.id.buttonRightJob))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (((new File(firstFilePath)).length()) > (positionInFile + 200 * 4 -1)) 
						{
							positionInFile += 200 * 4;

							try {
								populateList();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
		
	}

	private void populateList() throws IOException {
		

		
		ArrayList<PreviewData> data = new ArrayList<PreviewData>();
		FileInputStream inputStream = new FileInputStream(firstFilePath);
		FileInputStream inputStream2 = new FileInputStream(secondFilePath);
		
		
		long count=0;
		while(count != positionInFile+firstFileShift)
		{
			count = count + inputStream.skip(positionInFile+firstFileShift-count);
		}
		Log.d(TAG, "count1: " +count );
		for(int i=0;i<200;i++)
		{
			if(inputStream.available() <=0 ) break;
			int k =0;
			long tmp=0;
			PreviewData prewTemp = new PreviewData();
			while(k<4)
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
					
				}
				tmp = tmp & 255;
				prewTemp.first += ( tmp << k*8 );

				k++;
			}
			data.add(prewTemp);
			
		}
		inputStream.close();
		
		count =0;
		while(count != positionInFile+secondFileShift)
		{
			count = count + inputStream2.skip(positionInFile+secondFileShift-count);
		}
		Log.d(TAG, "count2: " +count );
		for(int i=0;i<200;i++)
		{
			if(inputStream2.available() <=0 ) break;
			int k =0;
			long tmp=0;
			PreviewData prewTemp = data.get(i);
			while(k<4)
			{
				try {
					tmp=inputStream2.read();
				//	Log.d(TAG, k+ ": "+Integer.toHexString(tmp));
				} catch (IOException e) {
					tmp = 0;
					e.printStackTrace();
				}
				if(tmp == -1)
				{
					tmp=0;
					
				}
				tmp = tmp & 255;
				prewTemp.second += ( tmp << k*8 );

				k++;
			}
			prewTemp.position=positionInFile + i*4;
			data.remove(i);
			data.add(i,prewTemp);
		}
		inputStream2.close();
		
		FileDifferenceAdapter adapter = new FileDifferenceAdapter( this , R.layout.filedifference, data);

        /** Setting the list adapter for the ListFragment */
        listView.setAdapter(adapter);
	}
	private void setMACResult() throws IOException
	{
		relative8.setVisibility(RelativeLayout.VISIBLE);
		listView.setVisibility(ListView.GONE);
		((ImageButton) findViewById(R.id.buttonLeftJob)) .setVisibility(ImageButton.GONE);
		((ImageButton) findViewById(R.id.buttonRightJob)).setVisibility(ImageButton.GONE);
		((TextView) findViewById(R.id.textViewPosition)).setText("MAC:");
		((TextView) findViewById(R.id.textView2))		.setText("");
		int k=0;
		long tmp =0;
		FileInputStream inputStream = new FileInputStream(secondFilePath);
		PreviewData prewTemp = new PreviewData();
		
		while(k<4)
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
				
			}
			tmp = tmp & 255;
			prewTemp.first += ( tmp << k*8 );

			k++;
		}
		inputStream.close();
		((TextView) findViewById(R.id.textView1))		.setText(prewTemp.getAsString(prewTemp.first));
	}

	

	@Override
	protected void onDestroy() {
		stopBackgroundProcess();
		super.onDestroy();
	}

	@Override
	public boolean workCancelled() {
		return false;
	}
}
