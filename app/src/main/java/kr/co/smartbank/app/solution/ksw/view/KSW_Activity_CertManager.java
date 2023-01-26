package kr.co.smartbank.app.solution.ksw.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lumensoft.ks.KSCertificate;
import com.lumensoft.ks.KSCertificateManager;
import com.lumensoft.ks.KSException;

import kr.co.coocon.sasapi.SASManager;
import kr.co.coocon.sasapi.common.SASEnvironment;
import kr.co.coocon.sasapi.common.SASException;
import kr.co.smartbank.app.R;
import kr.co.smartbank.app.config.Constants;
import kr.co.smartbank.app.process.ActivityResult;
import kr.co.smartbank.app.process.CommonDTO;
import kr.co.smartbank.app.solution.everspin.secureKeypad.QwertyView;
import kr.co.smartbank.app.solution.everspin.secureKeypad.SecureKeyPadManager;
import kr.co.smartbank.app.solution.ksw.util.KSW_Init_CertInfo;
import kr.co.smartbank.app.util.Logcat;
import kr.co.smartbank.app.util.SharedPreferenceHelper;
import kr.co.smartbank.app.view.BaseActivity;

public class KSW_Activity_CertManager extends BaseActivity {
    private Activity activity;
    private SharedPreferenceHelper sp;
    private KSCertificate userCert;
    private int runMode = 0; //R수행

    private SASManager sasManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ksw_activity_manage);
        activity    =   this;
        sp          =   new SharedPreferenceHelper(activity);

        Button btn_back = findViewById(R.id.ksw_manage_next);
        btn_back.setOnClickListener(view -> {
            finish();
            //overridePendingTransition(R.anim.slide_up,R.anim.slide_bottom);
        });
        TextView txvTbTitle = findViewById(R.id.ksw_manage_title);

        KSW_Init_CertInfo init_CertInfo = new KSW_Init_CertInfo(this);
        userCert=init_CertInfo.init();
        //userCert = KSW_CertListManager.getSelectedCert();


        /*
         * 인증서 추출정보
         */

        Logcat.dd("[버전]" + userCert.getVersion());
        Logcat.dd( "[일련번호(Hex)]" + userCert.getSerialNumberInt());
        Logcat.dd( "[서명 알고리즘]" + userCert.getSignatureAlgorithm());

        Logcat.dd( "[발급자명]" + userCert.getIssuerName());
        Logcat.dd( "[발급자DN]" + userCert.getIssuerDn());
        Logcat.dd( "[발급자CN]" + userCert.getIssuerCn());

        Logcat.dd( "[유효기간(시작)]" + userCert.getNotBefore());
        Logcat.dd( "[유효기간(끝)]" + userCert.getNotAfter());
        Logcat.dd( "[인증서 만료일]" + userCert.getNotAfter());
        Logcat.dd( "[인증서 만료일시]" + userCert.getExpiredTime());


        Logcat.dd( "[만료여부]" + userCert.isExpired());
        Logcat.dd( "[만료여부,갱신가능여부]" + userCert.isExpired());
        // 1(RSKSWCertificate.RSKSWConstCertExpModeNORMAL): 갱신가능 일자까지 남음
        // 0(RSKSWCertificate.RSKSWConstCertExpModeALMOST): 갱신가능 일자
        // -1(RSKSWCertificate.RSKSWConstCertExpModeEXPIRED): 만료됨

        Logcat.dd( "[사용자명]" + userCert.getSubjectName());
        Logcat.dd( "[사용자DN]" + userCert.getSubjectDn());
        Logcat.dd( "[공개키(소문자)]" + userCert.getPublicKey_HexLow());
        Logcat.dd( "[인증서 정책]" + userCert.getPolicy());
        Logcat.dd( "[인증서 OID값 ]" + userCert.getOID ());
        Logcat.dd( "[인증서 용도]" + userCert.getPolicyNumString());
