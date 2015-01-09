package com.zoran.gostapp.gost.auxillary;

import java.util.ArrayList;
import java.util.List;

public interface MainActivityInterface {
	public void sendData(String tag, String ID);
	public void setList(ArrayList<FileItem> list);
	public ArrayList<FileItem> getList();
	boolean encrypt();
	public void decrypt();
	
}
