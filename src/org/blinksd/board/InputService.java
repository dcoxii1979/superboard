package org.blinksd.board;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.inputmethodservice.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.blinksd.*;
import org.blinksd.board.LayoutUtils.*;
import org.blinksd.utils.color.*;
import org.blinksd.utils.image.*;
import org.superdroid.db.*;

import static org.blinksd.board.SuperBoard.*;

public class InputService extends InputMethodService {
	
	private SuperBoard sb = null;
	private BoardPopup po = null;
	private SuperDB sd = null;
	public static final String COLORIZE_KEYBOARD = "org.blinksd.board.KILL";
	private String kbd[][][] = null;
	private LinearLayout ll = null;
	private RelativeLayout fl = null;
	private ImageView iv = null;
	private File img = null;
	private static final boolean IS_OREO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
	private Language cl;

	@Override
	public View onCreateInputView(){
		setLayout();
		return fl;
	}

	@Override
	public void onUnbindInput(){
		requestHideSelf(0);
		super.onUnbindInput();
	}

	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting){
		super.onStartInput(attribute, restarting);
		if(sb != null){
			sb.updateKeyState(this);
		}
	}

	@Override
	public void onFinishInput(){
		super.onFinishInput();
		System.gc();
	}
	
	private void setKeyBg(int clr){
		sb.setKeysBackground(setKeyBg(sd,sb,clr,true));
	}

	public static Drawable setKeyBg(SuperDB sd,SuperBoard sb,int clr,boolean pressEffect){
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(sb.getColorWithState(clr,false));
		gd.setCornerRadius(sb.mp(Settings.a(SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key_radius.name(),10))));
		gd.setStroke(sb.mp(Settings.a(SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key_padding.name(),10))),0);
		if(pressEffect){
			StateListDrawable d = new StateListDrawable();
			GradientDrawable pd = new GradientDrawable();
			pd.setColor(sb.getColorWithState(clr,true));
			pd.setCornerRadius(sb.mp(Settings.a(SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key_radius.name(),10))));
			pd.setStroke(sb.mp(Settings.a(SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key_padding.name(),10))),0);
			d.addState(new int[]{android.R.attr.state_selected},pd);
			d.addState(new int[]{},gd);
			return d;
		}
		return gd;
	}
	
	private void setLayout(){
		if(sd == null){
			sd = SuperBoardApplication.getApplicationDatabase();
			registerReceiver(r,new IntentFilter(COLORIZE_KEYBOARD));
		}
		if(sb == null){
			sb = new SuperBoard(this){
				private boolean shown = false;
				@Override
				public void onKeyboardEvent(View v){
					if(shown = po.isShown()){
						po.showPopup(false);
						po.clear();
						return;
					}
					po.setKey((SuperBoard.Key)v,sd);
					po.showCharacter();
				}
				
				public void onPopupEvent(){
					po.showPopup();
					po.setShiftState(shift);
				}
				
				@Override
				public void afterKeyboardEvent(){
					super.afterKeyboardEvent();
					po.hideCharacter();
				}
				
				public void sendDefaultKeyboardEvent(View v){
					if(!shown) super.sendDefaultKeyboardEvent(v);
					else shown = false;
				}
			};
			sb.setLayoutParams(new LinearLayout.LayoutParams(-1,-1,1));
			String appname = getString(R.string.app_name),abc = "ABC";
			kbd = new String[][][]{
				{
					{"[","]","θ","÷","<",">","`","´","{","}"},
					{"©","£","€","+","®","¥","π","Ω","λ","β"},
					{"@","#","$","%","&","*","-","=","(",")"},
					{"S2","!","\"","'",":",";","/","?",""},
					{abc,",",appname,".",""}
				},{
					{"√","ℕ","★","×","™","‰","∛","^","~","±"},
					{"♣","♠","♪","♥","♦","≈","Π","¶","§","∆"},
					{"←","↑","↓","→","∞","≠","_","℅","‘","’"},
					{"S3","¡","•","°","¢","|","\\","¿",""},
					{abc,"₺",appname,"…",""}
				},{
					{"F1","F2","F3","F4","F5","F6","F7","F8"},
					{"F9","F10","F11","F12","P↓","P↑","INS","DEL"},
					{"TAB","ENTER","","ESC","PREV","PL/PA","STOP","NEXT"},
					{"","","","","","","",""},
					{abc,"🔇","←","↑","↓","→","🔉","🔊"}
				},{
					{"1","2","3","+"},
					{"4","5","6",";"},
					{"7","8","9",""},
					{"*","0","#",""}
				}
			};

			try {
				String lang = SuperDBHelper.getValueAndSetItToDefaultIsNotSet(sd,Settings.Key.keyboard_lang_select.name(),"tr_TR_Q");
				cl = LayoutUtils.getLanguage(this,lang);
				if(!cl.language.equals(lang)){
					throw new RuntimeException("Where is the layout JSON file (in assets)?");
				}
				String[][] lkeys = LayoutUtils.getLayoutKeysFromList(cl.layout);
				sb.addRows(0,lkeys);
				sb.setLayoutPopup(0,LayoutUtils.getLayoutKeysFromList(cl.popup));
				if(cl.midPadding && lkeys != null){
					sb.setRowPadding(0,lkeys.length/2,sb.wp(2));
				}
				LayoutUtils.setKeyOpts(cl.layout,sb);
			} catch(Throwable e){
				throw new RuntimeException(e);
				/*ByteArrayOutputStream bos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(bos);
				e.printStackTrace(ps);
				Log.e("AndroidRuntime","FATAL "+bos.toString());*/
			}
			sb.createLayoutWithRows(kbd[0],KeyboardType.SYMBOL);
			sb.createLayoutWithRows(kbd[1],KeyboardType.SYMBOL);
			sb.createLayoutWithRows(kbd[2],KeyboardType.SYMBOL);
				
			sb.createLayoutWithRows(kbd[3],KeyboardType.NUMBER);
			
			sb.setPressEventForKey(1,3,0,Keyboard.KEYCODE_ALT);
			sb.setPressEventForKey(2,3,0,Keyboard.KEYCODE_ALT);
			sb.setPressEventForKey(3,-1,0,Keyboard.KEYCODE_MODE_CHANGE);
			
			sb.setPressEventForKey(-1,2,-1,Keyboard.KEYCODE_DELETE);
			sb.setKeyRepeat(-1,2,-1);
			sb.setKeyDrawable(-1,2,-1,R.drawable.sym_keyboard_delete);
			sb.setPressEventForKey(-1,3,-1,Keyboard.KEYCODE_DONE);
			sb.setKeyDrawable(-1,3,-1,R.drawable.sym_keyboard_return);
			
			sb.setPressEventForKey(3,1,4,KeyEvent.KEYCODE_PAGE_DOWN);
			sb.setPressEventForKey(3,1,5,KeyEvent.KEYCODE_PAGE_UP);
			sb.setPressEventForKey(3,1,6,KeyEvent.KEYCODE_INSERT);
			sb.setPressEventForKey(3,1,7,KeyEvent.KEYCODE_DEL);
			sb.setPressEventForKey(3,2,0,KeyEvent.KEYCODE_TAB);
			sb.setPressEventForKey(3,2,1,'\n',false);
			sb.setPressEventForKey(3,2,3,KeyEvent.KEYCODE_ESCAPE);
			sb.setPressEventForKey(3,2,4,KeyEvent.KEYCODE_MEDIA_PREVIOUS);
			sb.setPressEventForKey(3,2,5,KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
			sb.setPressEventForKey(3,2,6,KeyEvent.KEYCODE_MEDIA_STOP);
			sb.setPressEventForKey(3,2,7,KeyEvent.KEYCODE_MEDIA_NEXT);
			
			sb.setPressEventForKey(3,-1,1,KeyEvent.KEYCODE_MUTE);
			sb.setPressEventForKey(3,-1,2,KeyEvent.KEYCODE_DPAD_LEFT);
			sb.setPressEventForKey(3,-1,3,KeyEvent.KEYCODE_DPAD_UP);
			sb.setPressEventForKey(3,-1,4,KeyEvent.KEYCODE_DPAD_DOWN);
			sb.setPressEventForKey(3,-1,5,KeyEvent.KEYCODE_DPAD_RIGHT);
			sb.setPressEventForKey(3,-1,6,KeyEvent.KEYCODE_VOLUME_DOWN);
			sb.setPressEventForKey(3,-1,7,KeyEvent.KEYCODE_VOLUME_UP);
			
			for(int i = 0;i < 2;i++){
				for(int g = 0;g < 8;g++){
					if(i >= 1 && g >= 4) break;
					sb.setPressEventForKey(3,i,g,KeyEvent.KEYCODE_F1+(g+(i*8)));
				}
			}
			
			for(int i = 0;i < kbd.length;i++){
				if(i != 0 && i < 3){
					sb.setRowPadding(i,2,sb.wp(2));
					sb.setKeyRepeat(i,3,-1);
					sb.setKeyRepeat(i,4,2);
					sb.setPressEventForKey(i,3,-1,Keyboard.KEYCODE_DELETE);
					sb.setKeyDrawable(i,3,-1,R.drawable.sym_keyboard_delete);
					sb.setPressEventForKey(i,4,0,Keyboard.KEYCODE_MODE_CHANGE);
					sb.setPressEventForKey(i,4,2,KeyEvent.KEYCODE_SPACE);
					sb.setPressEventForKey(i,4,-1,Keyboard.KEYCODE_DONE);
					sb.setKeyDrawable(i,4,-1,R.drawable.sym_keyboard_return);
					sb.setLongPressEventForKey(i,4,0,sb.KEYCODE_CLOSE_KEYBOARD);
					sb.setLongPressEventForKey(i,4,1,'\t',false);
					sb.setKeyWidthPercent(i,3,0,15);
					sb.setKeyWidthPercent(i,3,-1,15);
					sb.setKeyWidthPercent(i,4,0,20);
					sb.setKeyWidthPercent(i,4,1,15);
					sb.setKeyWidthPercent(i,4,2,50);
					sb.setKeyWidthPercent(i,4,3,15);
					sb.setKeyWidthPercent(i,4,-1,20);
				}
			}
		}
		
		sb.updateKeyState(this);
		
		if(ll == null){
			ll = new LinearLayout(this);
			ll.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
			ll.setOrientation(LinearLayout.VERTICAL);
			ll.addView(sb);
		}
		if(fl == null){
			fl = new RelativeLayout(this);
			fl.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
			iv = new ImageView(this);
			fl.addView(iv);
			fl.addView(ll);
			iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
			iv.setAdjustViewBounds(false);
		}
		if(po == null){
			po = new BoardPopup(fl){
				@Override
				public void afterKeyboardEvent(){
					sb.afterPopupEvent();
				}
			};
			fl.addView(po);
		}
		setPrefs();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		try {
			sd.onlyRead();
			setPrefs();
		} catch(Throwable t){
			System.exit(0);
		}
	}
	
	public void setPrefs(){
		if(sb != null && sd != null){
			sb.setKeyboardHeight(SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.keyboard_height.name(),36));
			img = Settings.getBackgroundImageFile(this);
			if(fl != null){
				int blur = SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.keyboard_bgblur.name(),0);
				Bitmap b = BitmapFactory.decodeFile(img.getAbsolutePath());
				iv.setImageBitmap(img.exists()?(blur > 0 ? ImageUtils.fastblur(b,1,blur) : b):null);
			}
			int c = SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.keyboard_bgclr.name(),0xFF282D31);
			sb.setBackgroundColor(c);
			setKeyBg(SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key_bgclr.name(),0xFF474B4C));
			int shr = SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key_shadowsize.name(),0),
				shc = SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key_shadowclr.name(),0xFFDDE1E2);
			sb.setKeysShadow(shr,shc);
			sb.setLongPressMultiplier(SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key_longpress_duration.name(),1));
			sb.setKeyVibrateDuration(SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key_vibrate_duration.name(),0));
			sb.setKeysTextColor(SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key_textclr.name(),0xFFDDE1E2));
			sb.setKeysTextSize(sb.mp(Settings.a(SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key_textsize.name(),13))));
			int y = SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.key2_bgclr.name(),0xFF373C40);
			int z = SuperDBHelper.getIntValueAndSetItToDefaultIsNotSet(sd,Settings.Key.enter_bgclr.name(),0xFF5F97F6);
			for(int i = 0;i < kbd.length;i++){
				if(i != 0){
					if(i < 3){
						sb.setKeyTintColor(i,3,0,y);
						sb.setKeyTintColor(i,3,-1,y);
						for(int h = 3;h < 5;h++) sb.setKeyTintColor(i,h,0,y);
						sb.setKeyTintColor(i,4,1,y);
						sb.setKeyTintColor(i,4,3,y);
					}
					if(i != 3) sb.setKeyTintColor(i,-1,-1,z);
				}
			}
			String lang = SuperDBHelper.getValueAndSetItToDefaultIsNotSet(sd,Settings.Key.keyboard_lang_select.name(),"tr_TR_Q");
			if(!lang.equals(cl.language)){
				setKeyboardLayout(lang);
			}
			List<List<KeyOptions>> kOpt = cl.layout;
			for(int i = 0;i < kOpt.size();i++){
				List<KeyOptions> subKOpt = kOpt.get(i);
				for(int g = 0;g < subKOpt.size();g++){
					KeyOptions ko = subKOpt.get(g);
					if(ko.darkerKeyTint){
						sb.setKeyTintColor(sb.getKey(0,i,g),y);
					}
					if(ko.pressKeyCode == Keyboard.KEYCODE_DONE){
						sb.setKeyTintColor(sb.getKey(0,i,g),z);
					}
				}
			}
			sb.setKeyboardLanguage(cl.language);
			adjustNavbar(c);
		}
	}
	
	private void setKeyboardLayout(String lang){
		try {
			Language l = SuperBoardApplication.getKeyboardLanguage(lang);
			if(!l.language.equals(lang)){
				throw new RuntimeException("Where is the layout JSON file (in assets)?");
			}
			String[][] lkeys = LayoutUtils.getLayoutKeysFromList(l.layout);
			sb.replaceNormalKeyboard(lkeys);
			sb.setLayoutPopup(sb.findNormalKeyboardIndex(),LayoutUtils.getLayoutKeysFromList(l.popup));
			if(l.midPadding && lkeys != null){
				sb.setRowPadding(sb.findNormalKeyboardIndex(),lkeys.length/2,sb.wp(2));
			}
			LayoutUtils.setKeyOpts(l.layout,sb);
			cl = l;
		} catch(Throwable e){
			throw new RuntimeException(e);
			/*ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(bos);
			e.printStackTrace(ps);
			Log.e("AndroidRuntime","FATAL "+bos.toString());*/
		}
	}
	
	private void adjustNavbar(int c){
		if(Build.VERSION.SDK_INT > 20){
			Window w = getWindow().getWindow();
			if(detectNavbar()){
				if(ll.getChildCount() > 1){
					ll.removeViewAt(1);
				}
				if(x()){
					w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
					iv.setLayoutParams(new RelativeLayout.LayoutParams(-1,sb.getKeyboardHeight()+navbarH()));
					ll.addView(createNavbarLayout(c));
				} else {
					w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
					iv.setLayoutParams(new RelativeLayout.LayoutParams(-1,sb.getKeyboardHeight()));
				}
			} else {
				iv.setLayoutParams(new RelativeLayout.LayoutParams(-1,sb.getKeyboardHeight()));
			}
		} else {
			iv.setLayoutParams(new RelativeLayout.LayoutParams(-1,sb.getKeyboardHeight()));
		}
		po.setFilterHeight(iv.getLayoutParams().height);
	}
	
	private View createNavbarLayout(int color){
		View v = new View(this);
		v.setLayoutParams(new ViewGroup.LayoutParams(-1,x() ? navbarH() : -1));
		v.setBackgroundColor(sb.getColorWithState(color,ColorUtils.satisfiesTextContrast(Color.rgb(Color.red(color),Color.green(color),Color.blue(color)))));
		return v;
	}
	
	private int navbarH(){
		if(x()){
			int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
			return resourceId > 0 ? getResources().getDimensionPixelSize(resourceId) : 0;
		}
		return 0;
	}
	
	private boolean x(){
		return !isLand() || isTablet();
	}
	
	private boolean isTablet(){
		return isLand() && getResources().getConfiguration().smallestScreenWidthDp >= 600;
	}
	
	private boolean isLand(){
		return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
	
	private boolean detectNavbar(){
		if(Build.VERSION.SDK_INT >= 14){
			try {
				Class<?> serviceManager = Class.forName("android.os.ServiceManager");
				IBinder serviceBinder = (IBinder)serviceManager.getMethod("getService", String.class).invoke(serviceManager, "window");
				Class<?> stub = Class.forName("android.view.IWindowManager$Stub");
				Object windowManagerService = stub.getMethod("asInterface", IBinder.class).invoke(stub, serviceBinder);
				Method hasNavigationBar = windowManagerService.getClass().getMethod("hasNavigationBar");
				return (boolean) hasNavigationBar.invoke(windowManagerService);
			} catch(Exception e){
				return ViewConfiguration.get(this).hasPermanentMenuKey();
			}
		}
		return (!(KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK) && 
			KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME)));
	}
	
	BroadcastReceiver r = new BroadcastReceiver(){
		@Override
		public void onReceive(Context p1,Intent p2){
			sd.onlyRead();
			setPrefs();
		}
	};

	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(po != null && po.isShown()){
			po.showPopup(false);
		}
		return super.onKeyDown(keyCode,event);
	}
}
