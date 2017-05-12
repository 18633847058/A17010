package com.yang.eric.a17010.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yang.eric.a17010.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/5/8.
 */

public class ConversationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final Context context;
    private final LayoutInflater inflater;
    private List<String> list;

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_EMPTY = 1;

    private OnRecyclerViewOnClickListener mListener;

    public ConversationsAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ConversationsAdapter.TYPE_EMPTY:
                return new EmptyViewHolder(inflater.inflate(R.layout.empty_layout,parent,false));
            case ConversationsAdapter.TYPE_NORMAL:
                View view = inflater.inflate(R.layout.item_conversation_layout,parent,false);
                return new ConversationViewHolder(view,mListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ConversationViewHolder) {
            String item = list.get(position);
            ((ConversationViewHolder) holder).tvTitle.setText(item);
        } else if (holder instanceof EmptyViewHolder) {
            if (list.size() != 0) {
                ((EmptyViewHolder) holder).tvTitle.setText("到底了!");
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.size() == position) {
            return ConversationsAdapter.TYPE_EMPTY;
        }
        return ConversationsAdapter.TYPE_NORMAL;
    }

    public void setItemClickListener(OnRecyclerViewOnClickListener listener){
        this.mListener = listener;
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {

        TextView tvTitle;

        OnRecyclerViewOnClickListener listener;

        public ConversationViewHolder(View itemView, OnRecyclerViewOnClickListener listener) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);

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
    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        public EmptyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_info);
        }
    }
}
