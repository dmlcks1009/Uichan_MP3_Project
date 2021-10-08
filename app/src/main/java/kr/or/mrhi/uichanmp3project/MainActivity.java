package kr.or.mrhi.uichanmp3project;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

//프래그먼트
public class MainActivity extends AppCompatActivity implements Fragment1.OnItemLongClickListener {
    private TabLayout tabLayout;
    private ViewPager2 pager;

    //음악리스트 정보
    private ArrayList<MusicData> arrayList = new ArrayList<>();
    private ArrayList<MusicData> likeList = new ArrayList<>();

    //FragmentStateAdapter
    private final static int NUM_PAGES = 2;
    private final static String[] tabElement = {"TOTAL MUSIC LIST", "LIKE MUSIC LIST"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Hide ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //ViewPager2랑 TabLayout 객체찾기
        tabLayout = findViewById(R.id.tabLayout);
        pager = findViewById(R.id.pager);


        //Content Provider 통해서 음악파일을 가져와야 한다
        getMusicList();

        //DataBase에서 음악파일 insert를 진행한다.
        MyDBHelper myDBHelper = new MyDBHelper(this);
        boolean flag = myDBHelper.insertMusicDataAll(myDBHelper.getWritableDatabase(), arrayList);

        //DataBase에서 모든 리스트와 좋아요 리스트를 가져온다
        arrayList = myDBHelper.getTableAllMusicList(myDBHelper.getReadableDatabase());
        likeList = myDBHelper.getTableLikeMusicList(myDBHelper.getReadableDatabase());

        //프래그먼트 어댑터 생성 (FragmentStateAdapter)
        ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(this);
        pager.setAdapter(screenSlidePagerAdapter);

        //Tablayout 과 프래그먼트를 어탭터에 연결
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(MainActivity.this);
                textView.setGravity(Gravity.CENTER);
                textView.setText(tabElement[position]);
                tab.setCustomView(textView);
            }
        });
        tabLayoutMediator.attach();
    }

    //Content Provider에서 contentResolver를 이용해서 음악파일을 가져와야 한다
    private void getMusicList() {
        //permission 요청
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                MODE_PRIVATE);
        Cursor cursor = null;
        try {
            //contentResolver를 이용해서 음악파일을 가져온다(아이디,앨범아이디,타이틀,아티스트)
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media.ALBUM_ID,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.ARTIST}, null, null, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String albumId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                MusicData musicData = new MusicData(id, albumId, title, artist, "no");
                arrayList.add(musicData);
            }
        } catch (Exception e) {
            Log.d("음악플레이어", "getMusicList() 외부에서 음악파일가져오기 오류" + e.toString());
        } finally {
            cursor.close();
        }
    }//end of getMusicList

    @Override
    public void onItemLongClick(int position) {
        Log.e("음악플레이어", "인터페이스 프래그먼트에서 액티비로 전달방법 " + position);
    }

    //viewPager 에 프래그먼트 붙이기
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                Fragment fragment1 = new Fragment1(MainActivity.this);
                Bundle bundle = new Bundle();
                bundle.putSerializable("arrayList", arrayList);
                fragment1.setArguments(bundle);
                return fragment1;

            } else if (position == 1) {
                Fragment fragment2 = new Fragment2(MainActivity.this);
                Bundle bundle = new Bundle();
                bundle.putSerializable("likeList", likeList);
                fragment2.setArguments(bundle);
                return fragment2;
            } else {
                Log.d("음악플레이어", "createFragment() 프레그먼트 생성 오류");
                return null;
            }
        }

        //뷰페이지 개수
        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }//end of adapter

}