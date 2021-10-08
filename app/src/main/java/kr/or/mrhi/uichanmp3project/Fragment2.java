package kr.or.mrhi.uichanmp3project;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class Fragment2 extends Fragment {
    private Context context;
    private ArrayList<MusicData> likeList;

    //fragment1.xml
    private ListView listView2;

    public Fragment2(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!= null){
            likeList = (ArrayList<MusicData>) getArguments().getSerializable("likeList");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2,container,false);
        listView2 = view.findViewById(R.id.listView2);

        //MyAdapter 생성
        MyAdapter myAdapter = new MyAdapter(context,likeList);
        listView2.setAdapter(myAdapter);

        //listView 항목을 클릭을하면 MusicActivity 화면 전환한다.(Intent)
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(),MusicActivity.class);
                intent.putExtra("arrayList",likeList);
                intent.putExtra("position",position);
                startActivity(intent);
                getActivity().finish();//방법이 여러가지 중에 한개를 선택한다.
//                 onItemLongClickListener.onItemLongClick(position);
            }
        });

        Log.d("음악플레이어", "Fragment2 onCreateView");
        return view;
    }
}