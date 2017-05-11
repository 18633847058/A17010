package com.yang.eric.a17010.contract;

import com.yang.eric.a17010.presenter.BasePresenter;
import com.yang.eric.a17010.ui.BaseView;

/**
 * Created by Yang on 2017/4/21.
 */

public interface MainContract {
    interface Presenter extends BasePresenter {
        void sendAuth();
        void receive(byte[] message);
        void wipeData();
        void disconnect();
    }

    interface View extends BaseView<Presenter> {

        void showLoading();

        void stopLoading();

        void showMessage(String error, boolean flag);

        void showPop();

        void go2Login();

        void setListener();

        void removeListener();
    }
}
