package opendiylib.SocketFunction;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import org.omg.CORBA.INTERNAL;

import opendiylib.CommonUtils.LogUtils;

public class CommonTcpServer extends AbstractTcpSocketServer {

	public static final String TAG = CommonTcpServer.class.getSimpleName();
	public static final boolean DEBUG = true;
	
	private TcpCommonReceiver mTcpCommonReceiver = null;
	
	private int mPort = 0;
	
	public CommonTcpServer(int port) {
		super(port);
		this.mPort = port;
	}

	@Override
	public void onStartReceiver(Socket socket) {
		mTcpCommonReceiver = new TcpCommonReceiver(socket);
		CommonTcpServer.this.addReceiver(mTcpCommonReceiver);
		mTcpCommonReceiver.onConnect();
		mTcpCommonReceiver.start();
	}

	@Override
	public void onConnectFailed() {
		LogUtils.LOGD(TAG, "--------onConnectFailed--------");
	}

	@Override
	public void onServerStart(ServerSocket serverSocket) {
		LogUtils.LOGD(TAG, "--------onServerStart--------");
	}
	
	@Override
	public void onServerStop() {
		LogUtils.LOGD(TAG, "--------onServerStop--------");
	}
	
	private class TcpCommonReceiver extends AbstractTcpSocketReceiver {

		private CommonDataParse mCommonDataParse = new CommonDataParse();
		
		public TcpCommonReceiver(Socket socket) {
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
			int result = mCommonDataParse.dealData(buffer, length);
			if (result == 2) {
				printReceiverInfo(this, "onReceive: parse over=" + mCommonDataParse.getClientInfomation().getPacketHead());
				sendString("receive extra data = " + (mCommonDataParse.getClientInfomation().getReceiveTotalNumber() - 100 - mCommonDataParse.getClientInfomation().getDataNumber()));
				mCommonDataParse.initClientInformation();
			} else if (result == 1) {
				printReceiverInfo(this, "onReceive: parse over=" + mCommonDataParse.getClientInfomation().getPacketHead());
				//sendBytesByLength(mCommonDataParse.getClientInfomation().getBuffer(), mCommonDataParse.getClientInfomation().getReceiveTotalNumber());
				sendString("receive data = " + mCommonDataParse.getClientInfomation().getPacketHead());
				mCommonDataParse.initClientInformation();
			} else if (result == 0) {
				printReceiverInfo(this, "onReceive: parse continue");
			} else {
				printReceiverInfo(this, "onReceive: parse stop");
				mCommonDataParse.initClientInformation();
			}
		}

		@Override
		public void onDisconnect() {
			// TODO Auto-generated method stub
			CommonTcpServer.this.removeReceiver(mTcpCommonReceiver);
			printReceiverInfo(mTcpCommonReceiver, "onDisconnect");
		}
	}
	
	private void printReceiverInfo(AbstractTcpSocketReceiver st, String msg) {
		LogUtils.LOGD(TAG, st.getInetAddress() + "  " + msg);
	}
	
	public class CommonDataParse {
		
		public final String NODE_MCU = "nodemcu";
		public final int[] NODE_MCU_HEAD_LENGTH = {6, 20, 20, 20, 20, 8, 6};//^head# 0123450123456789name 012345670123456789id 0120123456789command 0123450123456789data 0, 0, 1, 164 255, 255, 254, 91 #tail$
		public final int[] NODE_MCU_DATA_LENGTH = {0, 0, 1, 164, 255, 255, 254, 91};
		
		private CommonClientInfoMation mCommonClientInfoMation = null;
		private boolean mDealOver = false;
		
		public CommonDataParse() {
			initClientInformation();
		}

		public void initClientInformation() {
			mCommonClientInfoMation = new CommonClientInfoMation();
		}
		
