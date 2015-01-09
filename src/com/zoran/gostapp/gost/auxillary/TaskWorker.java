package com.zoran.gostapp.gost.auxillary;



import android.os.AsyncTask;

public class TaskWorker extends AsyncTask<Integer, Long, Integer> implements ProgressInterface{

	private StartInterface startInterface;
	private ProgressInterface progressInterface;
	
	public TaskWorker(StartInterface startInterface, ProgressInterface progressInterface)
	{
		this.startInterface = startInterface;
		this.progressInterface = progressInterface;
		startInterface.setProgressInterface((ProgressInterface) this);	
	}
	
	@Override
	protected Integer doInBackground(Integer... params) 
	{
		startInterface.start();
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Long... current) 
	{
		long id = current[0];
		long what = current[1];
		long value = current[2];
		progressInterface.setProgress(id, what, value);
		super.onProgressUpdate(current);
	}
	
	@Override
	public void setProgress(long id, long what, long value) 
	{
		publishProgress(id, what, value);
	}
	
	@Override
	public boolean workCancelled() 
	{
		return isCancelled();
	}
}
