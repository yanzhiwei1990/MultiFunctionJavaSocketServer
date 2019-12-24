package opendiylib.SocketFunction;

import java.net.Socket;

import opendiylib.CommonUtils.LogUtils;

public class CommonTcpServer extends AbstractTcpSocketServer {

	public static final String TAG = CommonTcpServer.class.getSimpleName();
	public static final boolean DEBUG = true;
	
	private TcpCommandReceiver mTcpCommandReceiver = null;
	
	private int mPort = 0;
	
	public CommonTcpServer(int port) {
		super(port);
		this.mPort = port;
	}

	@Override
	public void onStartReceiver(Socket socket) {
		mTcpCommandReceiver = new TcpCommandReceiver(socket);
		CommonTcpServer.this.addReceiver(mTcpCommandReceiver);
		mTcpCommandReceiver.onConnect();
		mTcpCommandReceiver.start();
	}

	@Override
	public void onConnectFailed() {
		LogUtils.LOGD(TAG, "--------onConnectFailed--------");
	}

	@Override
	public void onServerStop() {
		LogUtils.LOGD(TAG, "--------onServerStop--------");
	}
	
	private class TcpCommandReceiver extends AbstractTcpSocketReceiver {

		public TcpCommandReceiver(Socket socket) {
			super(socket);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onConnect() {
			printReceiverInfo(this, "onConnect");
		}
		
		@Override
		public void onReceive(byte[] buffer, int length) {
			// TODO Auto-generated method stub
			String receiveData = new String(buffer, 0, length);
			printReceiverInfo(this, "onReceive:" + receiveData);
			sendString(receiveData);
		}

		@Override
		public void onDisconnect() {
			// TODO Auto-generated method stub
			CommonTcpServer.this.removeReceiver(mTcpCommandReceiver);
			printReceiverInfo(mTcpCommandReceiver, "onDisconnect");
		}
	}
	
	public static void printReceiverInfo(AbstractTcpSocketReceiver st, String msg) {
		LogUtils.LOGD(TAG, st.getInetAddress() + "  " + msg);
	}
}