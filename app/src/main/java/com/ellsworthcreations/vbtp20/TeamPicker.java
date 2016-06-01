package com.ellsworthcreations.vbtp20;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageButton;

public class TeamPicker {
    private int numberOfTeams = 2;
    private int PickingAlgorithm = 2;
    public boolean UnevenTeams = false;
    public TeamSet chosenTeams;
    private TeamSet existingTeams = null;
    private Player[] allPlayers;
    private boolean forceEqualGenders = false;
    private Context context;

    public TeamPicker(Context ctx, int numberOfTeams, Player[] allPlayers, TeamSet existingTeams, boolean forceEqualGenders) {
        this.context = ctx;
        this.numberOfTeams = numberOfTeams;
        this.existingTeams = existingTeams;
        this.allPlayers = allPlayers;
        this.forceEqualGenders = forceEqualGenders;
    }

    public TeamSet repickTeams(boolean randomize, boolean findBetter, ImageButton repickButton) {
//        if (this.numberOfTeams < PlayerPicker.TeamsRequired) {
//            slowToast(String.format(getResources().getString(R.string.teamPicker_moreTeamsRequired), PlayerPicker.TeamsRequired));
//            return;
//        } else if (PlayerPicker.selectedPlayerIDs.length % this.numberOfTeams != 0) {
//            slowToast(String.format(getResources().getString(R.string.teamPicker_unevenTeams), selectedPlayerIDs.length, this.numberOfTeams));
//        }

        // if we haven't exited by now, we're going to try to repick.
//        slowToast(String.format(getResources().getString(R.string.teamPicker_repickingTeams), getResources().getStringArray(R.array.algorithmChoices)[PickingAlgorithm]));


        TeamSet BestTeams = existingTeams;
        TeamSet teams = null;

        // go through each algorithm.
        teams = new BringTeamsCloserAlgorithm(context, allPlayers, numberOfTeams, randomize, forceEqualGenders).runAlgorithm();
        if(teams != null) {
            if (BestTeams == null || teams.getLargestDifferenceBetweenTeams(context, false) < BestTeams.getLargestDifferenceBetweenTeams(context, false)) {
                BestTeams = teams;
            }
        }

        teams = new BringTeamsTowardAverageAlgorithm(context, allPlayers, numberOfTeams, randomize, forceEqualGenders).runAlgorithm();
        if(teams != null) {
            if (BestTeams == null || teams.getLargestDifferenceBetweenTeams(context, false) < BestTeams.getLargestDifferenceBetweenTeams(context, false)) {
                BestTeams = teams;
            }
        }

        teams = new PickMostDifferentFirstAlgorithm(context, allPlayers, numberOfTeams, randomize, forceEqualGenders).runAlgorithm();
        if(teams != null) {
            if (BestTeams == null || teams.getLargestDifferenceBetweenTeams(context, false) < BestTeams.getLargestDifferenceBetweenTeams(context, false)) {
                BestTeams = teams;
            }
        }

        teams = new TeamCaptainAlgorithm(context, allPlayers, numberOfTeams, randomize, forceEqualGenders).runAlgorithm();
        if(teams != null) {
            if (BestTeams == null || teams.getLargestDifferenceBetweenTeams(context, false) < BestTeams.getLargestDifferenceBetweenTeams(context, false)) {
                BestTeams = teams;
            }
        }

        teams = new RandomAlgorithm(context, allPlayers, numberOfTeams, randomize, forceEqualGenders).runAlgorithm();
        if(teams != null) {
            if (BestTeams == null || teams.getLargestDifferenceBetweenTeams(context, false) < BestTeams.getLargestDifferenceBetweenTeams(context, false)) {
                BestTeams = teams;
            }
        }

        return BestTeams;
    }
}
