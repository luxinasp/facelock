package cn.edu.hit.facelock;

import org.opencv.contrib.FaceRecognizer;
import org.opencv.core.Core;

public class LBPHFaceRecognizer extends FaceRecognizer {
	
	public double value = 999;

    static{ System.loadLibrary("facelock"); }

    private static native long createLBPHFaceRecognizer_0();
    private static native long createLBPHFaceRecognizer_1(int radius, int neighbors,int grid_x, int grid_y, double threshold);

    public LBPHFaceRecognizer () {
        super(createLBPHFaceRecognizer_0());
    }

    public LBPHFaceRecognizer (int radius, int neighbors,int grid_x, int grid_y, double threshold) {
        super(createLBPHFaceRecognizer_1(radius, neighbors,grid_x, grid_y, threshold));
    }
	
	public void setValue(double i){
		this.value = i;
		return;
	}
	
	public double getValue(){
		return this.value;
	}
	
}
