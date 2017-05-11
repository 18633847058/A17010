package com.yang.eric.a17010.presenter;

import android.content.Context;
import android.content.Intent;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.beans.Conversation;
import com.yang.eric.a17010.contract.MessageContract;
import com.yang.eric.a17010.greendao.gen.ConversationDao;
import com.yang.eric.a17010.ui.activity.DetailActivity;
import com.yang.eric.a17010.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/5/9.
 */

public class MessagePresenter implements MessageContract.Presenter {

    private MessageContract.View view;
    private Context context;

    private ConversationDao conversationDao;

    private ArrayList<Conversation> list = new ArrayList<>();
    private ArrayList<Conversation> search = new ArrayList<>();

    public MessagePresenter(Context context, MessageContract.View view) {
        this.view = view;
        this.context = context;
        view.setPresenter(this);
        conversationDao = MapsApplication.getInstance().getDaoSession().getConversationDao();
    }

    @Override
    public void start() {
        loadMessages();
    }

    @Override
    public void loadMessages() {
        view.showLoading();
        try {
            String  username = MapsApplication.getInstance()
                    .getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE).getString(Constants.USERNAME, "123");
            if (!username.equals("")) {
//            List<Message> senders = messageDao.queryBuilder().where(
//                    new WhereCondition.StringCondition("SENDER IN (SELECT DISTINCT SENDER FROM MESSAGE WHERE USERNAME = ?)", username)
//            ).list();
            List<Conversation> conversations = conversationDao.queryBuilder().where(ConversationDao.Properties.Username.eq(username)).list();
            list.clear();
            for (Conversation c :
                    conversations) {
                list.add(c);
            }
            view.showResult(list);
            } else {
                view.showMessage("无法找到当前用户!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            view.showMessage("发生错误!");
        }
        view.stopLoading();
    }

    @Override
    public void refresh() {
        loadMessages();
    }

    @Override
    public void startReading(int position) {
        Conversation item = list.get(position);
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("title",item.getPartner());
        context.startActivity(intent);
    }

    @Override
    public void search(String string) {
        search.clear();
        for (int i = 0; i < list.size(); i++) {
            int index = list.get(i).getPartner().indexOf(string);
            // 存在匹配的数据
            if (index != -1) {
                search.add(list.get(i));
            }
        }
        view.showResult(search);
    }

    @Override
    public void cancelSearch() {
        loadMessages();
    }
}
