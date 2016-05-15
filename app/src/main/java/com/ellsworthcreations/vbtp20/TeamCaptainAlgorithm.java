package com.ellsworthcreations.vbtp20;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by Paul on 5/14/2016.
 */
public class TeamCaptainAlgorithm extends TeamPickingAlgorithm {
    public TeamCaptainAlgorithm(Player[] allPlayers, int numberOfTeams, boolean randomize, boolean forceEqualGenders) {
        super(allPlayers, numberOfTeams, randomize, forceEqualGenders);
    }

    @Override
    public void nextStep(TeamSet teams, Team playerPool, Team playerPool2, int originalPlayerPool) {
        currentTeam = getNextTeam_FurthestFromGrandAverage(teams, playerPool, currentTeam, startingAveragePlayer, randomize);
        Team thisTeam = teams.get(currentTeam);
        if (thisTeam.size() == 0 || (forceEqualGenders && !thisTeam.genderExistsOnTeam(Player.Gender.FEMALE) && !startedPickingOtherGender)) {
            if (randomize) {
                thisTeam.add(playerPool.remove(new Random().nextInt(playerPool.size())));
            } else {
                thisTeam.add(playerPool.removeBest());
            }
        } else {
            Player thisPlayer = new Player();
            Skills teamSkillAverages = getTeamsSkillAverages(teams, currentTeam);
            Skills myTeamSkills = thisTeam.getCumulativeSkills();

            // okies.  now we have to get what we are deficient in.
            HashMap<String, Double> myWeights = getDeficiencyWeights(teamSkillAverages, myTeamSkills);
            thisPlayer = playerPool.getBestPlayerWeighted(myWeights);
            thisTeam.add(thisPlayer);
            playerPool.remove(thisPlayer);
            teams.set(currentTeam, thisTeam);
        }
    }

    private HashMap<String, Double> getDeficiencyWeights(
            Skills teamSkillAverages,
            Skills myTeamSkills) {

        HashMap<String, Double> deficiencies = new HashMap<String, Double>();
        Iterator<String> itr = teamSkillAverages.keySet().iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            int myValue = 0;
            if (myTeamSkills.containsKey(key)) {
                myValue = myTeamSkills.get(key);
            }
            int averageValue = teamSkillAverages.get(key);
            int difference = averageValue - myValue;
            if (difference > 0) {
                deficiencies.put(key, difference * 2.5);
            } else if (difference == 0) {
                deficiencies.put(key, 1.0);
            } else if (difference < 0) {
                deficiencies.put(key, (1.0 / difference) / 3);
            }
        }
        return deficiencies;
    }

    // this gets the average for ALL teams
    public Skills getTeamsSkillAverages(TeamSet teams, int myTeam) {
        Skills teamSkillAverages = new Skills();
        for (int i = 0; i < teams.size(); i++) {
            // except we can't count me.
            if (i != myTeam) {
                Team cTeam = teams.get(i);
                Skills tSkills = cTeam.getCumulativeSkills();
                Iterator<String> itr = tSkills.keySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next();
                    if (!teamSkillAverages.containsKey(key)) {
                        teamSkillAverages.put(key, tSkills.get(key));
                        continue;
                    }
                    int cskill = teamSkillAverages.get(key);
                    cskill += tSkills.get(key);
                    teamSkillAverages.put(key, cskill);
                }
            }
        }
        Iterator<String> itr = teamSkillAverages.keySet().iterator();
        while (itr.hasNext()) {
            // generate the actual averages, right now we have totals.
            String key = itr.next();
            int cskill = teamSkillAverages.get(key);
            cskill /= numberOfTeams;
            // this really doesn't make sense... but it seems to work.
            // no clue why.... maybe because at this point
            // we are down a man?
            teamSkillAverages.put(key, cskill);
        }

        return teamSkillAverages;
    }
}
