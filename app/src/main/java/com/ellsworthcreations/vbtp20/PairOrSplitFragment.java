package com.ellsworthcreations.vbtp20;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PairOrSplitFragment extends DialogFragment {
    private Boolean split = false;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PairOrSplitListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    PairOrSplitListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (PairOrSplitListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                + " must implement PairOrSplitListener");
        }
    }

    public void setSplit(Boolean split) { this.split = split; }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View ll = inflater.inflate(R.layout.pair_or_split_view, null);
        Spinner spinner = (Spinner) ll.findViewById(R.id.pair_or_split_first_player);
        Spinner spinner2 = (Spinner) ll.findViewById(R.id.pair_or_split_second_player);

        ArrayAdapter<Player> adapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_dropdown_item, VBTP.PlayerDB(this.getContext()).getAllPlayersSortedByName());
        spinner.setAdapter(adapter);

        // setup a listener so that we can filter the second spinner automatically and filter out
        // the player selected in the first spinner.  Can't pair or split someone with themself :)
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                Spinner spinner2 = (Spinner) ll.findViewById(R.id.pair_or_split_second_player);
                Player selectedFirst = (Player) parent.getItemAtPosition(pos);
                Player selectedSecondPlayer = (Player) spinner2.getSelectedItem();

                List<Player> players = new ArrayList<Player>(Arrays.asList(VBTP.PlayerDB(parent.getContext()).getAllPlayersSortedByName()));
                ArrayAdapter<Player> adapter2 = new ArrayAdapter(parent.getContext(), android.R.layout.simple_spinner_dropdown_item, players);

                int selectedIndex = 0;
                for(int i=0 ; i<adapter2.getCount() ; i++){
                    Player p = adapter2.getItem(i);
                    if(p.equals(selectedFirst))
                    { adapter2.remove(p); }
                    else if(p.equals(selectedSecondPlayer))
                    { selectedIndex = i; }

                    // is this player already paired or split?
                    for(Constraint c: VBTP.constraints) {
                        if(c.player1.equals(selectedFirst) && c.player2.equals(p) && c.split == PairOrSplitFragment.this.split)
                        { adapter2.remove(p); }
                    }
                }
                spinner2.setAdapter(adapter2);
                spinner2.setSelection(selectedIndex);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        List<Player> players = new ArrayList<Player>(Arrays.asList(VBTP.PlayerDB(this.getContext()).getAllPlayersSortedByName()));
        ArrayAdapter<Player> adapter2 = new ArrayAdapter(
                this.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                players);

        // remove the first item, since it is going to be selected already.
        Player firstPlayer = (Player) adapter.getItem(0);
        for(int i=0 ; i<adapter2.getCount() ; i++){
            Player p = adapter2.getItem(i);
            if(p.equals(firstPlayer))
            { adapter2.remove(p); }
            else {
                // is this player already paired or split?
                for(Constraint c: VBTP.constraints) {
                    if(c.player1.equals(firstPlayer) && c.player2.equals(p) && c.split == PairOrSplitFragment.this.split)
                    { adapter2.remove(p); }
                }
            }
        }
        spinner2.setAdapter(adapter2);

        builder.setView(ll).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PairOrSplitFragment.this.getDialog().cancel();
            }
        });

        if(this.split) {
            builder.setPositiveButton(R.string.button_split, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Player p1 = (Player) ((Spinner) ll.findViewById(R.id.pair_or_split_first_player)).getSelectedItem();
                    Player p2 = (Player) ((Spinner) ll.findViewById(R.id.pair_or_split_second_player)).getSelectedItem();
                    VBTP.addConstraint(p1, p2, true);
                    mListener.onDialogPositiveClick(PairOrSplitFragment.this);
                }
            });
        } else {
            builder.setPositiveButton(R.string.button_pair, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Player p1 = (Player) ((Spinner) ll.findViewById(R.id.pair_or_split_first_player)).getSelectedItem();
                    Player p2 = (Player) ((Spinner) ll.findViewById(R.id.pair_or_split_second_player)).getSelectedItem();
                    VBTP.addConstraint(p1, p2, false);
                    mListener.onDialogPositiveClick(PairOrSplitFragment.this);
                }
            });
        }

        return builder.create();
    }
}
