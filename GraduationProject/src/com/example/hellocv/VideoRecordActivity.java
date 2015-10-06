package com.example.hellocv;

import java.io.File;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.VideoView;

public class VideoRecordActivity extends Activity implements SurfaceHolder.Callback {


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// on Pause ���¿��� ī�޶� ,���ڴ� ��ü�� �����Ѵ�
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		super.onPause();
	}

	// Video View ��ü
	private VideoView mVideoView = null;
	// ī�޶� ��ü
	private Camera mCamera;
	// ���ڴ� ��ü ����
	private MediaRecorder recorder = null;
	// �ƿ�ǲ ���� ���
	private static final String OUTPUT_FILE = "/sdcard/videooutput.mp4";
	// ��ȭ �ð� - 10��
	private static final int RECORDING_TIME = 10000;

	// ī�޶� �����並 �����Ѵ�
	private void setCameraPreview(SurfaceHolder holder) {
		try {
			// ī�޶� ��ü�� �����
			mCamera = Camera.open();
			// ī�޶� ��ü�� �Ķ���͸� ��� �����̼��� 90�� ���´�,��Q�� ��� 90ȸ���� �ʿ�� �Ѵ� ,��Q�� ����
			// ���ϴµ�....
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setRotation(90);
			mCamera.setParameters(parameters);
			// ������ ���÷��̸� ����� ���ǽ� Ȧ���� �����Ѵ�
			mCamera.setPreviewDisplay(holder);
			// ������ �ݹ��� �����Ѵ� - ������ ������ �����ϴ�,
			/*
			 * mCamera.setPreviewCallback(new PreviewCallback() {
			 * 
			 * @Override public void onPreviewFrame(byte[] data, Camera camera)
			 * { // TODO Auto-generated method stub } });
			 */
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// ���ǽ��� ��������� ���� ���� ��ƾ
		setCameraPreview(holder);
		Log.e("CAM TEST", "Error Occur???!!!"+mCamera.toString());
		beginRecording(mVideoView.getHolder()); 
		long time = System.currentTimeMillis();
		
		while(System.currentTimeMillis()-time < 3000);
		stop();
		finish();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		// TODO Auto-generated method stub
		// ���ǽ� ����Ǿ��� ���� ���� ��ƾ
		if (mCamera != null) {
			Camera.Parameters parameters = mCamera.getParameters();
			// ������ ������ �� ������
			parameters.setPreviewSize(width, height);
			mCamera.setParameters(parameters);
			// ������ �ٽ� ����
			mCamera.startPreview();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

		// ���ǽ� �Ҹ���� ���� ��ƾ

		// �����並 �����
		if (mCamera != null) {
			mCamera.stopPreview();
			// ī�޶� ��ü �ʱ�ȭ
			mCamera = null;
		}

	}

	// ������(ī�޶� ��� �ִ� ȭ���� �����ִ� ȭ��) ���� �Լ�
	private void setPreview() {
		// 1) ���̾ƿ��� videoView �� ��� ������ �����Ѵ�
		mVideoView = (VideoView) findViewById(R.id.videoView);
		// 2) surface holder ������ ����� videoView�κ��� �ν��Ͻ��� ���´�
		final SurfaceHolder holder = mVideoView.getHolder();
		// 3)ǥ���� ��ȭ�� �������� �ݹ� ��ü�� ����Ѵ�
		holder.addCallback(this);
		// 4)Surface view�� ������ �����Ѵ�, �Ʒ� Ÿ���� ���۰� ���̵� ȭ���� ǥ���� �� ���ȴ�.ī�޶� ������� ������
		// ���۰� �ʿ����
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	private void beginRecording(SurfaceHolder holder) {
		// ���ڴ� ��ü �ʱ�ȭ
		Log.e("CAM TEST", "#1 Begin REC!!!");
		if (recorder != null) {
			recorder.stop();
			recorder.release();
		}
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			Log.e("CAM TEST", "I/O Exception");
		}
		// ���� ����/�ʱ�ȭ
		Log.e("CAM TEST", "#2 Create File!!!");
		File outFile = new File(OUTPUT_FILE);
		if (outFile.exists()) {
			outFile.delete();
		}
		Log.e("CAM TEST", "#3 Release Camera!!!");
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			Log.e("CAM TEST", "#3 Release Camera  _---> OK!!!");
		}

		try {
			recorder = new MediaRecorder();
			// Video/Audio �ҽ� ����
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			// ���� ����� �����ϸ� prepare ������ ����, �� �׷���? -> Ư�� �ػ󵵰� ������ �� �ػ󵵿��� ���� ����
			// �ִ�
			recorder.setVideoSize(800, 480);
			recorder.setVideoFrameRate(25);
			// Video/Audio ���ڴ� ����
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			// ��ȭ �ð� �Ѱ� , 10��
			recorder.setMaxDuration(RECORDING_TIME);
			// �����並 ������ ���ǽ� ����
			recorder.setPreviewDisplay(holder.getSurface());
			// ��ȭ�� ��� ���� ����
			recorder.setOutputFile(OUTPUT_FILE);
			recorder.prepare();
			recorder.start();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	void stop() {
	    // TODO Auto-generated method stub
	    // ���ڴ� ��ü�� ������ ��� �̸� �����Ų��
	    if ( recorder !=null){
	    Log.e("CAM TEST","CAMERA STOP!!!!!");
	     recorder.stop();
	     recorder.release();
	     recorder = null;
	    }
	    // �����䰡 ���� ��� �ٽ� ���� ��Ų��
	    if ( mCamera == null ) {
	     Log.e("CAM TEST","Preview Restart!!!!!");
	     // ������ �ٽ� ����
	     setCameraPreview(mVideoView.getHolder());
	     // ������ �����
	     mCamera.startPreview();
	    }
	    
	   }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_record);
		// ����ȭ�� �������� ó���Ѵ�
		// SCREEN_ORIENTATION_LANDSCAPE - ����ȭ�� ����
		// SCREEN_ORIENTATION_PORTRAIT - ����ȭ�� ����
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// �����並 �����Ѵ�
		setPreview();
		if (mVideoView.getHolder() == null)
	    {
	     Log.e("CAM TEST","View Err!!!!!!!!!!!!!!!");
	    }
	}
}
