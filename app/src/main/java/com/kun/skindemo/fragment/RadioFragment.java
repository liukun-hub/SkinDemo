package com.kun.skindemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.kun.skindemo.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * @author Lance
 * @date 2018/3/12
 */

public class RadioFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_radio, container, false);
    }
}

