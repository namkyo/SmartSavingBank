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
    private int runMode = 0; //Rėí

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
         * ėļėĶė ėķėķė ëģī
         */

        Logcat.dd("[ëēė ]" + userCert.getVersion());
        Logcat.dd( "[ėžë Ļëēíļ(Hex)]" + userCert.getSerialNumberInt());
        Logcat.dd( "[ėëŠ ėęģ ëĶŽėĶ]" + userCert.getSignatureAlgorithm());

        Logcat.dd( "[ë°ęļėëŠ]" + userCert.getIssuerName());
        Logcat.dd( "[ë°ęļėDN]" + userCert.getIssuerDn());
        Logcat.dd( "[ë°ęļėCN]" + userCert.getIssuerCn());

        Logcat.dd( "[ė íĻęļ°ę°(ėė)]" + userCert.getNotBefore());
        Logcat.dd( "[ė íĻęļ°ę°(ë)]" + userCert.getNotAfter());
        Logcat.dd( "[ėļėĶė ë§ëĢėž]" + userCert.getNotAfter());
        Logcat.dd( "[ėļėĶė ë§ëĢėžė]" + userCert.getExpiredTime());


        Logcat.dd( "[ë§ëĢėŽëķ]" + userCert.isExpired());
        Logcat.dd( "[ë§ëĢėŽëķ,ę°ąė ę°ëĨėŽëķ]" + userCert.isExpired());
        // 1(RSKSWCertificate.RSKSWConstCertExpModeNORMAL): ę°ąė ę°ëĨ ėžėęđė§ ëĻė
        // 0(RSKSWCertificate.RSKSWConstCertExpModeALMOST): ę°ąė ę°ëĨ ėžė
        // -1(RSKSWCertificate.RSKSWConstCertExpModeEXPIRED): ë§ëĢëĻ

        Logcat.dd( "[ėŽėĐėëŠ]" + userCert.getSubjectName());
        Logcat.dd( "[ėŽėĐėDN]" + userCert.getSubjectDn());
        Logcat.dd( "[ęģĩę°íĪ(ėëŽļė)]" + userCert.getPublicKey_HexLow());
        Logcat.dd( "[ėļėĶė ė ėą]" + userCert.getPolicy());
        Logcat.dd( "[ėļėĶė OIDę° ]" + userCert.getOID ());
        Logcat.dd( "[ėļėĶė ėĐë]" + userCert.getPolicyNumString());
