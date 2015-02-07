package cn.edu.hit.facelock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import cn.edu.hit.facelock.R;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.text.*;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.Intent;
import android.gesture.*;

public class MatchFace extends Activity implements CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;
   

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;

    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;
    private double mLikely=999;

    private CameraBridgeViewBase   mOpenCvCameraView;
    
    Handler mHandler;
	Button usePattern;
  
    FisherFaceRecognizer pr;
    
    
    ImageView ivGreen,ivYellow,ivRed; 
    
    
    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

 
                    //pr = new LBPHFaceRecognizer();
					pr = new FisherFaceRecognizer();
                    pr.load("/sdcard/facelock.yml");
                    
                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

       //                 mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }
					
					mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);

                    mOpenCvCameraView.enableView();
					//mOpenCvCameraView.setCamFront();
              
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
                
                
            }
        }
    };

    public MatchFace() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.matchface);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial3_activity_java_surface_view);
		
		usePattern=(Button)findViewById(R.id.matchfaceButton1);
   
        mOpenCvCameraView.setCvCameraViewListener(this);
      
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	if (msg.obj=="catch")
            	{
            		
            		 ivGreen.setVisibility(View.INVISIBLE);
            	     ivYellow.setVisibility(View.INVISIBLE);
            	     ivRed.setVisibility(View.INVISIBLE);
            	     
					 mLikely = pr.getValue();
					 
            	     if (mLikely<0);
            	     else if(mLikely<50){
            			ivGreen.setVisibility(View.VISIBLE);
						pr.setValue(999);
						//finish();
						}
            	   	else if (mLikely<60){
            			ivYellow.setVisibility(View.VISIBLE);    
						pr.setValue(999);
						//finish();
						}
            		else 
            			ivRed.setVisibility(View.VISIBLE);
						
            	}
            }
        };
        
 
        ivGreen=(ImageView)findViewById(R.id.imageView3);
        ivYellow=(ImageView)findViewById(R.id.imageView4);
        ivRed=(ImageView)findViewById(R.id.imageView2);
        
        
        ivGreen.setVisibility(View.INVISIBLE);
        ivYellow.setVisibility(View.INVISIBLE);
        ivRed.setVisibility(View.INVISIBLE);
		
		
		
        usePattern.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
                 Intent intent = new Intent();
				 intent.setClass(MatchFace.this, PatternUnlock.class);
				 startActivity(intent);
				 finish();
			}
		});
    

    }

	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    switch (keyCode) {
	    case KeyEvent.KEYCODE_BACK: 
			finish();
	        return true; 
	    }
	    return super.onKeyDown(keyCode, event); 
	}
    
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();       
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
		if(OpenCVLoader.initDebug()){
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		}
		
       
      	
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
		
		int rows_start = (int)(0.167*mGray.rows());
		int rows_end = (int)(0.833*mGray.rows());
		int rows_lenght = rows_end - rows_start;
		int cols_start = (int)(0.5*(mGray.cols() - rows_lenght));
		int cols_end = (int)(0.5*(mGray.cols() + rows_lenght));
		int cols_lenght = rows_lenght;
		
		int S = (int)(rows_lenght*cols_lenght*0.7);
		
		//int rows_start = (int)0.167*mGray.rows();
		//int rows_end = (int)0.833*mGray.rows();
		//int rows_lenght = rows_end - rows_start;
		//int cols_start = (int)0.5*(mGray.cols() - rows_lenght);
		//int cols_end = (int)0.5*(mGray.cols() + rows_lenght);
		//int cols_lenght = rows_lenght;

		//int S = 184320/2;

		//Range rows = new Range(120,600);
		//Range cols = new Range(400,880);

		//Point tl = new Point(400,120);
		//Point br = new Point(880,600);
		
		Range rows = new Range(rows_start,rows_end);
		Range cols = new Range(cols_start,cols_end);
		
		Point tl = new Point(cols_start,rows_start);
		Point br = new Point(cols_end,rows_end);


		Mat new_mGray = new Mat(mGray,rows,cols);

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
          //  mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(new_mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }


        Rect[] facesArray = faces.toArray();
        
        
        if ((facesArray.length>0))
          {
			  //Point x = new Point(facesArray[0].tl().getx()+400,facesArray[0].tl().gety()+120);
			  //Point y = new Point(facesArray[0].br().getx()+400,facesArray[0].br().gety()+120);
			  
			  Point x = new Point(facesArray[0].tl().getx()+cols_start,facesArray[0].tl().gety()+rows_start);
			  Point y = new Point(facesArray[0].br().getx()+cols_start,facesArray[0].br().gety()+rows_start);

			  Core.rectangle(mRgba,x , y, FACE_RECT_COLOR, 3);
			  
			  
        	  Mat m=new Mat();
        	  m=new_mGray.submat(facesArray[0]);
			  Mat n_resize = new Mat(128,128,m.type());
			  
			  int label[] = new int[1];
			  label[0] = -1;
			  
			  double confidence[] = new double[1];
			  confidence[0] = 999;
			  
			  pr.predict(n_resize,label,confidence);
			  
			  
			  try{
				  File file1 = new File(Environment.getExternalStorageDirectory(),"data.txt");
				  BufferedWriter bw1 = new BufferedWriter(new FileWriter(file1, true));
				  //String info1 = "0000000000";
				  bw1.write(confidence[0]+"|");
				  //bw1.write("/");
				  //bw1.write("|"+mGray.cols());
				  //bw1.write("/");
				  bw1.flush();
			  }
			  catch (Exception e) {
				  e.printStackTrace();
			  }
			  
			  if (label[0] != -1){
				  
			  

		          pr.setValue(confidence[0]);
			  
			  
                  Message msg = new Message();
                  String textTochange = "catch";
                  msg.obj = textTochange;
                  mHandler.sendMessage(msg);
			  }
        	  
          }
        
		Core.rectangle(mRgba, tl, br, FACE_RECT_COLOR, 3);
        return mRgba;
    }


    }
