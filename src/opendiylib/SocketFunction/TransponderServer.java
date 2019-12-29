package opendiylib.SocketFunction;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import opendiylib.CommonUtils.LogUtils;
import opendiylib.SocketFunction.CommonTcpServer.CommonDataParse;

public class TransponderServer extends AbstractTcpSocketServer {
	private final String TAG = TransponderServer.class.getSimpleName();
	
	private int mPort = 0;
	
	public TransponderServer(int port) {
		super(port);
		mPort = port;
	}

	@Override
	public void onStartReceiver(Socket socket) {
		TransponderReceiver mTransponderReceiver = new TransponderReceiver(socket);
		addReceiver(mTransponderReceiver);
		mTransponderReceiver.onConnect();
		mTransponderReceiver.start();
	}

	@Override
	public void onConnectFailed() {
		LogUtils.LOGD(TAG, "onConnectFailed");
	}

	@Override
	public void onServerStart(ServerSocket serverSocket) {
		LogUtils.LOGD(TAG, "onServerStart " + serverSocket);
	}

	@Override
	public void onServerStop() {
		LogUtils.LOGD(TAG, "onServerStop");
	}
	
	private class TransponderReceiver extends AbstractTcpSocketReceiver {
		
		private DataParse mDataParse = new DataParse();
		
		public TransponderReceiver(Socket socket) {
			super(socket);
		}

		@Override
		public void onConnect() {
			LogUtils.LOGD(TAG, "onConnect");
		}

		@Override
		public void onReceive(byte[] buf, int length) {
			LogUtils.LOGD(TAG, "onReceive length = " + length);
			mDataParse.dealData(buf, length);
		}

		@Override
		public void onDisconnect() {
			LogUtils.LOGD(TAG, "onConnect");
		}
    }
	
	private class DataParse {
		
		private final String TYPE_REQUEST = "request";
		private final String TYPE_RESPONSE = "response";
		private final int[] NODE_MCU_HEAD_LENGTH = {6, 20, 20, 20, 20, 8, 6};//^head#0123450123456789name012345670123456789id0120123456789command0123450123456789data 0, 0, 1, 164 255, 255, 254, 91 #tail$
		private final int[] NODE_MCU_DATA_LENGTH = {0, 0, 1, 164, 255, 255, 254, 91};
		
		private String mPacketHead = null;
		private String mHead = null;
		private String mTail = null;
		private String mName = null;
		private String mId = null;
		private String mCommand = null;
		private String mData = null;
		private int mDataNumber = 0;
		private int mReceiveTotalNumber = 0;
		private boolean mOver = false;
		private byte[] mBuffer = new byte[1024 * 1024];
		
		private DataParse() {
			
		}
		
		public int dealData(byte[] buffer, int length) {
			int status = 0;
			if (buffer != null && length > 0) {
				//get 100 bytes of head data at least
				System.arraycopy(buffer, 0, mBuffer, mReceiveTotalNumber, length);
				mReceiveTotalNumber += length;
				String head = null;
				if (length == 100) {
					LogUtils.LOGD(TAG, "dealData 100 bytes head available " + mReceiveTotalNumber);
					if (mPacketHead == null) {
						mPacketHead = new String(mBuffer, 0, 100);//all head
						mHead = new String(mBuffer, 0, 6);//6
						mName = new String(mBuffer, 6, 20);//20
						mId = new String(mBuffer, 26, 20);//20
						mCommand = new String(mBuffer, 46, 20);//20
						mData = new String(mBuffer, 66, 20);//20
						//86~93
						int[] checkArray = new int[4];
						boolean checkResult = true;
						for (int i = 86; i < 94; i++) {
							LogUtils.LOGD(TAG, "dealData check i = " + i + ":"+ Integer.toHexString(Byte.toUnsignedInt(mBuffer[i])));
							if (i >= 86 && i <= 89) {
								checkArray[i - 86] = Byte.toUnsignedInt(mBuffer[i]) + Byte.toUnsignedInt(mBuffer[i + 4]);
								if (checkArray[i - 86] != 255 && checkResult) {
									checkResult = false;
								}
								mDataNumber += Byte.toUnsignedInt(mBuffer[i]) * Math.pow(256, 3 - (i - 86));
							}
						}
						mTail = new String(mBuffer, 94, 6);//6
						if (checkResult && "^head#".equals(mHead) && "#tail$".equals(mTail) && mDataNumber == mReceiveTotalNumber - 100) {
							LogUtils.LOGD(TAG, "dealData mDataNumber = " + mDataNumber);
							mOver = true;
							status = 1;
						} else {
							return status;
						}
					}
				}
			}
			return status;
		}
	}
	
	private class ExchangeData {
		private ReadWriteRunnable fromToRunnable;
		private ReadWriteRunnable toFromRunnable;
		
		private ExchangeData(ReadWriteRunnable from, ReadWriteRunnable to) {
			this.fromToRunnable = from;
			this.toFromRunnable = to;
		}
		
		private void startReadWrite() {
			if (fromToRunnable != null) {
				fromToRunnable.startRun();
			}
			if (toFromRunnable != null) {
				toFromRunnable.startRun();
			}
	    }
	    
		private void stopReadWrite() {
	    	if (fromToRunnable != null) {
				fromToRunnable.stopRun();
			}
			if (toFromRunnable != null) {
				toFromRunnable.stopRun();
			}
	    }
	}
}
