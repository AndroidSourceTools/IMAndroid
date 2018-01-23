package com.lcp.imandroid;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by linchenpeng on 2018/1/22.
 */

public class MessageAdapter extends BaseQuickAdapter<Message, BaseViewHolder> {
    public MessageAdapter(@Nullable List<Message> data) {
        super(R.layout.item_message, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, Message item) {
        holder.setText(R.id.text_name, String.format("%s :",item.getName()))
                .setText(R.id.text_message, item.getMessage());
    }
}
