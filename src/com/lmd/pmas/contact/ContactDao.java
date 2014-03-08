package com.lmd.pmas.contact;

import java.util.ArrayList;

import android.content.Context;

import com.lmd.pmas.common.DBDao;

/**
 * 联系人数据的Dao类
 * @author LMD
 *
 */
public class ContactDao extends DBDao<ContactModel>{
	
	private final static String TABLE_NAME 		= "pmas_contact_contact";
	public final static int QUERY_BY_ID 		= 0;
	public final static int QUERY_BY_GROUP 	= 1;
	
	public ContactDao(Context context) {
		super(context, TABLE_NAME, ContactModel.class.getName());
	}
	
	
	/**
	 * 重载父类delete方法，删除指定id的联系人数据
	 * @param id 联系人ID
	 * @return 删除的条目数
	 */
	public int delete(int id) {
		return delete("_id = ?", new String[]{id+""});
	}
	
	
	/**
	 * 重载父类update方法，更新联系人数据
	 * @param model 联系人数据
	 * @return 更新的条目数
	 */
	public int update(ContactModel model) {
		return update(model, "_id = ?", new String[]{model.get_id()+""});
	}
	
	
	/**
	 * 重载父类query方法，查询指定id或指定群组的联系人数据
	 * @param id 联系人ID
	 * @param queryByWhat 通过哪种方式查找
	 * @return 联系人数据
	 */
	public ArrayList<ContactModel> query(int id, int queryByWhat) {
		String section = null;
		switch (queryByWhat) {
		case QUERY_BY_GROUP:
			section = "gr_id = ?";
			break;
		case QUERY_BY_ID:
		default:
			section = "_id = ?";
			break;
		}
		ArrayList<ContactModel> contactModels = query(section, new String[]{id+""});
		if (contactModels == null || contactModels.size() == 0) {
			return null;
		}
		return contactModels;
	}
	
	
	/**
	 * 重载父类query方法，查询全部联系人数据
	 * @return 联系人数据
	 */
	public ArrayList<ContactModel> query() {
		ArrayList<ContactModel> contactModels = query(null, null);
		if (contactModels == null) {
			return new ArrayList<ContactModel>();
		}
		return contactModels;
	}
	
	public ArrayList<ContactModel> query(String search) {
		ArrayList<ContactModel> contactModels = query("index_name LIKE ?", new String[]{search+"%"});
		if (contactModels == null) {
			return new ArrayList<ContactModel>();
		}
		return contactModels;
	}
}
