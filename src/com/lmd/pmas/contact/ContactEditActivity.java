package com.lmd.pmas.contact;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.lmd.pmas.R;
import com.lmd.pmas.common.Chinese;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 添加或编辑联系人
 * @author LMD
 */
public class ContactEditActivity extends Activity {

	/**
	 *  静态成员变量
	 */
	
	
	/**
	 * UI相关成员变量
	 */
	private ActionBar actionBar;
	private DatePickerDialog.OnDateSetListener dateSetListener;
	
	/**
	 * View相关成员变量
	 */
	Spinner groupSpinner;
	
	EditText nameText;
	EditText phoneText;
	EditText emailText;
	EditText birthdayText;
	EditText addressText;
	
	/**
	 * Dao成员变量
	 */
	ContactGroupDao cGroupDao;
	ContactDao contactDao;
	
	/**
	 * 数据模型变量
	 */
	ArrayList<ContactGroupModel> contactGroups;
	ContactModel contactModel;

	/**
	 * 数据存储变量
	 */
	ArrayList<String> groupList;
	
	/**
	 * 状态变量
	 */
	public final static int CONTACT_ADD 	= 0;
	public final static int CONTACT_EDIT 	= 1;
	private Bundle bundle;
	private int mode;

	/**
	 * 临时变量
	 */
	Calendar birthdayCalendar;
	DecimalFormat decimalFormat;
	
	/**
	 * Activity回调函数
	 */

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
    	// 绑定View
 		super.onCreate(savedInstanceState);
 		setContentView(R.layout.activity_contact_edit);
 		
 		// 初始化context
 		bundle = getIntent().getExtras();

 		cGroupDao = new ContactGroupDao(this);
 		contactDao = new ContactDao(this);
 		
 		initActionBar();
 		
 		// 初始化数据
 		initData();
 		
 		// 初始化View成员变量
 		initView();
 		
 		// 初始化ActionBar
	 	
 		// 初始化监听器
 		initListener();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_edit, menu);
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
            return true;
		case R.id.action_contact_edit_save:
			saveContact();
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		
		switch (id) {
		case 1:
			switch (mode) {
			case CONTACT_ADD:
				return new DatePickerDialog(this, dateSetListener, 1990, 0, 1);
			case CONTACT_EDIT:
				if (contactModel.getBirthday() == 0 || birthdayCalendar == null) {
					return new DatePickerDialog(this, dateSetListener, 1990, 0, 1);
				}
				else {
					return new DatePickerDialog(this, dateSetListener, 
							birthdayCalendar.get(Calendar.YEAR), 
							birthdayCalendar.get(Calendar.MONTH), 
							birthdayCalendar.get(Calendar.DAY_OF_MONTH));
				}
			}
		
		default:
			return super.onCreateDialog(id);
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
 		
 		
 		contactGroups = cGroupDao.query();
 		groupList = new ArrayList<String>();
 		groupList.add("默认群组");
 		
 		if (contactGroups != null) {
 	 		for (ContactGroupModel i : contactGroups) {
 	 			groupList.add(i.getName());
 			}
		}

		birthdayCalendar = Calendar.getInstance();
		decimalFormat = new DecimalFormat("00");
	}
	
	void initActionBar() {
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		mode = bundle.getInt("mode");
		switch (mode) {
		case CONTACT_ADD:
	 		contactModel = new ContactModel();
			actionBar.setTitle(getResources().getString(R.string.activity_contact_add));
			break;
		case CONTACT_EDIT:
	 		contactModel = contactDao.query(bundle.getInt("_id"), ContactDao.QUERY_BY_ID).get(0);
			actionBar.setTitle(contactModel.getName());
			break;
		}
	}
	
	@SuppressLint({ "SimpleDateFormat", "UseValueOf" })
	void initView() {
		groupSpinner = (Spinner) findViewById(R.id.spinner_Contact_group);
		nameText = (EditText) findViewById(R.id.editText_Contact_name);
		phoneText = (EditText) findViewById(R.id.editText_Contact_phone);
		emailText = (EditText) findViewById(R.id.editText_Contact_email);
		birthdayText = (EditText) findViewById(R.id.editText_Contact_birthday);
		addressText = (EditText) findViewById(R.id.editText_Contact_address);
		
		birthdayText.setInputType(InputType.TYPE_NULL);
		
		groupSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, groupList));
		
		nameText.setText(contactModel.getName());
		phoneText.setText(contactModel.getPhone());
		emailText.setText(contactModel.getEmail());
		addressText.setText(contactModel.getAddress());

		Long time = new Long(contactModel.getBirthday());
		time *= 1000;
		birthdayCalendar.setTimeInMillis(time);
		
		if (mode == CONTACT_EDIT) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if (time > 0) {
				String birthdayStr = format.format(time);
				birthdayText.setText(birthdayStr);
			}
			if (contactModel.get_id() == 0 || contactGroups == null) {
				groupSpinner.setSelection(0, true);
			}
			else {
				int gr_id = contactModel.getGr_id();
				for (int i = 0; i < contactGroups.size(); i++) {
					if (contactGroups.get(i).get_id() == gr_id) {
						groupSpinner.setSelection(i+1, true);
					}
				}
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	void initListener() {
		
		groupSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == 0) {
					contactModel.setGr_id(0);
				}
				else {
					contactModel.setGr_id(contactGroups.get(arg2-1).get_id());
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		dateSetListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				birthdayText.setText(year + "-" 
					+ decimalFormat.format((monthOfYear+1)) + "-" 
					+ decimalFormat.format(dayOfMonth));
				birthdayCalendar.set(year, monthOfYear, dayOfMonth);
				contactModel.setBirthday((int)(birthdayCalendar.getTimeInMillis()/1000));
			}
		};
		
		birthdayText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showDialog(1);
				}
			}
		});
		
		birthdayText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(1);
			}
		});
		
	}
	
	void saveContact() {
		
		String nameString = nameText.getText().toString();
		String phoneString = phoneText.getText().toString();
		String emailString = emailText.getText().toString();
		String address = addressText.getText().toString();
		
		if (nameString.length() == 0) {
			nameText.requestFocus();
			Toast.makeText(this, "请输入联系人姓名", Toast.LENGTH_SHORT).show();
		}
		else {
			Chinese chinese = new Chinese();
			contactModel.setIndex_name(chinese.getStringPinYin(nameString));
			contactModel.setName(nameString);
			contactModel.setPhone(phoneString);
			contactModel.setEmail(emailString);
			contactModel.setAddress(address);
			
			if (mode == CONTACT_EDIT) {
				contactDao.update(contactModel);
			}
			else if (mode == CONTACT_ADD) {
				contactDao.insert(contactModel);
			}
			Toast.makeText(this, "已保存", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
}
