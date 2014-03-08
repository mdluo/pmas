package com.lmd.pmas.contact;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
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
		arrayAdapter = new ContactGroupAdapter(this, R.layout.list_item_contact_group,
				R.id.textview_contact_group_text, listDada);
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
					for (int j = 0; j < checkedList.size(); j++) {
						if (!checkedList.get(j)) {
							groupListView.performItemClick(groupListView.getChildAt(j), j, 0);
						}
					}
					arrayAdapter.notifyDataSetChanged();
					return true;
					// 删除
				case R.id.action_contact_context_delete:
					final ActionMode actionMode = arg0;
					Dialog deleteDialog = new AlertDialog.Builder(ContactGroupActivity.this)
						.setTitle("确定删除")
						.setMessage("删除选中的联系人？")
						.setPositiveButton("删除", new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface arg0, int arg1) {
								int num = 0;
								for (int i = 0; i < checkedList.size(); i++) {
									if (checkedList.get(i)) {
										num++;
										cGroupDao.delete(listDada.get(i).get_id());
									}
								}
								refreshData();
								Toast.makeText(ContactGroupActivity.this,"已删除 " + num + "项", Toast.LENGTH_LONG).show();
								actionMode.finish();
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
							}
						})
						.create();
					deleteDialog.show();
					return true;
				default:
					return false;
				}
			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				if (checked) {
					checkedNum++;
				} else {
					checkedNum--;
				}
				checkedList.set(position, checked);
				mode.setTitle("已选中 " + checkedNum + " 项");
				arrayAdapter.notifyDataSetChanged();
			}
		});
	}

}
