package com.yang.eric.a17010.contract;

import com.yang.eric.a17010.presenter.BasePresenter;
import com.yang.eric.a17010.ui.BaseView;

/**
 * Created by Yang on 2017/4/20.
 */

public interface LoginContract {
    interface Presenter extends BasePresenter {

        boolean checkInput(String username, String password);

        void login(String username, String password);

        void sendLogin(String username, String password);

        void sendAuth();

        void loginResponse(byte[] message);

        void saveCode();

        void disconnect();
    }

    interface View extends BaseView<Presenter> {

        void showLoading();

        void stopLoading();

        void showMessage(String error, boolean flag);

        void go2Main();

    }
}
