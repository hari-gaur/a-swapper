package lv.n3o.swapper;


import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class Swapper extends Activity implements OnClickListener {

	ProgressDialog	dia;
	Button			get_info;
	TextView		log;
	Thread			progress;
	Button			set_swappiness;
	Button			startsettings;
	SuCommander		su;
	Button			swapoff;
	Button			swapon;
	Button			recreateSwap;
	Button			formatSwap;
	private Handler	handler	= new Handler() {
								@Override
								public void handleMessage(Message msg) {
									log.append("\n->" + (String) msg.obj);
								}
							};

	@Override
	public void onClick(View arg0) {
		SwapperCommands sc = new SwapperCommands(this, handler);
		if (arg0.equals(startsettings)) {
			sc.swapOff();
			Intent i = new Intent(this, SwapperPreferences.class);
			startActivity(i);
			return;
		}
		log.setText("Please wait...\n");
		if (arg0.equals(swapon)) {
			sc.swappiness();
			sc.swapOff();
			sc.swapOn();
		} else if (arg0.equals(swapoff)) {
			sc.swapOff();
		} else if (arg0.equals(recreateSwap)) {
			sc.swapOff();
			sc.createSwapFile();
		} else if (arg0.equals(formatSwap)) {
			sc.swapOff();
			sc.formatSwap();
		}

		else if (arg0.equals(get_info)) {
			log.setText("Swappiness: ");
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
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		swapon = (Button) findViewById(R.id.SwapOn_32);
		recreateSwap = (Button) findViewById(R.id.RecreateSwapButton);
		formatSwap = (Button) findViewById(R.id.FormatSwapButton);
		log = (TextView) findViewById(R.id.LogText);
		swapon.setOnClickListener(this);
		swapon.setText("Swap ON");
		swapoff = (Button) findViewById(R.id.SwapOff);
		swapoff.setOnClickListener(this);
		get_info = (Button) findViewById(R.id.GetInfo);
		get_info.setOnClickListener(this);
		startsettings = (Button) findViewById(R.id.StartSettings);
		startsettings.setOnClickListener(this);
		recreateSwap.setOnClickListener(this);
		formatSwap.setOnClickListener(this);
		try {
			su = new SuCommander();
		} catch (IOException e) {
			log.append(e.getMessage());
			e.printStackTrace();
			// TODO: Here should be info about root :)
			finish();
		}

	}
}
