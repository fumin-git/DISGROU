package sgpae.extractor.data;

import java.util.ArrayList;
import java.util.HashSet;

import sgpae.manifest.Manifest;
import sgpae.manifest.Manifest.Couple;

public class Interval implements Comparable<Interval>
{
	
	private static final int SIMILIPERCENT = 20;
	
	private String attribut;
	private ArrayList<Transaction> transOrd1;
	private ArrayList<Transaction> transOrd2;
	private Float[] mins = new Float[2];
	private Float[] maxs = new Float[2];
	
	public Interval(String attribut)
	{
		super();
		this.attribut = attribut;
		
		this.transOrd1  = new ArrayList<Transaction>();
		this.transOrd2  = new ArrayList<Transaction>();
	}
	
	public Interval(String attribut, DataSet dataset, boolean positive)
	{
		this(attribut);
		if(positive)
		{
			int nbTrans = dataset.getNbPositiveTransactions();
			for(int i = 0;i<nbTrans;i++)
			{
				this.addTransaction(dataset.getPositiveTransactions(i));
			}
		}
		else
		{
			int nbTrans = dataset.getNbTransactions();
			for(int i = 0;i<nbTrans;i++)
			{
				this.addTransaction(dataset.getTransactions(i));
			}			
		}
	}
	
	public Interval(String attribut, DataSet dataset, String target)
	{
		this(attribut);
		dataset.compileData(target);
		int nbTrans = dataset.getNbPositiveTransactions();
		for(int i = 0;i<nbTrans;i++)
		{
			this.addTransaction(dataset.getPositiveTransactions(i));
		}
	}
	
	public void addTransaction(Transaction trans)
	{
		Valeur val = trans.getValue(attribut);
		int indexMax = transOrd1.size();
		if(indexMax!=0)
		{
			int index;
			for(index = 0; (index < transOrd1.size()) && ((Float)(transOrd1.get(index).getValue(attribut).getValue()) < (Float)(val.getValue()) ) ; index++);
			transOrd1.add(index, trans);
		}
		else
		{
			transOrd1.add(trans);
		}
		mins[0]=(Float)(transOrd1.get(0).getValue(attribut).getValue());
		maxs[0]=(Float)(transOrd1.get(indexMax).getValue(attribut).getValue());
		try
		{
			mins[1]=(Float)(transOrd2.get(0).getValue(attribut).getValue());
			maxs[1]=(Float)(transOrd2.get(indexMax).getValue(attribut).getValue());
		}
		catch(IndexOutOfBoundsException e)
		{
			mins[1] = null;
			maxs[1] = null;
		}
		
	}
	
	//TODO test
	public Transaction getTransaction(int index)
	{
		if(index<transOrd1.size())
		{
			return transOrd1.get(index);			
		}
		else
		{
			return transOrd2.get(index-transOrd1.size());
		}
	}
	
	public String listOfTransaction()
	{
		String str = "";
		for(Transaction trans : transOrd1)
		{
			System.out.println(trans);
		}
		for(Transaction trans : transOrd2)
		{
			System.out.println(trans);
		}
		return str;
	}

	//TODO test
	public Interval childFromExternLeft()
	{
		Interval child = new Interval(attribut);
		child.transOrd1 = (ArrayList<Transaction>) this.transOrd1.clone();
		child.transOrd2 = (ArrayList<Transaction>) this.transOrd2.clone();
		child.erosionLeft1();
		
		child.validate();
		
		return child;
	}
	
	//TODO test
	public Interval childFromInnerLeft()
	{
		if(transOrd2.size()==0)
		{
			return null;
		}
		Interval child = new Interval(attribut);
		child.transOrd1 = (ArrayList<Transaction>) this.transOrd1.clone();
		child.transOrd2 = (ArrayList<Transaction>) this.transOrd2.clone();
		child.erosionLeft2();
		
		child.validate();
		
		return child;
	}
	
	//TODO test
	public Interval childFromExtenRight()
	{
		if(transOrd2.size()==0)
		{
			return null;
		}
		Interval child = new Interval(attribut);
		child.transOrd1 = (ArrayList<Transaction>) this.transOrd1.clone();
		child.transOrd2 = (ArrayList<Transaction>) this.transOrd2.clone();
		child.erosionRight2();
		
		child.validate();
		
		return child;
	}
	
