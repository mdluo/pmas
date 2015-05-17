package com.lmd.pmas.contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.lmd.pmas.R;
import com.lmd.pmas.common.CommonUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContactAdapter extends ArrayAdapter<ContactModel> implements
		SectionIndexer {

	// key：首字母，value：首字母起始的indexer
	public HashMap<String, Integer> alphaIndexer;
	// 索引文字数组
	public String[] sections;
	
	// key：position，value：section
	private int[] sectionIndexer;
	
	
	private int resource;
	private int textViewResourceId;
	private List<ContactModel> data;
	private ContactActivity contactActivity;

	@SuppressLint("DefaultLocale")
	public ContactAdapter(ContactActivity contactActivity,
			int resource, int textViewResourceId, List<ContactModel> data) {
		
		super(contactActivity, resource, textViewResourceId, data);
		this.contactActivity = contactActivity;
		this.resource = resource;
		this.textViewResourceId = textViewResourceId;
		this.data = data;
		notifyDataSetChanged();
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void notifyDataSetChanged() {
		alphaIndexer = new HashMap<String, Integer>();
		int size = data.size();
		sectionIndexer = new int[size];
		int section = -1;
		for (int position = 0; position < size; position++) {
			ContactModel model = data.get(position);
			String ch = CommonUtils.getSortKey(model.getIndex_name());
			if (alphaIndexer.get(ch) == null) {
				alphaIndexer.put(ch, position);
				section ++;
			}
			sectionIndexer[position] = section;
		}
		Set<String> sectionLetters = alphaIndexer.keySet();
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
		Collections.sort(sectionList);
		sections = new String[sectionList.size()];
		sectionList.toArray(sections);
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ContactModel contactModel = getItem(position);

		View layout = super.getView(position, convertView, parent);

		if (layout == null) {
			LayoutInflater inflater = (LayoutInflater) contactActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(resource, null);
		}

		TextView name = (TextView) layout.findViewById(textViewResourceId);
		name.setText(contactModel.getName());
		
		CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkbox_contact_context);
		if (contactActivity.isContextMenu) {
			checkBox.setVisibility(View.VISIBLE);
			if (contactActivity.checkedList.get(position)) {
				checkBox.setChecked(true);
			}
			else {
				checkBox.setChecked(false);
			}
		}
		else {
			QuickContactBadge mPhotoView = (QuickContactBadge) layout.findViewById(R.id.quickcontact_contact_item);
			mPhotoView.assignContactFromPhone(contactModel.getPhone(), false);
			checkBox.setVisibility(View.GONE);
		}
		
		LinearLayout sortKeyLayout = (LinearLayout) layout.findViewById(R.id.layout_contact_item_header);
		LinearLayout dividerLayout = (LinearLayout) layout.findViewById(R.id.layout_contact_item_divider);
		TextView sortKey = (TextView) layout.findViewById(R.id.textview_contact_item_header);
		int section = getSectionForPosition(position);
		if (position == getPositionForSection(section)) {
			sortKey.setText(sections[section]);
			sortKeyLayout.setVisibility(View.VISIBLE);
		} else {
			sortKeyLayout.setVisibility(View.GONE);
		}
		if (position == getPositionForSection(section+1)-1) {
			dividerLayout.setVisibility(View.INVISIBLE);
		}
		else {
			dividerLayout.setVisibility(View.VISIBLE);
		}
		
		return layout;
	}

	@Override
	public int getPositionForSection(int section) {
		int length = sections.length;
		if (sections == null || length == 0) {
			return 0;
		}
		if (section > (length-1)) {
			return alphaIndexer.get(sections[length-1]);
		}
		return alphaIndexer.get(sections[section]);
	}

	@Override
	public int getSectionForPosition(int position) {
		int length = sectionIndexer.length;
		if (sectionIndexer == null || length == 0) {
			return 0;
		}
		if (position > (length-1)) {
			return sectionIndexer[length-1];
		}
		return sectionIndexer[position];
	}

	@Override
	public Object[] getSections() {
		return sections;
	}

}
