package kr.or.mrhi.uichanmp3project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class Fragment1 extends Fragment {
    private Context context;
    private  ArrayList<MusicData> arrayList;

    //fragment1.xml
    private ListView listView1;

    //프래그먼트에서 액티비티로 데이터를 전달하는 방법
    //3.내부 인터페이스 참조변수를 만든다.
    OnItemLongClickListener onItemLongClickListener;

    //프래그먼트에서 액티비티로 데이터를 전달하는 방법
    //1.프래그먼트 내부 인터페이스를 만든다.
    public interface  OnItemLongClickListener{
        void onItemLongClick(int position);
    }

    public Fragment1(Context context) {
        this.context = context;
    }

    //프래그먼트에서 액티비티로 데이터를 전달하는 방법
    //4.내부 인터페이스 참조변수에 해당된 객체를 저장한다
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof OnItemLongClickListener){
            onItemLongClickListener = (OnItemLongClickListener) context;
        }
    }

    //프래그먼트에서 액티비티로 데이터를 전달하는 방법
    //5.내부 인터페이스 참조변수에 해당된 객체를 초기화 시킨다
    @Override
    public void onDetach() {
        super.onDetach();
        onItemLongClickListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!= null){
            arrayList = (ArrayList<MusicData>) getArguments().getSerializable("arrayList");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1,container,false);
        listView1 = view.findViewById(R.id.listView1);

        //MyAdapter 생성
        MyAdapter myAdapter = new MyAdapter(context,arrayList);
        listView1.setAdapter(myAdapter);

        //listView 항목을 클릭을하면 MusicActivity 화면 전환한다.(Intent)
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(),MusicActivity.class);
                intent.putExtra("arrayList",arrayList);
                intent.putExtra("position",position);
                startActivity(intent);
                getActivity().finish();//방법이 여러가지 중에 한개를 선택한다.
//                 onItemLongClickListener.onItemLongClick(position);
            }
        });

        return view;
    }
}
