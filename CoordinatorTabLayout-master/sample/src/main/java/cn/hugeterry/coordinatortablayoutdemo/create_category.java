package cn.hugeterry.coordinatortablayoutdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// 카테고리 추가 화면
public class create_category extends AppCompatActivity {
    Button addBtn, back;
    EditText category;
    String category_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_category);

        addBtn = (Button)findViewById(R.id.addBtn);
        back = (Button)findViewById(R.id.backBtn);
        category = (EditText)findViewById(R.id.category);

        //초기화
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        //툴바 설정
        toolbar.setTitleTextColor(Color.parseColor("#ffff33")); //제목의 칼라
        toolbar.setSubtitle(R.string.addCategory); //부제목 넣기
        toolbar.setNavigationIcon(R.mipmap.ic_launcher); //제목앞에 아이콘 넣기
        setSupportActionBar(toolbar); //툴바를 액션바와 같게 만들어 준다.


        // 카테고리 추가 + 카테고리 이름 그 전 Activity 에 전달 --> 리스트 뷰에 추가해야함.
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                category_name = category.getText().toString();
                /*
                Toast.makeText(getApplicationContext(),
                        "Word category name: "+category_name
                        ,Toast.LENGTH_SHORT).show();
                        */
                Intent intent = new Intent();
                intent.putExtra("category_name",category_name);
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
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent); // 아무 내용도 return X
    }
}
