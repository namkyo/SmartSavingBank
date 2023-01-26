package kr.co.smartbank.app.solution.everspin.secureKeypad;

import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import kr.co.everspin.eversafe.keypad.activity.ESSecureKeypadActivity;
import kr.co.everspin.eversafe.keypad.params.ESEditTextParams;
import kr.co.everspin.eversafe.keypad.params.ESKeypadParams;
import kr.co.everspin.eversafe.keypad.widget.ESEditText;
import kr.co.everspin.eversafe.keypad.widget.ESKeypadException;
import kr.co.everspin.eversafe.keypad.widget.params.ESSecureKeypadResult;
import kr.co.smartbank.app.R;
import kr.co.smartbank.app.config.Constants;
import kr.co.smartbank.app.util.CryptoUtil;
import kr.co.smartbank.app.util.Logcat;
import kr.co.smartbank.app.view.BaseActivity;

public class SecureKeyPadManager {
    public void keyPadActivityOn(Activity activity,int type,String title,String hint,String des,int max,int requestCode,String key,int min){
        Logcat.dd("보안키패드 실행 ====");
        ESEditTextParams.Builder builder = new ESEditTextParams.Builder();
        builder.setTitle(title);
        builder.setHintText(hint);
        builder.setDescription(des);
        builder.setMaxInputLength(max);
        builder.setKeyClickSound(true); //키버튼 클릭음 설정
        builder.setTargetLayoutId(R.layout.keypad_activity_pin); //키패드가 표시될 레이아웃 ID
        try{
            if(!"".equals(key)&&key!=null){
                Logcat.dd("RSA");
                builder.setEncryptMethodWithKey(ESKeypadParams.EncryptMethod.e_rsa,key);
            }else{
                //byte[] secureKey = { 'M', 'O', 'B', 'I', 'L', 'E', 'T', 'R', 'A', 'N', 'S', 'K', 'E', 'Y', '1', '0' };
                Logcat.dd("SEED");
                String SEED_KEY = "MobileTransKey10";
                builder.setEncryptMethodWithKey(ESKeypadParams.EncryptMethod.e_seed, SEED_KEY);
            }
        }catch (Exception e){
            Logcat.dd("키보드 암호화오류");
        }

        //builder.setTargetLayout(R.layout.keypad_activity_pin);
        switch (type){
            case Constants.KEYPAD_TYPE_QWERTY:
                builder.setKeypadType(ESKeypadParams.KeypadType.e_qwerty);
                break;
            case Constants.KEYPAD_TYPE_NUMBERIC:
                builder.setKeypadType(ESKeypadParams.KeypadType.e_numeric);
                break;
            case Constants.KEYPAD_TYPE_NUMBERIC_LINE:
                builder.setKeypadType(ESKeypadParams.KeypadType.e_numeric_line);
                break;
            default:
                Logcat.dd("보안키패드 타입 없음 ====");
        }
        builder.setOnKeypadListener(new ESEditText.OnKeypadListener() {
            @Override
            public void secureKeypadDone(ESEditText esEditText) {
                Logcat.dd("secureKeypadDone size : ");
            }
            @Override
            public void secureKeypadCancel() {
                Logcat.dd("secureKeypadCancel size : ");
            }
            @Override
            public void secureKeypadTextLengthChanged(ESEditText esEditText) {
                ESSecureKeypadResult result = esEditText.getSecureKeypadResult();
                Logcat.dd("secureKeypadTextLengthChanged size : " + result.getPlainText());
            }
        });
        builder.setOnKeypadActivityListener(new ESSecureKeypadActivity.ESSecureKeypadActivityBtnClickListener() {
            @Override
            public boolean onDoneBtnClick(ESSecureKeypadResult esSecureKeypadResult) {
                if (esSecureKeypadResult.getEnteredCharacters() < min) {
                    Logcat.dd("맥스 부족");
                    Toast.makeText(activity, min + "자리 이상 입력하세요", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
            @Override
            public void onCancelBtnClick() {
                activity.finish();
                //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom);
                Logcat.dd("보안키패드 닫기");
            }
        });
        Intent intent = ESSecureKeypadActivity.makeIntent(activity,builder.build());
        activity.startActivityForResult(intent,requestCode);
    }

    public String keyPadActivityOff(Intent data){
        ESSecureKeypadResult result = ESSecureKeypadActivity.getResult(data);
        if(result!=null){
            Logcat.dd("공동인증서 보안키패드 평문내용 : "+result.getPlainText());
            Logcat.dd("공동인증서 보안키패드 암호내용 : "+result.getEncryptedString());
            // using SecureKeypad input value
            // - ex. result.getEncryptedString()
            return result.getPlainText();
        }
        return "";
    }
    public String keyPadActivityOff_encryptedString(Intent data){
        ESSecureKeypadResult result = ESSecureKeypadActivity.getResult(data);
        if(result!=null){
            Logcat.dd("공동인증서 보안키패드 평문내용 : "+result.getPlainText());
            Logcat.dd("공동인증서 보안키패드 암호내용 : "+result.getEncryptedString());
            // using SecureKeypad input value
            // - ex. result.getEncryptedString()
            try {
                return CryptoUtil.getInstace().encrypt(result.getPlainText());
            }catch (Exception e){
                Logcat.dd("암호화오류");
            }
        }
        return "";
    }
    public String keyPadActivityOff_encryptedString2(Intent data){
        ESSecureKeypadResult result = ESSecureKeypadActivity.getResult(data);
        if(result!=null){
            Logcat.dd("공동인증서 보안키패드 평문내용 : "+result.getPlainText());
            Logcat.dd("공동인증서 보안키패드 암호내용 : "+result.getEncryptedString());
            // using SecureKeypad input value
            // - ex. result.getEncryptedString()
            try {
                return result.getEncryptedString();
            }catch (Exception e){
                Logcat.dd("암호화오류");
            }
        }
        return "";
    }

    public Map<String,String> keyPadActivityOff_enc(Intent data){
        ESSecureKeypadResult esResult = ESSecureKeypadActivity.getResult(data);
        Map<String,String> map = new HashMap<String,String>();
        if(esResult!=null){
            try{
                map.put("size",""+String.valueOf(esResult.getPlainText()).length());
                map.put("val",String.valueOf(CryptoUtil.getInstace().encrypt(esResult.getPlainText())));
                Logcat.dd("공동인증서 보안키패드 getEncryptedString : "+map.toString());
            }catch (Exception e){
                Logcat.dd("에러입니다");
            }
        }
        return map;
    }
    public String keyPadActivityOff_encval(Intent data){
        ESSecureKeypadResult esResult = ESSecureKeypadActivity.getResult(data);
        String result="";
        if(esResult!=null){
            try{
                result=CryptoUtil.getInstace().encrypt(esResult.getPlainText());
                Logcat.dd("공동인증서 보안키패드 getEncryptedString : "+result);
            }catch (Exception e){
                Logcat.dd("에러입니다");
            }
        }
        return result;
    }
}
