package kr.co.smartbank.app.solution.ksw.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lumensoft.icrp.KSICRProtocol;
import com.lumensoft.ks.KSCertificate;
import com.lumensoft.ks.KSException;

import java.io.IOException;
import java.util.Hashtable;

import kr.co.smartbank.app.R;
import kr.co.smartbank.app.solution.ksw.util.KSW_AlertDialog;
import kr.co.smartbank.app.util.LoadingUtil;
import kr.co.smartbank.app.util.Logcat;
import kr.co.smartbank.app.view.BaseActivity;

public class KSW_Activity_ICRSImportCert extends BaseActivity  {
    private Activity activity;
    
    // 인증번호 서버등록 성공
    private final static String CODE_SUCC_R1 = "SC200";
    // 인증서 다운 성공
    private final static String CODE_SUCC_R2 = "SC201";
    // 인증서 다운 성공 with kmCert
    private final static String CODE_SUCC_R2_KM = "SC203";
    // 서버에 등록된 인증번호로 인증서가 존재하지 아니함
    private final static String CODE_FAIL_NULL_CERT = "PT115";
    private KSICRProtocol icrp;

    private KSCertificate userCert;
    private String copiedCertDn = "";

    private String userRandomNumber = "";
    private TextView txt_importnum;

    private String codeR1 = "";
    private String messageR1 = "";
    private String codeR2 = "";
    private String messageR2 = "";

    private final String ip="210.207.195.142";
    private final String port="10500";

