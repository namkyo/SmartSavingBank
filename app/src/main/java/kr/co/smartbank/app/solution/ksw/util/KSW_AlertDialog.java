package kr.co.smartbank.app.solution.ksw.util;

import android.content.Context;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import kr.co.smartbank.app.R;

public class KSW_AlertDialog {

    Context context;
    Builder dialog;

    public KSW_AlertDialog(Context context, String title, String msg) {
        this.context = context;
        dialog = new Builder(context).setIcon(R.drawable.cert_run_yes)
                .setTitle(title).setMessage(msg);
    }

    public Builder getAlertDialog() {
        return dialog;
    }

    // *****
    // 사용방법
    // *****
    // AlertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener()
    // {
    // public void onClick(DialogInterface dialog, int whichButton) {
    //
    // 확인버튼 눌렀을 때 동작
    // finish();
    //
    // }
    // });
    // *****
}
