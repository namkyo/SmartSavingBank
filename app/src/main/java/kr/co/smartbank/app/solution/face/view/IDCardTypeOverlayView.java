package kr.co.smartbank.app.solution.face.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.inzisoft.mobile.data.MIDReaderProfile;
import com.inzisoft.mobile.view.overlay.CameraOverlayView;

public class IDCardTypeOverlayView extends CameraOverlayView {
	private int mGuideTextSize;
	private String mGuideString;
	private boolean mShowFingerPrintArea;

	private boolean mRatioSetted = false;
	private float mRatio = 0.63f;

	// 자동촬영 시, 외곽선 Draw 관련 필드
	private boolean mAutoCaptureEnabled;
	private boolean isFindingSuccess;
	private Point[] foundEdgePoints;
	protected Path mEdgePath;
	protected Paint mEdgePaint, mGuidePaint;
	protected int mEdgeStrokeWidth;

	private int mDisplayWidth;
	private int mDisplayHeight;
	private Long timer = System.currentTimeMillis();
	/**
	 * 자동 촬영 시, 촬영 대상의 Edge 찾음 성공 유무와 찾았을 때의 4꼭짓점을 반환하는 CallbackListener
	 */
	FoundEdgeCallbackListener mFoundEdgeCallBackListener = new FoundEdgeCallbackListener() {

		@Override
		public void callback(boolean isSuccess, Point[] points) {
			isFindingSuccess = isSuccess;
			foundEdgePoints = points;
			processFoundEdge();
			invalidate();
		}
	};

	public IDCardTypeOverlayView(Context context) {
		super(context);
		init(context);
		mShowFingerPrintArea = false;
	}

	public IDCardTypeOverlayView(Context context, boolean autoCaptureEnabled) {
		this(context);
		this.mAutoCaptureEnabled = autoCaptureEnabled;
		init(context);
	}

	public IDCardTypeOverlayView(Context context, boolean autoCaptureEnabled, boolean showFingerPrintArea) {
		super(context);
		this.mAutoCaptureEnabled = autoCaptureEnabled;
		this.mShowFingerPrintArea = showFingerPrintArea;
		init(context);
	}

	public IDCardTypeOverlayView(Context context, boolean showFingerPrintArea, float guideRatio) {
		this(context, showFingerPrintArea);
		setRatio(guideRatio);
	}

	public void setRatio(float r) {
		if (r <= 0.0f || 1.0f < r) {
			return;
		}

		if (mRatioSetted == true) {
			return;
		}
		mRatio = r;
		mRatioSetted = true;
	}

	private void init(Context context) {
		int resId = context.getResources().getIdentifier("str_idcard_guide_text", "string", context.getPackageName());
		mGuideString = context.getResources().getString(resId);
		mGuideTextSize = context.getResources().getDisplayMetrics().densityDpi/12;
		//mleader 20200512
		//preview 인식 시 신분증 영역을 표시해주도록 callback 추가
		if (mAutoCaptureEnabled || MIDReaderProfile.getInstance().FIND_EDGE_ON_PREVIEW) {
			// 자동촬영 시, 촬영 대상의 외곽 꼭짓점을 반환하는 CallbacnkListener등록
			super.setFoundEdgeCallbackListener(mFoundEdgeCallBackListener);
			// 자동촬영 시, 촬영 대상의 외곽선을 그리는 도구 초기화
			initDrawEdge();
		}
	}

