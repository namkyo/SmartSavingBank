package kr.co.smartbank.app.solution.ksw.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.lumensoft.ks.KSCertificate;

import java.util.Hashtable;


import kr.co.smartbank.app.R;
import kr.co.smartbank.app.view.BaseActivity;


public class KSW_Activity_Pwd_DecWireless extends BaseActivity implements OnClickListener, OnTouchListener {
    private Bundle bundle = null;
    private Intent intent = null;
    private TextView textView;
    private EditText editText;
    private Button button;
    private KSCertificate userCert = null;
    private String errMsg = "";
    private String finalReaultData;
    private String passWord;
    private byte[] signResult = null;
    private byte[] vidResult = null;
    private byte[] userCertPubKey = null;

    private String cipherData;  // 암호 데이터를 이전 액티비티로 전달하기 위해 전역변수 처리
    private int iRealDataLength;// 암호 데이터를 이전 액티비티로 전달하기 위해 전역변수 처리
    private byte[] secureKey = null;

    private String qwertyCipherString;
    private byte[] random = null;
    private String mTranskeyDummy;

    private Hashtable<?, ?> resultHashtable = new Hashtable<String, Object>();
    private String resultCode = "";
    private String resultMessage = "";

    private ProgressDialog dialog;

    private static final int REQUEST_CODE_NEW = 200;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ksw_activity_cspwd_mtk);

        intent = getIntent();
        bundle = intent.getBundleExtra("BUNDLE");

        TextView txvTbTitle = findViewById(R.id.txvTbTitle);
        txvTbTitle.setText("인증서 비밀번호 입력");

        //editText = (EditText) findViewById(R.id.edittext);
        editText.setOnTouchListener(this);

        button = findViewById(R.id.button);
        button.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        // 일시 중지된 모든 UI 업데이트나 스레드 혹은 액티비티에 의해 필요하지만 액티비티가 비활성화되면서 일시 중단된 처리를 재개한다.
        super.onResume();

        /*
        if (KSW_OptionManager.getIsAuto() == true && !KSW_OptionManager.getCachedDn().equals("") && !KSW_OptionManager.getCachedPw().equals("")) {
            try {
                userCert = KSW_CertListManager.getInstance().getCertificate(KSW_OptionManager.getCachedDn(), KSW_Activity_Pwd_DecWireless.this);
            } catch (RSKSWException e) {
                errMsg = "인증서 리스트 수령에 실패 하였습니다.";
                handler.sendMessage(handler.obtainMessage(-1));
            }
            qwertyCipherString = KSW_OptionManager.getCachedPw();
            makeValue();
        }
        */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 암복호화 HTTP 샘플
            case R.id.button:
                if (editText.getText().toString().length() == 0)
                    Toast.makeText(KSW_Activity_Pwd_DecWireless.this, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                else
                   // makeValue();
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            /* if (id == R.id.edittext) {
                editText.requestFocus();

                Intent newIntent = getIntentParam(
                        TransKeyActivity.mTK_TYPE_KEYPAD_QWERTY_LOWER,
                        TransKeyActivity.mTK_TYPE_TEXT_PASSWORD_IMAGE,
                        "인증서 비밀번호 입력",
                        "인증서 비밀번호를 입력하세요",
                        50, "최대 입력글자 50 자를 초과하였습니다.",
                        8, "최소 입력글자는 8 입니다.",
                        5, 20, false);
                startActivityForResult(newIntent, REQUEST_CODE_NEW);
                return true;

            } */
        }
        return false;
    }

    private void Dialog(String code, String msg) {
       // handler.sendMessage(handler.obtainMessage(44));
        Builder ad = new Builder(this);
        ad.setMessage("CODE : " + code + "\n" + " MSG : " + msg)
                .setCancelable(false)
                .setPositiveButton("CLOSE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
        AlertDialog alert = ad.create();
        alert.setTitle("Title");
        //alert.setIcon(R.drawable.icon);
        alert.show();
    }
}
