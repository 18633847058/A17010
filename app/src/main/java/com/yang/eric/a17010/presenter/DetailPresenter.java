package com.yang.eric.a17010.presenter;

import android.content.Context;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.beans.Conversation;
import com.yang.eric.a17010.beans.Message;
import com.yang.eric.a17010.contract.DetailContract;
import com.yang.eric.a17010.greendao.gen.ConversationDao;
import com.yang.eric.a17010.greendao.gen.MessageDao;
import com.yang.eric.a17010.utils.Constants;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Yang on 2017/5/10.
 */

public class DetailPresenter implements DetailContract.Presenter {

    private DetailContract.View view;
    private Context context;

    private ConversationDao conversationDao;
    private MessageDao messageDao;

    private String username;

    private List<Conversation> conversations;
    private Conversation conversation;

    public DetailPresenter(DetailContract.View view, Context context) {
        this.view = view;
        this.context = context;
        conversationDao = MapsApplication.getInstance().getDaoSession().getConversationDao();
        messageDao = MapsApplication.getInstance().getDaoSession().getMessageDao();
        username = MapsApplication.getInstance()
                .getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE).getString(Constants.USERNAME, "123");
    }

    @Override
    public void start() {

    }

    @Override
    public void init(String s) {
        view.showLoading();
        if (!username.equals("")&&!s.equals("")) {
            QueryBuilder<Conversation> qb = conversationDao.queryBuilder();
            qb.where(ConversationDao.Properties.Username.eq(username),ConversationDao.Properties.Partner.eq(s));
            conversations = qb.list();
            if (!conversations.isEmpty()) {
                conversation = conversations.remove(0);
                loadMessages();
            } else {
                conversation = new Conversation();
                conversation.setUsername(username);
                conversation.setPartner(s);
                conversationDao.insert(conversation);
                view.showLoading();
                init(s);
            }
        }
    }

    @Override
    public void loadMessages() {
        conversation.resetMessages();
        List<Message> list = conversation.getMessages();
        view.showMessages(list);
        view.stopLoading();
    }

    @Override
    public void refresh() {

    }

    @Override
    public void search(String string) {

    }

    @Override
    public void cancelSearch() {

    }

    @Override
    public void send(String content) {
        Message m = new Message();
        m.setContent(content);
        m.setMid(conversation.getId());
        m.setRead(1);
        m.setReceived(0);
        m.setTime("2017/5/11");
        m.setSendState(1);
        view.showNewMessage(m);
        //传输--------------------



    }
}
