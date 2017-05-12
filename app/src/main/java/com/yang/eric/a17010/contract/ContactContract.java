package com.yang.eric.a17010.contract;

import com.yang.eric.a17010.beans.TreeNode;
import com.yang.eric.a17010.presenter.BasePresenter;
import com.yang.eric.a17010.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by Yang on 2017/5/9.
 */

public interface ContactContract {

    interface Presenter extends BasePresenter {

        void loadResults(boolean refresh);

        void startReading(int type, int position);

        void setRoot(TreeNode root);

        void checkForFreshData();

        void search(String string);

        void cancelSearch();
    }

    interface View extends BaseView<Presenter> {

        void showLoading();

        void stopLoading();

        void showMessage(String error);

        void showResult(ArrayList<TreeNode> departments,
                        ArrayList<TreeNode> employees,
                        ArrayList<Integer> types);
        void notifyDataChanged(TreeNode t);

    }
}
