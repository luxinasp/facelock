package cn.edu.hit.facelock;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.content.SharedPreferences;

import cn.edu.hit.facelock.GestureLockView2.OnGestureFinishListener;

public class SignActivity extends Activity {

    public static String[] result = {"",""};
	Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.signactivity);
		
		GestureLockView2 gv = (GestureLockView2) findViewById(R.id.gv);
		final TextView label1 = (TextView) findViewById(R.id.label1);
		final TextView label2 = (TextView) findViewById(R.id.label2);
		final TextView label3 = (TextView) findViewById(R.id.label3);
		final TextView label4 = (TextView) findViewById(R.id.label4);
		
		label1.setVisibility(View.VISIBLE); 
		label2.setVisibility(View.GONE); 
		label3.setVisibility(View.GONE); 
		label4.setVisibility(View.GONE); 

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
				
			
            	if (msg.obj=="state1") {
				 label2.setVisibility(View.VISIBLE); 
				 label1.setVisibility(View.GONE); 
				 label3.setVisibility(View.GONE); 
				 label4.setVisibility(View.GONE); 

            	}
				
            	if (msg.obj=="state2") {
            		label1.setVisibility(View.VISIBLE); 
					label2.setVisibility(View.GONE); 
					label3.setVisibility(View.GONE); 
					label4.setVisibility(View.GONE); 

            	}
				
            	if (msg.obj=="state3") {
            		label4.setVisibility(View.VISIBLE); 
					label2.setVisibility(View.GONE); 
					label3.setVisibility(View.GONE); 
					label1.setVisibility(View.GONE); 

            	}
				
				
            }
        };
		
		gv.setOnGestureFinishListener(new OnGestureFinishListener() {
			
			@Override
			public void OnGestureFinish(String success) {

				if(result[0] == "" && result[1] == "" ) {

					result[0] = success;
					
					change1();
				
					return;
				}
				
				if(result[0] != "" && result[1] == "") {

					result[1] = success;
			    
			        
				    if(!(result[0]).equals(result[1])){
					
					    change2();

						AlertDialog.Builder builder = new AlertDialog.Builder(SignActivity.this);
						builder.setTitle("提示");
						builder.setMessage("两次绘制的图案不一致！");
						builder.setPositiveButton("是",null);
						builder.show();
	
						result[0] = "";
						result[1] = "";

						return;
					}
					else{
					
					    change3();
						
						SharedPreferences PatternUnlockKey = getSharedPreferences("PatternUnlockKey", 0);

						PatternUnlockKey.edit().putString("key", result[0]).commit();
						
						AlertDialog.Builder builder = new AlertDialog.Builder(SignActivity.this);
						builder.setTitle("提示");
						builder.setMessage("注册成功！");
						builder.setPositiveButton("是",new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {

									Intent intent = new Intent();
									intent.setClass(SignActivity.this, SettingActivity.class);
									startActivity(intent);
									finish();
								
								}
							});
						
						builder.show();
						
						

											

						
						
	
						return;
						
					}
				
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
	
    public void change3() {
        Message msg = new Message();
		String textTochange = "state3";
		msg.obj = textTochange;
		mHandler.sendMessage(msg);	
	
    }
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