		public int dealData(byte[] buffer, int length) {
			int status = -1;
			if (buffer != null && length > 0) {
				//get 100 bytes of head data at least
				System.arraycopy(buffer, 0, mCommonClientInfoMation.mBuffer, mCommonClientInfoMation.mReceiveTotalNumber, length);
				mCommonClientInfoMation.mReceiveTotalNumber += length;
				String head = null;
				if (mCommonClientInfoMation.mReceiveTotalNumber < 100) {
					LogUtils.LOGD(TAG, "dealData 100 bytes head not available " + mCommonClientInfoMation.mReceiveTotalNumber);
					status = -1;
				} else if (length >= 100) {
					LogUtils.LOGD(TAG, "dealData 100 bytes head available " + mCommonClientInfoMation.mReceiveTotalNumber);
					if (mCommonClientInfoMation.mPacketHead == null) {
						mCommonClientInfoMation.mPacketHead = new String(mCommonClientInfoMation.mBuffer, 0, 100);//all head
						mCommonClientInfoMation.mHead = new String(mCommonClientInfoMation.mBuffer, 0, 6);//6
						mCommonClientInfoMation.mName = new String(mCommonClientInfoMation.mBuffer, 6, 20);//20
						mCommonClientInfoMation.mId = new String(mCommonClientInfoMation.mBuffer, 26, 20);//20
						mCommonClientInfoMation.mCommand = new String(mCommonClientInfoMation.mBuffer, 46, 20);//20
						mCommonClientInfoMation.mData = new String(mCommonClientInfoMation.mBuffer, 66, 20);//20
						int num1 = Byte.toUnsignedInt(mCommonClientInfoMation.mBuffer[86]);
						int num2 = Byte.toUnsignedInt(mCommonClientInfoMation.mBuffer[87]);
						int num3 = Byte.toUnsignedInt(mCommonClientInfoMation.mBuffer[88]);
						int num4 = Byte.toUnsignedInt(mCommonClientInfoMation.mBuffer[89]);
						int num5 = Byte.toUnsignedInt(mCommonClientInfoMation.mBuffer[90]);
						int num6 = Byte.toUnsignedInt(mCommonClientInfoMation.mBuffer[91]);
						int num7 = Byte.toUnsignedInt(mCommonClientInfoMation.mBuffer[92]);
						int num8 = Byte.toUnsignedInt(mCommonClientInfoMation.mBuffer[93]);
						//86~93
						int[] checkArray = new int[4];
						boolean checkResult = true;
						for (int i = 86; i < 94; i++) {
							LogUtils.LOGD(TAG, "dealData check i = " + i + ":"+ Integer.toHexString(Byte.toUnsignedInt(mCommonClientInfoMation.mBuffer[i])));
							if (i >= 86 && i <= 89) {
								checkArray[i - 86] = Byte.toUnsignedInt(mCommonClientInfoMation.mBuffer[i]) + Byte.toUnsignedInt(mCommonClientInfoMation.mBuffer[i + 4]);
								if (checkArray[i - 86] != 255 && checkResult) {
									checkResult = false;
								}
							}
						}
						mCommonClientInfoMation.mTail = new String(mCommonClientInfoMation.mBuffer, 94, 6);//6
						LogUtils.LOGD(TAG, "dealData checkResult = " + checkResult + ", array = " + Arrays.toString(checkArray) + " ,mHead=" + mCommonClientInfoMation.mHead +  ", mTail=" + mCommonClientInfoMation.mTail);
						//LogUtils.LOGD(TAG, "dealData check number 1 = " + (num1 + num5) + ", number 2 = " + (num2 + num6) + ", number 3 = " + (num3 + num7) + ", number 4 = " + (num4 + num8));
						if (checkResult && "^head#".equals(mCommonClientInfoMation.mHead) && "#tail$".equals(mCommonClientInfoMation.mTail)) {
							mCommonClientInfoMation.mDataNumber = num1 * 256 * 256 * 256 + num2 * 256 * 256 + num3 * 256 + num4;//8
							LogUtils.LOGD(TAG, "dealData mDataNumber = " + mCommonClientInfoMation.mDataNumber);
						} else {
							return -1;
						}
					}
					status = 0;
					if (mCommonClientInfoMation.mDataNumber == mCommonClientInfoMation.mReceiveTotalNumber - 100) {
						LogUtils.LOGD(TAG, "dealData one packet data over");
						status = 1;
						mDealOver = true;
					} else if (mCommonClientInfoMation.mDataNumber < mCommonClientInfoMation.mReceiveTotalNumber - 100) {
						LogUtils.LOGD(TAG, "dealData one packet data over and receive extra data");
						status = 2;
						mDealOver = true;
					}
				}
			}
			return status;
		}
		
		public CommonClientInfoMation getClientInfomation() {
			return mCommonClientInfoMation;
		}
		
		public boolean hasDealtOver() {
			return mDealOver;
		}
	}
	
	public class CommonClientInfoMation {
		private String mPacketHead = null;
		private String mHead = null;
		private String mTail = null;
		private String mName = null;
		private String mId = null;
		private String mCommand = null;
		private String mData = null;
		private int mDataNumber = 0;
		private int mReceiveTotalNumber = 0;
		private byte[] mBuffer = new byte[1024 * 1024];
		
		public CommonClientInfoMation() {
			// TODO Auto-generated constructor stub
		}
		
		public String getPacketHead() {
			return mPacketHead;
		}
		
		public String getHead() {
			return mHead;
		}
		
		public String getName() {
			return mName;
		}
		
		public String getId() {
			return mId;
		}
		
		public String getCommand() {
			return mCommand;
		}
		
		public String getData() {
			return mData;
		}
		
		public int getDataNumber() {
			return mDataNumber;
		}
		
		public byte[] getDtaBuffer() {
			byte[] result = null;
			if (mReceiveTotalNumber - 100 > 0) {
				result = new byte[mReceiveTotalNumber - 100];
				System.arraycopy(mBuffer, 0, result, 0, mReceiveTotalNumber - 100);
			}
			return result;
		}
		
		public byte[] getBuffer() {
			return mBuffer;
		}
		
		public int getReceiveTotalNumber() {
			return mReceiveTotalNumber;
		}
	}
}