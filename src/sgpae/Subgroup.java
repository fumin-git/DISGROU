package sgpae;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import sgpae.extractor.data.DataSet;
import sgpae.extractor.data.Interval;
import sgpae.extractor.data.Transaction;
import sgpae.manifest.Manifest;

public class Subgroup
{
	public ArrayList<Interval> intervals;
	public double diffSum;
	public int nbElements;
	
	public ArrayList<Transaction> transactions;
	public String falseKey;
	
	public Subgroup(ArrayList<Interval> intervals, double diffSum, int nbElements)
	{
		super();
		this.diffSum = diffSum;
		this.nbElements = nbElements;
		
		this.intervals = sortInterval(intervals);
		transactions = new ArrayList<Transaction>();
		
		falseKey = "";
	}


	public static ArrayList<Interval> sortInterval(ArrayList<Interval> intervals)
	{
		ArrayList<Interval> finalRes = new ArrayList<Interval>();
		newInterLoop : for(Interval interval : intervals)
		{
			int i;
			
			positionInFinalLoop : for(i=0; i < finalRes.size(); i++)
			{
				Interval actInter = finalRes.get(i);
				
				if(actInter.compareTo(interval)<0)
				{
					break positionInFinalLoop; 
				}
			}
			finalRes.add(i,interval);
		}
		return finalRes;
	}
	
	public void add(Transaction trans)
	{
		this.diffSum += (float)trans.getValue(Manifest.dataset.getTarget()).getValue();
		this.nbElements += 1;
		transactions.add(trans);
	}
	
	/*public void add(double diffSum, int nbElements)
	{
		this.diffSum += diffSum;
		this.nbElements += nbElements;
	}
	*/
	
	public double getScore(double alpha)
	{
		double sizeWeight = Math.pow(nbElements, alpha);
		return sizeWeight*(diffSum/nbElements-Manifest.dataset.getMean());
	}
	
	public String toString()
	{
		String str = "";
		String attribut = "";
		for(Interval inter : intervals)
		{
			if(!inter.getAttribut().equals(attribut))
			{
				attribut = inter.getAttribut();
				if(!str.equals(""))
				{
					str+="\t";
				}
				str+= attribut + " : ";
			}
			else
			{
				str+= " U ";
			}
			str+=inter;
		}
		return str + " : \t" + getScore(0.5) + "(" + diffSum + " " + nbElements + ")";
	}
	
	public int nbHole()
	{
		int nbTrou = 0;
		int nbInterMax = 0;
		Hashtable<String, Integer> countInter = new Hashtable<String, Integer>();
		for(Interval inter : intervals)
		{
			String attribut = inter.getAttribut();
			Integer nbInter = countInter.get(attribut);
			if(nbInter == null)
			{
				nbInter=0;
			}
			nbInter+=inter.getNbPart();
			countInter.put(attribut, nbInter);
			if(nbInter>nbInterMax)
			{
				nbInterMax=nbInter;
			}
		}
		nbTrou=nbInterMax-1;
		return nbTrou;
	}
}
