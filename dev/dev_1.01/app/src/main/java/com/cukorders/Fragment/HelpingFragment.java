package com.cukorders.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Script;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cukorders.Adapter.PostAdapter;
import com.cukorders.Adapter.PostAdapter_helping;
import com.cukorders.Adapter.PostAdapter_request;
import com.cukorders.helping.InitPost;
import com.cukorders.helping.R;
import com.cukorders.helping.postGet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpingFragment extends Fragment {

    private static final String TAG = "RequestingFragment";

    private RecyclerView recentPostListsView;
    private PostAdapter_helping mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<InitPost> mPost=new ArrayList<InitPost>();

    private DatabaseReference mHelperPostRef;
    private DatabaseReference getPostuidRef;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private String mUid="";

    private String currentTime;
    private SimpleDateFormat sdf;

    //todo 의뢰중인 미션
    public HelpingFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.requesting_missions,container,false);
        //recyclerview
        recentPostListsView = (RecyclerView) view.findViewById(R.id.requesting_recyclerview);
        recentPostListsView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recentPostListsView.setLayoutManager(mLayoutManager);
        //맨 밑으로 내려갔을때, 파란색 그라데이션 안뜨도록
        recentPostListsView.scrollToPosition(0);

        mAdapter = new PostAdapter_helping(getActivity(),mPost);
        recentPostListsView.setAdapter(mAdapter);
        recentPostListsView.setItemAnimator(new DefaultItemAnimator());

        //set mUid
        mAuth= FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mUid = mCurrentUser.getUid();

        //client postref
        mHelperPostRef = FirebaseDatabase.getInstance().getReference().child("Posting");
        getPostuidRef = FirebaseDatabase.getInstance().getReference().child("Chat_list_helper");

        //getCurrentTime
        sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        currentTime = sdf.format(new Date());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mHelperPostRef.orderByChild("isMatched").equalTo(mUid);
        Log.d(TAG,"Check my uid "+mUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아옴
                mPost.clear();
                Log.d(TAG,"Check my dataSnapshot "+dataSnapshot);
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        //반복을 통해서 안에 있는 데이터리스트들을 추출하기
                        InitPost post = snapshot.getValue(InitPost.class);//InitPost 객체에 담기
                        mPost.add(post); //담은 데이터들을 배열 리스트에 넣고 리사이클러 뷰로 보낼 준비하기
                    }
                }
                //데이터 저장 및 새로고침
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //디비 가져오는 중 에러 발생시
                Log.e(TAG, String.valueOf(databaseError.toException()));
            }
        });
        mAdapter= new PostAdapter_helping(getActivity(),mPost);
        recentPostListsView.setAdapter(mAdapter);
    }
}