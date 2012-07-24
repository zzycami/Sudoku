package cn.edu.hdu;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class PuzzleView extends View{
	public final static String TAG = "Sudoku";
	
	private static final String SELX = "selX";
	private static final String SELY = "selY";
	private static final String VIEW_STATE = "viewState";
	private static final int ID = 100;
	
	private final Game game;
	
	private float width;
	private float height;
	private int selX;
	private int selY;
	private final Rect selRect = new Rect();

	public PuzzleView(Context context) {
		super(context);
		
		game = (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		setId(ID);
	}
	
	

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable par =  super.onSaveInstanceState();
		Bundle bundle = new Bundle();
		bundle.putInt(SELX, selX);
		bundle.putInt(SELY, selY);
		bundle.putParcelable(VIEW_STATE, par);
		return bundle;
	}



	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle) state;
		select(bundle.getInt(SELX), bundle.getInt(SELY));
		super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));
	}



	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = (int)(w/9.0f);
		height = (int)(h/9.0f);
		getRect(selX, selY, selRect);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void getRect(int x, int y, Rect rect) {
		rect.set((int)(x*width), (int)(y*height), (int)(x*width + width), (int)(y*height + height));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// draw the background
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		
		// draw the board
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		
		// draw the minor grid lines
		for(int i=0;i<9;i++){
			// horizontal lines
			canvas.drawLine(0, i*height, getWidth(), i*height, light);
			canvas.drawLine(0, i*height + 1, getWidth(), i*height + 1, hilite);
			
			// vertical lines
			canvas.drawLine(width*i, 0, width*i, getHeight(), light);
			canvas.drawLine(width*i + 1, 0, width*i + 1, getHeight(), hilite);
		}
		// draw the major grid lines
		for(int i=0;i<=9;i+=3){
			// horizontal lines
			canvas.drawLine(0, height*i, getWidth(), height*i, dark);
			canvas.drawLine(0, height*i + 1, getWidth(), height*i + 1, hilite);
			//vertical lines
			canvas.drawLine(width*i, 0, width*i, getHeight(), dark);
			canvas.drawLine(width*i + 1, 0, width*i + 1, getHeight(), hilite);
		}
		
		// draw the numbers
		// define color and style for numbers
		Paint foreground = new Paint();
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height*0.75f);
		foreground.setTextScaleX(width/height);
		foreground.setTextAlign(Paint.Align.CENTER);
		foreground.setAntiAlias(true);
		
		Paint static_number = new Paint();
		static_number.setColor(getResources().getColor(R.color.puzzle_static_number));
		static_number.setStyle(Style.FILL);
		static_number.setTextSize(height*0.75f);
		static_number.setTextScaleX(width/height);
		static_number.setTextAlign(Paint.Align.CENTER);
		static_number.setAntiAlias(true);
		
		// draw the number in the center of the tile
		FontMetrics fm = foreground.getFontMetrics();
		float x = width/2;
		float y = height/2 - (fm.ascent + fm.descent)/2;
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				if(game.checkStaticTile(i, j)){
					canvas.drawText(game.getTileString(i, j), i*width + x, j*height + y, static_number);
				}else {
					canvas.drawText(game.getTileString(i, j), i*width + x, j*height + y, foreground);
				}
			}
		}
	
		// draw the hints
		if(Prefs.getHint(getContext())){
			Paint hint = new Paint();
			int c[] = {
					getResources().getColor(R.color.puzzle_hint_0),
					getResources().getColor(R.color.puzzle_hint_1),
					getResources().getColor(R.color.puzzle_hint_2)
			};
			Rect rect = new Rect();
			for(int i=0;i<9;i++){
				for(int j=0;j<9;j++){
					int moveleft = 9 - game.getUsedTiles(i, j).length;
					if(moveleft < c.length){
						getRect(i, j, rect);
						hint.setColor(c[moveleft]);
						canvas.drawRect(rect, hint);
					}
				}
			}
		}
		
		
		// draw the selection
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.puzzle_selected));
		canvas.drawRect(selRect, selected);
		super.onDraw(canvas);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_DPAD_UP:select(selX, selY - 1);break;
		case KeyEvent.KEYCODE_DPAD_DOWN:select(selX, selY + 1);break;
		case KeyEvent.KEYCODE_DPAD_LEFT:select(selX - 1, selY);break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:select(selX + 1, selY);break;
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE:setSelectedTile(0);break;
		case KeyEvent.KEYCODE_1:setSelectedTile(1);break;
		case KeyEvent.KEYCODE_2:setSelectedTile(2);break;
		case KeyEvent.KEYCODE_3:setSelectedTile(3);break;
		case KeyEvent.KEYCODE_4:setSelectedTile(4);break;
		case KeyEvent.KEYCODE_5:setSelectedTile(5);break;
		case KeyEvent.KEYCODE_6:setSelectedTile(6);break;
		case KeyEvent.KEYCODE_7:setSelectedTile(7);break;
		case KeyEvent.KEYCODE_8:setSelectedTile(8);break;
		case KeyEvent.KEYCODE_9:setSelectedTile(9);break;
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:game.showKeypadOrError(selX, selY);break;
		case KeyEvent.KEYCODE_BACK:showReturnDialog();break;
		default:super.onKeyDown(keyCode, event);break;
		}
		return true;
	}
	
	private void showReturnDialog(){
		AlertDialog.Builder build = new Builder(getContext());
		build.setTitle(R.string.return_title);
		build.setMessage(R.string.return_text);
		build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				game.finish();
			}
		});
		
		build.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		build.create().show();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() != MotionEvent.ACTION_DOWN){
			return super.onTouchEvent(event);
		}
		select((int)(event.getX()/width), (int)(event.getY()/height));
		game.showKeypadOrError(selX, selY);
		return true;
	}

	void setSelectedTile(int number) {
		if(game.setTileIfValid(selX, selY, number)){
			this.invalidate();
		}else {
			startAnimation(AnimationUtils.loadAnimation(game, R.anim.shake));
		}
	}

	private void select(int x, int y) {
		this.invalidate(selRect);
		selX = Math.min(Math.max(x, 0), 8);// make sure selX is between 0-8
		selY = Math.min(Math.max(y, 0), 8);
		getRect(selX, selY, selRect);
		this.invalidate(selRect);
	}
	
	

}
