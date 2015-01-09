package com.zoran.gostapp;

import java.util.ArrayList;
import java.util.List;

import com.zoran.gostapp.gost.auxillary.FileItem;
import com.zoran.gostapp.gost.auxillary.FileItemAdapter;
import com.zoran.gostapp.gost.auxillary.MainActivityInterface;
import com.zoran.gostapp2.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.sax.RootElement;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */

public class FilesFragment extends Fragment  {
	MainActivityInterface inter;
	private Activity activity;
	@Override
	public void onAttach(Activity activity) {
		inter = (MainActivityInterface) activity;
		inter.sendData(getTag(), "FilesFragment");
		this.activity = activity;
		super.onAttach(activity);
	}
	private static final String TAG = null;

	private FileItemAdapter adapter;
	private ArrayList<FileItem> fetch = new ArrayList<FileItem>();
	private ListView lv;
	public Button buttonNew;
	public LinearLayout layoutNoFiles;
	public ListView layoutListView;
	 
	 
	 private EditText editTextDescription;

		public SharedPreferences prefs;
		public SharedPreferences.Editor editor;
	 
	public void fillListView(ArrayList<FileItem> input)
	{
		if(input.size() == 0) 
		{
			layoutListView.setVisibility(ListView.INVISIBLE);
			layoutNoFiles.setVisibility(RelativeLayout.VISIBLE);
			
		}
		else //if( fetch.size() == 0)
		{
			layoutListView.setVisibility(ListView.VISIBLE);
			layoutNoFiles.setVisibility(RelativeLayout.INVISIBLE);
		}
		fetch = (ArrayList<FileItem>) input.clone();
		lv.invalidate();
		
		Log.d(TAG, "fillListView " +fetch.size()+"");
		
		adapter = new FileItemAdapter( getActivity() , R.layout.fileitem, fetch);
		 
        /** Setting the list adapter for the ListFragment */
        lv.setAdapter(adapter);
		//adapter.notifyDataSetChanged();
	
	}

	 
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	        /** Creating an array adapter to store the list of countries **/
	    	View rootView = inflater.inflate(R.layout.fragment_files,
					container, false);
	    	lv = (ListView) rootView.findViewById(R.id.listView1);
	    	
	    	layoutListView = (ListView) rootView.findViewById(R.id.listView1);
	    	layoutNoFiles = (LinearLayout) rootView.findViewById(R.id.relativeNoFiles);
	    	fetch=inter.getList();
	    	inter.setList(fetch);
	        adapter = new FileItemAdapter( getActivity() , R.layout.fileitem, fetch);
	 
	        /** Setting the list adapter for the ListFragment */
	        lv.setAdapter(adapter);
	        
	        prefs=PreferenceManager.getDefaultSharedPreferences(getActivity());
			editor = prefs.edit();
	        editTextDescription = (EditText) rootView.findViewById(R.id.editTextDescription);
	        editTextDescription.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					String string = editTextDescription.getText().toString();
					
					editor.putString(MainActivity.DESCRIPTION, string);
					
					editor.commit();
					
				}
			});
	 return rootView;
	       // return super.onCreateView(inflater, container, savedInstanceState);
	    }

	    public  String getRealPathFromURI(Uri contentUri) {

	        // can post image
	        String [] proj={MediaStore.Images.Media.DATA};
	        Cursor cursor = activity.getContentResolver().query( contentUri,
	                        proj, // Which columns to return
	                        null,       // WHERE clause; which rows to return (all rows)
	                        null,       // WHERE clause selection arguments (none)
	                        null); // Order-by clause (ascending by name)
	        if(cursor != null)
	        {
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();

	        return cursor.getString(column_index);
	        }
	        else
	        {
	        	
	        	return contentUri.getPath();
	        }
	    }
		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			if(resultCode != Activity.RESULT_OK) return;
			if(requestCode == MainActivity.GET_FILE)
			{
				Uri uri = data.getData();
				String resultPath =getRealPathFromURI(uri);
				String filename = resultPath.substring(resultPath.lastIndexOf("/")+1);
				
				filename = filename.substring(0, filename.lastIndexOf("."));
				FileItem item = new FileItem(filename, resultPath);
				fetch.add(item);
				inter.setList(fetch);
//				adapter = new FileItemAdapter( getActivity() , R.layout.fileitem, fetch);
//				 
//		        /** Setting the list adapter for the ListFragment */
//		        lv.setAdapter(adapter);
				
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
		public void addFileIntent()
		{
			Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
		       intent2.setType("file/*");
		       startActivityForResult(intent2,MainActivity.GET_FILE);
		}
		
}