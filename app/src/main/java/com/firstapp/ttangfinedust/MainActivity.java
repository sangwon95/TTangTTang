package com.firstapp.ttangfinedust;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private ImageButton image_btn_add_location;
    private ImageView image_main, image_pm25value, image_coValue, image_o3Value, image_no2Value;
    private TextView textview_state, textview_finedust_value, textview_time, textview_location, textview_pm25value_value, textview_coValue_value, textview_o3Value_value, textview_no2Value_value;
    private SwipeRefreshLayout swipelayout;
    private LinearLayout main_background_color, head_color, location_color, time_color, card_1, card_2, card_3, card_4;

    private String key ;
    private String[][] Data = new String[31][7];

    private int city_number = 0;
    private int count = 0;
    private int REQUEST_TEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //button
        image_btn_add_location = findViewById(R.id.image_btn_add_location);

        //image
        image_main = findViewById(R.id.image_main);
        image_pm25value = findViewById(R.id.image_pm25value);
        image_coValue = findViewById(R.id.image_coValue);
        image_o3Value = findViewById(R.id.image_o3Value);
        image_no2Value = findViewById(R.id.image_no2Value);

        //Text View
        textview_state = findViewById(R.id.textview_state);
        textview_finedust_value = findViewById(R.id.textview_finedust_value);
        textview_time = findViewById(R.id.textview_time);
        textview_location = findViewById(R.id.textview_location);
        textview_pm25value_value = findViewById(R.id.textview_pm25value_value);
        textview_coValue_value = findViewById(R.id.textview_coValue_value);
        textview_o3Value_value = findViewById(R.id.textview_o3Value_value);
        textview_no2Value_value = findViewById(R.id.textview_no2Value_value);

        //background color
        main_background_color = findViewById(R.id.main_background_color);
        head_color = findViewById(R.id.head_color);
        location_color = findViewById(R.id.location_color);
        time_color = findViewById(R.id.time_color);
        card_1 = findViewById(R.id.card_1);
        card_2 = findViewById(R.id.card_2);
        card_3 = findViewById(R.id.card_3);
        card_4 = findViewById(R.id.card_4);

        //SwipeRefreshLayout
        swipelayout= findViewById(R.id.swipelayout);

        //Network 확인
        if(isConnectedNetwork()){
            Toast.makeText(MainActivity.this,"환영합니다.",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this,"네트워크 확인 바랍니다.",Toast.LENGTH_SHORT).show();
        }

        //앱 첫 실행 여부 확인
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);

                if (data.getString("dataTime", null) == null) {
                    InitialSetting();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendData();
                    }
                });


            }
        }).start();

        //Search location
        image_btn_add_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FinedustActivity.class);
                startActivityForResult(intent, REQUEST_TEST);
            }
        });

        //수동 새로고침
        swipelayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Update();
                Toast.makeText(MainActivity.this,"새로고침 완료",Toast.LENGTH_SHORT).show();
                swipelayout.setRefreshing(false);
            }
        });


    }

    //Network 연결확인
    private boolean isConnectedNetwork(){
        boolean networkState = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            networkState = true;
            Log.e("Network", "네트워크 연결 확인 됨");
        }
        return networkState;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEST) {
            if (resultCode == RESULT_OK) {
                //저장된 데이터 불러오기
                sendData();
            } else {   // RESULT_CANCEL
                Toast.makeText(MainActivity.this, "데이터를 가져오지 못했습니다. 다시 시도 바랍니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    //파싱된 데이터 불러오기
    private void sendData() {

        SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);

        String dataTime = data.getString("dataTime", null);
        String cityName = data.getString("cityName", null);
        String coValue = data.getString("coValue", null);
        String o3Value = data.getString("o3Value", null);
        String no2Value = data.getString("no2Value", null);
        String pm10Value = data.getString("pm10Value", null);
        String pm25Value = data.getString("pm25Value", null);
        String location = data.getString("location", null);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (dataTime != null) {
            //미세먼지
            if (Integer.parseInt(pm10Value) <= 30) {
                image_main.setImageResource(R.drawable.iconfinder_good);
                image_main.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_state.setText("좋음");
                textview_finedust_value.setText(Integer.parseInt(pm10Value) + "㎍/㎥");
                main_background_color.setBackgroundColor(getResources().getColor(R.color.colorGood));
                head_color.setBackgroundColor(getResources().getColor(R.color.colorGood_sub));
                location_color.setBackgroundColor(getResources().getColor(R.color.colorGood_sub));
                time_color.setBackgroundColor(getResources().getColor(R.color.colorGood_sub));
                window.setStatusBarColor(getResources().getColor(R.color.colorbarGood));

            } else if (Integer.parseInt(pm10Value) <= 80) {
                image_main.setImageResource(R.drawable.iconfinder_normal);
                image_main.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_state.setText("보통");
                textview_finedust_value.setText(Integer.parseInt(pm10Value) + "㎍/㎥");
                main_background_color.setBackgroundColor(getResources().getColor(R.color.colorNormal));
                head_color.setBackgroundColor(getResources().getColor(R.color.colorNormal_sub));
                location_color.setBackgroundColor(getResources().getColor(R.color.colorNormal_sub));
                time_color.setBackgroundColor(getResources().getColor(R.color.colorNormal_sub));
                window.setStatusBarColor(getResources().getColor(R.color.colorbarNormal));
            } else if (Integer.parseInt(pm10Value) <= 150) {
                image_main.setImageResource(R.drawable.iconfinder_bad);
                image_main.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_state.setText("나쁨");
                textview_finedust_value.setText(Integer.parseInt(pm10Value) + "㎍/㎥");
                main_background_color.setBackgroundColor(getResources().getColor(R.color.colorBad));
                head_color.setBackgroundColor(getResources().getColor(R.color.colorBad_sub));
                location_color.setBackgroundColor(getResources().getColor(R.color.colorBad_sub));
                time_color.setBackgroundColor(getResources().getColor(R.color.colorBad_sub));
                window.setStatusBarColor(getResources().getColor(R.color.colorbarBad));
            } else if (Integer.parseInt(pm10Value) >= 151) {
                image_main.setImageResource(R.drawable.iconfinder_verybad);
                image_main.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_state.setText("최악");
                textview_finedust_value.setText(Integer.parseInt(pm10Value) + "㎍/㎥");
                main_background_color.setBackgroundColor(getResources().getColor(R.color.colorVeryBad));
                head_color.setBackgroundColor(getResources().getColor(R.color.colorVeryBad));
                location_color.setBackgroundColor(getResources().getColor(R.color.colorVeryBad));
                time_color.setBackgroundColor(getResources().getColor(R.color.colorVeryBad));
                window.setStatusBarColor(getResources().getColor(R.color.colorbarVeryBad));
            }

            //초미세먼지
            if (Integer.parseInt(pm25Value) <= 15) {
                image_pm25value.setImageResource(R.drawable.iconfinder_good);
                image_pm25value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_pm25value_value.setText(Integer.parseInt(pm25Value) + "㎍/㎥");
                card_1.setBackgroundColor(getResources().getColor(R.color.colorGood_sub));

            } else if (Integer.parseInt(pm25Value) <= 16) {
                image_pm25value.setImageResource(R.drawable.iconfinder_normal);
                image_pm25value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_pm25value_value.setText(Integer.parseInt(pm25Value) + "㎍/㎥");
                card_1.setBackgroundColor(getResources().getColor(R.color.colorNormal_sub));
            } else if (Integer.parseInt(pm25Value) <= 75) {
                image_pm25value.setImageResource(R.drawable.iconfinder_bad);
                image_pm25value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_pm25value_value.setText(Integer.parseInt(pm25Value) + "㎍/㎥");
                card_1.setBackgroundColor(getResources().getColor(R.color.colorBad_sub));
            } else if (Integer.parseInt(pm25Value) >= 76) {
                image_pm25value.setImageResource(R.drawable.iconfinder_verybad);
                image_pm25value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_pm25value_value.setText(Integer.parseInt(pm25Value) + "㎍/㎥");
                card_1.setBackgroundColor(getResources().getColor(R.color.colorVeryBad));
            }

            //일산화탄소
            if (Double.parseDouble(coValue) < 2.0) {
                image_coValue.setImageResource(R.drawable.iconfinder_good);
                image_coValue.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_coValue_value.setText(Double.parseDouble(coValue) + "㎍/㎥");
                card_2.setBackgroundColor(getResources().getColor(R.color.colorGood_sub));
            } else if (Double.parseDouble(coValue) < 9.0) {
                image_coValue.setImageResource(R.drawable.iconfinder_normal);
                image_coValue.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_coValue_value.setText(Double.parseDouble(coValue) + "㎍/㎥");
                card_2.setBackgroundColor(getResources().getColor(R.color.colorNormal_sub));
            } else if (Double.parseDouble(coValue) < 15.0) {
                image_coValue.setImageResource(R.drawable.iconfinder_bad);
                image_coValue.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_coValue_value.setText(Double.parseDouble(coValue) + "㎍/㎥");
                card_2.setBackgroundColor(getResources().getColor(R.color.colorBad_sub));
            } else if (Double.parseDouble(coValue) > 15.1) {
                image_coValue.setImageResource(R.drawable.iconfinder_verybad);
                image_coValue.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_coValue_value.setText(Double.parseDouble(coValue) + "㎍/㎥");
                card_2.setBackgroundColor(getResources().getColor(R.color.colorVeryBad));
            }

            //오존
            if (Double.parseDouble(o3Value) < 0.03) {
                image_o3Value.setImageResource(R.drawable.iconfinder_good);
                image_o3Value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_o3Value_value.setText(Double.parseDouble(o3Value) + "㎍/㎥");
                card_3.setBackgroundColor(getResources().getColor(R.color.colorGood_sub));
            } else if (Double.parseDouble(o3Value) < 0.09) {
                image_o3Value.setImageResource(R.drawable.iconfinder_normal);
                image_o3Value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_o3Value_value.setText(Double.parseDouble(o3Value) + "㎍/㎥");
                card_3.setBackgroundColor(getResources().getColor(R.color.colorNormal_sub));
            } else if (Double.parseDouble(o3Value) < 0.15) {
                image_o3Value.setImageResource(R.drawable.iconfinder_bad);
                image_o3Value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_o3Value_value.setText(Double.parseDouble(o3Value) + "㎍/㎥");
                card_3.setBackgroundColor(getResources().getColor(R.color.colorBad_sub));
            } else if (Double.parseDouble(o3Value) > 0.151) {
                image_o3Value.setImageResource(R.drawable.iconfinder_verybad);
                image_o3Value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_o3Value_value.setText(Double.parseDouble(o3Value) + "㎍/㎥");
                card_3.setBackgroundColor(getResources().getColor(R.color.colorVeryBad));
            }

            //이산화질소
            if (Double.parseDouble(no2Value) < 0.03) {
                image_no2Value.setImageResource(R.drawable.iconfinder_good);
                image_no2Value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_no2Value_value.setText(Double.parseDouble(no2Value) + "㎍/㎥");
                card_4.setBackgroundColor(getResources().getColor(R.color.colorGood_sub));
            } else if (Double.parseDouble(no2Value) < 0.06) {
                image_no2Value.setImageResource(R.drawable.iconfinder_normal);
                image_no2Value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_no2Value_value.setText(Double.parseDouble(no2Value) + "㎍/㎥");
                card_4.setBackgroundColor(getResources().getColor(R.color.colorNormal_sub));
            } else if (Double.parseDouble(no2Value) < 0.2) {
                image_no2Value.setImageResource(R.drawable.iconfinder_bad);
                image_no2Value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_no2Value_value.setText(Double.parseDouble(no2Value) + "㎍/㎥");
                card_4.setBackgroundColor(getResources().getColor(R.color.colorBad_sub));
            } else if (Double.parseDouble(no2Value) > 0.201) {
                image_no2Value.setImageResource(R.drawable.iconfinder_verybad);
                image_no2Value.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                textview_no2Value_value.setText(Double.parseDouble(no2Value) + "㎍/㎥");
                card_4.setBackgroundColor(getResources().getColor(R.color.colorVeryBad));
            }

            textview_location.setText(location + " " + cityName);
            textview_time.setText(dataTime);

        } else {

            Log.d("로그", "false");
            Toast.makeText(MainActivity.this, "기상 정보를 가져오지 못했습니다.", Toast.LENGTH_LONG).show();
        }


    }
    //다운로드 후 초기 화면 설정
    private void InitialSetting() {

        String search_city_name = "대전";
        String queryUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst?serviceKey=" + key + "&numOfRows=1&pageNo=1&sidoName=" + search_city_name + "&searchCondition=DAILY";

        try {
            URL url = new URL(queryUrl); //문자열로 된 요청 url을 URL 객체로 생성
            InputStream is = url.openStream(); // url위치로 인풋스트림 연결

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            // inputstream 으로부터 xml 입력받기
            parser.setInput(new InputStreamReader(is, "UTF-8"));
            String tag;
            parser.next();

            int eventType = parser.getEventType();

            SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
            SharedPreferences.Editor initial = data.edit();
            initial.putInt("position", 0);

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = parser.getName(); // 태그 이름 얻어오기

                        if (tag.equals("item")) ; // 첫번째 검색결과
                        else if (tag.equals("dataTime")) {//측정시간
                            parser.next();
                            String dataTime = parser.getText();
                            initial.putString("dataTime", dataTime);
                            initial.commit();
                            Log.d("dataTime", dataTime);
                        } else if (tag.equals("cityName")) {//도시명
                            parser.next();
                            String cityName = parser.getText();
                            initial.putString("cityName", cityName);
                            initial.commit();
                            Log.d("cityName", cityName);
                        } else if (tag.equals("coValue")) {//일산화탄소
                            parser.next();
                            String coValue = parser.getText();
                            initial.putString("coValue", coValue);
                            initial.commit();
                            Log.d("coValue", coValue);

                        } else if (tag.equals("o3Value")) {//오존
                            parser.next();
                            String o3Value = parser.getText();
                            initial.putString("o3Value", o3Value);
                            initial.commit();
                            Log.d("o3Value", o3Value);
                        } else if (tag.equals("no2Value")) {//이산화질소
                            parser.next();
                            String no2Value = parser.getText();
                            initial.putString("no2Value", no2Value);
                            initial.commit();
                            Log.d("no2Value", no2Value);
                        } else if (tag.equals("pm10Value")) {//미세먼지
                            parser.next();
                            String pm10Value = parser.getText();
                            initial.putString("pm10Value", pm10Value);
                            initial.commit();
                            Log.d("pm10Value", pm10Value);
                        } else if (tag.equals("pm25Value")) {//초미세먼지
                            parser.next();
                            String pm25Value = parser.getText();
                            initial.putString("pm25Value", pm25Value);
                            initial.putString("location", search_city_name);
                            initial.commit();
                            Log.d("pm25Value", pm25Value);
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next(); // 종료 태그 파싱
            }


        } catch (Exception e) {

        }
    }

    //update
    private void Update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runSearch();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        selectData();
                        sendData();
                    }
                });

            }
        }).start();
    }

    private void selectData() {

        SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
        int position = data.getInt("position", 0);

        SharedPreferences.Editor initial = data.edit();

        initial.putString("dataTime", Data[position][0]);
        initial.putString("cityName", Data[position][1]);
        initial.putString("coValue", Data[position][2]);
        initial.putString("o3Value", Data[position][3]);
        initial.putString("no2Value", Data[position][4]);
        initial.putString("pm10Value", Data[position][5]);
        initial.putString("pm25Value", Data[position][6]);
        initial.commit();
    }


    private void runSearch() {

        SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
        //Data Clear
        for (int i = 0; i < 31; i++) {
            for (int j = 0; j < 7; j++) {
                Data[i][j] = null;
            }
        }
        count = 0;

        String search_city_name = data.getString("location", null);

        switch (search_city_name) {
            case "대전":
            case "광주":
            case "울산":
                city_number = 5;
                break;
            case "서울":
                city_number = 25;
                break;
            case "부산":
                city_number = 16;
                break;
            case "대구":
                city_number = 8;
                break;
            case "충북":
                city_number = 11;
                break;
            case "충남":
                city_number = 15;
                break;
            case "인천":
                city_number = 10;
                break;
            case "경기":
                city_number = 31;
                break;
            case "경남":
                city_number = 18;
            case "강원":
                city_number = 18;
                break;
            case "전북":
                city_number = 14;
                break;
            case "전남":
                city_number = 22;
                break;
            case "경북":
                city_number = 23;
                break;
            case "제주":
                city_number = 2;
                break;
            case "세종":
                city_number = 1;
                break;
        }
        String queryUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst?serviceKey=" + key + "&numOfRows=" + city_number + "&pageNo=1&sidoName=" + search_city_name + "&searchCondition=DAILY";

        try {

            URL url = new URL(queryUrl); //문자열로 된 요청 url을 URL 객체로 생성
            InputStream is = url.openStream(); // url위치로 인풋스트림 연결
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            // inputstream 으로부터 xml 입력받기
            parser.setInput(new InputStreamReader(is, "UTF-8"));
            String tag;
            parser.next();

            int eventType = parser.getEventType();


            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = parser.getName(); // 태그 이름 얻어오기

                        if (tag.equals("item")) ; // 첫번째 검색결과
                        else if (tag.equals("dataTime")) {//측정시간
                            parser.next();
                            String dataTime = parser.getText();
                            Data[count][0] = dataTime;

                        } else if (tag.equals("cityName")) {//도시명
                            parser.next();
                            String cityName = parser.getText();
                            Data[count][1] = cityName;
                        } else if (tag.equals("coValue")) {//일산화탄소
                            parser.next();
                            String coValue = parser.getText();
                            Data[count][2] = coValue;

                        } else if (tag.equals("o3Value")) {//오존
                            parser.next();
                            String o3Value = parser.getText();
                            Data[count][3] = o3Value;

                        } else if (tag.equals("no2Value")) {//이산화질소
                            parser.next();
                            String no2Value = parser.getText();
                            Data[count][4] = no2Value;

                        } else if (tag.equals("pm10Value")) {//미세먼지
                            parser.next();
                            String pm10Value = parser.getText();
                            Data[count][5] = pm10Value;

                        } else if (tag.equals("pm25Value")) {//초미세먼지
                            parser.next();
                            String pm25Value = parser.getText();
                            Data[count][6] = pm25Value;
                            count++;

                        }
                        break;

                    case XmlPullParser.TEXT:

                        // break;

                    case XmlPullParser.END_TAG:

                        //break;

                }
                eventType = parser.next(); // 종료 태그 파싱
            }


        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //15min 마다 자동 업데이트(설정된), 설정(x)-> 초기 설정 지역으로 업데이트
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Update();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 5000, 150000);

    }
}

