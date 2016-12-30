package by.digitalshop.quests.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.digitalshop.quests.R;

/**
 * Created by CoolerBy on 27.12.2016.
 */
public class AddMarkerDialog extends DialogFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_marker, container);
    }

    public static AddMarkerDialog newInstance() {
        Bundle args = new Bundle();

        AddMarkerDialog fragment = new AddMarkerDialog();
        fragment.setArguments(args);
        return fragment;
    }
}
