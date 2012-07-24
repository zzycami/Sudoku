package cn.edu.hdu;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity{
	// Option names and default values
	private static final String OPT_MUSIC = "music"; 
	private static final boolean OPT_MUSIC_DEF = true;
	
	private static final String OPT_HINTS = "hint";
	private static final boolean OPT_HINT_DEF = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.setting);
	}
	
	public static boolean getMusic(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
	}
	
	public static boolean getHint(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(OPT_HINTS, OPT_HINT_DEF);
	}

}
