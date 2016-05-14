package com.ellsworthcreations.vbtp20;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class Team extends LinkedList<Player> {

	private static final long serialVersionUID = 1L;
	
	public Team()
	{ }
	
	public Team(LinkedList<Player> pList)
	{
		// make a team out of the given players.
		this.addAll(pList);
	}
	
	public Team(Player p) 
	{
		// make a team with one player
		this.add(p);
	}

	public Team copy()
	{
		// make a copy of each player object
		// and add it to the new team.
		Team nt = new Team();
		for(int i=0; i < this.size(); i++)
		{
			Player p = this.get(i).copy();
			nt.add(p);
		}
		return nt;
	}
	public Player getAveragePlayer()
	{
		Player player;
		// average is the only player left...
		if(this.size() == 1)
		{ return (Player) this.removeFirst(); }
		
		// average is zero :P
		else if(this.size() == 0) { return new Player(); }
		
		// so we can just treat the pool as one giant team:
		Skills averagePlayerSkills = this.getSkillAverages();
		
		// 	public Player (int playerID, String playerFirstName, String playerLastName, boolean playerActive, Skills skills) {
		player = new Player(0, "Average", "Player", true, averagePlayerSkills);
	
		return player;
	}
	
	public Player getRandomPlayer()
	{
		Random r = new Random();
		return this.get(r.nextInt(this.size()));
	}
	
	public Player getBestPlayerWeighted(HashMap<String, Double> myWeights) {
		Player bestPlayer = this.getFirst();
		Double bestRank = 0.0;
		// so we need to walk through and get the best player.
		Iterator<Player> pitr = this.iterator();
		while(pitr.hasNext())
		{
			Player tp = pitr.next();
			Double tpRank = 0.0;
			Skills tpSkills = tp.getSkills();
			Iterator<String> tpitr = tpSkills.keySet().iterator();
			while(tpitr.hasNext())
			{
				String key = tpitr.next();
				// myWeights Deficiencies contains ... weights.
				if(myWeights.containsKey(key))
				{
					tpRank += (tpSkills.get(key) * myWeights.get(key));
				}
				else { tpRank += tpSkills.get(key); }
			}
			if(tpRank > bestRank)
			{ 
				bestRank = tpRank;
				bestPlayer = tp;
			}
		}
		
		return bestPlayer;
	}
	
	public Player removeBest()
	{
		Player player = this.getFirst();
		int bestSkills = player.getSkills().cumulativeSkills();
		ListIterator<Player> itr = this.listIterator();
		while(itr.hasNext())
		{
			Player tp = itr.next();
			if(tp.getSkills().cumulativeSkills() > bestSkills)
			{ player = tp; bestSkills = tp.getSkills().cumulativeSkills(); }
		}
		this.remove(player);
		return player;
	}
	
	public void removePlayer(Player removeThisPlayer)
	{
		Player tp = new Player();
		ListIterator<Player> itr = this.listIterator();
		while(itr.hasNext())
		{
			Player tp1 = itr.next();
			if(tp1.getPlayerID() == removeThisPlayer.getPlayerID())
			{ tp = tp1; }
		}
		this.remove(tp);
	}
	
	public Player removeAverage()
	{
		if(this.size() == 0) { return null; }
		else if(this.size() == 1) { return this.removeFirst(); }
		
		Player averagePlayer = this.getAveragePlayer();
		Player player = this.getFirst();
		double distanceFromAverage = averagePlayer.distanceTo(player);
		
		ListIterator<Player> itr = this.listIterator();
		while(itr.hasNext())
		{
			Player tp = itr.next();
			if(averagePlayer.distanceTo(tp) > distanceFromAverage)
			{ player = tp; distanceFromAverage = averagePlayer.distanceTo(tp); }
		}
		this.remove(player);
		return player;
	}
	
	public Skills getSkillAverages()
	{
		Skills teamSkillAverages = this.getCumulativeSkills();
		Iterator<String> itr = teamSkillAverages.keySet().iterator();
		if(this.size() > 0)
		{
			while(itr.hasNext())
			{
				String key = itr.next();
				int cskill = teamSkillAverages.get(key);
				cskill /= this.size();
				teamSkillAverages.put(key, cskill);
			}
		}
		return teamSkillAverages;
	}
	
	public int getSkillsSum()
	{
		int teamSkills = 0;
		ListIterator<Player> itr = this.listIterator();
		while(itr.hasNext())
		{
			teamSkills += itr.next().getSkills().cumulativeSkills();
		}
		return teamSkills;
	}
	
	public Skills getCumulativeSkills()
	{
		Skills teamSkills = new Skills();
		for(int i=0; i < Settings.colSkills.length; i++) { teamSkills.put(Settings.colSkills[i], 0); }
		ListIterator<Player> itr = this.listIterator();
		while(itr.hasNext())
		{
			Player thisPlayer = itr.next();
			teamSkills.add(thisPlayer.getSkills());
		}
		return teamSkills;
	}
	
	@Override
	public String toString()
	{
		String ns = "";
		Iterator<Player> pitr = this.iterator();
		while(pitr.hasNext())
		{ if(ns != "") { ns += ", "; } ns += pitr.next().getName(); }
		return ns;
	}
	
	public void removeAll(Team removeThesePlayers)
	{
		Iterator<Player> pitr = removeThesePlayers.iterator();
		while(pitr.hasNext())
		{
			Player p1 = pitr.next();
			int remaining = this.size();
			for(int i=0; i < remaining; i++)
			{
				Player p2 = this.get(i);
				if(p1.equals(p2))
				{ this.remove(p2); remaining--; break; }
			}
		}
	}
	
	public boolean genderExistsOnTeam(Player.Gender genderType)
	{
		Iterator<Player> pitr = this.iterator();
		while(pitr.hasNext())
		{ if(pitr.next().getGender() == genderType) { return true; } }
		return false;
	}
	
	public double distanceTo(Team team2)
	{ return distanceTo(team2, false); }
	
	public double distanceTo(Team team2, boolean padWithAverage)
	{
		//Log.v("TeamDistance", "Getting distances for teams 1 and 2...");
		
		/*
		 * if comparing to a team of size 0, should that team be considered average?
		 * e.g., if team1 has 0 players and team2 has 1 player, should team 1
		 * just be a team of 1 average player?  or perhaps fill it out with
		 * average players?
		
		*/
		Skills map1 = this.getCumulativeSkills();
		Skills map2 = team2.getCumulativeSkills();
		if(padWithAverage)
		{
//			for(int i=this.size(); i < team2.size(); i++)
//			{ map1.add(TeamPicker.averagePlayer.getSkills()); }
//			for(int i=team2.size(); i < this.size(); i++)
//			{ map2.add(TeamPicker.averagePlayer.getSkills()); }
		}
		return map1.distanceTo(map2);
	}
}
