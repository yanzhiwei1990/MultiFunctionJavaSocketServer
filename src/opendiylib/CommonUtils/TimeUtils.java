package opendiylib.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

	public static final String TAG = TimeUtils.class.getSimpleName();
	public static final boolean DEBUG = true;
	
	public TimeUtils() {
		// TODO Auto-generated constructor stub
	}

	public static void delayMs(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static String getCurrentTime() {
		String result = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		result = dateFormat.format(new Date());
		return result;
	}
}
