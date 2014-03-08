package com.lmd.pmas.contact;

import java.util.List;
import com.lmd.pmas.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactGroupAdapter extends ArrayAdapter<ContactGroupModel>{
	
	private int resource;
	private int textViewResourceId;
	private ContactGroupActivity activity;

	@SuppressLint("DefaultLocale")
	public ContactGroupAdapter(ContactGroupActivity activity,
			int resource, int textViewResourceId, List<ContactGroupModel> data) {
		
		super(activity, resource, textViewResourceId, data);
		this.activity = activity;
		this.resource = resource;
		this.textViewResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactGroupModel cGroupModel = getItem(position);
		View layout = super.getView(position, convertView, parent);
		if (layout == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(resource, null);
		}
		TextView name = (TextView) layout.findViewById(textViewResourceId);
		name.setText(cGroupModel.getName());
		
		CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkbox_contact_grroup_context);
		LinearLayout editLayout = (LinearLayout) layout.findViewById(R.id.layout_contact_group_edit);
		
		if (activity.isContextMenu) {
			checkBox.setVisibility(View.VISIBLE);
			editLayout.setVisibility(View.VISIBLE);
			if (activity.checkedList.get(position)) {
				checkBox.setChecked(true);
			}
			else {
				checkBox.setChecked(false);
			}
		}
		else {
			checkBox.setVisibility(View.GONE);
			editLayout.setVisibility(View.GONE);
		}
		
		return layout;
	}
}
