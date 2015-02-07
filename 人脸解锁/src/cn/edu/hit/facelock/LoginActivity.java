package cn.edu.hit.facelock;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.Intent;
import android.content.SharedPreferences;

import cn.edu.hit.facelock.GestureLockView.OnGestureFinishListener;

public class LoginActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginactivity);
		
		startService(new Intent(LoginActivity.this, MyService.class));
		
		GestureLockView gv = (GestureLockView) findViewById(R.id.gv);
		gv.setVisibility(View.INVISIBLE);
		
		
		SharedPreferences PatternUnlockKey = getSharedPreferences("PatternUnlockKey", 0);
		String key = PatternUnlockKey.getString("key", "");
		
		if(key == ""){
		    Intent intent = new Intent();
			intent.setClass(LoginActivity.this, SignActivity.class);
			startActivity(intent);
			finish();

		}
		else{
		    gv.setVisibility(View.VISIBLE);
		}
		
		
		gv.setKey(key);

		gv.setOnGestureFinishListener(new OnGestureFinishListener() {
			@Override
			public void OnGestureFinish(boolean success) {
			    
				if(success){
				    Intent intent = new Intent();
				    intent.setClass(LoginActivity.this, SettingActivity.class);
				    startActivity(intent);
				    finish();
				}
				else{
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
					builder.setTitle("提示");
					builder.setMessage("验证身份失败！");
					builder.setPositiveButton("重试",null);
					builder.show();
				}
				
			}
		});
	}

	
	
	
}
