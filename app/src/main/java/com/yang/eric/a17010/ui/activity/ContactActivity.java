package com.yang.eric.a17010.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yang.eric.a17010.R;
import com.yang.eric.a17010.beans.TreeNode;
import com.yang.eric.a17010.contract.ContactContract;
import com.yang.eric.a17010.presenter.ContactPresenter;
import com.yang.eric.a17010.ui.adapter.ContactsAdapter;
import com.yang.eric.a17010.ui.adapter.OnRecyclerViewOnClickListener;
import com.yang.eric.a17010.widget.RecyclerViewDivider;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity implements ContactContract.View{

    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private ContactsAdapter adapter;

    private ContactPresenter presenter;

    private ArrayList<TreeNode> path = new ArrayList<>();
    private ArrayList<TextView> textViews = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("通讯录");
        setSupportActionBar(toolbar);
        presenter = new ContactPresenter(this, this);
        initViews(null);
        presenter.loadResults(false);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadResults(true);
            }
        });

    }
    @Override
    public void setPresenter(ContactContract.Presenter presenter) {

    }

    @Override
    public void initViews(View view) {
        linearLayout = (LinearLayout) findViewById(R.id.ll_organization);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.red);
    }

    @Override
    public void showLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void stopLoading() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String error) {

    }

    @Override
    public void showResult(ArrayList<TreeNode> departments, ArrayList<TreeNode> employees, ArrayList<Integer> types) {
        if (adapter == null) {

            adapter = new ContactsAdapter(this,departments,employees,types);
            adapter.setItemClickListener(new OnRecyclerViewOnClickListener() {
                @Override
                public void OnItemClick(View v, int position) {
                    int type = recyclerView.findViewHolderForLayoutPosition(position).getItemViewType();
                    if (type == 0) {
                        presenter.startReading(0, position);
                    } else {
                        presenter.startReading(2,position);
                    }
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataChanged(TreeNode t) {
        presenter.setRoot(t);
        presenter.loadResults(true);
        adapter.notifyDataSetChanged();
    }
}
