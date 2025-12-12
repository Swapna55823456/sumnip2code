package com.sunmi.payment.demo.page;

import static com.sunmi.payment.demo.constant.Constants.TAG;

import android.widget.TextView;

import com.android.architecture.helper.AppExecutors;
import com.hjq.toast.ToastUtils;
import com.sunmi.payment.SunmiPaymentKernel;
import com.sunmi.posrouter.utils.LogUtil;
import com.sunmi.protocol.sdk.utils.ThreadPoolManager;

/**
 * File describe:
 * Author: SuQi
 * Create date: 2024/8/1
 * Modify date: 2024/8/1
 * Version: 1
 */
public class BaseTransActivity extends BaseActivity {

    protected void startTrans(String requestData, TextView tvResult) {
        showLoading(this::cancelTrans);
        AppExecutors.getInstance().io().execute(() -> {
            try {
                boolean connectStatus = SunmiPaymentKernel.getInstance().checkConnectStatus();
                if (connectStatus) {
                    String result = SunmiPaymentKernel.getInstance().mPaymentManager.doTransaction(requestData);
                    LogUtil.e(TAG, "trans result: " + result);
                    AppExecutors.getInstance().main().execute(() -> {
                        hideLoading();
                        tvResult.setText(result);
                    });
                } else {
                    hideLoading();
                    AppExecutors.getInstance().main().execute(() -> ToastUtils.show("未连接"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    protected void cancelTrans() {
        ThreadPoolManager.executeInCachePool(() -> {
            try {
                boolean result = SunmiPaymentKernel.getInstance().mPaymentManager.cancelTransaction();
                LogUtil.e(TAG, "cancelTransaction result:" + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
