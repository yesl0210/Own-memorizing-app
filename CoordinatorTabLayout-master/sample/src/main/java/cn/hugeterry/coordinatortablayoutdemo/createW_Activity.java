package cn.hugeterry.coordinatortablayoutdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class createW_Activity extends AppCompatActivity {
    Button addBtn, back, translationBtn;
    EditText editSpell, editMeaning;
    String spelling, meaning;
    private String result;

    // 백그라운드에서 파파고 API와 연결하여 번역 결과를 가져온다
    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            StringBuilder output = new StringBuilder();
            String clientId = "kdxEu9kBN37Ds4XBk8og";   //애플리케이션 클라이언트 아이디값";
            String clientSecret = "N8QOFlV0Dr"; //애플리케이션 클라이언트 시크릿값";
            try {
                //번역문을 UTF-8으로 인코딩한다
                String text = URLEncoder.encode(editSpell.getText().toString(), "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt"; // 파파고 API 서버 주소

                // 파파고 API 서버와 연결
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                // 번역할 문장을 파라미터로 전송
                String postParams = "source=en&target=ko&text=" + text; // 파파고 API 서버로 전달할 파라미터 설정, 한->영
                con.setDoOutput(true);  // 파파고 API 서버로 번역할 문장 전송
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();

                // 번역 결과를 받아온다
                int responseCode = con.getResponseCode();   // 파파고 API 서버로부터 번역된 메시지를 전달 받는다
                BufferedReader br;
                if (responseCode == 200) { // 번역에 성공한 경우
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생한 경우
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;   // 전달받은 메시지 출력
                while ((inputLine = br.readLine()) != null) {
                    output.append(inputLine);
                }
                br.close();
            } catch (Exception ex) {
                Log.e("SampleHTTP", "Exception in processing response.", ex);
                ex.printStackTrace();
            }
            result = output.toString();
            return null;
        }

        protected void onPostExecute(Integer a) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            if (element.getAsJsonObject().get("errorMessage") != null) {
                Log.e("번역 오류", "번역 오류가 발생했습니다." +
                        "[오류 코드: " + element.getAsJsonObject().get("errorCode").getAsString() + "]");
            } else if (element.getAsJsonObject().get("message") != null) {
                // 번역 결과 출력
                editMeaning.setText(element.getAsJsonObject().get("message").getAsJsonObject().get("result")
                        .getAsJsonObject().get("translatedText").getAsString());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_word);

        addBtn = (Button)findViewById(R.id.addBtn);
        back = (Button)findViewById(R.id.backBtn);
        editSpell= (EditText)findViewById(R.id.spelling);
        editMeaning= (EditText)findViewById(R.id.meaning);
        translationBtn = (Button) findViewById(R.id.translationBtn);

        //초기화
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        //툴바 설정
        toolbar.setTitleTextColor(Color.parseColor("#ffff33")); //제목의 칼라
        toolbar.setSubtitle(R.string.addWord); //부제목 넣기
        toolbar.setNavigationIcon(R.mipmap.ic_launcher); //제목앞에 아이콘 넣기
        setSupportActionBar(toolbar); //툴바를 액션바와 같게 만들어 준다.

        translationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundTask().execute();
            }
        });

        // 카테고리 얻어와야 함.
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // category_name text, spelling text, meaning text, wrong_N integer
                spelling = editSpell.getText().toString();
                meaning = editMeaning.getText().toString();
                /*
                Toast.makeText(getApplicationContext(),
                        "Word category name: "+category_name
                        ,Toast.LENGTH_SHORT).show();
                        */
                Intent intent = new Intent();
                intent.putExtra("spelling",spelling);
                intent.putExtra("meaning",meaning);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        // 이전 화면으로 돌아가기 + 카테고리 추가 취소
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Back to the previous screen",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });

    }

    // 뒤로 가기 버튼이 눌렸을 때
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent); // 아무 내용도 return X
        super.onBackPressed();
    }
}

