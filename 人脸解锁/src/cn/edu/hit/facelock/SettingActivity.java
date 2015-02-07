package cn.edu.hit.facelock;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.Intent;
import java.io.File;

public class SettingActivity extends Activity {
	
	Button sign,add,del;
	Boolean check;
	Handler mHandler;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingactivity);
		
		sign=(Button)findViewById(R.id.mainButton0);
		add=(Button)findViewById(R.id.mainButton1);
		del=(Button)findViewById(R.id.mainButton2);
		
		sign.setVisibility(View.INVISIBLE);
		add.setVisibility(View.INVISIBLE);
		del.setVisibility(View.INVISIBLE);
		
		check = checkPicturePath();

		if(check) {
			add.setVisibility(View.VISIBLE);
			del.setVisibility(View.VISIBLE);
		}
		else {
			sign.setVisibility(View.VISIBLE);
		}
		
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	if (msg.obj=="HaveDeleted") {
            		add.setVisibility(View.INVISIBLE);
					del.setVisibility(View.INVISIBLE);
					sign.setVisibility(View.VISIBLE);

            	}
				
            	/*if (msg.obj=="HaveSigned") {
            		add.setVisibility(View.VISIBLE);
					del.setVisibility(View.VISIBLE);
					sign.setVisibility(View.INVISIBLE);

            	}*/
            }
        };
		
		
        del.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				delOnclick();
				Message msg = new Message();
				String textTochange = "HaveDeleted";
				msg.obj = textTochange;
				mHandler.sendMessage(msg);			
			}
		});
		
        add.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				 
				 /*Message msg = new Message();
				 String textTochange = "HaveSigned";
				 msg.obj = textTochange;
				 mHandler.sendMessage(msg);	*/
				 
                 Intent intent = new Intent();
				 intent.setClass(SettingActivity.this, AddFaceActivity.class);
				 startActivity(intent);

				 
			}
		});
		
        sign.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
                 Intent intent = new Intent();
				 intent.setClass(SettingActivity.this, AddFaceActivity.class);
				 startActivity(intent);
			}
		});
		
		
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		check = checkPicturePath();

		if(check) {
			add.setVisibility(View.VISIBLE);
			del.setVisibility(View.VISIBLE);
		}
		else {
			sign.setVisibility(View.VISIBLE);
		}
		
	}
	
	
    public void delOnclick() {
	    File sdDir = null;
		String path;
		sdDir = Environment.getExternalStorageDirectory();
		path = sdDir.toString() + "/facerecog/faces";
		File file = new File(path);
		
		if(file.exists()) {
		    delete(file);
		}
	
    }
	
    public void delete(File file) {
	
	    if(file.isFile()) {
		    file.delete();
			return;
		}
		
		if(file.isDirectory()) {
		    File[] childFiles = file.listFiles();
			
			if(childFiles == null || childFiles.length == 0) {
			    file.delete();
				return;
			}
			
			for(int i = 0;i<childFiles.length;i++) {
			    delete(childFiles[i]);
				
			}
			
			file.delete();
			
			
			
		}
	

    }
		

	public boolean checkPicturePath() {
		
		File sdDir = null;
		String path;
		
		sdDir = Environment.getExternalStorageDirectory();
		path = sdDir.toString() + "/facerecog/faces/host-0.jpg";
		
		//Toast.makeText(MainActivity.this,path,Toast.LENGTH_SHORT).show();
		
		File file = new File(path);
		
		if(!file.exists()) {
			return false;
		}
		
		return true;
		
		
		
	}
}
