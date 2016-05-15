package com.ellsworthcreations.vbtp20;

import android.widget.Button;

public class TeamPicker {
    private int numberOfTeams = 2;
    private int PickingAlgorithm = 2;
    public boolean UnevenTeams = false;
    public TeamSet chosenTeams;
    private TeamSet existingTeams = null;
    private Player[] allPlayers;
    private boolean forceEqualGenders = false;

    public TeamPicker(int numberOfTeams, Player[] allPlayers, TeamSet existingTeams, boolean forceEqualGenders) {
        this.numberOfTeams = numberOfTeams;
        this.existingTeams = existingTeams;
        this.allPlayers = allPlayers;
        this.forceEqualGenders = forceEqualGenders;
    }

    public TeamSet repickTeams(boolean randomize, boolean findBetter, Button repickButton) {
//        if (this.numberOfTeams < PlayerPicker.TeamsRequired) {
//            slowToast(String.format(getResources().getString(R.string.teamPicker_moreTeamsRequired), PlayerPicker.TeamsRequired));
//            return;
//        } else if (PlayerPicker.selectedPlayerIDs.length % this.numberOfTeams != 0) {
//            slowToast(String.format(getResources().getString(R.string.teamPicker_unevenTeams), selectedPlayerIDs.length, this.numberOfTeams));
//        }

        // if we haven't exited by now, we're going to try to repick.
//        slowToast(String.format(getResources().getString(R.string.teamPicker_repickingTeams), getResources().getStringArray(R.array.algorithmChoices)[PickingAlgorithm]));


        repickButton.setEnabled(false);
        TeamSet BestTeams = existingTeams;
        TeamSet teams = null;

        // go through each algorithm.
//        teams = new BringTeamsCloserAlgorithm(allPlayers, numberOfTeams, randomize, forceEqualGenders).runAlgorithm();
//        if(BestTeams == null || teams.getLargestDifferenceBetweenTeams(false) < BestTeams.getLargestDifferenceBetweenTeams(false))
//        { BestTeams = teams; }

        teams = new BringTeamsTowardAverageAlgorithm(allPlayers, numberOfTeams, randomize, forceEqualGenders).runAlgorithm();
        if(BestTeams == null || teams.getLargestDifferenceBetweenTeams(false) < BestTeams.getLargestDifferenceBetweenTeams(false))
        { BestTeams = teams; }

//        teams = new PickMostDifferentFirstAlgorithm(allPlayers, numberOfTeams, randomize, forceEqualGenders).runAlgorithm();
//        if(BestTeams == null || teams.getLargestDifferenceBetweenTeams(false) < BestTeams.getLargestDifferenceBetweenTeams(false))
//        { BestTeams = teams; }

        teams = new TeamCaptainAlgorithm(allPlayers, numberOfTeams, randomize, forceEqualGenders).runAlgorithm();
        if(BestTeams == null || teams.getLargestDifferenceBetweenTeams(false) < BestTeams.getLargestDifferenceBetweenTeams(false))
        { BestTeams = teams; }

        teams = new RandomAlgorithm(allPlayers, numberOfTeams, randomize, forceEqualGenders).runAlgorithm();
        if (BestTeams == null || teams.getLargestDifferenceBetweenTeams(false) < BestTeams.getLargestDifferenceBetweenTeams(false)) {
            BestTeams = teams;
        }

        repickButton.setEnabled(true);
        return BestTeams;
    }
}
