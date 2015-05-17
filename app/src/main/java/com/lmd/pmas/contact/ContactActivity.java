package com.lmd.pmas.contact;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.lmd.pmas.R;
import com.lmd.pmas.common.Chinese;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 联系人Activity类
 * 
 * @version 1
 * @author LMD
 */

public class ContactActivity extends Activity {

	/**
	 * 静态成员变量
	 */

	/**
	 * UI相关成员变量
	 */
	private DrawerLayout drawerLayout;
	private ActionBar actionBar;
	private ActionBarDrawerToggle abdToggle;
	private Menu menu;
	private ProgressDialog progressDialog;

	/**
	 * View相关成员变量
	 */
	private ListView menuListView;
	private ListView contactListView;
	private ContactAdapter arrayAdapter;
	private LinearLayout listHeaderLayout;
	private TextView listHeaderText;
	private EditText searchText;
	

	MenuItem searchItem;
	MenuItem groupItem;
	MenuItem overflowItem;
	SubMenu subMenu;
	LinearLayout searchLayout;

	/**
	 * Dao成员变量
	 */
	private ContactGroupDao cGroupDao;
	private ContactDao contactDao;

	/**
	 * 数据模型变量
	 */

	/**
	 * 数据存储变量
	 */
	private String[] menuListData;
	
	private ArrayList<ContactModel> listDada;
	private ArrayList<ContactGroupModel> cGroupModels;
	
	private ArrayList<String> actionData;
	private ArrayAdapter<String> actionAdapter;

	/**
	 * 状态变量
	 */
	private int lastFirstVisibleItem = -1;
	public boolean isContextMenu = false;
	public ArrayList<Boolean> checkedList;
	public int checkedNum;
	private int importNum;
	private int gr_id;
	/**
	 * 临时变量
	 */

	/**
	 * Activity回调函数
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// 绑定View
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		// 初始化context
		cGroupDao = new ContactGroupDao(this);
		contactDao = new ContactDao(this);

		// 初始化View成员变量
		initView();
		
		// 初始化ActionBar
		initActionBar();

		// 初始化数据
		initData();

		// 初始化左部菜单
		initDrawer(savedInstanceState);

		// 初始化监听器
		initListener();

	}

	@Override
	protected void onResume() {
		refreshData();
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			gr_id = data.getExtras().getInt("gr_id");
			refreshData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.contact, menu);
		
		searchItem = menu.findItem(R.id.action_contact_search);
		groupItem = menu.findItem(R.id.action_contact_group);
		overflowItem = menu.findItem(R.id.action_contact_overflow);
		subMenu = overflowItem.getSubMenu();
		searchLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_action_search, null);
		searchText = (EditText) searchLayout.findViewById(R.id.edittext_search);
		
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 菜单(ActionBar)选项点击事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (abdToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case android.R.id.home:
			searchText.setVisibility(View.GONE);
			searchText.setInputType(InputType.TYPE_NULL);
			abdToggle.setDrawerIndicatorEnabled(true);
			menu.clear();
			onCreateOptionsMenu(menu);
			refreshData();
			return true;
		
		case R.id.action_contact_search:
			subMenu.add(groupItem.getGroupId(), groupItem.getItemId(), groupItem.getOrder(), groupItem.getTitle());
			menu.removeItem(R.id.action_contact_group);
			menu.removeItem(R.id.action_contact_search);
			groupItem = menu.findItem(R.id.action_contact_group);
			groupItem.setIcon(R.drawable.ic_action_group);
			abdToggle.setDrawerIndicatorEnabled(false);
			actionBar.setCustomView(searchLayout);			
			searchText.requestFocus();
			searchText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
			
			searchText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					String tmpstr = s.toString();
					listDada.clear();
					listDada.addAll(contactDao.query(tmpstr));
					resortData();
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {				
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {				
				}
			});
			
			return true;
			
		case R.id.action_contact_group:
			Intent intentGroup = new Intent(this, ContactGroupActivity.class);
			startActivityForResult(intentGroup, 0);
			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			return true;
			
		case R.id.action_contact_add:
			Intent intentAdd = new Intent(this, ContactEditActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("mode", ContactEditActivity.CONTACT_ADD);

			// TODO 判断当前激活的群组传给ContactEditActivity
			intentAdd.putExtras(bundle);
			startActivity(intentAdd);
			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			return true;
		/*	
		case R.id.action_contact_random:
			ContactModel contactModel = new ContactModel();
			String newName = CommonUtils.generateString(5);
			contactModel.setName(newName);
			contactModel.setIndex_name(newName);
			contactDao.insert(contactModel);
			refreshData();
			return true;
		*/
		case R.id.action_contact_import:
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("导入中...");
			progressDialog.setCancelable(true);
			progressDialog.show();
			