//        Logcat.dd( "[인증서 VID메시지]" + userCert.getVidMsg());
        Logcat.dd( "[인증서 해쉬값]" + userCert.getCertHashHex());
        Logcat.dd( "[인증서 디렉토리]" + userCert.getDirPath());
        Logcat.dd( "[인증서 파일경로]" + userCert.getCertPath());
        Logcat.dd( "[인증서키 파일경로]" + userCert.getKeyPath());

        runMode = getIntent().getIntExtra(Constants.KSW_Activity,0);
        SecureKeyPadManager manaer = new SecureKeyPadManager();
        switch (runMode){
            case Constants.KSW_Activity_CertSign:
                txvTbTitle.setText("공동인증서 서명");
                //manaer.keyPadActivityOn(activity,Constants.KEYPAD_TYPE_QWERTY,"공동인증서 인증","비밀번호 입력","인증서 비밀번호을 넣어주세요",30,Constants.KSW_Activity_CertSign,"",6);
                Intent intent=new Intent(activity,QwertyView.class);
                intent.putExtra("maxNumChar",16);
                intent.putExtra("key","");
                intent.putExtra("title","공동인증서 인증 서명");
                intent.putExtra("sub_title","공동인증서 인증 서명");
                intent.putExtra("min",8);
                intent.putExtra("max",20);
                activity.startActivityForResult(intent, Constants.KSW_Activity_CertSign);
                break;
            case Constants.KSW_Activity_CertList:
                txvTbTitle.setText("공동인증서 조회");
                break;
            case Constants.KSW_Activity_SCRAPING:
                try {
                    SASManager.initInstance();
                    SASManager.setContext(activity);
                    SASManager.setCryptoMode(false);
                    SASManager.setDebugMode(false);
                    SASManager.setV8Mode(true);

                    //안드로이드 10 이상 설정
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        String defaultNpki = SASEnvironment.getString(SASEnvironment.LOCATIONOFCERTIFICATE); // 기본 인증서 저장 경로
                        String addNpki = "/data/data/kr.co.smartbank.app/NPKI/"; // 앱에 저장된 인증서 위치 (예시로 작성된 경로 입니다.)  인증서 탐색경로 추가 설정 ( ';' 구분자로 표시하여 추가합니다. )
                        SASEnvironment.setString(SASEnvironment.LOCATIONOFCERTIFICATE, defaultNpki + ";" + addNpki);// 기존 'NPKI' 폴더와 새로 추가한 인증서 저장위치로 인증서를 탐색합니다.
                    }
                    sasManager=SASManager.getInstance();
                } catch (SASException e) {
                    Logcat.dd("에러입니다");
                }
                txvTbTitle.setText("공동인증서 인증");
               // manaer.keyPadActivityOn(activity,Constants.KEYPAD_TYPE_QWERTY,"공동인증서 인증","비밀번호 입력","인증서 비밀번호을 넣어주세요",30,Constants.KSW_Activity_SCRAPING,"",6);

                Intent intent2=new Intent(activity,QwertyView.class);
                intent2.putExtra("maxNumChar",16);
                intent2.putExtra("key","");
                intent2.putExtra("title","공동인증서 인증 스크랩핑 서명");
                intent2.putExtra("sub_title","공동인증서 인증 스크랩핑 서명");
                intent2.putExtra("min",8);
                intent2.putExtra("max",20);
                activity.startActivityForResult(intent2, Constants.KSW_Activity_SCRAPING);


                break;
            case Constants.REQUEST_SCRAPING_CAPTCHA_INPUT:
                Logcat.dd("공동인증 작업구분 REQUEST_SCRAPING_CAPTCHA_INPUT");
                break;
            default:
                Logcat.dd("공동인증 작업구분 없음");
        }
    }
    protected void onResume() {
        super.onResume();
    }
    private AlertDialog DeleteConfirmDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setIcon(R.drawable.icon_normal)
                .setTitle("인증서 삭제확인")
                .setMessage("인증서를 삭제 하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int ret = KSCertificateManager.deleteCert(userCert);
                        if (KSException.SUCC == ret){
                            Toast.makeText(activity, "인증서를 성공적으로 삭제했습니다.",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            //overridePendingTransition(R.anim.slide_up,R.anim.slide_bottom);
                        }
                        else{
                            Toast.makeText(activity, "인증서 삭제를 실패하였습니다.(" + ret + ")",
                                    Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        return dialog.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommonDTO commonDto = new CommonDTO(activity,getSupportFragmentManager(),sp);
        commonDto.setUserCert(userCert);
        commonDto.setCustomProgressDialog(customProgressDialog);
        if(runMode==Constants.KSW_Activity_SCRAPING){
            commonDto.setParmas(getIntent().getStringExtra("params"));
            //스크래핑 처리
            commonDto.setSasManager(sasManager);
        }else if(runMode==Constants.KSW_Activity_CertSign){
            commonDto.singData=getIntent().getStringExtra("signData");
            commonDto.rbrno=getIntent().getStringExtra("rbrno");
        }
        new ActivityResult().onActivityResult(requestCode, resultCode, data,commonDto);
    }

    //전자서명
//    private String cert_sign(int signType,ProtectedData pw){
//        int alg = 0;
//        if (signType == Constants.TYPE_KOSCOM_SIGN) {
//            alg = KSSign.KOSCOM;
//        } else if (signType == Constants.TYPE_KOSCOM_SIGN_BRIEF) {
//            alg = KSSign.KOSCOM_BRIEF;
//        } else if (signType == Constants.TYPE_CMS_SIGN) {
//            alg = KSSign.CMS;
//        } else if (signType == Constants.TYPE_BRIEF_SIGN) {
//            alg = KSSign.BRIEF;
//        }
//        String plainText="서명합니다";
//        byte[] signature = new byte[4096];
//        try {
//            signature = KSSign.sign(alg, userCert, plainText.getBytes(), pw);
//            byte[] signature2=KSSign.cmsSign(userCert,plainText.getBytes(),pw);
//            byte[] encodedSignResult;
//            encodedSignResult = KSBase64.encode(signature2);
//            String finalSignResult = new String(encodedSignResult);
//            return finalSignResult;
//        } catch (KSException  e) {
//            Logcat.dd("에러입니다");
//        }
//        return "";
//    }
    @Override
    protected void onPause() {
        super.onPause();
        if (customProgressDialog!=null){
            customProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customProgressDialog!=null){
            customProgressDialog.dismiss();
        }
    }
}
