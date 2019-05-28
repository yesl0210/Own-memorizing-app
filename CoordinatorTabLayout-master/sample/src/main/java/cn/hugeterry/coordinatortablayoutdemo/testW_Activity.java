package cn.hugeterry.coordinatortablayoutdemo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class testW_Activity extends AppCompatActivity {
    Button wrong, correct, showM;
    TextView contents;
    ArrayList<String> spelling, meaning;
    int pos=1,start=0; // 첫번째 페이지때문에 1부터 시작
   // String size;
    int size;
    String type;

    SQLiteDatabase db;
    MySQLiteOpenHelper helper;
    String category_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_word);
        // Intent intent = getIntent();
        // ArrayList<String> spelling = intent.getStringArrayList("spelling")



        Intent intent = getIntent();
        size = Integer.parseInt(intent.getStringExtra("TestNum"));
        size +=1;

        type = intent.getStringExtra("TestType");

        spelling = new ArrayList<String>();
        spelling = intent.getStringArrayListExtra("spelling"); // <-- 랜덤인 경우 여기서 갯수만큼 랜덤으로 뽑으면 됨
        meaning = new ArrayList<String>();
        meaning = intent.getStringArrayListExtra("meaning");

        category_name = intent.getStringExtra("category_name");

        wrong = (Button)findViewById(R.id.wrong);
        correct=(Button)findViewById(R.id.correct);
        showM=(Button)findViewById(R.id.showMeaning);
        contents = (TextView)findViewById(R.id.testTxt);
        Toast.makeText(this, "category: "+category_name, Toast.LENGTH_SHORT).show();

        // 몰라요 버튼 + 알아요 버튼 누르면 바로 다음으로 넘어가기

        // 몰라요
        wrong.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if(pos==1 && start==0){ // --> 처음이야.
                    start=1; // 시작했어

                }
                else {
                    // worng +1 하기
                    UpdateWrongN(category_name,spelling.get(pos)); //<---수정하기
                    pos +=1; // 다음으로 넘어가기
                }


                if(pos == size){ // size= 6개 감. , startPage 때문에 0(startPage),1,2,3,4,5 -> 이렇게 감
                    // 5 보고 나서 pos 는 6됨
                    Toast.makeText(getApplicationContext(),"Test End",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    contents.setText((pos)+"th Spelling:\n\n"+spelling.get(pos));
                }


            }
        });

        // 알아요
        correct.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if(pos==1 && start==0){ // --> 처음이야.
                    start=1; // 시작했어

                }
                else {
                    pos +=1; // 다음으로 넘어가기
                }


                // worng +1 안 해도 됨
                //Toast.makeText(getApplicationContext(),"pos: "+pos+", size: "+size,Toast.LENGTH_SHORT).show();

                //Toast.makeText(getApplicationContext(),"pos: "+pos+", size: "+size,Toast.LENGTH_SHORT).show();
                if(pos == size){
                    Toast.makeText(getApplicationContext(),"Test End",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    contents.setText((pos)+"th Spelling:\n\n"+spelling.get(pos));
                }

            }
        });

        // 뜻보기
        showM.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(pos==1 && start==0){ // --> 처음이야.
                    start=1; // 시작했어
                    contents.setText((pos)+"th Spelling:\n\n"+spelling.get(pos));

                }
                else {
                    contents.setText((pos)+"th Meaning:\n\n"+meaning.get(pos));
                }


            }
        });




    }

    public void UpdateWrongN(String name, String spelling){

        helper = new MySQLiteOpenHelper(this, "Remembering.db", null,1);
        db = helper.getWritableDatabase();

        String sql = "update contents set wrong_N = wrong_N+1 where category_name='"+name+ "' and spelling='"+spelling+"';";

        db.execSQL(sql);

    }


}
