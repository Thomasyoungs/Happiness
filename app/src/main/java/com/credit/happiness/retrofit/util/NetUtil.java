package com.credit.happiness.retrofit.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class NetUtil {
    public final static String BASE_HOST = "https://static.51dafenqi.com";
    //    public final static String BASE_URL = "http://toolapi.dev.amazingday.cn";
    public final static String BASE_URL = "https://cms.51dafenqi.com/advertisement/";
    public static String serviceUrl = "http://file.dafy.com.cn/Servlet/imageUpload.svl";

    /**
     * 首页广告
     */
    public final static String NET_ADVERTISING_NEW = "https://cms.51dafenqi.com/advertisement/findAdByPositionList.do";


    /**
     * 启动页广告
     */
    public final static String NET_ADVERTISING = "https://cms.51dafenqi.com/advertisement/findStartImg.do";
//    public static final String NET_ADVERTISING1 = "http://192.168.5.25:8080/advertisement/findStartImg.do";

    /**
     * 请求首页团品
     */
//    public static final String NET_RECOMMEND_BUSINESS = "http://192.168.5.161:6300/es/merchant/findMerchant.do";
    public static final String NET_GROUP_BUYS = "https://stagees.dafy.com/goods/popularGroupPurchase.do";
    /**
     * 请求首页为你推荐
     */
//    public static final String NET_RECOMED = "http://192.168.5.83:/goods/recommendedForYou.do";
    public static final String NET_RECOMED = "https://stagees.dafy.com/goods/recommendedForYou.do";

    /**
     * 请求推荐商家地址
     */
    public static final String NET_RECOMMEND_BUSINESS = "https://stagees.dafy.com/merchant/findMerchant.do";
    /**
     * 请求个人中心数据
     */
    public static final String NET_USER_INFO = "https://user.51dafenqi.com/user/findUserInformation.do";
//    public static final String NET_USER_INFO1 = "http://192.168.5.25:8089/user/findUserInformation.do";

    /**
     * 请求推荐城市列表
     */
    public static final String NET_RECOMMEND_CITY = "https://cms.51dafenqi.com/merchantCity/findrecommendedCity.do";
    /**
     * 请求城市列表
     */
    public static final String NET_CITY_LIST = "https://cms.51dafenqi.com/merchantCity/recommendCityAndList.do";
    /**
     * 扫码
     */
    public static final String NET_SCAN = "https://user.51dafenqi.com/userInformation/checkIsMerchantOrEducation.do";
//    public static final String NET_SCAN1 = "http://192.168.5.172:8080/userInformation/checkIsMerchantOrEducation.do";

    //    public static final String NET_CREDITDATA = "https://user.51dafenqi.com/userInformation/submitAllForm.do";
    public static final String NET_FINDALLFORM = "https://user.51dafenqi.com/userInformation/findAllForm.do.do";

    public static final String NET_CREDITDATA = "https://user.51dafenqi.com/userInformation/submitAllForm.do";
//    public static final String NET_CREDITDATA = "http://192.168.5.172:8080/userInformation/submitAllForm.do";
//    public static final String NET_FINDALLFORM = "http://192.168.5.172:8080/userInformation/findAllForm.do.do";


    /**
     * lbs
     */
    public static final String NET_LBS = "https://user.51dafenqi.com/userInformation/checkLBS.do";
    /**
     * 检查定位城市是否开通达分期
     */
    public static final String NET_CHECK_LOCATION_CITY_STATUS = "https://cms.51dafenqi.com/merchantCity/findCityIsActive.do";

//    /**
//     * 检查城市是否开通达分期
//     */
//    public static final String NET_CHECK_CITY_STATUS = "https://stagees.dafy.com/merchant/findIsHaveMerchant.do";

    /**
     * 检查app是否有更新
     */
    public static final String NET_CHECK_APP_UPDATE = "https://upgradeweb.51dafenqi.com/upgrade/findApkNewUpgrade.do";
//    public static final String NET_CHECK_APP_UPDATE = "http://192.168.5.83:4242/upgrade/findApkNewUpgrade.do";
    /**
     * 检查H5是否有更新
     */
    public static final String NET_CHECK_H5_UPDATE = "https://upgradeweb.51dafenqi.com/upgrade/wrapUpgrade.do";

    /**
     * 发送验证码
     */
    public static final String NET_SEND_VERIFY = "https://user.51dafenqi.com/userRegister/registerMobileCode.do";
    /**
     * 发送验证码
     */
    public static final String NET_SEND_VERIFY_FORGET = "https://user.51dafenqi.com/userRegister/sendMobileCode.do";
//    public static final String NET_SEND_VERIFY_FORGET = "http://192.168.5.10:8089/userRegister/sendMobileCode.do";

    /**
     * 图形验证码
     */
//    public static final String NET_GRAPHIC = "http://192.168.5.10:8089/userRegister/testaaa.do";//发送图形验证码
//    public static final String NET_GRAPHIC = "http://192.168.5.10:8089/userRegister/getVerrificationImg.do";
    public static final String NET_GRAPHIC = "https://user.51dafenqi.com/userRegister/getVerrificationImg.do";
    //    public static final String NET_GRAPHIC_VERIFY = "http://192.168.5.10:8089/userRegister/verifyAppImgCode.do";
    public static final String NET_GRAPHIC_VERIFY = "https://user.51dafenqi.com/userRegister/verifyAppImgCode.do";
    /**
     * 注册
     */
    public static final String NET_REGISTER = "https://user.51dafenqi.com/userRegister/register.do";
    public static final String NET_CHECKMSM = "https://user.51dafenqi.com/userRegister/verifyRegisterMobileCode.do";//验证手机号与验证码
//    public static final String NET_CHECKMSM = "http://192.168.5.10:8089/userRegister/verifyRegisterMobileCode.do";//验证手机号与验证码
//    public static final String NET_REGISTER = "http://192.168.5.161:8089/userRegister/register.do";

    /**
     * 找回密码相关
     */
    public static final String NET_VERIFYMOBILE = "https://user.51dafenqi.com/userQueryPassword/qryUserIdByMobile.do";
    //    public static final String NET_VERIFYMOBILE = "http://192.168.5.10:8089/userQueryPassword/qryUserIdByMobile.do";
    public static final String NET_FORGET_VERIFY = "https://user.51dafenqi.com/userQueryPassword/verifySendMobileCode.do";
    public static final String NET_RETRIEVE = "https://user.51dafenqi.com/userQueryPassword/retrievePassword1.do";
    //    public static final String NET_RETRIEVE = "http://192.168.5.10:8089//userQueryPassword/retrievePassword1.do";
    public static final String NET_RETRIEVE2 = "https://user.51dafenqi.com/userQueryPassword/retrievePassword2.do";
    public static final String NET_RETRIEVE_INDENTIFY = "https://user.51dafenqi.com/userQueryPassword/verifyStrIdentity.do";
//    public static final String NET_REGISTER = "http://192.168.5.161:8089/userRegister/register.do";
    /**
     * 新-预登陆
     */
    public static final String NET_PREPARE_LOGIN = "https://user.51dafenqi.com/userRegister/beforeLogin.do";
//    public static final String NET_PREPARE_LOGIN = "http://192.168.5.10:8089/userRegister/beforeLogin.do";
    /**
     * 新-登陆
     */
    public static String NET_LOGIN = "https://user.51dafenqi.com/userRegister/appDfqLogin.do";
    //    public static String NET_LOGIN = "http://192.168.5.10:8089/userRegister/appDfqLogin.do";
    //    public static String NET_LOGIN = "http://192.168.5.161:8089/userRegister/appLogin.do";
    public static String NET_LOGIN2 = "https://user.51dafenqi.com/userRegister/appLogin.do";
    /**
     * 新-zip更新
     */
    public static String NET_CHECK_ZIP_UPDATE = "https://upgradeweb.51dafenqi.com/upgrade/newZipUpgrade.do";
//    public static String NET_CHECK_ZIP_UPDATE1 = "http://192.168.5.83:4242/upgrade/newZipUpgrade.do";

    /**
     * 请求版本信息的地址
     */
//    public static final String DOWNLOAD_VERSION_ADRESS = "https://upgrade.dafy.com";
    //所有接口是否加密


    /**
     * 查询是否借记卡，参数strCardNum
     */
    public static String CHECK_DEBIT_CARD = "https://user.51dafenqi.com/userBankInformation/checkDebitCard.do";
//    public static String CHECK_DEBIT_CARD1 = "http://192.168.5.172:8080/userBankInformation/checkDebitCard.do";

    /**
     * 查询是否信用卡，参数strCardNum
     */
    public static String CHECK_CREDIT_CARD = "https://user.51dafenqi.com/userBankInformation/checkCreditCard.do";

    /**
     * 加密秘钥获取
     */
    public static String NET_ENCRYPT = "https://upgradeweb.51dafenqi.com/encryptionKey/queryDfqSecurity.do";
    //    public static String NET_ENCRYPT = "http://192.168.5.83:9191/encryptionWeb/findEncryptionBysecretType.do";
    //face++地址
    public static String FACECHECK = "https://user.51dafenqi.com/userInformation/face.do";
    /**
     * 加密测试
     */
    public static String test = "http://192.168.5.25:8089/user/findUserInformation.do";
    public static String test1 = "http://192.168.5.25:8089/user/testMi.do";

    /**
     * 加密秘钥类型
     */
    public static String secretType = "2";

    /**
     * 加密标识
     */
    public static boolean isEncrypt = true;

    /**
     * 加密key
     */
    public static String encryptKey = "";
    /**
     * 没有连接网络
     */
    public static final int NETWORK_NONE = -1;
    public static final String NETWORK_NONE_TOAST = "无网络";
    /**
     * 移动网络
     */
    public static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    public static final int NETWORK_WIFI = 1;

    public static final String deviceChannel = "2";//app 终端渠道2 标识独立app客户端

    public static String encryptPrimary = "1.1.4";
    public static String encryptSubsidiary = "dfq";

    public static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    /**
     * 忽略https证书验证
     *
     * @return
     */
    public static SSLParams getSSLSocketFactory() {
        SSLSocketFactory sslSocketFactory = null;
        SSLParams sslParams = new SSLParams();
        X509TrustManager xtm = new X509TrustManager() {
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();

            sslParams.sSLSocketFactory = sslSocketFactory;
            sslParams.trustManager = xtm;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslParams;
    }

    public static class SSLParams {
        public SSLSocketFactory sSLSocketFactory;
        public X509TrustManager trustManager;
    }
}
