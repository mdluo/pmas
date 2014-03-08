package com.lmd.pmas.contact;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.lmd.pmas.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ContactGroupActivity extends Activity {
	
	/**
	 * 静态成员变量
	 */

	
	/**
	 * UI相关成员变量
	 */
	private ActionBar actionBar;
	private ArrayAdapter<ContactGroupModel> arrayAdapter;
	
	/**
	 * View相关成员变量
	 */
	private ListView groupListView;
	
	/**
	 * Dao成员变量
	 */
	private ContactGroupDao cGroupDao;
	private ContactDao contactDao;
	
	/**
	 * 数据模型变量
	 */
	ContactGroupModel defaultGroupModel;
	
	/**
	 * 数据存储变量
	 */
	private ArrayList<ContactGroupModel> listDada;
	
	
	/**
	 * 状态变量
	 */
	public boolean isContextMenu = false;
	public ArrayList<Boolean> checkedList;
	public int checkedNum;
	
	
	/**
	 * 临时变量
	 */

	
	/**
	 * Activity回调函数
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// 绑定View
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_group);
		
		// 初始化context
		cGroupDao = new ContactGroupDao(this);
		contactDao = new ContactDao(this);

		// 初始化View成员变量
		initView();

		// 初始化数据
		initData();

		// 初始化ActionBar
		initActionBar();
		
		// 初始化监听器
		initListener();
	}
	
	@Override
	protected void onResume() {
		refreshData();
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_group, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	
	/**
	 * 菜单(ActionBar)选项点击事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
			return true;
			
		case R.id.action_contact_group_add:
			editDialog(0);
			return true;
			
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
	private void initView() {
		groupListView = (ListView) findViewById(R.id.listview_group);
		groupListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
	}
	
	private void initData() {
		defaultGroupModel = new ContactGroupModel();
		defaultGroupModel.set_id(0);
		defaultGroupModel.setName("默认群组");
		defaultGroupModel.setDescription("");
		
		listDada = new ArrayList<ContactGroupModel>();
		checkedList = new ArrayList<Boolean>();
		arrayAdapter = new ContactGroupAdapter(this, R.layout.list_item_contact_group, listDada);
		groupListView.setAdapter(arrayAdapter);
		refreshData();
	}
	
	private void refreshData() {
		listDada.clear();
		checkedList.clear();
		ArrayList<ContactModel> defaultContacts = contactDao.query(0, ContactDao.QUERY_BY_GROUP);
		defaultGroupModel.setCount(defaultContacts.size());
		
		listDada.add(defaultGroupModel);
		listDada.addAll(cGroupDao.query());
		
		for (int i = 0; i < listDada.size(); i++) {
			checkedList.add(false);
		}
		
		arrayAdapter.notifyDataSetChanged();
	}	
	
	private void initActionBar() {
		actionBar = getActionBar();
		actionBar.setTitle(getResources().getString(R.string.activity_contact_group));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}
	
	private void initListener() {
		
		groupListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {
				isContextMenu = false;
				checkedNum = 0;
				for (int i = 0; i < listDada.size(); i++) {
					checkedList.set(i, false);
				}
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				getMenuInflater().inflate(R.menu.contact_context, menu);
				isContextMenu = true;
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
				switch (arg1.getItemId()) {
				// 全选
				case R.id.action_contact_context_all:
					for (int j = 1; j < checkedList.size(); j++) {
						if (!checkedList.get(j)) {
							groupListView.performItemClick(groupListView.getChildAt(j), j, 0);
						}
					}
					arrayAdapter.notifyDataSetChanged();
					return true;
					// 删除
				case R.id.action_contact_context_delete:
					final ActionMode actionMode = arg0;
					final int size = checkedList.size();
					if (checkedNum > 0) {
						Dialog deleteDialog = new AlertDialog.Builder(ContactGroupActivity.this)
						.setTitle("确定删除选中的群组")
						.setMessage("如果群组不为空，群组里的联系人将放入默认群组")
						.setPositiveButton("删除", new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface arg0, int arg1) {
								if (size > 0) {
									int num = 0;
									int gr_id = 0;
									for (int i = 1; i < checkedList.size(); i++) {
										if (checkedList.get(i)) {
											num++;
											gr_id = listDada.get(i).get_id();
											cGroupDao.delete(gr_id);
											ArrayList<ContactModel> contactModels = contactDao.query(gr_id, ContactDao.QUERY_BY_GROUP);
											for (ContactModel contactModel : contactModels) {
												contactModel.setGr_id(0);
												contactDao.update(contactModel);
											}
										}
									}
									refreshData();
									Toast.makeText(ContactGroupActivity.this,"已删除 " + num + "项", Toast.LENGTH_LONG).show();
								}
								actionMode.finish();
							}
						})
						.setNegativeButton("取消", null)
						.create();
						deleteDialog.show();
					}
					else {
						actionMode.finish();
					}
					return true;
				default:
					return false;
				}
			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				if (position != 0) {
					if (checked) {
						checkedNum++;
					} else {
						checkedNum--;
					}
					checkedList.set(position, checked);
				}
				mode.setTitle("已选中 " + checkedNum + " 项");
				arrayAdapter.notifyDataSetChanged();
			}
		});
	}
	
	public void editDialog(final int position) {
		String titleStr = "";
		String positiveStr = "";
		final ContactGroupModel cGroupModel;
		
		if (position == 0) {
			titleStr = "新增群组";
			positiveStr = "新增";
			cGroupModel = new ContactGroupModel();
		}
		else {
			titleStr = "编辑群组";
			positiveStr = "保存";
			cGroupModel = listDada.get(position);
		}
		
		final LinearLayout dialogLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_contact_group_edit, null);
		final EditText groupName = (EditText) dialogLayout.findViewById(R.id.edittext_contact_group_name);
		final EditText groupDescription = (EditText) dialogLayout.findViewById(R.id.edittext_contact_group_description);
		
		groupName.setText(cGroupModel.getName());
		groupDescription.setText(cGroupModel.getDescription());
		
		Dialog addDialog = new AlertDialog.Builder(this)
			.setTitle(titleStr)
			.setView(dialogLayout)
			.setPositiveButton(positiveStr, new DialogInterface.OnClickListener() { 
				@Override
				public void onClick(DialogInterface dialog, int which) { 
					
					if (groupName.length() == 0) {
						groupName.requestFocus();
						Toast.makeText(ContactGroupActivity.this, "群组名不能为空", Toast.LENGTH_SHORT).show();
						try {
							Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else {
						if (position == 0) {
							cGroupModel.setName(groupName.getText().toString());
							cGroupModel.setDescription(groupDescription.getText().toString());
							cGroupModel.setCount(0);
							cGroupDao.insert(cGroupModel);
						}
						else {
							cGroupModel.setName(groupName.getText().toString());
							cGroupModel.setDescription(groupDescription.getText().toString());
							cGroupDao.update(cGroupModel);
						}
						refreshData();
						try {
							Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, true);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true);
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			})
			.create();
		addDialog.show();
	}

}
