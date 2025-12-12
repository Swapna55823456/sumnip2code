package com.sunmi.payment.demo.page.manager;

import static com.sunmi.payment.demo.constant.Constants.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hjq.bar.TitleBar;
import com.hjq.toast.ToastUtils;
import com.pos.connection.bridge.binder.ECRConstant;
import com.pos.connection.bridge.binder.ECRParameters;
import com.sunmi.payment.SunmiPaymentKernel;
import com.sunmi.payment.demo.R;
import com.sunmi.payment.demo.constant.Constants;
import com.sunmi.payment.demo.dialog.InputIpPortDialog;
import com.sunmi.payment.demo.helper.PaymentHelper;
import com.sunmi.payment.demo.page.BaseActivity;
import com.sunmi.payment.demo.utils.CacheHelper;
import com.sunmi.payment.demo.utils.OnTitleBarListenerWrapper;
import com.sunmi.posrouter.utils.LogUtil;

public class ConnectActivity extends BaseActivity {

    private TextView tvConnectStatus, tvConnectType, tvConnectDevice;
    private TextView tvDisconnect;
    private boolean isConnected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnTitleBarListener(new OnTitleBarListenerWrapper() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }
        });
        tvConnectStatus = findViewById(R.id.tv_connect_status);
        tvConnectType = findViewById(R.id.tvConnect_type);
        tvConnectDevice = findViewById(R.id.tv_connect_device);
        tvDisconnect = findViewById(R.id.tv_disconnect);
        tvDisconnect.setOnClickListener(view -> disConnect());
        findViewById(R.id.ll_local).setOnClickListener(view -> connectLocal());
        findViewById(R.id.ll_usb).setOnClickListener(view -> connectUsb());
        findViewById(R.id.ll_wifi).setOnClickListener(view -> connectWifi());
        findViewById(R.id.bt_check_connect_status).setOnClickListener(view -> checkConnectStatus());
        isConnected = SunmiPaymentKernel.getInstance().mPaymentManager != null;
        refreshUI(isConnected);
    }

    private final SunmiPaymentKernel.ConnectCallback connectCallback = new SunmiPaymentKernel.ConnectCallback() {
        @Override
        public void onConnect() {
            hideLoading();
            refreshUI(true);
        }

        @Override
        public void onDisconnect(int code, String msg) {
            hideLoading();
            refreshUI(false);
        }
    };

    private void checkConnectStatus() {
        new Thread() {
            @Override
            public void run() {
                boolean checkConnectStatus = SunmiPaymentKernel.getInstance().checkConnectStatus();
                LogUtil.d(TAG, "checkConnectStatus:" + checkConnectStatus);
                ToastUtils.show("checkConnectStatus:" + checkConnectStatus);
            }
        }.start();
    }


    private void disConnect() {
        if (!isConnected) {
            return;
        }
        isConnected = false;
        SunmiPaymentKernel.getInstance().disconnect();
        refreshUI(false);
    }

    private void connectLocal() {
        if (isConnected) {
            return;
        }
        CacheHelper.saveString(Constants.Connect.PAYMENT_CONNECT_TYPE, Constants.ConnectType.LOCAL);
        CacheHelper.saveString(Constants.Connect.PAYMENT_CONNECT_DEVICES, "--");
        Bundle config = new Bundle();
        config.putBoolean("ecrMode", false);
        config.putInt("timeout", 10);
        initPayment(config);
    }

    private void connectUsb() {
        if (isConnected) {
            return;
        }
        CacheHelper.saveString(Constants.Connect.PAYMENT_CONNECT_TYPE, Constants.ConnectType.USB);
        CacheHelper.saveString(Constants.Connect.PAYMENT_CONNECT_DEVICES, "--");
        Bundle config = new Bundle();
        config.putBoolean("ecrMode", true);
        config.putString(ECRParameters.MODE, ECRConstant.Mode.USB);
        config.putInt("timeout", 10);
        initPayment(config);
    }

    private void connectWifi() {
        if (isConnected) {
            return;
        }
        showInputIpPortDialog();
    }

    private void showInputIpPortDialog() {
        new InputIpPortDialog.Builder(this)
                .title("Please enter IP and port")
                .ip(CacheHelper.getString(Constants.Connect.PAYMENT_CONNECT_WIFI_IP))
                .port(CacheHelper.getString(Constants.Connect.PAYMENT_CONNECT_WIFI_PORT, "3888"))
                .onConfirm2((ip, port) -> {
                    Log.e(TAG, "ip: " + ip + ", port: " + port);
                    CacheHelper.saveString(Constants.Connect.PAYMENT_CONNECT_TYPE, Constants.ConnectType.WIFI);
                    CacheHelper.saveString(Constants.Connect.PAYMENT_CONNECT_DEVICES, "--");
                    CacheHelper.saveString(Constants.Connect.PAYMENT_CONNECT_WIFI_IP, ip);
                    CacheHelper.saveString(Constants.Connect.PAYMENT_CONNECT_WIFI_PORT, port);
                    Bundle config = new Bundle();
                    config.putBoolean("ecrMode", true);
                    config.putString(ECRParameters.MODE, ECRConstant.Mode.WIFI);
                    config.putString(ECRParameters.WIFI_ADDRESS, ip);
                    config.putInt(ECRParameters.WIFI_PORT, Integer.parseInt(port));
                    initPayment(config);
                    return null;
                })
                .setCancelable(false)
                .show();
    }

    private void initPayment(Bundle config) {
        showLoading("Connecting...");
        PaymentHelper.connectPayment(config, connectCallback);
    }

    public void refreshUI(boolean isConnected) {
        this.isConnected = isConnected;
        runOnUiThread(() -> {
            CacheHelper.saveBool(Constants.Connect.PAYMENT_CONNECT_STATUS, isConnected);
            String connectType = CacheHelper.getString(Constants.Connect.PAYMENT_CONNECT_TYPE);
            String connectDevice = CacheHelper.getString(Constants.Connect.PAYMENT_CONNECT_DEVICES);
            if (isConnected) {
                tvConnectStatus.setText(getString(R.string.connected));
                tvConnectType.setText(connectType);
                tvConnectDevice.setText(connectDevice);
                tvDisconnect.setEnabled(true);
                tvDisconnect.setTextColor(getResources().getColor(R.color.white));
                findViewById(R.id.ll_local).setBackgroundResource(R.drawable.bg_connect_way_select_disabled);
                findViewById(R.id.ll_usb).setBackgroundResource(R.drawable.bg_connect_way_select_disabled);
                findViewById(R.id.ll_wifi).setBackgroundResource(R.drawable.bg_connect_way_select_disabled);
            } else {
                tvConnectStatus.setText(getString(R.string.unconnected));
                tvConnectType.setText(getString(R.string.not_have));
                tvConnectDevice.setText(getString(R.string.not_have));
                tvDisconnect.setEnabled(false);
                tvDisconnect.setTextColor(getResources().getColor(R.color.black50));
                findViewById(R.id.ll_local).setBackgroundResource(R.drawable.bg_connect_way_select);
                findViewById(R.id.ll_usb).setBackgroundResource(R.drawable.bg_connect_way_select);
                findViewById(R.id.ll_wifi).setBackgroundResource(R.drawable.bg_connect_way_select);
            }
        });
    }

     @Override
    protected void onDestroy() {
        super.onDestroy();
        PaymentHelper.removeCallback(connectCallback);
    }


}
