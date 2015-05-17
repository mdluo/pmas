package com.lmd.pmas.contact;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.lmd.pmas.R;
import com.lmd.pmas.common.Chinese;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.QuickContactBadge;
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
	
	QuickContactBadge mPhotoView;
	
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
	ContactModel backUpModel;

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
	
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    
    // 创建一个以当前时间为名称的文件
    File tempFile;

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
 		
 		tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
 		
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {
        case PHOTO_REQUEST_TAKEPHOTO:
            startPhotoZoom(Uri.fromFile(tempFile), 150);
            break;

        case PHOTO_REQUEST_GALLERY:
            if (data != null)
                startPhotoZoom(data.getData(), 150);
            break;

        case PHOTO_REQUEST_CUT:
            if (data != null) 
                setPicToView(data);
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);

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
			back();
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
	 		backUpModel = new ContactModel();
	 		backUpModel.setName("");
	 		backUpModel.setAddress("");
	 		backUpModel.setEmail("");
	 		backUpModel.setPhone("");
			actionBar.setTitle(getResources().getString(R.string.activity_contact_add));
			break;
		case CONTACT_EDIT:
	 		contactModel = contactDao.query(bundle.getInt("_id"), ContactDao.QUERY_BY_ID).get(0);
	 		backUpModel = contactModel.clone();
	 		if (backUpModel.getAddress() == null) {
	 			backUpModel.setAddress("");
			}
	 		if (backUpModel.getEmail() == null) {
				backUpModel.setEmail("");
			}
	 		if (backUpModel.getName() == null) {
				backUpModel.setName("");
			}
	 		if (backUpModel.getPhone() == null) {
				backUpModel.setName("");
			}
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
		
		mPhotoView = (QuickContactBadge) findViewById(R.id.image_contact_edit_head);
		mPhotoView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialog headDialog = new AlertDialog.Builder(ContactEditActivity.this)
					.setTitle("选择头像图片")
					.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
	                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
						}
					})
					.setNegativeButton("相册", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
	                        Intent intent = new Intent(Intent.ACTION_PICK, null);
	                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
	                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
						}
					})
					.create();
				headDialog.show();
			}
		});
		
	}    
	
	
	private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.getDefault());
        return dateFormat.format(date) + ".jpg";
    }
	
    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    //将进行剪裁后的图片显示到UI界面上
    @SuppressLint("NewApi")
	private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
			Drawable drawable = new BitmapDrawable(getResources(), photo);
            mPhotoView.setBackground(drawable);
        }
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
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		}
	}
	
	void back() {
		
		String nameString = nameText.getText().toString();
		String phoneString = phoneText.getText().toString();
		String emailString = emailText.getText().toString();
		String address = addressText.getText().toString();
		
		if ( nameString.equals(backUpModel.getName()) && 
			 phoneString.equals(backUpModel.getPhone()) &&
			 emailString.equals(backUpModel.getEmail()) &&
			 address.equals(backUpModel.getAddress()) &&
			 contactModel.getBirthday() == backUpModel.getBirthday()) {
			finish();
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		}
		else {
			Dialog backDialog = new AlertDialog.Builder(this)
				.setTitle("确定返回")
				.setMessage("您已修改联系人信息，是不保存修改而直接返回")
				.setPositiveButton("不保存", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
					}
				})
				.setNegativeButton("取消", null)
				.create();
			backDialog.show();
		}
		
	}
}
