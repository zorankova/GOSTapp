package com.zoran.gostapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import com.zoran.gostapp.gost.auxillary.Auxillary;
import com.zoran.gostapp.gost.auxillary.MainActivityInterface;
import com.zoran.gostapp.gost.gost.GOSTcipher;
import com.zoran.gostapp.gost.gost.GOSTkeyfile;

import com.zoran.gostapp2.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */

public class ExportFragment extends Fragment implements OnClickListener {
	MainActivityInterface inter;
	private Activity activity;
	//ProgressInterface prog;
	public static String MODE;
	@Override
	public void onAttach(Activity activity) {
		inter = (MainActivityInterface) activity;
	//	prog = (ProgressInterface) activity;
		inter.sendData(getTag(),"ExportFragment" );
		this.activity = activity;
		
		super.onAttach(activity);
	}

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	protected static final String TAG = null;
	protected static final int GET_KEYS = 52;
	private static final int PICKFILE_RESULT_CODE = 6;
	private static final int MODE_CREATE = 90;
	private static final int MODE_OPEN = 91;
	private String stringButtonOpen;
	private String stringButtonCreate;
	public EditText editTextFilename;
	public Button buttonExport;
	public ImageButton buttonBrowse;
	public ImageButton buttonCreateKey;
	private Switch switchMain;

	//private RadioGroup radioGroup;
	public SharedPreferences prefs;
	public SharedPreferences.Editor editor;
	
	private boolean isSaveModeOn=true;
	
	private ArrayAdapter<CharSequence> adapter;
	private Spinner spinner;
	private List<CharSequence> listKeys;
	private List<String> listMods;
	private Spinner spinnerMode;
	
	@Override
	public void onPause() {
		Log.d(TAG, "pauseExp");
		
		super.onPause();
		
	}
	public ExportFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		createDirs();
		View rootView = inflater.inflate(R.layout.fragment_export,
				container, false);
		spinner = (Spinner) rootView.findViewById(R.id.spinner);
		File fileKeysDir = new File(MainActivity.PATH_TEMPORAL_KEYS);
		listKeys = Auxillary.ListDirFiles(fileKeysDir);
		//
		stringButtonOpen = getResources().getString(R.string.button_open);
		stringButtonCreate = getResources().getString(R.string.button_save);
		
		adapter = new ArrayAdapter<CharSequence>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,listKeys);
		// Specify the layout to use when the list of choices appears
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(TAG, "position Spinner: " + position + " " + listKeys.get(position).toString());
				editor.putString(MainActivity.KEYFILE_SELECTED, listKeys.get(position).toString());
				editor.commit();
				//Create new
//				if(getResources().getString(R.string.create_new_key).equals(listKeys.get(position)))
//				{
//					Intent intent = new Intent(getActivity(), KeysActivity.class);
//					startActivityForResult(intent, GET_KEYS);
//				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		spinnerMode = (Spinner) rootView.findViewById(R.id.spinnerMode);
		listMods = new ArrayList<String>();
		listMods.add("ECB");
		listMods.add("CTR");
		listMods.add("CFB");
		listMods.add("MAC");
		ArrayAdapter <String> adapterMode = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item ,listMods);
		spinnerMode.setAdapter(adapterMode);
		spinnerMode.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.d(TAG, "position SpinnerMode: " + position + " " +listMods.get(position));
				String mode = listMods.get(position);
					if(mode == "ECB") mode = GOSTcipher.MODE_ECB;
					if(mode == "CTR") mode = GOSTcipher.MODE_CTR;
					if(mode == "CFB") mode = GOSTcipher.MODE_CFB;
					if(mode == "MAC") mode = GOSTcipher.MODE_MAC;
				editor.putString(MainActivity.CIPHER_MODE_SELECTED, mode);
				editor.commit();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		editTextFilename = (EditText) rootView.findViewById(R.id.editTextArchiveName);
		editTextFilename.addTextChangedListener(new TextWatcher() {
			
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
				String string = editTextFilename.getText().toString();
				
				if(string.equals("") || string.equals("Select file")) buttonExport.setEnabled(false);
				else buttonExport.setEnabled(true);
			
				//SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(getActivity());
				//SharedPreferences.Editor editor = prefs.edit();
				
				editor.putString(MainActivity.FILENAME, string);

				editor.commit();
				
			}
		});
		editTextFilename.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				
			}
		});
		//editTextFilename.setOnClickListener(this);
		buttonExport = (Button) rootView.findViewById(R.id.buttonSave);
		buttonExport.setOnClickListener(this);
				prefs=PreferenceManager.getDefaultSharedPreferences(getActivity());
		editor = prefs.edit();
		
		buttonBrowse = (ImageButton) rootView.findViewById(R.id.buttonDelete);
		buttonBrowse.setOnClickListener(this);
		buttonCreateKey = (ImageButton) rootView.findViewById(R.id.buttonCreateKey);
		buttonCreateKey.setOnClickListener(this);
		
		spinner.setSelection(0);
		spinnerMode.setSelection(0);
		
