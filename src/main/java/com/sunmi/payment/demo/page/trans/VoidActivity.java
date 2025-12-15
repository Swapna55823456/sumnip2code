package com.sunmi.payment.demo.page.trans;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class VoidActivity extends BaseTransActivity {

    private EditText editAmount, editTransactionId, editOriginOrderId;
    private TextView tvResult;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void);

        editAmount = findViewById(R.id.edit_amount);
        editTransactionId = findViewById(R.id.edit_origin_transaction_id);
        editOriginOrderId = findViewById(R.id.edit_origin_order_id);
        tvResult = findViewById(R.id.tv_result);

        // ✅ Receive from Flutter
        if (getIntent() != null) {
            String amount = getIntent().getStringExtra("amount");
            String orderId = getIntent().getStringExtra("originOrderId");
            String transactionId = getIntent().getStringExtra("originTransactionId");

            if (amount != null) editAmount.setText(amount);
            if (orderId != null) editOriginOrderId.setText(orderId);
            if (transactionId != null) editTransactionId.setText(transactionId);
        }

        // 🚀 AUTO START VOID
        tvResult.setText("");
        handler.postDelayed(this::startVoid, 300);
    }

    private void startVoid() {
        String amountStr = editAmount.getText().toString().trim();
        String originTransactionId = editTransactionId.getText().toString().trim();
        String originOrderId = editOriginOrderId.getText().toString().trim();

        long amountLong = 0;
        try {
            double amt = Double.parseDouble(amountStr.replace(",", ""));
            amountLong = Math.round(amt * 100);
        } catch (Exception ignored) {}

        if (TextUtils.isEmpty(originTransactionId) && TextUtils.isEmpty(originOrderId)) {
            sendError("Missing originTransactionId / originOrderId");
            return;
        }

        try {
            JSONObject obj = new JSONObject();
            obj.put("action", "void");
            obj.put("orderId", System.currentTimeMillis() + "");

            if (!TextUtils.isEmpty(originTransactionId)) {
                obj.put("originTransactionId", originTransactionId);
            }
            if (!TextUtils.isEmpty(originOrderId)) {
                obj.put("originOrderId", originOrderId);
            }

            if (amountLong <= 0) {
                obj.put("voidType", "full");
            } else {
                obj.put("voidType", "partial");
                obj.put("amount", amountLong);
            }

            startTrans(obj.toString(), tvResult);
            handler.postDelayed(this::checkAndSendResult, 500);

        } catch (Exception e) {
            sendError(e.getMessage());
        }
    }

    private void checkAndSendResult() {
        String resultJson = tvResult.getText().toString().trim();

        if (resultJson.isEmpty() || !resultJson.contains("{")) {
            handler.postDelayed(this::checkAndSendResult, 500);
            return;
        }

        try {
            JSONObject sunmi = new JSONObject(resultJson);

            JSONObject response = new JSONObject();
            response.put(
                    "status",
                    "00".equals(sunmi.optString("resultCode")) ? "SUCCESS" : "FAILED"
            );
            response.put("message", sunmi.optString("resultMsg"));
            response.put("orderId", sunmi.optString("orderId"));
            response.put("amount", sunmi.optString("processedAmount"));
            response.put("fullResponse", sunmi.toString());

            Intent intent = new Intent();
            intent.putExtra("paymentResult", response.toString());
            setResult(RESULT_OK, intent);
            finish();

        } catch (Exception e) {
            sendError(e.getMessage());
        }
    }

    private void sendError(String msg) {
        try {
            JSONObject err = new JSONObject();
            err.put("status", "FAILED");
            err.put("message", msg);

            Intent intent = new Intent();
            intent.putExtra("paymentResult", err.toString());
            setResult(RESULT_OK, intent);
        } catch (Exception ignored) {}
        finish();
    }
}

