package com.ellsworthcreations.vbtp20;

import java.util.Iterator;

/**
 * Created by Paul on 5/14/2016.
 */
public class BringTeamsTowardAverageAlgorithm extends TeamPickingAlgorithm {
    public BringTeamsTowardAverageAlgorithm(Player[] allPlayers, int numberOfTeams, boolean randomize, boolean forceEqualGenders) {
        super(allPlayers, numberOfTeams, randomize, forceEqualGenders);
    }

    @Override
    public void nextStep(TeamSet teams, Team playerPool, Team playerPool2, int originalPlayerPool) {
        currentTeam = getNextTeam_FurthestFromGrandAverage(teams, playerPool, currentTeam, startingAveragePlayer, randomize);
        Team thisTeam = teams.get(currentTeam);
        if (thisTeam.size() == 0 || (forceEqualGenders && !thisTeam.genderExistsOnTeam(Player.Gender.FEMALE) && !startedPickingOtherGender)) {
            Player[] tps = getFirstPlayerGrandAverageAlgorithm(playerPool, startingAveragePlayer);
            if (oneFirstPickAtATime) {
                tps = new Player[]{tps[0]};
            }
            // need to remove the player from the pool somehow though
            for (int i = 0; i < tps.length; i++) {
                thisTeam.add(tps[i]);
                playerPool.removePlayer(tps[i]);
            }
        } else {
            Player thisPlayer = new Player();
            thisPlayer = findPlayerBringingTeamTowardsAverage(playerPool, teams, currentTeam, startingAveragePlayer);
            thisTeam.add(thisPlayer);
            playerPool.remove(thisPlayer);
            teams.set(currentTeam, thisTeam);
        }
    }

    public Player findPlayerBringingTeamTowardsAverage(Team playerPool, TeamSet teams, int myTeam, Player startingAveragePlayer) {

        if (playerPool.size() == 1) {
            return playerPool.getFirst();
        }

        Player bestPlayer = new Player();
        double bestDistance = -1;

        // so we need to walk through and get the best player.
        Iterator<Player> pitr = playerPool.iterator();
        while (pitr.hasNext()) {
            // for each player, calculate the differential.
            Player tp = pitr.next();

            // get my team average skills, adding in the possible player.
            Skills myMap = teams.get(myTeam).getCumulativeSkills();
            Iterator<String> it = myMap.keySet().iterator();
            while (it.hasNext()) {
                String thisSkill = it.next();
                int thisTS = myMap.get(thisSkill);
                thisTS += tp.getSkill(thisSkill);
                myMap.put(thisSkill, thisTS / (teams.get(myTeam).size() + 1));
            }

            double thisOne = myMap.distanceTo(startingAveragePlayer);

            if (thisOne < bestDistance || bestDistance == -1) {
                bestDistance = thisOne;
                bestPlayer = tp;
            }

            // remove from my team.
            //myt.removeLast();
            //teams.set(myTeam, myt);
        }
        //Log.v("Algorithm", "Best player: " + bestPlayer.getName());
        return bestPlayer;
    }

    public Player[] getFirstPlayerGrandAverageAlgorithm(Team playerPool, Player grandAveragePlayer) {
        Player[] players;

        // create new blank array.
        if (playerPool.size() > 2) {
            players = new Player[2];
        }

        // return the only two players left.
        else if (playerPool.size() == 2) {
            return new Player[]{playerPool.get(0), playerPool.get(1)};
        }

        // return one player.
        else if (playerPool.size() == 1) {
            return new Player[]{playerPool.getFirst()};
        }

        // return no players.
        else {
            return new Player[0];
        }

        double largestDistanceFromAverage = -1;
        double distanceFromGrandAverageAsTeam = -1;

        Iterator<Player> pitr = playerPool.iterator();

        // get the player furthest from the average player
        while (pitr.hasNext()) {
            Player p1 = pitr.next();
            double thisDistance = p1.distanceTo(grandAveragePlayer);
            if (thisDistance > largestDistanceFromAverage || largestDistanceFromAverage == -1) {
                players[0] = p1;
                largestDistanceFromAverage = thisDistance;
            }
        }

        // get the player that would pull the first player closest to the average.  That means we make a "team" out of the two players...
        pitr = playerPool.iterator();
        while (pitr.hasNext()) {
            Player p1 = pitr.next();

            if (players[0].getPlayerID() == p1.getPlayerID()) {
                continue;
            }

            // make a team.
            Team tt = new Team();
            tt.add(players[0]);
            tt.add(p1);

            double thisDistance = grandAveragePlayer.distanceTo(tt);
            if (thisDistance < distanceFromGrandAverageAsTeam || distanceFromGrandAverageAsTeam == -1) {
                players[1] = p1;
                distanceFromGrandAverageAsTeam = thisDistance;
            }
        }

        //Log.v("GrandAverageAlgorithm", "FINISHED: best first player pair is " + players[0].getName() + " and " + players[1].getName() + " who are " + distanceFromGrandAverageAsTeam + " away from the grand average when combined.");
        return players;
    }
}
