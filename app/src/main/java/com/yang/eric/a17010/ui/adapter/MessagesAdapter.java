package com.yang.eric.a17010.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yang.eric.a17010.R;
import com.yang.eric.a17010.beans.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/5/10.
 */

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private List<Message> list;

    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;

    public MessagesAdapter(Context context, ArrayList<Message> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_LEFT:
                return new LeftViewHolder(inflater.inflate(R.layout.layout_receive_item,parent,false));
            case TYPE_RIGHT:
                return new RightViewHolder(inflater.inflate(R.layout.layout_send_item,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RightViewHolder) {
            ((RightViewHolder) holder).tvContent.setText(list.get(position).getContent());
            ((RightViewHolder) holder).tvTime.setText(list.get(position).getTime());
            ((RightViewHolder) holder).tvType.setText(list.get(position).getType()==1?"北斗":"网络");
            if (list.get(position).getSendState() != 0) {
                if (list.get(position).getSendState() == 1) {
                    ((RightViewHolder) holder).imageView.setImageResource(R.drawable.ic_doing);
                }
            }
        } else {
            ((LeftViewHolder) holder).tvContent.setText(list.get(position).getContent());
            ((LeftViewHolder) holder).tvTime.setText(list.get(position).getTime());
            ((LeftViewHolder) holder).tvType.setText(list.get(position).getType()==1?"北斗":"网络");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = list.get(position);
        if (message.getReceived() == 1) {
            return TYPE_LEFT;
        } else {
            return TYPE_RIGHT;
        }
    }

    private class LeftViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;
        TextView tvTime;
        TextView tvType;

        public LeftViewHolder(View itemView) {
            super(itemView);

            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvType = (TextView) itemView.findViewById(R.id.tv_type);
        }
    }

    private class RightViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;
        TextView tvTime;
        TextView tvType;
        ImageView imageView;

        public RightViewHolder(View itemView) {
            super(itemView);

            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvType = (TextView) itemView.findViewById(R.id.tv_type);
            imageView = (ImageView) itemView.findViewById(R.id.iv_state);
        }
    }
}
