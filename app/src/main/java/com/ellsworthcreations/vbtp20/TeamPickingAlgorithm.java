package com.ellsworthcreations.vbtp20;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Paul on 5/14/2016.
 */
public class TeamPickingAlgorithm {
    public Player[] allPlayers;
    public Player startingAveragePlayer;
    public Player averagePlayer;
    public boolean randomize;
    public int currentTeam;
    public boolean forceEqualGenders;
    public boolean UnevenTeams = false;
    public int numberOfTeams = 2;
    public boolean startedPickingOtherGender = false;
    public boolean oneFirstPickAtATime = false;

    public TeamPickingAlgorithm() { }

    public TeamPickingAlgorithm(Player[] allPlayers, int numberOfTeams, boolean randomize, boolean forceEqualGenders) {
        this.allPlayers = allPlayers;
        this.numberOfTeams = numberOfTeams;
        this.forceEqualGenders = forceEqualGenders;
    }

    private boolean canPickTeams() {
        if (allPlayers.length < numberOfTeams) {
            // heh.
            return false;
        }

        // these require at least 2 per team to work.
//        else if ((algorithm == 1 || algorithm == 2 || algorithm == 3)
//                && allPlayers.length < 2 * numberOfTeams) {
//            return null;
//        }

        return true;
    }

