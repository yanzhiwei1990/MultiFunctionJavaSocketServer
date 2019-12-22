package opendiylib.SocketFunction;

import java.net.InetAddress;
import java.net.Socket;

public class TcpCommandServer extends AbstractTcpSocketServer {

	public static final String TAG = TcpCommandServer.class.getSimpleName();
	public static final boolean DEBUG = true;
	
	private TcpCommandReceiver mTcpCommandReceiver = null;
	
	private int mPort = 0;
	
	public TcpCommandServer(int port) {
		super(port);
		this.mPort = port;
	}

	/*@Override
	public void onConnect(AbstractTcpSocketReceiver client) {
		// TODO Auto-generated method stub
		printInfo(client, "Connect");
	}*/

	@Override
	public void onStartReceiver(Socket socket) {
		// TODO Auto-generated method stub
		mTcpCommandReceiver = new TcpCommandReceiver(socket);
		TcpCommandServer.this.addReceiver(mTcpCommandReceiver);
		mTcpCommandReceiver.onConnect();
		mTcpCommandReceiver.start();
	}

	@Override
	public void onConnectFailed() {
		// TODO Auto-generated method stub
		System.out.println("Client Connect Failed");
	}

	@Override
	public void onServerStop() {
		// TODO Auto-generated method stub
		System.out.println("--------Server Stopped--------");
	}
	
	/*public void onReceive(InetAddress addr, String s) {
		// TODO Auto-generated method stub
		printInfo(mTcpCommandReceiver, "Send Data: " + s);
		mTcpCommandReceiver.send(s);
	}*/
	
	/*public void onDisconnect(InetAddress addr) {
		// TODO Auto-generated method stub
		printInfo(mTcpCommandReceiver, "Disconnect");
	}*/
	
	public static void printInfo(AbstractTcpSocketReceiver st, String msg) {
		System.out.println("Client " + st.getInetAddress().getHostAddress());
		System.out.println("  " + msg);
	}
	
	private class TcpCommandReceiver extends AbstractTcpSocketReceiver {

		public TcpCommandReceiver(Socket socket) {
			super(socket);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onConnect() {
			printInfo(this, "Connect");
		}
		
		@Override
		public void onReceive(String s) {
			// TODO Auto-generated method stub
			printInfo(this, "Send Data: " + s);
			send(s);
		}

		@Override
		public void onDisconnect() {
			// TODO Auto-generated method stub
			printInfo(mTcpCommandReceiver, "Disconnect");
		}
	}
}