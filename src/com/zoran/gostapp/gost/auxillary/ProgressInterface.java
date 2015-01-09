package com.zoran.gostapp.gost.auxillary;

public interface ProgressInterface {
	public static long PROGRESS_UPDATE = 1;
	public static long PROGRESS_END = 2;
	public static long JOB_FAIL = 3;
	public static long JOB_DONE = 4;
	public static long CANCELLED = 5;
	public static long EXCEPTION_ERROR = 9;
	
	
	
	
	public void setProgress(long id, long what, long value);
	public boolean workCancelled();
} 
