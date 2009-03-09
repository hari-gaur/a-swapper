/**
 * 
 */
package lv.n3o.swapper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author n3o
 * 
 */
public class SuCommander {

	OutputStream writer;
	BufferedInputStream reader;
	BufferedInputStream err;

	private String errors;

	/**
	 * @return the errors, null if no errors
	 */
	public String getErrors() {
		if (errors.length() == 0)
			return null;
		return errors;
	}

	public SuCommander() throws IOException {
		Process p;
		p = Runtime.getRuntime().exec("su");
		writer = p.getOutputStream();
		reader = new BufferedInputStream(p.getInputStream());
		err = new BufferedInputStream(p.getErrorStream());

	}

	protected void finalize() throws Throwable
	{
		reader.close();
		writer.close();
		err.close();
		super.finalize(); //not necessary if extending Object.
	} 

	public void exec(String command) throws IOException {
		writer.write((command + "\n").getBytes("ASCII"));
	}

	/**
	 * @return output
	 * @throws IOException 
	 */
	public String get_output() throws IOException {
		return get_output(null);
	}


	/**
	 * @param what = what to wait for (if not there, endless loop will occur)
	 * @return
	 * @throws IOException 
	 */
	public String get_output(String what) throws IOException {
		StringBuilder output = new StringBuilder();
		StringBuilder error = new StringBuilder();
		int read; // nolasīšanai
		do {
			while (reader.available() > 0) {
				read = reader.read();
				output.append((char) read);
			}
			while (err.available() > 0) {
				read = err.read();
				error.append((char) read);
			}
			if (what != null && output.toString().contains(what)) {
				what = null;		
			}
			if (what != null)
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} while (what != null);

		errors = error.toString();
		return output.toString();

	}

	public String exec_o(String arg0) throws IOException {
		exec(arg0);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return get_output();
	}
}
