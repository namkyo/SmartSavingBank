package kr.co.smartbank.app.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class AlertUtil {
    public static void alertDlg(Activity activity,String msg){
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                //activity.finish();
            }
        });
    }

}
