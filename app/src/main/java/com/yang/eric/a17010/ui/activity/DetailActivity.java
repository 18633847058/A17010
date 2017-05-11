package com.yang.eric.a17010.ui.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.yang.eric.a17010.R;
import com.yang.eric.a17010.beans.Message;
import com.yang.eric.a17010.contract.DetailContract;
import com.yang.eric.a17010.presenter.DetailPresenter;
import com.yang.eric.a17010.ui.adapter.MessagesAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements DetailContract.View{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private MessagesAdapter adapter;
    private ArrayList<Message> arrayList = new ArrayList<>();
    private ArrayList<Message> backup = new ArrayList<>();
    private ListView lvSearch;
    private ArrayList<String> result;

    private DetailPresenter presenter;

    private SearchView searchView;
    private String title;

    private Button btnSend;
    private SwitchCompat sc;
    private EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        presenter = new DetailPresenter(this, this);
        initViews(null);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadMessages();
            }
        });
        if (!title.equals("新短信")) {
            presenter.init(title);
        }
    }
    @Override
    public void initViews(View v) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(R.color.red);

        lvSearch = (ListView) findViewById(R.id.lv_search);

        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setEnabled(false);
        sc = (SwitchCompat) findViewById(R.id.switchCompat);
        etContent = (EditText) findViewById(R.id.et_message);
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    btnSend.setEnabled(true);
                } else {
                    btnSend.setEnabled(false);
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.equals("新短信")) {
                    if (TextUtils.isEmpty(searchView.getQuery())){
                        showInfo("请输入地址!");
                        return;
                    }
                    presenter.init(searchView.getQuery().toString());
                    changeState(searchView.getQuery().toString());
                    presenter.loadMessages();
                }
                presenter.send(etContent.getText().toString());
                etContent.setText("");
                btnSend.setEnabled(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.receiver));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {

                } else {

                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!title.equals("新短信")) {
            menu.findItem(R.id.action_search).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_add:
                Toast.makeText(this, "通讯录", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {

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
    public void showInfo(String error) {
        Snackbar.make(btnSend, error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showMessages(List<Message> list) {
        arrayList.clear();
        for (Message m: list) {
            arrayList.add(m);
        }
        if (adapter == null) {
            adapter = new MessagesAdapter(DetailActivity.this, arrayList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showNewMessage(Message m) {
        backup.add(m);
        arrayList.add(m);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showResult(ArrayList<String> list) {

    }

    @Override
    public void changeState(String s) {
        if (title.equals("新短信")) {
            title = s;
            getSupportActionBar().setTitle(s);
            invalidateOptionsMenu();
        }
    }
}
