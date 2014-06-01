package com.samsung.android.example.helloaccessoryprovider.service;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.samsung.android.example.helloaccessoryprovider.R;

/**
 * 기어2와 통신을 하기 위한 서비스 작성 예제 소스.
 * 
 * @author KKS
 * 
 */
public class MainActivity extends Activity {
	private EditText editText;
	private GearMsgReceiveListener listener = new GearMsgReceiveListener() {
		@Override
		public void receivedGearMsg(String msg) {
			// gear init
//			if (msg.equalsIgnoreCase("gear start")) {
//				HelloAccessoryProviderService.sendMessage("INIT//init");
//			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		editText = (EditText) findViewById(R.id.editText);

		HelloAccessoryProviderService.addReceiveGearMsgListener(listener);
	}

	public void sendMsg(View v) {
		String str = editText.getText().toString();
		try {
			HelloAccessoryProviderService.sendMessage(str);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

}
