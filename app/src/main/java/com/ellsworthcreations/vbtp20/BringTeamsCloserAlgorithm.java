package com.ellsworthcreations.vbtp20;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Paul on 5/14/2016.
 */
public class BringTeamsCloserAlgorithm extends TeamPickingAlgorithm {
    public BringTeamsCloserAlgorithm(Context ctx, Player[] allPlayers, int numberOfTeams, boolean randomize, boolean forceEqualGenders) {
        super(ctx, allPlayers, numberOfTeams, randomize, forceEqualGenders);
    }

    @Override
    public void nextStep(TeamSet teams, Team playerPool, Team playerPool2, int originalPlayerPool) {
        currentTeam = getNextTeam_FurthestFromGrandAverage(teams, playerPool, currentTeam, startingAveragePlayer, randomize);
        Team thisTeam = teams.get(currentTeam);
        if (thisTeam.size() == 0 || (forceEqualGenders && !thisTeam.genderExistsOnTeam(Player.Gender.FEMALE) && !startedPickingOtherGender)) {
            // picks pairs for each team, so need to use this initial algorithm each time.
            Player[] tps;
//            if (!PlayerPicker.constraintsExist()) {
                // do it normally.
                tps = getFirstPlayerRickAlgorithm(playerPool);
                if (oneFirstPickAtATime) {
                    tps = new Player[]{tps[0]};
                }
//            } else {
                // otherwise, we we probably want to try to get
                // two players that will get us closest
                // to the other team(s)
//                Player tp = findPlayerGeneratingLowestOverallDifferential(playerPool, teams, currentTeam);
//                tps = new Player[]{tp};
//            }
            // need to remove the player from the pool somehow though
            for (int i = 0; i < tps.length; i++) {
                thisTeam.add(tps[i]);
                playerPool.remove(tps[i]);
                Log.d("Algorith", "Added " + tps[i] + " to team.");
            }
        } else {
            Player thisPlayer = new Player();
            thisPlayer = findPlayerGeneratingLowestOverallDifferential(playerPool, teams, currentTeam);
            thisTeam.add(thisPlayer);
            playerPool.remove(thisPlayer);
            teams.set(currentTeam, thisTeam);
        }
    }
}
