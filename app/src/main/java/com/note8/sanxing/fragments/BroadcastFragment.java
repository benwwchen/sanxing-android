package com.note8.sanxing.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.note8.sanxing.AnswerActivity;
import com.note8.sanxing.R;
import com.note8.sanxing.adapters.BroadcastQuestionsAdapter;
import com.note8.sanxing.listeners.OnItemClickListener;
import com.note8.sanxing.models.BroadcastQuestion;
import com.note8.sanxing.models.TodayQuestion;
import com.note8.sanxing.utils.network.SanxingApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BroadcastFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BroadcastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BroadcastFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Context mContext;

    // views
    private View mBroadcastView;
    private RecyclerView mBroadcastRecyclerView;

    // adapter
    private BroadcastQuestionsAdapter mBroadcastQuestionsAdapter;

    // data
    private ArrayList<BroadcastQuestion> mBroadcastQuestions;

    // handler
    private Handler mBroadcastQuestionsHandler;

    public BroadcastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BroadcastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BroadcastFragment newInstance() {
        BroadcastFragment fragment = new BroadcastFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();

        // Inflate the layout for this fragment
        mBroadcastView = inflater.inflate(R.layout.fragment_broadcast, container, false);

        initView();

        initData();

        updateData();

        return mBroadcastView;
    }

    private void initView() {
        // setup views
        mBroadcastRecyclerView = (RecyclerView) mBroadcastView.findViewById(R.id.recycler_view_broadcast_questions);

        // set layout manager
        mBroadcastRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void initData() {
        mBroadcastQuestions = new ArrayList<>();
        mBroadcastQuestionsAdapter = new BroadcastQuestionsAdapter(mBroadcastQuestions, mContext);
        mBroadcastQuestionsAdapter.setOnItemClickListener(onItemClickListener);
        mBroadcastRecyclerView.setAdapter(mBroadcastQuestionsAdapter);
        mBroadcastQuestionsHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<BroadcastQuestion> broadcastQuestions;

                if(msg.what == SanxingApiClient.SUCCESS_CODE){
                    broadcastQuestions = (List<BroadcastQuestion>) msg.obj;
                } else {
                    Log.d("error", (String) msg.obj);
                    broadcastQuestions = BroadcastQuestion.sampleQuestions;
                }

                // update data & notify the adapter
                mBroadcastQuestions.clear();
                mBroadcastQuestions.addAll(broadcastQuestions);
                mBroadcastQuestionsAdapter.notifyDataSetChanged();
            }
        };
    }

    /**
     * Retrieve data from server
     */
    private void updateData() {
        SanxingApiClient.getInstance(mContext).getBroadcastQuestions(mBroadcastQuestionsHandler);
    }

    /**
     * Item click listener, start AnswerActivity
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(mContext, AnswerActivity.class);
            intent.putExtra("question", mBroadcastQuestions.get(position));
            startActivity(intent);
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
