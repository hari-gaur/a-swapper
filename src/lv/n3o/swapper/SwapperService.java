package lv.n3o.swapper;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * @author n3o Class for abstracting swapper (things should be done in service,
 *         not in UI)
 */
class SwapperService extends Service {

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void swapOn() {

	}

	private void swapOff() {

	}

	private void setSwappiness(int swappiness) {
		if (swappiness < 0 || swappiness > 100)
			return; // incorrect swappiness;
	}
}
