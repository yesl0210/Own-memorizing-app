package cn.hugeterry.coordinatortablayoutdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    // method 만들어서 table 이름 계속 바꿔주기
    String table_name ="table_name";
    private static String sql_for="";
    // 단어 table 생성시 sql 문
    private static String create_table_word = "";
    // 문장 table 생성시 sql 문
    private static String create_table_sentence = "";

    // changeParams 로 테입ㄹ 이름과 명령문 바꿔준뒤, 테이블 생성


    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.
            CursorFactory factory, int version){
        super(context,name, factory, version);
    }

    @Override
    // 이 두 Table에 내용 다 때려넣기
    // category Table 에 새 category 추가
    // Table 추가를 여기에서 해야함
    public void onCreate(SQLiteDatabase db) {
        // 기존에 카테고리 테이블이 있다면 삭제
        db.execSQL("DROP TABLE IF EXISTS category");
        // 기존에 컨텐츠 테이블이 있다면 삭제
        db.execSQL("DROP TABLE IF EXISTS contents");
        // category_name -> unique primary key
        db.execSQL("Create table category (category_name text, type integer);");
        // category_name -> 속하는 카테고리의 이름
        // alpha -> 철자
        // meaning -> 한국어 뜻
        // wrong_N -> 틀린 횟수
        db.execSQL("Create table contents (category_name text, spelling text, meaning text, wrong_N integer)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists contents"); // 이미 있으면 삭제
        db.execSQL("drop table if exists category"); // 이미 있으면 삭제

        onCreate(db); // db생성
    }



/*
    public void changeParams(String tName, String WorSorC) {
        // WorS -> 만들어야 하는 테이블이 단어인지 문장인지
        table_name = tName;
        if (WorSorC == "word") {
            // 단어일때
            create_table_word = "CREATE TABLE "
                    + table_name + "(word text, meaning text, wrong_N integer);";
        }
        else if(WorSorC == "sentence"){
            // 문장일때
            create_table_sentence =  "CREATE TABLE "
                    + table_name + "(sentence text, meaning text, wrong_N integer);";
        }
    }


    // Table 동적으로 생성
    public void createTable(SQLiteDatabase db, String tName, String WorS) {

        table_name = tName;
        db.execSQL("DROP TABLE IF EXISTS " + table_name);

        // WorS -> 만들어야 하는 테이블이 단어인지 문장인지
        if (WorS == "word") {
            // 단어일때
            create_table_word = "CREATE TABLE "
                    + table_name + "(word text, meaning text, wrong_N integer);";

            db.execSQL(create_table_word);
        }
        else if(WorS== "sentence"){
            // 문장일때
            create_table_sentence =  "CREATE TABLE "
                    + table_name + "(sentence text, meaning text, wrong_N integer);";

            db.execSQL(create_table_sentence);
        }
    }
*/

}
