//package com.ellsworthcreations.vbtp20;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.res.Resources;
//import android.graphics.Color;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.math.BigInteger;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.ListIterator;
//import java.util.Random;
//
//public class TeamPicker extends Activity {
//
//    public static int numberOfTeams = 2;
//    public int[] selectedPlayerIDs = new int[0];
//    private int PickingAlgorithm = 5;
//    public static int TryAllSelection = 5;
//    public static int ExhaustiveAlgorithm = 6;
//    private boolean UnevenTeams = false;
//    private Preferences sp;
//    public static TeamSet LastTeams;
//    private Resources resources;
//    public static Player averagePlayer;
//    private BigInteger exhaustiveCombinationsChecked = BigInteger.ZERO;
//    private BigInteger ExhaustiveCombinationsPossible = BigInteger.ZERO;
//    private BigInteger LastChecked = BigInteger.ZERO;
//    private PickingProgressDialog pd1;
//    private String pd1Title;
//    private int pd1Progress = 0;
//    private static boolean isMyDialogShowing;
//    private static long secondsTakenByExhaustive = 0;
//    public static boolean StopAlgorithm = false;
//    private static boolean MonteCarloSimulation = false;
//    private static int MonteCarloIterations = 0;
//    private static int MonteCarloPlayers = 0;
//    private static TeamSet BestTeams;
//
//    public void onCreate(Bundle savedInstanceState) {
//        resources = this.getResources();
//        setUpMyDialog();
//        // on an orientation change, the team pick is
//        // canceled, so we don't need to show
//        // the dialog.
//        // if(isMyDialogShowing) { showMyDialog(); }
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.teampicker);
//
//        sp = new Preferences(this.getBaseContext());
//        if (sp.getIntegerPreference("NumberOfTeams") != -1) {
//            ((EditText) this.findViewById(R.id.numberOfTeams)).setText(Integer.toString(sp.getIntegerPreference("NumberOfTeams")));
//        }
//
//        LastTeams = (TeamSet) getLastNonConfigurationInstance();
//        if (LastTeams != null) {
//            setContentView(R.layout.teampicker);
//            updateDisplayWithTeams(LastTeams);
//            Log.d("LastTeams", "Updating display with last known configuration...");
//            Log.d("LastTeams", "Last Teams size: " + LastTeams.size());
//            return;
//        }
//        // show players
//        int[] newSelectedPlayerIDs = PlayerPicker.selectedPlayerIDs;
//
//        if (newSelectedPlayerIDs != null &&
//                !PlayerPicker.selectedPlayerIDListsEqual(selectedPlayerIDs, newSelectedPlayerIDs)) {
//            selectedPlayerIDs = newSelectedPlayerIDs;
//            repickTeams(false);
//        }
//
//        Player[] allPlayers = VBTP.PlayerDB.getAllPlayers();
//        if (allPlayers.length == 0) {
//            // prepopulate for testing purposes.
//            // in order: height, speed, throwing, catching, defense, competitive, experience
//            VBTP.PlayerDB.insertPlayer(new Player(0, "John", "Doe", true,
//                    new Skills(new int[]{4, 5, 4, 5, 4, 4, 3})));
//            VBTP.PlayerDB.insertPlayer(new Player(0, "Jane", "Doe", true,
//                    new Skills(new int[]{3, 4, 3, 3, 3, 2, 4})));
//            VBTP.PlayerDB.insertPlayer(new Player(0, "Bob", "Deller", true,
//                    new Skills(new int[]{3, 5, 4, 4, 4, 3, 4})));
//            VBTP.PlayerDB.insertPlayer(new Player(0, "Larry", "Page", true,
//                    new Skills(new int[]{4, 4, 2, 2, 4, 5, 3})));
//        }
//
//        // TODO: CHANGE IF NEEDED
//        //MonteCarloTest(true);
//
//        // TODO: do I want to repick teams on load or not?
//        repickTeams(false);
//    }
//
//    public void onResume() {
//        int[] newSelectedPlayerIDs = PlayerPicker.selectedPlayerIDs;
//
//        if (newSelectedPlayerIDs != null &&
//                !PlayerPicker.selectedPlayerIDListsEqual(selectedPlayerIDs, newSelectedPlayerIDs)) {
//            selectedPlayerIDs = newSelectedPlayerIDs;
//            repickTeams(false);
//        }
//        super.onResume();
//    }
//
//    public void onPause() {
//        super.onPause();
//        dismissMyDialog();
//    }
//
//    public void onClick(View v) {
//
//    }
//
//    public void finish() {
//        VBTP.PlayerDB.close();
//        super.finish();
//    }
//
//    @Override
//    public TeamSet onRetainNonConfigurationInstance() {
//        return TeamPicker.LastTeams;
//    }
//
//    public void findBetterTeams(View view) {
//        repickTeams(true);
//    }
//
//    public void repickTeams(View view) {
//        repickTeams(false);
//    }
//
//    public LinkedList<Integer> getLowestMembershipTeams(TeamSet teams) {
//        int lowestMembership = -1;
//        LinkedList<Integer> lowestMembershipTeams = new LinkedList<Integer>();
//        Iterator<Team> titr = teams.iterator();
//        while (titr.hasNext()) {
//            Team tt = titr.next();
//            if (tt.size() < lowestMembership || lowestMembership == -1) {
//                lowestMembership = tt.size();
//            }
//        }
//
//        // now we have the lowest membership.  Now figure out who is in the running for next pick.
//        titr = teams.iterator();
//        while (titr.hasNext()) {
//            Team tt = titr.next();
//            if (tt.size() == lowestMembership) {
//                //Log.d("LowestMembershipCalculation", "Team " + (teams.indexOf(tt)+1) + " is part of the Lowest Membership club...");
//                lowestMembershipTeams.add(teams.indexOf(tt));
//            }
//        }
//        return lowestMembershipTeams;
//    }
//
//    public int getNextTeam_FurthestFromGrandAverage(TeamSet teams, Team playerPool,
//                                                    int lastTeamPick, Player grandAveragePlayer, boolean randomize) {
//        int nextTeam = 0;
//        LinkedList<Integer> lowestMembershipTeams = getLowestMembershipTeams(teams);
//
//        // if there's only one, return that.
//        if (lowestMembershipTeams.size() == 1) {
//            return lowestMembershipTeams.get(0);
//        }
//
//        // if we have fewer players than teams, give the pick to the lowest ranking member of lowestMembershipTeams.
//        if (playerPool.size() < teams.size()) {
//            int lowestRank = -1;
//            Iterator<Integer> titr2 = lowestMembershipTeams.iterator();
//            while (titr2.hasNext()) {
//                int thisTeamNumber = titr2.next();
//                Team tt = teams.get(thisTeamNumber);
//                if (tt.getSkillsSum() < lowestRank || lowestRank == -1) {
//                    nextTeam = thisTeamNumber;
//                }
//            }
//        }
//
//        // otherwise, do it based on who is farthest away from the average ... amongst the "lowest membership" teams.
//        else {
//            double highestDistance = -1;
//            Iterator<Integer> titr = lowestMembershipTeams.iterator();
//            while (titr.hasNext()) {
//                int thisTeam = titr.next();
//                double thisDistance = grandAveragePlayer.distanceTo(teams.get(thisTeam));
//                if (highestDistance == -1 || thisDistance < highestDistance) {
//                    nextTeam = thisTeam;
//                    highestDistance = thisDistance;
//                }
//            }
//        }
//        //Log.d("NextTeamPick","Team " + nextTeam+1 + " will pick next.");
//        return nextTeam;
//    }
//
//    public int getNextTeam(TeamSet teams, Team playerPool, int lastTeamPick, boolean randomize) {
//        if (playerPool.size() == 0) {
//            return 0;
//        }
//        int nextTeam = 0;
//
//        LinkedList<Integer> lowestMembershipTeams = getLowestMembershipTeams(teams);
//
//        // if there's only one, return that.
//        if (lowestMembershipTeams.size() == 1) {
//            return lowestMembershipTeams.get(0);
//        }
//
//        // if we have fewer players than teams, give the pick to the lowest ranking member of lowestMembershipTeams.
//        if (playerPool.size() < teams.size()) {
//            int lowestRank = -1;
//            Iterator<Integer> titr2 = lowestMembershipTeams.iterator();
//            while (titr2.hasNext()) {
//                int thisTeamNumber = titr2.next();
//                Team tt = teams.get(thisTeamNumber);
//                if (tt.getSkillsSum() < lowestRank || lowestRank == -1) {
//                    nextTeam = thisTeamNumber;
//                }
//            }
//        }
//
//        // otherwise, we can either do it randomly or sequentially.
//        else {
//            if (randomize) {
//                if (lowestMembershipTeams.size() == 1) {
//                    return lowestMembershipTeams.get(0);
//                }
//                Random generator = new Random();
//                //Log.d("RandomGenerator","Trying to get a random number between 0 and " + (lowestMembershipTeams.size()-1));
//                nextTeam = lowestMembershipTeams.get(generator.nextInt(lowestMembershipTeams.size() - 1));
//            } else {
//                if (lastTeamPick >= lowestMembershipTeams.size() - 1) {
//                    nextTeam = lowestMembershipTeams.get(0);
//                } else {
//                    nextTeam = lowestMembershipTeams.get(lastTeamPick + 1);
//                }
//            }
//        }
//        //Log.d("NextTeamPick","Team " + nextTeam+1 + " will pick next.");
//        return nextTeam;
//    }
//
//    //public TeamSet getTeamsUsingAlgorithm(int algorithm, boolean reutrn results)
//    class getTeamsUsingAlgorithmTask extends AsyncTask<Integer, Integer, TeamSet> {
//
//        private Context context;
//        private boolean randomize;
//        private boolean findBetter;
//        private int numberOfPlayers;
//        private int currentAlgorithm = 0;
//
//        public getTeamsUsingAlgorithmTask(Context c, boolean randomizePassed, boolean findBetterPassed) {
//            this.context = c;
//            this.randomize = randomizePassed;
//            this.currentAlgorithm = PickingAlgorithm;
//            this.findBetter = findBetterPassed;
//            String algorithmName = resources.getStringArray(R.array.algorithmChoices)[PickingAlgorithm];
//            if (PickingAlgorithm == TeamPicker.ExhaustiveAlgorithm) {
//                algorithmName = "Exhaustive Algorithm";
//            }
//            pd1Title = algorithmName;
//
//            if (findBetter) {
//                randomize = true;
//            }
//        }
//
//        protected void onPreExecute() {
//            super.onPreExecute();
//            showMyDialog(this);
//        }
//
//        protected TeamSet doInBackground(Integer[] params) {
//            /* algorithm to use */
//            int algorithm = params[0];
//
//			/* the resulting set of teams from a run of the algorithm(s) */
//            TeamSet BestTeams = new TeamSet();
//
//			/*
//			 * number of times to loop through the picking, comparing the resulting team to the
//			 * thus-far best team selection.  There's an option in preferences to do 10 at a time.
//			 */
//            int retryNumber = 1;
//
//			/*
//			 * if this is true, then we should replace the current teams (LastTeams) only if
//			 * we find one that is better.
//			 */
//
//            if (sp.getBooleanPreference("AlgorithmOptions_Retry10AtATime")) {
//                retryNumber = 10;
//                randomize = true;
//            }
//
//            for (int q = 0; q < retryNumber; q++) {
//                TeamSet teams = new TeamSet();
//                if (PickingAlgorithm == TeamPicker.TryAllSelection) {
//                    int bestAlgorithm = 0;
//                    TeamSet teams2 = new TeamSet();
//                    double teamsDistance = -1;
//                    String results = Integer.toString(numberOfTeams) + '\t' + TeamPicker.MonteCarloPlayers;
//
//                    // why do I have to make it to +1?? I have no idea.
//                    int whereToStop = PickingAlgorithm + 1;
//                    if (VBTP.AllowExhaustive && TeamPicker.MonteCarloSimulation) {
//                        whereToStop = TeamPicker.ExhaustiveAlgorithm + 1;
//                    }
//
//                    //Log.d("MonteCarloDetails","Going to " + whereToStop);
//                    for (int i = 0; i < whereToStop; i++) {
//                        //Log.d("MonteCarloDetails","i: " + i);
//                        if (TeamPicker.StopAlgorithm == true) {
//                            return null;
//                        }
//                        if (i == TeamPicker.TryAllSelection) {
//                            continue;
//                        }
//
//                        // make sure we don't kill ourselves doing an exhaustive one.
//                        if (TeamPicker.MonteCarloSimulation && i == TeamPicker.ExhaustiveAlgorithm) {
//                            if (TeamPicker.MonteCarloPlayers > 16) {
//                                Log.d("MonteCarloDetails", "SKIP: Too many players to do exhaustive");
//                            }
//                        }
//
//                        currentAlgorithm = i;
//                        String algorithmName = resources.getStringArray(R.array.algorithmChoices)[i];
//                        Log.d("Algorithm", "STARTING ALGORITHM: " + algorithmName);
//                        // try all of them.
//                        //long start = System.currentTimeMillis();
//                        teams2 = getTeamsUsingAlgorithm(i, randomize);
//
//						/*long end = System.currentTimeMillis();
//						double s = (double) ((end-start) / 1000) % 60;
//						double m = (double) ((end-start) / (1000*60)) % 60;
//						double h = (double) ((end-start) / (1000*60*60)) % 60;
//						String timeTaken = h + " hours, " + m + " minutes, " + s + " seconds";*/
//
//                        Log.d("Algorithm", "ALGORITHM FINISHED: " + algorithmName);
//                        if (teams2 == null) {
//                            continue;
//                        }
//                        Log.d(algorithmName, " ... returned with a team size of " + teams2.size());
//                        double teams2Distance = teams2.getLargestDifferenceBetweenTeams();
//                        Log.d("BestAlgorithm", "Largest distance between any two teams using \"" + algorithmName + "\": " + teams2Distance);
//                        results += '\t';
//                        results += Double.toString(teams2Distance);
//
//                        //if(TeamPicker.MonteCarloSimulation)
//                        //{ Log.d("MonteCarlo","Algorithm: " + algorithmName + "\nGreatest distance: " + teams2Distance); }
//                        if (teamsDistance == -1 || teams2Distance < teamsDistance) {
//                            teams = teams2;
//                            teamsDistance = teams2Distance;
//                            bestAlgorithm = i;
//                            Log.d("BestAlgorithm", "Best algorithm is now \"" + algorithmName + "\"");
//                        }
//                        if (retryNumber == 1) {
//                            publishProgress((100 / (PickingAlgorithm)) * (i + 1));
//                        } else {
//                            publishProgress((int) Math.ceil((double) (100 / ((retryNumber) * PickingAlgorithm))) * ((q * PickingAlgorithm + i)));
//                        }
//                    }
//                    if (TeamPicker.MonteCarloSimulation) {
//                        Log.d("MonteCarloTabSeparated", results);
//                        //Log.d("MonteCarlo","Time: " + timeTaken);
//                    }
//                    Log.i("BestAlgorithm", "The best algorithm ended up being -- " + getResources().getStringArray(R.array.algorithmChoices)[bestAlgorithm] + " -- ");
//                    //if(TeamPicker.MonteCarloSimulation)
//                    //{
//                    //	Log.d("MonteCarlo","Best: " + getResources().getStringArray(R.array.algorithmChoices)[bestAlgorithm]);
//                    //}
//                } else {
//                    Log.d("Algorithm", "" + algorithm);
//                    teams = getTeamsUsingAlgorithm(algorithm, randomize);
//                }
//
//                if (teams != null) {
//                    for (int i = 0; i < teams.size(); i++) {
//                        numberOfPlayers += teams.get(i).size();
//                    }
//                }
//
//                // so now we have teams.  is it the best? we don't know.
//                if (BestTeams.size() == 0) {
//                    BestTeams = teams;
//                } else {
//                    // comparison needed.
//                    // current best teams has a largest difference?
//                    if (BestTeams.getLargestDifferenceBetweenTeams(false) > teams.getLargestDifferenceBetweenTeams(false)) {
//                        BestTeams = teams;
//                    }
//                }
//            }
//
//            // was used to find a bug...
//            Log.d("BestTeams", "Best teams size: " + BestTeams.size());
//            if (TeamPicker.LastTeams != null) {
//                Log.d("BestTeams", "Best teams largest differential: " + BestTeams.getLargestDifferenceBetweenTeams() + " ... LastTeams largest diff: " + TeamPicker.LastTeams.getLargestDifferenceBetweenTeams());
//            }
//            if (!findBetter || BestTeams.getLargestDifferenceBetweenTeams(false) < TeamPicker.LastTeams.getLargestDifferenceBetweenTeams(false)) {
//                // this is a better team set.
//				/* teams last picked ... to prevent a double-pick on orientation change. */
//                TeamPicker.LastTeams = BestTeams;
//                return BestTeams;
//            } else {
//                // this is not a better team set... return null? ... nah, return an empty one.
//                Log.d("BackgroundRunner", "Returning null...");
//                return new TeamSet();
//            }
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer[] progress) {
//            String algorithmName = resources.getStringArray(R.array.algorithmChoices)[currentAlgorithm];
//            if (getDialogTitle() != algorithmName) {
//                setDialogTitle(algorithmName);
//            }
//            if (currentAlgorithm == TeamPicker.ExhaustiveAlgorithm) {
//                setProgressMessage(String.format(context.getResources().getString(R.string.exhaustive_progressUpdate), exhaustiveCombinationsChecked, ExhaustiveCombinationsPossible));
//            }
//            setProgressPercent(progress[0]);
//        }
//
//        protected void onPostExecute(TeamSet teams) {
//            dismissMyDialog();
//
//            if (teams == null) {
//                slowToast("Eror picking teams; perhaps too few players for number of teams requested.");
//            }
//            if (PickingAlgorithm == TeamPicker.ExhaustiveAlgorithm) {
//                double s = (double) (TeamPicker.secondsTakenByExhaustive / 1000) % 60;
//                double m = (double) (TeamPicker.secondsTakenByExhaustive / (1000 * 60)) % 60;
//                double h = (double) (TeamPicker.secondsTakenByExhaustive / (1000 * 60 * 60)) % 60;
//                String timeTaken = h + " hours, " + m + " minutes, " + s + " seconds";
//                new AlertDialog.Builder(context)
//                        .setTitle(context.getResources().getString(R.string.exhaustive_timeTakenTitle))
//                        .setMessage(String.format(context.getResources().getString(R.string.exhaustive_timeTaken), timeTaken, numberOfPlayers, numberOfTeams))
//                        .setNegativeButton(R.string.changelog_ok_button, null)
//                        .show();
//            }
//            Log.d("Async", "Done!");
//
//            VBTPActivity.allowOrientationChange = true;
//            TeamPicker.StopAlgorithm = false;
//            if (!TeamPicker.MonteCarloSimulation) {
//                if (teams.size() == 0 && findBetter) {
//                    slowToast("Not updating display; teams found were not more fair than existing teams.");
//                } else {
//                    updateDisplayWithTeams(teams);
//                }
//
//                ((Button) findViewById(R.id.repickButton)).setEnabled(true);
//                ((Button) findViewById(R.id.findBetterButton)).setEnabled(true);
//            } else {
//                TeamPicker.MonteCarloIterations++;
//
//                // log a bunch of stuff I guess.
//
//                MonteCarloTest(false);
//            }
//
//        }
//
//        @Override
//        public void onCancelled() {
//            super.onCancelled();
//            TeamPicker.StopAlgorithm = true;
//
//            if (!TeamPicker.MonteCarloSimulation) {
//                ((Button) findViewById(R.id.repickButton)).setEnabled(true);
//                ((Button) findViewById(R.id.findBetterButton)).setEnabled(true);
//            }
//
//            VBTPActivity.allowOrientationChange = true;
//            if (this.cancel(true)) {
//                Log.d("Task", "Canceled!");
//            } else {
//                Log.d("Task", "Could not cancel task.  Oh Java.  Sigh.");
//            }
//            toast(getResources().getString(R.string.teamPicker_canceledAlgorithm));
//        }
//
//        public TeamSet getTeamsUsingAlgorithm(int algorithm, boolean randomize) {
//            // this returns a completely new list of teams using the given algorithm number.
//            Player[] allPlayers = VBTP.PlayerDB.getActivePlayersSortedByName();
//
//            if (allPlayers.length < numberOfTeams) {
//                // heh.
//                return null;
//            }
//            // these require at least 2 per team to work.
//            else if ((algorithm == 1 || algorithm == 2 || algorithm == 3)
//                    && allPlayers.length < 2 * numberOfTeams) {
//                return null;
//            }
//
//            if (algorithm == TeamPicker.ExhaustiveAlgorithm) {
//                randomize = false;
//            }
//
//            // so I'm going to need a list of teams.  Each team will be an linked list of players.
//            TeamSet teams = new TeamSet();
//
//            // I also need a pool of players.
//            Team playerPool = new Team();
//
//            // this one is used for randomizing.
//            Team playerPool2 = new Team();
//
//            // used for making equal male/female counts on teams
//
//            Team firstPlayers = new Team();        // this will get a list of players picked first.
//            Team otherPlayers = new Team();        // players picked second.
//            Team malePlayers = new Team();        // male players
//            Team femalePlayers = new Team();    // female players.
//
//            // my random generator. yay.
//            Random generator = new Random();
//
//            // this doens't actually have to be random, since we shuffle the array every time.
//            // we will remove 25% of the players.
//            int randomRemovalInterval = 4;
//
//            boolean forceEqualGenders = sp.getBooleanPreference("AlgorithmOptions_Gender");
//
//            // sort allPlayers ... randomly.
//            if (randomize) {
//                TeamPicker.shufflePlayerArray(allPlayers);
//            }
//
//            for (int i = 0; i < allPlayers.length; i++) {
//                boolean isSelected = false;
//                if (selectedPlayerIDs == null || selectedPlayerIDs.length == 0) {
//                    isSelected = true;
//                } else {
//                    for (int j = 0; j < selectedPlayerIDs.length; j++) {
//                        if (selectedPlayerIDs[j] == allPlayers[i].getPlayerID()) {
//                            isSelected = true;
//                            break;
//                        }
//
//                    }
//                }
//                if (isSelected) {
//                    // we don't want to split up any pairs or anti-pairs... so
//                    // random functionality stuff won't include those.
//                    if (
//                            !randomize
//                                    || forceEqualGenders
//                                    || i % randomRemovalInterval != 0
//                                    || PlayerPicker.getAllAntiPairsForPlayer(allPlayers[i].getPlayerID()).length != 0
//                                    || PlayerPicker.getAllPairsForPlayer(allPlayers[i].getPlayerID()).length != 0) {
//                        playerPool.add(allPlayers[i]);
//                    }
//
//                    // take out a certain percentage of players.
//                    else {
//                        playerPool2.add(allPlayers[i]);
//                    }
//                }
//            }
//            //Log.d("PlayerPoolSize","Player Pool Size at beginning: " + playerPool.size());
//            //Log.d("PlayerPoolSize","AllPlayers array length at beginning: " + allPlayers.length);
//            // check to see if we have uneven teams.
//            if ((playerPool.size() + playerPool2.size()) % numberOfTeams != 0) {
//                UnevenTeams = true;
//            } else {
//                UnevenTeams = false;
//            }
//
//            for (int i = 0; i < numberOfTeams; i++) {
//                teams.add(new Team());
//            }
//
//            // team to start with
//            int teamToStartWith = 0;
//
//            int gotFirstPlayer = 0;
//            int currentTeam = teamToStartWith;
//
//            Player startingAveragePlayer = playerPool.getAveragePlayer();
//            averagePlayer = startingAveragePlayer;
//
//            if (algorithm == TeamPicker.ExhaustiveAlgorithm) {
//                if (PickingAlgorithm == TeamPicker.ExhaustiveAlgorithm) {
//                    sp.savePreference("PickingAlgorithm", TeamPicker.TryAllSelection);
//                }
//                return getBestCombinationExhaustive(playerPool, numberOfTeams, startingAveragePlayer);
//            }
//
//            int originalPlayerPool = playerPool.size();
//            //Log.d("RepickTeams","randomize: " + randomize);
//
//            // CONSTRAINTS ... SORT ALL CONSTRAINTS FIRST
//            if (PlayerPicker.constraintsExist() && sp.getBooleanPreference("ShowPairOptions")) {
//                teams = sortOutConstraintsFirst(teams, playerPool, startingAveragePlayer, randomize);
//                if (teams == null) {
//                    return null;
//                }
//                Iterator<Team> teamsItr = teams.iterator();
//                while (teamsItr.hasNext()) {
//                    Team thisTeam = teamsItr.next();
//                    Iterator<Player> teamItr = thisTeam.iterator();
//                    while (teamItr.hasNext()) {
//                        Player tp = teamItr.next();
//                        Iterator<Player> pitr = playerPool.iterator();
//                        while (pitr.hasNext()) {
//                            Player tp2 = pitr.next();
//                            if (tp.getPlayerID() == tp2.getPlayerID()) {
//                                playerPool.remove(tp2);
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//
//            // now that constraints are sorted out, sort lists of genders if necessary.
//            // pick a certain gender first, possibly.
//            boolean startedPickingOtherGender = false;
//            boolean oneFirstPickAtATime = false;
//            Player.Gender InitialGenderPicks = Player.Gender.FEMALE;
//            if (forceEqualGenders) {
//                // divide into lists.
//                Iterator<Player> pitr = playerPool.iterator();
//                while (pitr.hasNext()) {
//                    Player tp = pitr.next();
//                    if (tp.isMale()) {
//                        malePlayers.add(tp);
//                    } else {
//                        femalePlayers.add(tp);
//                    }
//                }
//
//                // so first of all, ARE there both genders...
//                if (femalePlayers.size() == 0 || malePlayers.size() == 0) {
//                    // don't have to even it :)
//                    // playerPool already has a bunch anyway...
//                    forceEqualGenders = false;
//                } else {
//                    // for now, pick female players first...
//                    InitialGenderPicks = Player.Gender.FEMALE;
//                    otherPlayers = malePlayers;
//
//                    // start with female players.
//                    // first, though, we need to only pick an even number of them...
//                    if (femalePlayers.size() < numberOfTeams || femalePlayers.size() == numberOfTeams) {
//                        firstPlayers = femalePlayers;
//                    } else {
//                        Log.d("EqualGenders", "Before removing: " + femalePlayers);
//                        while (femalePlayers.size() >= numberOfTeams) {
//                            for (int i = 0; i < numberOfTeams; i++) {
//                                firstPlayers.add(femalePlayers.removeAverage());
//                            }
//                        }
//                        Log.d("EqualGenders", "After removing: " + femalePlayers);
//                        otherPlayers.addAll(femalePlayers);
//                        femalePlayers.clear();
//                    }
//
//                    playerPool = firstPlayers;
//                    if (playerPool.size() < (numberOfTeams * 2)) {
//                        oneFirstPickAtATime = true;
//                    }
//
//                    // this is how we randomize if we're forcing equal genders.
//                    if (randomize) {
//                        for (int i = 0; i < otherPlayers.size(); i++) {
//                            if (i % randomRemovalInterval != 0) {
//                                continue;
//                            }
//
//                            // take out a certain percentage of players.
//                            else {
//                                playerPool2.add(otherPlayers.get(i));
//                                otherPlayers.remove(otherPlayers.get(i));
//                            }
//                        }
//                    }
//                }
//                Log.d("EqualGenders", "Finished making the two lists.\nFirst picks: " + playerPool + "\nSecond picks: " + otherPlayers);
//                Log.d("EqualGenders", "Setting playerPool to female: " + playerPool);
//            }
//
//            while (playerPool.size() > 0 || playerPool2.size() > 0) {
//                if (playerPool.size() <= 2) {
//                    // better add the other players in!
//                    Log.d("RepickTeams", "putting " + playerPool2.size() + " players back in because playerPool is " + playerPool.size());
//                    while (playerPool2.size() > 0) {
//                        playerPool.add(playerPool2.remove());
//                    }
//                }
//
//                if (algorithm == 3) {
//                    currentTeam = getNextTeam_FurthestFromGrandAverage(teams, playerPool, currentTeam, startingAveragePlayer, randomize);
//                } else {
//                    currentTeam = getNextTeam(teams, playerPool, currentTeam, randomize);
//                }
//
//                if (((forceEqualGenders && startedPickingOtherGender || !forceEqualGenders)) && randomize && playerPool2.size() > 0) {
//                    if (originalPlayerPool - playerPool.size() > ((originalPlayerPool / randomRemovalInterval))) {
//                        Log.d("RepickTeams", "putting " + playerPool2.size() + " players back in because " + originalPlayerPool + " - " + playerPool.size() + " > " + ((originalPlayerPool / randomRemovalInterval)));
//                        while (playerPool2.size() > 0) {
//                            playerPool.add(playerPool2.remove());
//                        }
//                    }
//                }
//
//                Team thisTeam = teams.get(currentTeam);
//                if (thisTeam.size() == 0 || (forceEqualGenders && !thisTeam.genderExistsOnTeam(InitialGenderPicks) && !startedPickingOtherGender)) {
//                    if (algorithm == 0) {
//                        if (randomize) {
//                            thisTeam.add(playerPool.remove(generator.nextInt(playerPool.size())));
//                        } else {
//                            thisTeam.add(playerPool.removeBest());
//                        }
//                        gotFirstPlayer = 1;
//                    } else if (algorithm == 1 || algorithm == 2) {
//                        // picks pairs for each team, so need to use this initial algorithm each time.
//                        Player[] tps;
//                        if (!PlayerPicker.constraintsExist()) {
//                            // do it normally.
//                            tps = getFirstPlayerRickAlgorithm(playerPool);
//                            if (oneFirstPickAtATime) {
//                                tps = new Player[]{tps[0]};
//                            }
//                        } else {
//                            // otherwise, we we probably want to try to get
//                            // two players that will get us closest
//                            // to the other team(s)
//                            Player tp = findPlayerGeneratingLowestOverallDifferential(playerPool, teams, currentTeam);
//                            tps = new Player[]{tp};
//                        }
//                        // need to remove the player from the pool somehow though
//                        for (int i = 0; i < tps.length; i++) {
//                            thisTeam.add(tps[i]);
//                            playerPool.remove(tps[i]);
//                            Log.d("Algorith", "Added " + tps[i] + " to team.");
//                        }
//                        gotFirstPlayer += 1;
//                        //Log.d("Algorithm"+algorithm, "Finished picking first pair for team " + currentTeam + "\nAdded: ");
//                    } else if (algorithm == 3) {
//                        Player[] tps = getFirstPlayerGrandAverageAlgorithm(playerPool, startingAveragePlayer);
//                        if (oneFirstPickAtATime) {
//                            tps = new Player[]{tps[0]};
//                        }
//                        // need to remove the player from the pool somehow though
//                        for (int i = 0; i < tps.length; i++) {
//                            thisTeam.add(tps[i]);
//                            playerPool.removePlayer(tps[i]);
//                        }
//                        gotFirstPlayer += 1;
//                    } else if (algorithm == 4) {
//                        Player tp = playerPool.getRandomPlayer();
//                        thisTeam.add(tp);
//                        playerPool.remove(tp);
//                        gotFirstPlayer += 1;
//                    } else if (algorithm == 5) {
//                        // hybrid.  So, to start with, let's pick players using the "most different" one.
//                        Player[] tps = getFirstPlayerRickAlgorithm(playerPool);
//                        if (oneFirstPickAtATime) {
//                            tps = new Player[]{tps[0]};
//                        }
//                        for (int i = 0; i < tps.length; i++) {
//                            thisTeam.add(tps[i]);
//                            playerPool.remove(tps[i]);
//                        }
//                        gotFirstPlayer += 1;
//                    }
//
//                } else {
//                    Player thisPlayer = new Player();
//                    if (algorithm == 0) {
//                        //Log.d("Algorithm", "Using getBestPlayer");
//                        // get each team's skills and generate averages.
//                        Skills teamSkillAverages = getTeamsSkillAverages(teams, currentTeam);
//                        Skills myTeamSkills = thisTeam.getCumulativeSkills();
//
//                        // okies.  now we have to get what we are deficient in.
//                        HashMap<String, Double> myWeights = getDeficiencyWeights(teamSkillAverages, myTeamSkills);
//                        thisPlayer = playerPool.getBestPlayerWeighted(myWeights);
//                        thisTeam.add(thisPlayer);
//                        playerPool.remove(thisPlayer);
//                        teams.set(currentTeam, thisTeam);
//                    } else if (algorithm == 1) {
//                        //Log.d("Algorithm", "Using findPlayerGeneratingLowestOverallDifferential");
//                        thisPlayer = findPlayerGeneratingLowestOverallDifferential(playerPool, teams, currentTeam);
//                        thisTeam.add(thisPlayer);
//                        playerPool.remove(thisPlayer);
//                        teams.set(currentTeam, thisTeam);
//                    } else if (algorithm == 2) {
//                        //Log.d("Algorithm", "Using findPlayerRickAlgorithm");
//                        thisPlayer = findPlayerRickAlgorithm(playerPool, thisTeam);
//                        thisTeam.add(thisPlayer);
//                        playerPool.remove(thisPlayer);
//                        Log.d("Algorithm", "findPlayerRickAlgorithm grabbed: " + thisPlayer);
//                        teams.set(currentTeam, thisTeam);
//                    } else if (algorithm == 3) {
//                        // FIRST PICK: find the person furthest from grand average.  Then choose that person along with the person who
//                        // would bring their average to the grand average the best.
//
//                        //Log.d("Algorithm", "Using findPlayerBringingTeamTowardsAverage");
//                        thisPlayer = findPlayerBringingTeamTowardsAverage(playerPool, teams, currentTeam, startingAveragePlayer);
//                        thisTeam.add(thisPlayer);
//                        playerPool.remove(thisPlayer);
//                        teams.set(currentTeam, thisTeam);
//                    } else if (algorithm == 4) {
//                        Player tp = playerPool.getRandomPlayer();
//                        thisTeam.add(tp);
//                        playerPool.remove(tp);
//                        gotFirstPlayer += 1;
//                    } else if (algorithm == 5) {
//                        if (playerPool.size() == 0) {
//                            return teams;
//                        }
//						/*else if(playerPool.size() < 10)
//						{
//							TeamSet otherTeams = new TeamSet();
//							if(playerPool.size() <= numberOfTeams)
//							{
//								for(int i=0; i < playerPool.size(); i++)
//								{ otherTeams.add(new Team(playerPool.remove())); }
//							}*/
//                        else {
//                            thisPlayer = findPlayerGeneratingLowestOverallDifferential(playerPool, teams, currentTeam);
//                            thisTeam.add(thisPlayer);
//                            playerPool.remove(thisPlayer);
//                            teams.set(currentTeam, thisTeam);
//                            // finish off with exhaustive.
//                            //Player newAveragePlayer = playerPool.getAveragePlayer();
//                            //otherTeams = getBestCombinationExhaustive(playerPool, numberOfTeams, newAveragePlayer);
//                        }
//							/*
//							// soooo now we have to merge the teams.  Maybe try to get back to the starting average player?
//							for(int i=0; i < teams.size(); i++)
//							{
//								// for now just stick them on the team. we need to improve this though so it puts it on
//								// the team that would bring it closer to the average.
//								Team myteam = teams.get(i);
//								double differential = -1;
//								int bestTeam = -1;
//								for(int j=0; j < otherTeams.size(); j++)
//								{
//									TeamSet teamsCopy = teams;
//									Team myTeamCopy = myteam.copy();
//									myTeamCopy.addAll(otherTeams.get(j));
//									teamsCopy.set(i, myTeamCopy);
//									double t = getLargestDifferenceBetweenTeams(teamsCopy);
//									if( differential == -1 || t < differential)
//									{ differential = t; bestTeam = j; }
//								}
//								if(bestTeam != -1)
//								{
//									myteam.addAll(otherTeams.get(bestTeam));
//									otherTeams.remove(bestTeam);
//								}
//								teams.set(i, myteam);
//							}
//							// return...
//							return teams;
//						}
//						else
//						{
//							thisPlayer = findPlayerRickAlgorithm(playerPool, thisTeam);
//							thisTeam.add(thisPlayer); playerPool.remove(thisPlayer);
//							teams.set(currentTeam, thisTeam);
//						}*/
//                    }
//                }
//
//                // for forcing equal male/female player teams...
//                if (playerPool.size() == 0 && forceEqualGenders && !startedPickingOtherGender) {
//                    playerPool = otherPlayers;
//                    startedPickingOtherGender = true;
//                    Log.d("EqualGenders", "Setting playerPool to other players: " + otherPlayers);
//                }
//            }
//
//            return teams;
//        }
//
//        public TeamSet getBestCombinationExhaustiveR(Team playerPool, TeamSet teams, int numberOfTeams, Player grandAveragePlayer, int perTeam, double bestTeamCombination_skillDifferential, int callingIteration) {
//            if (TeamPicker.StopAlgorithm == true) {
//                return null;
//            }
//            int myTeamSlot = new Integer(callingIteration);
//            int myTeamNumber = myTeamSlot + 1;
//            int myPerTeam = perTeam;
//
//            // if it's not even, then take an extra player...
//            // but keep subsequent calls the same.
//            // so, for example, if we have 4 teams and 11 players, we have
//            // technically "2" per team (I think integer division is a floor...)
//            // so that means that the last team would have 5 players.
//            // this way, each team along the way takes an extra person
//            // leaving the last team with 2 players and all others with 3.
//            // well, that's the hope. :P
//            // loop 1: 11 % (5-1) = 3
//            // 	2 per team + 1: 3 teams and 8 players left
//            // loop 2: 8 % (5-2) = 2
//            //	2 per team + 1: 2 teams and 5 players left
//            // loop 3: 5 % (5-3) = 1
//            //	2 per team + 1: 1 team and 2 players left
//            if (myTeamNumber != numberOfTeams && playerPool.size() % ((numberOfTeams + 1) - myTeamNumber) != 0) {
//                myPerTeam++;
//            }
//
//            double newBestTeamCombination_skillDifferential = new Double(bestTeamCombination_skillDifferential);
//
//            int myCallingIteration = new Integer(callingIteration) + 1;
//            //Log.d("Exhaustive","my calling iteration: " + myCallingIteration);
//
//            TeamSet newTeams = new TeamSet(teams);
//            Team nPlayerPool = new Team(playerPool);
//
//            double ethreshold = (double) sp.getFloatPreference("ExhaustiveThreshold");
//            if (ethreshold == -1) {
//                ethreshold = 1;
//            }
//
//            if (playerPool.size() % numberOfTeams != 0 && playerPool.size() / numberOfTeams < 3) {
//                ethreshold += 1;
//            }
//
//            if (playerPool.size() / numberOfTeams == 2) {
//                ethreshold += 5;
//            }
//
//            boolean last = false;
//            if (myTeamSlot >= numberOfTeams - 1) {
//                last = true;
//            }
//
//            if (newTeams.size() < numberOfTeams) {
//                newTeams.add(new Team());
//            }
//
//            if (exhaustiveCombinationsChecked.compareTo(LastChecked.add(new BigInteger("500"))) == 1) {
//                LastChecked = exhaustiveCombinationsChecked;
//                // percentage done...
//                float done = exhaustiveCombinationsChecked.floatValue();
//                float todo = ExhaustiveCombinationsPossible.floatValue();
//                float pct = (done / todo) * 100;
//                publishProgress(Math.round(pct));
//                Log.d("Exhaustive", pct + "% done.  Combinations checked: " + exhaustiveCombinationsChecked + "/" + ExhaustiveCombinationsPossible);
//            }
//
//            if (last) {
//                exhaustiveCombinationsChecked = exhaustiveCombinationsChecked.add(BigInteger.ONE);
//                // I'm the last one, there's nothing to loop through... just
//                // put all of the players on my team and return it.
//                // the previous loop will take care of whether or not
//                // it should use the returned one.
//                newTeams.set(myTeamSlot, new Team(nPlayerPool));
//                //Log.d("Exhaustive","Last team to create (team " + (myTeamNumber) + ").  Teams differential: " + getLargestDifferenceBetweenTeams(teams) + ". size: " + teams.size());
//                int largestSize = 0;
//                int lowestSize = -1;
//                int place = 0;
//                for (int i = 0; i < newTeams.size(); i++) {
//                    if (newTeams.get(i).size() > largestSize) {
//                        largestSize = newTeams.get(i).size();
//                        place = i;
//                    }
//                    if (lowestSize == -1 || newTeams.get(i).size() < lowestSize) {
//                        lowestSize = newTeams.get(i).size();
//                    }
//                }
//                if (largestSize - lowestSize > 1) {
//                    Log.d("Exhaustive", "Oddly large team " + (place + 1) + ": " + newTeams.get(place));
//                }
//                return newTeams;
//            } else {
//                //Log.d("Exhaustive","playerPool size for team " + (myTeamNumber) + ": " + playerPool.size());
//                CombinationGenerator c = new CombinationGenerator(nPlayerPool.size(), myPerTeam);
//                //Log.d("Exhaustive",c.getTotal() + " combinations for team " + (myTeamNumber) + "...");
//
//                while (c.hasMore()) {
//                    // make the team.
//                    Team newTeam = new Team();
//                    //myTeam.clear();
//                    Team playerPool2 = new Team(nPlayerPool);
//                    int[] indices = c.getNext();
//
//                    for (int i = 0; i < indices.length; i++) {
//                        newTeam.add(nPlayerPool.get(indices[i]));
//                    }
//                    playerPool2.removeAll(newTeam);
//
//                    //Log.d("Exhaustive","Team " + myTeamNumber + ": " + newTeam);
//
//                    // check if this team is too far from an average player
//                    double distance = grandAveragePlayer.getSkills().distanceTo(newTeam);
//
//                    // disable for monte carlo, for now...
//                    if (!TeamPicker.MonteCarloSimulation && distance > ethreshold)
//                    //if( distance > ethreshold )
//                    {
//                        BigInteger totalCombinations = BigInteger.ONE;
//                        int thisSize = playerPool2.size();
//                        for (int i = myTeamNumber; i < numberOfTeams - 1; i++) {
//                            CombinationGenerator x = new CombinationGenerator(thisSize, myPerTeam);
//                            totalCombinations = totalCombinations.multiply(x.getTotal());
//                            thisSize -= myPerTeam;
//                        }
//                        exhaustiveCombinationsChecked = exhaustiveCombinationsChecked.add(totalCombinations);
//                        if (totalCombinations.compareTo(new BigInteger("1000")) == 1) {
//                            Log.d("Exhaustive", "Skipping branch (saving " + totalCombinations + " combinations!) due to over or under powered team: " + distance + " > " + ethreshold);
//                        }
//                        //Log.d("Exhaustive","Team " + myTeamNumber + "(" + newTeam + ") was too far from average: " + distance + " > " + ethreshold);
//                        // nothing changed
//                        continue;
//                    }
//
//                    // this copy is for reverting if we don't end up
//                    // getting a better team.
//                    TeamSet teamsCopy = new TeamSet(newTeams);
//                    //for(int i=0; i < teams.size(); i++)
//                    //{ teamsCopy.add(teams.get(i).copy()); }
//
//                    // set myTeam...
//                    teamsCopy.set(myTeamSlot, newTeam);
//                    //Log.d("Exhaustive","Recursive call to get team " + (myTeamNumber+1));
//                    TeamSet teamsCopyReturned = getBestCombinationExhaustiveR((Team) playerPool2, teamsCopy, numberOfTeams, grandAveragePlayer, perTeam, newBestTeamCombination_skillDifferential, myCallingIteration);
//                    if (TeamPicker.StopAlgorithm == true) {
//                        return null;
//                    }
//                    double differential = teamsCopyReturned.getLargestDifferenceBetweenTeams();
//                    if (newBestTeamCombination_skillDifferential == -1 || differential < newBestTeamCombination_skillDifferential) {
//                        Log.d("BestExhaustiveTeams", "New best team for team picker " + (myTeamNumber) + ".  Differential: " + differential + " ... old: " + newBestTeamCombination_skillDifferential);
//                        newBestTeamCombination_skillDifferential = differential;
//                        newTeams = teamsCopyReturned;
//                        for (int i = 0; i < newTeams.size(); i++) {
//                            Log.d("BestExhaustiveTeams", "Team " + (i + 1) + ": " + newTeams.get(i));
//                        }
//                    }
//                }
//                //Log.d("Exhaustive","Last team by loop for team " + (myTeamNumber) + ": " + getLargestDifferenceBetweenTeams(newTeams) + " ... size of: " + newTeams.size());
//                //Log.d("BestExhaustiveTeams","Best after while loop team by loop for team " + (myTeamNumber) + ": " + getLargestDifferenceBetweenTeams(newTeams));
//            }
//            //if(!last)
//            //{ Log.d("Exhaustive","Best team combination found by team " + (myTeamNumber) + " loop: " + getLargestDifferenceBetweenTeams(newTeams)); }
//            return newTeams;
//        }
//
//        public TeamSet getBestCombinationExhaustive(Team playerPool, int numberOfTeams, Player grandAveragePlayer) {
//            long start = System.currentTimeMillis();
//            Log.d("Exhaustive", "START: " + (new Timestamp(start).toString()));
//            exhaustiveCombinationsChecked = BigInteger.ZERO;
//            // number per team.
//            int perTeam = (playerPool.size() / numberOfTeams);
//
//            // the best teams combination.
//
//
//            BigInteger totalCombinations = BigInteger.ONE;
//            int thisSize = playerPool.size();
//            int perTeam2 = perTeam;
//            for (int i = 0; i < numberOfTeams; i++) {
//                perTeam2 = perTeam;
//                int myTeamNumber = i + 1;
//                if (myTeamNumber != numberOfTeams && thisSize % ((numberOfTeams + 1) - myTeamNumber) != 0) {
//                    perTeam2++;
//                }
//                if (thisSize == 0 || perTeam2 == 0) {
//                    continue;
//                }
//                Log.d("...", "thisSize: " + thisSize + " ... perTeam2: " + perTeam2);
//                CombinationGenerator c = new CombinationGenerator(thisSize, perTeam2);
//                totalCombinations = totalCombinations.multiply(c.getTotal());
//                thisSize -= perTeam2;
//            }
//            ExhaustiveCombinationsPossible = totalCombinations;
//
//            Log.d("Exhaustive", "TOTAL COMBINATIONS POSSIBLE: " + totalCombinations);
//            publishProgress(0);
//            //setDialogMessage(");
//            Log.d("...", "teams: " + numberOfTeams + ", pp size: " + playerPool.size() + ", pT: " + perTeam);
//            TeamSet bestTeams = getBestCombinationExhaustiveR(playerPool, new TeamSet(), numberOfTeams, grandAveragePlayer, perTeam, -1.0, 0);
//            if (TeamPicker.StopAlgorithm == true) {
//                return null;
//            }
//
//            long end = System.currentTimeMillis();
//            Log.d("Exhaustive", "END: " + (new Timestamp(end).toString()));
//            TeamPicker.secondsTakenByExhaustive = (end - start);
//
//            double s = (double) ((end - start) / 1000) % 60;
//            double m = (double) ((end - start) / (1000 * 60)) % 60;
//            double h = (double) ((end - start) / (1000 * 60 * 60)) % 60;
//            String timeTaken = h + " hours, " + m + " minutes, " + s + " seconds";
//            Log.d("Exhaustive", "TOTAL TIME: " + timeTaken);
//            return bestTeams;
//        }
//
//    }
//
//    private TeamSet sortOutConstraintsFirst(
//            TeamSet teams,
//            Team playerPool, Player startingAveragePlayer,
//            boolean randomize) {
//
//        Iterator<Player> pitr = playerPool.iterator();
//        ArrayList<Player> usedPlayers = new ArrayList<Player>();
//        while (pitr.hasNext()) {
//            Player tp = pitr.next();
//            // check if we've already used this player...
//
//            boolean doNotContinue = false;
//            for (int i = 0; i < usedPlayers.size(); i++) {
//                if (usedPlayers.get(i).getPlayerID() == tp.getPlayerID()) {
//                    //Log.d("UsedPlayer","Player " + tp.getName() + " has already been put on a team.");
//                    doNotContinue = true;
//                    break;
//                }
//            }
//            if (doNotContinue) {
//                continue;
//            }
//
//            //Log.d("Constraints","Checking to see if " + tp.getName() + " has any constraints.");
//            if (PlayerPicker.getAntiPairsForPlayer(tp.getPlayerID()).length != 0
//                    || PlayerPicker.getPairsForPlayer(tp.getPlayerID()).length != 0) {
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
//                    toast(getResources().getString(R.string.teamPicker_cannotSolve));
//                    return null;
//                }
//
//                Team thisTeam = teams.get(bestTeamIndex);
//                thisTeam.addAll(tt);
//                usedPlayers.addAll(tt);
//                teams.set(bestTeamIndex, thisTeam);
//            }
//        }
//
//        return teams;
//    }
//
//    public void repickTeams(boolean findBetter) {
//        TeamPicker.StopAlgorithm = false;
//        if (sp.getIntegerPreference("PickingAlgorithm") != -1) {
//            PickingAlgorithm = sp.getIntegerPreference("PickingAlgorithm");
//
//            // to prevent force close because of previous preference.
//            if (PickingAlgorithm > getResources().getStringArray(R.array.algorithmChoices).length - 1) {
//                PickingAlgorithm = getResources().getStringArray(R.array.algorithmChoices).length - 1;
//                sp.savePreference("PickingAlgorithm", PickingAlgorithm);
//            }
//        }
//
//        //PlayerDatabase VBTP.PlayerDB = new PlayerDatabase(getBaseContext());
//        EditText myEditText = (EditText) this.findViewById(R.id.numberOfTeams);
//        if (myEditText.getText().toString().length() > 0) {
//            TeamPicker.numberOfTeams = Integer.parseInt(myEditText.getText().toString());
//        }
//        if (TeamPicker.numberOfTeams <= 1) {
//            TeamPicker.numberOfTeams = 2;
//        }
//        sp.savePreference("NumberOfTeams", numberOfTeams);
//
//        if (TeamPicker.numberOfTeams < PlayerPicker.TeamsRequired) {
//            slowToast(String.format(getResources().getString(R.string.teamPicker_moreTeamsRequired), PlayerPicker.TeamsRequired));
//            return;
//        } else if (PlayerPicker.selectedPlayerIDs.length % TeamPicker.numberOfTeams != 0) {
//            slowToast(String.format(getResources().getString(R.string.teamPicker_unevenTeams), selectedPlayerIDs.length, TeamPicker.numberOfTeams));
//        }
//
//        // if we haven't exited by now, we're going to try to repick.
//        slowToast(String.format(getResources().getString(R.string.teamPicker_repickingTeams), getResources().getStringArray(R.array.algorithmChoices)[PickingAlgorithm]));
//
//        //teams = getTeamsUsingAlgorithm(PickingAlgorithm, ((CheckBox) this.findViewById(R.id.teamPickerRandomCheckbox)).isChecked());
//        // randomize is also going to be "randomize" later on.
//        boolean randomize = ((CheckBox) this.findViewById(R.id.teamPickerRandomCheckbox)).isChecked();
//
//        ((Button) this.findViewById(R.id.repickButton)).setEnabled(false);
//        ((Button) this.findViewById(R.id.findBetterButton)).setEnabled(false);
//        VBTPActivity.allowOrientationChange = false;
//        new getTeamsUsingAlgorithmTask(this, randomize, findBetter).execute(PickingAlgorithm);
//    }
//
//    public void updateDisplayWithTeams(TeamSet teams) {
//        if (teams == null || TeamPicker.StopAlgorithm) {
//            if (TeamPicker.StopAlgorithm) {
//                Log.d("UpdateDisplay", "Algorithm was canceled.");
//            }
//            toast(getResources().getString(R.string.teamPicker_canceledAlgorithm));
//            TeamPicker.StopAlgorithm = false;
//            return;
//        }
//
//        LinearLayout ll = (LinearLayout) findViewById(R.id.teams);
//        ll.removeAllViews();
//
//        if (UnevenTeams) {
//            ((TextView) findViewById(R.id.unevenTeamsMessage)).setText(this.getResources().getString(R.string.unevenTeamsText));
//            ((TextView) findViewById(R.id.unevenTeamsMessage)).setVisibility(View.VISIBLE);
//        } else {
//            ((TextView) findViewById(R.id.unevenTeamsMessage)).setText("");
//            ((TextView) findViewById(R.id.unevenTeamsMessage)).setVisibility(View.GONE);
//        }
//        Log.d("UpdateDisplay", "Trying to update display... # of teams: " + teams.size());
//        int teamBiggestScore = 0;
//        int teamSmallestScore = -1;
//        Skills highestSkills = new Skills();
//        Skills lowestSkills = new Skills(new int[]{0, 0, 0, 0, 0, 0, 0});
//
//        for (int i = 0; i < teams.size(); i++) {
//            int thisTeamNumber = i + 1;
//            int teamTotalScore = 0;
//
//            LinearLayout playerList = new LinearLayout(getBaseContext());
//            playerList.setGravity(Gravity.VERTICAL_GRAVITY_MASK);
//            playerList.setOrientation(LinearLayout.VERTICAL);
//            Team thisTeam = teams.get(i);
//            ListIterator<Player> itr = thisTeam.listIterator();
//            while (itr.hasNext()) {
//                Player thisPlayer = itr.next();
//
//                // get highest/lowest skills.
//                Skills teamSkills = thisTeam.getCumulativeSkills();
//                Iterator<String> tsitr = teamSkills.keySet().iterator();
//                //String TAG = "HighestLowestSkillComputation";
//                while (tsitr.hasNext()) {
//                    String key = tsitr.next();
//                    if (!highestSkills.containsKey(key)) {
//                        highestSkills.put(key, teamSkills.get(key));
//                        //Log.v("SkillMeasurements", "initialized highest " + key + " with: " + teamSkills.get(key));
//                    } else if (teamSkills.get(key) > highestSkills.get(key)) {
//                        highestSkills.put(key, teamSkills.get(key));
//                        //Log.v("SkillMeasurements", "set highest " + key + " to: " + teamSkills.get(key));
//                    }
//                    if (!lowestSkills.containsKey(key) || lowestSkills.get(key) == 0) {
//                        lowestSkills.put(key, teamSkills.get(key));
//                        //Log.v("SkillMeasurements", "initialized lowest " + key + " with: " + teamSkills.get(key));
//                    } else if (teamSkills.get(key) < lowestSkills.get(key) || lowestSkills.get(key) == 0) {
//                        lowestSkills.put(key, teamSkills.get(key));
//                        //Log.v("SkillMeasurements", "set lowest " + key + " to: " + teamSkills.get(key));
//                    }
//                    teamTotalScore += teamSkills.get(key);
//                }
//                TextView playerName = new TextView(getBaseContext());
//                playerName.setText(thisPlayer.getName());
//                playerName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                playerList.addView(playerName);
//            }
//
//            teamTotalScore = thisTeam.getSkillsSum();
//            if (teamTotalScore < teamSmallestScore || teamSmallestScore == -1) {
//                teamSmallestScore = teamTotalScore;
//            }
//            if (teamTotalScore > teamBiggestScore) {
//                teamBiggestScore = teamTotalScore;
//            }
//
//            TextView teamTitle = new TextView(getBaseContext());
//            teamTitle.setTextColor(Color.WHITE);
//            teamTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//            if (sp.getBooleanPreference("ShowDetailedAlgorithmResults")) {
//                teamTitle.setText(String.format(resources.getString(R.string.teamHeader_detailed), thisTeamNumber, teamTotalScore));
//            } else {
//                teamTitle.setText(String.format(resources.getString(R.string.teamHeader), thisTeamNumber));
//            }
//            TextView playerNumber = new TextView(getBaseContext());
//            playerNumber.setTextColor(Color.WHITE);
//            playerNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//            playerNumber.setText(String.format(resources.getString(R.string.teamPlayerNumber), thisTeam.size()));
//            ll.addView(teamTitle);
//            ll.addView(playerNumber);
//            ll.addView(playerList);
//        }
//
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
//    }
//
//    private HashMap<String, Double> getDeficiencyWeights(
//            Skills teamSkillAverages,
//            Skills myTeamSkills) {
//
//        HashMap<String, Double> deficiencies = new HashMap<String, Double>();
//        Iterator<String> itr = teamSkillAverages.keySet().iterator();
//        while (itr.hasNext()) {
//            String key = itr.next();
//            int myValue = 0;
//            if (myTeamSkills.containsKey(key)) {
//                myValue = myTeamSkills.get(key);
//            }
//            int averageValue = teamSkillAverages.get(key);
//            int difference = averageValue - myValue;
//            if (difference > 0) {
//                deficiencies.put(key, difference * 2.5);
//            } else if (difference == 0) {
//                deficiencies.put(key, 1.0);
//            } else if (difference < 0) {
//                deficiencies.put(key, (1.0 / difference) / 3);
//            }
//        }
//        return deficiencies;
//    }
//
//    // this gets the average for ALL teams
//    public Skills getTeamsSkillAverages(TeamSet teams, int myTeam) {
//        Skills teamSkillAverages = new Skills();
//        for (int i = 0; i < teams.size(); i++) {
//            // except we can't count me.
//            if (i != myTeam) {
//                Team cTeam = teams.get(i);
//                Skills tSkills = cTeam.getCumulativeSkills();
//                Iterator<String> itr = tSkills.keySet().iterator();
//                while (itr.hasNext()) {
//                    String key = itr.next();
//                    if (!teamSkillAverages.containsKey(key)) {
//                        teamSkillAverages.put(key, tSkills.get(key));
//                        continue;
//                    }
//                    int cskill = teamSkillAverages.get(key);
//                    cskill += tSkills.get(key);
//                    teamSkillAverages.put(key, cskill);
//                }
//            }
//        }
//        Iterator<String> itr = teamSkillAverages.keySet().iterator();
//        while (itr.hasNext()) {
//            // generate the actual averages, right now we have totals.
//            String key = itr.next();
//            int cskill = teamSkillAverages.get(key);
//            cskill /= numberOfTeams;
//            // this really doesn't make sense... but it seems to work.
//            // no clue why.... maybe because at this point
//            // we are down a man?
//            teamSkillAverages.put(key, cskill);
//        }
//
//        return teamSkillAverages;
//    }
//
//    public Player findPlayerBringingTeamTowardsAverage(Team playerPool,
//                                                       TeamSet teams, int myTeam, Player startingAveragePlayer) {
//
//        if (playerPool.size() == 1) {
//            return playerPool.getFirst();
//        }
//
//        Player bestPlayer = new Player();
//        double bestDistance = -1;
//
//        // so we need to walk through and get the best player.
//        Iterator<Player> pitr = playerPool.iterator();
//        while (pitr.hasNext()) {
//            // for each player, calculate the differential.
//            Player tp = pitr.next();
//
//            // get my team average skills, adding in the possible player.
//            Skills myMap = teams.get(myTeam).getCumulativeSkills();
//            Iterator<String> it = myMap.keySet().iterator();
//            while (it.hasNext()) {
//                String thisSkill = it.next();
//                int thisTS = myMap.get(thisSkill);
//                thisTS += tp.getSkill(thisSkill);
//                myMap.put(thisSkill, thisTS / (teams.get(myTeam).size() + 1));
//            }
//
//            double thisOne = myMap.distanceTo(startingAveragePlayer);
//
//            if (thisOne < bestDistance || bestDistance == -1) {
//                bestDistance = thisOne;
//                bestPlayer = tp;
//            }
//
//            // remove from my team.
//            //myt.removeLast();
//            //teams.set(myTeam, myt);
//        }
//        //Log.v("Algorithm", "Best player: " + bestPlayer.getName());
//        return bestPlayer;
//    }
//
//    public Player findPlayerGeneratingLowestOverallDifferential(Team playerPool,
//                                                                TeamSet teams, int myTeam) {
//        if (playerPool.size() == 1) {
//            return playerPool.getFirst();
//        }
//
//        Player bestPlayer = new Player();
//        double bestDistance = -1;
//
//        // so we need to walk through and get the best player.
//        Iterator<Player> pitr = playerPool.iterator();
//        while (pitr.hasNext()) {
//            // for each player, calculate the differential.
//            Player tp = pitr.next();
//
//            // add to my team.
//            Team myt = teams.get(myTeam);
//            myt.add(tp);
//            teams.set(myTeam, myt);
//
//            double thisOne = teams.getLargestDifferenceBetweenTeams();
//
//            if (thisOne < bestDistance || bestDistance == -1) {
//                bestDistance = thisOne;
//                bestPlayer = tp;
//            }
//
//            // remove from my team.
//            myt.remove(tp);
//            teams.set(myTeam, myt);
//        }
//        //Log.v("Algorithm", "Best player: " + bestPlayer.getName());
//        return bestPlayer;
//    }
//
//    public Player findPlayerRickAlgorithm(Team playerPool,
//                                          Team team) {
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
//
//    public Player[] getFirstPlayerGrandAverageAlgorithm(Team playerPool, Player grandAveragePlayer) {
//        Player[] players;
//
//        // create new blank array.
//        if (playerPool.size() > 2) {
//            players = new Player[2];
//        }
//
//        // return the only two players left.
//        else if (playerPool.size() == 2) {
//            return new Player[]{playerPool.get(0), playerPool.get(1)};
//        }
//
//        // return one player.
//        else if (playerPool.size() == 1) {
//            return new Player[]{playerPool.getFirst()};
//        }
//
//        // return no players.
//        else {
//            return new Player[0];
//        }
//
//        double largestDistanceFromAverage = -1;
//        double distanceFromGrandAverageAsTeam = -1;
//
//        Iterator<Player> pitr = playerPool.iterator();
//
//        // get the player furthest from the average player
//        while (pitr.hasNext()) {
//            Player p1 = pitr.next();
//            double thisDistance = p1.distanceTo(grandAveragePlayer);
//            if (thisDistance > largestDistanceFromAverage || largestDistanceFromAverage == -1) {
//                players[0] = p1;
//                largestDistanceFromAverage = thisDistance;
//            }
//        }
//
//        // get the player that would pull the first player closest to the average.  That means we make a "team" out of the two players...
//        pitr = playerPool.iterator();
//        while (pitr.hasNext()) {
//            Player p1 = pitr.next();
//
//            if (players[0].getPlayerID() == p1.getPlayerID()) {
//                continue;
//            }
//
//            // make a team.
//            Team tt = new Team();
//            tt.add(players[0]);
//            tt.add(p1);
//
//            double thisDistance = grandAveragePlayer.distanceTo(tt);
//            if (thisDistance < distanceFromGrandAverageAsTeam || distanceFromGrandAverageAsTeam == -1) {
//                players[1] = p1;
//                distanceFromGrandAverageAsTeam = thisDistance;
//            }
//        }
//
//        //Log.v("GrandAverageAlgorithm", "FINISHED: best first player pair is " + players[0].getName() + " and " + players[1].getName() + " who are " + distanceFromGrandAverageAsTeam + " away from the grand average when combined.");
//        return players;
//    }
//
//    public Player[] getFirstPlayerRickAlgorithm(Team playerPool) {
//        //Log.v("RickAlgorithmFirst", "starting getFirstPlayerRickAlgorithm");
//        Player[] players;
//        //Log.d("PlayerPoolSize",""+playerPool.size());
//
//        // create new blank array.
//        if (playerPool.size() > 2) {
//            players = new Player[2];
//        }
//
//        // return the only two players left.
//        else if (playerPool.size() == 2) {
//            return new Player[]{playerPool.get(0), playerPool.get(1)};
//        }
//
//        // return one player.
//        else if (playerPool.size() == 1) {
//            return new Player[]{playerPool.getFirst()};
//        }
//
//        // return no players.
//        else {
//            return new Player[0];
//        }
//
//        double largestDistance = -1;
//        Iterator<Player> pitr = playerPool.iterator();
//        while (pitr.hasNext()) {
//            Player p1 = pitr.next();
//            //Log.d("RickAlgorithmFirst", "comparing " + p1.getName() + " to everyone else.");
//            Iterator<Player> pitr2 = playerPool.iterator();
//            while (pitr2.hasNext()) {
//                Player p2 = pitr2.next();
//                if (p1.getPlayerID() == p2.getPlayerID()) {
//                    continue;
//                }
//                double thisDistance = p1.distanceTo(p2);
//                if (thisDistance > largestDistance || largestDistance == -1) {
//                    //Log.d("RickAlgorithm", "new best player pair is " + p1.getName() + " and " + p2.getName() + " who are " + thisDistance + " apart.");
//                    players[0] = p1;
//                    players[1] = p2;
//                    largestDistance = thisDistance;
//                }
//            }
//        }
//        //Log.v("RickAlgorithm", "FINISHED: best first player pair is " + players[0].getName() + " and " + players[1].getName() + " who are " + largestDistance + " apart.");
//        return players;
//    }
//
//    public void MonteCarloTest(boolean restart) {
//        TeamPicker.MonteCarloSimulation = true;
//
//        if (TeamPicker.StopAlgorithm == true) {
//            TeamPicker.MonteCarloSimulation = false;
//            return;
//        }
//
//        // so first we need to populate the database with randomness.
//        int maxPlayers = 60;
//        int maxTeams = 10;
//        int maxIterations = 10;
//        if (VBTP.AllowExhaustive) {
//            maxPlayers = 12;
//            maxTeams = 4;
//        }
//
//        if (restart) {
//            TeamPicker.MonteCarloIterations = 0;
//        }
//        if (TeamPicker.MonteCarloIterations == maxIterations) {
//            TeamPicker.MonteCarloSimulation = false;
//            return;
//        }
//
//        // empty the database.
//        VBTP.PlayerDB.clear();
//
//        // next, for each iteration...
//        Log.d("MonteCarlo", "Iteration " + (TeamPicker.MonteCarloIterations + 1));
//
//        Random r = new Random();
//        int numPlayers = r.nextInt(maxPlayers + 1);
//
//        // need at least 4 players for more than 2 teams.
//        if (numPlayers < 4) {
//            numPlayers = 2;
//        }
//
//        int numTeams = r.nextInt(maxTeams + 1);
//
//        // need at least 2 people on a team.
//        if (numTeams > numPlayers / 2) {
//            numTeams = numPlayers / 2;
//        }
//        if (numTeams <= 1) {
//            numTeams = 2;
//        }
//
//        for (int j = 0; j < numPlayers; j++) {
//            Random x = new Random();
//            Skills s = new Skills();
//            for (int k = 0; k < Settings.colSkills.length; k++) {
//                s.put(Settings.colSkills[k], x.nextInt(PlayerDatabase.MaxSkillValue));
//            }
//            Player p = new Player(0, "Player", Integer.toString(j + 1), true, s);
//            VBTP.PlayerDB.insertPlayer(p);
//
//            Log.d("MonteCarloDetails", "Inserted player " + p.getName());
//        }
//
//        // now pick teams.  how many teams, you ask? ah-ha.
//        numberOfTeams = numTeams;
//        TeamPicker.MonteCarloPlayers = numPlayers;
//
//        Log.d("MonteCarlo", "Number of players: " + numPlayers + '\n' + "Number of teams: " + numTeams);
//        new getTeamsUsingAlgorithmTask(this, false, false).execute(PickingAlgorithm);
//    }
//
//
//    void setUpMyDialog() {
//        if (pd1 == null) {
//            pd1 = new PickingProgressDialog(this);
//            pd1Title = getResources().getString(R.string.algorithmProgressDialog_defaultTitle);
//            pd1Progress = 0;
//            pd1.setTitle(pd1Title);
//            pd1.setMessage(getResources().getString(R.string.algorithmProgressDialog_message));
//            pd1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        }
//    }
//
//    public void setProgressPercent(int progress) {
//        setDialogProgress(progress);
//    }
//
//    String getDialogTitle() {
//        return pd1Title;
//    }
//
//    void setDialogTitle(String title) {
//        if (pd1 != null && isMyDialogShowing) {
//            pd1.setTitle(title);
//        }
//    }
//
//    void setProgressMessage(String message) {
//        if (pd1 != null && isMyDialogShowing) {
//            pd1.setMessage(message);
//        }
//    }
//
//    void setDialogProgress(int progress) {
//        Log.d("Progress", "Progress: " + progress);
//        if (pd1 != null && isMyDialogShowing) {
//            pd1.setProgress(progress);
//        }
//    }
//
//    public void showMyDialog() {
//        isMyDialogShowing = true;
//        if (pd1 != null && !pd1.isShowing()) {
//            pd1.show();
//        }
//    }
//
//    public void showMyDialog(getTeamsUsingAlgorithmTask task) {
//        isMyDialogShowing = true;
//        if (pd1 != null && !pd1.isShowing()) {
//            pd1.setTitle(pd1Title);
//            pd1.setProgress(pd1Progress);
//            pd1.show();
//        }
//        pd1.setTask(task);
//    }
//
//    public void dismissMyDialog() {
//        if (pd1 != null && pd1.isShowing()) {
//            pd1.setProgress(0);
//            pd1Progress = 0;
//            pd1.dismiss();
//        }
//    }
//
//    public void killMyDialog() {
//        isMyDialogShowing = false;
//    }
//
//    public void toast(String aMessage) {
//        Toast.makeText(this.getBaseContext(), aMessage, Toast.LENGTH_SHORT).show();
//    }
//
//    public void slowToast(String aMessage) {
//        Toast.makeText(this.getBaseContext(), aMessage, Toast.LENGTH_LONG).show();
//    }
//
//    // this has problems!
//    static void shufflePlayerArray(Player[] ar) {
//        Random rnd = new Random();
//        for (int i = ar.length - 1; i >= 0; i--) {
//            int index = rnd.nextInt(i + 1);
//            // Simple swap
//            Player a = ar[index];
//            ar[index] = ar[i];
//            ar[i] = a;
//        }
//    }
//}
