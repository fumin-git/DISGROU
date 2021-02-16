package sgpae.extractor;

import java.util.ArrayList;
import java.util.HashSet;

import sgpae.extractor.data.Interval;
import sgpae.manifest.Manifest;

public class SelectorExtractionThread extends Thread
{
	public ArrayList<Interval> selectors;
	private Interval baseInterval;
	private int nbMin;
	
	public SelectorExtractionThread(Interval baseInterval, int nbMin)
	{
		selectors = new ArrayList<Interval>();
		this.baseInterval = baseInterval;
		this.nbMin =  nbMin;
		Manifest.debugPrint(baseInterval);
	}

	public void run()
	{
		HashSet<String> hash = new HashSet<String>();
		ArrayList<Interval> children = baseInterval.makeGeration1();
		Manifest.debugPrint("Attribut : " + baseInterval.getAttribut());
		Manifest.debugPrint("NB gen1 : " + children.size());
		if(baseInterval.getAttribut().equals("gender"))
		{
			selectors.addAll(children);
		}
		for(Interval child : children)
		{
			Manifest.debugPrint("new Child : "+child.toString() + " " + child.getNbElement() + "/" + nbMin);
			if(child.getNbElement()>=nbMin)
			{
				selectors.remove(baseInterval);
				if(!hash.contains(child.toString()))
				{
					hash.add(child.toString());
					selectors.add(child);
					Manifest.debugPrint("new Child : "+child.toString());
					Interval.EROSION(child, selectors, nbMin, hash);					
				}
			}
		}
	}
	
	
}
