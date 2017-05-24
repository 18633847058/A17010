package com.yang.eric.a17010.presenter;

import android.content.Context;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.beans.TreeNode;
import com.yang.eric.a17010.contract.ContactContract;
import com.yang.eric.a17010.greendao.gen.TreeNodeDao;
import com.yang.eric.a17010.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/5/12.
 */

public class ContactPresenter implements ContactContract.Presenter {

    private ContactContract.View view;
    private Context context;

    private ArrayList<TreeNode> departments;
    private ArrayList<TreeNode> employees;
    private ArrayList<Integer> types;

    private TreeNodeDao treeNodeDao;
    private List<TreeNode> tree;

    public ContactPresenter(Context context, ContactContract.View view) {
        this.view = view;
        this.context = context;
        treeNodeDao = MapsApplication.getInstance().getDaoSession().getTreeNodeDao();

        departments = new ArrayList<>();
        employees = new ArrayList<>();
        types = new ArrayList<>();
    }

    @Override
    public void start() {

    }

    @Override
    public void loadResults(boolean refresh) {
        if (!refresh) {
            view.showLoading();
//            TreeNode t = new TreeNode();
//            t.setId(3L);
//            t.setPid(1L);
//            t.setLevel(1);
//            t.setType(1);
//            t.setName("杨理清");
//            treeNodeDao.insert(t);
//            TreeNode t2 = new TreeNode();
//            t2.setId(4L);
//            t2.setPid(1L);
//            t2.setLevel(1);
//            t2.setType(0);
//            t2.setName("软件组");
//            treeNodeDao.insert(t2);
        } else {
            departments.clear();
            employees.clear();
            types.clear();
        }


        checkForFreshData();

        view.showResult(departments, employees, types);

        view.stopLoading();
    }

    @Override
    public void startReading(int type, int position) {
        switch (type) {
            case Constants.TYPE_DEPARTMENTS:
                TreeNode d = departments.get(position);
                view.notifyDataChanged(d);
                break;
            case Constants.TYPE_EMPLOYEES:
                TreeNode e = employees.get(position - departments.size() - 1);
                //跳转到通讯录详情界面
                view.showMessage(e.getName());
        }
    }

    @Override
    public void setRoot(TreeNode root) {
        if (root == null) {
            tree = null;
        } else {
            tree = root.getChildren();
        }
    }

    @Override
    public void checkForFreshData() {
        if (tree == null) {
            tree = treeNodeDao.queryBuilder().where(TreeNodeDao.Properties.Level.eq(0)).list();
        }
        for (TreeNode t :
                tree) {
            if (t.getType() == 0) {
                departments.add(t);
                types.add(0);
            } else if (t.getType() == 1){
                employees.add(t);
            }
        }
        if (!employees.isEmpty()) {
            types.add(1);
        }
        for (TreeNode t :
                employees) {
            types.add(2);
        }
    }

    @Override
    public void search(String string) {

    }

    @Override
    public void cancelSearch() {

    }
}
