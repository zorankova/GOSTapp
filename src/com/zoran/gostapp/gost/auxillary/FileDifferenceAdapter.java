 
package com.zoran.gostapp.gost.auxillary;

import java.io.File;
import java.util.ArrayList;

import com.zoran.gostapp2.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FileDifferenceAdapter extends ArrayAdapter<PreviewData>{
    private ArrayList<PreviewData> entries;
    private Activity activity;
    public FileDifferenceAdapter(Activity a, int textViewResourceId, ArrayList<PreviewData> entries) {
        super(a, textViewResourceId, entries);
        this.entries = entries;
        this.activity = a;
    }
 
    public static class ViewHolder{
        public TextView textViewPosition;
        public TextView textView1;
        public TextView textView2;
        
    }
 
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater vi =
               (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.filedifference, null);
            holder = new ViewHolder();
            holder.textViewPosition = (TextView) v.findViewById(R.id.textViewPosition);
            holder.textView1 = (TextView) v.findViewById(R.id.textView1);
            holder.textView2 = (TextView) v.findViewById(R.id.textView2);
            
            v.setTag(holder);
        }
        else
            holder=(ViewHolder)v.getTag();
 
        final PreviewData previewData = entries.get(position);
        if (previewData != null) {
            holder.textView1.setText(previewData.getAsString(previewData.first));
            holder.textView2.setText(previewData.getAsString(previewData.second));
            holder.textViewPosition.setText(Long.toString(previewData.position));
            
            }
        return v;
    }


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return super.getCount();
	}
 
}