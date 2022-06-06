package com.example.natour21.chat.views.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MessaggioViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private String otherUserId;
    private Application application;

    public MessaggioViewModelFactory(@NonNull Application application) {
        super(application);
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MessaggioViewModel(application, otherUserId);
    }
}
