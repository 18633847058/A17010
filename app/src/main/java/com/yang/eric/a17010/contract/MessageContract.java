package com.yang.eric.a17010.contract;

import com.yang.eric.a17010.beans.Conversation;
import com.yang.eric.a17010.presenter.BasePresenter;
import com.yang.eric.a17010.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by Yang on 2017/5/9.
 */

public interface MessageContract {

    interface Presenter extends BasePresenter {

        void loadMessages();

        void refresh();

        void startReading(int position);

        void search(String string);

        void cancelSearch();
    }

    interface View extends BaseView<Presenter> {

        void showLoading();

        void stopLoading();

        void showMessage(String error);

        void showResult(ArrayList<Conversation> list);

    }
}
