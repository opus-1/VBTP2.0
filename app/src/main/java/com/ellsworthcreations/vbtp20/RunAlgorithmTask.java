//package com.ellsworthcreations.vbtp20;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import java.sql.Timestamp;
//
///**
// * Created by Paul on 5/14/2016.
// */
//public class pickTeamsTask extends AsyncTask<Integer, Integer, TeamSet> {
//    private boolean randomize;
//    private boolean findBetter;
//    private int numberOfPlayers;
//    private int currentAlgorithm = 0;
//    private String[] algorithmChoices = {""};
//
//    public pickTeamsTask(boolean randomize, boolean findBetter) {
//        this.randomize = randomize;
//        this.findBetter = findBetter;
////        String algorithmName = resources.getStringArray(R.array.algorithmChoices)[PickingAlgorithm];
////        pd1Title = algorithmName;
//
//        if (findBetter) {
//            randomize = true;
//        }
//    }
//
//    protected void onPreExecute() {
//        super.onPreExecute();
//        showMyDialog(this);
//    }
//
//    protected TeamSet doInBackground(Integer[] params) {
//        /* algorithm to use */
//        int algorithm = params[0];
//
//        /* the resulting set of teams from a run of the algorithm(s) */
//        TeamSet BestTeams = new TeamSet();
//
//        /*
//         * number of times to loop through the picking, comparing the resulting team to the
//         * thus-far best team selection.  There's an option in preferences to do 10 at a time.
//         */
//        int retryNumber = 1;
//
//        /*
//         * if this is true, then we should replace the current teams (LastTeams) only if
//         * we find one that is better.
//         */
//
//        if (sp.getBooleanPreference("AlgorithmOptions_Retry10AtATime")) {
//            retryNumber = 10;
//            randomize = true;
//        }
//
//        for (int q = 0; q < retryNumber; q++) {
//            TeamSet teams = new TeamSet();
//            if (PickingAlgorithm == TeamPicker.TryAllSelection) {
//                int bestAlgorithm = 0;
//                TeamSet teams2 = new TeamSet();
//                double teamsDistance = -1;
//                String results = Integer.toString(numberOfTeams) + '\t' + TeamPicker.MonteCarloPlayers;
//
//                // why do I have to make it to +1?? I have no idea.
//                int whereToStop = PickingAlgorithm + 1;
//                if (VBTP.AllowExhaustive && TeamPicker.MonteCarloSimulation) {
//                    whereToStop = TeamPicker.ExhaustiveAlgorithm + 1;
//                }
//
//                //Log.d("MonteCarloDetails","Going to " + whereToStop);
//                for (int i = 0; i < whereToStop; i++) {
//                    //Log.d("MonteCarloDetails","i: " + i);
//                    if (TeamPicker.StopAlgorithm == true) {
//                        return null;
//                    }
//                    if (i == TeamPicker.TryAllSelection) {
//                        continue;
//                    }
//
//                    currentAlgorithm = i;
//                    String algorithmName = resources.getStringArray(R.array.algorithmChoices)[i];
//                    Log.d("Algorithm", "STARTING ALGORITHM: " + algorithmName);
//                    // try all of them.
//                    //long start = System.currentTimeMillis();
//                    teams2 = getTeamsUsingAlgorithm(i, randomize);
//
//                    /*long end = System.currentTimeMillis();
//                    double s = (double) ((end-start) / 1000) % 60;
//                    double m = (double) ((end-start) / (1000*60)) % 60;
//                    double h = (double) ((end-start) / (1000*60*60)) % 60;
//                    String timeTaken = h + " hours, " + m + " minutes, " + s + " seconds";*/
//
//                    Log.d("Algorithm", "ALGORITHM FINISHED: " + algorithmName);
//                    if (teams2 == null) {
//                        continue;
//                    }
//                    Log.d(algorithmName, " ... returned with a team size of " + teams2.size());
//                    double teams2Distance = teams2.getLargestDifferenceBetweenTeams();
//                    Log.d("BestAlgorithm", "Largest distance between any two teams using \"" + algorithmName + "\": " + teams2Distance);
//                    results += '\t';
//                    results += Double.toString(teams2Distance);
//
//                    //if(TeamPicker.MonteCarloSimulation)
//                    //{ Log.d("MonteCarlo","Algorithm: " + algorithmName + "\nGreatest distance: " + teams2Distance); }
//                    if (teamsDistance == -1 || teams2Distance < teamsDistance) {
//                        teams = teams2;
//                        teamsDistance = teams2Distance;
//                        bestAlgorithm = i;
//                        Log.d("BestAlgorithm", "Best algorithm is now \"" + algorithmName + "\"");
//                    }
//                    if (retryNumber == 1) {
//                        publishProgress((100 / (PickingAlgorithm)) * (i + 1));
//                    } else {
//                        publishProgress((int) Math.ceil((double) (100 / ((retryNumber) * PickingAlgorithm))) * ((q * PickingAlgorithm + i)));
//                    }
//                }
//
//                Log.i("BestAlgorithm", "The best algorithm ended up being -- " + getResources().getStringArray(R.array.algorithmChoices)[bestAlgorithm] + " -- ");
//                //if(TeamPicker.MonteCarloSimulation)
//                //{
//                //	Log.d("MonteCarlo","Best: " + getResources().getStringArray(R.array.algorithmChoices)[bestAlgorithm]);
//                //}
//            } else {
//                Log.d("Algorithm", "" + algorithm);
//                teams = getTeamsUsingAlgorithm(algorithm, randomize);
//            }
//
//            if (teams != null) {
//                for (int i = 0; i < teams.size(); i++) {
//                    numberOfPlayers += teams.get(i).size();
//                }
//            }
//
//            // so now we have teams.  is it the best? we don't know.
//            if (BestTeams.size() == 0) {
//                BestTeams = teams;
//            } else {
//                // comparison needed.
//                // current best teams has a largest difference?
//                if (BestTeams.getLargestDifferenceBetweenTeams(false) > teams.getLargestDifferenceBetweenTeams(false)) {
//                    BestTeams = teams;
//                }
//            }
//        }
//
//        // was used to find a bug...
//        Log.d("BestTeams", "Best teams size: " + BestTeams.size());
//        if (TeamPicker.LastTeams != null) {
//            Log.d("BestTeams", "Best teams largest differential: " + BestTeams.getLargestDifferenceBetweenTeams() + " ... LastTeams largest diff: " + TeamPicker.LastTeams.getLargestDifferenceBetweenTeams());
//        }
//        if (!findBetter || BestTeams.getLargestDifferenceBetweenTeams(false) < TeamPicker.LastTeams.getLargestDifferenceBetweenTeams(false)) {
//            // this is a better team set.
//            /* teams last picked ... to prevent a double-pick on orientation change. */
//            TeamPicker.LastTeams = BestTeams;
//            return BestTeams;
//        } else {
//            // this is not a better team set... return null? ... nah, return an empty one.
//            Log.d("BackgroundRunner", "Returning null...");
//            return new TeamSet();
//        }
//    }
//
//    @Override
//    protected void onProgressUpdate(Integer[] progress) {
//        String algorithmName = resources.getStringArray(R.array.algorithmChoices)[currentAlgorithm];
//        if (getDialogTitle() != algorithmName) {
//            setDialogTitle(algorithmName);
//        }
//        if (currentAlgorithm == TeamPicker.ExhaustiveAlgorithm) {
//            setProgressMessage(String.format(context.getResources().getString(R.string.exhaustive_progressUpdate), exhaustiveCombinationsChecked, ExhaustiveCombinationsPossible));
//        }
//        setProgressPercent(progress[0]);
//    }
//
//    protected void onPostExecute(TeamSet teams) {
//        dismissMyDialog();
//
//        if (teams == null) {
//            slowToast("Eror picking teams; perhaps too few players for number of teams requested.");
//        }
//
//        Log.d("Async", "Done!");
//
//        TeamPicker.StopAlgorithm = false;
//        if (teams.size() == 0 && findBetter) {
//            slowToast("Not updating display; teams found were not more fair than existing teams.");
//        } else {
//            updateDisplayWithTeams(teams);
//        }
//
//        ((Button) findViewById(R.id.repickButton)).setEnabled(true);
//        ((Button) findViewById(R.id.findBetterButton)).setEnabled(true);
//    }
//
//    @Override
//    public void onCancelled() {
//        super.onCancelled();
//        TeamPicker.StopAlgorithm = true;
//
//        if (!TeamPicker.MonteCarloSimulation) {
//            ((Button) findViewById(R.id.repickButton)).setEnabled(true);
//            ((Button) findViewById(R.id.findBetterButton)).setEnabled(true);
//        }
//
//        VBTPActivity.allowOrientationChange = true;
//        if (this.cancel(true)) {
//            Log.d("Task", "Canceled!");
//        } else {
//            Log.d("Task", "Could not cancel task.  Oh Java.  Sigh.");
//        }
//        toast(getResources().getString(R.string.teamPicker_canceledAlgorithm));
//    }
//}
