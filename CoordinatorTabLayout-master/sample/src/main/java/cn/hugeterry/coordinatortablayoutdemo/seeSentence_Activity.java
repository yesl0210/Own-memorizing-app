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

public class seeSentence_Activity extends AppCompatActivity {
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
        setContentView(R.layout.seesentence_main);

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


        Button addBtn = (Button)findViewById(R.id.add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //추가 페이지로 가기
                Intent intent2 = new Intent(getApplicationContext(),createS_Activity.class);
                intent2.putExtra("category_name",category_name); // 카테고리 넘기기
                startActivityForResult(intent2,1); // requestCode 의미없음.

                Toast.makeText(getApplicationContext(),"Current item: "+viewPager.getCurrentItem(), Toast.LENGTH_SHORT).show();



            }
        });

        Button DBbtn = (Button)findViewById(R.id.showDB);
        DBbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int position;
                position = viewPager.getCurrentItem(); // 현재 보여지는 아이템의 위치를 리턴
                show_sentence();
                //helper.onUpgrade(db,0,0);
            }
        });


        // 현재 아이템을 철자 -> 뜻으로
        Button showBtn = (Button)findViewById(R.id.showMeaning);
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

        // 이전 버튼 누르면 이전 페이지로
        Button delBtn = (Button)findViewById(R.id.del);
        delBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int position = viewPager.getCurrentItem();
                if(pagerAdapter.getSpelling().size() ==1){
                    // Start Page 만 있다는 뜻.
                    // 삭제하지 않기
                }
                else {
                    // Start Page 외에 더 있음 -> 삭제 가능
                    delete_sentence(pagerAdapter.getSpelling().get(position)); // DB에서 현재꺼 삭제
                    pagerAdapter.chagneData(position); // 데이터 셋에서 현재꺼 빼기
                    pagerAdapter.notifyDataSetChanged();

                    //Toast.makeText(getApplicationContext(),"Delete : '"+pagerAdapter.getSpelling().get(position)+"'",Toast.LENGTH_SHORT).show();
                }

                //viewPager.setCurrentItem(position-1,true);




            }
        });






    }


    // Result가 도착했을 때 -> 단어 DB에 & Pager에 추가
    // // category_name text, spelling text, meaning text, wrong_N integer
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(getApplicationContext(),"Right",Toast.LENGTH_SHORT).show();
        String spelling = data.getStringExtra("spelling");
        String meaning = data.getStringExtra("meaning");

        // 단어 DB & Pager에 추가
        if(requestCode == 1){ //
            if(resultCode == Activity.RESULT_OK) {

                insert_sentence(spelling,meaning);
                pagerAdapter.addPage(spelling,meaning); // spelling 은 어떻게 넣을지 고민
                Log.i("Insert Sentence","Sentence - Spelling: "+spelling+", Meaning: "+meaning);
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED){
            // 반환값이 없을 경우, 유지
            Toast.makeText(getApplicationContext(),"아무것도 안 받았다",Toast.LENGTH_SHORT).show();
        }
    }

    // 단어 추가
    // category_name text, spelling text, meaning text, wrong_N integer
    public void insert_sentence(String spelling, String meaning) {
        db = helper.getWritableDatabase();
        ContentValues contents = new ContentValues();

        contents.put("category_name", category_name);
        contents.put("spelling",spelling);
        contents.put("meaning",meaning);
        contents.put("wrong_N",0);

        db.insert("contents",null,contents);
    }

    // 단어 삭제
    public void delete_sentence(String spelling) {
        db = helper.getWritableDatabase();
        db.delete("contents","spelling=?",new String[]{spelling});
        //Toast 정상 삭제
    }

    public void show_sentence(){
        db =helper.getReadableDatabase();
        ArrayList<String> mSpell, mMean;
        mSpell = new ArrayList<String>();
        mMean = new ArrayList<String>();

        String sql = "select * from contents where category_name="+category_name; // 철자
        Cursor c = db.rawQuery("select spelling,meaning,category_name from contents where category_name='"+category_name+"';" , null);

        while(c.moveToNext()){
            //Toast.makeText(this,"들어왔다",Toast.LENGTH_SHORT).show();
            mSpell.add(c.getString(0)); // 철자 얻기
            mMean.add(c.getString(1)); // 의미 얻기
            //Log.i("Show Word","Word - Spelling: "+c.getString(0));
            Log.i("Show Sentence","Sentence - Spelling: "+c.getString(0));
            Log.i("Show Sentence","Sentence - Meaning: "+c.getString(1));
            Log.i("Show Sentence","Sentence - category: "+c.getString(2));

        }
        //Log.i("Show Word - Spell","Word - Spelling: "+pagerAdapter.getSpelling());
        //Log.i("Show Word - Spell","Word - Meaning: "+pagerAdapter.getMeaning());
        //Log.i("Show Word - Size","Word - Size: "+pagerAdapter.getCount());
    }






    public void init(){

        Intent intent = getIntent();
        category_name = intent.getStringExtra("category_name"); // <-- 툴바에 이걸로 제목 설정


        db=helper.getReadableDatabase();

        mSpelling = new ArrayList<String>();
        mMeaning = new ArrayList<String>();



        String sql = "select * from contents where category_name="+category_name; // 철자
        //Cursor c = db.rawQuery("SELECT spelling, meaning FROM contents where category_name='"+category_name+"';" , null);
        Cursor c = db.rawQuery("select spelling,meaning from contents where category_name='"+category_name+"';" , null);

        while(c.moveToNext()){
            //Toast.makeText(this,"들어왔다",Toast.LENGTH_SHORT).show();
            mSpelling.add(c.getString(0)); // 철자 얻기
            mMeaning.add(c.getString(1)); // 의미 얻기
            //Log.i("Select Word","Word - Spelling: "+c.getString(0));
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager) ;
        pagerAdapter = new TextViewPagerAdapter(this,mSpelling,mMeaning) ;
        viewPager.setAdapter(pagerAdapter) ;








/*
        Cursor c = db.rawQuery(sql, null);
        while(c.moveToNext()){
            mSpelling.add(c.getString(c.getColumnIndex("spelling"))); // 철자 얻기
            mMeaning.add(c.getString(c.getColumnIndex("meaning"))); // 의미 얻기
            Log.i("Select Word","Word - Spelling: "+c.getString(c.getColumnIndex("spelling")));
        }
        //Toast.makeText(this,mSpelling.size(),Toast.LENGTH_SHORT).show();
*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final List<String> ListItems = new ArrayList<>();

        ListItems .add("랜덤으로");
        ListItems .add("순서대로");
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        switch (item.getItemId()) {
            case R.id.test:
                Toast.makeText(getApplicationContext(),"Test",Toast.LENGTH_SHORT).show();


                final EditText editNum = new EditText(this);
                final EditText editType = new EditText(this);



                final AlertDialog.Builder testNum = new AlertDialog.Builder(this);
                final AlertDialog.Builder testType = new AlertDialog.Builder(this);

                testNum.setTitle("Test 시험지 설정하기")

                        .setMessage("Test 볼 갯수")

                        .setView(editNum)

                        .setPositiveButton("다음", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                number = editNum.getText().toString();

                                testType.setTitle("Test 시험지 설정하기")
                                        .setMessage("시험지 타입 : 랜덤으로/순서대로")
                                        .setView(editType)
                                        .setPositiveButton("만들기", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String type = editType.getText().toString();
                                                Toast.makeText(getApplicationContext(),"만들어짐",Toast.LENGTH_SHORT).show();

                                                Intent testIntent = new Intent(getApplicationContext(),testW_Activity.class);
                                                testIntent.putExtra("TestNum",number); // 테스트 볼 갯수 넘기기 +1 (첫 페이지 때문에)
                                                testIntent.putExtra("TestType",type);
                                                testIntent.putExtra("spelling",pagerAdapter.getSpelling());
                                                testIntent.putExtra("meaning",pagerAdapter.getMeaning());
                                                testIntent.putExtra("category_name",category_name);
                                                startActivity(testIntent);

                                                // Intent intent(test_word);
                                                // intent.putExtra("Test_Num",number);
                                                // intent.putExtra("Test_Type",type)
                                                //  getSpelling()
                                                // getMeaning()
                                                // intent.putExtra("spelling",spelling)
                                                // startActivity();
                                                // 저쪽에서는
                                                // Intent intent = getIntent();
                                                // ArrayList<String> spelling = intent.getStringArrayList("spelling")

                                            }
                                        })
                                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .create().show();
                            }

                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog alert = testNum.create();

                alert.show();





                break;
            case R.id.showWrong:
                Intent wrongIntent = new Intent (getApplicationContext(),wrongNote_Activity.class);
                startActivity(wrongIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.see_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }





}