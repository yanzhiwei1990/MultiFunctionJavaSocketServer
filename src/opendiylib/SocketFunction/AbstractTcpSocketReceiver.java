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
	protected byte[] databuffer;
	protected int datacount = 0;
	private boolean runFlag;

	public AbstractTcpSocketReceiver(Socket socket) {
		this.socket = socket;
		this.addr = socket.getInetAddress();
	}

	public InetAddress getInetAddress() {
		return addr;
	}

	public Socket getSocket() {
		return socket;
	}
	
	public void start() {
		runFlag = true;
		new Thread(this).start();
	}

	public void stop() {
		runFlag = false;
		try {
			if (socket != null) {
				socket.shutdownInput();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (socket != null) {
				socket.shutdownOutput();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean sendString(String s) {
		if (out != null) {
			try {
				//out.writeUTF(s);
				out.writeBytes(s);
				out.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean sendBytes(byte[] buffer) {
		if (out != null) {
			try {
				out.write(buffer);
				out.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean sendBytesByLength(byte[] buffer, int length) {
		if (out != null) {
			try {
				out.write(buffer, 0, length);
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
			databuffer = new byte[1024 * 1024];
			in = new DataInputStream(this.socket.getInputStream());
			out = new DataOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			runFlag = false;
		}
		while (runFlag) {
			try {
				//System.out.println("run start read");
				datacount = in.read(databuffer);
				if (datacount != -1) {
					//System.out.println("run read over");
					this.onReceive(databuffer, datacount);
				} else {
					System.out.println(addr + " Disconnect");
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				runFlag = false;
			}
		}
		runFlag = false;
		try {
			if (in != null) {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			in = null;
		}
		try {
			if (out != null) {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out = null;
		}
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket = null;
		}
		this.onDisconnect();
	}

	public abstract void onConnect();
	
	public abstract void onReceive(byte[] buf, int length);

	public abstract void onDisconnect();
}
