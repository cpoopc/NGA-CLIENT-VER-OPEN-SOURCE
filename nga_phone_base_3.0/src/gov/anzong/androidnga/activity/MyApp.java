package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sp.phone.bean.Board;
import sp.phone.bean.BoardHolder;
import sp.phone.bean.Bookmark;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.util.Log;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MyApp extends Application implements PerferenceConstant {
	final private static String TAG = MyApp.class.getSimpleName();
	public final static int version = 2012;
	private PhoneConfiguration config = null;
	boolean newVersion = false;
	static final String RECENT = "最近访问";
	static final String ADDFID = "用户自定义";
	
	
	@Override
	public void onCreate() {
		Log.w(TAG,"app nga androind start");
		//CrashHandler crashHandler = CrashHandler.getInstance();
		//crashHandler.init(getApplicationContext());
		if(config == null)
			config = PhoneConfiguration.getInstance();
		initUserInfo();
		loadConfig();
		if(ActivityUtil.isGreaterThan_2_1())
			initPath();

		initImageLoader();
		loadDefaultBoard();
		super.onCreate();
	}
	
	public void initImageLoader(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.loading)
		.showImageForEmptyUri(R.drawable.loading_null)
		.showImageOnFail(R.drawable.loading_fail)
		.cacheInMemory(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.cacheOnDisk(true)
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
    	int MEM_CACHE_SIZE = 1024 * 1024 * ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass() / 3;
		if(ActivityUtil.isGreaterThan_2_1()){
			File baseDir = getExternalCacheDir();
			File sdCardDir;
			if(baseDir!= null)
				sdCardDir = new File(baseDir.getAbsolutePath()+"/dbmeizi_cache");
			else
				sdCardDir =  new File(android.os.Environment
						.getExternalStorageDirectory().getPath()
						+"/Android/data/gov.anzong.androidnga/cache/dbmeizi_cache");
				ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.denyCacheImageMultipleSizesInMemory()
				.defaultDisplayImageOptions(options)
				.memoryCache(new LruMemoryCache(MEM_CACHE_SIZE))
		        .memoryCacheSize(MEM_CACHE_SIZE)
		        .discCache(new UnlimitedDiscCache(sdCardDir))
		        .build();
				ImageLoader.getInstance().init(config);
		}else{
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.denyCacheImageMultipleSizesInMemory()
			.defaultDisplayImageOptions(options)
			.memoryCache(new LruMemoryCache(MEM_CACHE_SIZE))
	        .memoryCacheSize(MEM_CACHE_SIZE)
	        .writeDebugLogs()
	        .build();
			ImageLoader.getInstance().init(config);
		}
	}
	
	public BoardHolder loadDefaultBoard(){
		
		BoardHolder boards = new BoardHolder();
		
		int i= 0;
		
		SharedPreferences share = getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		String recentStr = share.getString(RECENT_BOARD, "");
		List<Board> recentList = null;
		if(!StringUtil.isEmpty(recentStr)){
			recentList = JSON.parseArray(recentStr, Board.class);
			if(recentList != null){
				for(int j = 0;j< recentList.size();j++){
					boards.add(recentList.get(j));
				}
			}
		}
		if(recentList != null)
		{
			boards.addCategoryName(i, RECENT);
			i++;
		}
		
		boards.add(new Board(i, "7", "议事厅", R.drawable.p7));
		boards.add(new Board(i, "323", "国服以外讨论", R.drawable.p323));
		boards.add(new Board(i, "10", "银色黎明", R.drawable.p10));
		boards.add(new Board(i, "230", "艾泽拉斯风纪委员会", R.drawable.p230));
		boards.add(new Board(i, "387", "潘大力亚之迷雾", R.drawable.p387));
		boards.add(new Board(i, "430", "德拉诺之王", R.drawable.p430));
		boards.add(new Board(i, "305", "305权贵区", R.drawable.p305));
		boards.add(new Board(i, "11", "诺森德埋骨地", R.drawable.p11));
		boards.addCategoryName(i, "综合讨论");
		i++;

		boards.add(new Board(i, "-7", "大漩涡", R.drawable.p354));
		boards.add(new Board(i, "-362960", "艾欧泽亚", R.drawable.p362960));
        boards.add(new Board(i, "-343809", "汽车俱乐部", R.drawable.p343809));
		boards.add(new Board(i, "-81981", "生命之杯", R.drawable.p81981));
		boards.add(new Board(i, "-576177", "影音讨论区", R.drawable.p576177));
		boards.add(new Board(i, "-43", "军事历史", R.drawable.p43));
        boards.add(new Board(i, "414", "游戏综合讨论", R.drawable.p414));
        boards.add(new Board(i, "415", "主机游戏综合讨论", R.drawable.p415));
        boards.add(new Board(i, "431", "风暴英雄", R.drawable.p431));
        boards.add(new Board(i, "436", "消费电子 IT新闻", R.drawable.p436));
        boards.add(new Board(i, "-187579", "大漩涡历史博物馆", R.drawable.p187579));
		boards.addCategoryName(i, "大漩涡系列");
		i++;
		
		
		boards.add(new Board(i, "390", "五晨寺", R.drawable.p390));
		boards.add(new Board(i, "320", "黑锋要塞", R.drawable.p320));
		boards.add(new Board(i, "181", "铁血沙场", R.drawable.p181));
		boards.add(new Board(i, "182", "魔法圣堂", R.drawable.p182));
		boards.add(new Board(i, "183", "信仰神殿", R.drawable.p183));
		boards.add(new Board(i, "185", "风暴祭坛", R.drawable.p185));
		boards.add(new Board(i, "186", "翡翠梦境", R.drawable.p186));
		boards.add(new Board(i, "187", "猎手大厅", R.drawable.p187));
		boards.add(new Board(i, "184", "圣光之力", R.drawable.p184));
		boards.add(new Board(i, "188", "恶魔深渊", R.drawable.p188));
		boards.add(new Board(i, "189", "暗影裂口", R.drawable.p189));
		boards.addCategoryName(i, "职业讨论区");
		i++;
		
		boards.add(new Board(i, "310", "精英议会", R.drawable.p310));
		boards.add(new Board(i, "190", "任务讨论", R.drawable.p190));
		boards.add(new Board(i, "213", "战争档案", R.drawable.p213));
		boards.add(new Board(i, "218", "副本专区", R.drawable.p218));
		boards.add(new Board(i, "258", "战场讨论", R.drawable.p258));
		boards.add(new Board(i, "272", "竞技场", R.drawable.p272));
		boards.add(new Board(i, "191", "地精商会", R.drawable.p191));
		boards.add(new Board(i, "200", "插件研究", R.drawable.p200));
		boards.add(new Board(i, "240", "BigFoot", R.drawable.p240));
		boards.add(new Board(i, "274", "插件发布", R.drawable.p274));
		boards.add(new Board(i, "315", "战斗统计", R.drawable.p315));
		boards.add(new Board(i, "333", "DKP系统", R.drawable.p333));
		boards.add(new Board(i, "327", "成就讨论", R.drawable.p327));
		boards.add(new Board(i, "388", "幻化讨论", R.drawable.p388));
		boards.add(new Board(i, "411", "宠物讨论", R.drawable.p411));
		boards.add(new Board(i, "255", "公会管理", R.drawable.p255));
		boards.add(new Board(i, "306", "人员招募", R.drawable.p306));
		boards.addCategoryName(i, "冒险心得");
		i++;
		
		boards.add(new Board(i, "264", "卡拉赞剧院", R.drawable.p264));
		boards.add(new Board(i, "8", "大图书馆", R.drawable.p8));
		boards.add(new Board(i, "102", "作家协会", R.drawable.p102));
		boards.add(new Board(i, "124", "壁画洞窟", R.drawable.p124));
		boards.add(new Board(i, "254", "镶金玫瑰", R.drawable.p254));
		boards.add(new Board(i, "355", "龟岩兄弟会", R.drawable.p355));
		boards.add(new Board(i, "116", "奇迹之泉", R.drawable.p116));
		boards.addCategoryName(i, "麦迪文之塔");
		i++;
		
		
		boards.add(new Board(i, "193", "帐号安全", R.drawable.p193));
		boards.add(new Board(i, "334", "PC软硬件", R.drawable.p334));
		boards.add(new Board(i, "201", "系统问题", R.drawable.p201));
		boards.add(new Board(i, "335", "网站开发", R.drawable.p335));
		boards.addCategoryName(i, "系统软硬件讨论");
		i++;

        boards.add(new Board(i, "414", "游戏综合讨论", R.drawable.p414));
        boards.add(new Board(i, "428", "手机游戏", R.drawable.p428));
        boards.add(new Board(i, "431", "风暴英雄", R.drawable.p431));
        boards.add(new Board(i, "-452227", "口袋妖怪", R.drawable.p452227));
        boards.add(new Board(i, "426", "智龙迷城", R.drawable.p426));
		boards.add(new Board(i, "-51095", "部落战争", R.drawable.p51095));
		boards.add(new Board(i, "-362960", "最终幻想14", R.drawable.p362960));
        boards.add(new Board(i, "-6194253", "战争雷霆", R.drawable.p6194253));
        boards.add(new Board(i, "427", "怪物猎人", R.drawable.p427));
        boards.add(new Board(i, "-47218", "地下城与勇士", R.drawable.p47218));
		boards.add(new Board(i, "425", "行星边际2", R.drawable.p425));
		boards.add(new Board(i, "422", "炉石传说", R.drawable.p422));
		boards.add(new Board(i, "-65653", "剑灵", R.drawable.p65653));
		boards.add(new Board(i, "412", "巫师之怒", R.drawable.p412));
		boards.add(new Board(i, "-235147", "激战2", R.drawable.p235147));
		boards.add(new Board(i, "442", "逆战", R.drawable.p442));
		boards.add(new Board(i, "-46468", "坦克世界", R.drawable.p46468));
		boards.add(new Board(i, "432", "战机世界", R.drawable.p432));
		boards.add(new Board(i, "441", "战舰世界", R.drawable.p441));
		boards.add(new Board(i, "321", "DotA", R.drawable.p321));
		boards.add(new Board(i, "375", "DotA联赛饰品", R.drawable.p375));
		boards.add(new Board(i, "-2371813", "EVE", R.drawable.p2371813));
		boards.add(new Board(i, "-7861121", "剑叁 ", R.drawable.p7861121));
		boards.add(new Board(i, "448", "剑叁同人 ", R.drawable.p448));
		boards.add(new Board(i, "-793427", "斗战神", R.drawable.p793427));
		boards.add(new Board(i, "332", "战锤40K", R.drawable.p332));
		boards.add(new Board(i, "416", "火炬之光2", R.drawable.p416));
		boards.add(new Board(i, "406", "星际争霸2", R.drawable.p406));
		boards.add(new Board(i, "420", "MT Online", R.drawable.p420));
        boards.add(new Board(i, "424", "圣斗士", R.drawable.p424));
		boards.add(new Board(i, "-1513130", "鲜血兄弟会", R.drawable.p1513130));
		boards.add(new Board(i, "433", "神雕侠侣", R.drawable.p433));
		boards.add(new Board(i, "434", "神鬼幻想", R.drawable.p434));
		boards.add(new Board(i, "435", "上古卷轴Online", R.drawable.p435));
		boards.add(new Board(i, "443", "FIFA Online 3", R.drawable.p443));
		boards.add(new Board(i, "444", "刀塔传奇", R.drawable.p444));
		boards.add(new Board(i, "445", "迷你西游", R.drawable.p445));
		boards.add(new Board(i, "447", "锁链战记", R.drawable.p447));
		boards.add(new Board(i, "-532408", "沃土", R.drawable.p532408));
		boards.add(new Board(i, "353", "纽沃斯英雄传", R.drawable.p353));
		boards.addCategoryName(i, "其他游戏");
		i++; 
		
		boards.add(new Board(i, "318", "暗黑破坏神3", R.drawable.p318));
		boards.add(new Board(i, "403", "购买/安装/共享", R.drawable.p403));
		boards.add(new Board(i, "393", "背景故事与文艺作品", R.drawable.p393));
		boards.add(new Board(i, "400", "职业讨论区", R.drawable.p400));
		boards.add(new Board(i, "395", "野蛮人", R.drawable.p395));
		boards.add(new Board(i, "396", "猎魔人", R.drawable.p396));
		boards.add(new Board(i, "397", "武僧", R.drawable.p397));
		boards.add(new Board(i, "398", "巫医", R.drawable.p398));
		boards.add(new Board(i, "399", "魔法师", R.drawable.p399));
		boards.add(new Board(i, "446", "圣教军", R.drawable.p446));
		boards.addCategoryName(i, "暗黑破坏神");
		i++; 

		boards.add(new Board(i, "422", "炉石传说", R.drawable.p422));
		boards.add(new Board(i, "429", "战术讨论", R.drawable.p429));
		boards.add(new Board(i, "450", "文章存档", R.drawable.p450));
		boards.addCategoryName(i, "炉石传说");
		i++; 

		boards.add(new Board(i, "-152678", "英雄联盟", R.drawable.p152678));
		boards.add(new Board(i, "418", "游戏视频", R.drawable.p418));
		boards.addCategoryName(i, "英雄联盟");
		i++; 
		

		boards.add(new Board(i, "-447601", "二次元国家地理", R.drawable.p447601));
		boards.add(new Board(i, "-84", "模玩之魂", R.drawable.p84));
		boards.add(new Board(i, "-8725919", "小窗视界", R.drawable.p8725919));
		boards.add(new Board(i, "-965240", "树洞", R.drawable.p965240));
		boards.add(new Board(i, "-131429", "红茶馆——小说馆", R.drawable.p131429));
		boards.add(new Board(i, "-608808", "血腥厨房", R.drawable.p608808));
		boards.add(new Board(i, "-469608", "影~视~秀", R.drawable.p469608));
		boards.add(new Board(i, "-55912", "音乐讨论", R.drawable.p55912));
		boards.add(new Board(i, "-522474", "综合体育讨论区", R.drawable.p522474));
		boards.add(new Board(i, "-1068355", "晴风村", R.drawable.p1068355));
		boards.add(new Board(i, "-168888", "育儿版", R.drawable.p168888));
		boards.add(new Board(i, "-54214", "时尚板", R.drawable.p54214));
		boards.add(new Board(i, "-353371", "宠物养成", R.drawable.p353371));
		boards.add(new Board(i, "-538800", "乙女向二次元", R.drawable.p538800));
		boards.add(new Board(i, "-7678526", "麻将科学院", R.drawable.p7678526));
		boards.add(new Board(i, "-202020", "程序员职业交流", R.drawable.p202020));
		boards.add(new Board(i, "-444012", "我们的骑迹", R.drawable.p444012));
		boards.add(new Board(i, "-349066", "开心茶园", R.drawable.p349066));
		boards.add(new Board(i, "-314508", "世界尽头的百货公司", R.drawable.p314508));		
		boards.add(new Board(i, "-2671", "耳机区", R.drawable.p2671));
		boards.add(new Board(i, "-970841", "东方教主陈乔恩", R.drawable.p970841));
		boards.add(new Board(i, "-3355501", "基腐版", R.drawable.p3355501));
		boards.add(new Board(i, "-403298", "怨灵图纸收藏室", R.drawable.p403298));
		boards.add(new Board(i, "-3432136", "飘落的诗章", R.drawable.p3432136));
		boards.add(new Board(i, "-187628", "家居 装修", R.drawable.p187628));
        boards.addCategoryName(i, "个人版面");
		//i++;
		

		SharedPreferences shareaddFid = getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		String addFidStr = share.getString(ADD_FID, "");
		List<Board> addFidList = null;
		if(!StringUtil.isEmpty(addFidStr)){
			addFidList = JSON.parseArray(addFidStr, Board.class);
			if(addFidList != null){
				i++;
				for(int j = 0;j< addFidList.size();j++){
					boards.add(new Board(i,addFidList.get(j).getUrl(),addFidList.get(j).getName(),addFidList.get(j).getIcon()));
				}
			}
		}
		if(addFidList != null)
		{
			boards.addCategoryName(i, ADDFID);
//			i++;
		}
        
        
		return boards;
	}
	
	@TargetApi(8)
	private void initPath(){
		File baseDir = getExternalCacheDir();
		if(baseDir!= null)
			HttpUtil.PATH = baseDir.getAbsolutePath();
		else
			HttpUtil.PATH = android.os.Environment
					.getExternalStorageDirectory().getPath()
					+"/Android/data/gov.anzong.androidnga";
		HttpUtil.PATH_AVATAR = HttpUtil.PATH +
				 "/nga_cache";
		HttpUtil.PATH_NOMEDIA = HttpUtil.PATH + "/.nomedia";
        HttpUtil.PATH_IMAGES = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

	private void initUserInfo() {
		

		
		PhoneConfiguration config = PhoneConfiguration.getInstance();

		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);

		
			final String uid = share.getString(UID, "");
			final String cid = share.getString(CID, "");
			final String replystring = share.getString(PENDING_REPLYS, "");
			final int replytotalnum = Integer.parseInt(share.getString(REPLYTOTALNUM, "0"));
			if (!StringUtil.isEmpty(uid) && !StringUtil.isEmpty(cid) ) {
				config.setUid(uid);
				config.setCid(cid);
				config.setReplyString(replystring);
				config.setReplyTotalNum(replytotalnum);
				String userListString = share.getString(USER_LIST, "");
				final String name = share.getString(USER_NAME, "");
				config.userName = name;
				if(StringUtil.isEmpty(userListString)){
					
					addToUserList(uid,cid,name,replystring,replytotalnum);
	
				}
			}

			boolean downImgWithoutWifi = share.getBoolean(DOWNLOAD_IMG_NO_WIFI, false);
			config.setDownImgNoWifi(downImgWithoutWifi);
			boolean downAvatarNoWifi = share.getBoolean(DOWNLOAD_AVATAR_NO_WIFI, false);
			config.setDownAvatarNoWifi(downAvatarNoWifi);
			
			config.setDb_Cookie(share.getString(DBCOOKIE, ""));

	}

	public void addToUserList(String uid, String cid, String name,String replyString,int replytotalnum){
		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		
		String userListString = share.getString(USER_LIST, "");
		
		List<User> userList = null;
		//new ArrayList<User>();
		if(StringUtil.isEmpty(userListString)){
			userList = new ArrayList<User>();
		}else
		{
			userList = JSON.parseArray(userListString, User.class);
			for( User u : userList){
				if(u.getUserId().equals(uid)){
					userList.remove(u);
					break;
				}
					
			}
		}
		
		User user = new User();
		user.setCid(cid);
		user.setUserId(uid);
		user.setNickName(name);
		user.setReplyString(replyString);
		user.setReplyTotalNum(replytotalnum);
		userList.add(0,user);
		
		userListString = JSON.toJSONString(userList);
		share.edit().putString(UID, uid).putString(CID, cid)
		.putString(USER_NAME, name )
		.putString(PENDING_REPLYS, replyString )
		.putString(REPLYTOTALNUM, String.valueOf(replytotalnum) )
		.putString(USER_LIST, userListString).commit();
	}
	
	

	public void addToMeiziUserList(String uid, String sess){
		SharedPreferences  share = 
				getSharedPreferences(PERFERENCE, MODE_PRIVATE);
		String cookie = "uid="+uid+"; sess="+sess;
		share.edit().putString(DBCOOKIE, cookie).commit();
		config.setDb_Cookie(cookie);
	}
	
	private void loadConfig(){
		
		SharedPreferences share = this.getSharedPreferences(PERFERENCE,
				MODE_PRIVATE);
		if(share.getBoolean(NIGHT_MODE, false))
			ThemeManager.getInstance().setMode(1);
		
		ThemeManager.getInstance().screenOrentation = 
				share.getInt(SCREEN_ORENTATION,ActivityInfo.SCREEN_ORIENTATION_USER);
		
		
		int version_in_config = share.getInt(VERSION, 0);
		if(version_in_config < version){
			newVersion = true;
			Editor editor = share.edit();
			editor.putInt(VERSION, version);
			editor.putBoolean(REFRESH_AFTER_POST, false);
			
			String recentStr = share.getString(RECENT_BOARD, "");
			List<Board> recentList = null;
			if(!StringUtil.isEmpty(recentStr)){
				recentList = JSON.parseArray(recentStr, Board.class);
				if(recentList != null){
					for(int j = 0;j< recentList.size();j++){
						recentList.get(j).setIcon(R.drawable.pdefault);
					}
					recentStr = JSON.toJSONString(recentList);
					editor.putString(RECENT_BOARD,recentStr);
				}
			}
			
			editor.commit();
			
		}
		

		//refresh
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		config.setRefreshAfterPost(
				share.getBoolean(REFRESH_AFTER_POST,false));
		config.setRefreshAfterPost(false);
		
		config.showAnimation = share.getBoolean(SHOW_ANIMATION, false);
		config.useViewCache = share.getBoolean(USE_VIEW_CACHE, true);
		config.showSignature = share.getBoolean(SHOW_SIGNATURE, false);
		config.uploadLocation = share.getBoolean(UPLOAD_LOCATION, false);
		config.showStatic = share.getBoolean(SHOW_STATIC,false);
		config.showReplyButton = share.getBoolean(SHOW_REPLYBUTTON,true);
		config.showColortxt = share.getBoolean(SHOW_COLORTXT,false);
		config.showNewweiba = share.getBoolean(SHOW_NEWWEIBA,false);
		config.showLajibankuai = share.getBoolean(SHOW_LAJIBANKUAI,true);
		config.imageQuality = share.getInt(DOWNLOAD_IMG_QUALITY_NO_WIFI, 0);
		config.HandSide = share.getInt(HANDSIDE, 0);
		config.fullscreen = share.getBoolean(FULLSCREENMODE, false);
		config.playMode = share.getInt(PLAY_MODE, 0);
		config.blackgunsound = share.getInt(BLACKGUN_SOUND, 0);


		//font
		final float defTextSize = 21.0f;//new TextView(this).getTextSize();
		final int defWebSize = 16;//new WebView(this).getSettings().getDefaultFontSize();
		
		final float textSize = share.getFloat(TEXT_SIZE, defTextSize);
		final int webSize = share.getInt(WEB_SIZE, defWebSize);
		config.setTextSize(textSize);
		config.setWebSize(webSize);
		
		boolean notification = share.getBoolean(ENABLE_NOTIFIACTION, true);
		boolean notificationSound = share.getBoolean(NOTIFIACTION_SOUND, true);
		config.notification = notification;
		config.notificationSound = notificationSound;
		
		config.nikeWidth = share.getInt(NICK_WIDTH, 100);
		
		int uiFlag = share.getInt(UI_FLAG, 0);
		config.setUiFlag(uiFlag);
		
		//bookmarks
		String bookmarkJson = share.getString(BOOKMARKS, "");
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		try{
		if(!bookmarkJson.equals(""))
			bookmarks=JSON.parseArray(bookmarkJson, Bookmark.class);
		}catch(Exception e){
			Log.e("JSON_error",Log.getStackTraceString(e));
		}
		PhoneConfiguration.getInstance().setBookmarks(bookmarks);
		
		
		
	}
	
	
	
	public boolean isNewVersion() {
		return newVersion;
	}

	public void setNewVersion(boolean newVersion) {
		this.newVersion = newVersion;
	}

	

	

	public PhoneConfiguration getConfig() {
		return config;
	}




}
