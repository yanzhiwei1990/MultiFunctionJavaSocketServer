package opendiylib.Main;

import opendiylib.CommonUtils.LogUtils;

public class Main {

	public static final String TAG = Main.class.getSimpleName();
	public static final boolean DEBUG = false;
	
	public Main() {
		LogUtils.LOGD(TAG, "Main");
	}

	public static void main(String[] args) {
		MainManager mainManager = new MainManager();
		mainManager.startMain(false);
	}
}
