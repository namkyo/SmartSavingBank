package kr.co.smartbank.app.solution.face.view;

import kr.co.smartbank.app.R;
import kr.co.smartbank.app.solution.face.util.AppConstants;
import kr.co.smartbank.app.solution.face.util.ImageBucket;
import kr.co.smartbank.app.util.SharedPreferenceHelper;
import kr.co.smartbank.app.view.BaseActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.inzisoft.mobile.data.MIDReaderProfile;
import com.inzisoft.mobile.data.RecognizeResult;
import com.inzisoft.mobile.recogdemolib.LibConstants;
import com.inzisoft.mobile.recogdemolib.RecognizeInterface;
import com.inzisoft.mobile.recogdemolib.RecognizeInterface.RecognizeFinishListener;
import com.inzisoft.mobile.sealextractor.SealAndSignatureScanner;
import com.inzisoft.mobile.util.CommonUtils;
import com.inzisoft.mobile.view.CardPointView;

public class OcrRecognizeActivity extends BaseActivity implements View.OnClickListener {
    protected static final String TAG = OcrRecognizeActivity.class.getSimpleName();

    private Activity activity;
    private SharedPreferenceHelper sp;

    public static final int TAKE_REPLAY = 2;

    private Button mCloseButton;
    private Button mRecognizeButton;
    private FrameLayout mProgressbar;
    private LinearLayout mTitleLayout;
    private Rect mGuideRoi, mScreenRoi;

    private int mCameraRunType;

    private RecognizeInterface mRecognizeInterface;
    private CardPointView mCardPointView;
    private boolean mIsManualCropStarted;

