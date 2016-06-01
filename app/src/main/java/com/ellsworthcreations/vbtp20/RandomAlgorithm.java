package com.ellsworthcreations.vbtp20;

import android.content.Context;

/**
 * Created by Paul on 5/14/2016.
 */
public class RandomAlgorithm extends TeamPickingAlgorithm {
    public RandomAlgorithm(Context ctx, Player[] allPlayers, int numberOfTeams, boolean randomize, boolean forceEqualGenders) {
        super(ctx, allPlayers, numberOfTeams, randomize, forceEqualGenders);
    }

    @Override
    public void nextStep(TeamSet teams, Team playerPool, Team playerPool2, int originalPlayerPool) {
        currentTeam = getNextTeam(teams, playerPool, currentTeam, randomize);
        Team thisTeam = teams.get(currentTeam);
        Player tp = playerPool.getRandomPlayer();
        thisTeam.add(tp);
        playerPool.remove(tp);
    }
}
