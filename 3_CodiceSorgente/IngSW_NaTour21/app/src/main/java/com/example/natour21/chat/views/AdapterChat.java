package com.example.natour21.chat.views;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.natour21.chat.room.entities.ChatDBEntity;
import com.example.natour21.controllers.ControllerChat;

public class AdapterChat extends ListAdapter<ChatDBEntity, ViewHolderChat>{

    public AdapterChat(@NonNull DiffUtil.ItemCallback<ChatDBEntity> diffCallback){
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ViewHolderChat.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderChat holder, int position) {
        ChatDBEntity chatDBEntity = getItem(position);
        holder.bind(chatDBEntity.getNomeChat(), chatDBEntity.getUnreadMessageCount());
        ConstraintLayout layout = holder.getConstraintLayout();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context ctx = layout.getContext();
                Intent intent = new Intent(ctx, ControllerChat.class);
                intent.putExtra("OTHER_USER_ID", chatDBEntity.getOtherUserId());
                intent.putExtra("CHAT_NAME", chatDBEntity.getNomeChat());
                ctx.startActivity(intent);
            }
        });
    }

    public static class DiffChat extends DiffUtil.ItemCallback<ChatDBEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull ChatDBEntity oldItem, @NonNull ChatDBEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatDBEntity oldItem, @NonNull ChatDBEntity newItem) {
            return oldItem.getOtherUserId().equals(newItem.getOtherUserId());
        }
    }


}
