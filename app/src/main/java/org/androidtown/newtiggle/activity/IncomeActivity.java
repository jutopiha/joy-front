package org.androidtown.newtiggle.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.androidtown.newtiggle.R;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juha on 2017-05-18.
 */

/* 스레드 수정  현정2017-08-14 */

public class IncomeActivity extends AppCompatActivity implements View.OnClickListener {

    InputMethodManager imm; //화면 터치시 키보드 내리기 위한

    DatePicker mDate; //day받기 위한
    EditText mDay;  //day받기 위한
    int year, month, day; //day받기 위한

    TimePicker mTime;//time 받기 위한
    EditText mClock;   //time 받기 위한
    int hour, minute;//time 받기 위한

    private static final int LAYOUT = R.layout.activity_income;
    private URL url;
    private static String strUrl, strCookie, result;

    private EditText mJsonIncomeMoneyEt;
    private EditText mJsonIncomeMemoEt;
    private EditText mJsonIncomeDayEt; //day받기 위한
    private EditText mJsonIncomeTimeEt;    //time 받기 위한
    private Spinner mJsonIncomeCategorySp; //category받기 위한


    private Button mObjectBtn;

    private JSONObject jsonObject = new JSONObject(); // for temp


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        mDate = (DatePicker) findViewById(R.id.incomeDatePicker); //day 받기 위한
        mDay = (EditText) findViewById(R.id.jsonIncomeDayEt);   //day 받기 위한

        mTime = (TimePicker) findViewById(R.id.incomeTimePicker); //time 받기 위한
        mClock = (EditText) findViewById(R.id.jsonIncomeTimeEt); //time 받기 위한

        //day받기 위한
        mDate.init(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mDay.setText(String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth));
            }
        });


        year = mDate.getYear();
        month = mDate.getMonth();
        day = mDate.getDayOfMonth();
        mDay.setText(String.format("%d-%d-%d", year, month + 1, day));


        //time받기 위한
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour = mTime.getHour();
            minute = mTime.getMinute();

            mClock.setText(String.format("%d:%d", hour, minute));
        } else {
            hour = mTime.getCurrentHour();
            minute = mTime.getCurrentMinute();
            mClock.setText(String.format("%d:%d", hour, minute));
        }

        //timepicker setontimechangeListener
        mTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mClock.setText(String.format("%d:%d", hourOfDay, minute));
            }
        });

        bindView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        //화면 터치시 키보드 내리기
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

    }


    @Override
    public void onClick(View v) {
        //sendObject();
        switch (v.getId()) {
            case R.id.objectBtn:
                sendObject();
                finish();
                break;
        }
    }

    private void bindView() {
        //bind view
        mJsonIncomeMoneyEt = (EditText) findViewById(R.id.jsonIncomeMoneyEt);
        mJsonIncomeMemoEt = (EditText) findViewById(R.id.jsonIncomeMemoEt);
        mJsonIncomeDayEt = (EditText) findViewById(R.id.jsonIncomeDayEt); //day받기 위한
        mJsonIncomeTimeEt = (EditText) findViewById(R.id.jsonIncomeTimeEt);//time 받기 위한
        mJsonIncomeCategorySp = (Spinner) findViewById(R.id.jsonIncomeCategorySp); //category받기 위한

        mObjectBtn = (Button) findViewById(R.id.objectBtn);

        //set listener
        mObjectBtn.setOnClickListener(this);
    }


    private void sendObject() {

        try {
            int incomeDay; //day를 20170528 형식으로 받기 위해
            int incomeTime;    //time을 1200 형식으로 받기 위해
            incomeDay = mDate.getYear() * 10000 + (mDate.getMonth() + 1) * 100 + mDate.getDayOfMonth();

            //time 만들기
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                incomeTime = mTime.getHour() + 100 + mTime.getMinute();
            } else {
                incomeTime = mTime.getCurrentHour() * 100 + mTime.getCurrentMinute();
            }

            //jsonObject.put("day", mJsonExpenseDayEt.getText().toString());  //day받기 위한
            jsonObject.put("date", incomeDay); //day받기 위한
            jsonObject.put("time", incomeTime); //time받기 위한
            jsonObject.put("category", mJsonIncomeCategorySp.getSelectedItem().toString()); //category받기 위한
            jsonObject.put("money", mJsonIncomeMoneyEt.getText().toString());
            jsonObject.put("memo", mJsonIncomeMemoEt.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SaveNewIncome request = new SaveNewIncome();
        request.run();

    }


    //화면 터치시 키보드 내리기
    public void linearOnClick(View view) {
        imm.hideSoftInputFromWindow(mJsonIncomeMemoEt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mJsonIncomeMoneyEt.getWindowToken(), 0);
    }

    public void onBackButtonClicked(View v) {
        Toast.makeText(getApplicationContext(), "돌아가기 버튼이 눌렸어요", Toast.LENGTH_LONG).show();
        finish();
    }


    private class SaveNewIncome extends Thread {
        @Override
        public void run() {

            postData("ljh", jsonObject);

        }
    }

    public String postData(String uid, JSONObject data) {

        String msg = MainActivity.urlString + "/post/income";

        InputStream inputStream = null;
        BufferedReader rd = null;
        StringBuilder result = new StringBuilder();

        StringBuilder requestUrl = new StringBuilder(msg);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("uid", MainActivity.currentUserId));
        String querystring = URLEncodedUtils.format(nvps, "utf-8");

        requestUrl.append("?");
        requestUrl.append(querystring);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(requestUrl.toString());
        Log.d("msg is :", requestUrl.toString());

        try {
            String json = "";
            json = data.toString();
            //json = URLEncoder.encode(json,"UTF-8"); //한글이 ??로 저장되는 거 바꿈
            //String koreanJson = new String(json.getBytes("UTF-8"));

            // loglog
            Log.v("^^^^^json", json);

            StringEntity stringEntity = new StringEntity(json, "utf-8");
            httpPost.setEntity(stringEntity);


            //answer객체 서버로 전송하고 survey객체 받아오는 과정

            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.v("******server", "send msg successed");

            inputStream = httpResponse.getEntity().getContent();
            rd = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            Log.v("Main::bring success", "result:" + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("******server", "send msg failed");
        }

        if (result != null) {
            return result.toString();
        } else {
            return null;
        }

    }
}