package com.sunmi.payment.demo.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.architecture.ui.page.BaseDialog;
import com.sunmi.payment.demo.R;
import com.sunmi.payment.demo.dialog.LoadingDialog;

public abstract class BaseActivity extends AppCompatActivity {

    protected final Handler handler = new Handler(Looper.getMainLooper());
    private BaseDialog mLoadingDialog;
    private LoadingDialog.Builder mLoadingDialogBuilder;

    protected void showToast(String text) {
        handler.post(() -> Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT).show());
    }

    protected void showToast(int resId) {
        handler.post(() -> Toast.makeText(BaseActivity.this, resId, Toast.LENGTH_SHORT).show());
    }

    public void openActivity(Class<? extends Activity> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    public void showLoading() {
        showLoading(getString(R.string.common_loading));
    }

    public void showLoading(String message) {
        showLoading(message, null);
    }

    public void showLoading(LoadingDialog.CloseListener closeListener) {
        showLoading(getString(R.string.common_loading), closeListener);
    }

    public void showLoading(String message, LoadingDialog.CloseListener closeListener) {
        if (mLoadingDialog == null) {
            mLoadingDialogBuilder = new LoadingDialog.Builder(this)
                    .setMessage(message)
                    .setCancelable(false);
            mLoadingDialog = mLoadingDialogBuilder.create();
        }
        if (mLoadingDialog.isShowing()) {
            mLoadingDialogBuilder.setMessage(message);
            mLoadingDialogBuilder.setCloseListener(closeListener);
        } else {
            mLoadingDialogBuilder.setMessage(message);
            mLoadingDialogBuilder.setCloseListener(closeListener);
            mLoadingDialog.show();
        }
    }

    public void hideLoading() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }

}
