package cn.gddiyi.cash.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class AndroidBug5497Workaround {
    public static final String TAG = "Bug5497";

    public static void assistActivity(Activity activity) {
        new AndroidBug5497Workaround(activity);
    }

    public static boolean isPlayAd =false;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private int contentHeight;
    private boolean isfirst = true;
    private Activity activity;
    private int statusBarHeight;

    public static void setIsPlayAd(boolean playAd) {
        isPlayAd = playAd;
    }

    private AndroidBug5497Workaround(Activity activity) {
        //获取状态栏的高度
        Log.d(TAG, "AndroidBug5497Workaround: "+isPlayAd);

        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        this.activity = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        //界面出现变动都会调用这个监听事件
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout: in");
                if (!isPlayAd) {
                    if (isfirst) {
                        contentHeight = mChildOfContent.getHeight();
                        isfirst = false;
                    }
                    Log.d(TAG, "onGlobalLayout:123 ");
                    possiblyResizeChildOfContent();
                }
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams)
                mChildOfContent.getLayoutParams();


    }
    //重新调整跟布局的高度
    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        //当前可见高度和上一次可见高度不一致 布局变动
        Log.d(TAG, "ChildOfContent: ");
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference + statusBarHeight;
                } else {
                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                }
            } else {
                frameLayoutParams.height = contentHeight;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    /**
     * 计算mChildOfContent可见高度 ** @return
     */
    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }
}
