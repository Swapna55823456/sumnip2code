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

public class SaleActivity extends BaseTransActivity {

    private EditText editAmount;
    private EditText editTip;
    private EditText editTax;
    private EditText editOrderId;
    private TextView tvResult;

    private String currentOrderId = "";
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnTitleBarListener(new OnTitleBarListenerWrapper() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }
        });

        editAmount = findViewById(R.id.edit_amount);
        editTip = findViewById(R.id.edit_tip);
        editTax = findViewById(R.id.edit_tax);
        editOrderId = findViewById(R.id.edit_order_id);
        tvResult = findViewById(R.id.tv_result);

        // Receive data from Flutter
        if (getIntent() != null) {
            String amtStr = getIntent().getStringExtra("amount");
            if (amtStr != null) editAmount.setText(amtStr);

            String orderIdStr = getIntent().getStringExtra("orderId");
            if (orderIdStr != null) editOrderId.setText(orderIdStr);
        }

        findViewById(R.id.mb_ok).setOnClickListener(view -> {
            tvResult.setText("");
            sale();
        });
    }

    private void sale() {
        String amount = editAmount.getText().toString().trim();
        String tip = editTip.getText().toString().trim();
        String tax = editTax.getText().toString().trim();

        long amountLong;

        try {
            amount = amount.replace(",", "").trim();
            double amtDouble = Double.parseDouble(amount);
            amountLong = Math.round(amtDouble * 100);
        } catch (Exception e) {
            showToast("Invalid amount");
            return;
        }

        if (amountLong <= 0) {
            showToast("Invalid amount");
            return;
        }

        currentOrderId = editOrderId.getText().toString().trim();
        if (TextUtils.isEmpty(currentOrderId)) {
            currentOrderId = System.currentTimeMillis() + "";
        }

        try {
            JSONObject obj = new JSONObject();
            obj.put("action", "purchase");
            obj.put("orderId", currentOrderId);
            obj.put("paymentType", "credit");
            obj.put("amount", amountLong);

            long tipLong = TextUtils.isEmpty(tip) ? 0 : Long.parseLong(tip);
            long taxLong = TextUtils.isEmpty(tax) ? 0 : Long.parseLong(tax);

            obj.put("tip", tipLong);
            obj.put("tax", taxLong);

            // Sunmi processes transaction and writes result JSON to tvResult
            startTrans(obj.toString(), tvResult);

            // Re-check for result until it arrives
            handler.postDelayed(this::checkAndSendResult, 500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the Sunmi JSON from tvResult and sends full data back to Flutter
     */
    private void checkAndSendResult() {
        String resultJson = tvResult.getText().toString().trim();

        if (resultJson.isEmpty() || !resultJson.contains("{")) {
            handler.postDelayed(this::checkAndSendResult, 500);
            return;
        }

        try {
            JSONObject sunmi = new JSONObject(resultJson);

            JSONObject sendBack = new JSONObject();
            sendBack.put("status", sunmi.optString("resultCode").equals("00") ? "SUCCESS" : "FAILED");
            sendBack.put("message", sunmi.optString("resultMsg"));
            sendBack.put("orderId", sunmi.optString("orderId", currentOrderId));
            sendBack.put("amount", sunmi.optString("processedAmount"));

            // ⭐ FULL RAW SUNMI RESPONSE
            sendBack.put("fullResponse", sunmi.toString());

            Intent intent = new Intent();
            intent.putExtra("paymentResult", sendBack.toString());

            setResult(RESULT_OK, intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
