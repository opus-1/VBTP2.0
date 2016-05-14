package com.ellsworthcreations.vbtp20;

import java.util.Iterator;
import java.util.LinkedList;

public class TeamSet extends LinkedList<Team> {

	private static final long serialVersionUID = 1L;
	
	public TeamSet()
	{ }
	
	public String toString()
	{
		String teamString = "TEAM SET";
		Iterator<Team> titr = this.iterator();
		int x = 1;
		while(titr.hasNext())
		{
			teamString += "\nTeam " + x + ":" + titr.next().toString();
			x += 1;
		}
		return teamString;
	}
	
	public TeamSet(LinkedList<Team> pList)
	{
		// make a team out of the given teams.
		this.addAll(pList);
	}
	
	public TeamSet(Team p) 
	{
		// make a team set with one team
		this.add(p);
	}

	public TeamSet copy()
	{
		// make a copy of each player object
		// and add it to the new team.
		TeamSet nt = new TeamSet();
		for(int i=0; i < this.size(); i++)
		{
			Team p = this.get(i).copy();
			nt.add(p);
		}
		return nt;
	}
	
	public double getLargestDifferenceBetweenTeams()
	{ return this.getLargestDifferenceBetweenTeams(false); }
	
	public double getLargestDifferenceBetweenTeams(boolean padWithAverage)
	{
		double greatestDifference = 0;
		for(int i=0; i < this.size(); i++)
		{
			double myDifference = 0;
			for(int j=i; j < this.size(); j++)
			{
				if(i == j) { continue; }
				double myDifference2 = this.get(i).distanceTo(this.get(j), padWithAverage);
				//Log.v("LargestDifference","Difference between team " + i + " and team " + j + ": " + myDifference2);
				if(myDifference2 > myDifference)
				{ myDifference = myDifference2; }
			}
			if(myDifference > greatestDifference)
			{ greatestDifference = myDifference; }
		}
		return greatestDifference;
	}
}
