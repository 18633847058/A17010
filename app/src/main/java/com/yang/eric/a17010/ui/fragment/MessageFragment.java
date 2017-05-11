package com.yang.eric.a17010.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yang.eric.a17010.R;
import com.yang.eric.a17010.beans.Conversation;
import com.yang.eric.a17010.contract.MessageContract;
import com.yang.eric.a17010.ui.adapter.ConversationsAdapter;
import com.yang.eric.a17010.ui.adapter.OnRecyclerViewOnClickListener;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MessageFragment extends Fragment implements MessageContract.View{


    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;


    private ConversationsAdapter adapter;
    private ArrayList<String> arrayList = new ArrayList<>();

    private MessageContract.Presenter presenter;

    public MessageFragment() {
    }

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        initViews(view);
        presenter.start();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.refresh();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.refresh();
    }

    @Override
    public void setPresenter(MessageContract.Presenter presenter) {
        if (presenter != null) {
            this.presenter = presenter;
        }
    }

    @Override
    public void initViews(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.red);
    }

    @Override
    public void showLoading() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void stopLoading() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showMessage(String error) {
        Snackbar.make(recyclerView, error,Snackbar.LENGTH_INDEFINITE)
                .setAction("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.refresh();
                    }
                })
                .show();
    }

    @Override
    public void showResult(ArrayList<Conversation> list) {
        arrayList.clear();
        for (Conversation s : list) {
            arrayList.add(s.getPartner());
        }
        if (adapter == null) {
            adapter = new ConversationsAdapter(getContext(),arrayList);
            adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {
                @Override
                public void OnItemClick(View v, int position) {
                    presenter.startReading(position);
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