    public TeamSet runAlgorithm() {
// so I'm going to need a list of teams.  Each team will be an linked list of players.
        TeamSet teams = new TeamSet();

        // I also need a pool of players.
        Team playerPool = new Team();

        // this one is used for randomizing.
        Team playerPool2 = new Team();

        // used for making equal male/female counts on teams

        Team firstPlayers = new Team();        // this will get a list of players picked first.
        Team otherPlayers = new Team();        // players picked second.
        Team malePlayers = new Team();        // male players
        Team femalePlayers = new Team();    // female players.

        // my random generator. yay.
        Random generator = new Random();

        // this doens't actually have to be random, since we shuffle the array every time.
        // we will remove 25% of the players.
        int randomRemovalInterval = 4;

        // sort allPlayers ... randomly.
        if (randomize) {
            shufflePlayerArray(allPlayers);
        }

        for (int i = 0; i < allPlayers.length; i++) {
            boolean isSelected = allPlayers[i].isActive();

            if (isSelected) {
                // we don't want to split up any pairs or anti-pairs... so
                // random functionality stuff won't include those.
                if (!randomize || forceEqualGenders || i % randomRemovalInterval != 0)
//                        || PlayerPicker.getAllAntiPairsForPlayer(allPlayers[i].getPlayerID()).length != 0
//                        || PlayerPicker.getAllPairsForPlayer(allPlayers[i].getPlayerID()).length != 0)
                {
                 playerPool.add(allPlayers[i]);
                }

                // take out a certain percentage of players.
                else {
                    playerPool2.add(allPlayers[i]);
                }
            }
        }
        //Log.d("PlayerPoolSize","Player Pool Size at beginning: " + playerPool.size());
        //Log.d("PlayerPoolSize","AllPlayers array length at beginning: " + allPlayers.length);
        // check to see if we have uneven teams.
        if ((playerPool.size() + playerPool2.size()) % numberOfTeams != 0) {
            this.UnevenTeams = true;
        } else {
            this.UnevenTeams = false;
        }

        for (int i = 0; i < numberOfTeams; i++) {
            teams.add(new Team());
        }

        // team to start with
        int teamToStartWith = 0;

        int currentTeam = teamToStartWith;

        startingAveragePlayer = playerPool.getAveragePlayer();
        averagePlayer = startingAveragePlayer;

        int originalPlayerPoolSize = playerPool.size();
        //Log.d("RepickTeams","randomize: " + randomize);
        // CONSTRAINTS ... SORT ALL CONSTRAINTS FIRST
//        if (PlayerPicker.constraintsExist() && sp.getBooleanPreference("ShowPairOptions")) {
//            teams = sortOutConstraintsFirst(teams, playerPool, startingAveragePlayer, randomize);
//            if (teams == null) {
//                return null;
//            }
//            Iterator<Team> teamsItr = teams.iterator();
//            while (teamsItr.hasNext()) {
//                Team thisTeam = teamsItr.next();
//                Iterator<Player> teamItr = thisTeam.iterator();
//                while (teamItr.hasNext()) {
//                    Player tp = teamItr.next();
//                    Iterator<Player> pitr = playerPool.iterator();
//                    while (pitr.hasNext()) {
//                        Player tp2 = pitr.next();
//                        if (tp.getPlayerID() == tp2.getPlayerID()) {
//                            playerPool.remove(tp2);
//                            break;
//                        }
//                    }
//                }
//            }
//        }

        // now that constraints are sorted out, sort lists of genders if necessary.
        // pick a certain gender first, possibly.

        if (forceEqualGenders) {
            // divide into lists.
            Iterator<Player> pitr = playerPool.iterator();
            while (pitr.hasNext()) {
                Player tp = pitr.next();
                if (tp.isMale()) {
                    malePlayers.add(tp);
                } else {
                    femalePlayers.add(tp);
                }
            }

            // so first of all, ARE there both genders...
            if (femalePlayers.size() == 0 || malePlayers.size() == 0) {
                // don't have to even it :)
                // playerPool already has a bunch anyway...
                forceEqualGenders = false;
            } else {
                // for now, pick female players first...
                otherPlayers = malePlayers;

                // start with female players.
                // first, though, we need to only pick an even number of them...
                if (femalePlayers.size() < numberOfTeams || femalePlayers.size() == numberOfTeams) {
                    firstPlayers = femalePlayers;
                } else {
                    Log.d("EqualGenders", "Before removing: " + femalePlayers);
                    while (femalePlayers.size() >= numberOfTeams) {
                        for (int i = 0; i < numberOfTeams; i++) {
                            firstPlayers.add(femalePlayers.removeAverage());
                        }
                    }
                    Log.d("EqualGenders", "After removing: " + femalePlayers);
                    otherPlayers.addAll(femalePlayers);
                    femalePlayers.clear();
                }

                playerPool = firstPlayers;
                if (playerPool.size() < (numberOfTeams * 2)) {
                    oneFirstPickAtATime = true;
                }

                // this is how we randomize if we're forcing equal genders.
                if (this.randomize) {
                    for (int i = 0; i < otherPlayers.size(); i++) {
                        if (i % randomRemovalInterval != 0) {
                            continue;
                        }

                        // take out a certain percentage of players.
                        else {
                            playerPool2.add(otherPlayers.get(i));
                            otherPlayers.remove(otherPlayers.get(i));
                        }
                    }
                }
            }
            Log.d("EqualGenders", "Finished making the two lists.\nFirst picks: " + playerPool + "\nSecond picks: " + otherPlayers);
            Log.d("EqualGenders", "Setting playerPool to female: " + playerPool);
        }

        while (playerPool.size() > 0 || playerPool2.size() > 0) {
            if (playerPool.size() <= 2) {
                // better add the other players in!
                Log.d("RepickTeams", "putting " + playerPool2.size() + " players back in because playerPool is " + playerPool.size());
                while (playerPool2.size() > 0) {
                    playerPool.add(playerPool2.remove());
                }
            }

            if (((forceEqualGenders && startedPickingOtherGender || !forceEqualGenders)) && randomize && playerPool2.size() > 0) {
                if (originalPlayerPoolSize - playerPool.size() > ((originalPlayerPoolSize / randomRemovalInterval))) {
                    Log.d("RepickTeams", "putting " + playerPool2.size() + " players back in because " + originalPlayerPoolSize + " - " + playerPool.size() + " > " + ((originalPlayerPoolSize / randomRemovalInterval)));
                    while (playerPool2.size() > 0) {
                        playerPool.add(playerPool2.remove());
                    }
                }
            }

            nextStep(teams, playerPool, playerPool2, originalPlayerPoolSize);

            // for forcing equal male/female player teams...
            if (playerPool.size() == 0 && forceEqualGenders && !startedPickingOtherGender) {
                playerPool = otherPlayers;
                startedPickingOtherGender = true;
                Log.d("EqualGenders", "Setting playerPool to other players: " + otherPlayers);
            }
        }

        return teams;
    }

