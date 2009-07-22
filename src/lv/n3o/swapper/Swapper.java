package lv.n3o.swapper;


import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
	Button			busybox;
	View			downloadBusybox			= new View(null);
	private Handler	handler					= new Handler() {
												@Override
												public void handleMessage(
														Message msg) {
													log.append("\n->"
															+ (String) msg.obj);
												}
											};

	final int		DIALOG_YES_NO_MESSAGE	= 999;

	@Override
	public void onClick(View arg0) {
		final SwapperCommands sc = new SwapperCommands(this, handler);
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
		} else if (arg0.equals(busybox)) {
			showDialog(DIALOG_YES_NO_MESSAGE);
		} else if (arg0.equals(downloadBusybox)) {
			final ProgressDialog pd = ProgressDialog.show(Swapper.this,
					"Please wait!", "Downloading busybox...", true);
			new Thread() {
				public void run() {
					try {
						sc.prepareBusybox();
					} catch (Exception e) {
					}
					pd.dismiss();
				}
			}.start();
		}

		else if (arg0.equals(get_info)) {
			log.setText("Swappiness: ");
			log.append(su.exec_o("cat /proc/sys/vm/swappiness"));
			// structure
			// array lists
			// total, used, free
			// TODO: it seems that some roms have different free output
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
		busybox = (Button) findViewById(R.id.Busybox);
		busybox.setOnClickListener(this);
		try {
			su = new SuCommander();
		} catch (IOException e) {
			log.append(e.getMessage());
			e.printStackTrace();
			// TODO: Here should be info about root :)
			finish();
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
			case DIALOG_YES_NO_MESSAGE:
				return new AlertDialog.Builder(this).setTitle(
						"Install busybox?").setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Swapper.this.onClick(downloadBusybox);
							}
						}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								// User clicked Cancel so do some stuff

								System.out.println("cancel clicked.");
							}
						}).create();
		}
		return null;
	}

}
