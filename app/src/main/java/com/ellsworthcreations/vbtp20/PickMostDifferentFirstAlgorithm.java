//package com.ellsworthcreations.vbtp20;
//
//import android.util.Log;
//
//import java.util.HashMap;
//import java.util.Iterator;
//
///**
// * Created by Paul on 5/14/2016.
// */
//public class PickMostDifferentFirstAlgorithm extends TeamPickingAlgorithm {
//    public PickMostDifferentFirstAlgorithm(Player[] allPlayers, int numberOfTeams, boolean randomize, boolean forceEqualGenders) {
//        super(allPlayers, numberOfTeams, randomize, forceEqualGenders);
//    }
//
//    @Override
//    public void nextStep(TeamSet teams, Team playerPool, Team playerPool2, int originalPlayerPool) {
//        currentTeam = getNextTeam_FurthestFromGrandAverage(teams, playerPool, currentTeam, startingAveragePlayer, randomize);
//        Team thisTeam = teams.get(currentTeam);
//        if (thisTeam.size() == 0 || (forceEqualGenders && !thisTeam.genderExistsOnTeam(Player.Gender.FEMALE) && !startedPickingOtherGender)) {
//            // picks pairs for each team, so need to use this initial algorithm each time.
//            Player[] tps;
//            if (!PlayerPicker.constraintsExist()) {
//                // do it normally.
//                tps = getFirstPlayerRickAlgorithm(playerPool);
//                if (oneFirstPickAtATime) {
//                    tps = new Player[]{tps[0]};
//                }
//            } else {
//                // otherwise, we we probably want to try to get
//                // two players that will get us closest
//                // to the other team(s)
//                Player tp = findPlayerGeneratingLowestOverallDifferential(playerPool, teams, currentTeam);
//                tps = new Player[]{tp};
//            }
//            // need to remove the player from the pool somehow though
//            for (int i = 0; i < tps.length; i++) {
//                thisTeam.add(tps[i]);
//                playerPool.remove(tps[i]);
//                Log.d("Algorithm", "Added " + tps[i] + " to team.");
//            }
//        } else {
//            Player thisPlayer = new Player();
//            //Log.d("Algorithm", "Using findPlayerRickAlgorithm");
//            thisPlayer = findPlayerRickAlgorithm(playerPool, thisTeam);
//            thisTeam.add(thisPlayer);
//            playerPool.remove(thisPlayer);
//            Log.d("Algorithm", "findPlayerRickAlgorithm grabbed: " + thisPlayer);
//            teams.set(currentTeam, thisTeam);
//        }
//    }
//
//    public Player findPlayerRickAlgorithm(Team playerPool, Team team) {
//        //Log.v("RickAlgorithm", "starting findPlayerRickAlgorithm");
//        if (playerPool.size() == 1) {
//            return playerPool.getFirst();
//        }
//
//        Player bestPlayer = new Player();
//        double largestDistance = -1;
//        Iterator<Player> pitr = playerPool.iterator();
//        while (pitr.hasNext()) {
//            Player p1 = pitr.next();
//            //Log.v("RickAlgorithm", "comparing " + p1.getName() + " to this team.");
//            double thisDistance = p1.distanceTo(team);
//            if (thisDistance > largestDistance || largestDistance == -1) {
//                //Log.v("RickAlgorithm", "new best player is " + p1.getName() + " with a distance of " + thisDistance);
//                bestPlayer = p1;
//                largestDistance = thisDistance;
//            }
//        }
//        //Log.v("RickAlgorithm", "FINISHED: best player is " + bestPlayer.getName() + " with a distance of " + largestDistance);
//        return bestPlayer;
//    }
//}