    public int getNextTeam_FurthestFromGrandAverage(TeamSet teams, Team playerPool,
        int lastTeamPick, Player grandAveragePlayer, boolean randomize) {
        int nextTeam = 0;
        LinkedList<Integer> lowestMembershipTeams = teams.getLowestMembershipTeams();

        // if there's only one, return that.
        if (lowestMembershipTeams.size() == 1) {
            return lowestMembershipTeams.get(0);
        }

        // if we have fewer players than teams, give the pick to the lowest ranking member of lowestMembershipTeams.
        if (playerPool.size() < teams.size()) {
            int lowestRank = -1;
            Iterator<Integer> titr2 = lowestMembershipTeams.iterator();
            while (titr2.hasNext()) {
                int thisTeamNumber = titr2.next();
                Team tt = teams.get(thisTeamNumber);
                if (tt.getSkillsSum() < lowestRank || lowestRank == -1) {
                    nextTeam = thisTeamNumber;
                }
            }
        }

        // otherwise, do it based on who is farthest away from the average ... amongst the "lowest membership" teams.
        else {
            double highestDistance = -1;
            Iterator<Integer> titr = lowestMembershipTeams.iterator();
            while (titr.hasNext()) {
                int thisTeam = titr.next();
                double thisDistance = grandAveragePlayer.distanceTo(teams.get(thisTeam));
                if (highestDistance == -1 || thisDistance < highestDistance) {
                    nextTeam = thisTeam;
                    highestDistance = thisDistance;
                }
            }
        }
        //Log.d("NextTeamPick","Team " + nextTeam+1 + " will pick next.");
        return nextTeam;
    }

    public int getNextTeam(TeamSet teams, Team playerPool, int lastTeamPick, boolean randomize) {
        if (playerPool.size() == 0) {
            return 0;
        }
        int nextTeam = 0;

        LinkedList<Integer> lowestMembershipTeams = teams.getLowestMembershipTeams();

        // if there's only one, return that.
        if (lowestMembershipTeams.size() == 1) {
            return lowestMembershipTeams.get(0);
        }

        // if we have fewer players than teams, give the pick to the lowest ranking member of lowestMembershipTeams.
        if (playerPool.size() < teams.size()) {
            int lowestRank = -1;
            Iterator<Integer> titr2 = lowestMembershipTeams.iterator();
            while (titr2.hasNext()) {
                int thisTeamNumber = titr2.next();
                Team tt = teams.get(thisTeamNumber);
                if (tt.getSkillsSum() < lowestRank || lowestRank == -1) {
                    nextTeam = thisTeamNumber;
                }
            }
        }

        // otherwise, we can either do it randomly or sequentially.
        else {
            if (randomize) {
                if (lowestMembershipTeams.size() == 1) {
                    return lowestMembershipTeams.get(0);
                }
                Random generator = new Random();
                //Log.d("RandomGenerator","Trying to get a random number between 0 and " + (lowestMembershipTeams.size()-1));
                nextTeam = lowestMembershipTeams.get(generator.nextInt(lowestMembershipTeams.size() - 1));
            } else {
                if (lastTeamPick >= lowestMembershipTeams.size() - 1) {
                    nextTeam = lowestMembershipTeams.get(0);
                } else {
                    nextTeam = lowestMembershipTeams.get(lastTeamPick + 1);
                }
            }
        }
        //Log.d("NextTeamPick","Team " + nextTeam+1 + " will pick next.");
        return nextTeam;
    }

