package kr.co.smartbank.app.solution.ksw.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.lumensoft.ks.KSCertificate;

public class KSW_CertItem extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	public static final String POLICY = "policy";
	public static final String SUBJECTNAME = "subjectName";
	public static final String EXPIREDTIME = "expiredTime";
	public static final String ISSUERNAME = "issuerName";
	public static final String EXPIREDIMG = "expiredImg";

	public static final String EXPIREDYN = "EXPIREDYN";
	public KSCertificate userCert;
	public String userCertPath;
	boolean selectable = true;

	public KSW_CertItem(KSCertificate userCert) throws UnsupportedEncodingException {
		this.userCert = userCert;
		// this.put(POLICY, "구분 : " +
		// userCert.getPolicyNumString());//policy Number
		this.put(SUBJECTNAME, userCert.getSubjectDn());
		this.put(POLICY		, 	"구분자 " + userCert.getPolicy());
		this.put(ISSUERNAME	,	"발급자 " + userCert.getIssuerName());
		this.put(EXPIREDTIME,	"만료일 " + userCert.getExpiredTime());
		int expiredT = userCert.isExpiredTime();
		this.put(EXPIREDIMG, expiredT);
		this.put(EXPIREDYN, userCert.isExpired());
	}

	public KSCertificate getCertItem() {
		return userCert;
	}
}
