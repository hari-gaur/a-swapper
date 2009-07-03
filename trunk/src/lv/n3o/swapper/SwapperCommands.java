package lv.n3o.swapper;


import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;


public class SwapperCommands extends Thread {
	static class command {
		String	title;
		String	command;

		public command(String title, String command) {
			super();
			this.title = title;
			this.command = command;
		}

		public String getCommand() {
			return command;
		}

		public String getTitle() {
			return title;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}

	SharedPreferences			settings;
	Context						context;
	static SuCommander			su;
	String						swapPlace;
	int							swapSize;
	String						status;
	int							swappiness;
	Handler						handler;
	static ArrayList<command>	commands;

	static Thread				t;			;

	public SwapperCommands(Context c) {
		init_commands(c, (Handler) null);
	}

	public SwapperCommands(Context c, Handler h) {
		init_commands(c, h);
	}

	private void init_commands(Context c, Handler h) {
		handler = h;
		if (commands == null) {
			commands = new ArrayList<command>();
		}
		if (su == null) {
			try {
				su = new SuCommander();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
		context = c;
		settings = PreferenceManager.getDefaultSharedPreferences(c);
		try {
			su = new SuCommander();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		swapSize = Integer.parseInt(settings.getString("swapsize", "32"));
		swapPlace = settings.getString("swapplace", "/sdcard/swapfile.swp");
		swappiness = Integer.parseInt(settings.getString("swappiness", "10"));

	}

	public void run() {
		try {
			boolean done = false;
			while (t != null) {
				while (!su.isReady()) {
					Thread.sleep(100);
				}
				if (!commands.isEmpty()) {
					done = true;
					command c = commands.remove(0);
					su.exec(c.getCommand());
					if (handler != null) {
						Message m = Message.obtain();
						m.obj = c.getTitle();
						handler.sendMessage(m);
					}
				} else {
					if (handler != null && done) {
						done = false;
						Message m = Message.obtain();
						m.obj = "All done!";
						handler.sendMessage(m);
					}
				}
				Thread.sleep(1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void swapoff() {
		commands.add(new command("Turning swap off", "swapoff " + swapPlace + " && rm "
				+ swapPlace));
	}

	public void swapon() {
		commands.add(new command("Turning swap on", "dd if=/dev/zero of=" + swapPlace
				+ " bs=1048576 count=" + swapSize + " && busybox mkswap "
				+ swapPlace + " && swapon " + swapPlace));
	}

	public void swappiness() {
		commands.add(new command("Setting swappiness", "echo " + swappiness
				+ " > /proc/sys/vm/swappiness"));
	}
}
