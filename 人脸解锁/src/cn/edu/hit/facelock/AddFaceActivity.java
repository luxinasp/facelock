package cn.edu.hit.facelock;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import android.os.Environment;

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
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;


import cn.edu.hit.facelock.R;


import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
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
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import org.opencv.android.*;






public class AddFaceActivity extends Activity implements CvCameraViewListener2 {

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
    
    
    //String mPath="";

    private CameraBridgeViewBase   mOpenCvCameraView;
    
    Handler mHandler;
	
	FisherFaceRecognizer pr;
	
	List<Mat> src = new ArrayList<Mat>();  
	int count=0;

    
   
    
    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                 //   System.loadLibrary("detection_based_tracker");
           
					
					//pr = new LBPHFaceRecognizer();
					pr = new FisherFaceRecognizer();
                    
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
              
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
                
                
            }
        }
    };

    public AddFaceActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.addfaceactivity);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial3_activity_java_surface_view);
   
        mOpenCvCameraView.setCvCameraViewListener(this);
      
		
		
		//mPath=Environment.getExternalStorageDirectory()+"/facerecog/faces/";
     
           
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	if (msg.obj=="IMG")
            	{
            	 
            	}
            	else
            	{
            	}
            }
        };
		
        
        /*
        boolean success=(new File(mPath)).mkdirs();
        if (!success)
        {
        	Log.e("Error","Error creating directory");
        }
		*/
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
		
		
		
		
		int rows_start = (int)0.167*mGray.rows();
		int rows_end = (int)0.833*mGray.rows();
		int rows_lenght = rows_end - rows_start;
		int cols_start = (int)0.5*(mGray.cols() - rows_lenght);
		int cols_end = (int)0.5*(mGray.cols() + rows_lenght);
		int cols_lenght = rows_lenght;

		int S = 184320/2;

		Range rows = new Range(120,600);
		Range cols = new Range(400,880);

		Point tl = new Point(400,120);
		Point br = new Point(880,600);


		Mat new_mGray = new Mat(mGray,rows,cols);
		
		

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }

        }

        MatOfRect faces = new MatOfRect();
		

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(new_mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
       
        }


        Rect[] facesArray = faces.toArray();
		
		
		if (facesArray.length==1){
			
			
		
			if (facesArray[0].area() > S){
			

				
				Point x = new Point(facesArray[0].tl().getx()+400,facesArray[0].tl().gety()+120);
				Point y = new Point(facesArray[0].br().getx()+400,facesArray[0].br().gety()+120);
			
				Core.rectangle(mRgba,x , y, FACE_RECT_COLOR, 3);
			
				Mat n=new Mat();
				Rect r=facesArray[0];
				n=new_mGray.submat(r);
				Mat n_resize = new Mat(128,128,n.type());
				
				Imgproc.resize(n,n_resize,n_resize.size(),0,0,Imgproc.INTER_LINEAR);
				
		
				Bitmap bmp= Bitmap.createBitmap(n_resize.width(), n_resize.height(), Bitmap.Config.ARGB_8888);
				 
				Utils.matToBitmap(n_resize,bmp);
				bmp= Bitmap.createScaledBitmap(bmp, 128, 128, false);
				
				/*
				FileOutputStream f;
				try {
					f = new FileOutputStream(mPath+"luxin"+"-"+count+".jpg",true);
					count++;
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, f);
					f.close();

				} catch (Exception e) {
					Log.e("error",e.getCause()+" "+e.getMessage());
					e.printStackTrace();
				}
				*/
				
				if (src.size() < 100){
				
					src.add(n_resize);
				
				}
				
				else{
				
					count = 0;
					
					Mat labels = new Mat(100, 1, CvType.CV_32SC1);
					
					int i;
					
					for (i=0;i<50;i++){
						labels.put(i, 0, 1);
						//labels.put(i+25, 0, 2);
					}
					
					for (i=50;i<100;i++){
						labels.put(i, 0, 2);
						
					}
					
	
					pr.train(src,labels);
					pr.save("/sdcard/facelock.yml");
					
					finish();
				
				
				}
			
			}
		
		}
	
		Core.rectangle(mRgba, tl, br, FACE_RECT_COLOR, 3);
		
		return mRgba;
		
		
		
    }


    }
