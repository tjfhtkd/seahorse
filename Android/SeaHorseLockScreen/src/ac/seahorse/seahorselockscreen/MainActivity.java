package ac.seahorse.seahorselockscreen;

import ac.seahorse.seahorselockscreen.lockscreen.LockScreenService;
import ac.seahorse.seahorselockscreen.lockscreen.data.LockScreenDataManager;
import ac.seahorse.seahorselockscreen.lockscreen.view.LockScreenType1;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 이 프로젝트의 사용예제 파일.
 * 
 * @author KKS
 * 
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 잠금 화면에 관한 데이터 관리자
		LockScreenDataManager dataMng = LockScreenDataManager.getInstance(this);
		dataMng.setLockScreenEnable(true);
		dataMng.setLockScreenType(LockScreenType1.class.getName());
		// dataMng.setLockScreenType(TestView.class.getName());
		dataMng.setNotificationWord("SeaHorse");
		dataMng.setNotificationMean("해마");
		dataMng.setLockScreenWord("Paladin", "신성기사");
		dataMng.addLockScreenAnswer("야만용사");
		dataMng.addLockScreenAnswer("신성기사");
		dataMng.addLockScreenAnswer("주술사");
		dataMng.addLockScreenAnswer("마법사");
		dataMng.setAppearCycle(2);
		dataMng.commit();

		// BootService 에서도 따로 등록해주어야 함.
		startService(new Intent(MainActivity.this, LockScreenService.class));
	}

}
