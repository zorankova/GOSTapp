package com.zoran.gostapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import com.zoran.gostapp.gost.auxillary.FileItem;
import com.zoran.gostapp.gost.auxillary.MainActivityInterface;
import com.zoran.gostapp2.R;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, MainActivityInterface {
	
	protected static final int GET_FILE = 111;

	//tags
	private boolean isPageMoved=false;
	private static final String TAG = "asd";
	public static final String PREFS = "prefs";
	public static final String DESCRIPTION = "desc";
	public static final String FILENAME = "desc1";
	public static final String CREATED_KEY = "createdkey";
	public static String FILENAME_PATH = "filenamepath";
	//constants
	public static String PATH;
	public static String PATH_TEMPORAL;
	public static String PATH_TEMPORAL_KEYS;
	private static final String path_append = "GOSTapp/";
	private static final String path_temporal = "temporal/";
	private static final String path_temporal_keys = "keys/";
	public static final String keyfile_extension = ".key";
	public static String FILENAME_DESCRIPTION = "description.txt";
	public static String ZIP_ARCHIVE_NAME = "archive.zip";
	public static String NUMBER_OF_FILES="numberoffiles";
	public static String FILE="file";
	private Menu menu;

	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final String SHARED_PREFERENCES = "shared";
	public static final String DESCRIPTION_TXT = "dessesae";
	public static final String KEYFILE_SELECTED = "keyfile_selected";
	public static final String CIPHER_MODE_SELECTED ="cipher_mode_selected";
	public static final long COMPRESS = 77;
	public static final long DECOMPRESS = 78;
	public static final long ENCRYPTING = 79;
	public static final long DECRYPTING = 80;
	private static final int DO_JOB = 70;
	public static final String JOB_TYPE = "jobtype";
	private Fragment fragmentFiles;
	public SharedPreferences prefs;
	public SharedPreferences.Editor editor;
	public ArrayList<FileItem> list;
	public String[] tags  = new String[3];
	
	
	private int tmp_progress;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Debug.startMethodTracing("gost");
		setContentView(R.layout.activity_main);
		list = new ArrayList<FileItem>();
		
		PATH = Environment.getExternalStorageDirectory()+"/"+path_append;
		PATH_TEMPORAL = PATH + path_temporal;
		PATH_TEMPORAL_KEYS = PATH_TEMPORAL + path_temporal_keys;
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
						MenuItem item = menu.findItem(R.id.action_add);
						   if(position == 0)  item.setVisible(false);
						   else item.setVisible(true);
						   if(!isPageMoved) item.setTitle("add file");
						   isPageMoved =true;
					}
				});
		mViewPager.setOffscreenPageLimit(10);

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		prefs=PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;

		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		
		mViewPager.setCurrentItem(tab.getPosition());
		//
		//
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

