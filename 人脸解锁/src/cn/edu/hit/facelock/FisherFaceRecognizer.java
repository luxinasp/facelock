package cn.edu.hit.facelock;

import org.opencv.contrib.FaceRecognizer;
import org.opencv.core.Core;

public class FisherFaceRecognizer extends FaceRecognizer {
	
	public double value = 999;

    static{ System.loadLibrary("facelock"); }

    private static native long createFisherFaceRecognizer_0();
    private static native long createFisherFaceRecognizer_1(int num_components);
    private static native long createFisherFaceRecognizer_2(int num_components, double threshold);

    public FisherFaceRecognizer () {
        super(createFisherFaceRecognizer_0());
    }
    public FisherFaceRecognizer (int num_components) {
        super(createFisherFaceRecognizer_1(num_components));
    }
    public FisherFaceRecognizer (int num_components, double threshold) {
        super(createFisherFaceRecognizer_2(num_components, threshold));
    }
	
	public void setValue(double i){
		this.value = i;
		return;
	}
	
	public double getValue(){
		return this.value;
	}
	
}