//		radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup1);
//		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(RadioGroup arg0, int arg1) {
//				switch(arg1){
//				case R.id.radioCreate:
//					setOpenOrSave(MODE_CREATE);
//				break;
//				case R.id.radioOpen:
//					setOpenOrSave(MODE_OPEN);
//				}
//				
//			}
//		});
		switchMain = (Switch) rootView.findViewById(R.id.switch1);
		switchMain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!isChecked) 	setOpenOrSave(MODE_CREATE);
				if( isChecked)	setOpenOrSave(MODE_OPEN);
				
			}
		});
//		radioGroup.clearCheck();
//		radioGroup.check(R.id.radioCreate);
		switchMain.setChecked(true); //open
		switchMain.setChecked(false);//create
		
		
		return rootView;
	}

	private void createDirs() {
		
		File gost = new File(MainActivity.PATH_TEMPORAL_KEYS);
		if(!gost.exists()) gost.mkdirs();
		createDefaultKeyfile();
		
	}
	@Override
	public void onClick(View v) {
		
		switch(v.getId()) {

		case R.id.buttonDelete:
			Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
		       intent2.setType("file/*");
		       startActivityForResult(intent2,PICKFILE_RESULT_CODE);
			
			break;
		case R.id.buttonCreateKey:
//			ArrayList<FileItem> list = new ArrayList<FileItem>();
//			list = (ArrayList<FileItem>) inter.getList().clone();
//			list.add(new FileItem("jos jedan", "path"));
//			inter.setList(list);
//			Log.d(TAG, "buttonCreateKey "+list.size()+"");
			Intent intent = new Intent(getActivity(), KeysActivity.class);
			startActivityForResult(intent, GET_KEYS);
			break;
			case R.id.buttonSave:
//			createZip();
//			createCrypted();
			if(isSaveModeOn)
			{
				if(editTextFilename.getText().toString().equals("")) break;
				inter.encrypt();
				 
//				CompressAndCrypt work = new CompressAndCrypt();
//				work.execute(0,0,0);
			}
			else
			{

				if(editTextFilename.getText().toString().equals("Select file")) break;

//				DecryptAndDecompress dnd = new DecryptAndDecompress();
//				dnd.execute(prefs.getString(MainActivity.FILENAME_PATH, ""));
				
				inter.decrypt();				
			}
			break;
		
		}
		
	}

	
	


	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		if(resultCode != Activity.RESULT_OK) return;
		
		if(requestCode == GET_KEYS )
		{
			File fileKeysDir = new File(MainActivity.PATH_TEMPORAL_KEYS);
			listKeys = Auxillary.ListDirFiles(fileKeysDir);
			adapter = new ArrayAdapter<CharSequence>(getActivity(),
					android.R.layout.simple_spinner_dropdown_item,listKeys);
			// Specify the layout to use when the list of choices appears
			//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);
			String filename = intent.getExtras().getString(MainActivity.CREATED_KEY);
			Log.d(TAG, "="+filename+"=");
			String in;
			for(int i=0; i< listKeys.size();i++){
				in = listKeys.get(i).toString();
				Log.d(TAG, ":"+in+":");
				if(in.equals(filename))
				{
					spinner.setSelection(i);
				}
			}
			
		}
		
		if(requestCode == PICKFILE_RESULT_CODE)
		{
			processIntent(intent);
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	public void processIntent(Intent intent)
	{
		Uri uri = intent.getData();
		String resultPath =uri.getPath();
		String filename = uri.getLastPathSegment();
		Log.d(TAG, resultPath + " || "+filename);
		//resultPath = resultPath.substring(0, resultPath.lastIndexOf("."));
		editor.putString(MainActivity.FILENAME_PATH, resultPath);
		editTextFilename.setText(filename);
		editor.putString(MainActivity.FILENAME, filename);
		editor.commit();
		
	}
	
        

	

	private void createDefaultKeyfile()
	{
		try {
		
			long box[][]=new long [][]		
				{{4 , 10 , 9 , 2 , 13 , 8 , 0 , 14 , 6  , 11 , 1 , 12 , 7 , 15 , 5, 3}, 
				{14 , 11 , 4 , 12 , 6 , 13 , 15 , 10 , 2 , 3 , 8 , 1 , 0 , 7 , 5 , 9}, 
				{5 , 8 , 1 , 13 , 10 , 3 , 4 , 2 , 14 , 15 , 12 , 7 , 6 , 0 , 9 , 11},
				{7 , 13 , 10 , 1 , 0 , 8 , 9 , 15 , 14 , 4 , 6 , 12 , 11 , 2 , 5 , 3},
				{6 , 12 , 7 , 1 , 5 , 15 , 13 , 8 , 4 , 10 , 9 , 14 , 0 , 3 , 11 , 2},
				{4 , 11 , 10 , 0 , 7 , 2 , 1 , 13 , 3 , 6 , 8 , 5 , 9 , 12 , 15 , 14},
				{13 , 11 , 4 , 1 , 3 , 15 , 5 , 9 , 0 , 10 , 14 , 7 , 6 , 8 , 2 , 12},
				{1 , 15 , 13 , 0 , 5 , 7 , 10 , 4 , 9 , 2 , 3 , 14 , 6 , 11 , 8 , 12}};
			long key[] = new long []{3203121	,38417	,3983360	,2897519,	3031391	,2253234	,1277403,	1379486};
			long ic[] = new long[]{3408121	,72417};
			GOSTkeyfile keyfile = new GOSTkeyfile();
			File file = new File(MainActivity.PATH_TEMPORAL_KEYS + "Default.key");
			
			keyfile.setIc(ic);
			
				keyfile.setSBoxFull(box);
			
			keyfile.setKey(key);	
			keyfile.saveToFile(file);
			
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void onStart() {
		Intent intent = activity.getIntent();
		if(intent.getScheme() != null)
		{
			Log.d(TAG, "in onStart");
//			radioGroup.check(R.id.radioOpen);
			setOpenOrSave(MODE_OPEN);
			switchMain.setChecked(true);
			processIntent(intent);
			
		}
		super.onStart();
	}

	private void setOpenOrSave(int mode)
	{
		switch(mode){
		case MODE_CREATE:
			editTextFilename.setEnabled(true);
			editTextFilename.setText("");
			buttonBrowse.setVisibility(Button.GONE);
			//buttonExport.setText(stringButtonCreate);
			isSaveModeOn=true;
			buttonExport.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable._device_access_secure), null,null, null);
			
		break;
		case MODE_OPEN:
			editTextFilename.setEnabled(false);
			editTextFilename.setText("Select file");
			buttonBrowse.setVisibility(Button.VISIBLE);
			//buttonExport.setText(stringButtonOpen);
			isSaveModeOn=false;
			buttonExport.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable._device_access_not_secure), null,null, null);
			
			break;
			default:
				break;
		}
		
	}
}