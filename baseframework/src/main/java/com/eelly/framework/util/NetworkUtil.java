package com.eelly.framework.util;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

/**
 * 网络工具类
 */
public class NetworkUtil {

    public static final String WIFI = "wifi";
    public static final String G2 = "2G";
    public static final String G3 = "3G";
    public static final String G4 = "4G";

    /**
     * 获得当前的网络信息
     *
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    /**
     * 获得当前的网络类型,返回值(0:移动网络,1:Wifi网络,-1:没有可用网络)
     *
     * @param context
     * @return
     */
    private static int getNetworkType(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return (networkInfo == null || !networkInfo.isConnected()) ? -1 : networkInfo.getType();
    }

    /**
     * 是否可以获得网络
     *
     * @param context
     * @return
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo netinfo = getNetworkInfo(context);
        return netinfo == null ? false : netinfo.isAvailable();
    }

    /**
     * 网络是否已连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        NetworkInfo netinfo = getNetworkInfo(context);
        return netinfo == null ? false : netinfo.isConnected();
    }

    /**
     * 网络是否正在连接中
     *
     * @param context
     * @return
     */
    public static boolean isConnecting(Context context) {
        NetworkInfo netinfo = getNetworkInfo(context);
        return netinfo == null ? false : netinfo.getState() == NetworkInfo.State.CONNECTING;
    }

    /**
     * 当前正在使用wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        return getNetworkType(context) == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 当前正在使用移动网络(2G,3G)
     *
     * @param context
     * @return
     */
    public static boolean isMobile(Context context) {
        return getNetworkType(context) == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 获得移动网络代理
     *
     * @param context
     * @return
     */
    public static Proxy getAPNProxy(Context context, Proxy.Type type) {
        InetSocketAddress address = getAPNInetSocketAddress(context);
        if (address == null) return null;
        return new Proxy(type, address);
    }

    /**
     * 获得移动网络地址
     *
     * @param context
     * @return
     */
    public static InetSocketAddress getAPNInetSocketAddress(Context context) {
        final Uri uri = Uri.parse("content://telephony/carriers/preferapn");
        final Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            final String address = cursor.getString(cursor.getColumnIndex("proxy"));
            final String port = cursor.getString(cursor.getColumnIndex("port"));
            if (address != null && address.trim().length() > 0)
                return new InetSocketAddress(address, Integer.getInteger(port, 80));
        }
        return null;
    }


    /*****
     * 获取网络的类型：2G,3G,4G,wifi
     *
     * @param context
     * @return 2G, 3G, 4G, wifi，如果找不到或没有网络返回 ""
     */
    public static String getNetworkTypeStr(Context context) {
        if (context == null) {
            return "";
        }
        String strNetworkType = "";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return "";
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                //
                strNetworkType = WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                //Log.e("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = G2;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = G3;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = G4;
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = G3;
                        } else {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

                //Log.e("cocos2d-x", "Network getSubtype : " + Integer.valueOf(networkType).toString());
            }
        }

        //Log.e("cocos2d-x", "Network Type : " + strNetworkType);

        return strNetworkType;
    }


    /****
     * 获取手机网络的ip地址
     *
     * @return
     */
    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /****
     * 获取手机WIFI网络的DNS地址
     *
     * @return
     */
    public static String getWIFIDNS(Context context) {
        String dns = "";
        try {
            if (isWifi(context)) {
                WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfor = wm.getConnectionInfo();
                dns = intToIp(wm.getDhcpInfo().dns1);
            }
        } catch (Exception e) {
        }
        return dns;
    }

    /****
     * 转成ip形式
     *
     * @param paramInt
     * @return
     */
    private static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }


    /**
     * 获取公网ip
     *
     * @return
     */
    public static String getNetIp(String url) {
        URL infoUrl = null;
        InputStream inStream = null;
        try {
            infoUrl = new URL(url);
            URLConnection connection = infoUrl.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setConnectTimeout(5000);
            httpConnection.setReadTimeout(5000);
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "gb2312"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    strber.append(line + "\n");
                }
                inStream.close();

                //从反馈的结果中提取出IP地址
                int start = strber.indexOf("[");
                int end = strber.indexOf("]", start + 1);
                line = strber.substring(start + 1, end);
                return line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
