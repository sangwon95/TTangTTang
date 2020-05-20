package com.firstapp.ttangfinedust;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class GetDatawidget extends AppCompatActivity {
    AppWidgetManager appWidgetManager;
    ComponentName thisWidget;
    Context context;
    RemoteViews views;



    private static int appWidgetid;
    private static String key = "";
    private static String[][] Data = new String[31][7];

    private static int city_number = 0;
    private static int count = 0;
    private static int REQUEST_TEST = 1;

    public GetDatawidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId,RemoteViews views) {

        this.appWidgetid = appWidgetId;
        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.views = views;
        thisWidget = new ComponentName(context, FinedustWidget.class);
        run();
    }

    private void Update(final Context context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                /*try {*/
                    runSearch(context);
                    Log.e("widget update", "runSerachupdate");
                    selectData(context);

                /*Thread.sleep(500); // 1s
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                runOnUiThread(new Runnable() {
                    public void run() { // 메시지 큐에 저장될 메시지의 내용 textView.setText("runOnUiThread 님을 통해 텍스트 설정"); } });
                        SharedPreferences data = context.getSharedPreferences("data", MODE_PRIVATE);

                        String location = data.getString("location", null);
                        String dataTime = data.getString("dataTime", null);
                        String cityName = data.getString("cityName", null);
                        String pm10Value = data.getString("pm10Value", null);
                        String pm25Value = data.getString("pm25Value", null);
                        Log.e("widget update", "update get");

                        if (pm10Value == null && pm25Value == null) {

                            Log.e("get data null", "update action");
                        } else {
                            //미세먼지
                            if (Integer.parseInt(pm10Value) <= 30) {
                                views.setImageViewResource(R.id.image_1, R.drawable.iconfinder_good);
                                views.setTextViewText(R.id.pm10Value_state, "좋음");
                                views.setInt(R.id.pm10Value_widget, "setBackgroundResource", R.drawable.good_radius);
                                views.setInt(R.id.information_layout, "setBackgroundResource", R.drawable.good_radius);
                                //views.setInt(R.id.main_background_widget, "setBackgroundColor", context.getResources().getColor(R.color.colorGood));
                                //views.setInt(R.id.Top_color, "setBackgroundColor", context.getResources().getColor(R.color.colorbarGood));
                                views.setInt(R.id.Top_color, "setBackgroundResource", R.drawable.radius_top);
                                views.setInt(R.id.main_background_widget, "setBackgroundResource", R.drawable.radius_bottom);
                                views.setInt(R.id.image_1, "setColorFilter", Color.WHITE);
                            } else if (Integer.parseInt(pm10Value) <= 80) {
                                views.setImageViewResource(R.id.image_1, R.drawable.iconfinder_normal);
                                views.setTextViewText(R.id.pm10Value_state, "보통");
                                views.setInt(R.id.pm10Value_widget, "setBackgroundResource", R.drawable.normal_radius);
                                views.setInt(R.id.information_layout, "setBackgroundResource", R.drawable.normal_radius);
                                // views.setInt(R.id.main_background_widget, "setBackgroundColor", context.getResources().getColor(R.color.colorNormal));
                                // views.setInt(R.id.Top_color, "setBackgroundColor", context.getResources().getColor(R.color.colorbarNormal));
                                views.setInt(R.id.Top_color, "setBackgroundResource", R.drawable.radius_top_normal);
                                views.setInt(R.id.main_background_widget, "setBackgroundResource", R.drawable.radius_bottom_normal);
                                views.setInt(R.id.image_1, "setColorFilter", Color.WHITE);
                            } else if (Integer.parseInt(pm10Value) <= 150) {
                                views.setImageViewResource(R.id.image_1, R.drawable.iconfinder_bad);
                                views.setTextViewText(R.id.pm10Value_state, "나쁨");
                                views.setInt(R.id.pm10Value_widget, "setBackgroundResource", R.drawable.bad_radius);
                                views.setInt(R.id.information_layout, "setBackgroundResource", R.drawable.bad_radius);
                                //views.setInt(R.id.main_background_widget, "setBackgroundColor", context.getResources().getColor(R.color.colorBad));
                                // views.setInt(R.id.Top_color, "setBackgroundColor", context.getResources().getColor(R.color.colorbarBad));
                                views.setInt(R.id.Top_color, "setBackgroundResource", R.drawable.radius_top_bad);
                                views.setInt(R.id.main_background_widget, "setBackgroundResource", R.drawable.radius_bottom_bad);
                                views.setInt(R.id.image_1, "setColorFilter", Color.WHITE);
                            } else if (Integer.parseInt(pm10Value) >= 151) {
                                views.setImageViewResource(R.id.image_1, R.drawable.iconfinder_verybad);
                                views.setTextViewText(R.id.pm10Value_state, "최악");
                                views.setInt(R.id.pm10Value_widget, "setBackgroundResource", R.drawable.verybad_radius);
                                views.setInt(R.id.information_layout, "setBackgroundResource", R.drawable.verybad_radius);
                                // views.setInt(R.id.main_background_widget, "setBackgroundColor", context.getResources().getColor(R.color.colorVeryBad));
                                // views.setInt(R.id.Top_color, "setBackgroundColor", context.getResources().getColor(R.color.colorbarVeryBad));
                                views.setInt(R.id.Top_color, "setBackgroundResource", R.drawable.radius_top_verybad);
                                views.setInt(R.id.main_background_widget, "setBackgroundResource", R.drawable.radius_bottom_verybad);
                                views.setInt(R.id.image_1, "setColorFilter", Color.WHITE);
                            }
                            //초미세먼지
                            if (Integer.parseInt(pm25Value) <= 15) {
                                views.setImageViewResource(R.id.image_2, R.drawable.iconfinder_good);
                                views.setTextViewText(R.id.pm25Value_state, "좋음");
                                views.setInt(R.id.pm25Value_widget, "setBackgroundResource", R.drawable.good_radius);
                                views.setInt(R.id.image_2, "setColorFilter", Color.WHITE);

                            } else if (Integer.parseInt(pm25Value) <= 16) {
                                views.setImageViewResource(R.id.image_2, R.drawable.iconfinder_normal);
                                views.setTextViewText(R.id.pm25Value_state, "보통");
                                views.setInt(R.id.pm25Value_widget, "setBackgroundResource", R.drawable.normal_radius);
                                views.setInt(R.id.image_2, "setColorFilter", Color.WHITE);

                            } else if (Integer.parseInt(pm25Value) <= 75) {
                                views.setImageViewResource(R.id.image_2, R.drawable.iconfinder_bad);
                                views.setTextViewText(R.id.pm25Value_state, "나쁨");
                                views.setInt(R.id.pm25Value_widget, "setBackgroundResource", R.drawable.bad_radius);
                                views.setInt(R.id.image_2, "setColorFilter", Color.WHITE);

                            } else if (Integer.parseInt(pm25Value) >= 76) {
                                views.setImageViewResource(R.id.image_2, R.drawable.iconfinder_verybad);
                                views.setTextViewText(R.id.pm25Value_state, "최악");
                                views.setInt(R.id.pm25Value_widget, "setBackgroundResource", R.drawable.verybad_radius);
                                views.setInt(R.id.image_2, "setColorFilter", Color.WHITE);

                            }
                            views.setTextViewText(R.id.widget_location, location + " " + cityName);
                            views.setTextColor(R.id.widget_location, Color.WHITE);


                            views.setTextViewText(R.id.widget_time, dataTime);
                            views.setTextColor(R.id.widget_time, Color.WHITE);


                            // Instruct the widget manager to update the widget
                            appWidgetManager.updateAppWidget(appWidgetid, views);
                            Log.e("end", "update ");
                        }

                    }
                });

            }
        }).start();

    }

    private static void runSearch(Context context) {

        //Data Clear
        for (int i = 0; i < 31; i++) {
            for (int j = 0; j < 7; j++) {
                Data[i][j] = null;
            }
        }
        count = 0;

        SharedPreferences data = context.getSharedPreferences("data", MODE_PRIVATE);
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
                            Log.e("widget update", cityName);
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

    private static void selectData(Context context) {

        SharedPreferences data = context.getSharedPreferences("data", MODE_PRIVATE);
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
        Log.e("commit","complete");

    }

    public void run() {
        Update(context);
    }
}
