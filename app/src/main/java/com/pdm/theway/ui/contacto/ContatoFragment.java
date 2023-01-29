package com.pdm.theway.ui.contacto;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdm.theway.databinding.FragmentContatoBinding;

public class ContatoFragment extends Fragment {

    private FragmentContatoBinding binding;

    public ContatoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContatoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
}