package com.example.hellocv;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends Activity implements CvCameraViewListener2 {
	private static final String TAG = "HelloCV::HelloOpenCvActivity";
	
	//private CameraBridgeViewBase mOpenCvCameraView;
	static {
	    if (!OpenCVLoader.initDebug()) {
	        // Handle initialization error
	    }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG,"called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.layout_main);
		ImageView iv = (ImageView)findViewById(R.id.image);
		iv.setImageBitmap(peopleDetect("http://habrastorage.org/getpro/habr/post_images/829/8c9/963/8298c9963eed721dabb0548dba577d1b.jpg"));
//		mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.HelloOpenCvView);
//		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
//		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hello_open_cv, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
//				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};
	
	@Override
	public void onResume()
	{
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		if(mOpenCvCameraView != null)
//			mOpenCvCameraView.disableView();
	}
	
	public void onDestory() {
		super.onDestroy();
//		if(mOpenCvCameraView != null)
//			mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		return inputFrame.rgba();
	}
	 public Bitmap peopleDetect (String path) {
	        Bitmap bitmap = null;
	        float execTime;
//	        try {
	            //we Download a photo
	        	/*
	            URL url = new URL (path);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection ();
	            connection.setDoInput (true);
	            connection.connect ();
	            InputStream input = connection.getInputStream ();
	            BitmapFactory. Options opts = new BitmapFactory. Options ();
	            opts.inPreferredConfig = Bitmap. Config. ARGB_8888;
	            bitmap = BitmapFactory.decodeStream (input, null, opts); */
	            long time = System.currentTimeMillis ();
	            //we Create a matrix of the image for OpenCV and it is placed in it our photo
	            Mat mat = new Mat ();
	            bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.dong);
	            Utils.bitmapToMat (bitmap, mat);
	            //Perekonvertiruem a matrix with RGB on graduation of the gray
	            Imgproc.cvtColor (mat, mat, Imgproc. COLOR_RGB2GRAY, 4);
	            HOGDescriptor hog = new HOGDescriptor ();
	            //the standard determinant of people Is gained and installed to its our descriptor
	            MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector ();
	            hog.setSVMDetector (descriptors);
	            //It is defined variables in which search results (locations - the right-angled areas will be placed, weights - weight (it is possible to tell relevance) an appropriate location)
	            MatOfRect locations = new MatOfRect ();
	            MatOfDouble weights = new MatOfDouble ();
	            //As a matter of fact, the analysis of photos. Results register in locations and weights
	            hog.detectMultiScale (mat, locations, weights);
	            execTime = ((float) (System.currentTimeMillis () - time)) / 1000f;
	            //Variables for selection of areas in a photo
	            Point rectPoint1 = new Point ();
	            Point rectPoint2 = new Point ();
	            Scalar fontColor = new Scalar (0, 0, 0);
	            Point fontPoint = new Point ();
	            //If there is a result - is added on a photo of area and weight of each of them
	            if (locations.rows () > 0) {
	                List<Rect> rectangles = locations.toList ();
	                int i = 0;
	                List<Double> weightList = weights.toList ();
	                for (Rect rect: rectangles) {
	                    float weigh = weightList.get (i ++).floatValue ();

	                    rectPoint1.x = rect.x;
	                    rectPoint1.y = rect.y;
	                    fontPoint.x = rect.x;
	                    fontPoint.y = rect.y - 4;
	                    rectPoint2.x = rect.x + rect.width;
	                    rectPoint2.y = rect.y + rect.height;
	                    final Scalar rectColor = new Scalar (0, 0, 0);
	                    //It is added on images the found information
	                    Core.rectangle (mat, rectPoint1, rectPoint2, rectColor, 2);
	                    Core.putText (mat,
	                            String.format ("%1.2f", weigh),
	                            fontPoint, Core. FONT_HERSHEY_PLAIN, 1.5, fontColor,
	                            2, Core. LINE_AA, false);

	                }
	            }
	            fontPoint.x = 15;
	            fontPoint.y = bitmap.getHeight () - 20;
	            //It is added the additional debug information
	            Core.putText (mat,
	                    "Processing time:" + execTime + "width:" + bitmap.getWidth () + "height:" + bitmap.getHeight (),
	                    fontPoint, Core. FONT_HERSHEY_PLAIN, 1.5, fontColor,
	                    2, Core. LINE_AA, false);
	            Utils.matToBitmap (mat, bitmap);
//	        } catch (IOException e) {
//	            e.printStackTrace ();
//	        }
	        return bitmap;
	    }
}
