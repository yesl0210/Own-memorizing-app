package cn.hugeterry.coordinatortablayoutdemo;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TextViewPagerAdapter extends PagerAdapter {

    // LayoutInflater 서비스 사용을 위한 Context 참조 저장.
    private Context mContext = null ;
    private ArrayList<String> mSpelling;
    private ArrayList<String> mMeaning;
    private TextView textView;
    private int position_G;

    private View mCurrentView;

    SQLiteDatabase db;
    MySQLiteOpenHelper helper;



    // Context를 전달받아 mContext에 저장하는 생성자 추가.
    // 초기화 하는 곳 ( 첨에 만들어지니까)
    public TextViewPagerAdapter(Context context, ArrayList<String> spelling, ArrayList<String> meaning) {
        //DB

        int i=0;
        int size = spelling.size();


        mContext = context ;
        mSpelling = new ArrayList<String>();
        mMeaning = new ArrayList<String>();
        // mSpelling.add(spelling.get(0)); //<----------size가 안됨

        String start = "Start Page";
        mSpelling.add(start);
        mMeaning.add(start);

        if(size >=1){ // Start Page 를 제외하고 element 가 있을때
            while(i <= size-1){
                mSpelling.add(spelling.get(i));
                mMeaning.add(meaning.get(i));
                i=i+1;
            }
        }



/*
        while(i<=size){
            mSpelling.add(spelling.get(i));
            mMeaning.add(meaning.get(i));
            i+=1;
        }*/

        //mItems.add("contents: 0 th");
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null ;

        if (mContext != null) {
            // LayoutInflater를 통해 "/res/layout/page.xml"을 뷰로 생성.
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.page, container, false);

            // 이 text를 아이템으로...
            textView = (TextView) view.findViewById(R.id.title) ;
            textView.setText("[Spelling]\n\n"+mSpelling.get(position));
            textView.setTag(1);
            //textView.setText("TEXT " + position) ;
        }
        //position_G = position;

        // 뷰페이저에 추가.
        view.setTag(position);
        container.addView(view) ;




        return view ;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 뷰페이저에서 삭제.
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        // 전체 페이지 수는 10개로 고정.
        return mSpelling == null ? 0 : mSpelling.size();
        // mItems 에 들어있는게 없으면 0 return
        // 있으면 그 사이즈 return
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }

    public void addPage(String spelling,String meaning) {
        mSpelling.add(spelling);
        mMeaning.add(meaning); // Pager를 꾹 누르면 뜻 보이도록
        notifyDataSetChanged();
    }

    public void showMeaning(ViewPager viewpager){
        //Toast.makeText(mContext.getApplicationContext(), "Long Click : "+position_G,Toast.LENGTH_SHORT).show();
        //Toast.makeText(mContext.getApplicationContext(),"Current position: "+pos+", Current item: "+mMeaning.get(pos),
        //        Toast.LENGTH_SHORT).show();
        //View view = (view)
        //viewPager.getChildAt()
        //View view = (View)viewPager.getChildAt(viewPager.getCurrentItem());
        //textView = (TextView) view.findViewById(R.id.title) ;

        int pos = viewpager.getCurrentItem();
        TextView text = viewpager.findViewWithTag(viewpager.getCurrentItem()).findViewById(R.id.title);
        text.setText("[Meaning]\n\n"+mMeaning.get(viewpager.getCurrentItem())); // <----여기 수정
        Toast.makeText(mContext.getApplicationContext(),"Current text: "+text.getText(),
                Toast.LENGTH_SHORT).show();


    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        mCurrentView = (View)object;
    }

    public ArrayList<String> getSpelling(){
        return mSpelling;
    }

    public ArrayList<String> getMeaning(){
        return mMeaning;
    }

    public void chagneData(int currentIndex){
        if(mSpelling.size() == 1){ // Start Page라는 뜻
            // 삭제하지 않기

        }
        else {
            mSpelling.remove(currentIndex);
            mMeaning.remove(currentIndex);
        }



    }
}