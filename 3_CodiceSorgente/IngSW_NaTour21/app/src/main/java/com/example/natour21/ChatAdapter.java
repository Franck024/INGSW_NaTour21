package com.example.natour21;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.databinding.ItemContainerReceivedMessageBinding;
import com.example.natour21.databinding.ItemContainerSentMessageBinding;

import java.util.List;

// YT

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<chatMessage> chatMessages;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECIVED = 2;

    public ChatAdapter(List<chatMessage> chatMessages, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false)
            );
        }else{
            return new RecivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()), parent, false)
            );

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT)
            ((SentMessageViewHolder) holder).setData((chatMessages).get(position));
        else
            ((RecivedMessageViewHolder) holder).setData((chatMessages).get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position){
        if(chatMessages.get(position).senderId.equals(senderId))
            return VIEW_TYPE_SENT;
        else
            return VIEW_TYPE_RECIVED;
    }


    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }
        void setData(chatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.orario.setText(chatMessage.dateTime);
        }
    }
    static class RecivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding; //10.05 p8

        RecivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerRecivedMessageBinding) {
            super(itemContainerRecivedMessageBinding.getRoot());
            binding = itemContainerRecivedMessageBinding;
        }

        void setData(chatMessage chatMessage) {
            binding.textMessageReceive.setText(chatMessage.message);
            binding.orarioR.setText(chatMessage.dateTime);
        }
    }
}
