package opendiylib.SocketFunction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * AbstractTcpSocketReceiver
 * 
 * @author zhiwei.yan
 * @since 2019-12-22
 */
public abstract class AbstractTcpSocketReceiver implements Runnable {

	protected Socket socket;
	protected InetAddress addr;
	protected DataInputStream in;
	protected DataOutputStream out;
	private boolean runFlag;

	public AbstractTcpSocketReceiver(Socket socket) {
		this.socket = socket;
		this.addr = socket.getInetAddress();
	}

	public InetAddress getInetAddress() {
		return addr;
	}

	public void start() {
		runFlag = true;
		new Thread(this).start();
	}

	public void stop() {
		runFlag = false;
		try {
			socket.shutdownInput();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean send(String s) {
		if (out != null) {
			try {
				out.writeUTF(s);
				out.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void run() {
		try {
			in = new DataInputStream(this.socket.getInputStream());
			out = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			runFlag = false;
		}
		while (runFlag) {
			try {
				System.out.println("run start read");
				final int s = in.read();
				if (s != -1) {
					System.out.println("run read over");
					this.onReceive(String.format("%c", s));
				} else {
					System.out.println("run read exit");
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				runFlag = false;
			}
		}
		try {
			runFlag = false;
			in.close();
			out.close();
			socket.close();
			in = null;
			out = null;
			socket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.onDisconnect();
	}

	public abstract void onConnect();
	
	public abstract void onReceive(String s);

	public abstract void onDisconnect();
}