//	@Override
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//		switch(item.getItemId())
//		{
//		case R.id.action_new:
//			
//			//ft.replace(arg0, arg1, arg2)
//			
//			Fragment f = getSupportFragmentManager().findFragmentByTag(tags[0]);
//			if(f==null)Log.d(TAG, "ne radi");
//			else
//			{
//				EditText edit = (EditText) f.getView().findViewById(R.id.editText);
//				if(edit==null)Log.d(TAG, "ne radi edit");
//				else
//				{
//					edit.setText("RADIII");
//				}
//			}
//			//mSectionsPagerAdapter.notifyDataSetChanged();
//			//Log.d(TAG, ""+mSectionsPagerAdapter.);
//			//Fragment fragment2 = new DescriptionFragment();
//			break;
//		case R.id.action_open:
//			break;
//		case R.id.action_settings:
//			break;
//		}
//		return super.onMenuItemSelected(featureId, item);
//	}
	
	
	


	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Bundle args = new Bundle();
			Fragment fragment;
			//return Fragment.instantiate(getApplicationContext(), info.clss.getName(), info.args);
			switch(position)
			{
//			case 1:
//				fragment = new DescriptionFragment();
//				args.putString("TAG", "DescriptionFragment");
//				break;
			case 1:
				fragment = new FilesFragment();
				args.putString("TAG", "FilesFragment");
				//return Fragment.instantiate(mContext, info.clss.getName(), info.args);
				
				break;
			case 0:
				fragment = new ExportFragment();
				args.putString("TAG", "ExportFragment");
				break;
			default:
				fragment = new FilesFragment();
				args.putString("TAG", "FilesFragment");
					break;
			
			}
			
			fragment.setArguments(args);
			return fragment;
		}

		
		@Override
		public int getCount() {
			// Show 3 total pages.
			
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
//			case 2:
//				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}




		
	}

	
	
	@Override
	public void sendData(String tag, String ID) {
		Log.d("TAG", tag);
		
		if(ID.equals("DescriptionFragment")) tags[0]=tag;
		else if (ID.equals("ExportFragment")) tags[1]=tag;
		else if (ID.equals("FilesFragment")) tags[2]=tag;
		
		
		
		
	}

	public static String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	      sb.append(line).append("\n");
	    }
	    return sb.toString();
	}

	public static String getStringFromFile (String filePath) throws Exception {
	    File fl = new File(filePath);
	    FileInputStream fin = new FileInputStream(fl);
	    String ret = convertStreamToString(fin);
	    //Make sure you close all streams.
	    fin.close();        
	    return ret;
	}

	@Override
	public void setList(ArrayList<FileItem> list) {
		Fragment f = getSupportFragmentManager().findFragmentByTag(tags[2]);
		this.list = (ArrayList<FileItem>) list.clone();
		
		Log.d(TAG, "setList"+this.list.size()+"");
		
		
		editor.putInt(NUMBER_OF_FILES, this.list.size());
		
		for(int i=0;i<this.list.size();i++)
		{
			Log.d(TAG, "prefs: "+i+" "+ this.list.get(i).path);
			editor.putString(FILE+Integer.toString(i), this.list.get(i).path);
			
		}
		editor.commit();
		((FilesFragment) f).fillListView(this.list);
		
	}

	
	
	@Override
	public ArrayList<FileItem> getList() {
		return  this.list;
	}

	@Override
	public boolean encrypt() {
//		String filenameSave = prefs.getString(FILENAME, "");
//		String filenameDescription = FILENAME_DESCRIPTION;
//		String mode = prefs.getString(CIPHER_MODE_SELECTED, "");
		
		//createZip();
		Intent intent = new Intent(this, JobActivity.class);
		intent.putExtra(JOB_TYPE, true);
		startActivityForResult(intent, DO_JOB);
		return false;
	}



	@Override
	public void decrypt() {
		Intent intent = new Intent(this, JobActivity.class);
		intent.putExtra(JOB_TYPE, false);
		startActivityForResult(intent, DO_JOB);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		if (requestCode == DO_JOB) {
			if (intent.getBooleanExtra(JOB_TYPE, true))// enc
			{
				return;
			} else // dec
			{
				int numberOfFiles;
				boolean jobDone;
				if (resultCode == RESULT_OK) {
					numberOfFiles = prefs.getInt(NUMBER_OF_FILES, 0);
					jobDone = true;
				} else {
					numberOfFiles = 0;
					jobDone = false;
				}

				importDescription(jobDone);
				importFiles(numberOfFiles);

			}

		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
public void importFiles(int numberOfFiles)
{
	list.clear();
	
	
	for(int i=0;i<numberOfFiles -1 ;i++)
	{
		String path = prefs.getString(FILE + i, "");
		String name = path.substring(path.lastIndexOf("/")+1, path.lastIndexOf("."));
		//if(name.equals(MainActivity.FILENAME_DESCRIPTION)) continue;
		list.add(new FileItem(name, path));
	}
	//list.remove(numberOfFiles-1);
//	Fragment f = getSupportFragmentManager().findFragmentByTag(tags[2]);
//	((FilesFragment) f).fillListView(this.list);
	setList(this.list);
	
	
}


private void importDescription(boolean jobDone)
 {
		Fragment f = getSupportFragmentManager().findFragmentByTag(tags[2]);
		EditText edit = (EditText) f.getView().findViewById(R.id.editTextDescription);
		String str="";
		
		if (jobDone) {
			String cryptFileName = prefs.getString(MainActivity.FILENAME, "");
			cryptFileName = cryptFileName.substring(0,cryptFileName.lastIndexOf('.'));
			cryptFileName = PATH + cryptFileName + "/" + FILENAME_DESCRIPTION;

			try {

				str = getStringFromFile(cryptFileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			(new File(cryptFileName)).delete();
		}
		
		edit.setText(str);

	}
@Override
protected void onStart() {
	
		super.onStart();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			if(fragmentFiles == null) fragmentFiles = getSupportFragmentManager().findFragmentByTag(tags[2]);
			((FilesFragment) fragmentFiles).addFileIntent();
			return true;

		default:

			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		Debug.stopMethodTracing();
		super.onDestroy();
	}

	

	
	
	
}
