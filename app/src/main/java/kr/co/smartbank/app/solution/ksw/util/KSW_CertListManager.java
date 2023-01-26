package kr.co.smartbank.app.solution.ksw.util;

import com.lumensoft.ks.KSCertificate;

public class KSW_CertListManager {
//    private static KSW_CertListManager instance = null;
//    private static Vector<KSCertificate> userCerts = null;
    private static KSCertificate selectedCert = null;

    public static KSCertificate getSelectedCert() {
        return selectedCert;
    }

    public static void setSelectedCert(KSCertificate selectedCert) {
        KSW_CertListManager.selectedCert = selectedCert;
    }

//    static public KSW_CertListManager getInstance() {
//        if (instance == null) {
//            instance = new KSW_CertListManager();
//        }
//        return instance;
//    }

//    public Vector<KSCertificate> getCertificateList(Context context)
//            throws KSException {
//        try {
//            userCerts = KSCertificateLoader
//                    .getUserCertificateListWithGpki(context);
//
//            userCerts = KSCertificateLoader.FilterByExpiredTime(userCerts);
//            // Vector<KSCertificate> oidFilteredCertList = KSCertificateLoader
//            // .policyOidCertFilter(userCerts, "1.2.410.200005.1.1.4");
//
//            // Vector<KSCertificate> policyFilteredCertList =
//            // KSCertificateLoader
//            // .issuerCertFilter(userCerts,
//            // "cn=yessignCA Class 1,ou=AccreditedCA,o=yessign,c=kr");
//
//            return userCerts;
//
//        } catch (KSException e) {
//            throw new KSException(KSException.FAIL_MAKECERTLIST);
//        }
//    }
//
//    public Vector<KSCertificate> getCertificateList(String certDn,
//                                                    Context context) throws KSException {
//        try {
//            userCerts = KSCertificateLoader
//                    .getUserCertificateListWithGpki(context);
//            Vector<KSCertificate> newUserCertList = new Vector();
//
//            for (int i = 0; i < userCerts.size(); i++) {
//
//                KSCertificate ksCertificate = (KSCertificate) userCerts.get(i);
//
//                if (certDn != null) {
//                    if (ksCertificate.getSubjectDn().equals(certDn)) {
//
//                        newUserCertList.add(ksCertificate);
//
//                    }
//                }
//
//            }
//            return newUserCertList;
//
//        } catch (KSException e) {
//            throw new KSException(KSException.FAIL_MAKECERTLIST);
//        }
//    }
//
//    public KSCertificate getCertificate(String certDn, Context context)
//            throws KSException {
//        KSCertificate ksCertificate = null;
//        KSCertificate selectedCert = null;
//
//        try {
//            userCerts = KSCertificateLoader
//                    .getUserCertificateListWithGpki(context);
//
//            for (int i = 0; i < userCerts.size(); i++) {
//
//                ksCertificate = (KSCertificate) userCerts.get(i);
//
//                if (certDn != null) {
//                    if (ksCertificate.getSubjectDn().equals(certDn)) {
//                        selectedCert = ksCertificate;
//                    }
//                }
//            }
//
//        } catch (KSException e) {
//            throw new KSException(KSException.FAIL_MAKECERTLIST);
//        }
//        return selectedCert;
//    }
}
