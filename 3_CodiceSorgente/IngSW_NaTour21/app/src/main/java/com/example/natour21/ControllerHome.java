package com.example.natour21;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

public class ControllerHome extends AppCompatActivity{
    private ImageView add_itin;
    private Animation anim_btn = null, anim_txtview = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_home);

        add_itin = findViewById(R.id.btn_add_itin);

        anim_btn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_bottone);
        anim_txtview = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_textview);

        add_itin.setOnClickListener(v -> {
            add_itin.startAnimation(anim_btn);
            startActivity(new Intent(ControllerHome.this, ControllerAddItin.class));
        });

        //Apertura  menu da imageview
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        // colore delle icone naturali
        NavigationView nav_View = findViewById(R.id.navigationView);
        nav_View.setItemIconTintList(null);

        //Apertura fragment
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(nav_View, navController);

        //Setto il titolo della toolBar con il nome del fragment
        final TextView textTitolo = findViewById(R.id.textTitolo);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                textTitolo.setText(navDestination.getLabel());
            }
        });

    }


}
