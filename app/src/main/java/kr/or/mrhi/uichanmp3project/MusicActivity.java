package kr.or.mrhi.uichanmp3project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title;
    private ImageView album;
    private ImageView pre;
    private ImageView play;
    private ImageView pause;
    private ImageView next;
    private ImageView heart;
    private ImageView ivExit;
    private SeekBar seekBar;


    //이미지 사이즈
    private static final int MAX_IMAGE_SIZE = 170;
    private static final BitmapFactory.Options options = new BitmapFactory.Options();
    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<MusicData> arrayList;

    //스레드 관련된 변수
    private boolean isPlaying = true;
    private boolean nextFlag = false;
    private MyDBHelper myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        //Intent객체를 구한다.(position,ArrayList<MusicData>)
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        arrayList = (ArrayList<MusicData>) intent.getSerializableExtra("arrayList");

        findViewByIdEnrollFunc();

        //미디어 플레이어 객체를 가져옴
        mediaPlayer = new MediaPlayer();

        //선택된 노래 이미지 위치를 가져온다.
        //Long.parseLong(arrayList.get(position).getAlbumId())로 해야한다 int안됨
        Bitmap bitmap = getAlbumImage(getApplicationContext(), Long.parseLong(arrayList.get(position).getAlbumId()), MAX_IMAGE_SIZE);
        if (bitmap != null) {
            album.setImageBitmap(bitmap);
        } else {
            album.setImageResource(R.drawable.music_icon);
        }

        //음악을 시작함.
        playMusic(arrayList.get(position));

        //시크바를 동시에 진행함.
        ProgressUpdate progressUpdate = new ProgressUpdate();
        progressUpdate.start();

        //미디어플레이어에서 노래가 끝나면 이벤트 처리하는 기능
        mediaPlayer.setOnCompletionListener(completionListener);

        //데이터베이스를 불러온다.
        myDBHelper = new MyDBHelper(this);

    }
    //음악이 끝났을 떄 실행되는함수
    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if (position + 1 < arrayList.size()) {
                position++;
                playMusic(arrayList.get(position));

                Bitmap bitmap = getAlbumImage(getApplicationContext(), Long.parseLong(arrayList.get(position).getAlbumId()), MAX_IMAGE_SIZE);
                if (bitmap != null) {
                    album.setImageBitmap(bitmap);
                } else {
                    album.setImageResource(R.drawable.music_icon);
                }

            }
        }
    };
    //객체주소와 이벤트설정
    private void findViewByIdEnrollFunc() {
        title = findViewById(R.id.title);
        album = findViewById(R.id.album);
        pre = findViewById(R.id.pre);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        heart = findViewById(R.id.heart);
        ivExit = findViewById(R.id.ivExit);
        seekBar = findViewById(R.id.seekBar);

        pre.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        heart.setOnClickListener(this);
        ivExit.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(listener);
    }

    //이벤트 핸들러
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pre:
                if (position - 1 >= 0) {
                    position--;
                    playMusic(arrayList.get(position));
                    Bitmap bitmap = getAlbumImage(getApplicationContext(), Long.parseLong(arrayList.get(position).getAlbumId()),MAX_IMAGE_SIZE);
                    if (bitmap != null){
                        album.setImageBitmap(bitmap);
                    }else {
                        album.setImageResource(R.drawable.music_icon);
                    }
                    seekBar.setProgress(0);
                    if(arrayList.get(position).getOk().equals("yes")){
                        heart.setImageResource(R.drawable.heart);
                    }else{
                        heart.setImageResource(R.drawable.heart1);
                    }
                }
                break;
            case R.id.play:
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                mediaPlayer.start();

                break;
            case R.id.pause:
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                break;
            case R.id.next:
                if(nextFlag==false){

                    if (position + 1 < arrayList.size()) {
                        position++;
                        playMusic(arrayList.get(position));

                        Bitmap bitmap = getAlbumImage(getApplicationContext(), Long.parseLong(arrayList.get(position).getAlbumId()),MAX_IMAGE_SIZE);
                        if (bitmap != null){
                            album.setImageBitmap(bitmap);
                        }else {
                            album.setImageResource(R.drawable.music_icon);
                        }

                        seekBar.setProgress(0);
                        if(arrayList.get(position).getId().equals("yes")){
                            heart.setImageResource(R.drawable.heart);
                        }else{
                            heart.setImageResource(R.drawable.heart1);
                        }
                    }
                }else{

                    if (position + 1 < arrayList.size()) {
                        position=(int)(Math.random()*(arrayList.size()-0+1)+0);
                        playMusic(arrayList.get(position));

                        Bitmap bitmap = getAlbumImage(getApplicationContext(), Long.parseLong(arrayList.get(position).getAlbumId()),MAX_IMAGE_SIZE);
                        if (bitmap != null){
                            album.setImageBitmap(bitmap);
                        }else {
                            album.setImageResource(R.drawable.music_icon);
                        }

                        seekBar.setProgress(0);
                        if(arrayList.get(position).getId().equals("yes")){
                            heart.setImageResource(R.drawable.heart);
                        }else{
                            heart.setImageResource(R.drawable.heart1);
                        }
                    }
                }
                break;
            case R.id.heart:
                if (arrayList.get(position).getOk().equals("yes")) {
                    myDBHelper.updateMusicTBLMyLike(myDBHelper.getWritableDatabase(),arrayList.get(position).getId(),"no");
                    arrayList.get(position).setOk("no");
                    heart.setImageResource(R.drawable.heart1);

                } else {
                    myDBHelper.updateMusicTBLMyLike(myDBHelper.getWritableDatabase(),arrayList.get(position).getId(),"yes");
                    arrayList.get(position).setOk("yes");
                    heart.setImageResource(R.drawable.heart);
                }
                break;
            case R.id.ivExit:
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mediaPlayer.pause();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekBar.getProgress());
            if (seekBar.getProgress() > 0 && play.getVisibility() == View.GONE) {
                mediaPlayer.start();
            }
        }
    };

    //음악을 시작함.
    public void playMusic(MusicData musicData) {

        try {
            seekBar.setProgress(0);
            title.setText(musicData.getArtist() + " - " + musicData.getTitle());

            if (musicData.getOk().equals("yes")) {
                heart.setImageResource(R.drawable.heart);
            } else {
                heart.setImageResource(R.drawable.heart1);
            }

            Uri musicURI = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + musicData.getId());

            mediaPlayer.reset();

            //듣고자하는 파일을 프로바이더가 가져온다.
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            if (mediaPlayer.isPlaying()) {
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            } else {
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.e("음악플레이어", "playMusic() 에러발생"+e.toString());
        }
    }
    //이미지를 가져온다.
    private Bitmap getAlbumImage(Context context, long albumId, int maxImageSize){
        //이미지를 가져올려면 contentresolver와 앨범 이미지 아이디를 통해서 Uri를 가져온다.
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://media/external/audio/albumart/"+albumId);
        if(uri != null){
            //이미지를 가져오기 위해서
            ParcelFileDescriptor pfd = null;

            try {
                pfd = contentResolver.openFileDescriptor(uri,"r");
                Bitmap bitmap =  BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(),null,options);

                if(bitmap != null){
                    //비트맵 사이즈를 체크해서 내가 정한 가로크기와 세로크기가 아니면 다시 비트맵 크기 재설정해서 비트맵을 만든다
                    if(options.outHeight != maxImageSize || options.outWidth != maxImageSize){
                        Bitmap tempBitmap = Bitmap.createScaledBitmap(bitmap,maxImageSize,maxImageSize,true);
                        bitmap.recycle();
                        bitmap = tempBitmap;
                    }
                }
                return  bitmap;
            } catch (FileNotFoundException e) {
                Log.e("음악플레이어", "비트맵 이미지 변환에서 오류"+e.toString());
            } finally {
                if(pfd != null){
                    try {
                        pfd.close();
                    } catch (IOException e) {
                        Log.e("음악플레이어", "ParcelFileDescriptor 닫기 오류"+e.toString());
                    }
                }
            }
        }
        return null;
    }//end of getAlbumImage


    //쓰레드 상속받아서 사용하는1번방식
    class ProgressUpdate extends Thread {
        @Override
        public void run() {
            while (isPlaying) {
                try {
                    Thread.sleep(300);
                    if (mediaPlayer.isPlaying()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            }
                        });
                    }
                } catch (Exception e) {
//                    Log.e("ProgressUpdate", e.getMessage());
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}