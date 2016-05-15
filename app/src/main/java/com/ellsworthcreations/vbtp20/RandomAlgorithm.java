package com.ellsworthcreations.vbtp20;

/**
 * Created by Paul on 5/14/2016.
 */
public class RandomAlgorithm extends TeamPickingAlgorithm {
    public RandomAlgorithm(Player[] allPlayers, int numberOfTeams, boolean randomize, boolean forceEqualGenders) {
        super(allPlayers, numberOfTeams, randomize, forceEqualGenders);
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
