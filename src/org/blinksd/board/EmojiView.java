package org.blinksd.board;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.TabHost.*;

public class EmojiView extends LinearLayout {
	
	private float txtsze;
	private int keyclr;
	private Drawable drw;
	
	public EmojiView(SuperBoard sb, View.OnClickListener ocl){
		super(sb.getContext());
		txtsze = sb.txtsze;
		keyclr = sb.keyclr;
		drw = sb.keybg.getConstantState().newDrawable();
		apply(ocl);
	}
	
	private int curTab = 0;
	
	private void apply(View.OnClickListener ocl){
		setBackgroundColor(0xFF000000);
		final TabHost th = new TabHost(getContext());
		th.setLayoutParams(new LayoutParams(-1,-2,1));
		final TabWidget tw = new TabWidget(getContext());
		tw.setId(android.R.id.tabs);
		tw.setLayoutParams(new LayoutParams(-1,-1,1));
		FrameLayout fl = new FrameLayout(getContext());
		fl.setLayoutParams(new LayoutParams(-1,-1));
		fl.setId(android.R.id.tabcontent);
		th.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
			@Override
			public void onTabChanged(String p1){
				tw.getChildTabViewAt(curTab).setBackgroundColor(0);
				tw.getChildTabViewAt(curTab = th.getCurrentTab()).setBackgroundColor(0xFF212121);
			}
		});
		final LinearLayout ll = new LinearLayout(getContext());
		ll.setLayoutParams(new LayoutParams(-1,-1));
		ll.setOrientation(VERTICAL);
		setLayoutParams(new LayoutParams(-1,-1));
		final LinearLayout tl = new LinearLayout(getContext());
		int l = getResources().getDisplayMetrics().widthPixels / emojis.length;
		tl.setLayoutParams(new LayoutParams(-1,l));
		tl.addView(categoryItem(-1,l,ocl));
		tl.addView(tw);
		tl.addView(categoryItem(10,l,ocl));
		ll.addView(tl);
		ll.addView(fl);
		th.addView(ll);
		th.setup();
		for(int i = 0;i < emojis.length;i++){
			TabSpec ts = th.newTabSpec(emojis[i][0]);
			TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1,tw,false);
			tv.setLayoutParams(new LayoutParams(-1,-2,1));
			tv.setText(emojis[i][0]);
			tv.setTextColor(keyclr);
			tv.setGravity(Gravity.CENTER);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,8);
			ts.setIndicator(tv);
			final int x = i;
			ts.setContent(new TabContentFactory(){
					@Override
					public View createTabContent(String p1){
						return emojiList(x,tw.getHeight());
					}
				});
			th.addTab(ts);
		}
		addView(th);
		setOrientation(VERTICAL);
	}
	
	private View categoryItem(int num, int width, View.OnClickListener ocl){
		if(num == -1){
			TextView tv = new TextView(getContext());
			tv.setLayoutParams(new LayoutParams(width,-1,0));
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(keyclr);
			tv.setText("A");
			tv.setTag(num);
			tv.setTextSize(txtsze);
			tv.setOnClickListener(ocl);
			return tv;
		} else {
			ImageView iv = new ImageView(getContext());
			iv.setLayoutParams(new LayoutParams(width,-1,0));
			iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			int p = width / 8;
			iv.setPadding(p,p,p,p);
			iv.setImageResource(R.drawable.sym_keyboard_delete);
			iv.setColorFilter(keyclr,PorterDuff.Mode.SRC_ATOP);
			iv.setTag(num);
			iv.setOnClickListener(ocl);
			return iv;
		}
	}
	
	// Disable View constructors
	private EmojiView(Context c){ super(c); }
	private EmojiView(Context c, AttributeSet a){ super(c,a); }
	private EmojiView(Context c, AttributeSet a, int d){ super(c,a,d); }
	private EmojiView(Context c, AttributeSet a, int d, int r){ super(c,a,d,r); }
	
	private GridView emojiList(final int index, int margin){
		final GridView gv = new GridView(getContext());
		gv.setOverScrollMode(GridView.OVER_SCROLL_NEVER);
		gv.setLayoutParams(new LayoutParams(-1,-1));
		gv.setNumColumns(6);
		gv.setGravity(Gravity.CENTER);
		gv.setOnItemClickListener(new GridView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4){
				((InputService)getContext()).onEmojiText(gv.getItemAtPosition(p3).toString());
			}
		});
		ArrayAdapter<String> aa = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,android.R.id.text1){
			@Override
			public View getView(int pos, View cv, ViewGroup p){
				TextView v = (TextView) super.getView(pos,cv,p);
				v.setBackgroundDrawable(drw.getConstantState().newDrawable());
				v.setTextColor(keyclr);
				v.setGravity(Gravity.CENTER);
				v.setSingleLine();
				v.setWidth(getResources().getDisplayMetrics().widthPixels / gv.getNumColumns());
				v.setHeight(getResources().getDisplayMetrics().heightPixels / 12);
				v.setTextSize(txtsze);
				return v;
			}
		};
		gv.setAdapter(aa);
		for(int i = 0;i < emojis[index].length;i++)
			aa.add(emojis[index][i]);
		return gv;
	}
	
	private static final String[][] emojis = new String[][]{
		{
			"☺","👅","😀","😁","😂","😃","😄","😅","😆","😇",
			"😈","😉","😊","😋","😌","😍","😎","😏","😐","😑",
			"😒","😓","😔","😕","😖","😗","😘","😙","😚","😛",
			"😜","😝","😞","😟","😠","😡","😢","😣","😤","😥",
			"😦","😧","😨","😩","😪","😫","😬","😭","😮","😯",
			"😰","😱","😲","😳","😴","😵","😶","😷","☝","✊",
			"✋","✌","👆","👇","👈","👉","👊","👋","👌","👍",
			"👎","👏","👐","🙌","🙏","💋","💌","💍","💎","💏",
			"💐","💑","💒","💓","💔","💕","💖","💗","💘","💙",
			"💚","💛","💜","💝","💞","💟","🏂","🏃","🏄","🏇",
			"🏊","👤","👥","👦","👧","👨","👩","👪","👫","👬",
			"👭","👮","👯","👰","👱","👲","👳","👴","👵","👶",
			"👷","👸","👹","👺","👻","👼","👽","👾","👿","💀",
			"💁","💂","💃","💆","💇","💈","🙅","🙆","🙇","🙊",
			"🙋","🙍","🙎","🚴","🚵","🚶"
		},{
			"©","®","‼","⁉","™","ℹ","Ⓜ","♻","⚠","⚡","⛔",
			"📵","🔇","🔉","🔊","🔕","🔯","🔱","🚫","🚮","🚯",
			"🚰","🚱","🚳","🚷","🚸","🛂","🛃","🛄","🛅",
			"↔","↕","↖","↗","↘","↙","↩","↪","⏩","⏪","⏫",
			"⏬","▶","◀","➡","⤴","⤵","⬅","⬆","⬇","🔀","🔁",
			"🔂","🔃","🔄","🔼","🔽"
		},{
			"⌚","⌛","⏰","⏳","☎","✂","✅","✉","✏","✒",
			"🌂","🎒","🎓","🎣","🎤","🎥","🎦","🎧","🎨","🎩",
			"🎭","🎮","🎰","🎲","🎳","🎴","🎵","🎶","🎷","🎸",
			"🎹","🎺","🎻","🎼","🎽","🎾","🎿","🏀","🏁","🏆",
			"🏈","🏉","🏠","🏡","🏧","🏮","👑","👒","👔","👕",
			"👗","👘","👙","👚","👛","👜","👝","👞","👟","👠",
			"👡","👢","👣","💄","💅","💉","💊","💠","💡","💢",
			"💣","💤","💰","💲","💴","💵","💺","💻","💼","💽",
			"💾","💿","📀","📖","📝","📞","📠","📡","📢","📣",
			"📨","📩","📪","📫","📬","📭","📮","📯","📰","📱",
			"📲","📳","📴","📶","📷","📹","📺","📻","📼","🔈",
			"🔍","🔎","🔏","🔐","🔑","🔒","🔓","🔔","🔞","🔥",
			"🔦","🔧","🔨","🔩","🔪","🔫","🔬","🔭","🔮","🚀",
			"🚥","🚦","🚧","🚩","🚬","🚭","🚲","🚹","🚺","🚻",
			"🚼","🚽","🚾","🛀"
		},{
			"▪","▫","◻","◼","◽","◾","☑","♠","♣","♥","♦",
			"♿","⚪","⚫","⚽","⚾","⛄","⛵","✔","✖","✨",
			"✳","✴","❇","❌","❎","❓","❔","❕","❗","❤",
			"➕","➖","➗","➰","➿","⬛","⬜","⭐","⭕","〰",
			"〽","㊗","㊙","🀄","🃏","🎀","🎁","🎃","🎄","🎅",
			"🎆","🎇","🎈","🎉","🎊","🎋","🎌","🎍","🎎","🎏",
			"🎐","🎠","🎡","🎢","🎫","🎬","🎯","🎱","👀","👂",
			"👃","👄","💨","💩","🔅","🔆","🔋","🔌","🔖","🔗",
			"🔘","🔲","🔳","🔴","🔵","🔶","🔷","🔸","🔹","🔺",
			"🔻","🚨","🚪","🚿","🛁","♈","♉","♊","♋","♌",
			"♍","♎","♏","♐","♑","♒","♓"
		},{
			"☕","♨","🌰","🍅","🍆","🍇","🍈","🍉","🍊","🍋",
			"🍌","🍍","🍎","🍏","🍐","🍑","🍒","🍓","🍔","🍕",
			"🍖","🍗","🍘","🍙","🍚","🍛","🍜","🍝","🍞","🍟",
			"🍠","🍡","🍢","🍣","🍤","🍥","🍦","🍧","🍨","🍩",
			"🍪","🍫","🍬","🍭","🍮","🍯","🍰","🍱","🍲","🍳",
			"🍴","🍵","🍶","🍷","🍸","🍹","🍺","🍻","🍼","🎂"
		},{
			"⚓","⛅","⛎","🐋","👓","👖","💥","💪","💫","💬",
			"💭","💮","💯","💱","💳","💶","💷","💸","💹","📁",
			"📂","📃","📄","📅","📆","📇","📈","📉","📊","📋",
			"📌","📍","📎","📏","📐","📑","📒","📓","📔","📕",
			"📗","📘","📙","📚","📛","📜","📟","📤","📥","📦","📧"
		},{
			"⛪","⛲","⛳","⛺","⛽","🌍","🌎","🌏","🌐","🎪",
			"🏢","🏣","🏤","🏥","🏦","🏨","🏩","🏪","🏫","🏬",
			"🏭","🏯","🏰","🗻","🗼","🗽","🗾","🗿","✈","🚁",
			"🚂","🚃","🚄","🚅","🚆","🚇","🚈","🚉","🚊","🚋",
			"🚌","🚍","🚎","🚏","🚐","🚑","🚒","🚓","🚔","🚕",
			"🚖","🚗","🚘","🚙","🚚","🚛","🚜","🚝","🚞","🚟",
			"🚠","🚡","🚢","🚣","🚤"
		},{
			"🅰","🅱","🅾","🅿","🆎","🆑","🆒","🆓","🆔","🆕","🆖",
			"🆗","🆘","🆙","🆚","🔙","🔚","🔛","🔜","🔝","🔟",
			"🔠","🔡","🔢","🔣","🔤","#⃣","0⃣","1⃣","2⃣","3⃣",
			"4⃣","5⃣","6⃣","7⃣","8⃣","9⃣","🕐","🕑","🕒","🕓",
			"🕔","🕕","🕖","🕗","🕘","🕙","🕚","🕛","🕜","🕝",
			"🕞","🕟","🕠","🕡","🕢","🕣","🕤","🕥","🕦","🕧",
			"🈁","🈂","🈚","🈯","🈲","🈳","🈴","🈵","🈶","🈷",
			"🈸","🈹","🈺","🉐","🉑","🔰"
		},{
			"🐀","🐁","🐂","🐃","🐄","🐅","🐆","🐇","🐈","🐉",
			"🐊","🐌","🐍","🐎","🐏","🐐","🐑","🐒","🐓","🐔",
			"🐕","🐖","🐗","🐘","🐙","🐚","🐛","🐜","🐝","🐞",
			"🐟","🐠","🐡","🐢","🐣","🐤","🐥","🐦","🐧","🐨",
			"🐩","🐪","🐫","🐬","🐭","🐮","🐯","🐰","🐱","🐲",
			"🐳","🐴","🐵","🐶","🐷","🐸","🐹","🐺","🐻","🐼",
			"🐽","🐾","😸","😹","😺","😻","😼","😽","😾","😿",
			"🙀","🙈","🙉","🌱","🌲","🌳","🌴","🌵","🌷","🌸",
			"🌹","🌺","🌻","🌼","🌽","🌾","🌿","🍀","🍁","🍂",
			"🍃","🍄","☀","☁","☔","❄","🌀","🌁","🌃","🌄",
			"🌅","🌆","🌇","🌈","🌉","🌊","🌋","🌌","🌑","🌒",
			"🌓","🌔","🌕","🌖","🌗","🌘","🌙","🌚","🌛","🌜",
			"🌝","🌞","🌟","🌠","🎑","💦","💧"
		},{
			"🇹🇷","🇦🇿","🇨🇳","🇩🇪","🇪🇸","🇫🇷","🇬🇧","🇮🇹","🇯🇵","🇰🇷","🇷🇺",
			"🇺🇸","🇦🇺","🇦🇹","🇧🇪","🇧🇷","🇨🇦","🇨🇱","🇨🇴","🇩🇰","🇫🇮",
			"🇭🇰","🇮🇳","🇮🇩","🇮🇪","🇮🇱","🇲🇴","🇲🇾","🇲🇽","🇳🇱","🇳🇿",
			"🇳🇴","🇵🇭","🇵🇱","🇵🇹","🇵🇷","🇸🇦","🇸🇬","🇿🇦","🇸🇪","🇨🇭",
			"🇦🇪","🇻🇳"
		}
	};
	
}
