package org.androidanalyzer.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.androidanalyzer.R;
import org.androidanalyzer.core.IAnalyzerPlugin;
import org.androidanalyzer.core.PluginStatus;
import org.androidanalyzer.core.PluginStatus;
import org.androidanalyzer.plugins.AbstractPlugin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

class AnalyzerListAdapter extends BaseAdapter implements ListAdapter {
	
	ArrayList<PluginStatus> listItems;
	Context ctx;
	AnalyzerList list;
  static SimpleDateFormat formatDate = new SimpleDateFormat("dd MMM yyyy");
  static SimpleDateFormat formatTime = new SimpleDateFormat("HH:ss");
	
	public AnalyzerListAdapter(Context ctx, ArrayList<PluginStatus> items, AnalyzerList list) {
		this.ctx = ctx;
		listItems = items;
		this.list = list;
	}

	public int getCount() {
		return listItems != null ? listItems.size() : 0;
	}

	public Object getItem(int position) {
		return position < getCount() ? listItems.get(position) : null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (position < getCount()) {
		  PluginStatus pluginStatus = listItems.get(position);
//			AbstractPlugin cProfile = listItems.get(position);
			RelativeLayout rowLayout;
			if (convertView == null) {
				rowLayout = (RelativeLayout)LayoutInflater.from(ctx).inflate(R.layout.plugin_list, parent, false);
			} else {
				rowLayout = (RelativeLayout)convertView;
			}
      String pluginName = pluginStatus.getPluginName();
      int status = pluginStatus.getStatus();
      long lastrun = pluginStatus.getLastRun();
      String lastRun;
      if (lastrun != -1) {
        Date dt = new Date(lastrun);
        String date = formatDate.format(dt);
        String time = formatTime.format(dt);      
        lastRun = ctx.getString(R.string.time_format);
        lastRun = String.format(lastRun, date, time);
      } else {
        lastRun = ctx.getString(R.string.plugin_not_run);
      }
			ImageView image = (ImageView)rowLayout.findViewById(R.id.plugin_status_icon);
			
			image.setImageResource(status);
			TextView name = (TextView)rowLayout.findViewById(R.id.firstLine);
			
			name.setText(pluginName);
			TextView number = (TextView)rowLayout.findViewById(R.id.secondLine);
			number.setText(lastRun);
			return rowLayout;
		}
		return null;
	}
	
}