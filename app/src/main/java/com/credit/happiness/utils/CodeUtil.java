package com.credit.happiness.utils;

import android.text.TextUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CodeUtil {
    /**
     * 编码 UTF-8
     */
    private final String CODE_TYPE = "UTF-8";
    private Cipher cipherEncode = null;
    private Cipher cipherDecode = null;
//	private static Pattern p = Pattern.compile("\\s*|\t|\r|\n");

    public CodeUtil(String strKey, String strIV) {
        init(strKey, strIV);
    }

    public void init(String strKey, String strIV) {
        if (strKey.length() < 16) {
            throw new RuntimeException("k length less than 16");
        }
        if (strIV.length() < 16) {
            throw new RuntimeException("iv length less than 16");
        }
        try {
            strKey = strKey.substring(0, 16);
            strIV = strIV.substring(0, 16);

            byte[] keyBytes = strKey.getBytes(CODE_TYPE);
            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
            byte[] strIVBytes = strIV.getBytes(CODE_TYPE);
            IvParameterSpec iv = new IvParameterSpec(strIVBytes);

            this.cipherEncode = Cipher
                    .getInstance("AES/CBC/PKCS7Padding", "BC");// 创建加密密码器
            this.cipherEncode.init(Cipher.ENCRYPT_MODE, key, iv);// 初始化
            this.cipherDecode = Cipher
                    .getInstance("AES/CBC/PKCS7Padding", "BC");// 创建解密密码器
            this.cipherDecode.init(Cipher.DECRYPT_MODE, key, iv);// 初始化
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密
     *
     * @param strEncode
     * @return
     */
    public String encode(String strEncode) {
        if (this.cipherEncode == null) {
            return strEncode;
        }
        if (strEncode == null || strEncode.equals("") || strEncode.length() == 0) {
            return strEncode;
        }
        try {
            byte[] byteContent = strEncode.getBytes(CODE_TYPE);
            byte[] bysResult = this.cipherEncode.doFinal(byteContent);

            String strResult = Base64Encoder.encode(bysResult, CODE_TYPE);
            return strResult;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 解密
     *
     * @param strDecode
     * @return
     */
    public String decode(String strDecode) {
        if (this.cipherEncode == null) {
            return strDecode;
        }

        byte[] bysEncoded = Base64Decoder.decodeToBytes(strDecode, CODE_TYPE);

        try {
            byte[] bysResult = this.cipherDecode.doFinal(bysEncoded);

            String strResult = new String(bysResult, CODE_TYPE);
            return strResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 去掉换行符  去空格
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (!TextUtils.isEmpty(str)) {
//			Matcher m = p.matcher(str);
//			dest = m.replaceAll("");
            dest = str.replace(" ", "").replace("\n", "");
        }
        return dest;
    }
}
