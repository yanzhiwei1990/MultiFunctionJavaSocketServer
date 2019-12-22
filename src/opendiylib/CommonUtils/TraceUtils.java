package opendiylib.CommonUtils;

import java.util.Arrays;

public class TraceUtils {

	public static final String TAG = TraceUtils.class.getSimpleName();
	public static final boolean DEBUG = true;
	
	public TraceUtils() {
		// TODO Auto-generated constructor stub
	}

	public static void printCallStack(String tag, String title) {
		Throwable ex = new Throwable(title);
		StackTraceElement[] stackElements = ex.getStackTrace();
		LogUtils.LOGD(tag, "---" + title + " CallStack");
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {
            	LogUtils.LOGD(tag, "------" + stackElements[i]);
            }
        }
	}
}
