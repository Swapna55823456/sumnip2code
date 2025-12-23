package com.sunmi.payment.demo.page.trans;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sunmi.payment.demo.R;
import com.sunmi.payment.demo.page.BaseTransActivity;

import org.json.JSONObject;

public class SaleActivity extends BaseTransActivity {

    private EditText editAmount;
    private EditText editTip;
    private EditText editTax;
    private EditText editOrderId;
    private TextView tvResult;

    private String currentOrderId = "";
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        editAmount = findViewById(R.id.edit_amount);
        editTip = findViewById(R.id.edit_tip);
        editTax = findViewById(R.id.edit_tax);
        editOrderId = findViewById(R.id.edit_order_id);
        tvResult = findViewById(R.id.tv_result);

        // Receive values from Flutter
        if (getIntent() != null) {
            String amt = getIntent().getStringExtra("amount");
            String orderId = getIntent().getStringExtra("orderId");

            if (amt != null) editAmount.setText(amt);
            if (orderId != null) editOrderId.setText(orderId);
        }

        // 🚀 AUTO START SALE (NO BUTTON)
        tvResult.setText("");
        handler.postDelayed(this::sale, 300);
    }

    private void sale() {
        String amountStr = editAmount.getText().toString().trim();

        long amountLong;
        try {
            double amt = Double.parseDouble(amountStr.replace(",", ""));
            amountLong = Math.round(amt * 100);
        } catch (Exception e) {
            sendError("Invalid amount");
            return;
        }

        if (amountLong <= 0) {
            sendError("Invalid amount");
            return;
        }

        currentOrderId = editOrderId.getText().toString().trim();
        if (TextUtils.isEmpty(currentOrderId)) {
            currentOrderId = String.valueOf(System.currentTimeMillis());
        }

        try {
            JSONObject obj = new JSONObject();
            obj.put("action", "purchase");
            obj.put("paymentType", "credit");
            obj.put("orderId", currentOrderId);
            obj.put("amount", amountLong);
            obj.put("tip", 0);
            obj.put("tax", 0);
            Log.e(TAG, "sale REQUEST → " + tvResult);

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

            // ✅ DEBUG: PRINT FULL SALE RESPONSE
            Log.e("SUNMI_SALE_RAW", "SALE RESPONSE → " + sunmi.toString());

            JSONObject response = new JSONObject();
            response.put(
                    "status",
                    "00".equals(sunmi.optString("resultCode")) ? "SUCCESS" : "FAILED"
            );
            response.put("message", sunmi.optString("resultMsg"));

            // ✅ MUST COME FROM SUNMI
            response.put("orderId", sunmi.optString("orderId"));
            response.put("transactionId", sunmi.optString("transactionId"));

            response.put("amount", sunmi.optString("processedAmount"));
            response.put("fullResponse", sunmi.toString());

            Log.e(
                    "SUNMI_SALE_PAIR",
                    "SALE PAIR → orderId="
                            + sunmi.optString("orderId")
                            + ", txnId="
                            + sunmi.optString("transactionId")
            );

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
        } catch (Exception ignored) {
        }
        finish();
    }
}
