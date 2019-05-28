package cn.hugeterry.coordinatortablayoutdemo;


import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class wrongNote_Activity extends AppCompatActivity {
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;
    String[] listItems;

    ArrayList<String> mSpelling, mMeaning;

    private ViewPager viewPager ;
    private TextViewPagerAdapter pagerAdapter ;
    private String number;

    int selectType;

    String category_name;
    Button addBtn;
    int i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ,,, 코드 계속.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrong_note);

        //DB
        helper = new MySQLiteOpenHelper(getApplicationContext(), "Remembering.db", null,1);


        init();


        //초기화
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        //툴바 설정
        toolbar.setTitleTextColor(Color.parseColor("#ffff33")); //제목의 칼라
        toolbar.setSubtitle(category_name); //부제목 넣기
        toolbar.setNavigationIcon(R.mipmap.ic_launcher); //제목앞에 아이콘 넣기
        setSupportActionBar(toolbar); //툴바를 액션바와 같게 만들어 준다.


        Button DBbtn = (Button)findViewById(R.id.showDB); // DB 보기
        DBbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int position;
                position = viewPager.getCurrentItem(); // 현재 보여지는 아이템의 위치를 리턴
                show_word();
                //helper.onUpgrade(db,0,0);
            }
        });


        // 현재 아이템을 철자 -> 뜻으로
        Button showBtn = (Button)findViewById(R.id.showMeaning); // 뜻 보기
        showBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // 뜻 보기
                int position;
                position = viewPager.getCurrentItem(); // 현재 보여지는 아이템의 위치를 리턴

                //View view = viewPager.getChildAt(viewPager.getCurrentItem());
                //view.findViewById(R.id.title);
                //View view = (View)viewPager.getChildAt(viewPager.getCurrentItem());
                //viewPager.findViewById(viewPager.getCurrentItem())
                pagerAdapter.showMeaning(viewPager);
                //View view = viewPager.findViewWithTag(viewPager.getCurrentItem());
                // pagerAdapter.showMeaning(view, position);
                //TextView text = (TextView)view.findViewById(R.id.title);
                //text.setText()



            }
        });

        // 삭제하기 -> Wrong_N 이 3이상인 걸 -> 0으로 바꾸고 && List에서 제거
        Button delBtn = (Button)findViewById(R.id.del); // 삭제하기 -> Wrong_N 이 3이상인 걸 -> 0으로 바꾸고 && List에서 제거
        delBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int position = viewPager.getCurrentItem();
                if(pagerAdapter.getSpelling().size() ==1){
                    // Start Page 만 있다는 뜻.
                    // 삭제하지 않기
                }
                else {
                    // Start Page 외에 더 있음 -> 삭제 가능
                    delete_word(pagerAdapter.getSpelling().get(position)); // DB에서 현재꺼 삭제
                    pagerAdapter.chagneData(position); // 데이터 셋에서 현재꺼 빼기
                    pagerAdapter.notifyDataSetChanged();

                    //Toast.makeText(getApplicationContext(),"Delete : '"+pagerAdapter.getSpelling().get(position)+"'",Toast.LENGTH_SHORT).show();
                }

                //viewPager.setCurrentItem(position-1,true);

            }
        });






    }


    // 단어 삭제 -> 걍 삭제가 아니라 wrong_N 을 3에서 0으로 설정하고 List에서 제거
    public void delete_word(String spelling) {
        db = helper.getWritableDatabase();
        String sql = "update contents set wrong_N = 0 where spelling='"+spelling+"';";
        db.execSQL(sql);


        // List 에서 제거하기 추가


        //Toast 정상 삭제
    }

    public void show_word(){ // 오답노트에 해당되는 list들 보여주기
        db =helper.getReadableDatabase();
        ArrayList<String> mSpell, mMean;
        mSpell = new ArrayList<String>();
        mMean = new ArrayList<String>();

        Cursor c = db.rawQuery("select spelling,meaning,category_name from contents where wrong_N >= 4" , null);

        while(c.moveToNext()){
            //Toast.makeText(this,"들어왔다",Toast.LENGTH_SHORT).show();
            mSpell.add(c.getString(0)); // 철자 얻기
            mMean.add(c.getString(1)); // 의미 얻기
            //Log.i("Show Word","Word - Spelling: "+c.getString(0));
            Log.i("Wrong Note","Spelling: "+c.getString(0));
            Log.i("Wrong Note","Meaning: "+c.getString(1));
            Log.i("Wrong Note","category: "+c.getString(2));

        }
        //Log.i("Show Word - Spell","Word - Spelling: "+pagerAdapter.getSpelling());
        //Log.i("Show Word - Spell","Word - Meaning: "+pagerAdapter.getMeaning());
        //Log.i("Show Word - Size","Word - Size: "+pagerAdapter.getCount());
    }






    public void init(){

        Intent intent = getIntent();
        category_name = intent.getStringExtra("category_name"); // <-- 툴바에 이걸로 제목 설정
        db =helper.getReadableDatabase();

        mSpelling = new ArrayList<String>();
        mMeaning = new ArrayList<String>();

        // 첫 초기화시, 가져오기
        Cursor c = db.rawQuery("select spelling,meaning,category_name from contents where wrong_N >= 4" , null);

        while(c.moveToNext()){
            //Toast.makeText(this,"들어왔다",Toast.LENGTH_SHORT).show();
            mSpelling.add(c.getString(0)); // 철자 얻기
            mMeaning.add(c.getString(1)); // 의미 얻기
            //Log.i("Show Word","Word - Spelling: "+c.getString(0));
            Log.i("Wrong Note","Spelling: "+c.getString(0));
            Log.i("Wrong Note","Meaning: "+c.getString(1));
            Log.i("Wrong Note","category: "+c.getString(2));

        }

        viewPager = (ViewPager) findViewById(R.id.viewPager) ;
        pagerAdapter = new TextViewPagerAdapter(this,mSpelling,mMeaning) ;
        viewPager.setAdapter(pagerAdapter) ;


    }






}