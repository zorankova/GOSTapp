package com.zoran.gostapp;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import com.zoran.gostapp.gost.gost.GOSTkeyfile;
import com.zoran.gostapp2.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class KeysActivity extends Activity {

	private EditText[] editKeys;
	private EditText[] editSBox;
	private EditText[] editIc;
	private EditText editFilename;
	private GOSTkeyfile keyFile;
	
	public long[][] sBox = new long [8][16];
	public long []key = new long [8];

	public long [] ic = new long [2];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keys);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle("Keys");
		editKeys = new EditText[8];
		editKeys[0] = (EditText) findViewById(R.id.editText1);
		editKeys[1] = (EditText) findViewById(R.id.editText2);
		editKeys[2] = (EditText) findViewById(R.id.editText3);
		editKeys[3] = (EditText) findViewById(R.id.editText4);
		editKeys[4] = (EditText) findViewById(R.id.editText5);
		editKeys[5] = (EditText) findViewById(R.id.editText6);
		editKeys[6] = (EditText) findViewById(R.id.editText7);
		editKeys[7] = (EditText) findViewById(R.id.editText8);
		
		editSBox = new EditText[8];
		editSBox[0] = (EditText) findViewById(R.id.EditText10);
		editSBox[1] = (EditText) findViewById(R.id.EditText11);
		editSBox[2] = (EditText) findViewById(R.id.EditText12);
		editSBox[3] = (EditText) findViewById(R.id.EditText13);
		editSBox[4] = (EditText) findViewById(R.id.EditText14);
		editSBox[5] = (EditText) findViewById(R.id.EditText15);
		editSBox[6] = (EditText) findViewById(R.id.EditText16);
		editSBox[7] = (EditText) findViewById(R.id.EditText17);
		
		editIc = new EditText[2];
		editIc[0] = (EditText) findViewById(R.id.editText20);
		editIc[1] = (EditText) findViewById(R.id.editText21);
		
		editFilename = (EditText) findViewById(R.id.editTextKeyfileName);
		keyFile = new GOSTkeyfile();
		setResult(RESULT_CANCELED);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.keys, menu);
		
		editKeys = new EditText[8];
		editKeys[0] = (EditText) findViewById(R.id.editText1);
		editKeys[1] = (EditText) findViewById(R.id.editText2);
		editKeys[2] = (EditText) findViewById(R.id.editText3);
		editKeys[3] = (EditText) findViewById(R.id.editText4);
		editKeys[4] = (EditText) findViewById(R.id.editText5);
		editKeys[5] = (EditText) findViewById(R.id.editText6);
		editKeys[6] = (EditText) findViewById(R.id.editText7);
		editKeys[7] = (EditText) findViewById(R.id.editText8);
		
		editSBox = new EditText[8];
		editSBox[0] = (EditText) findViewById(R.id.EditText10);
		editSBox[1] = (EditText) findViewById(R.id.EditText11);
		editSBox[2] = (EditText) findViewById(R.id.EditText12);
		editSBox[3] = (EditText) findViewById(R.id.EditText13);
		editSBox[4] = (EditText) findViewById(R.id.EditText14);
		editSBox[5] = (EditText) findViewById(R.id.EditText15);
		editSBox[6] = (EditText) findViewById(R.id.EditText16);
		editSBox[7] = (EditText) findViewById(R.id.EditText17);
		
		editIc = new EditText[2];
		editIc[0] = (EditText) findViewById(R.id.editText20);
		editIc[1] = (EditText) findViewById(R.id.editText21);
		
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.action_new_keys:
			clearAllFields();
			break;
		case R.id.action_save_keys:
			getDataFromFields();
			if(keyFile.isParametersOk())
			{
				saveKeyfile();
			}
			break;
		case android.R.id.home:
			onBackPressed();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void saveKeyfile() {
	String filename = editFilename.getText().toString();
	File file = new File(MainActivity.PATH_TEMPORAL_KEYS + "/"+filename+MainActivity.keyfile_extension);
	if(file.exists()) file.delete();
	try {
		keyFile.saveToFile(file);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		Toast.makeText(getApplicationContext(), "Keyfile saved !!", Toast.LENGTH_LONG).show();
		Intent intent = getIntent();
		intent.putExtra(MainActivity.CREATED_KEY,filename );
		setResult(RESULT_OK, intent);
	}

	private void getDataFromFields() 
	{
		setResult(RESULT_CANCELED);

		String inputString;
		try {
			for(int i=0;i<8;i++)
			{
				inputString = editKeys[i].getText().toString();
				key[i] = Integer.parseInt(inputString); 
			}
			
		} catch (NumberFormatException e) {e.printStackTrace();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			for(int i=0;i<8;i++)
			{
				inputString = editSBox[i].getText().toString();
				String [] list = inputString.split(" ");
				for(int j=0;j<16;j++)
				{
					sBox[i][j] = Integer.parseInt(list[j]); 
				}
			}
			
		} catch (NumberFormatException e) {e.printStackTrace();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			for(int i=0;i<2;i++)
			{
				inputString = editIc[i].getText().toString();
				ic[i] = Integer.parseInt(inputString); 
			}
			
		} catch (NumberFormatException e) {e.printStackTrace();
		} catch (Exception e) {e.printStackTrace();}
		
		try {
			keyFile.setParameters(sBox, key, ic);
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private void clearAllFields() {

		setResult(RESULT_CANCELED);
		editFilename.setText("");
		for(int i=0;i<8;i++)
		{
			editKeys[i].setText("");
			editSBox[i].setText("");		
		}
		for(int i=0;i<2;i++)
		{
			editIc[i].setText("");		
		}
		
	}

}
