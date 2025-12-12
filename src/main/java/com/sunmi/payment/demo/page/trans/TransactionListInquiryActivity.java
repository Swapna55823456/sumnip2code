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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TransactionListInquiryActivity extends BaseTransActivity {

    private EditText editStartDate, editEndDate;
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list_inquiry);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnTitleBarListener(new OnTitleBarListenerWrapper() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }
        });
        editStartDate = findViewById(R.id.edit_start_date);
        editEndDate = findViewById(R.id.edit_end_date);
        editStartDate.setText(getStartDate());
        editEndDate.setText(getEndDate());
        tvResult = findViewById(R.id.tv_result);
        findViewById(R.id.mb_ok).setOnClickListener(view -> {
            tvResult.setText("");
            transactionInquiry();
        });
    }

    private void transactionInquiry() {
        String startDate = editStartDate.getText().toString();
        String endDate = editEndDate.getText().toString();
        if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            showToast("startDate 和 endDate 不能为空");
            return;
        }

        try {
            JSONObject obj = new JSONObject();
            obj.put("action", "transactionListInquiry");
            obj.put("orderId", System.currentTimeMillis() + "");
            if (!TextUtils.isEmpty(startDate)) {
                obj.put("fromDate", startDate);
            }
            if (!TextUtils.isEmpty(endDate)) {
                obj.put("toDate", endDate);
            }
            startTrans(obj.toString(), tvResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getStartDate() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        Calendar nowBefore = Calendar.getInstance();
        nowBefore.add(Calendar.DAY_OF_MONTH, -6);
        String strDate = date.format(nowBefore.getTimeInMillis());
        return strDate + "T" + "00:00:00";
    }

    public static String getEndDate() {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        Calendar nowBefore = Calendar.getInstance();
        String strDate = date.format(nowBefore.getTimeInMillis());
        return strDate + "T" + "23:59:59";
    }

}
