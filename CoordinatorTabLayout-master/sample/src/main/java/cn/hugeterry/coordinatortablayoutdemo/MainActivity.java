package cn.hugeterry.coordinatortablayoutdemo;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout;

/**
 * Created by hugeterry(http://hugeterry.cn)
 */
public class MainActivity extends AppCompatActivity {
    private CoordinatorTabLayout mCoordinatorTabLayout;
    private int[] mImageArray, mColorArray;
    private ArrayList<Fragment> mFragments;
    private final String[] mTitles = {"Words", "Sentences"};
    private ViewPager mViewPager;

    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragments();
        initViewPager();
        mImageArray = new int[]{
                R.mipmap.bg_android,
                R.mipmap.bg_ios,
                R.mipmap.bg_js,
                R.mipmap.bg_other};
        mColorArray = new int[]{
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light};

        mCoordinatorTabLayout = (CoordinatorTabLayout) findViewById(R.id.coordinatortablayout);
        mCoordinatorTabLayout.setTranslucentStatusBar(this)
                .setTitle("Demooooooooooooo")
                .setBackEnable(true)
                .setImageArray(mImageArray, mColorArray)
                .setupWithViewPager(mViewPager);

    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        for (String title : mTitles) {
            mFragments.add(MainFragment.getInstance(title));
        }
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.vp);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        helper = new MySQLiteOpenHelper(getApplicationContext(), "Remembering.db", null,1);
        db = helper.getReadableDatabase();

        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("카테고리 검색");
        MenuItem item_like = menu.add(0,0,0,"오답노트");
        item_like.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {



                Intent wrongIntent = new Intent(getApplicationContext(), wrongNote_Activity.class);
                startActivity(wrongIntent);

                return true;
            }
        });
        // 리스너 구현
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(getApplicationContext(),"검색완료 : "+s,Toast.LENGTH_SHORT).show();

                /////////////
                String sql = "select category_name,spelling, meaning from contents where spelling='"+s+"' or meaning = '"+s+"';"; // 단어
                Cursor c = db.rawQuery(sql, null);

                //Cursor c = db.query("category",null,"type=1",null,null,null,null);
                if(c == null || c.getCount() ==0){
                    Toast.makeText(getApplicationContext(),"다음 내용 없음 : "+s,Toast.LENGTH_SHORT).show();
                }
                else {
                    while(c.moveToNext()){
                        Toast.makeText(getApplicationContext(),
                                "category: "+c.getString(0)+", spelling: "+c.getString(1)+", meaning: "+c.getString(2),
                                Toast.LENGTH_SHORT).show();
                        Log.i("Search","Search contents - category : "+c.getString(0)+", spelling : "+c.getString(1)+
                                ", meaning : "+c.getString(2));
                    }
                }




                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                //Toast.makeText(getApplicationContext(),"입력중 : "+s,Toast.LENGTH_SHORT).show();
                return false;
            }
        };
        searchView.setOnQueryTextListener(listener);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        if(null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(true);



        return true;
    }
}
