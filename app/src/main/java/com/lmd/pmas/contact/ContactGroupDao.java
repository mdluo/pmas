package com.lmd.pmas.contact;

import java.util.ArrayList;

import android.content.Context;

import com.lmd.pmas.common.DBDao;

/**
 * 联系人群组数据的Dao类
 * @author LMD
 *
 */
public class ContactGroupDao extends DBDao<ContactGroupModel>{
	
	private final static String TABLE_NAME = "pmas_contact_group";
	
	public ContactGroupDao(Context context) {
		super(context, TABLE_NAME, ContactGroupModel.class.getName());
	}
	
	
	/**
	 * 重载父类delete方法，删除指定id的联系人群组数据
	 * @param id 联系人群组ID
	 * @return 删除的条目数
	 */
	public int delete(int id) {
		return delete("_id = ?", new String[]{id+""});
	}
	
	
	/**
	 * 重载父类update方法，更新联系人群组数据
	 * @param model 联系人群组数据
	 * @return 更新的条目数
	 */
	public int update(ContactGroupModel model) {
		return update(model, "_id = ?", new String[]{model.get_id()+""});
	}
	
	
	/**
	 * 重载父类query方法，查询指定id的群组数据
	 * @param id 联系人群组ID
	 * @return 联系人群组数据
	 */
	public ContactGroupModel query(int id) {
		ArrayList<ContactGroupModel> cgModels = query("_id=?", new String[]{id+""});
		if (cgModels == null || cgModels.size() == 0) {
			return new ContactGroupModel();
		}
		return cgModels.get(0);
	}
	
	
	/**
	 * 重载父类query方法，查询所有群组数据
	 * @return 联系人群组数据
	 */
	public ArrayList<ContactGroupModel> query() {
		ArrayList<ContactGroupModel> cgModels = query(null, null);
		if (cgModels == null || cgModels.size() == 0) {
			return new ArrayList<ContactGroupModel>();
		}
		return cgModels;
	}
}
