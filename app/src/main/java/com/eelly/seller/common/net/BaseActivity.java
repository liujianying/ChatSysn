package com.eelly.seller.common.net;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 项目Activity基础类
 *
 * @author 李欣
 */
public abstract class BaseActivity extends EellyBaseActivity {

    private Toast mToast;


    private boolean mOnTop = false;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 打开UI线程性能监控
        // TestUtil.openStrictModeThreadPolicy();
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mOnTop = true;

    }

    @Override
    protected void onPause() {
        mOnTop = false;

        super.onPause();
    }

    public boolean isOnTop() {
        return mOnTop;
    }


    /**
     * 显示提示
     *
     * @param content
     */
    protected void showToast(CharSequence content) {
        mToast.setText(content);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    /**
     * @param resId
     */
    protected void showToast(int resId) {
        showToast(getString(resId));
    }

    /**
     * 显示提示
     *
     * @param content
     * @param duration 显示时长
     */
    protected void showToast(CharSequence content, int duration) {
        mToast.setText(content);
        mToast.setDuration(duration);
        mToast.show();
    }

    /**
     * @param resId
     * @param duration
     */
    protected void showToast(int resId, int duration) {
        showToast(getString(resId), duration);
    }

    /**
     * 吐司 信息
     *
     * @param s
     */
    public void toastMessage(CharSequence s) {
        mToast.setText(s);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void toastMessage(int resId) {
        toastMessage(getString(resId));
    }
}
