package opendiylib.CommonUtils;

import java.lang.management.ManagementFactory;

public class ProcessUtils {

	public static final String TAG = ProcessUtils.class.getSimpleName();
	public static final boolean DEBUG = true;
	
	public ProcessUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getProcessPid() {
		String result = "-1";
		String name = ManagementFactory.getRuntimeMXBean().getName();
		if (name != null) {
			String[] split =  name.split("@");
			if (split != null && split.length > 0) {
				result = split[0];
			}
		}
		return result;
	}
}
