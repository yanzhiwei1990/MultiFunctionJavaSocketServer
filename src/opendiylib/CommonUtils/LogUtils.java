package opendiylib.CommonUtils;

public class LogUtils {
	
	public static final String TAG = LogUtils.class.getSimpleName();
	public static final boolean DEBUG = true;
	public static final String LOG_LEVEL_DEBUG = "debug";
	public static final String LOG_LEVEL_WARNING = "warning";
	public static final String LOG_LEVEL_ERROR = "error";
	
	public LogUtils() {
		// TODO Auto-generated constructor stub
	}

	public static void LOG(String level, String tag, String log) {
		if (DEBUG) {
			switch (level) {
				case LOG_LEVEL_DEBUG:
					printFunction(tag + "-d:" + log);
					break;
				case LOG_LEVEL_WARNING:
					printFunction(tag + "-w:" + log);
					break;
				case LOG_LEVEL_ERROR:
					printFunction(tag + "-e:" + log);
					break;
				default:
					printFunction(tag + "-0:unkown log level");
					break;
			}
		}
	}
	
	public static void LOGD(String tag, String log) {
		if (DEBUG) {
			printFunction(TimeUtils.getCurrentTime() + "  " + tag + "(D)(" + ProcessUtils.getProcessPid() + "):" + log);
		}
	}
	
	public static void LOGW(String tag, String log) {
		if (DEBUG) {
			printFunction(TimeUtils.getCurrentTime() + "  " + tag + "(W)(" + ProcessUtils.getProcessPid() + "):" + log);
		}
	}
	
	public static void LOGE(String tag, String log) {
		if (DEBUG) {
			printFunction(TimeUtils.getCurrentTime() + "  " + tag + "(E)(" + ProcessUtils.getProcessPid() + "):" + log);
		}
	}
	
	public static void printFunction(String log) {
		System.out.println(log);
	}
}
