package com.example.hellocv;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
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
import org.opencv.objdetect.HOGDescriptor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements CvCameraViewListener2, OnTouchListener {
	private static final String TAG = "HelloCV::HelloOpenCvActivity";
	private ImageView iv;
	private TextView textView_check;
	private ProgressBar bar;
    private Mat                    mRgba;
    private Mat                    mGray, mDescriptor=null;
    private boolean isSet=false;
    private static final String ACTION_KEY_TYPE = "ActionKeyType";
    private static final String ACTION_KEY_VALUE = "ActionKeyValue";
    private ArrayList<Boolean> maintainance = new ArrayList<Boolean>();
    	
    private static final int ACTION_TYPE_SETTEXT = 0;
    private static final int ACTION_TYPE_SETSCROLL = 1;
	
	private CameraBridgeViewBase mOpenCvCameraView;
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
		iv = (ImageView)findViewById(R.id.modifiedImage);
		textView_check = (TextView)findViewById(R.id.detectionCheck);
		bar = (ProgressBar)findViewById(R.id.progressbar);
		mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.HelloOpenCvView);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.setOnTouchListener(this);
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
				System.loadLibrary("hog_detecting");
				mOpenCvCameraView.enableView();
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
		if(mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}
	
	public void onDestory() {
		super.onDestroy();
		if(mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}
	private boolean isMaintained(int result){
		int count=0;
		String str="";
		
		if(result > 0)
			maintainance.add(true);
		else maintainance.add(false);
		if(maintainance.size()>=12) {
			maintainance.remove(0);
		}
		for(int i=0 ;i<maintainance.size();i++) {
			if(maintainance.get(i)) {
				count++;
				str+='O';
			}else str+='X';
		}
        sendActionMsg(ACTION_TYPE_SETTEXT, str);
        sendActionMsg(ACTION_TYPE_SETSCROLL,count);
		if(count >= 10) {
			maintainance.removeAll(maintainance);
			return true;
		}
		else return false;
	}
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		if(!isSet) {
			SetDescriptor();
			isSet=true;
		}
        mGray = inputFrame.gray();
        mRgba = inputFrame.rgba();
        int detectResult = FindPeople(mGray.getNativeObjAddr());
        //sendActionMsg(ACTION_TYPE_SETSCROLL, detectResult);
        if(isMaintained(detectResult)) {
			Intent intentVideoRecordActivity = 
					new Intent(MainActivity.this, VideoRecordActivity.class);
			startActivity(intentVideoRecordActivity);        	
        }
        
        /*
        int distance;
        if(mDescriptor != null) {
            Scalar fontColor = new Scalar (0, 0, 0);
            Point fontPoint = new Point (100,100);

        	distance = FindFeature(mGray.getNativeObjAddr(),mDescriptor.getNativeObjAddr());
            Core.putText (mRgba,
                    "Distance:" + distance,
                    fontPoint, Core. FONT_HERSHEY_PLAIN, 1.5, fontColor,
                    2, Core. LINE_AA, false);
        }
        */
		return mRgba;
	}
	private void sendActionMsg(int action, int value)
	{
		Message msg = mActionHandler.obtainMessage();
				
		Bundle bundle = new Bundle();
		bundle.putInt(ACTION_KEY_TYPE, action);
		bundle.putInt(ACTION_KEY_VALUE, value);
				
		msg.setData(bundle);
		mActionHandler.sendMessage(msg);
	}

	private void sendActionMsg(int action, String value)
	{
		Message msg = mActionHandler.obtainMessage();
				
		Bundle bundle = new Bundle();
		bundle.putInt(ACTION_KEY_TYPE, action);
		bundle.putString(ACTION_KEY_VALUE, value);
				
		msg.setData(bundle);
		mActionHandler.sendMessage(msg);
	}
	public Handler mActionHandler = new Handler()
	{
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
					
			switch(data.getInt(ACTION_KEY_TYPE)) {
			case ACTION_TYPE_SETTEXT:
				String str = data.getString(ACTION_KEY_VALUE);
		        textView_check.setText(str);
			break;    
						
			case ACTION_TYPE_SETSCROLL:
				int intvalue = data.getInt(ACTION_KEY_VALUE);
		        bar.setProgress(intvalue*10);
		  
			break;
		}
	}
		};
	
    public Bitmap peopleDetect() {
        Bitmap bitmap = Bitmap.createBitmap(mGray.cols(), mGray.rows(),Bitmap.Config.ARGB_8888);
        float execTime;
        Mat mat = mGray;
        long time = System.currentTimeMillis ();
        HOGDescriptor hog = new HOGDescriptor ();
        MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector ();
        hog.setSVMDetector (descriptors);MatOfRect locations = new MatOfRect ();
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
        Log.v("howmany",""+locations.rows());
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
    	return bitmap;
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.v("OnTouch","OK"+iv.getVisibility());
		return false;
	}
	 public native int FindPeople(long matAddrGr);
	 public native int FindFeature(long matAddrGr,long matAddrDescriptor);
	 public native void SetDescriptor();
}
