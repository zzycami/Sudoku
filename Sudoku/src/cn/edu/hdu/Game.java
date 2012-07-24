package cn.edu.hdu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Game extends Activity{
	public final static String TAG = "Sudoku";
	
	public final static String KEY_DIFFICULTY = "cn.edu.hdu.difficulty";
	private final static String PREF_PUZZLE = "puzzle";
	
	public final static int DIFFICULTY_CONTINUE = -1;
	public final static int DIFFICULTY_EASY = 0;
	public final static int DIFFICULTY_MEDIUM = 1;
	public final static int DIFFICULTY_HARD = 2;
	
	private final String easyPuzzle = 
			"360000000004230800000004200" + 
			"070460003820000014500013020" +
			"001900000007048300000000045";
	
	private final String mediumPuzzle = 
			"650000070000506000014000005" +
			"007009000002314700000700800" +
			"500000630000201000030000097";
	
	private final String hardPuzzle = 
			"009000000080605020501078000" +
			"000000700706040102004000000" +
			"000720903090301080000000600";
	
	private int puzzle[] = new int[9*9];
	private final int used[][][] = new int[9][9][];
	
	private PuzzleView puzzleView;
	private int diff;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
		puzzle = getPuzzle(diff);
		calculateUsedTiles();
		
		puzzleView = new PuzzleView(this);
		setContentView(puzzleView);
		puzzleView.requestFocus();
	}

	private int[] getPuzzle(int diff) {
		String puz; 
		switch(diff){
		case DIFFICULTY_CONTINUE:
			puz = getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE, easyPuzzle); 
			break;
		case DIFFICULTY_EASY: puz =easyPuzzle;break;
		case DIFFICULTY_MEDIUM: puz = mediumPuzzle;break;
		case DIFFICULTY_HARD: puz = hardPuzzle;break;
		default: puz = easyPuzzle;break;
		}
		return fromPuzzleString(puz);
	}

	private int[] fromPuzzleString(String string) {
		int puz[] = new int[string.length()];
		for(int i = 0;i<string.length();i++){
			puz[i] = string.charAt(i) - '0';
		}
		return puz;
	}
	
	public String toPuzzleString(int[] puz){
		StringBuilder buf = new StringBuilder();
		for(int temp : puz){
			buf.append(temp);
		}
		return buf.toString();
	}

	private void calculateUsedTiles() {
		for(int i = 0; i<9; i++){
			for(int j = 0; j<9; j++){
				used [i][j] = calculateUsedTiles(i, j);
			}
		}
	}
	
	private int[] calculateUsedTiles(int x, int y) {
		int c[] = new int[9];
		
		// horizontal
		for(int i=0;i<9;i++){
			if(y == i) continue;
			int temp = getTile(x, i);
			if(temp != 0){
				c[temp - 1] = temp;
			}
		}
		
		// vertical 
		for(int i=0;i<9;i++){
			if(x == i) continue;
			int temp = getTile(i, y);
			if(temp != 0){
				c[temp - 1] = temp;
			}
		}
		
		// same cell block
		int startx = (int)(x/3)*3;
		int starty = (int)(y/3)*3;
		for(int i=startx;i<startx + 3;i++){
			for(int j=starty;j<starty + 3;j++){
				if(x == i  || y == j)
					continue;
				int temp = getTile(i, j);
				if(temp != 0){
					c[temp - 1] = temp;
				}
			}
		}
		
		//compress
		int usedNum = 0;
		for(int temp : c){
			if(temp != 0){
				usedNum ++;
			}
		}
		int cc[] = new int[usedNum];
		usedNum = 0;
		for(int temp : c){
			if(temp != 0){
				cc[usedNum ++] = temp;
			}
		}
		return cc;
	}
	
	private int getTile(int x, int y){
		return puzzle[y*9 + x];
	}
	
	public void setTile(int x, int y, int value){
		puzzle[y*9 + x] = value;
	}
	
	public boolean checkStaticTile(int x, int y){
		String puz;
		switch(diff){
		case DIFFICULTY_EASY: puz =easyPuzzle;break;
		case DIFFICULTY_MEDIUM: puz = mediumPuzzle;break;
		case DIFFICULTY_HARD: puz = hardPuzzle;break;
		default: puz = easyPuzzle;break;
		}
		int num = fromPuzzleString(puz)[y*9 + x];
		return num != 0;
	}

	public String getTileString(int x, int y) {
		int value = getTile(x, y);
		if(value == 0){
			return "";
		}else {
			return String.valueOf(value);
		}
	}

	public void showKeypadOrError(int x, int y) {
		// this position is filled by origin puzzle
		if(checkStaticTile(x, y)){
			return ;
		}
		int tiles[] = getUsedTiles(x, y);
		if(tiles.length == 9){
			Toast toast = Toast.makeText(this, R.string.no_moves_label, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}else {
			new Keypad(this, tiles, puzzleView).show();
		}
	}

	public boolean setTileIfValid(int x, int y, int number) {
		int tiles[] = getUsedTiles(x, y);
		
		// this position is filled by origin puzzle
		if(checkStaticTile(x, y)){
			return false;
		}
		
		if(number != 0){
			for(int temp : tiles){
				if(temp == number){
					return false;
				}
			}
		}
		setTile(x, y, number);
		calculateUsedTiles();
		return true;
	}

	public int[] getUsedTiles(int x, int y) {
		return used[x][y];
	}

	@Override
	protected void onResume() {
		super.onResume();
		Music.play(this, R.raw.game);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		Music.stop(this);
		
		// save the current puzzle
		getPreferences(MODE_PRIVATE).edit()
		.putString(PREF_PUZZLE, toPuzzleString(puzzle))
		.commit();
	}
	
	

}
