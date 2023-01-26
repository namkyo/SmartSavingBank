package kr.co.smartbank.app.solution.face.util

import com.inzisoft.izmobilereader.IZMobileReaderCommon
import com.inzisoft.mobile.recogdemolib.LibConstants

object AppConstants {
    /**
     * ORIENTATION TYPE CONSTANTS
     */
    const val ORIENTATION_LANDSCAPE = 0
    const val ORIENTATION_PORTRAIT = 1
    const val CARD_POINT_PORT_ID_TYPE = LibConstants.CARD_POINT_PORT_ID_TYPE
    const val CARD_POINT_LAND_ID_TYPE = LibConstants.CARD_POINT_LAND_ID_TYPE


    /**
     * INTENT KEY
     */
    const val INTENT_RECOG_TYPE = LibConstants.INTENT_KEY_RECOG_TYPE
    const val INTENT_RECT_PICTURE = LibConstants.INTENT_KEY_PICTURE_ROI
    const val INTENT_RECT_FOR_PASSPORT_GUIDE = LibConstants.INTENT_KEY_GUIDE_ROI
    const val INTENT_RECT_FOR_PASSPORT_SCREEN = LibConstants.INTENT_KEY_SCREEN_ROI
    const val INTENT_RECOG_RESULT_VALUE = "intent_recog_result_value"
    const val INTENT_KEY_AUTO_SHOOTING = "auto_shooting"
    const val INTENT_KEY_SEAL_SAVED_PATH = "intent_key_seal_saved_path"
    const val INTENT_KEY_FROM_GALLERY = "intent_key_from_gallery"
    const val INTENT_CAMERA_PREVIEW_RESOLUTION = "intent_camera_preview_resolution" //for seal recog

    const val INTENT_OVERLAY_GUIDE_RECT = "intent_overlay_guide_rect" //for seal recog

    const val INTENT_OVERLAY_RESOLUTION = "intent_overlay_resolution" //for seal recog

    const val INTENT_PICTURE_RESOLUTION = "intent_picture_resolution" //for seal recog


    const val INTENT_RESULT_RETRY = 0x010

    // 서명/인감 스캔관련 OFFSET 값
    const val FIRST_CROP_OFFSET = 20 // 인식용 사각형 제거에 사용

    var BI_THRESHOLD_OFFSET = 150 // 0~255 사이의 값을 지정. 값이 클수록 밝을 부분을 많이 추출함.


    // 인감스캔시 사용
    const val SEAL_RECT_WIDTH_INCH = 2.56f // 도장등록 원장의 도장이 찍히는 사각형의 실제 가로사이즈를 인치로 환산.

    const val SEAL_RECT_HEIGHT_INCH = 2.24f // 도장등록 원장의 도장이 찍히는 사각형의 실제 세로사이즈를 인치로 환산.


    //mleader202007
    const val INTENT_RESULT_CLOSE = 0x011

    //외국인 등록증
    const val IDCARD_TYPE_ALIEN =
        IZMobileReaderCommon.IZMOBILEREADER_COMMON_RESULT_TYPE_ETC_ID_ALIEN_CARD

    //국내거소신고증
    const val IDCARD_TYPE_COMPATRIOT =
        IZMobileReaderCommon.IZMOBILEREADER_COMMON_RESULT_TYPE_ETC_ID_COMPATRIOT_CARD

    //복지카드
    const val IDCARD_WELFARE =
        IZMobileReaderCommon.IZMOBILEREADER_COMMON_RESULT_TYPE_ETC_ID_WELFARE_CARD

    //유공자
    const val IDCARD_HONOREE_CARD =
        IZMobileReaderCommon.IZMOBILEREADER_COMMON_RESULT_TYPE_ETC_ID_HONOREE_CARD

    //영주증
    const val IDCARD_RESIDENST =
        IZMobileReaderCommon.IZMOBILEREADER_COMMON_RESULT_TYPE_ETC_ID_RESIDENST_CARD

}