package kr.co.smartbank.app.solution.ksw.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lumensoft.ks.KSCertificate;
import com.lumensoft.ks.KSCertificateLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import kr.co.smartbank.app.R;
import kr.co.smartbank.app.config.Constants;
import kr.co.smartbank.app.process.ActivityResult;
import kr.co.smartbank.app.process.CommonDTO;
import kr.co.smartbank.app.solution.ksw.util.KSW_CertItem;
import kr.co.smartbank.app.solution.ksw.util.KSW_CertItemAdapter;
import kr.co.smartbank.app.util.Logcat;
import kr.co.smartbank.app.util.SharedPreferenceHelper;
import kr.co.smartbank.app.view.BaseActivity;

import androidx.annotation.Nullable;

public class KSW_Activity_CertList  extends BaseActivity  {
    private Activity activity;
    private Context context;
    private SharedPreferenceHelper sp;
    private KSW_CertItemAdapter userCertAdapter;
    private ArrayList<KSW_CertItem> userCertItem;
    private ListView userCertList;
    //private KSCertificate userCert;
    private static Vector<KSCertificate> userCerts = new Vector<>();

    private int runMode = 0; //R수행

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ksw_activity_certlist);

        activity    =   this;
        context     =   getApplicationContext();
        sp          =   new SharedPreferenceHelper(activity);
        userCertList = findViewById(R.id.usercertlist);

        TextView txvTbTitle = findViewById(R.id.cert_list_title);

        //private static final int KSW_Activity_Pwd_Result = 0;
        //private static final int KSW_Activity_Manage_Result = 1;
        //private AlertDialog alerDialog;
        View btn_cert_cert_close = findViewById(R.id.btn_cert_list_close);
        btn_cert_cert_close.setOnClickListener(view -> {
            activity.finish();
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom);
        });


        runMode = getIntent().getIntExtra(Constants.KSW_Activity,0);
        Logcat.dd("runMode : "+runMode);
        switch (runMode){
            case Constants.KSW_Activity_CertSign:
                txvTbTitle.setText(R.string.ksw_activity_certsign_title);
                Logcat.dd("공동인증서명");
                break;
            case Constants.KSW_Activity_CertList:
                txvTbTitle.setText(R.string.ksw_activity_certlist_title);
                Logcat.dd("공동인증관리 ");
                break;
            case Constants.KSW_Activity_SCRAPING:
                txvTbTitle.setText("스크래핑 공동인증서");
                Logcat.dd("스크랩핑 공동인증 : "+R.string.ksw_activity_certlist_title);
            default:
                Logcat.dd("공동인증 작업구분 없음");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCertListInApp(getIntent());
    }

    private void loadCertListInApp(Intent parmas) {
         Iterable<KSCertificate> sdcardUserCerts = null;
            // 앱내 인증서 보기 체크 선택/해제 시 리스트 초기
            if (userCerts != null) {
                userCerts.clear();
                if (userCertItem != null) userCertItem.clear();
                if (userCertAdapter != null) userCertAdapter.notifyDataSetChanged();
            }

            userCertItem = new ArrayList<KSW_CertItem>();
            try {
                // 1. NPKI + GPKI 인증서 리스트 생성부
                // userCerts = KSCertificateLoader.getUserCertificateListWithGpki(this);
                userCerts=KSCertificateLoader.getUserCertificateListWithGpki (KSW_Activity_CertList.this);
                // 2. sd카드 리스트 생성부
               // sdcardUserCerts = KSCertificateLoader.getCertificateListfromSDCard(KSW_Activity_CertList.this);
            } catch (Exception e1) {
                Logcat.dd("인증서 불러오기 중 에러가 발생했습니다.");
            }

        if (userCerts.size() == 0) {
            Toast.makeText(KSW_Activity_CertList.this, "인증서 리스트에 인증서가 없습니다.",
                    Toast.LENGTH_LONG).show();
        }else {
            //조회된 인증서 리스트에 적재
            for (int i = 0; i < userCerts.size(); i++) {
                try {
                    userCertItem.add(new KSW_CertItem((KSCertificate) userCerts.elementAt(i)));
                } catch (Exception e) {
                    Logcat.dd("인증서에러");
                }
            }
            if(sdcardUserCerts!=null){
                Iterator<KSCertificate> iter = sdcardUserCerts.iterator();
                while(iter.hasNext()){
                    try {
                        userCertItem.add(new KSW_CertItem((KSCertificate) iter.next()));
                    } catch (Exception e) {
                        Logcat.dd("인증서에러");
                    }
                }
            }

            //클릭이벤트는 이 안에 있슴..
            userCertAdapter = new KSW_CertItemAdapter(context,activity,R.layout.ksw_certitem, userCertItem,runMode,parmas);
            userCertList.setAdapter(userCertAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommonDTO commonDto = new CommonDTO(activity,getSupportFragmentManager(),sp);
        //commonDto.setUserCert(userCert);
        commonDto.setCustomProgressDialog(customProgressDialog);
        if(runMode==Constants.KSW_Activity_SCRAPING){
            commonDto.setParmas(getIntent().getStringExtra("params"));
        }else if(runMode==Constants.KSW_Activity_CertSign){
            commonDto.rbrno=getIntent().getStringExtra("rbrno");
        }

        new ActivityResult().onActivityResult(requestCode, resultCode, data,commonDto);
    }

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
