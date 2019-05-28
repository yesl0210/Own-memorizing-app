package cn.hugeterry.coordinatortablayoutdemo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hugeterry(http://hugeterry.cn)
 */
public class MainFragment extends Fragment {
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    private RecyclerView mRecyclerView;
    private ListView mListView;
    Button btn,showBtn;
    private int delIndex;

    //private List<String> mDatas;
    private ArrayList<String> mDatas;
    private ArrayAdapter<String> adapter;

    private static final String ARG_TITLE = "title";
    private String mTitle;

    public static MainFragment getInstance(String title) {

        MainFragment fra = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        fra.setArguments(bundle);
        return fra;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mTitle = bundle.getString(ARG_TITLE);

        // DB
        helper = new MySQLiteOpenHelper(getActivity(), "Remembering.db", null,1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);


        initData();
        btn = (Button)v.findViewById(R.id.btn);//************
        if(mTitle == "Words") {
            btn.setText("Words Category 추가");
        }
        else if (mTitle == "Sentences") {
            btn.setText("Sentences Category 추가");
        }
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),create_category.class);
                if(mTitle == "Words") {
                    btn.setText("Words 눌렸다");
                    //Intent intent = new Intent(getActivity(),create_category.class);
                    //startActivity(intent);
                    startActivityForResult(intent,1); // 1 -> Word

                }
                else if (mTitle == "Sentences") {
                    btn.setText("Sentences 눌렸다");
                    //Intent intent = new Intent(getActivity(),create_category.class);

                    //startActivity(intent);
                    startActivityForResult(intent,2); // 2 -> Sentence
                }
            }
        });

        showBtn = (Button)v.findViewById(R.id.showBtn);
        showBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                select();
            }
        });


        /*
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mRecyclerView.setAdapter(new RecyclerAdapter(mRecyclerView.getContext(), mDatas));
        */

        // ***************************************************************

        mListView = (ListView)v.findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(mListView.getContext(),
                android.R.layout.simple_list_item_1,mDatas);
        //.setAdapter(new ListAdapter(mListView.getContext(),mDatas));
        mListView.setAdapter(adapter);
        // mListView 초이스 모드

        // 리스트 아이템 터치했을때
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str = "Click "+i + "th";
                Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT).show();

                if(mTitle == "Words"){
                    // 여기서 seeWord로 가도록
                    Intent intent = new Intent(getActivity(),seeWord_Activity.class);
                    intent.putExtra("category_name",mDatas.get(i));
                    startActivity(intent);
                }
                else if (mTitle == "Sentences"){
                    // 여기서 seeSentence로 가도록
                    Intent intent = new Intent(getActivity(),seeSentence_Activity.class);
                    intent.putExtra("category_name",mDatas.get(i));
                    startActivity(intent);
                }



            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                delIndex = i;

                new AlertDialog.Builder(getActivity())
                        .setTitle("카테고리 삭제")
                        .setMessage("정말 삭제할거냐?")
                        .setIcon(android.R.drawable.ic_menu_save)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String str = "Delete "+ delIndex + "th";
                                // 확인시 처리 로직
                                delete_category(mDatas.get(delIndex)); // <----------------여기 추가
                                Log.i("Delete Category","category - name: "+mDatas.get(delIndex));
                                mDatas.remove(delIndex); // <--------------------method override 해서 i 넣을 수 있도록
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                                                          }})
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 취소시 처리 로직
                                Toast.makeText(getActivity(), "삭제ㄴㄴㄴㄴ.", Toast.LENGTH_SHORT).show();
                            }})
                        .show();






                //Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT).show();


                return true; // 롱클릭시 클릭 인식 안 되도록
            }
        });

        // ****************************************************************

        return v;
    }

    // 카테고리 추가
    // type - 문장인지 단어인지 1 - 단어, 2 - 문장
    // ----> listView 에 카테고리 리스트 보여줄 때, 단어 문장 구분해서 하기 위함
    public void insert_category(String category_name, int type) {
        db = helper.getWritableDatabase();
        ContentValues contents = new ContentValues();

        contents.put("category_name", category_name);
        contents.put("type",type);

        db.insert("category",null,contents);
    }

    // 카테고리 삭제
    public void delete_category(String category_name) {
        db = helper.getWritableDatabase();
        db.delete("category","category_name=?",new String[]{category_name});
        //Toast 정상 삭제
    }

    public void select(){

        db = helper.getReadableDatabase();
        String sql = "select * from category where type=1"; // 단어
        Cursor c = db.rawQuery(sql, null);

        //Cursor c = db.query("category",null,"type=1",null,null,null,null);
        while(c.moveToNext()){
            String category_name = c.getString(c.getColumnIndex("category_name"));
            Log.i("showCategory","Words category - name: "+category_name);
        }

        sql = "select * from category where type=2"; // 문장
        c = db.rawQuery(sql, null);
        while(c.moveToNext()){
            String category_name = c.getString(c.getColumnIndex("category_name"));
            Log.i("showCategory","Sentences category - name: "+category_name);
        }


    }

    private void initData() {
       // mDatas = new ArrayList<>(); // listview 로 바꾸든가 해야할듯

        mDatas = new ArrayList<String>();
        // category DB에서 category_name 가져와야함
        // 문장인 경우 - 문장에 추가, 단어인 경우- 단어에 추가

        db = helper.getReadableDatabase();
        String sql="";

        if(mTitle == "Words") {
            sql = "select * from category where type=1"; // 단어

        }
        else if(mTitle == "Sentences"){
            sql = "select * from category where type=2"; // 문장
        }

        Cursor c = db.rawQuery(sql, null);
        while(c.moveToNext()){
            mDatas.add(c.getString(c.getColumnIndex("category_name"))); // category_name 얻어 추가하기
        }
        //Cursor c = db.query("select * from category where type=1");


        /*
        for (int i = 1; i < 31; i++) { // 원하는 만큼 추가 가능 & 카테고리 추가할 때마다 리스트뷰 추가되도록 하기
            mDatas.add(mTitle + i);
        }
        */

    }


    // Result가 도착했을 때 -> Category 추가
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getActivity(),"Right",Toast.LENGTH_SHORT).show();
        String category_name;

        // category DB에 추가

        if(requestCode == 1){ // word
            if(resultCode == Activity.RESULT_OK) {
                category_name = data.getStringExtra("category_name");
                insert_category(category_name,1);
                Log.i("Insert Category","Words category - name: "+category_name);
                mDatas.add(category_name);
                adapter.notifyDataSetChanged();
            }/*
            else if (resultCode == Activity.RESULT_CANCELED) {
                // 반환값이 없을 경우, 유지
            }*/
        }
        else if (requestCode == 2) { // sentence
            if(resultCode == Activity.RESULT_OK) {
                category_name = data.getStringExtra("category_name");
                insert_category(category_name,2);

                Log.i("Insert Category","Sentences category - name: "+category_name);
                mDatas.add(category_name);
                adapter.notifyDataSetChanged();
            } /*
            else if (resultCode == Activity.RESULT_CANCELED) {
                // 반환값이 없을 경우, 유지
            }*/
        }
        else if(resultCode == Activity.RESULT_CANCELED){
            // 반환값이 없을 경우, 유지
        }
    }

    // ListView 선택 -> 단어 보기
    // LIstView 꾹 누르기 -> 그 카테고리 삭제
}