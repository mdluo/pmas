package com.lmd.pmas.contact;

import java.util.Comparator;

/**
 * 联系人索引值比较类
 * @author LMD
 * 用于联系人列表的排序
 */
public class ContactComparator implements Comparator<ContactModel> {

	@Override
	public int compare(ContactModel arg0, ContactModel arg1) {
		return arg0.getIndex_name().compareToIgnoreCase(arg1.getIndex_name());
	}
}
