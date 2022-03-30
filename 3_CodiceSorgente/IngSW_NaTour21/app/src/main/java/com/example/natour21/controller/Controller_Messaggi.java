package com.example.natour21.controller;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.dynamicfeatures.Constants;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.natour21.ChatAdapter;
import com.example.natour21.ConstantsChat;
import com.example.natour21.ListChatAdapter;
import com.example.natour21.ParentItem;
import com.example.natour21.PostAdapter;
import com.example.natour21.R;
import com.example.natour21.User;
import com.example.natour21.UtentiChat;
import com.example.natour21.chatMessage;
import com.example.natour21.databinding.ActivityChatBinding;
import com.example.natour21.entities.Messaggio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
// YT

// classe MessagingService video2 min 6.45
public class Controller_Messaggi extends AppCompatActivity {


    private ActivityChatBinding binding;
    private User receiverUser;  // nome chat
    private List<chatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    // private Firebase dadabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        loadReceiverDetails();
    //    init();
       // listenMessage();
    }

    private void loadReceiverDetails(){
        receiverUser = (User) getIntent().getSerializableExtra(ConstantsChat.KEY_USER);
        binding.nomeChat.setText(receiverUser.nome);
    }
/* test
    //Invio messaggi
    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages, preferenceManager.getString(ConstantsChat.KEY_USER_ID)
        );
        binding.chatRecycleView.setAdapter(chatAdapter);
    //  database = FirebaseFirestore.getInstance();
    }
*/
   /*      CONTROLLO IN FIREBASE
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null)
            return;
        if(value != null){
            int count = chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    chatMessage chatMessage = new chatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SEND_ID);
                    ....passare tutti gli altri dati...
                }
            }
            Collections.sort(chatMessage, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if(count == 0)
                chatAdapter.notifyDataSetChanged();
                else{
                    chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                    binding.chatRecycleView.smoothScrollToPosition(chatMessages.size() -1);
                }
                binding.chatRecycleView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
    };
*/
/*test
    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        message.put(ConstantsChat.KEY_SENDER_ID, preferenceManager.getString(ConstantsChat.KEY_USER_ID));
        message.put(ConstantsChat.KEY_RECEIVER_ID, receiverUser.id);
        message.put(ConstantsChat.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(ConstantsChat.KEY_TIMESTAMP, new Date());
        /// database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        binding.inputMessage.getText(null);
    }
*/
    /*
    private void listenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
            •
        whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            •whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
            •addSnapshotListener(eventListener);
        database.collection(Constants.KEY COLLECTION CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                •
        whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

     */

    private void setListener(){
      //  binding.layoutSend.setOnClickListener(v -> sendMessage());
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

}
