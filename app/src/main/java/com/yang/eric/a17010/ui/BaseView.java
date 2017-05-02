package com.yang.eric.a17010.ui;

import android.view.View;

/**
 * Created by Yang on 2017/4/20.
 */

public interface BaseView<T> {
    //为View设置Presenter
    void setPresenter(T presenter);

    void initViews(View view);

    void setListener();

    void removeListener();
}
