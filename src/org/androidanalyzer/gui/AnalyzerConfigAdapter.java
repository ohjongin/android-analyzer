package org.androidanalyzer.gui;

import java.util.ArrayList;

import org.androidanalyzer.R;
import org.androidanalyzer.plugins.AbstractPlugin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

class AnalyzerConfigAdapter extends BaseAdapter implements ListAdapter {
	
	ArrayList<AbstractPlugin> listItems;
	Context ctx;
	PluginConfiguration config;
	
	public AnalyzerConfigAdapter(Context ctx, ArrayList<AbstractPlugin> items, PluginConfiguration config) {
		this.ctx = ctx;
		listItems = items;
		this.config = config;
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
			AbstractPlugin cProfile = listItems.get(position);
			RelativeLayout rowLayout;
			if (convertView == null) {
				rowLayout = (RelativeLayout)LayoutInflater.from(ctx).inflate(R.layout.plugin_config, parent, false);
			} else {
				rowLayout = (RelativeLayout)convertView;
			}
      String pluginName = cProfile.getPluginName();
      boolean selected = config.name2selected.get(pluginName);
			CheckBox checkBox = (CheckBox)rowLayout.findViewById(R.id.plugin_status_checkbox);
			
			checkBox.setChecked(selected);
			TextView name = (TextView)rowLayout.findViewById(R.id.firstLine2);
			
			name.setText(pluginName);
			TextView number = (TextView)rowLayout.findViewById(R.id.secondLine2);
			String lastRun = config.name2description.get(pluginName);
			number.setText(lastRun);
			return rowLayout;
		}
		return null;
	}
	
}