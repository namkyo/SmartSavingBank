package kr.co.smartbank.app.solution.ksw.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.lumensoft.ks.KSCertificate;
import com.lumensoft.ks.KSCertificateManager;

import java.util.ArrayList;

import kr.co.smartbank.app.R;

public class KSW_Init_CertInfo {
    private Context context = null;
    private Bundle bundle = null;
    private KSCertificate userCert = null;
    private Activity activity = null;
    public KSW_Init_CertInfo(Context context, Bundle bundle) {
        this.context = context;
        activity = (Activity) context;
        this.bundle = bundle;
    }
    public KSW_Init_CertInfo(Context context) {
        this.context = context;
        activity = (Activity) context;
    }

    public KSCertificate init() {

        userCert = KSW_CertListManager.getSelectedCert();
//		initCertList();
        //ImageView imgType = (ImageView) activity.findViewById(R.id.img_cert);
        /* 1. 고객명 */
        TextView subject = (TextView) activity.findViewById(R.id.txt_subject);
        subject.setText(userCert.getSubjectName());
        /* 2. 발급기관 */
        TextView issuer = (TextView) activity.findViewById(R.id.txt_issuer);
        issuer.setText(userCert.getIssuerOrg());
        /* 3. 인증서 종류 */
        TextView explain = (TextView) activity.findViewById(R.id.txt_explain);
        explain.setText(userCert.getPolicy());
        /* 4. 발급일 */
        TextView desc6 = (TextView) activity.findViewById(R.id.desc_6);
        desc6.setText(userCert.getNotBefore());
        /* 5. 만료일 */
        TextView desc7 = (TextView) activity.findViewById(R.id.desc_7);
        desc7.setText(userCert.getNotAfter());

        userCert = KSW_CertListManager.getSelectedCert();
        //expiredDay.setText("만료일 : " + userCert.getNotAfterDate());

        int expiredTime = userCert.isExpiredTime();
        // 1(RSKSWCertificate.RSKSWConstCertExpModeNORMAL): 갱신가능 일자까지 남음
        // 0(RSKSWCertificate.RSKSWConstCertExpModeALMOST): 갱신가능 일자
        // -1(RSKSWCertificate.RSKSWConstCertExpModeEXPIRED): 만료됨

//		if (expiredTime == RSKSWCertificate.RSKSWConstCertExpModeALMOST) {
//			imgType.setBackgroundResource(R.drawable.certlist_ic_normal);	// 만료예정 이미지 받을 경우 여기에 셋팅
//		} else if (expiredTime == RSKSWCertificate.RSKSWConstCertExpModeEXPIRED) {
//			imgType.setBackgroundResource(R.drawable.certlist_ic_expired);
//		} else {
//			imgType.setBackgroundResource(R.drawable.certlist_ic_normal);
//		}
        return userCert;
    }
/*
    private void initCertList() {
        ArrayList<?> vCert = null;
        try {
            // vCert = KSCertificateLoader.getUserCertificateList(context);
            vCert = KSCertificateManager.getInstance(context).getArrCert();

            userCert = KSW_CertListManager.getSelectedCert();
        } catch (RSKSWException e1) {
            System.out.println("예외발생");
        }
    }
*/
    public KSCertificate getUserCert() {
        return userCert;
    }

}