	@Override
	protected void onDrawOverlayView(Canvas canvas) {
//        if (MIDReaderProfile.getInstance().DISPLAY_SIZE != null) {
//            mDisplayWidth = MIDReaderProfile.getInstance().DISPLAY_SIZE.widthPixels;
//            mDisplayHeight = MIDReaderProfile.getInstance().DISPLAY_SIZE.heightPixels;
//        } else {
//			mDisplayWidth = getWidth();
//            mDisplayHeight = getHeight();
//		}

		mDisplayWidth = getWidth();
		mDisplayHeight = getHeight();

		if (mAutoCaptureEnabled) {
			// 자동촬영 시, 촬영대상의 찾은 외곽선 표시
			canvas.drawPath(mEdgePath, mEdgePaint);
		}
		// Calculate width and height of the guide area.
		int guideWidth;
		int guideHeight;

		//세로촬영
		if (MIDReaderProfile.getInstance().SET_USER_SCREEN_PORTRAIT) {
			guideWidth = mDisplayWidth * 6 / 7;
			guideHeight = (int) (guideWidth * mRatio);
		} else {
			guideWidth = mDisplayWidth * 3 / 5;
			guideHeight = (int) (guideWidth * mRatio);
			mDisplayHeight = getHeight();
			mDisplayWidth = getWidth();
		}

		// Set start point of the guide area.
		int guideStartX = mDisplayWidth / 2 - guideWidth / 2;
		int guideStartY = mDisplayHeight / 2 - guideHeight / 2;

		// Make guide rect instance.
		mGuideRect = new Rect(guideStartX, guideStartY, guideStartX+guideWidth, guideStartY+guideHeight);

		// Draw guide lines.
		Paint paint = new Paint();
		paint.setColor(0xff8c8c8c);

		//mleader 20200512
		//상황에 따른 라인색 추가가
		if (mGuidePaint != null) {
			canvas.drawLine(guideStartX, guideStartY, guideStartX + guideWidth, guideStartY, mGuidePaint);
			canvas.drawLine(guideStartX, guideStartY, guideStartX, guideStartY + guideHeight, mGuidePaint);
			canvas.drawLine(guideStartX, guideStartY + guideHeight, guideStartX + guideWidth, guideStartY + guideHeight, mGuidePaint);
			canvas.drawLine(guideStartX + guideWidth, guideStartY, guideStartX + guideWidth, guideStartY + guideHeight, mGuidePaint);
		}
		// Draw outer bar of the guide area.
		paint.setColor(0x55000000);

		// Finger Print Area
		if (mShowFingerPrintArea) {
			Rect fpa = new Rect(
					guideStartX + (int) ((float) guideWidth * (1.0 / 25)),
					guideStartY + (int) ((float) guideHeight * (3.0 / 8)),
					guideStartX + (int) ((float) guideWidth * (2.0 / 5)),
					guideStartY + (int) ((float) guideHeight * (15.0 / 16)));
			canvas.drawRect(fpa, paint);
		}

		Rect leftOverlayBar = new Rect(0, 0, guideStartX, getHeight());
		Rect rightOverlayBar = new Rect(guideStartX + guideWidth, 0, getWidth(), getHeight());
		Rect topOverlayBar = new Rect(guideStartX, 0, guideStartX + guideWidth, guideStartY);
		Rect bottomOverlayBar = new Rect(guideStartX, guideStartY + guideHeight, guideStartX + guideWidth, getHeight());

		canvas.drawRect(leftOverlayBar, paint);
		canvas.drawRect(rightOverlayBar, paint);
		canvas.drawRect(topOverlayBar, paint);
		canvas.drawRect(bottomOverlayBar, paint);

		// Draw guide text.
		paint.setStyle(Style.FILL);
		paint.setColor(0xffffffff);
		paint.setTextSize(mGuideTextSize);
		paint.setTextAlign(Align.LEFT);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

		Rect bounds = new Rect();
		paint.getTextBounds(mGuideString, 0, mGuideString.length(), bounds);

		//mleader 20200512
		//상황에 따른 blinking 문구 추가
		if (mGuidePaint != null) {
			if(System.currentTimeMillis() > timer+400) {
				timer=System.currentTimeMillis();
				canvas.drawRoundRect(guideStartX + guideWidth /10, (float)(guideStartY - bounds.height()*4),
						guideStartX + guideWidth - guideWidth/10, (float)(guideStartY - bounds.height()*7),
						85,85, mGuidePaint);

				bounds.right = (int) paint.measureText(mGuideString, 0, mGuideString.length());
				bounds.left += (mDisplayWidth - bounds.right) / 2.0f;
				canvas.drawText(mGuideString, bounds.left, (guideStartY - bounds.height()*5), paint);
			}
		}
//		canvas.drawText(mGuideString, guideStartX + (guideWidth/2-bounds.width()/2), guideStartY + (guideHeight/2-bounds.height()/2), paint);
	}

	//mleader 20200512
	//신분증 찾은 영역 path 표시를 않고, 가이드 영역의 색 및 문구를 제어
	private void processFoundEdge( ) {
		Log.i("view", "mleader found edge!");
		if (foundEdgePoints != null) {
			if (isFindingSuccess) {	//인식가능상태
				// showEdge
				mEdgePath.reset();
				mEdgePaint.setColor(Color.YELLOW);
//                setPointsToPath(foundEdgePoints);
				mGuideString = "네모 영역에 꽉 차게 맞춰주세요.";
				setOkGuide();
			} else {				//가이드 영역에 신분증이 위치하지 않은 상태
				mEdgePath.reset();
				mEdgePaint.setColor(Color.RED);
//                setPointsToPath(foundEdgePoints);
				mGuideString = "네모 영역에 꽉 차게 맞춰주세요.";
				setStillGuide();
			}
		} else {					//빛반사 및 기울기로 인해 인식 불가 상태태            // hideEdge
			mEdgePath.reset();
			setAlertGuide();
			setGuideString("빛반사 및 기울임에 문제가 있습니다.");

		}
	}

	public void setGuideString(String message) {
		mGuideString = message;
		setAlertGuide();
		invalidate();
	}

	private void setStillGuide() {
		mGuidePaint.setColor(Color.WHITE);
	}

	private void setAlertGuide() {
		mGuidePaint.setColor(Color.RED);
	}

	private void setOkGuide() {
		mGuidePaint.setColor(Color.GREEN);
	}


	private void setPointsToPath(Point[] points) {
		Point startPoint = null;
		for (int i = 0; i < points.length; i++) {
			Point point = points[i];
			if (point == null) {
				continue;
			}
			if (i == 0) {
				mEdgePath.moveTo(point.x, point.y);
				startPoint = point;
			} else {
				mEdgePath.lineTo(point.x, point.y);
			}
		}
		if (startPoint != null) {
			mEdgePath.lineTo(startPoint.x, startPoint.y);
		}
	}

	private void initDrawEdge() {
		mEdgeStrokeWidth = 10;

		mEdgePath = new Path();

		mEdgePaint = new Paint();
		mEdgePaint.setDither(true);
		mEdgePaint.setAntiAlias(true);
		mEdgePaint.setStyle(Style.STROKE);
		mEdgePaint.setStrokeCap(Cap.ROUND);
		mEdgePaint.setStrokeWidth(mEdgeStrokeWidth);
		mEdgePaint.setColor(Color.YELLOW);

		mGuidePaint = new Paint();
		mGuidePaint.setStyle(Style.STROKE);
		mGuidePaint.setStrokeWidth(mEdgeStrokeWidth);
		setStillGuide();
	}

	public void setAutoCaptureEnabled(boolean autoCaptureEnabled) {
		this.mAutoCaptureEnabled = autoCaptureEnabled;
	}
}
