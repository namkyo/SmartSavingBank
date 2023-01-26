package kr.co.smartbank.app.util;


import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Base64Util {
    /**
     * * Base64 인코딩
     * */
    public static String getBase64encode(String content){
        return Base64.encodeToString(content.getBytes(), 0);
    }
    /**
     * * Base64 디코딩
     * */
    public static String getBase64decode(String content){
        return new String(Base64.decode(content, 0));
    }
    /**
     * * getURLDecode
     * */
    public static String getURLDecode(String content){
        try {
             return URLDecoder.decode(content, "utf-8"); // UTF-8
            //return URLDecoder.decode(content, "euc-kr"); // EUC-KR
        } catch (UnsupportedEncodingException e) {
            Logcat.dd("에러입니다");
        } return null;
    }
}
