package com.sunmi.payment.demo.page.trans;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hjq.bar.TitleBar;
import com.sunmi.payment.demo.R;
import com.sunmi.payment.demo.page.BaseTransActivity;
import com.sunmi.payment.demo.utils.OnTitleBarListenerWrapper;

import org.json.JSONObject;

public class TransactionInquiryActivity extends BaseTransActivity {

    private EditText editTransactionId, editOriginOrderId;
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_inquiry);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnTitleBarListener(new OnTitleBarListenerWrapper() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }
        });
        editTransactionId = findViewById(R.id.edit_origin_transaction_id);
        editOriginOrderId = findViewById(R.id.edit_origin_order_id);
        tvResult = findViewById(R.id.tv_result);
        findViewById(R.id.mb_ok).setOnClickListener(view -> {
            tvResult.setText("");
            transactionInquiry();
        });
    }

    private void transactionInquiry() {
        String originTransactionId = editTransactionId.getText().toString();
        String originOrderId = editOriginOrderId.getText().toString();
        if (TextUtils.isEmpty(originTransactionId) && TextUtils.isEmpty(originOrderId)) {
            showToast("originTransactionId 或 originOrderId 不能都为空");
            return;
        }

        try {
            JSONObject obj = new JSONObject();
            obj.put("action", "transactionInquiry");
            obj.put("orderId", System.currentTimeMillis() + "");
            if (!TextUtils.isEmpty(originTransactionId)) {
                obj.put("originTransactionId", originTransactionId);
            }
            if (!TextUtils.isEmpty(originOrderId)) {
                obj.put("originOrderId", originOrderId);
            }
            startTrans(obj.toString(), tvResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
