package com.ellsworthcreations.vbtp20;

import android.annotation.SuppressLint;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainView extends AppCompatActivity implements PairOrSplitFragment.PairOrSplitListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    // the currently displaying teamset.
    public TeamSet chosenTeams =  null;

    // force the teams to be re-chosen
    private Boolean forcePick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_view);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.edit_player_button).setOnTouchListener(mDelayHideTouchListener);
    }

    /** What happens when you split or pair things. **/
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        forcePick = true;
        showConstraints();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    public void editPlayers(View view) {
        Intent intent = new Intent(this, PlayerListActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void splitPlayers(View view) {
        PairOrSplitFragment pairFragment = new PairOrSplitFragment();
        pairFragment.setSplit(true);
        pairFragment.show(getSupportFragmentManager(), "splitting");
    }

    public void pairPlayers(View view) {
        PairOrSplitFragment pairFragment = new PairOrSplitFragment();
        pairFragment.setSplit(false);
        pairFragment.show(getSupportFragmentManager(), "pairing");
    }

    public void showConstraints() {
        LinearLayout pairs_and_splits = (LinearLayout) this.findViewById(R.id.pairs_and_splits);
        pairs_and_splits.removeAllViews();
        for(Constraint c: VBTP.constraints) {
            View ll = getLayoutInflater().inflate(R.layout.pair_or_split_view_single, null);
            ll.findViewById(R.id.pair_or_split_view_remove_button).setTag(c);
            TextView tv = (TextView) ll.findViewById(R.id.pair_or_split_view_text);
            tv.setText(c.toString());
            pairs_and_splits.addView(ll);
        }
    }

    public void deleteConstraint(View v) {
        forcePick = true;
        Constraint c = (Constraint) v.getTag();
        VBTP.removeConstraint(c);
        showConstraints();
    }

    public void pickTeams(View view) {
        view.setEnabled(false);
        // this is to make sure we can get skill weights and stuff.
        Player[] allPlayers = VBTP.PlayerDB(this.getBaseContext()).getActivePlayersSortedByName();
        Log.d("pickTeams", "length is: "+allPlayers.length);

        // this needs to go into an async thing.
        Boolean forceEqualGenders = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean("pref_gender_distribution", false);
        TeamPicker tp = new TeamPicker(this.getApplicationContext(), 2, allPlayers, chosenTeams, forceEqualGenders);
        if(forcePick) {
            tp = new TeamPicker(this.getApplicationContext(), 2, allPlayers, null, forceEqualGenders);
        }
        chosenTeams = tp.repickTeams(true, true, (ImageButton) view);
        updateDisplayWithTeams(chosenTeams);
        view.setEnabled(true);
        forcePick = false;
    }

    public void updateDisplayWithTeams(TeamSet teams) {
        TextView tv = (TextView) findViewById(R.id.chosenTeams);
        tv.setText("");
        int teamBiggestScore = 0;
        int teamSmallestScore = -1;
        Skills highestSkills = new Skills();
        Skills lowestSkills = new Skills(new int[]{0, 0, 0, 0, 0, 0, 0});
        if(teams == null) { tv.setText("Could not generate teams."); return; }

        for (int i = 0; i < teams.size(); i++) {
            int thisTeamNumber = i + 1;
            int teamTotalScore = 0;

            Team thisTeam = teams.get(i);
            String playerList = "";
            ListIterator<Player> itr = thisTeam.listIterator();
            while (itr.hasNext()) {
                Player thisPlayer = itr.next();

                // get highest/lowest skills.
                Skills teamSkills = thisTeam.getCumulativeSkills();
                Iterator<String> tsitr = teamSkills.keySet().iterator();
                //String TAG = "HighestLowestSkillComputation";
                while (tsitr.hasNext()) {
                    String key = tsitr.next();
                    if (!highestSkills.containsKey(key)) {
                        highestSkills.put(key, teamSkills.get(key));
                        //Log.v("SkillMeasurements", "initialized highest " + key + " with: " + teamSkills.get(key));
                    } else if (teamSkills.get(key) > highestSkills.get(key)) {
                        highestSkills.put(key, teamSkills.get(key));
                        //Log.v("SkillMeasurements", "set highest " + key + " to: " + teamSkills.get(key));
                    }
                    if (!lowestSkills.containsKey(key) || lowestSkills.get(key) == 0) {
                        lowestSkills.put(key, teamSkills.get(key));
                        //Log.v("SkillMeasurements", "initialized lowest " + key + " with: " + teamSkills.get(key));
                    } else if (teamSkills.get(key) < lowestSkills.get(key) || lowestSkills.get(key) == 0) {
                        lowestSkills.put(key, teamSkills.get(key));
                        //Log.v("SkillMeasurements", "set lowest " + key + " to: " + teamSkills.get(key));
                    }
                    teamTotalScore += teamSkills.get(key);
                }
                playerList += "\n" + thisPlayer.getName();
            }

            teamTotalScore = thisTeam.getSkillsSum();
            if (teamTotalScore < teamSmallestScore || teamSmallestScore == -1) {
                teamSmallestScore = teamTotalScore;
            }
            if (teamTotalScore > teamBiggestScore) {
                teamBiggestScore = teamTotalScore;
            }

            tv.append("Team #" + thisTeamNumber + ", " + thisTeam.size() + " players (" + teamTotalScore + ")\n--------------------------\n" + playerList + "\n\n");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
//        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