	//TODO test
	public Interval childFromInnerRight()
	{
		Interval child = new Interval(attribut);
		child.transOrd1 = (ArrayList<Transaction>) this.transOrd1.clone();
		child.transOrd2 = (ArrayList<Transaction>) this.transOrd2.clone();
		child.erosionRight1();
		
		child.validate();
		
		return child;
	}
	
	public boolean isTransactionValid(Transaction trans)
	{
		boolean resultat = false;
		for(int i = 0; i < mins.length; i++)
		{
			
			if( (mins[i]!=null) && (maxs[i]!=null) )
			{
				resultat = resultat || (((Float)(trans.getValue(attribut).getValue()) >= mins[i]) && ((Float)(trans.getValue(attribut).getValue()) <= maxs[i]));
				//System.out.println((Integer)(trans.getValue(attribut).getValue()) >= mins[i]);
				//System.out.println((Integer)(trans.getValue(attribut).getValue()) <= maxs[i]);
			}
		}
		return resultat;
	}
	
	private void erosionLeft1()
	{
		float min = (Float) transOrd1.get(0).getValue(attribut).getValue();
		while(transOrd1.get(0).getValue(attribut).getValue().equals(min))
		{
			transOrd1.remove(0);
			if(transOrd1.size()==0)
			{
				break;
			}
		}
	}
	
	private void erosionRight1()
	{
		int indexMax = transOrd1.size()-1;
		float max = (Float) transOrd1.get(indexMax).getValue(attribut).getValue();
		while(transOrd1.get(indexMax).getValue(attribut).getValue().equals(max))
		{
			transOrd1.remove(indexMax);
			indexMax--;
			if(transOrd1.size()==0)
			{
				break;
			}
		}
	}
	
	private void erosionLeft2()
	{
		float min = (Float) transOrd2.get(0).getValue(attribut).getValue();
		while(transOrd2.get(0).getValue(attribut).getValue().equals(min))
		{
			transOrd2.remove(0);
			if(transOrd2.size()==0)
			{
				break;
			}
		}
	}
	
	private void erosionRight2()
	{
		int indexMax = transOrd2.size()-1;
		float max = (Float) transOrd2.get(indexMax).getValue(attribut).getValue();
		while(transOrd2.get(indexMax).getValue(attribut).getValue().equals(max))
		{
			transOrd2.remove(indexMax);
			indexMax--;
			if(transOrd2.size()==0)
			{
				break;
			}
		}
	}

	//TODO test
	//TODO faire fonctionner les intervals "binaire"
	public ArrayList<Interval> makeGeration1()
	{
		ArrayList<Interval> listG1 = new ArrayList<Interval>();
		if(attribut.equals("gender"))
		{
			Interval inter1 = new Interval(attribut);
			Interval inter2 = new Interval(attribut);
			for(Transaction trans : transOrd1)
			{
				if(trans.getValue("gender").getValue().floatValue()==1)
				{
					inter1.addTransaction(trans);
				}
				else
				{
					inter2.addTransaction(trans);;
				}
			}
			listG1.add(inter1);
			listG1.add(inter2);
			System.out.println(inter1);
			System.out.println(inter2);
			
		}
		float interdit = (float) transOrd1.get(0).getValue(attribut).getValue();
		int indexOk = 0;
		int indexPrec=0;
		while(indexOk<transOrd1.size())
		{
			interdit = (float) transOrd1.get(indexOk).getValue(attribut).getValue();
			while( (indexOk < transOrd1.size()) && ((float)(transOrd1.get(indexOk).getValue(attribut).getValue())==interdit) )
			{
				indexOk++;
			}
			Interval inter = new Interval(attribut);
			inter.transOrd1.addAll(transOrd1.subList(0,indexPrec));
			inter.transOrd2.addAll(transOrd1.subList(indexOk,transOrd1.size()));
			inter.validate();
			if(!inter.mins[0].equals(inter.maxs[0]))
			{
				if( (inter.transOrd2.size()==0) || (!inter.mins[1].equals(inter.maxs[1])))
				{
					listG1.add(inter);
				}
			}
			indexPrec = indexOk;
		}
		
		return listG1;
	}
	
