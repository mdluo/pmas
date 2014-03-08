package com.lmd.pmas.contact;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.lmd.pmas.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 联系人详情Activity类
 * @author LMD
 *
 */
public class ContactDetailActivity extends Activity {

	/**
	 *  静态成员变量
	 */
	
	
	/**
	 * UI相关成员变量
	 */
	private ActionBar actionBar;
	
	/**
	 * View相关成员变量
	 */
	
	TextView nameText;
	TextView groupText;
	TextView phoneText;
	TextView emailText;
	TextView birthdayText;
	TextView addressText;
	
	/**
	 * Dao成员变量
	 */
	ContactGroupDao cGroupDao;
	ContactDao contactDao;
	
	/**
	 * 数据模型变量
	 */
	ContactModel contactModel;
	ContactGroupModel cGroupModel;

	/**
	 * 数据存储变量
	 */
	
	/**
	 * 状态变量
	 */
	Bundle bundle;

	/**
	 * 临时变量
	 */
	Calendar birthdayCalendar;
	
	/**
	 * Activity回调函数
	 */

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
    	// 绑定View
 		super.onCreate(savedInstanceState);
 		setContentView(R.layout.activity_contact_detail);
 		
 		// 初始化context
 		bundle = getIntent().getExtras();
 		
 		// 初始化数据
 		initData();
 		
 		// 初始化View成员变量
 		initView();
 		
 		// 更新数据
 		refreshData();
 		
 		// 初始化ActionBar
 		initActionBar();
	}
	
	@Override
	protected void onResume() {
 		refreshData();
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_detail, menu);
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
            return true;
		case R.id.action_contact_detail_edit:
			Intent intent = new Intent(ContactDetailActivity.this, ContactEditActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("mode", ContactEditActivity.CONTACT_EDIT);
			bundle.putInt("_id", contactModel.get_id());
			intent.putExtras(bundle);
			startActivity(intent);
			//finish();
            return true;
		case R.id.action_contact_detail_delete:
			Dialog deleteDialog = new AlertDialog.Builder(ContactDetailActivity.this)
				.setTitle("确定删除")
				.setMessage("删除此联系人？")
				.setPositiveButton("删除", new DialogInterface.OnClickListener() {
					@Override
					public void onClick( DialogInterface arg0, int arg1) {
						contactDao.delete(contactModel.get_id());
						finish();
						Toast.makeText(ContactDetailActivity.this,"已删除 ",Toast.LENGTH_LONG).show();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick( DialogInterface arg0, int arg1) {
					}
				})
				.create();
			deleteDialog.show();
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	/**
	 * 线程对象
	 */
	
	
	/**
	 * 自定义成员对象
	 */
	



	/**
	 * 自定义方法
	 */
	void initData() {
 		
 		cGroupDao = new ContactGroupDao(ContactDetailActivity.this);
 		contactDao = new ContactDao(ContactDetailActivity.this);
 		
		birthdayCalendar = Calendar.getInstance();
	}
	
	void initView() {
		
		nameText = (TextView) findViewById(R.id.textview_contact_detail_name);
		groupText = (TextView) findViewById(R.id.textview_contact_detail_group);
		phoneText = (TextView) findViewById(R.id.textview_contact_detail_phone);
		emailText = (TextView) findViewById(R.id.textview_contact_detail_email);
		birthdayText = (TextView) findViewById(R.id.textview_contact_detail_birthday);
		addressText = (TextView) findViewById(R.id.textview_contact_detail_address);
	}
	
	@SuppressLint({ "SimpleDateFormat", "UseValueOf" })
	void refreshData() {

 		contactModel = contactDao.query(bundle.getInt("_id"), ContactDao.QUERY_BY_ID).get(0);
 		cGroupModel = cGroupDao.query(contactModel.getGr_id());nameText.setText(contactModel.getName());
 		
		if (cGroupModel == null || contactModel.getGr_id() == 0) {
			groupText.setText("默认群组");
		}
		else {
			groupText.setText(cGroupModel.getName());
		}
		phoneText.setText(contactModel.getPhone());
		emailText.setText(contactModel.getEmail());
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Long time = new Long(contactModel.getBirthday());
		time *= 1000;
		if (time == 0) {
			birthdayText.setText("未设置");
		}
		else {
			birthdayText.setText(format.format(time));
		}
		addressText.setText(contactModel.getAddress());
	}
	
	void initActionBar() {
		actionBar = getActionBar();
		actionBar.setTitle(contactModel.getName());
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

}
