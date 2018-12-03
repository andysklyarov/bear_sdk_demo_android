package com.bear2b.sampleapp.ui.view.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bear.common.sdk.BearHandler;
import com.bear.common.sdk.listeners.flash.FlashStatus;
import com.bear.common.sdk.listeners.flash.FlashStatusListener;
import com.bear.common.sdk.listeners.network.NetworkStatus;
import com.bear.common.sdk.listeners.network.NetworkStatusListener;
import com.bear.common.sdk.ui.activities.main.ArActivity;
import com.bear2b.sampleapp.R;
import com.bear2b.sampleapp.ui.view.activities.ExampleActivity;
import com.bear2b.sampleapp.ui.view.activities.AdvancedSampleActivity;

public class MainFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout dlMain;
    Toolbar toolbar;
    NavigationView nvMain;
    ActionBarDrawerToggle toggle;
    BearHandler handler;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        handler = ((AdvancedSampleActivity) getActivity()).getHandler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    NetworkStatusListener networkListener = new NetworkStatusListener() {
        @Override
        public void onStatusChanged(@NonNull NetworkStatus networkStatus) {
            Toast.makeText(getActivity(), "networkStatus = " + networkStatus.name(), Toast.LENGTH_SHORT).show();
        }
    };

    FlashStatusListener flashListener = new FlashStatusListener() {
        @Override
        public void onStatusChanged(@NonNull FlashStatus flashStatus) {
            Toast.makeText(getActivity(), "FlashStatus = " + flashStatus.name(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnStartScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.startScan();
            }
        });
        view.findViewById(R.id.btnFlash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler.isFlashEnabled()) {
                    handler.disableFlash();
                } else {
                    handler.enableFlash();
                }
            }
        });

        view.findViewById(R.id.btnMarker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler.isScanRunning()) {
                    handler.stopScan();
                }
                ((ArActivity) getActivity()).showArSceneWithoutTracking(226620);  //some marker id for example
            }
        });

        dlMain = (DrawerLayout) view.findViewById(R.id.dlMain);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        nvMain = (NavigationView) view.findViewById(R.id.nvMain);

        toggle = new ActionBarDrawerToggle(getActivity(), dlMain, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        dlMain.addDrawerListener(toggle);
        toggle.syncState();
        nvMain.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.addFlashListener(flashListener);
        handler.addNetworkListener(networkListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeFlashListener(flashListener);
        handler.removeNetworkListener(networkListener);
    }

    public void closeDrawer() {
        if (dlMain.isDrawerOpen(GravityCompat.START)) dlMain.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_history:
                ((AdvancedSampleActivity) getActivity()).showHistoryScreen();
                break;
            case R.id.nav_example_activity:
                Intent intent = new Intent(getActivity(), ExampleActivity.class);
                startActivity(intent);
                break;
        }
        closeDrawer();
        return false;
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }
}
