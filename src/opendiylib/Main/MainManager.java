package opendiylib.Main;

import java.util.Scanner;

import opendiylib.CommonUtils.LogUtils;
import opendiylib.SocketFunction.TcpCommandServer;

public class MainManager {

	public static final String TAG = MainManager.class.getSimpleName();
	public static final boolean DEBUG = true;
	//1.scanner
	private boolean mListenCommand = false;
	private Scanner mScanner = null;
	
	public MainManager() {
		LogUtils.LOGD(TAG, "Main");
		init();
	}

	private void init() {
		LogUtils.LOGD(TAG, "init");
		doShutDownWork();
	}
	
	private void doShutDownWork() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	LogUtils.LOGD(TAG, "doShutDownWork");
		    	stopMain(false);
		    }  
		});

	}
	
	private TcpCommandServer mTcpCommandServer = new TcpCommandServer(19999);
	
	public void startMain(boolean restart) {
		LogUtils.LOGD(TAG, "startMain");
		if (!restart) {
			startListenCommand();
		}
		//add function
		mTcpCommandServer.startServer();
	}
	
	public void stopMain(boolean restart) {
		LogUtils.LOGD(TAG, "stopMain");
		if (!restart) {
			stopListenCommand();
		}
		//add release function
		mTcpCommandServer.stopServer();
	}
	
	public void restartMain() {
		LogUtils.LOGD(TAG, "restartMain");
		stopMain(true);
		startMain(true);
	}
	
	private void startListenCommand() {
		LogUtils.LOGD(TAG, "startListenCommand");
		mListenCommand = true;
		mScanner = new Scanner(System.in);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String readLine = null;
				while (mListenCommand) {
					try {
						if (mScanner != null) {
							readLine = mScanner.nextLine();
							dealReadLine(readLine);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					if (mScanner != null) {
						mScanner.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				mScanner = null;
				LogUtils.LOGD(TAG, "startListenCommand over");
			}
		}).start();
	}
	
	private void stopListenCommand() {
		LogUtils.LOGD(TAG, "stopListenCommand");
		mListenCommand = false;
		if (mScanner != null) {
			mScanner.close();
		}
	}
	
	private String[] parseCommand(String command) {
		if (DEBUG) {
			LogUtils.LOGD(TAG, "parseCommand:" + command);
		}
		String[] result = null;
		if (command != null && command.length() > 0) {
			result = command.split("-");
		}
		if (result == null) {
			result = new String[1];
			result[0] = "";
		}
		return result;
	}
	
	private void dealReadLine(String readLine) {
		if (DEBUG) {
			LogUtils.LOGD(TAG, "dealReadLine:" + readLine);
		}
		String[] command = parseCommand(readLine);
		switch (command[0]) {
			case "exit":
				System.exit(0);
				break;
			case "restart":
				restartMain();
				break;
			case "print":
				if (command.length > 1) {
					LogUtils.LOGD(TAG, "print command:" + command[1]);
				} else {
					LogUtils.LOGD(TAG, "print command:no content");
				}
				break;
			case "help":
				LogUtils.LOGD(TAG, "help need add");
				break;
			case "status":
				LogUtils.LOGD(TAG, "status need add");
				break;
			default:
				LogUtils.LOGD(TAG, "unkown command");
				break;
		}
	}
}
