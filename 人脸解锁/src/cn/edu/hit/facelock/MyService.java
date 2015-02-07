
package cn.edu.hit.facelock;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.content.SharedPreferences;

@SuppressWarnings("deprecation")
public class MyService extends Service {
	private String TAG = "ScreenReceiver Log";
	private KeyguardManager keyguardManager = null;
	private KeyguardManager.KeyguardLock keyguardLock = null;
	Intent toMainIntent;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		
		toMainIntent = new Intent(MyService.this, MatchFace.class);
		toMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		
		IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
		intentFilter.addAction("android.intent.action.SCREEN_ON");
		registerReceiver(screenReceiver, intentFilter);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(screenReceiver);
		
		startActivity(new Intent(MyService.this,MyService.class));
	}
	
	private BroadcastReceiver screenReceiver = new BroadcastReceiver() {

		@SuppressWarnings("static-access")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.e(TAG, "intent.action = " + action);

			if (action.equals("android.intent.action.SCREEN_OFF")) {

				
				keyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
				keyguardLock = keyguardManager.newKeyguardLock("");
				keyguardLock.disableKeyguard();
				Log.e("", "closed the keyGuard");
				
				SharedPreferences PatternUnlockKey = getSharedPreferences("PatternUnlockKey", 0);
				String key = PatternUnlockKey.getString("key", "");
				
				if(key != ""){
				    startActivity(toMainIntent);
				}
			}

		}
	};

}