    public void nextStep(TeamSet teams, Team playerPool, Team playerPool2, int originalPlayerPoolSize)
    {

    }

    public Player findPlayerGeneratingLowestOverallDifferential(Team playerPool,
                                                                TeamSet teams, int myTeam) {
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

            // add to my team.
            Team myt = teams.get(myTeam);
            myt.add(tp);
            teams.set(myTeam, myt);

            double thisOne = teams.getLargestDifferenceBetweenTeams();

            if (thisOne < bestDistance || bestDistance == -1) {
                bestDistance = thisOne;
                bestPlayer = tp;
            }

            // remove from my team.
            myt.remove(tp);
            teams.set(myTeam, myt);
        }
        //Log.v("Algorithm", "Best player: " + bestPlayer.getName());
        return bestPlayer;
    }

        /*
            TODO: This needs to be fixed.  It refers to the PlayerPicker, where you can also do things like add splits and pairs.
        */
        private TeamSet sortOutConstraintsFirst(
            TeamSet teams,
            Team playerPool, Player startingAveragePlayer,
            boolean randomize) {

        Iterator<Player> pitr = playerPool.iterator();
        ArrayList<Player> usedPlayers = new ArrayList<Player>();
        while (pitr.hasNext()) {
            Player tp = pitr.next();
            // check if we've already used this player...

            boolean doNotContinue = false;
            for (int i = 0; i < usedPlayers.size(); i++) {
                if (usedPlayers.get(i).getPlayerID() == tp.getPlayerID()) {
                    //Log.d("UsedPlayer","Player " + tp.getName() + " has already been put on a team.");
                    doNotContinue = true;
                    break;
                }
            }
            if (doNotContinue) {
                continue;
            }

            //Log.d("Constraints","Checking to see if " + tp.getName() + " has any constraints.");
//            if (PlayerPicker.getAntiPairsForPlayer(tp.getPlayerID()).length != 0 || PlayerPicker.getPairsForPlayer(tp.getPlayerID()).length != 0) {
//				/* this could be made a separate function.
//				*  so... best would probably be to put each player in the "anti pair" on the team
//				*  such that it would bring that team closer to the other teams.
//				*  also, we need to factor in any baggage that this player might have...
//				*  so actually, this "player" will end up being a team.
//
//				*  so, for example.  Justin /!/ Kyle and Kyle /!/ Justin.
//				* First we get Justin.
//				* We put Justin on a team.
//				* We process Justin's "anti-pair" list and put them on other teams.
//				*/
//                //Log.d("Constraints","Trying to put player " + tp.getName() + " onto a team.");
//                int[] antiPairList = PlayerPicker.getAllAntiPairsForPlayer(tp.getPlayerID());
//
//                // make it a one man team.
//                Team tt = new Team();
//                tt.add(tp);
//
//                if (PlayerPicker.getPairsForPlayer(tp.getPlayerID()).length != 0) {
//                    //Log.d("Pairs","Getting all pairs.");
//                    int[] pp = PlayerPicker.getAllPairsForPlayer(tp.getPlayerID());
//                    //Log.d("Pairs","pairs length: " + pp.length);
//                    // so this player has positive pairings.
//                    for (int k = 0; k < pp.length; k++) {
//                        Player tp3 = VBTP.PlayerDB.getPlayerByID(pp[k]);
//                        tt.add(tp3);
//                        //Log.d("Pair", tp3.getName() + " has to be with " + tp.getName());
//                    }
//                }
//
//                // now figure out which team to add this one to.  It can't be on any of the teams that has
//                // one of the "anti" pair people.
//                int bestTeamIndex = -1;
//                double distanceFromOtherTeams = -1;
//                for (int x = 0; x < teams.size(); x++) {
//                    TeamSet teamsCopy = new TeamSet(teams);
//                    Team thisTeam = teamsCopy.get(x);
//
//                    Iterator<Player> titr2 = thisTeam.iterator();
//                    //Log.d("AntiPairs","thisTeam size: " + thisTeam.size());
//                    boolean cannotGoOn = false;
//                    while (titr2.hasNext()) {
//                        Player tp3 = titr2.next();
//                        //Log.d("AntiPairCheck","Checking to see if " + tp3.getName() + " is in the anti pair list");
//                        for (int l = 0; l < antiPairList.length; l++) {
//                            // this is an anti-pair list FOR player tp2.  That means that
//                            // tp2 will not be in the list.
//                            //if(antiPairList[l] == tp2.getPlayerID()) { continue; } // don't check for myself.
//                            // tp3, a player in "thisTeam," matches one of the other players that tp2 can NOT  be on.
//
//                            if (tp3.getPlayerID() == antiPairList[l]) {
//                                cannotGoOn = true;
//                                break;
//                            }
//                        }
//                    }
//                    if (cannotGoOn) {
//                        //Log.d("AntiPairCheck","Cannot put " + tp.getName() + " on this team.");
//                        continue;
//                    }
//                    Team thisTeamCopy = thisTeam.copy();
//                    thisTeamCopy.addAll(tt);
//                    teamsCopy.set(x, thisTeamCopy);
//
//                    double thisDistance = teamsCopy.getLargestDifferenceBetweenTeams(true);
//                    Log.d("BestTeamPlacement", "With this team, the teams had the largest difference between teams of " + thisDistance + ": " + thisTeamCopy);
//                    if (
//                            (distanceFromOtherTeams == -1 || thisDistance < distanceFromOtherTeams)
//                                    && thisTeamCopy.size() < (playerPool.size() / numberOfTeams)
//                            ) {
//                        distanceFromOtherTeams = thisDistance;
//                        bestTeamIndex = teams.indexOf(thisTeam);
//                    }
//                }
//
//                if (bestTeamIndex == -1) {
////                    toast(getResources().getString(R.string.teamPicker_cannotSolve));
//                    return null;
//                }
//
//                Team thisTeam = teams.get(bestTeamIndex);
//                thisTeam.addAll(tt);
//                usedPlayers.addAll(tt);
//                teams.set(bestTeamIndex, thisTeam);
//            }
        }

        return teams;
    }

    public Player[] getFirstPlayerRickAlgorithm(Team playerPool) {
        //Log.v("RickAlgorithmFirst", "starting getFirstPlayerRickAlgorithm");
        Player[] players;
        //Log.d("PlayerPoolSize",""+playerPool.size());

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

        double largestDistance = -1;
        Iterator<Player> pitr = playerPool.iterator();
        while (pitr.hasNext()) {
            Player p1 = pitr.next();
            //Log.d("RickAlgorithmFirst", "comparing " + p1.getName() + " to everyone else.");
            Iterator<Player> pitr2 = playerPool.iterator();
            while (pitr2.hasNext()) {
                Player p2 = pitr2.next();
                if (p1.getPlayerID() == p2.getPlayerID()) {
                    continue;
                }
                double thisDistance = p1.distanceTo(p2);
                if (thisDistance > largestDistance || largestDistance == -1) {
                    //Log.d("RickAlgorithm", "new best player pair is " + p1.getName() + " and " + p2.getName() + " who are " + thisDistance + " apart.");
                    players[0] = p1;
                    players[1] = p2;
                    largestDistance = thisDistance;
                }
            }
        }
        //Log.v("RickAlgorithm", "FINISHED: best first player pair is " + players[0].getName() + " and " + players[1].getName() + " who are " + largestDistance + " apart.");
        return players;
    }

    // this has problems!
    static void shufflePlayerArray(Player[] ar) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i >= 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Player a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
