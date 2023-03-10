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

    private int runMode = 0; //Rėí

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
                Logcat.dd("ęģĩëėļėĶėëŠ");
                break;
            case Constants.KSW_Activity_CertList:
                txvTbTitle.setText(R.string.ksw_activity_certlist_title);
                Logcat.dd("ęģĩëėļėĶęīëĶŽ ");
                break;
            case Constants.KSW_Activity_SCRAPING:
                txvTbTitle.setText("ėĪíŽëí ęģĩëėļėĶė");
                Logcat.dd("ėĪíŽëĐí ęģĩëėļėĶ : "+R.string.ksw_activity_certlist_title);
            default:
                Logcat.dd("ęģĩëėļėĶ ėėęĩŽëķ ėė");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCertListInApp(getIntent());
    }

    private void loadCertListInApp(Intent parmas) {
         Iterable<KSCertificate> sdcardUserCerts = null;
            // ėąëī ėļėĶė ëģīęļ° ėēīíŽ ė í/íīė  ė ëĶŽėĪíļ ėīęļ°
            if (userCerts != null) {
                userCerts.clear();
                if (userCertItem != null) userCertItem.clear();
                if (userCertAdapter != null) userCertAdapter.notifyDataSetChanged();
            }

            userCertItem = new ArrayList<KSW_CertItem>();
            try {
                // 1. NPKI + GPKI ėļėĶė ëĶŽėĪíļ ėėąëķ
                // userCerts = KSCertificateLoader.getUserCertificateListWithGpki(this);
                userCerts=KSCertificateLoader.getUserCertificateListWithGpki (KSW_Activity_CertList.this);
                // 2. sdėđīë ëĶŽėĪíļ ėėąëķ
               // sdcardUserCerts = KSCertificateLoader.getCertificateListfromSDCard(KSW_Activity_CertList.this);
            } catch (Exception e1) {
                Logcat.dd("ėļėĶė ëķëŽėĪęļ° ėĪ ėëŽę° ë°ėíėĩëëĪ.");
            }

        if (userCerts.size() == 0) {
            Toast.makeText(KSW_Activity_CertList.this, "ėļėĶė ëĶŽėĪíļė ėļėĶėę° ėėĩëëĪ.",
                    Toast.LENGTH_LONG).show();
        }else {
            //ėĄ°íë ėļėĶė ëĶŽėĪíļė ė ėŽ
            for (int i = 0; i < userCerts.size(); i++) {
                try {
                    userCertItem.add(new KSW_CertItem((KSCertificate) userCerts.elementAt(i)));
                } catch (Exception e) {
                    Logcat.dd("ėļėĶėėëŽ");
                }
            }
            if(sdcardUserCerts!=null){
                Iterator<KSCertificate> iter = sdcardUserCerts.iterator();
                while(iter.hasNext()){
                    try {
                        userCertItem.add(new KSW_CertItem((KSCertificate) iter.next()));
                    } catch (Exception e) {
                        Logcat.dd("ėļėĶėėëŽ");
                    }
                }
            }

            //íīëĶ­ėīëēĪíļë ėī ėė ėėī..
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
