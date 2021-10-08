package kr.or.mrhi.uichanmp3project;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MusicData> arrayList;
    private static final BitmapFactory.Options options = new BitmapFactory.Options();
    private static final int MAX_IMAGE_SIZE = 170;

    public MyAdapter(Context context, ArrayList<MusicData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listview_item, viewGroup, false);
        }
        ImageView ivAlbum = view.findViewById(R.id.ivAlbum);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvArtist = view.findViewById(R.id.tvArtist);
        //앨범 아이디를 가져온다.
        try{
            long a = Long.parseLong(arrayList.get(position).getAlbumId());
            Bitmap bitmap = getAlbumImage(context, a, MAX_IMAGE_SIZE);
            if (bitmap != null) {
                ivAlbum.setImageBitmap(bitmap);
            } else {
                ivAlbum.setImageResource(R.drawable.music_icon);
            }
        }catch(Exception e){
            Log.d("플레이어", "이미지 못 가져옴"+e.toString());
            ivAlbum.setImageResource(R.drawable.music_icon);
        }


        //가수의 이름, 가수의 곡이름을 가져온다.
        tvTitle.setText(arrayList.get(position).getTitle());
        tvArtist.setText(arrayList.get(position).getArtist());

        return view;
    }
    private Bitmap getAlbumImage(Context context, long albumId, int maxImageSize){
        //이미지를 가져올려면 contentresolver와 앨범 이미지 아이디를 통해서 Uri를 가져온다.
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://media/external/audio/albumart/"+albumId);
        if(uri != null){
            //이미지를 가져오기 위해서
            ParcelFileDescriptor pfd = null;

            try {
                pfd = contentResolver.openFileDescriptor(uri,"r");
                //options.inJustDecodeBound = true로 하면 이미지를 비트맵으로 변환해서 리턴하지 않고
                //비트맵 부가적인 정보만 option에 저장한다
                options.inJustDecodeBounds = true;
                //파일을 비트맵으로 전환한다.(bitMapFactory decode를 통해서 파일을 이미지로 전환
                BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(),null,options);


                //비트맵 부가적인 정보를 통해서비트르맵 크기를 체크한다. 정보를 Option 객체속에 들어있다
                int scale = 0;
                if(options.outHeight >  maxImageSize || options.outWidth>maxImageSize){
                    //우리가 원하는 이미지 사이즈로 전환하는 scale 값을 구한다.
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(maxImageSize / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }

                //비트맵을 가져온다.(내가 지정한 이미지 사이즈를 참고해서 비트맵을 가져온다.)
                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;
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
}