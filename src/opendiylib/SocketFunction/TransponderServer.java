package opendiylib.SocketFunction;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import opendiylib.CommonUtils.LogUtils;

public class TransponderServer {
	private final String TAG = TransponderServer.class.getSimpleName();
	
	private int mPort = 0;
	private ReadWriteRunnable fromToRunnable;
	private ReadWriteRunnable toFromRunnable;
	
	public TransponderServer(int port) {
		
	}

	public void startReadWrite() {
		if (fromToRunnable != null) {
			fromToRunnable.startRun();
		}
		if (toFromRunnable != null) {
			toFromRunnable.startRun();
		}
    }
    
    public void stopReadWrite() {
    	if (fromToRunnable != null) {
			fromToRunnable.stopRun();
		}
		if (toFromRunnable != null) {
			toFromRunnable.stopRun();
		}
    }
	
	private class ReadWriteRunnable implements Runnable {
    	private final String TAG = ReadWriteRunnable.class.getSimpleName();
    	
    	private Socket requestSocket = null; 
        private Socket responseSocket = null;
        private boolean running = true;
        private String runnableInformation = null;
        private InputStream is = null;
        private OutputStream os = null;
        
        public ReadWriteRunnable(Socket request, Socket response, String infomation) {
            this.requestSocket = request;
            this.responseSocket = response;
            this.runnableInformation = infomation;
        }
    	
        public void startRun() {
        	running = true;
        	new Thread(this).start();
        }
        
        public void stopRun() {
        	if (running) {
        		try {
            		if (requestSocket != null) {
            			requestSocket.shutdownInput();
            		}
            	} catch (Exception e) {
    				e.printStackTrace();
    			}
        		try {
            		if (responseSocket != null) {
            			requestSocket.shutdownOutput();
            		}
            	} catch (Exception e) {
    				e.printStackTrace();
    			}
        	}
        }
        
    	@Override
        public void run() {
            byte[] buffer = new byte[1024 * 1024];   
            
            try {
                is = requestSocket.getInputStream();
                os = responseSocket.getOutputStream();
                while(running){
                    int size = is.read(buffer); 
                    boolean isRequestInputOn = !requestSocket.isInputShutdown();
                    boolean isResponseOutputOn = !responseSocket.isOutputShutdown();
                    if (size > -1 && isRequestInputOn && isResponseOutputOn) {
                        os.write(buffer, 0, size);
                    } else {
                    	break;
                    }
                }
            } catch (Exception e) {
            	e.printStackTrace();
            }
            LogUtils.LOGD(TAG, "ReadWriteRunnable " + runnableInformation + " exit");
        	try {
        		if (is != null) {
        			is.close();
        			is = null;
        		}
        	} catch (Exception e) {
				e.printStackTrace();
			}
        	try {
        		if (os != null) {
        			os.close();
        			os = null;
        		}
        	} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}
}
