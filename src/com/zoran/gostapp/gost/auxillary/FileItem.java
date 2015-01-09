package com.zoran.gostapp.gost.auxillary;

public class FileItem  {
	public String name;
	public String path;

	public FileItem(String name, String path)
	{
		this.name = name;
		this.path = path;
	}
	public void setName(String name)
	{
		this.name = name;
		
	}
	public void setPath(String path)
	{
		this.path = path;
		
	}
}
