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
import android.widget.TextView;

public class FileItemAdapter extends ArrayAdapter<FileItem>{
    private ArrayList<FileItem> entries;
    private Activity activity;
    MainActivityInterface inter;
    public FileItemAdapter(Activity a, int textViewResourceId, ArrayList<FileItem> entries) {
        super(a, textViewResourceId, entries);
        inter = (MainActivityInterface) a;
        this.entries = entries;
        this.activity = a;
    }
 
    public static class ViewHolder{
        public TextView textViewPath;
        public TextView textViewSize;
        public TextView textViewFilename;
        public ImageButton buttonDelete;
        public ImageView imageView;
    }
 
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater vi =
               (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.fileitem, null);
            holder = new ViewHolder();
            holder.textViewPath = (TextView) v.findViewById(R.id.textView001);
            holder.textViewSize = (TextView) v.findViewById(R.id.textViewSize);
            holder.textViewFilename = (TextView) v.findViewById(R.id.textViewFilename);
            holder.buttonDelete = (ImageButton) v.findViewById(R.id.buttonDelete);
            holder.imageView = (ImageView) v.findViewById(R.id.imageView);
            v.setTag(holder);
        }
        else
            holder=(ViewHolder)v.getTag();
 
        final FileItem FileItem = entries.get(position);
        if (FileItem != null) {
            holder.textViewFilename.setText(FileItem.name);
            holder.textViewPath.setText(FileItem.path);
            String ext = FileItem.path.substring(FileItem.path.lastIndexOf(".")+1).toLowerCase();
            holder.textViewSize.setText(
            		ext + "    " + 
            		Auxillary.getFileSizeLabel((new File(FileItem.path)).length()));
            holder.buttonDelete.setTag(position);
            holder.buttonDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Integer index = (Integer) v.getTag();
					entries.remove(index.intValue());
					inter.setList(entries);
                    notifyDataSetChanged();
				}
			});
            BitmapFactory.Options opt = new Options();
            opt.inDensity = 1120;
            opt.inScaled = true;
            holder.imageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), Auxillary.getIconId(ext), opt));
            }
        return v;
    }
 
}