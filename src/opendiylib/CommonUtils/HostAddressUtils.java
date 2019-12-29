package opendiylib.CommonUtils;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import opendiylib.CommonUtils.LogUtils;

public class HostAddressUtils {

	public static final String TAG = HostAddressUtils.class.getSimpleName();
	public static final boolean DEBUG = false;
	
	public HostAddressUtils() {
		
	}
	
	public static String printAllAddress() {
		String hostAddress = "127.0.0.1";
		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			int netInterfacesCount = 0;
			int netAddressCount = 0;
			while (netInterfaces.hasMoreElements()) {
				netInterfacesCount++;
				NetworkInterface netInterface = netInterfaces.nextElement();
				if (DEBUG) {
					LogUtils.LOGD(TAG , "printAllAddress NetworkInterface " + netInterfacesCount + " = " + netInterface);
				}
				Enumeration<InetAddress> netAddresses = netInterface.getInetAddresses();
				netAddressCount = 0;
				while (netAddresses.hasMoreElements()) {
					netAddressCount++;
					InetAddress address = netAddresses.nextElement();
					String ip = address.getHostAddress();
					if (DEBUG) {
						LogUtils.LOGD(TAG , "printAllAddress NetworkInterface " + netInterfacesCount + " InetAddress " + netAddressCount + " = " + address);
					}
					hostAddress = ip;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return hostAddress;
	}
	
	public static List<Inet4Address> getLocalIp4AddressFromNetworkInterface() {
	    List<Inet4Address> addresses = new ArrayList<Inet4Address>();
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			if (netInterfaces == null) {
		        return addresses;
		    }
			int netInterfacesCount = 0;
			int netAddressCount = 0;
		    while (netInterfaces.hasMoreElements()) {
		    	netInterfacesCount++;
		        NetworkInterface netInterface = (NetworkInterface) netInterfaces.nextElement();
		        if (!isValidInterface(netInterface)) {
		            continue;
		        }
		        if (DEBUG) {
		        	LogUtils.LOGD(TAG , "getLocalIp4AddressFromNetworkInterface NetworkInterface " + netInterfacesCount + " = " + netInterface);
				}
		        Enumeration<InetAddress> netAddresses = netInterface.getInetAddresses();
		        netAddressCount = 0;
		        while (netAddresses.hasMoreElements()) {
		        	netAddressCount++;
		            InetAddress address = (InetAddress) netAddresses.nextElement();
		            if (isValidAddress(address)) {
		            	if (DEBUG) {
		            		LogUtils.LOGD(TAG , "getLocalIp4AddressFromNetworkInterface NetworkInterface " + netInterfacesCount + " InetAddress " + netAddressCount + " = " + address);
						}
		                addresses.add((Inet4Address) address);
		            }
		        }
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return addresses;
	}

	private static boolean isValidInterface(NetworkInterface ni) throws SocketException {
	    return !ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
	            && (ni.getName().startsWith("eth") || ni.getName().startsWith("ens"));
	}

	private static boolean isValidAddress(InetAddress address) {
	    return address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress();
	}

	private static String getIpBySocket() {
		String result = "127.0.0.1";
		DatagramSocket socket = null;
	    try {
	    	socket = new DatagramSocket();
	        socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
	        if (socket.getLocalAddress() instanceof Inet4Address) {
	        	result = socket.getLocalAddress().getHostAddress();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    if (socket != null) {
	    	socket.close();
	    }
	    if (DEBUG) {
	    	LogUtils.LOGD(TAG , "getIpBySocket result = " + result);
	    }
	    return result;
	}
	
	public static String getLocalIp4Address() {
		//TraceUtils.printCallStack(TAG, "getLocalIp4Address");
		String result = "127.0.0.1";
	    final List<Inet4Address> ipByNi = getLocalIp4AddressFromNetworkInterface();
	    if (ipByNi.isEmpty() || ipByNi.size() > 1) {
	        String ipBySocketOpt = getIpBySocket();
	        if (ipBySocketOpt != null) {
	        	result = ipBySocketOpt;
	        } else if (!ipByNi.isEmpty()) {
	        	result = ipByNi.get(0).getHostAddress();
	        }
	    } else {
	    	result = ipByNi.get(0).getHostAddress();
	    }
	    if (DEBUG) {
	    	LogUtils.LOGD(TAG , "getLocalIp4Address result = " + result);
	    }
	    return result;
	}
}
