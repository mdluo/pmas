package com.lmd.pmas.contact;

import java.util.ArrayList;
import java.util.List;
import com.lmd.pmas.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactGroupAdapter extends ArrayAdapter<ContactGroupModel>{
	
	private int resource;
	private ContactGroupActivity activity;

	@SuppressLint("DefaultLocale")
	public ContactGroupAdapter(ContactGroupActivity activity,
			int resource, List<ContactGroupModel> data) {
		
		super(activity, resource, R.id.textview_contact_group_name, data);
		this.activity = activity;
		this.resource = resource;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ContactGroupModel cGroupModel = getItem(position);
		View layout = super.getView(position, convertView, parent);
		if (layout == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(resource, null);
		}
		TextView name = (TextView) layout.findViewById(R.id.textview_contact_group_name);
		TextView description = (TextView) layout.findViewById(R.id.textview_contact_group_description);
		TextView count = (TextView) layout.findViewById(R.id.textview_contact_group_count);
		
		name.setText(cGroupModel.getName());
		description.setText(cGroupModel.getDescription());
		
		ArrayList<ContactModel> contactModels = activity.contactDao.query(cGroupModel.get_id(), ContactDao.QUERY_BY_GROUP);
		
		count.setText("( "+contactModels.size()+" )");
		
		CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkbox_contact_grroup_context);
		LinearLayout editLayout = (LinearLayout) layout.findViewById(R.id.layout_contact_group_edit);
		
		if (activity.isContextMenu) {
			if (position != 0) {
				editLayout.setVisibility(View.VISIBLE);
			}
			checkBox.setVisibility(View.VISIBLE);
			count.setVisibility(View.GONE);
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
			count.setVisibility(View.VISIBLE);
		}
		
		editLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.editDialog(position);
			}
		});
		
		return layout;
	}
}
