package kr.co.smartbank.app.solution.ksw.util;

public class KSW_Code {
    public static final String KSW_VERSION = "1.0.17";
    private static String encoding = "UTF-8";
    public static final int KSW_GARBAGE_COLLECTOR_CYCLE = 360000;
    public static final int KSW_CLOSE_MAIN = 101;
    public static final int KSW_CLOSE_CERTLIST = 102;
    public static final int KSW_CLOSE_BACK = 103;
    public static final int KSW_CLOSE_SUCC = 104;
    public static final int KSW_CLOSE_FAIL = 105;
    public static final int KSW_CLOSE_CANCEL = 106;
    public static final int KSW_SERVERCERT_IS_NULL = -4001;
    public static final int KSW_SID_IS_NULL = -4002;
    public static final int KSW_SERVERCERT_IS_INVALID = -4003;
    public static final int KSW_ENVELOPING_IS_FAILED = -4011;
    public static final int KSW_ENCRYPTEDDATALENGTH_IS_INVALID = -4012;
    public static final int KSW_REMOVINGPADDING_IS_FAILED = -4021;
    public static final int KSW_DECRYPTINGMAC_IS_FAILED = -4022;
    public static final int KSW_DECRYPTING_IS_FAILED = -4023;
    public static final int KSW_BASE64ENCODING_IS_FAILED = -4031;
    public static final int KSW_BASE64DECODING_IS_FAILED = -4032;
    public static final int KSW_MAKINGJSONDATA_IS_FAILED = -4033;
    public static final int KSW_MAKINGRANDOM_IS_FAILED = -4034;
    public static final int KSW_INSERTINGDB_IS_FAILED = -4103;
    public static final int KSW_SELECTINGDB_IS_FAILED = -4104;
    public static final int KSW_OLDPWD_IS_NULL = -4201;
    public static final int KSW_OLDPWD_ISNOT_MATCHED = -4202;
    public static final int KSW_OLDPWDANDNEWPWD_IS_SAME = -4203;
    public static final int KSW_NEWPWD_IS_NULL = -4204;
    public static final int KSW_CONFIRMPWD_IS_NULL = -4205;
    public static final int KSW_NEWPWDANDCONFIRMPWD_ISNOT_MATCHED = -4206;
    public static final int KSW_CHANGINGPWD_IS_FAILED = -4207;
    public static final int KSW_PWD_IS_NULL = -4208;
    public static final int KSW_PWD_IS_INVALID = -4209;
    public static final int KSW_IDN_IS_NULL = -4210;
    public static final int KSW_IDN_IS_INVALID = -4211;
    public static final int KSW_VERIFING_IS_FAILED = -4212;
    public static final int KSW_SIGNING_IS_FAILED = -4213;
    public static final int KSW_MAKINGCERTLIST_IS_FAILED = -4214;
    public static final int KSW_SIDLENGTH_IS_INVALID = -4215;
    public static final int KSW_PEMENCODING_IS_FAILED = -4216;
    public static final int KSW_PEMDECODING_IS_FAILED = -4217;
    public static final int KSW_ENCRYPTEDDATA_IS_NULL = -4218;
    public static final int KSW_DECRYPTEDDATA_IS_NULL = -4219;
    public static final int KSW_PLAINTEXT_IS_NULL = -4220;
    public static final int KSW_MTK_DECRYPTING_IS_FAILED = -4221;
    public static final int KSW_URLENCODING_IS_FAILED = -4222;
    public static final int KSW_URLDECODING_IS_FAILED = -4223;
    public static final int KSW_CALLBACKNAME_IS_NULL = -4224;
    public static final int KSW_OPTION_IS_NULL = -4225;
    public static final int KSW_ENCODING_IS_NULL = -4226;
    public static final int KSW_ARRAY_INDEX_OUT_OF_BOUNDS = -4227;

    public static String getEncoding()
    {
        return encoding;
    }

    public static void setEncoding(String encoding)
    {
        encoding = encoding;
    }
}