//        Logcat.dd( "[ėļėĶė VIDëĐėė§]" + userCert.getVidMsg());
        Logcat.dd( "[ėļėĶė íīėŽę°]" + userCert.getCertHashHex());
        Logcat.dd( "[ėļėĶė ëë í ëĶŽ]" + userCert.getDirPath());
        Logcat.dd( "[ėļėĶė íėžęē―ëĄ]" + userCert.getCertPath());
        Logcat.dd( "[ėļėĶėíĪ íėžęē―ëĄ]" + userCert.getKeyPath());

        runMode = getIntent().getIntExtra(Constants.KSW_Activity,0);
        SecureKeyPadManager manaer = new SecureKeyPadManager();
        switch (runMode){
            case Constants.KSW_Activity_CertSign:
                txvTbTitle.setText("ęģĩëėļėĶė ėëŠ");
                //manaer.keyPadActivityOn(activity,Constants.KEYPAD_TYPE_QWERTY,"ęģĩëėļėĶė ėļėĶ","ëđë°ëēíļ ėë Ĩ","ėļėĶė ëđë°ëēíļė ëĢėīėĢžėļė",30,Constants.KSW_Activity_CertSign,"",6);
                Intent intent=new Intent(activity,QwertyView.class);
                intent.putExtra("maxNumChar",16);
                intent.putExtra("key","");
                intent.putExtra("title","ęģĩëėļėĶė ėļėĶ ėëŠ");
                intent.putExtra("sub_title","ęģĩëėļėĶė ėļėĶ ėëŠ");
                intent.putExtra("min",8);
                intent.putExtra("max",20);
                activity.startActivityForResult(intent, Constants.KSW_Activity_CertSign);
                break;
            case Constants.KSW_Activity_CertList:
                txvTbTitle.setText("ęģĩëėļėĶė ėĄ°í");
                break;
            case Constants.KSW_Activity_SCRAPING:
                try {
                    SASManager.initInstance();
                    SASManager.setContext(activity);
                    SASManager.setCryptoMode(false);
                    SASManager.setDebugMode(false);
                    SASManager.setV8Mode(true);

                    //ėëëĄėīë 10 ėīė ėĪė 
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        String defaultNpki = SASEnvironment.getString(SASEnvironment.LOCATIONOFCERTIFICATE); // ęļ°ëģļ ėļėĶė ė ėĨ ęē―ëĄ
                        String addNpki = "/data/data/kr.co.smartbank.app/NPKI/"; // ėąė ė ėĨë ėļėĶė ėėđ (ėėëĄ ėėąë ęē―ëĄ ėëëĪ.)  ėļėĶė íėęē―ëĄ ėķę° ėĪė  ( ';' ęĩŽëķėëĄ íėíėŽ ėķę°íĐëëĪ. )
                        SASEnvironment.setString(SASEnvironment.LOCATIONOFCERTIFICATE, defaultNpki + ";" + addNpki);// ęļ°ėĄī 'NPKI' íīëė ėëĄ ėķę°í ėļėĶė ė ėĨėėđëĄ ėļėĶėëĨž íėíĐëëĪ.
                    }
                    sasManager=SASManager.getInstance();
                } catch (SASException e) {
                    Logcat.dd("ėëŽėëëĪ");
                }
                txvTbTitle.setText("ęģĩëėļėĶė ėļėĶ");
               // manaer.keyPadActivityOn(activity,Constants.KEYPAD_TYPE_QWERTY,"ęģĩëėļėĶė ėļėĶ","ëđë°ëēíļ ėë Ĩ","ėļėĶė ëđë°ëēíļė ëĢėīėĢžėļė",30,Constants.KSW_Activity_SCRAPING,"",6);

                Intent intent2=new Intent(activity,QwertyView.class);
                intent2.putExtra("maxNumChar",16);
                intent2.putExtra("key","");
                intent2.putExtra("title","ęģĩëėļėĶė ėļėĶ ėĪíŽëĐí ėëŠ");
                intent2.putExtra("sub_title","ęģĩëėļėĶė ėļėĶ ėĪíŽëĐí ėëŠ");
                intent2.putExtra("min",8);
                intent2.putExtra("max",20);
                activity.startActivityForResult(intent2, Constants.KSW_Activity_SCRAPING);


                break;
            case Constants.REQUEST_SCRAPING_CAPTCHA_INPUT:
                Logcat.dd("ęģĩëėļėĶ ėėęĩŽëķ REQUEST_SCRAPING_CAPTCHA_INPUT");
                break;
            default:
                Logcat.dd("ęģĩëėļėĶ ėėęĩŽëķ ėė");
        }
    }
    protected void onResume() {
        super.onResume();
    }
    private AlertDialog DeleteConfirmDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                .setIcon(R.drawable.icon_normal)
                .setTitle("ėļėĶė ė­ė íėļ")
                .setMessage("ėļėĶėëĨž ė­ė  íėęē ėĩëęđ?")
                .setPositiveButton("íėļ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int ret = KSCertificateManager.deleteCert(userCert);
                        if (KSException.SUCC == ret){
                            Toast.makeText(activity, "ėļėĶėëĨž ėąęģĩė ėžëĄ ė­ė íėĩëëĪ.",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            //overridePendingTransition(R.anim.slide_up,R.anim.slide_bottom);
                        }
                        else{
                            Toast.makeText(activity, "ėļėĶė ė­ė ëĨž ėĪíĻíėėĩëëĪ.(" + ret + ")",
                                    Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("ė·Ļė", new DialogInterface.OnClickListener() {
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
            //ėĪíŽëí ėēëĶŽ
            commonDto.setSasManager(sasManager);
        }else if(runMode==Constants.KSW_Activity_CertSign){
            commonDto.singData=getIntent().getStringExtra("signData");
            commonDto.rbrno=getIntent().getStringExtra("rbrno");
        }
        new ActivityResult().onActivityResult(requestCode, resultCode, data,commonDto);
    }

    //ė ėėëŠ
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
//        String plainText="ėëŠíĐëëĪ";
//        byte[] signature = new byte[4096];
//        try {
//            signature = KSSign.sign(alg, userCert, plainText.getBytes(), pw);
//            byte[] signature2=KSSign.cmsSign(userCert,plainText.getBytes(),pw);
//            byte[] encodedSignResult;
//            encodedSignResult = KSBase64.encode(signature2);
//            String finalSignResult = new String(encodedSignResult);
//            return finalSignResult;
//        } catch (KSException  e) {
//            Logcat.dd("ėëŽėëëĪ");
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
