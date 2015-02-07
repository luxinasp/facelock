package cn.edu.hit.facelock;

import org.opencv.contrib.FaceRecognizer;
import org.opencv.core.Core;

public class EigenFaceRecognizer extends FaceRecognizer {
	
	public double value = 999;

    static{ System.loadLibrary("facelock"); }

    private static native long createEigenFaceRecognizer_0();
    private static native long createEigenFaceRecognizer_1(int num_components);
    private static native long createEigenFaceRecognizer_2(int num_components, double threshold);

    public EigenFaceRecognizer () {
        super(createEigenFaceRecognizer_0());
    }
    public EigenFaceRecognizer (int num_components) {
        super(createEigenFaceRecognizer_1(num_components));
    }
    public EigenFaceRecognizer (int num_components, double threshold) {
        super(createEigenFaceRecognizer_2(num_components, threshold));
    }
	
	public void setValue(double i){
		this.value = i;
		return;
	}
	
	public double getValue(){
		return this.value;
	}
	
}