	private void validate()
	{
		int size1 = transOrd1.size();
		int size2 = transOrd2.size();
		if(size1!=0)
		{
			mins[0]=(Float) transOrd1.get(0).getValue(attribut).getValue();
			maxs[0]=(Float) transOrd1.get(size1-1).getValue(attribut).getValue();
			if(size2!=0)
			{
				mins[1]=(Float) transOrd2.get(0).getValue(attribut).getValue();
				maxs[1]=(Float) transOrd2.get(size2-1).getValue(attribut).getValue();
			}
			else
			{
				mins[1]=null;
				maxs[1]=null;
			}
		}
		else if(size2!=0)
		{
			transOrd1=transOrd2;
			transOrd2=new ArrayList<Transaction>();
			mins[1]=null;
			maxs[1]=null;
			mins[0]=(Float) transOrd1.get(0).getValue(attribut).getValue();
			maxs[0]=(Float) transOrd1.get(size2-1).getValue(attribut).getValue();
		}
		else
		{
			mins[0]=null;
			maxs[0]=null;
			mins[1]=null;
			maxs[1]=null;
		}
	}
	
	//TODO test
	public ArrayList<Interval> makeChildren()
	{
		ArrayList<Interval> children = new ArrayList<Interval>();
		ArrayList<Interval> tmp = new ArrayList<Interval>();
		tmp.add(this.childFromExternLeft());
		tmp.add(this.childFromInnerRight());
		if(transOrd2.size()!=0)
		{
			tmp.add(this.childFromInnerLeft());
			tmp.add(this.childFromExtenRight());
		}
		for(Interval inter : tmp)
		{
			if(!inter.mins[0].equals(inter.maxs[0]))
			{
				//if( (transOrd2.size()==0) || (!inter.mins[1].equals(inter.maxs[1])))
				boolean equilibre = inter.transOrd2.size() < 5*inter.transOrd1.size();
				equilibre = equilibre && (inter.transOrd1.size() < 5*inter.transOrd2.size());
				if( (transOrd2.size()==0) || (equilibre) )
				{
					//System.out.println(inter.transOrd1.size()+ " " +inter.transOrd2.size());
					children.add(inter);
				}
			}
		}
		return children;
	}
	
	public int getNbElement()
	{
		//System.out.println(transOrd1.size()+ " " +transOrd2.size());
		return transOrd1.size()+transOrd2.size();
	}
	
	public static void EROSION(Interval interval, ArrayList<Interval> selectors, int tailleMin, HashSet<String> hash)
	{
		if(interval.getAttribut().equals("gender"))
		{
			return;
		}
		ArrayList<Interval> children = interval.makeChildren();
		for(Interval child : children)
		{
			if(child.getNbElement()>=tailleMin)
			{
				selectors.remove(interval);
				if(!hash.contains(child.toString()))
				{
					hash.add(child.toString());
					selectors.add(child);
					EROSION(child, selectors, tailleMin, hash);					
				}
			}
		}
	}
	
	public String toString()
	{
		if(mins[0]!=null)
		{
			String str = "[";
			float min = mins[0];
			float max = maxs[0];
			str+=min+";"+max;
			if(mins[1]!=null)
			{
				str+="][";
				min = mins[1];
				max = maxs[1];
				str+=min+";"+max;
			}
			return str+"]";
		}
		else
		{
			return "";
		}
	}
	
	public static String toKey(ArrayList<Interval> list)
	{
		String str = "";
		return "";
	}
	
	public static Couple<Float,Float> subfusion(float min1, float max1, float min2, float max2)
	{
		if( (min1>max2) || (min2 > max1) )
		{
			return null;
		}
		float min;
		float max;
		if(min1<min2)
		{
			min = min2;
		}
		else
		{
			min = min1;
		}
		if(max1>max2)
		{
			max=max2;
		}
		else
		{
			max=max1;
		}
		return new Couple<Float, Float>(min,max);
	}
	
