package cn.edu.hit.facelock;

import java.util.Timer;
import java.util.TimerTask;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.Intent;
import android.content.SharedPreferences;

import cn.edu.hit.facelock.GestureLockView.OnGestureFinishListener;

public class PatternUnlock extends Activity
{
    Handler mHandler;
	Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patternunlock);

		
		GestureLockView gv = (GestureLockView) findViewById(R.id.gv);	
		final TextView label1 = (TextView) findViewById(R.id.plabel1);
		final TextView label2 = (TextView) findViewById(R.id.plabel2);
		
		gv.setVisibility(View.VISIBLE);		
		label1.setVisibility(View.VISIBLE); 
		label2.setVisibility(View.GONE); 
		
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
				
			
            	if (msg.obj=="state1") {
				 label2.setVisibility(View.VISIBLE); 
				 label1.setVisibility(View.GONE); 

            	}
				
            	if (msg.obj=="state2") {
            		label1.setVisibility(View.VISIBLE); 
					label2.setVisibility(View.GONE); 
					
            	}
				
			
            }
        };
		

		
		
		SharedPreferences PatternUnlockKey = getSharedPreferences("PatternUnlockKey", 0);
		String key = PatternUnlockKey.getString("key", "");

		
		gv.setKey(key);

		gv.setOnGestureFinishListener(new OnGestureFinishListener() {
			@Override
			public void OnGestureFinish(boolean success) {
			    
				if(success){
				
				    finish();
				}
				else{
				    change1();
					
					timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
						    change2();

						}
					}, 1000);

				}
				
			}
		});
	}
	
    public void change1() {
        Message msg = new Message();
		String textTochange = "state1";
		msg.obj = textTochange;
		mHandler.sendMessage(msg);	
		
	
    }
	
    public void change2() {
        Message msg = new Message();
		String textTochange = "state2";
		msg.obj = textTochange;
		mHandler.sendMessage(msg);	
		
	
    }
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    switch (keyCode) {
	    case KeyEvent.KEYCODE_BACK: 
			finish();
	        return true; 
	    }
	    return super.onKeyDown(keyCode, event); 
	}

	
	
	
}
