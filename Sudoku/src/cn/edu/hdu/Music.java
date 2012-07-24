package cn.edu.hdu;

import android.content.Context;
import android.media.MediaPlayer;

public class Music {
	private static MediaPlayer mediaPlayer = null;
	
	// stop the old music and start new one
	public static void play(Context context, int resource){
		stop(context);
		
		if(Prefs.getMusic(context)){
			mediaPlayer = MediaPlayer.create(context, resource);
			mediaPlayer.setLooping(true);
			mediaPlayer.start();
		}
	}
	
	// stop music
	public static void stop(Context context){
		if(mediaPlayer != null){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
}
