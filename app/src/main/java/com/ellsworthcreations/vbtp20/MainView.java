package com.ellsworthcreations.vbtp20;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainView extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_view);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.edit_player_button).setOnTouchListener(mDelayHideTouchListener);
    }

    public void editPlayers(View view) {
        Intent intent = new Intent(this, PlayerListActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void pickTeams(View view) {
        // this is to make sure we can get skill weights and stuff.
        Preferences gp = VBTP.GlobalPreferences(this.getBaseContext());
        Player[] allPlayers = VBTP.PlayerDB(this.getBaseContext()).getActivePlayersSortedByName();
        Log.d("pickTeams", "length is: "+allPlayers.length);

        // this needs to go into an async thing.
        TeamPicker tp = new TeamPicker(2, allPlayers, chosenTeams, false);
        chosenTeams = tp.repickTeams(true, true, (Button) view);
        updateDisplayWithTeams(chosenTeams);
    }

    public void updateDisplayWithTeams(TeamSet teams) {
        TextView tv = (TextView) findViewById(R.id.chosenTeams);
        tv.setText("");
        int teamBiggestScore = 0;
        int teamSmallestScore = -1;
        Skills highestSkills = new Skills();
        Skills lowestSkills = new Skills(new int[]{0, 0, 0, 0, 0, 0, 0});

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

//        if (sp.getBooleanPreference("ShowDetailedAlgorithmResults")) {
//            TextView differentialText = new TextView(this);
//            differentialText.setPadding(0, 10, 0, 0);
//            differentialText.setTextColor(Color.WHITE);
//            differentialText.setText(
//                    String.format(resources.getString(R.string.cumulativeDifferential), (teamBiggestScore - teamSmallestScore))
//            );
//            ll.addView(differentialText);
//
//            TextView it = new TextView(this);
//            it.setTextColor(Color.WHITE);
//            it.setText(resources.getString(R.string.individualDifferentialsHeader));
//            ll.addView(it);
//            // display differentials for each skill.  works for both high/low.
//            TableLayout tl = new TableLayout(this);
//            Iterator<String> tsitr = highestSkills.keySet().iterator();
//            int totalIndividual = 0;
//            while (tsitr.hasNext()) {
//                String thisskill = tsitr.next();
//                TextView dTitle = new TextView(this);
//                TextView dText = new TextView(this);
//                dTitle.setTextColor(Color.WHITE);
//                dTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                dTitle.setPadding(0, 0, 15, 0);
//                TableRow tr = new TableRow(this);
//                dTitle.setText(thisskill);
//                tr.addView(dTitle);
//                dText.setText("" + (highestSkills.get(thisskill) - lowestSkills.get(thisskill)));
//                totalIndividual += (highestSkills.get(thisskill) - lowestSkills.get(thisskill));
//                tr.addView(dText);
//                tl.addView(tr);
//            }
//
//            // add a total
//            TextView dTitle = new TextView(this);
//            TextView dText = new TextView(this);
//            dText.setText(Integer.toString(totalIndividual));
//            dTitle.setTextColor(Color.WHITE);
//            dTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//            dTitle.setPadding(0, 0, 15, 0);
//            TableRow tr = new TableRow(this);
//            dTitle.setText(resources.getString(R.string.individualDifferentialsTotal));
//            tr.addView(dTitle);
//            tr.addView(dText);
//            tl.addView(tr);
//            ll.addView(tl);
//        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
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
