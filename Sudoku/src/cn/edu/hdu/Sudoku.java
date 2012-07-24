package cn.edu.hdu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Sudoku extends Activity implements OnClickListener{
	public final static String TAG = "Sudoku";
	private Button exit;
	private Button about;
	private Button new_game;
	private Button conti;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initGUI();
    }
    
    private void initGUI(){
    	exit = (Button) findViewById(R.id.button_exit);
    	exit.setOnClickListener(this);
    	
    	about = (Button) findViewById(R.id.button_about);
    	about.setOnClickListener(this);
    	
    	new_game = (Button) findViewById(R.id.button_new_game);
    	new_game.setOnClickListener(this);
    	
    	conti = (Button) findViewById(R.id.button_continue);
    	conti.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button_exit:
			this.finish();
			break;
		case R.id.button_about:
			Intent intent = new Intent(this, About.class);
			startActivity(intent);
			break;
		case R.id.button_new_game:
			openNewGameDialog();
			break;
		case R.id.button_continue:
			startGame(Game.DIFFICULTY_CONTINUE);
			break;
		}
	}
	
	private void openNewGameDialog(){
		new AlertDialog.Builder(this)
		.setTitle(R.string.new_game_title)
		.setItems(R.array.disfficulty, 
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startGame(which);
					}
			
		}).show();
	}

	protected void startGame(int which) {
		Log.d(TAG, "click on" + which);
		// start game here
		Intent intent = new Intent(this, Game.class);
		intent.putExtra(Game.KEY_DIFFICULTY, which);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case R.id.setting:
			startActivity(new Intent(this, Prefs.class));
			break;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Music.play(this, R.raw.main);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Music.stop(this);
	}
	
	
	
	
}