    private RecognizeFinishListener mRecognizeFinishListener = new RecognizeFinishListener() {

        @Override
        public void onFinish(int resultValue) {

            if (mProgressbar != null) {
                mProgressbar.setVisibility(View.GONE);
            }

            switch (resultValue) {
                case LibConstants.ERR_CODE_RECOGNITION_SUCCESS:
                    if (mCameraRunType == LibConstants.TYPE_IDCARD_BACK) {
                        setResult(RESULT_OK);
                    } else if (mCameraRunType == LibConstants.TYPE_PAPER) {
                        if (MIDReaderProfile.getInstance().SET_USE_MANUAL_CROP && !mIsManualCropStarted) {
                            Bitmap originImage = RecognizeResult.getInstance().getOriginImage();
                            int imgWidth = originImage.getWidth();
                            int imgHeight = originImage.getHeight();
                            int viewWidth = mCardPointView.getWidth();
                            int viewHeight = mCardPointView.getHeight();

                            if (MIDReaderProfile.getInstance().SET_USER_SCREEN_PORTRAIT) {
                                imgWidth = originImage.getHeight();
                                imgHeight = originImage.getWidth();
                            }
                            Point[] recognizedPoints = RecognizeResult.getInstance().getCroppedPoints();
                            Point[] scaledPoints = new Point[4];
                            for (int i = 0; i < recognizedPoints.length; i++) {
                                scaledPoints[i] = new Point(recognizedPoints[i].x * viewWidth / imgWidth, recognizedPoints[i].y * viewHeight / imgHeight);
                            }

                            mCardPointView.setPoint(scaledPoints);

                            setManualCropUI();
                            return;
                        }

                        // ????????????(RecognizeResult)?????? ???????????? ???????????? ????????????.
                        RecognizeResult result = RecognizeResult.getInstance();
                        Bitmap recogBmp = result.getRecogResultImage(false);
                        ImageBucket.INSTANCE.addImage(recogBmp.copy(recogBmp.getConfig(), true));
                        // ?????????????????? ?????? ???????????? ????????? ??????
                        result.cleanRecogData();

                        if (ImageBucket.INSTANCE.isFull()) {
                            // ?????? ???????????? ????????? ???????????? ????????? ?????????????????? ????????????.
                            Intent intent = new Intent();
                            intent.putExtra(AppConstants.INTENT_RECOG_TYPE, mCameraRunType);
                            setResult(RESULT_OK, intent);
                        } else {
                            setResult(TAKE_REPLAY);
                        }
                    } else if (mCameraRunType == LibConstants.TYPE_FORMOCR_FAMILY || mCameraRunType == LibConstants.TYPE_FORMOCR_SEAL) {
                        // ????????????(RecognizeResult)?????? ???????????? ???????????? ????????????.
                        RecognizeResult result = RecognizeResult.getInstance();
                        //????????? ???????????? ?????? ????????? ????????? ??????
                        Bitmap recogBmp = result.getRecogResultImage(true);
                        ImageBucket.INSTANCE.addImage(recogBmp.copy(recogBmp.getConfig(), true));
                        // ?????????????????? ?????? ???????????? ????????? ??????
                        result.cleanRecogData();

                        Intent intent = new Intent();
                        intent.putExtra(AppConstants.INTENT_RECOG_TYPE, mCameraRunType);
                        setResult(RESULT_OK, intent);

                    } else if (mCameraRunType == LibConstants.TYPE_SEAL) {
                        Intent intent = getIntent();
                        Bundle extras = intent.getExtras();
                        Point cameraPreviewSize = extras.getParcelable(AppConstants.INTENT_CAMERA_PREVIEW_RESOLUTION); //for Seal
                        Rect overlayGuideRect = extras.getParcelable(AppConstants.INTENT_OVERLAY_GUIDE_RECT); //for Seal
                        Point overlaySize = extras.getParcelable(AppConstants.INTENT_OVERLAY_RESOLUTION); //for Seal
                        Point pictureSize = extras.getParcelable(AppConstants.INTENT_PICTURE_RESOLUTION); //for Seal

                        startSealRecognizeUseRgb(overlaySize, cameraPreviewSize, pictureSize, overlayGuideRect);
//						setResult(RESULT_OK, intent);
                        return;
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(AppConstants.INTENT_RECOG_TYPE, mCameraRunType);
                        intent.putExtra(AppConstants.INTENT_RECT_FOR_PASSPORT_GUIDE, mGuideRoi);
                        intent.putExtra(AppConstants.INTENT_RECT_FOR_PASSPORT_SCREEN, mScreenRoi);
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                    break;

                /**
                 * ????????? ????????? ?????? ??????
                 */
                // ????????? ??????  ??????
                case LibConstants.ERR_CODE_FIND_EDGE_FAILED:
                    // ????????? ?????? ?????? ??????
                case LibConstants.ERR_CODE_BOUNDARY_OUT_SCREEN:
                    // ????????? ???????????? ???????????? ?????? ??? ????????? ????????? ?????? ??????
                case LibConstants.ERR_CODE_IMAGE_RATIO_WEIRD:
                    Toast.makeText(activity, "ERR_CODE_FIND_EDGE_FAILED!!", Toast.LENGTH_LONG).show();
                    setManualCropUI();
                    break;

                case LibConstants.ERR_CODE_RECOGNITION_ORIENTATION_ERROR:
                    Log.e(TAG, "mleader ORIENTATION ERROR");
                    setResult(resultValue);
                    finish();

                    break;
                case LibConstants.ERR_CODE_TRANSFORMING_FAILED:
                case LibConstants.ERR_CODE_RECOGNITION_FAILED:
                    break;

                default:
                    if (mProgressbar != null) {
                        mProgressbar.setVisibility(View.GONE);
                    }
                    setResult(resultValue);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsManualCropStarted = false;

        activity=this;
        sp= new SharedPreferenceHelper(activity);

        if (savedInstanceState != null) {
            finish();
            return;
        }

        Intent intent = getIntent();
        Rect roi = null;

        if (intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                roi = extras.getParcelable(AppConstants.INTENT_RECT_PICTURE);

                if (roi == null) {
                    roi = new Rect(0, 0, 0, 0);
                }

                mCameraRunType = extras.getInt(AppConstants.INTENT_RECOG_TYPE, LibConstants.TYPE_IDCARD);
                mScreenRoi = extras.getParcelable(AppConstants.INTENT_RECT_FOR_PASSPORT_SCREEN);
                mGuideRoi = intent.getParcelableExtra(AppConstants.INTENT_RECT_FOR_PASSPORT_GUIDE);

                if (mCameraRunType == LibConstants.TYPE_OTHERS) { // ?????? ?????? ????????? ?????? ????????? ?????? ?????? ?????? ??????
                    setResult(RESULT_OK);
                    finish();
                    return;
                }

            } else {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }

        // RecognizeActivity UI ?????????
        // ?????? ?????? ?????? UI ?????? : ?????? ???(GONE), ??????(????????????), ???????????????(VISIBLE)
        if (MIDReaderProfile.getInstance().SET_USER_SCREEN_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            MIDReaderProfile.getInstance().SET_USER_SCREEN_PORTRAIT = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            MIDReaderProfile.getInstance().SET_USER_SCREEN_PORTRAIT = false;
        }

        setContentView(R.layout.activity_recognize);
        mTitleLayout = (LinearLayout) findViewById(R.id.layout_recog_result_top);
        mProgressbar = (FrameLayout) findViewById(R.id.layout_recog_result_frame_progress);
        mCloseButton = (Button) findViewById(R.id.layout_recog_result_btn_close);
        mRecognizeButton = (Button) findViewById(R.id.layout_recog_result_btn_recognize);

        mProgressbar.setVisibility(View.VISIBLE);
        mTitleLayout.setVisibility(View.GONE);

        mRecognizeButton.setOnClickListener(this);
        mRecognizeButton.setEnabled(false);
        mCloseButton.setOnClickListener(this);
        mCloseButton.setEnabled(false);

        // ?????? ?????? ?????? ??????
        mCardPointView = (CardPointView) findViewById(R.id.view_image);
        //Crop Point ??????
        if (mCameraRunType == LibConstants.TYPE_IDCARD) {
            if (MIDReaderProfile.getInstance().SET_USER_SCREEN_PORTRAIT) {
                mCardPointView.setCardPointType(LibConstants.CARD_POINT_PORT_ID_TYPE);
            } else {
                mCardPointView.setCardPointType(AppConstants.CARD_POINT_LAND_ID_TYPE);
            }
        } else if (mCameraRunType == LibConstants.TYPE_BIZ_REGI) {
            mCardPointView.setCardPointType(LibConstants.CARD_POINT_PORT_BIZREGI_TYPE);
        }
        // ????????? ????????? ??????
        mCardPointView.setCropDotImage(R.drawable.ml_img_cutting, R.drawable.ml_img_cutting);
        // ?????? ????????? ??????
        mCardPointView.setCropLineColor(Paint.ANTI_ALIAS_FLAG, 100, 0xff, 0x5a, 0x00, 4 / getResources()
                .getDisplayMetrics().density);

        mRecognizeInterface = new RecognizeInterface(this, roi, mCameraRunType, mRecognizeFinishListener);
        // ????????? ????????? ????????????????????? ??? ?????????.
        mRecognizeInterface.initLayout(mCardPointView);

        // ?????? ??????
        if (mCameraRunType == LibConstants.TYPE_PASSPORT) {
            mRecognizeInterface.startRecognizeAutoCrop(false, mScreenRoi, mGuideRoi);
        } else {
            mRecognizeInterface.startRecognizeAutoCrop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecognizeInterface.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == mCloseButton) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (v == mRecognizeButton) {
            // ?????? crop ?????? ?????? ?????? ?????? UI ?????? : ??????(????????????), ???????????????(VISIBLE)
            mProgressbar.setVisibility(View.VISIBLE);
            mRecognizeButton.setEnabled(false);
            mRecognizeInterface.enableCropUI(false);
            // ?????? crop ?????? ?????? ??????
            mRecognizeInterface.startRecognizeViaManuallyCrop();
            mIsManualCropStarted = true;
        }
    }

    private void setManualCropUI() {
        // ?????? crop ????????? ??????.
        // ?????? crop ?????? UI ?????? : ?????????(VISIBLE), ??????(?????????), ???????????????(GONE)
        mTitleLayout.setVisibility(View.VISIBLE);
        mRecognizeInterface.enableCropUI(true);
        mRecognizeButton.setEnabled(true);
        mCloseButton.setEnabled(true);
    }

    /**
     * @param overlaySize       Point(CameraOverlayView.getWidth(), CameraOverlayView.getWidth())
     * @param cameraPreviewSize CameraPreviewInterface.getPreviewResolution()
     * @param pictureSize       CameraPreviewInterface.getPictureSize()
     * @param overlayGuideRect  OverlayView.getGuideRect()
     */
    @SuppressLint("StaticFieldLeak")
    private void startSealRecognizeUseRgb(Point overlaySize, Point cameraPreviewSize, Point pictureSize, Rect overlayGuideRect) {

        final Rect pictureROI = CommonUtils.convertDisplayROIToPictureROI(
                overlaySize,
                cameraPreviewSize,
                pictureSize,
                overlayGuideRect);

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... voids) {
                SealAndSignatureScanner sealAndSignatureScanner = new SealAndSignatureScanner(getApplicationContext());
                Bitmap bitmap = sealAndSignatureScanner.scanSealAndSigNature(pictureROI, true, AppConstants.FIRST_CROP_OFFSET,
                        AppConstants.SEAL_RECT_WIDTH_INCH, AppConstants.SEAL_RECT_HEIGHT_INCH); //scan ??????
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                ImageBucket.INSTANCE.clean();
                ImageBucket.INSTANCE.addImage(bitmap);
                //???????????? ?????????
                RecognizeResult.getInstance().clean();
                setResult(RESULT_OK, getIntent());
                finish();
            }
        }.execute();
    }
}
