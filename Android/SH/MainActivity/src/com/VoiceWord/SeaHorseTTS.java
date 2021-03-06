package com.VoiceWord;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

/***
 * 출처 : http://blog.daum.net/mailss/26
 */
@SuppressLint("NewApi")
public class SeaHorseTTS implements OnInitListener {
	public static final int TTS_INIT_FAIL = 0;
	public static final int TTS_NOT_SUPPORT = 1;
	public static final int TTS_EMPTY_WORD = 2;
	public static final int TTS_SPEECH_SUCCESS = 3;
	public static final int TTS_SPEECH_ERROR = 4;
	private static final Locale language = Locale.US;

	private TextToSpeech mTTS;
	private boolean isInit;
	private boolean isSupport;
	private float pitchRatio;
	private float speechRatio;
	Context context;
	/**
	 * TTS engine은 반드시 onCreate 에서 생성되어야 한다.
	 */
	public SeaHorseTTS(Context context) {
		mTTS = new TextToSpeech(context, this); // tts 객체 생성
		this.context = context;
	}

	/***
	 * 음성 음역대 조절.
	 * 
	 * @param pitchRatio
	 *            - 0 ~ 20 낮을 수록 저음역대.
	 */
	public void setPitch(float pitchRatio) {
		if (pitchRatio < 0) {
			this.pitchRatio = 0;
		} else if (pitchRatio > 20) {
			this.pitchRatio = 20;
		} else {
			this.pitchRatio = pitchRatio;
		}
	}

	/***
	 * 말하는 속도를 조절.
	 * 
	 * @param speechRatio
	 *            -속도 조절량. 0 ~ 20
	 */
	public void setSpeechRatio(float speechRatio) {
		if (speechRatio < 0) {
			this.speechRatio = 0;
		} else if (speechRatio > 20) {
			this.speechRatio = 20;
		} else {
			this.speechRatio = speechRatio;
		}
	}

	/***
	 * 음성 출력 메서드. 이 메서드는 반드시 onCreate가 아닌 이벤트에 넣어줘야 한다. 언어 지원 체크가 검사를 하지 못하는데
	 * Activity 생명주기에 종속적인 것으로 보인다.
	 * 
	 * @param word
	 *            -음성으로 변환할 문자열.
	 * @return TTS_INIT_FAIL = 초기화 실패, TTS_NOT_SUPPORT = 미지원 언어, TTS_EMPTY_WORD
	 *         = 빈 텍스트, TTS_SPEECH_ERROR = 음성 출력 에러, TTS_SPEECH_SUCCESS =음성 출력
	 *         성공. { SeaHorseTTs.TTS_INIT_FAIL .... }
	 */
	public int speech(String word) {
		int state;

		// 해당 언어가 지원하는 언어인지 검사
		int available = mTTS.isLanguageAvailable(language);
		if (available < 0) {
			// 지원하지 않으면 메시지 출력
			isSupport = false;
		} else {
			// 지원하는 언어이면 플래그 변경
			isSupport = true;
		}

		if (!isInit) {
			// 초기화 실패
			state = TTS_INIT_FAIL;
		} else if (!isSupport) {
			// 지원하지 않는 언어
			state = TTS_NOT_SUPPORT;
		} else {
			if (TextUtils.isEmpty(word)) {
				// 빈 텍스트면 메시지 출력
				state = TTS_EMPTY_WORD;
			} else {
				mTTS.setLanguage(language); // 언어 설정.
				mTTS.setPitch(pitchRatio / 10.0f); // pitch 설정.
				mTTS.setSpeechRate(speechRatio / 10.0f); // rate 설정.
				if (mTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null) == TextToSpeech.ERROR) {
					// 음성 출력 실패
					state = TTS_SPEECH_ERROR;
				} else {
					// 음성 출력 성공
					state = TTS_SPEECH_SUCCESS;
				}
			}
		}
		return state;
	}

	public void close() {
		if (mTTS != null) { // TextToSpeech 객체가 생성되었으면
			mTTS.stop(); // 음성 출력 중지
			mTTS.shutdown(); // TextToSpeech 종료
		}
	}
	public void speechWord(String word) {
		//this.setPitch(10);
		//this.setSpeechRatio(10);

		String msg = null;
		switch (this.speech(word)) {
		case SeaHorseTTS.TTS_INIT_FAIL:
			msg = "TTS_INIT_FAIL";
			break;

		case SeaHorseTTS.TTS_NOT_SUPPORT:
			msg = "TTS_NOT_SUPPORT";
			break;

		case SeaHorseTTS.TTS_EMPTY_WORD:
			msg = "TTS_EMPTY_WORD";
			break;

		case SeaHorseTTS.TTS_SPEECH_SUCCESS:
			msg = "TTS_SPEECH_SUCCESS";
			break;

		case SeaHorseTTS.TTS_SPEECH_ERROR:
			msg = "TTS_SPEECH_ERROR";
			break;

		default:
			break;
		}
		if (msg != null) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	public void onInit(int status) {
		// 성공여부 저장
		if (status == TextToSpeech.SUCCESS) {
			isInit = true;
		} else {
			isInit = false;
		}
	}

}
