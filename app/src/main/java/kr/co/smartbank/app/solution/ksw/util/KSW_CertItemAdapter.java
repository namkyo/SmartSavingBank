package kr.co.smartbank.app.solution.ksw.util;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lumensoft.ks.KSCertificate;
import com.lumensoft.ks.KSCertificateManager;
import com.lumensoft.ks.KSException;

import kr.co.smartbank.app.R;
import kr.co.smartbank.app.config.Constants;
import kr.co.smartbank.app.solution.ksw.view.KSW_Activity_CertManager;
import kr.co.smartbank.app.solution.ksw.view.KSW_Activity_ChangeCertPwd;
import kr.co.smartbank.app.util.Logcat;
import kr.co.smartbank.app.view.BaseActivity;


public class KSW_CertItemAdapter extends ArrayAdapter<KSW_CertItem> {
	private ArrayList<KSW_CertItem> certItem;
	private Activity activity;
	private Context context;
	private Button list_pw_change_btn;
	private Button list_del_btn;
	private ViewGroup cert_list_body;
	private int runMode;
	private Intent parmas;

	public KSW_CertItemAdapter(Context context, Activity activity, int textViewResourceId,
							   ArrayList<KSW_CertItem> items, int runMode,Intent parmas) {
		super(context, textViewResourceId, items);
		this.activity=activity;
		this.certItem = items;
		this.context = context;
		this.runMode=runMode;
		this.parmas=parmas;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.ksw_certitem, null);
		}

		//인증서정보
		KSW_CertItem item = certItem.get(position);
		KSCertificate userCert = (KSCertificate) item.userCert;

		//바디클릭 이벤트
		cert_list_body=(ViewGroup)v.findViewById(R.id.cert_list_body);
		cert_list_body.setTag(position);
		cert_list_body.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(certItem.get(position).userCert.isExpired()){
					Logcat.dd("cert_list_body yn : 만료");
					BaseActivity baseActivity=new BaseActivity();
					baseActivity.alertDlg("만료된 인증서입니다",activity);
				}else{
					Logcat.dd("cert_list_body yn : 유효");
					Logcat.dd("item click : "+position);
					Intent intent = new Intent(context,KSW_Activity_CertManager.class);
					switch (runMode){
						case Constants.KSW_Activity_CertSign:
							intent.putExtra(Constants.KSW_Activity,Constants.KSW_Activity_CertSign);
							intent.putExtra("signData",parmas.getStringExtra("signData"));
							intent.putExtra("rbrno",parmas.getStringExtra("rbrno"));
							// 선택된 인증서 SET
							KSW_CertListManager.setSelectedCert(userCert);
							activity.startActivityForResult(intent,Constants.KSW_Cert_Activity_02);
							//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom);
							break;
						case Constants.KSW_Activity_SCRAPING:
							intent.putExtra(Constants.KSW_Activity,Constants.KSW_Activity_SCRAPING);
							Logcat.dd("List parmas.getStringExtra(\"params\") "+parmas.getStringExtra("params"));
							intent.putExtra("params",parmas.getStringExtra("params"));
							// 선택된 인증서 SET
							KSW_CertListManager.setSelectedCert(userCert);

							activity.startActivityForResult(intent,Constants.KSW_Activity_02);
							//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom);
							break;
						case Constants.KSW_Activity_CertList:
							intent.putExtra(Constants.KSW_Activity,Constants.KSW_Activity_CertList);
							// 선택된 인증서 SET
							KSW_CertListManager.setSelectedCert(userCert);
							activity.startActivity(intent);
							//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom);
							break;
						// 선택된 인증서 SET
						default:
							Logcat.dd("공동인증 작업구분 없음");
					}


				}
			}
		});
		
		//패스워드 변경 이벤트
		list_pw_change_btn = (Button)v.findViewById(R.id.cert_list_pw_change_btn);
		list_pw_change_btn.setTag(position);
		list_pw_change_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Logcat.dd("cert_list_pw_change_btn");
				Intent intent = new Intent(context, KSW_Activity_ChangeCertPwd.class);
				KSW_CertListManager.setSelectedCert(userCert);
				activity.startActivity(intent);
				//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom);
			}
		});
		
		//삭제 이벤트
		list_del_btn = (Button)v.findViewById(R.id.cert_list_delete_btn);
		list_del_btn.setTag(position);
		list_del_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Logcat.dd("cert_list_delete_btn");
				Logcat.dd("인증서 : "+userCert.getSubjectDn());
				DeleteConfirmDialog(userCert,activity).show();
				//new BaseActivity().alertDlgFinish("test",activity);
			}
		});




		if (item != null) {
			TextView txtPolicy = (TextView) v.findViewById(R.id.txt_explain);
			TextView txtSubject = (TextView) v.findViewById(R.id.txt_subject);
			TextView txtIssuer = (TextView) v.findViewById(R.id.txt_issuer);
			TextView txtExpire = (TextView) v.findViewById(R.id.txt_expiredday);
			ImageView imgExpire = (ImageView) v.findViewById(R.id.img_cert);
			if (txtPolicy != null){
				txtPolicy.setText((String) item.get(KSW_CertItem.POLICY));
			}

			//사용자
			if (txtSubject != null) {
				String subjectName = (String) item.get(KSW_CertItem.SUBJECTNAME);
				txtSubject.setText(subjectName);
				if (subjectName.length() > 15) {
					txtSubject.setText(subjectName.substring(0, 15) + "...");
				} else {
					txtSubject.setText(subjectName);
				}
			}
			//발급자
			if (txtIssuer != null){
				txtIssuer.setText((String) item.get(KSW_CertItem.ISSUERNAME));
			}
			//만료일
			if (txtExpire != null){
				String day=String.valueOf(item.get(KSW_CertItem.EXPIREDTIME));
				SpannableString spannableString = new SpannableString(day);
				int start = 4;
				int end = day.length();
				spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //글씨 진하게
				spannableString.setSpan( new ForegroundColorSpan(Color.parseColor("#F28E34")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE ); //글씨색
				txtExpire.setText(spannableString);
			}
			if (imgExpire != null) {
				int val = ((Integer) item.get(KSW_CertItem.EXPIREDIMG)).intValue();
				Log.e("val: ", String.valueOf(val));
				if (val != 1){
					imgExpire.setImageResource(R.drawable.cert_run_no);// 만료예정 이미지 받을 경우 여기에 셋팅
					Button button = (Button)v.findViewById(R.id.cert_list_pw_change_btn);

					//만료된 인증서 비밀번호변경 버튼 감추기
					button.setVisibility(View.GONE);
				}
//				else if (val < 0)
//					imgExpire.setBackgroundResource(R.drawable.certlist_ic_expired);
//				else if (val > 0)
//					imgExpire.setBackgroundResource(R.drawable.certlist_ic_normal);
			}
		}
		return v;
	}

	private AlertDialog DeleteConfirmDialog(KSCertificate userCert,Activity activity) {
		int image;
		if(userCert.isExpired()){
			image=R.drawable.cert_run_no;
		}else{
			image=R.drawable.cert_run_yes;
		}
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
				.setIcon(image)
				.setTitle("인증서 삭제확인")
				.setMessage("인증서를 삭제 하시겠습니까?")
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						int ret = KSCertificateManager.deleteCert(userCert);
						if (KSException.SUCC == ret){
							Toast.makeText(activity, "인증서를 성공적으로 삭제했습니다.",
									Toast.LENGTH_LONG).show();

							activity.finish();
							//activity.overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom);
						}
						else{
							Toast.makeText(activity, "인증서 삭제를 실패하였습니다.(" + ret + ")",
									Toast.LENGTH_LONG).show();
							dialog.dismiss();
						}
					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});

		return dialog.create();
	}

}
