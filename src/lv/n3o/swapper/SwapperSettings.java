/**
 * 
 */
package lv.n3o.swapper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * @author n3o
 * 
 */
public class SwapperSettings extends Activity implements OnClickListener,
		OnSeekBarChangeListener {

	TextView swapIndicator;
	SeekBar swapSeekBar;
	ImageButton back;
	SharedPreferences settings;
	EditText swapPlace;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swappersettings);

		swapIndicator = (TextView) findViewById(R.id.SwapIndicator);
		swapSeekBar = (SeekBar) findViewById(R.id.SwapSeekBar);
		swapSeekBar.setOnSeekBarChangeListener(this);
		swapPlace = (EditText)findViewById(R.id.swapPlace);
		
		settings = getSharedPreferences("Swapper", 0);
		swapIndicator.setText("" + settings.getInt("swapsize", 32));
		swapSeekBar.setProgress(settings.getInt("swapsize", 32));

		back = (ImageButton) findViewById(R.id.BackButton);
		back.setOnClickListener(this);

		
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		swapIndicator.setText("" + arg1);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("swapsize", arg1);
		editor.commit();
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {

	}

	public void onClick(View arg0) {
		if (arg0.equals(back)) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("swapplace",swapPlace.getText().toString());
			editor.commit();
			Intent i = new Intent(this, Swapper.class);
			startActivity(i);
			this.finish();
			return;
		}
	}
}
