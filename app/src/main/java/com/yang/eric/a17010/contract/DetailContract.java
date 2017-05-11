package com.yang.eric.a17010.contract;

import com.yang.eric.a17010.beans.Message;
import com.yang.eric.a17010.presenter.BasePresenter;
import com.yang.eric.a17010.ui.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/5/10.
 */

public interface DetailContract {
    interface Presenter extends BasePresenter {

        void init(String s);

        void loadMessages();

        void refresh();

        void search(String string);

        void cancelSearch();

        void send(String content);
    }

    interface View extends BaseView<Presenter> {

        void showLoading();

        void stopLoading();

        void showInfo(String error);

        void showMessages(List<Message> list);

        void showNewMessage(Message m);

        void showResult(ArrayList<String> list);

        void changeState(String s);

    }
}
