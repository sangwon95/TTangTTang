package com.firstapp.ttangfinedust;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class FinedustActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private EditText editText;
    private ListView listview;
    private ArrayList<String> city = new ArrayList<>();
    private ArrayList<String> data = new ArrayList<>();

    private String key="" ;
    private int city_number = 0;
    private String dataTime, cityName, pm10Value, pm25Value, coValue, o3Value, no2Value,so2Value;
    private String[][] Data = new String[31][8];
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finedust);

        //search editText
        editText = findViewById(R.id.search_edt);
        //list
        listview = findViewById(R.id.listview);

        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setOnEditorActionListener(this);
    }

    //Data parser
    public ArrayList<String> getXmlData() {
        //Data Clear
        for (int i = 0; i < 31; i++) {
            for (int j = 0; j < 8; j++) {
                Data[i][j] = null;

            }
        }
        count = 0;

        String search_city_name = editText.getText().toString();

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

                city.clear();//ArrayList 비우기

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    switch (eventType) {

                        case XmlPullParser.START_DOCUMENT:
                            break;

                        case XmlPullParser.START_TAG:
                            tag = parser.getName(); // 태그 이름 얻어오기

                            if (tag.equals("item")) ; // 첫번째 검색결과
                            else if (tag.equals("dataTime")) {//측정시간
                                parser.next();
                                dataTime = parser.getText();
                                Data[count][0] = dataTime;

                            } else if (tag.equals("cityName")) {//도시명
                                parser.next();
                                cityName = parser.getText();
                                Data[count][1] = cityName;
                            }
                            else if (tag.equals("so2Value")) {//이황산가스
                                parser.next();
                                so2Value = parser.getText();
                                Data[count][2] = so2Value;
                            }
                            else if (tag.equals("coValue")) {//일산화탄소
                                parser.next();
                                coValue = parser.getText();
                                Data[count][3] = coValue;

                            } else if (tag.equals("o3Value")) {//오존
                                parser.next();
                                o3Value = parser.getText();
                                Data[count][4] = o3Value;

                            } else if (tag.equals("no2Value")) {//이산화질소
                                parser.next();
                                no2Value = parser.getText();
                                Data[count][5] = no2Value;

                            } else if (tag.equals("pm10Value")) {//미세먼지
                                parser.next();
                                pm10Value = parser.getText();
                                Data[count][6] = pm10Value;

                            } else if (tag.equals("pm25Value")) {//초미세먼지
                                parser.next();
                                pm25Value = parser.getText();
                                Data[count][7] = pm25Value;
                                count++;
                                city.add("\n" + "  지역: " + cityName + "\n" + "  미세먼지: " + pm10Value + "㎍/㎥" + "   초미세먼지: " + pm25Value + "㎍/㎥" + "\n" + "  측정시간: " + dataTime + "\n");

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

        return city;
    }

    //키보드 확인버튼(search)
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.search_edt && actionId == EditorInfo.IME_ACTION_SEARCH) {

            InputMethodManager mInputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                String check_string =editText.getText().toString();
                if(check_string.equals("경기")||check_string.equals("서울")||check_string.equals("대전")||check_string.equals("충북")||check_string.equals("충남")||check_string.equals("전북")||
                        check_string.equals("전남")||check_string.equals("제주")||check_string.equals("세종")||check_string.equals("강원")||check_string.equals("인천")||check_string.equals("광주")||
                        check_string.equals("울산")||check_string.equals("대구")||check_string.equals("부산")||check_string.equals("경남")||check_string.equals("경북")) {


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            data = getXmlData();
                            // 아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, data);
                                    adapter.notifyDataSetChanged();
                                    listview.setAdapter(adapter);

                                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            String location = editText.getText().toString();

                                            SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
                                            SharedPreferences.Editor initial = data.edit();
                                            initial.clear();

                                            Intent intent = new Intent(FinedustActivity.this, MainActivity.class);

                                            initial.putString("dataTime", Data[position][0]);
                                            initial.putString("cityName", Data[position][1]);
                                            initial.putString("so2Value", Data[position][2]);
                                            initial.putString("coValue", Data[position][3]);
                                            initial.putString("o3Value", Data[position][4]);
                                            initial.putString("no2Value", Data[position][5]);
                                            initial.putString("pm10Value", Data[position][6]);
                                            initial.putString("pm25Value", Data[position][7]);
                                            initial.putString("location", location);
                                            initial.putInt("position", position);
                                            initial.commit();

                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    });

                                }
                            });
                        }
                    }).start();
                }
                else{
                    Toast.makeText(FinedustActivity.this,"도시 이름을 다시 작성해주세요.",Toast.LENGTH_LONG).show();
                }

        }
        return true;
    }
}
