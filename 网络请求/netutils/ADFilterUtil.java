package cn.gddiyi.cash.utils.netutils;

import android.content.Context;
import android.content.res.Resources;




/**
 * 出现部分广告弹出，过滤广告
 */
public class ADFilterUtil {
    public static  boolean booleanhasAd(Context context, String url){
            Resources res= context.getResources();
            String[]adUrls =res.getStringArray(cn.gddiyi.cash.cashier.R.array.adBlockUrl);
            for(String adUrl :adUrls){
                if(url.contains(adUrl)){
                    return true;
                }
            }
            return false;
        }
    }