    byte[] userCertByte;
    byte[] userKeyByte;
    byte[] userKmCertByte;
    byte[] userKmKeyByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ksw_activity_icrsimportcert);
        activity    =   this;


        //인증서 가져오기
        Button btn_importcert = findViewById(R.id.btn_importcert);
        btn_importcert.setOnClickListener(view -> {
            customProgressDialog.Hide();
            import2Start();
            LoadingUtil.INSTANCE.show(customProgressDialog);
        });

        View btn_cert_import_close = findViewById(R.id.btn_cert_import_close);
        btn_cert_import_close.setOnClickListener(view -> {
            activity.finish();
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom);
        });

        //인증번호 창
        txt_importnum= findViewById(R.id.txt_importnum);


        customProgressDialog.Show();
        //인증번호 가져오기
        new Handler().postDelayed(this::import1Start,1000);
        customProgressDialog.Hide();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //인증번호 가져오기
        new Handler().postDelayed(this::import1Start,1000);
        customProgressDialog.Hide();
    }


    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            runOnUiThread(() -> LoadingUtil.INSTANCE.show(customProgressDialog));
            switch (msg.what){
                case 1 :
                    String num1 = userRandomNumber.substring(0, 4);
                    String num2 = userRandomNumber.substring(4, 8);
                    String num3 = userRandomNumber.substring(8, 12);
                    Logcat.dd("인증번호"+num1 + " - " + num2 + " - " + num3);
                    txt_importnum.setText(num1 + " - " + num2 + " - " + num3);
                    Toast.makeText(activity,
                            "승인번호 생성이 성공하였습니다.",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    //String msg = "\"인증서 복사가 성공하였습니다.\\n받아온 인증서 DN : \"\n" + copiedCertDn + \"\\n받아온 인증서 정책 : \" + copiedCertPolicy";
                    String toastMsg = "인증서 저장 완료";
                    Toast.makeText(activity, toastMsg, Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> finish(), 1000);
                    break;
                case 3:
                    alertDialog(codeR1, messageR1);
                    break;
                case 4:
                    // 내부 오류
                    alertDialog(codeR2, messageR2);
                    break;
                case 5:
                    alertDialog("인증서저장", "인증서 저장에 문제가 생겼습니다.");
                    break;
                case 6:
                    import1Start(); // 동일한 인증번호가 서버에 존재시 다른 인증번호를 재생성하는 부분
                case 8:
                    alertDialog(codeR2, "PC에서 스마트폰 인증서 복사 을 위해 인증번호을 입력해주세요");
                    break;
                case 9:
                    Logcat.dd("인증서가져오기 통신오류");
                    alertDlgFinish("통신오류입니다 네트워크을 확인해주세요",activity);
                    break;
                case 10:
                    //btn_Import.setEnabled(false);
                    break;
                default:{

                }
            }
            customProgressDialog.Hide();
        }
    };



    public void import1Start() {
        new Thread(() -> {
            // 구 ICRP 생성자
            icrp = new KSICRProtocol(ip,
                    Integer.parseInt(port));

            // 신 ICRP 생성자 --> 증권사코드 입력 버전
            // KSICRProtocol(ip주소, port넘버, 증권사코드, sha256사용여부)
            // icrp = new KSICRProtocol(edittext_ip.getText().toString(),
            // Integer.parseInt(edittext_port.getText().toString()),
            // edittext_code.getText().toString().getBytes(), false);

            // 서버에 인증서 복사 요청(구/신버전 동일)
            Hashtable<?, ?> messageHashtable = icrp.import1();

            codeR1 = (String) messageHashtable.get("CODE");
            messageR1 = (String) messageHashtable.get("MESSAGE");
            userRandomNumber = (String) messageHashtable.get("RANDOMNUMBER");

            if (codeR1.equals(CODE_SUCC_R1)) {

                handler.sendMessage(handler.obtainMessage(1));

            } else {

                // 동일한 인덱스 존재 시 재요청
                if (codeR1.equals("PT118")) {
                    handler.sendMessage(handler.obtainMessage(6));
                } else {
                    handler.sendMessage(handler.obtainMessage(3));
                }
            }
        }).start();
    }

    public void import2Start() {
        new Thread(() -> {
            Hashtable<?, ?> messageHashtable = icrp.import2();

            codeR2 = (String) messageHashtable.get("CODE");
            messageR2 = (String) messageHashtable.get("MESSAGE");

            switch (codeR2) {
                case CODE_SUCC_R2:

                    userCertByte = (byte[]) messageHashtable.get("CERT");
                    userKeyByte = (byte[]) messageHashtable.get("KEY");

                    try {

                        icrp.saveCertAndKey(userCertByte, userKeyByte,
                                KSW_Activity_ICRSImportCert.this);
                        try {
                            // 인증서 Dn, policy를 추출 하기 위해 인증서 객체 생성
                            // byte[] 형 인증서를 통한 인증서 객체 생성을 할 경우
                            // 인증서 경로가 없음
                            userCert = new KSCertificate(userCertByte, "");
                            copiedCertDn = userCert.getSubjectDn();
                        } catch (KSException e) {
                            copiedCertDn = "";
                        }

                        handler.sendMessage(handler.obtainMessage(2));

                    } catch (IOException e) {
                        handler.sendMessage(handler.obtainMessage(5));
                    }

                    break;
                case CODE_SUCC_R2_KM:

                    userCertByte = (byte[]) messageHashtable.get("CERT");
                    userKeyByte = (byte[]) messageHashtable.get("KEY");
                    userKmCertByte = (byte[]) messageHashtable.get("KMCERT");
                    userKmKeyByte = (byte[]) messageHashtable.get("KMKEY");

                    try {
                        icrp.saveCertAndKey(userCertByte, userKeyByte,
                                userKmCertByte, userKmKeyByte, KSW_Activity_ICRSImportCert.this);
                        try {
                            // 인증서 Dn, policy를 추출 하기 위해 인증서 객체 생성
                            // byte[] 형 인증서를 통한 인증서 객체 생성을 할 경우
                            // 인증서 경로가 없음
                            userCert = new KSCertificate(userCertByte, "");
                            copiedCertDn = userCert.getSubjectDn();
                        } catch (KSException e) {
                            copiedCertDn = "";
                        }

                        handler.sendMessage(handler.obtainMessage(2));

                    } catch (IOException e) {
                        handler.sendMessage(handler.obtainMessage(5));
                    }
                    break;
                case CODE_FAIL_NULL_CERT:

                    handler.sendMessage(handler.obtainMessage(8));

                    break;
                default:

                    handler.sendMessage(handler.obtainMessage(4));

                    break;
            }
        }).start();
    }

    private AlertDialog alertDialog;
    private void alertDialog(String title, String msg) {
        KSW_AlertDialog skAlertDialog = new KSW_AlertDialog(activity,
                title, msg);
        AlertDialog.Builder builder = skAlertDialog.getAlertDialog();
        builder.setPositiveButton("확인", (dialog, whichButton) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();
    }
}
