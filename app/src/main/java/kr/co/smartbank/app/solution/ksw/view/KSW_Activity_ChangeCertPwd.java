package kr.co.smartbank.app.solution.ksw.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lumensoft.ks.KSCertificate;
import com.lumensoft.ks.KSCertificateManager;
import com.lumensoft.ks.KSException;
import com.signkorea.securedata.SecureData;

import java.util.Map;
import java.util.Objects;

import kr.co.smartbank.app.R;
import kr.co.smartbank.app.config.Constants;
import kr.co.smartbank.app.solution.everspin.secureKeypad.SecureKeyPadManager;
import kr.co.smartbank.app.solution.ksw.util.KSW_AlertDialog;
import kr.co.smartbank.app.solution.ksw.util.KSW_CertListManager;
import kr.co.smartbank.app.util.CryptoUtil;
import kr.co.smartbank.app.util.Logcat;
import kr.co.smartbank.app.view.BaseActivity;

public class KSW_Activity_ChangeCertPwd extends BaseActivity{
    private Activity activity;
    private EditText pwd1,pwd2,pwd3,pwd1Val,pwd2Val,pwd3Val;
    private KSCertificate userCert;
    private AlertDialog alertDlg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ksw_activity_changecertpwd);
        activity    =   this;

        userCert = KSW_CertListManager.getSelectedCert();
        Logcat.dd("userCert : "+userCert.getSubjectDn());


        pwd1 = findViewById(R.id.editText1);
        pwd1Val = findViewById(R.id.editText1_val);
        pwd1.setOnClickListener(view -> {
            pwd1.setText("");
            SecureKeyPadManager manaer = new SecureKeyPadManager();
            manaer.keyPadActivityOn(KSW_Activity_ChangeCertPwd.this, Constants.KEYPAD_TYPE_QWERTY,"현재 비밀번호 입력","비밀번호 입력","현재 비밀번호 입력을 넣어주세요",30,Constants.KSW_Activity_CHANGE_PWD_01,"",6);
        });
        pwd2 = findViewById(R.id.editText2);
        pwd2Val = findViewById(R.id.editText2_val);
        pwd2.setOnClickListener(view -> {
            pwd2.setText("");
            SecureKeyPadManager manaer = new SecureKeyPadManager();
            manaer.keyPadActivityOn(KSW_Activity_ChangeCertPwd.this, Constants.KEYPAD_TYPE_QWERTY,"새 비밀번호 입력","비밀번호 입력","새 비밀번호 입력을 넣어주세요",30,Constants.KSW_Activity_CHANGE_PWD_02,"",6);
        });
        pwd3 = (EditText) findViewById(R.id.editText3);
        pwd3Val = (EditText) findViewById(R.id.editText3_val);
        pwd3.setOnClickListener(view -> {
            pwd3.setText("");
            SecureKeyPadManager manaer = new SecureKeyPadManager();
            manaer.keyPadActivityOn(KSW_Activity_ChangeCertPwd.this, Constants.KEYPAD_TYPE_QWERTY,"새 비밀번호 입력 확인","비밀번호 입력 확인","새 비밀번호 입력 확인을 넣어주세요",30,Constants.KSW_Activity_CHANGE_PWD_03,"",8);
        });

        Button btnConfirm = (Button) findViewById(R.id.ksw_cspwd_next);
        btnConfirm.setOnClickListener(view -> {
            String oldPwd = pwd1Val.getText().toString();
            String newPwd = pwd2Val.getText().toString();
            String checkNewPwd = pwd3Val.getText().toString();
            //키파일 존재여부 확인
            if(!userCert.isKeyFileExist()) {
                //alertDlg("비밀번호 변경 실패", "키 파일이 존재하지 않습니다.");
                alertDlg("인증서 키 파일이 존재하지 않습니다.",activity);
                return;
            }

            //checkPwd(userCert, SecureData(oldPwd.getBytes()));

            if (oldPwd.equals("")) {
                //alertDlg("비밀번호 확인오류", "현재 인증서 비밀번호를 입력해 주십시오.");
                alertDlg("현재 인증서 비밀번호를 입력해 주십시오.",activity);
                return;
            } else if (newPwd.equals("")) {
                //alertDlg("비밀번호 확인오류", "변경할 인증서 비밀번호를 입력해 주십시오.");
                alertDlg("변경할 인증서 비밀번호를 입력해 주십시오.",activity);
                return;
            } else if (!checkNewPwd.equals(newPwd)) {
                //alertDlg("비밀번호 확인오류", "확인 비밀번호를 입력해 주십시오.");
                alertDlg("변경하실 비밀번호가 일치하지 않습니다 다시 입력해 주십시오.",activity);
                return;
            } else if (oldPwd.equals(newPwd)) {
                //alertDlg("비밀번호 확인오류", "확인 비밀번호를 입력해 주십시오.");
                alertDlg("같은 비밀번호는 사용할수 없습니다.",activity);
                return;
            }

            boolean isCorrectPassword = false;
            try{
                isCorrectPassword = KSCertificateManager.checkPwd(userCert, new SecureData(CryptoUtil.getInstace().decrypt(oldPwd).getBytes()));
            }catch (Exception e){
                Logcat.dd("복호화 오류");
            }
            if (!isCorrectPassword) {
                //alertDlg("비밀번호 확인오류", "현재 비밀번호가 올바르지 않습니다..");
                alertDlg("현재 비밀번호가 올바르지 않습니다.",activity);
                return;
            }

            int ret = 1;
            try{
                ret = KSCertificateManager.changePwd(userCert,
                        new SecureData(CryptoUtil.getInstace().decrypt(oldPwd).getBytes()), new SecureData(CryptoUtil.getInstace().decrypt(newPwd).getBytes()));
            }catch (Exception e){
                Logcat.dd("복호화 오류");
            }

            if (ret == KSException.SUCC) {
                alertDlgFinish("비밀번호 변경이 완료 되었습니다.", activity);
                //("비밀번호 변경 완료", "비밀번호 변경이 완료 되었습니다.");
            } else {//alertDlg("비밀번호 변경 실패", "비밀번호 변경에 실패 하였습니다.("+ Integer.toString(ret) + ")");
                alertDlg("비밀번호 변경에 실패 하였습니다.(" + ret + ")", activity);
            }
        });

        Button btnCancle = (Button) findViewById(R.id.ksw_cspwd_back);
        btnCancle.setOnClickListener(view -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AppCompatActivity.RESULT_OK == resultCode) {
            SecureKeyPadManager manaer = new SecureKeyPadManager();

            Map<String,String> result =manaer.keyPadActivityOff_enc(data);
            int size=Integer.parseInt(Objects.requireNonNull(result.get("size")));
            StringBuilder comma = new StringBuilder();
            for(int i=0;i<size;i++){
                comma.append("●");
            }
            switch (requestCode) {
                case Constants.KSW_Activity_CHANGE_PWD_01:
                    pwd1.setText(comma.toString());
                    pwd1Val.setText(String.valueOf(result.get("val")));
                    break;
                case Constants.KSW_Activity_CHANGE_PWD_02:
                    pwd2.setText(comma.toString());
                    pwd2Val.setText(String.valueOf(result.get("val")));
                    break;
                case Constants.KSW_Activity_CHANGE_PWD_03:
                    pwd3.setText(comma.toString());
                    pwd3Val.setText(String.valueOf(result.get("val")));
                    break;
                default:
                    Logcat.dd("KSW_Activity_ChangeCertPwd.onActivityResult : 테스트 후처리");

            }
        } else {
            Logcat.dd("화면작업 취소");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void alertDlg(String title, String msg) {

        KSW_AlertDialog skAlertDialog = new KSW_AlertDialog(
                KSW_Activity_ChangeCertPwd.this, title, msg);
        AlertDialog.Builder builder = skAlertDialog.getAlertDialog();
        builder.setPositiveButton("확인", (dialog, whichButton) -> alertDlg.dismiss());
        alertDlg = builder.create();
        alertDlg.show();
    }
}
