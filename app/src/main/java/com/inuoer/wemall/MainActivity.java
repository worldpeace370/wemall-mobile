package com.inuoer.wemall;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.inuoer.fragment.CartFragment;
import com.inuoer.fragment.DiscoveryFragment;
import com.inuoer.fragment.DrawerFragment;
import com.inuoer.fragment.MainFragment;
import com.inuoer.fragment.WoFragment;
import com.inuoer.manager.ObserverManager;
import com.inuoer.util.ActivityManager;
import com.inuoer.util.AsyncImageLoader;
import com.inuoer.util.CartData;
import com.inuoer.util.Config;
import com.inuoer.util.SharedDataSave;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DrawerFragment.OnDrawerItemSelectedListener, DrawerLayout.DrawerListener{
	private List<Fragment> mFragmentList = new ArrayList<Fragment>();
	private ArrayList<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();

	private final int mQRRequestCode = 0x22;
	private final int mCityRequestCode = 0x33;
	private Context mContext;
	private DrawerLayout mDrawerLayout;
	private RadioButton[] arrRadioButtons;
	//当前书签对应的Fragment索引
	private int currentTabIndex = 0;
	//每个Fragment的标题字符串数组
	private String[] mTitleArray;
	//抽屉布局的Fragment
	private DrawerFragment mDrawerFragment;

	private Toolbar mToolBar;
	private ActionBarDrawerToggle mToggle;
	private TextView mTab_title;
	private ImageView mQr_code;
	private MainFragment mMainFragment;
	private Drawable mTitleDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTransparent();
		mContext = this;
		//进入MainActivity之后，就不是第一次了，将配置改为false
		SharedDataSave.save(mContext, Config.THE_FIRST_INSTALL, false);
		initDrawer();
		initToolBar();
		initView();
		initFragmentList();
		initTabs();

	}
	/**
	 * 初始化抽屉布局
	 */
	private void initDrawer() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		//设置DrawerListener监听器，重写四个方法
		mDrawerLayout.setDrawerListener(this);
		mDrawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mDrawerFragment.setOnDrawerItemSelectedListener(this);
	}
	/**
	 * 初始化ToolBar
	 */
	private void initToolBar(){
		mToolBar = (Toolbar) findViewById(R.id.toolbar);
		if (!ActivityManager.hasKitKat()){//API<14
			ViewGroup.LayoutParams layoutParams =  mToolBar.getLayoutParams();
			layoutParams.height = (int)(50 * getResources().getDisplayMetrics().density);//设置50dp高，height的值是px，所以需要转化
			mToolBar.setLayoutParams(layoutParams);
		}
		//取消App name名字，靠近菜单附近的
		mToolBar.setTitle("");
		//mToolBar.showOverflowMenu();
		setSupportActionBar(mToolBar);
		getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//ToolBar的开关,toggle,设置后，点击开关打开抽屉布局或者关闭抽屉布局
		mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar,
				R.string.navigation_drawer_open, R.string.navigation_drawer_close);

		mToggle.syncState();
	}

	/**
	 * 初始化ToolBar的title和二维码扫描事件
	 */
	private void initView(){
		mTitleArray = getResources().getStringArray(R.array.title_name);
		mQr_code = (ImageView) mToolBar.findViewById(R.id.qr_code);
		mQr_code.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MipcaCapture.class);
				startActivityForResult(intent, mQRRequestCode);
			}
		});
		mTab_title = (TextView) mToolBar.findViewById(R.id.tab_title);
		mTab_title.setText(mTitleArray[0]);
		mTitleDrawable = getResources().getDrawable(R.drawable.iconfont_xiala);
		mTitleDrawable.setBounds(0, 0, mTitleDrawable.getMinimumWidth()-6, mTitleDrawable.getMinimumHeight()-6);
		mTab_title.setCompoundDrawables(null, null, mTitleDrawable, null);
		mTab_title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, CityChoiceActivity.class);
				startActivityForResult(intent, mCityRequestCode);
			}
		});
	}

	/**
	 * 初始化mFragmentList
	 */
	private void initFragmentList() {
		mMainFragment = MainFragment.newInstance(Config.API_GET_GOODS, null);
		mFragmentList.add(mMainFragment);
		mFragmentList.add(new CartFragment());
		mFragmentList.add(new DiscoveryFragment());
		mFragmentList.add(new WoFragment());
		//默认Fragment,为shopping页面--刚进入APP的首页
		getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_content, mMainFragment).commit();
	}
	/**
	 * 初始化主Activity页面下面的书签导航,并点击可以切换不同的页面
	 */
	private void initTabs(){
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		//为新建的RadioButton数组创建大小,该数组里面包含RadioGroup里面所有的RadioButton
		arrRadioButtons = new RadioButton[radioGroup.getChildCount()];
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			arrRadioButtons[i] = (RadioButton) radioGroup.getChildAt(i);
		}
		//设置第一个RadioButton默认被选中
		arrRadioButtons[0].setChecked(true);
		//mRadioGroup的监听事件,改变对应RadioButton的标题颜色和切换Fragment
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				for (int i = 0; i < arrRadioButtons.length; i++) {
					//如果找到了按下的那个RadioButton
					if (arrRadioButtons[i].getId() == checkedId) {
						//切换Fragment
						switchFragment(i);
					}
				}
			}
		});
	}

	/**将四个书签页面全部加到List<Fragment> mFragmentList里面,在这个函数里进行Fragment切换,管理
	 * 利用hide和show可以大大节省以后加载碎片的性能开销
	 * @param targetTabIndex
	 */
	private void switchFragment(int targetTabIndex){
		//主页面和其他页面需要在TooBar上显示不同的功能特性，刚开始布局的时候应该把ToolBar加入到Fragment中而不是Activity，这样可能会很方便
		if (targetTabIndex == 0){
			mQr_code.setVisibility(View.VISIBLE);
			mTab_title.setText(mTitleArray[0]);
			mTab_title.setCompoundDrawables(null, null, mTitleDrawable, null);
			//设置可点击
			mTab_title.setEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar,
					R.string.navigation_drawer_open, R.string.navigation_drawer_close);

			mToggle.syncState();
		}else {
			mQr_code.setVisibility(View.INVISIBLE);
			//设置不可点击
			mTab_title.setEnabled(false);
			mTab_title.setText(mTitleArray[targetTabIndex]);
			mTab_title.setCompoundDrawables(null, null, null, null);
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		//Fragment currentFragment = mFragmentList.get(currentTabIndex);
		Fragment targetFragment = mFragmentList.get(targetTabIndex);
		transaction.replace(R.id.framelayout_content, targetFragment);
//		//如果targetFragment没有被增加,隐藏currentFragment,增加targetFragment
//		if(!targetFragment.isAdded()){
//			transaction.hide(currentFragment).add(R.id.framelayout_content, targetFragment);
//		}else{//如果targetFragment添加过了,隐藏currentFragment,显示targetFragment
//			transaction.hide(currentFragment).show(targetFragment);
//		}
//		//更新currentTabIndex
//		currentTabIndex = targetTabIndex;
		transaction.commit();
	}

	/**
	 * 如果MainActivity中有Fragment attach了，则当这个Fragment执行startActivityForResult(),
	 * 并重写onActivityResult()方法时，所在的MainActivity也重写了onActivityResult()时，其必须调用
	 * super.onActivityResult(requestCode, resultCode, data);否则Fragment中的onActivityResult()
	 * 不会被回调
	 * @param requestCode
	 * @param resultCode
	 * @param data
     */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		listItem = mMainFragment.getListItem();
		if (this.mCityRequestCode == requestCode && resultCode == 0x66){
			String cityName = data.getExtras().getString("cityName");
			mTab_title.setText(cityName+"美食");
		}
		if (this.mQRRequestCode == requestCode && resultCode == 0x55){
			String result = data.getExtras().getString("result");
			//得到二维码扫描来的物品position信息
			final int position;
			if (!TextUtils.isEmpty(result)){
				position= Integer.parseInt(result)-1;
			}else {
				position = 0;
			}

			LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_detail, null);
			final Dialog dialog = new Dialog(mContext);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(layout);
			dialog.show();
			//显示菜品图案的ImageView
			final ImageView imageView = (ImageView) layout.findViewById(R.id.dialog_detail_big_image);
			AsyncImageLoader imageLoader = new AsyncImageLoader(mContext);
			//设置缓存到文件系统中去
			imageLoader.setCache2File(true);
			imageLoader.downloadImage(listItem.get(position).get("image").toString(), true,
					new AsyncImageLoader.ImageCallback() {
						@Override
						public void onImageLoaded(Bitmap bitmap, String imageUrl) {
							imageView.setImageBitmap(bitmap);
						}
					});
			TextView textViewPrice = (TextView)layout.findViewById(R.id.dialog_detail_single_price);
			textViewPrice.setText(listItem.get(position).get("price").toString());
			TextView textViewName = ((TextView) layout.findViewById(R.id.dialog_detail_title_name));
			textViewName.setText(listItem.get(position).get("name").toString());
			final TextView textViewNum = (TextView) layout.findViewById(R.id.count);
			layout.findViewById(R.id.dialog_detail_close).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			layout.findViewById(R.id.dialog_detail_addcart).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			//点击增加物品数
			layout.findViewById(R.id.plus_btn).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int num = Integer.parseInt(textViewNum.getText().toString()) + 1;
					textViewNum.setText(String.valueOf(num));
					CartData.editCart(listItem.get(position).get("id").toString(),
							listItem.get(position).get("name").toString(),
							listItem.get(position).get("price").toString(),
							String.valueOf(num),
							listItem.get(position).get("image").toString());
				}
			});
			//点击减少物品数
			layout.findViewById(R.id.minus_btn).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int num = Integer.parseInt(textViewNum.getText().toString()) - 1;
					if (num >=0){
						textViewNum.setText(String.valueOf(num));

						if (num == 0) {
							CartData.removeCart(listItem.get(position)
									.get("id").toString());
						} else {
							CartData.editCart(listItem.get(position).get("id").toString(),
									listItem.get(position).get("name").toString(),
									listItem.get(position).get("price").toString(),
									String.valueOf(num),
									listItem.get(position).get("image").toString());
						}
					}
				}
			});
		}
	}

	/**点击左侧抽屉菜单后的回调，为MainFragment传递不同的参数，得到不同的MainFragment
	 * mDrawerFragment的回调方法
	 * @param menu_id
	 */
	@Override
	public void onDrawerItemSelected(String menu_id) {
		mDrawerLayout.closeDrawer(GravityCompat.START);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if ("0".equals(menu_id)){
			transaction.replace(R.id.framelayout_content, MainFragment.newInstance(Config.API_GET_GOODS, null)).commit();
		}else {
			transaction.replace(R.id.framelayout_content, MainFragment.newInstance(Config.API_GET_GOODS, menu_id)).commit();
		}
	}

	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {//drawer滑动的时候回调
		mToggle.onDrawerSlide(drawerView, slideOffset);
	}

	@Override
	public void onDrawerOpened(View drawerView) { //打开drawer
		mToggle.onDrawerOpened(drawerView);  //需要把开关变为打开
	}

	@Override
	public void onDrawerClosed(View drawerView) { //关闭drawer
		mToggle.onDrawerClosed(drawerView);  //需要把开关也变为关闭
		invalidateOptionsMenu();//重新绘制选项菜单
	}

	@Override
	public void onDrawerStateChanged(int newState) {    //drawer状态改变的回调
		mToggle.onDrawerStateChanged(newState);
	}
	/**
	 * 按下返回键，关闭抽屉布局
	 */
	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * 设置状态栏透明
	 */
	private void setTransparent() {
		if (ActivityManager.hasKitKat() && !ActivityManager.hasLollipop()){
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			//getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}else if (ActivityManager.hasLollipop()){
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
					| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ObserverManager.getObserverManager().deleteObservers();
	}
}
