package lv.n3o.swapper;


import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Swapper extends Activity implements OnClickListener, Runnable {

	ProgressDialog		dia;
	Button				get_info;
	Button				get_swappiness;
	TextView			log;
	Thread				progress;
	Button				set_swappiness;
	Button				set_swappiness_10;
	SharedPreferences	settings;
	Button				startsettings;
	SuCommander			su;

	Button				swapoff;

	Button				swapon_32;
	EditText			swappiness;

	String				swapPlace;

	int					swapSize;

	@Override
	public void onClick(View arg0) {
		if (arg0.equals(startsettings)) {
			Intent i = new Intent(this, SwapperSettings.class);
			startActivity(i);
			finish();
			return;
		}
		dia = new ProgressDialog(this);
		dia.setCancelable(false);
		dia.setMessage("Please wait...");
		dia.setTitle("Executing command");
		log.setText("");
		try {
			if (arg0.equals(swapon_32)) {
				log.append("Starting... (It can take long time)\n");
				su.exec("dd if=/dev/zero of=" + swapPlace + " bs=1M count="
						+ swapSize + " && mkswap " + swapPlace + " && swapon "
						+ swapPlace);
				progress = new Thread(this);
				progress.start();
				dia.show();
				// log.append(su.get_output("swap_creation_end"));
			} else if (arg0.equals(swapoff)) {
				log.append("Stopping...\n");
				su.exec("swapoff " + swapPlace + " && rm " + swapPlace);
				progress = new Thread(this);
				progress.start();
				dia.show();
			} else if (arg0.equals(get_swappiness)) {
				swappiness.setText(su.exec_o("cat /proc/sys/vm/swappiness"));
			} else if (arg0.equals(set_swappiness)) {
				su.exec("echo " + swappiness.getText().toString()
						+ " > /proc/sys/vm/swappiness");
			} else if (arg0.equals(set_swappiness_10)) {
				su.exec("echo 10 > /proc/sys/vm/swappiness");
			} else if (arg0.equals(get_info)) {
				log.append("Swappiness: ");
				log.append(su.exec_o("cat /proc/sys/vm/swappiness"));
				// structure
				// arraylists
				// total, used, free
				try {
					String[] free = su.exec_o("free").split("\n");
					ArrayList<String> mem = new ArrayList<String>();
					for (String s : free[1].split(" ")) {
						s.replace(" ", "");
						if (s.length() > 0) {
							mem.add(s);
						}
					}
					log.append("Memory:\t" + mem.get(1) + " KB \n\tUsed:\t"
							+ mem.get(2) + "\n\tfree:\t" + mem.get(3) + "\n");

					mem = new ArrayList<String>();
					for (String s : free[2].split(" ")) {
						s.replace(" ", "");
						if (s.length() > 0) {
							mem.add(s);
						}
					}
					log.append("Swap: \t" + mem.get(1) + " KB \n\tUsed:\t"
							+ mem.get(2) + "\n\tfree\t" + mem.get(3) + "\n");
				} catch (ArrayIndexOutOfBoundsException e) {
					log.append("Reading error. Please try one more time.");
				}
			} else {
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.append(e.getMessage());
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences("Swapper", 0);
		swapSize = settings.getInt("swapsize", 32);
		swapPlace = settings.getString("swapplace", "/sdcard/swapfile.swp");
		setContentView(R.layout.main);
		swapon_32 = (Button) findViewById(R.id.SwapOn_32);
		log = (TextView) findViewById(R.id.LogText);
		swapon_32.setOnClickListener(this);
		swapon_32.setText("Swap ON (" + swapSize + " MB)");
		swapoff = (Button) findViewById(R.id.SwapOff);
		swapoff.setOnClickListener(this);
		get_swappiness = (Button) findViewById(R.id.GetSwappiness);
		get_swappiness.setOnClickListener(this);
		set_swappiness = (Button) findViewById(R.id.SetSwappiness);
		set_swappiness.setOnClickListener(this);
		get_info = (Button) findViewById(R.id.GetInfo);
		get_info.setOnClickListener(this);
		set_swappiness_10 = (Button) findViewById(R.id.SetSwappiness10);
		set_swappiness_10.setOnClickListener(this);
		startsettings = (Button) findViewById(R.id.StartSettings);
		startsettings.setOnClickListener(this);
		swappiness = (EditText) findViewById(R.id.Swappiness);
		try {
			su = new SuCommander();
		} catch (IOException e) {
			log.append(e.getMessage());
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!su.isReady()) {
			// dia.incrementProgressBy(10);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		dia.dismiss();
		// log.setText(su.getOutput());
	}
}