			new Thread(importRunnable).start();
			return true;
			
		case R.id.action_contact_clear:
			Dialog clearDialog = new AlertDialog.Builder(ContactActivity.this)
				.setTitle("确定清空")
				.setMessage("清空所有的联系人？")
				.setPositiveButton("清空", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						contactDao.clear();
						refreshData();
						Toast.makeText(ContactActivity.this, "联系人已清空", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0,
							int arg1) {
					}
				}).create();
			clearDialog.show();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		abdToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		abdToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * 线程对象
	 */
	private Runnable importRunnable = new Runnable() {
		
		@Override
		public void run() {
			importContact();
			runOnUiThread(succRunnable);
			progressDialog.dismiss();
		}
	};	
	
	private Runnable succRunnable = new Runnable() {
		@Override  
	    public void run() {
			refreshData();
			Toast.makeText(ContactActivity.this, "已导入" +importNum+ "个联系人", Toast.LENGTH_SHORT).show();
	    }
	};

	/**
	 * 自定义成员对象
	 */

	/**
	 * 抽屉菜单选项点击事件
	 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	/**
	 * 自定义方法
	 */
	private void initView() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_contact);
		menuListView = (ListView) findViewById(R.id.listview_menu_contact);
		listHeaderLayout = (LinearLayout) findViewById(R.id.layout_contact_list_header);
		listHeaderText = (TextView) findViewById(R.id.textview_contact_list_header);
		contactListView = (ListView) findViewById(R.id.listview_contact);

		//updateListViewMinPage(contactListView);
		contactListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		contactListView.setFastScrollEnabled(true);
		contactListView.setFastScrollAlwaysVisible(true);
		contactListView.setDivider(null);
	}

	private void initData() {
		listDada = new ArrayList<ContactModel>();
		actionData = new ArrayList<String>();
		
		gr_id = -1;
		actionAdapter = new ArrayAdapter<String>(this, R.layout.list_item_menu, actionData);
		
		actionBar.setListNavigationCallbacks(actionAdapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				if (itemPosition == 0) {
					gr_id = -1;
					refreshData();
					return false;
				}
				else if (itemPosition == 1) {
					gr_id = 0;
				}
				else {
					gr_id = cGroupModels.get(itemPosition-2).get_id();
				}
				refreshData();
				return false;
			}
		});
		
		arrayAdapter = new ContactAdapter(this, R.layout.list_item_contact,
				R.id.textview_contact_text, listDada);
		contactListView.setAdapter(arrayAdapter);
		
		checkedList = new ArrayList<Boolean>(listDada.size());

		refreshData();
	}

	private void refreshData() {
		listDada.clear();
		
		if (gr_id == -1) {
			listDada.addAll(contactDao.query());
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
		else {
			listDada.addAll(contactDao.query(gr_id, ContactDao.QUERY_BY_GROUP));
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		}
		
		actionData.clear();
		actionData.add("全部");
		actionData.add("默认群组");
		cGroupModels = cGroupDao.query();
		for (ContactGroupModel contactGroupModel : cGroupModels) {
			actionData.add(contactGroupModel.getName());
		}
		
		actionAdapter.notifyDataSetChanged();
		resortData();
	}
	
	private void resortData() {
		Comparator<ContactModel> comparator = new ContactComparator();
		Collections.sort(listDada, comparator);

		checkedList.clear();
		for (int i = 0; i < listDada.size(); i++) {
			checkedList.add(false);
		}
		checkedNum = 0;
		arrayAdapter.notifyDataSetChanged();
		
		if (listDada.size() == 0) {
			listHeaderLayout.setVisibility(View.GONE);
		}
		else {
			listHeaderLayout.setVisibility(View.VISIBLE);
		}
	}

	private void initActionBar() {
		actionBar = getActionBar();
		actionBar.setTitle(getResources().getString(R.string.activity_contact));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		
	}

	private void initDrawer(Bundle savedInstanceState) {

		menuListData = getResources().getStringArray(R.array.drawer_items);
		drawerLayout.setDrawerShadow(R.drawable.bg_drawer_shadow,
				GravityCompat.START);
		menuListView.setAdapter(new ArrayAdapter<String>(ContactActivity.this,
				R.layout.list_item_menu, menuListData));
		menuListView.setOnItemClickListener(new DrawerItemClickListener());
		abdToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};
		drawerLayout.setDrawerListener(abdToggle);
		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	private void initListener() {
		contactListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (listDada.size()>0) {
					listHeaderText.setText(arrayAdapter.sections[0]);
					int section = arrayAdapter.getSectionForPosition(firstVisibleItem);
					int nextSecPosition = arrayAdapter.getPositionForSection(section + 1);
					if (firstVisibleItem != lastFirstVisibleItem) {
						MarginLayoutParams params = (MarginLayoutParams) listHeaderLayout.getLayoutParams();
						params.topMargin = 0;
						listHeaderLayout.setLayoutParams(params);
						listHeaderText.setText(arrayAdapter.sections[section]);
					}
					if (nextSecPosition == firstVisibleItem + 1) {
						View childView = view.getChildAt(0);
						if (childView != null) {
							int titleHeight = listHeaderLayout.getHeight();
							int bottom = childView.getBottom();
							MarginLayoutParams params = (MarginLayoutParams) listHeaderLayout
									.getLayoutParams();
							if (bottom < titleHeight) {
								float pushedDistance = bottom - titleHeight;
								params.topMargin = (int) pushedDistance;
								listHeaderLayout.setLayoutParams(params);
							} else {
								if (params.topMargin != 0) {
									params.topMargin = 0;
									listHeaderLayout.setLayoutParams(params);
								}
							}
						}
					}
				}
			}
		});
		
		contactListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(ContactActivity.this, ContactDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("_id", listDada.get(arg2).get_id());
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			}
		});

		contactListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			@Override
			public boolean onPrepareActionMode(ActionMode arg0,
					Menu arg1) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode arg0) {
				isContextMenu = false;
				checkedNum = 0;
				for (int i = 0; i < listDada.size(); i++) {
					checkedList.set(i, false);
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
				getMenuInflater().inflate(R.menu.contact_context, arg1);
				isContextMenu = true;
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode arg0,
					MenuItem arg1) { 
				switch (arg1.getItemId()) {
				// 全选
				case R.id.action_contact_context_all:
					for (int j = 0; j < checkedList.size(); j++) {
						if (!checkedList.get(j)) {
							contactListView.performItemClick(
									contactListView.getChildAt(j), j, 0);
						}
					}
					arrayAdapter.notifyDataSetChanged();
					return true;
					// 删除
				case R.id.action_contact_context_delete:
					final ActionMode actionMode = arg0;
					Dialog deleteDialog = new AlertDialog.Builder(ContactActivity.this)
						.setTitle("确定删除")
						.setMessage("删除选中的联系人？")
						.setPositiveButton("删除", new DialogInterface.OnClickListener() {
							@Override
							public void onClick( DialogInterface arg0, int arg1) {
								int num = 0;
								for (int i = 0; i < checkedList.size(); i++) {
									if (checkedList.get(i)) {
										num++;
										contactDao.delete(listDada.get(i).get_id());
									}
								}
								refreshData();
								Toast.makeText(ContactActivity.this,"已删除 " + num + "项",Toast.LENGTH_LONG).show();
								actionMode.finish();
							}
						})
						.setNegativeButton("取消", null)
						.create();
					deleteDialog.show();
					return true;
				default:
					return false;
				}
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode arg0,
					int arg1, long arg2, boolean arg3) {
				if (arg3) {
					checkedNum++;
				} else {
					checkedNum--;
				}
				checkedList.set(arg1, arg3);
				arg0.setTitle("已选中 " + checkedNum + " 项");
				arrayAdapter.notifyDataSetChanged();
			}
		});
	}
	
	private void importContact() {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Cursor cursor = getContentResolver().query(uri, null, null, null, "sort_key");
		importNum = 0;
		if (cursor.moveToFirst()) {
			do {
				String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));  
				String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				int phoneCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            	String phoneNumber = "";
				if (phoneCount > 0) {
                    Cursor phones = getContentResolver().query(
                    	ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    	ContactsContract.CommonDataKinds.Phone.CONTACT_ID  
                                    + " = " + contactId, null, null);  
                    if (phones.moveToFirst()) {
                    	phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); 
                    }
                }
				ContactModel cModel = new ContactModel();
				Chinese chinese = new Chinese();
				cModel.setName(name);
				cModel.setIndex_name(chinese.getStringPinYin(name));
				cModel.setPhone(phoneNumber);
				contactDao.insert(cModel);
				importNum ++;
			} while (cursor.moveToNext());
		}
	}

	private void selectItem(int position) {
		menuListView.setItemChecked(position, true);
		drawerLayout.closeDrawer(menuListView);
		refreshData();
	}

	public void updateListViewMinPage(ListView listView) {
		try {
			Field field = AbsListView.class.getDeclaredField("mFastScroller");
			field.setAccessible(true);
			Object object = field.get(listView);
			Field minPages = object.getClass().getDeclaredField("MIN_PAGES");
			minPages.setAccessible(true);
			minPages.set(object, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
