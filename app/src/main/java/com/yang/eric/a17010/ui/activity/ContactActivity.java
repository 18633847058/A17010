package com.yang.eric.a17010.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.eric.a17010.R;
import com.yang.eric.a17010.beans.TreeNode;
import com.yang.eric.a17010.contract.ContactContract;
import com.yang.eric.a17010.presenter.ContactPresenter;
import com.yang.eric.a17010.ui.adapter.ContactsAdapter;
import com.yang.eric.a17010.ui.adapter.OnRecyclerViewOnClickListener;
import com.yang.eric.a17010.widget.RecyclerViewDivider;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity implements ContactContract.View{

    private LinearLayout llOrganization;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private ContactsAdapter adapter;

    private ContactPresenter presenter;
    private TextView tvRoot;

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
        llOrganization = (LinearLayout) findViewById(R.id.ll_organization);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.red);
        tvRoot = (TextView) findViewById(R.id.tv_root);
        tvRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataChanged(null);
            }
        });
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
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
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
        if (t != null) {
            path.add(t);
            TextView tv = new TextView(ContactActivity.this);
            tv.setText(t.getName());
            tv.setOnClickListener(tvOnClickListener);
            tv.setTextSize(17);
            Drawable drawable= getResources().getDrawable(R.drawable.ic_right);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv.setCompoundDrawables(null,null,drawable,null);
            tv.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            llOrganization.addView(tv, layoutParams);
            textViews.add(tv);
        } else {
            path.clear();
            for (TextView tv : textViews) {
                llOrganization.removeView(tv);
            }
            textViews.clear();
        }
        presenter.loadResults(true);
        adapter.notifyDataSetChanged();
    }
    //TextView的点击事件
    public View.OnClickListener tvOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = 0;
            for (int i = 0; i < textViews.size(); i++) {
                if (v == textViews.get(i))
                    index = i;
            }
            for (int i = index + 1; i < textViews.size(); i++) {
                llOrganization.removeView(textViews.remove(i));
                path.remove(i);
            }
            llOrganization.removeView(textViews.remove(index));
            notifyDataChanged(path.remove(index));
        }
    };
}