	public ArrayList<Interval> fusion(Interval inter)
	{
		ArrayList<Interval> intervals = new ArrayList<Interval>();
		Interval tmp=new Interval(attribut);
		Couple<Float, Float> res1=subfusion(mins[0], maxs[0], inter.mins[0], inter.maxs[0]);
		try
		{
			tmp.mins[0]=res1.valeur1;
			tmp.maxs[0]=res1.valeur2;
//			System.out.println("[" + mins[0] + ";" + maxs[0] + "] & [" + inter.mins[0] + ";" + inter.maxs[0] + "] ");
//			System.out.println(res1.valeur1 + " " + res1.valeur2);
			intervals.add(tmp);
		}
		catch(NullPointerException e)
		{
		}
		Couple<Float, Float> res2;
		Couple<Float, Float> res3;
		Couple<Float, Float> res4;
		if(mins[1]!=null)
		{
			res2=subfusion(mins[1], maxs[1], inter.mins[0], inter.maxs[0]);
			tmp=new Interval(attribut);
			try
			{
				tmp.mins[0]=res2.valeur1;
				tmp.maxs[0]=res2.valeur2;
				intervals.add(tmp);
			}
			catch(NullPointerException e)
			{
			}
			if(inter.mins[1]!=null)
			{
				res3=subfusion(mins[1], maxs[1], inter.mins[1], inter.maxs[1]);
				tmp=new Interval(attribut);
				try
				{
					tmp.mins[0]=res3.valeur1;
					tmp.maxs[0]=res3.valeur2;
					intervals.add(tmp);
				}
				catch(NullPointerException e)
				{
				}
			}
		}
		if(inter.mins[1]!=null)
		{
			res4=subfusion(mins[0], maxs[0], inter.mins[1], inter.maxs[1]);
			tmp=new Interval(attribut);
			try
			{
				tmp.mins[0]=res4.valeur1;
				tmp.maxs[0]=res4.valeur2;
				intervals.add(tmp);
			}
			catch(NullPointerException e)
			{
			}
		}
		return intervals;
	}
	
	public ArrayList<Interval> fusion(ArrayList<Interval> inters)
	{
		ArrayList<Interval> intervals = new ArrayList<Interval>();
		Manifest.debugPrint(this + " : ");
		for(Interval inter : inters)
		{
			Manifest.debugPrint(inter);
			ArrayList<Interval> tmp = this.fusion(inter);
			Manifest.debugPrint(tmp);
			intervals.addAll(tmp);
		}
		return intervals;
	}

	public String getAttribut()
	{
		return attribut;
	}

	public void setAttribut(String attribut)
	{
		this.attribut = attribut;
	}

	@Override
	public int compareTo(Interval inter)
	{
		int compareAttribut = this.attribut.compareTo(inter.attribut);
		if(compareAttribut!=0)
		{
			return compareAttribut;
		}
		if(this.mins[0]<inter.mins[0])
		{
			return -1;
		}
		if(this.mins[0]>inter.mins[0])
		{
			return 1;
		}
		if(this.maxs[0]<inter.maxs[0])
		{
			return -1;
		}
		if(this.maxs[0]>inter.maxs[0])
		{
			return 1;
		}
		
		if(this.mins[1]==null)
		{
			if(inter.mins[1]==null)
			{
				return 0;
			}
			return -1;
		}
		if(inter.mins[1]==null)
		{
			return 1;
		}
		
		if(this.mins[1]<inter.mins[1])
		{
			return -1;
		}
		if(this.mins[1]>inter.mins[1])
		{
			return 1;
		}
		if(this.maxs[1]<inter.maxs[1])
		{
			return -1;
		}
		if(this.maxs[1]>inter.maxs[1])
		{
			return 1;
		}
		return 0;	
	}

	//TODO améliorer le parcours des transactions
	public boolean isSimilar(Interval otherInterval)
	{	
		ArrayList<Transaction> part1 = new ArrayList<Transaction>();
		part1.addAll(transOrd1);
		part1.addAll(transOrd2);
		
		ArrayList<Transaction> part2 = new ArrayList<Transaction>();
		part2.addAll(otherInterval.transOrd1);
		part2.addAll(otherInterval.transOrd2);
		
		ArrayList<Transaction> dif1 = (ArrayList<Transaction>) part1.clone();
		dif1.removeAll(part2);
		
		ArrayList<Transaction> dif2 = (ArrayList<Transaction>) part2.clone();
		dif2.removeAll(part1);
		
		return (part1.size()*SIMILIPERCENT/100 > dif1.size()) && (part2.size()*SIMILIPERCENT/100 > dif2.size());
		
	}
	
	
	/**
	 * @param list : liste des selectors à filtrer
	 * @return liste des sélecteurs en retirant ceux qui ont une constitution trop similaire
	 */
	public static ArrayList<Interval> filterSimilar(ArrayList<Interval> list)
	{
		ArrayList<Interval> returnedList = (ArrayList<Interval>) list.clone();
		
		for(int i = 0; i < returnedList.size(); i++)
		{
			Interval selector = returnedList.get(i);
			for(int j = i+1; j < returnedList.size();)
			{
				Interval candidat = returnedList.get(j);
				if(candidat.isSimilar(selector))
				{
					returnedList.remove(j);
					continue;
				}
				j++;
			}
		}
		
		return returnedList;
	}
	
	public int getNbPart()
	{
		if(maxs[1]!=null)
		{
			return 2;
		}
		return 1;
	}
}
