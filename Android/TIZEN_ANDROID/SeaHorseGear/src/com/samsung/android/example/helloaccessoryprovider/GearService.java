package com.samsung.android.example.helloaccessoryprovider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

/***
 * 기어2와 연결을 위한 서비스.
 * 
 * @author KKS
 * 
 */
public class GearService extends SAAgent {
	private static GearSocket socket;
	private IBinder mBinder = new LocalBinder();
	private static String defaultWord, defaultMean;

	public class LocalBinder extends Binder {
		public GearService getService() {
			return GearService.this;
		}
	}

	/**
	 * Samsung Accessory 서비스를 수행할 SAAgent이다.
	 * 
	 * @param socket
	 *            -서비스를 쓰면서 통신에 사용할 SASocket의 class 파일을 받는다.
	 */
	public GearService() {
		super("GearService", /* SASocket class 넣어야 함. */GearSocket.class);
	}

	/**
	 * 통신에 사용할 소켓을 받아온다.
	 * 
	 * @return - 통신에 사용하는 소켓으로 SASocket이다.
	 */
	public static GearSocket getSocket() {
		return socket;
	}

	/***
	 * 통신에 사용할 소켓을 받음.
	 * 
	 * @param socket
	 *            -통신 값을 주고받을 소켓으로 SASocket를 받는다.
	 */
	public void setSocket(GearSocket socket) {
		GearService.socket = socket;
	}

	/***
	 * Gear2 app과 연결된 순간에 Gear2 app에 보여줄 기본 시작 단어를 보냄.
	 * 
	 * @param word
	 *            -기본 단어
	 * @param mean
	 *            -기본 단어의 뜻
	 */
	public static void setDefaultWords(String word, String mean) {
		defaultWord = word;
		defaultMean = mean;
	}

	public static String getDefaultWord() {
		return defaultWord;
	}

	public static String getDefaultMean() {
		return defaultMean;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SA mAccessory = new SA();
		try {
			mAccessory.initialize(this);
		} catch (SsdkUnsupportedException e) {
			// Samsung sdk 지원 안함.
		} catch (Exception e1) {
			// 초기화 실패. 서비스 끝냄.
			stopSelf();
		}
	}

	// 이게 뭐 하는건지는 잘 모르겠음. 필수 정의 사항은 아닌데 샘플소스엔 있음.
	@Override
	protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
		acceptServiceConnectionRequest(peerAgent);
		Log.d("Test",
				"onServiceConnectionRequested peerAgent : "
						+ peerAgent.getAppName());
	}

	@Override
	protected void onFindPeerAgentResponse(SAPeerAgent peerAgent, int arg1) {
		// 앱에서 연결될 위젯을 찾고 난 뒤에 콜백 되는 메서드.
		Log.d("Test",
				"onServiceConnectionRequested peerAgent : "
						+ peerAgent.getAppName());
	}

	@Override
	protected void onServiceConnectionResponse(SASocket thisConnection,
			int result) {
		switch (result) {
		case CONNECTION_SUCCESS:
			if (thisConnection != null) {
				socket = (GearSocket) thisConnection;
				if (defaultWord != null && defaultMean != null) {
					socket.sendMessage(defaultWord + "//" + defaultMean);
				}
				Toast.makeText(getBaseContext(), "Connect success.",
						Toast.LENGTH_LONG).show();
			} else {
				// SASocket object is null
				Toast.makeText(getBaseContext(), "Connect fail.",
						Toast.LENGTH_LONG).show();
			}
			break;

		case CONNECTION_ALREADY_EXIST:
			break;

		case CONNECTION_FAILURE_PEERAGENT_REJECTED:
			Dialog dialog = new Dialog(getApplicationContext());
			dialog.setTitle("REJECTED");
			dialog.show();
			break;

		default:
			// unknown err
			socket.disconnected(GearSocket.ERROR_CONNECTION_CLOSED);
			socket = null;
			break;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

}
