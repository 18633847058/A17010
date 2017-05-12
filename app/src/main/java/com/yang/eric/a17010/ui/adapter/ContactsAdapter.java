package com.yang.eric.a17010.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yang.eric.a17010.R;
import com.yang.eric.a17010.beans.TreeNode;
import com.yang.eric.a17010.utils.Constants;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Yang on 2017/5/12.
 */

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<TreeNode> departments;
    private List<TreeNode> employees;
    private List<Integer> types;

    private OnRecyclerViewOnClickListener mListener;



    public ContactsAdapter(Context context, ArrayList<TreeNode> departments,List<TreeNode> employees, List<Integer> types) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.departments = departments;
        this.employees = employees;
        this.types = types;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case  Constants.TYPE_DEPARTMENTS:
                return new DepartmentsViewHolder(inflater.inflate(R.layout.item_department_layout,parent,false),mListener);
            case Constants.TYPE_DIVIDER:
                return new DividerViewHolder(inflater.inflate(R.layout.item_divider_layout,parent,false));
        }
        View view = inflater.inflate(R.layout.item_employee_layout,parent,false);
        return new EmployeesViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (types.get(position)) {
            case Constants.TYPE_DEPARTMENTS:
                if (!departments.isEmpty()) {
                    TreeNode d = departments.get(position);
                    String name = d.getName();
                    ((DepartmentsViewHolder) holder).tvName.setText(name);
                    String text = d.getChildren().size() + "";
                    ((DepartmentsViewHolder) holder).tvNumber.setText(text);
                }
                break;
            case Constants.TYPE_EMPLOYEES:
                if (!employees.isEmpty()) {
                    TreeNode e = departments.get(position - departments.size() - 1);
                    ((EmployeesViewHolder) holder).tvName.setText(e.getName());
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    @Override
    public int getItemViewType(int position) {
        return types.get(position);
    }

    public void setItemClickListener(OnRecyclerViewOnClickListener listener){
        this.mListener = listener;
    }

    public class DepartmentsViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {

        TextView tvName;
        TextView tvNumber;

        OnRecyclerViewOnClickListener listener;

        public DepartmentsViewHolder(View itemView, OnRecyclerViewOnClickListener listener) {
            super(itemView);

            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);

            this.listener = listener;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (listener != null){
                listener.OnItemClick(v,getLayoutPosition());
            }
        }
    }
    public class DividerViewHolder extends RecyclerView.ViewHolder {

        public DividerViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class EmployeesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName;
        OnRecyclerViewOnClickListener listener;

        public EmployeesViewHolder(View itemView, OnRecyclerViewOnClickListener listener) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            this.listener = listener;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (listener != null){
                listener.OnItemClick(v,getLayoutPosition());
            }
        }
    }
}
