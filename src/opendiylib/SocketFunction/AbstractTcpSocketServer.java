package opendiylib.SocketFunction;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import opendiylib.CommonUtils.HostAddressUtils;
import opendiylib.CommonUtils.LogUtils;
import opendiylib.Main.MainManager;

/**
 * AbstractTcpSocketServer
 * 
 * @author zhiwei.yan
 * @since 2019-12-22
 */
public abstract class AbstractTcpSocketServer implements Runnable {

	public static final String TAG = AbstractTcpSocketServer.class.getSimpleName();
	public static final boolean DEBUG = true;
	
	private int mPort = 0;
	private boolean mRunFlag = false;
	private boolean mReconnect = false;
	private ServerSocket mServerSocket = null;
	private String mServerAddress = null;
	private List<AbstractTcpSocketReceiver> clients = new ArrayList<AbstractTcpSocketReceiver>();

	public AbstractTcpSocketServer(int port) {
		this.mPort = port;
	}

	public void startServer() {
		if (mRunFlag) {
			LogUtils.LOGD(TAG, "startServer running already");
			return;
		} else {
			LogUtils.LOGD(TAG, "startServer");
			mRunFlag = true;
			new Thread(this).start();
		}
	}

	public void stopServer() {
		LogUtils.LOGD(TAG, "stopServer");
		mRunFlag = false;
	}

	public void restartServer() {
		LogUtils.LOGD(TAG, "restartServer");
		mReconnect = true;
		mRunFlag = false;
	}
	
	@Override
	public void run() {
		try {
			mServerSocket = new ServerSocket();
			mServerSocket.setReuseAddress(true);
			mServerAddress = HostAddressUtils.getLocalIp4Address();
			mServerSocket.bind(new InetSocketAddress(mServerAddress, mPort));
			mServerSocket.setSoTimeout(1000);
			while (mRunFlag) {
				try {
					final Socket socket = mServerSocket.accept();
					onStartReceiver(socket);
				} catch (SocketTimeoutException timeout) {
					//timeout.printStackTrace();
					continue;
				} catch (IOException e) {
					//System.out.println("accept IOException " + e.getMessage());
					e.printStackTrace();
					this.onConnectFailed();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			for (AbstractTcpSocketReceiver client : clients) {
				client.stop();
			}
			clients.clear();
			mServerSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.onServerStop();
		if (mReconnect) {
			mReconnect = false;
			startServer();
		}
	}

	protected void addReceiver(AbstractTcpSocketReceiver receiver) {
		clients.add(receiver);
		//this.onConnect(receiver);
	}
	
	protected void removeReceiver(AbstractTcpSocketReceiver receiver) {
		clients.remove(receiver);
		//this.onDisconnect(receiver);
	}
	
	//public abstract void onConnect(AbstractTcpSocketReceiver client);
	
	public abstract void onStartReceiver(Socket socket);

	public abstract void onConnectFailed();

	/*public abstract void onReceive(AbstractTcpSocketReceiver client, String s);

	public abstract void onDisconnect(AbstractTcpSocketReceiver client);*/

	public abstract void onServerStop();
}
