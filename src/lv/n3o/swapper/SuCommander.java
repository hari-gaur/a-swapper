package lv.n3o.swapper;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Random;


/**
 * @author n3o
 */
public class SuCommander implements Runnable {
	Random				random;
	BufferedInputStream	err;
	private String		errors;
	private String		output;

	BufferedInputStream	reader;
	private boolean		ready;
	Thread				thread;
	OutputStream		writer;
	private boolean		success;

	public SuCommander() throws IOException {
		random = new Random();

		Process p;
		p = Runtime.getRuntime().exec("su");
		writer = p.getOutputStream();
		reader = new BufferedInputStream(p.getInputStream());
		err = new BufferedInputStream(p.getErrorStream());
		setReady(true);

	}

	public void exec(String command) throws IOException {
		if (isReady()) {
			writer.write((command).getBytes("ASCII"));
			thread = new Thread(this);
			setReady(false);
			thread.start();
		}
	}

	public String exec_o(String command) {
		try {
			exec(command);
			while (!ready) {
				Thread.sleep(100);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// do nothing
		}
		return getOutput();
	}

	@Override
	protected void finalize() throws Throwable {
		reader.close();
		writer.close();
		err.close();
		super.finalize(); // not necessary if extending Object.
	}

	/**
	 * @param what
	 *            to wait for (if not there, endless loop will occur)
	 * @return Output till waited string
	 * @throws IOException
	 */
	private String get_output(String what) throws IOException {
		StringBuilder output = new StringBuilder();
		StringBuilder error = new StringBuilder();
		int read;
		do {
			while (reader.available() > 0) {
				read = reader.read();
				output.append((char) read);
			}
			while (err.available() > 0) {
				read = err.read();
				error.append((char) read);
			}
			if ((what != null) && output.toString().contains(what)) {
				output = new StringBuilder(output.toString().replace(what, ""));
				what = null;
			}
			if (what != null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		} while (what != null);

		errors = error.toString();
		return output.toString();

	}

	/**
	 * @return the errors, null if no errors
	 */
	public String getErrors() {
		if (errors != null) {
			if (errors.length() == 0) {
				return null;
			}
		}
		return errors;
	}

	/**
	 * @return the output
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * @return the ready
	 */
	public boolean isReady() {
		return ready;
	}

	public boolean isSuccess() {
		return success;
	}

	/**
	 * @return randomized hash for identifying "end of output"
	 */
	private String make_id() {
		long r1 = random.nextLong();
		long r2 = random.nextLong();
		String hash1 = Long.toHexString(r1);
		String hash2 = Long.toHexString(r2);
		return hash1 + hash2;

	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		setOutput("");
		String id = make_id();
		String success = make_id();
		try {
			writer.write((" && echo " + success + " \n \n echo " + id + "\n")
					.getBytes("ASCII"));
			String output = get_output(id);
			if (output.toString().contains(success)) {
				output = new StringBuilder(output.toString().replace(success,
						"")).toString();
				setSuccess(true);
			} else {
				setSuccess(false);
			}
			setOutput(output);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setReady(true);
	}

	/**
	 * @param output
	 *            the output to set
	 */
	private void setOutput(String output) {
		this.output = output;
	}

	/**
	 * @param ready
	 *            the ready to set
	 */
	private void setReady(boolean ready) {
		this.ready = ready;
	}

	private void setSuccess(boolean success) {
		this.success = success;
	}
}
