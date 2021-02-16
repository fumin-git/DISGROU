package sgpae.extractor.data;

import java.util.ArrayList;

public class DataSet
{
	private final ArrayList<String> listAttributs;
	private final ArrayList<Transaction> listTransactions;
	private ArrayList<Transaction> positiveTransactions;
	private String target;
	private Float targetMean;
	
	public DataSet(ArrayList<String> listAttributs)
	{
		super();
		this.listAttributs = listAttributs;
		this.listTransactions = new ArrayList<Transaction>();
	}

	public String getAttributs(int index)
	{
		return listAttributs.get(index);
	}
	public int getNbAttributs()
	{
		return listAttributs.size();
	}

	public void addTransactions(Transaction trans)
	{
		listTransactions.add(trans);
	}
	public Transaction getTransactions(int index)
	{
		return listTransactions.get(index);
	}
	public ArrayList<Transaction> getTransactions()
	{
		return (ArrayList<Transaction>) listTransactions.clone();
	}
	public int getNbTransactions()
	{
		return listTransactions.size();
	}

	
	public Transaction getPositiveTransactions(int index)
	{
		return positiveTransactions.get(index);
	}
	public int getNbPositiveTransactions()
	{
		return positiveTransactions.size();
	}

	
	public void compileData(String target)
	{
		this.target = target;
		float moyenne = 0.0f;
		int count = 0;
		for(Transaction trans : listTransactions)
		{
			//TODO le faire marcher pour les float
			moyenne+=(float)(trans.getValue(target).getValue());
			count++;
		}
		moyenne /= count;
		targetMean = moyenne;
		
		positiveTransactions = new ArrayList<Transaction>();
		
		for(Transaction trans : listTransactions)
		{
			if((float)trans.getValue(target).getValue()>= targetMean)
			{
				positiveTransactions.add(trans);
			}
		}
	}
	
	public String listAttributToString()
	{
		String str = "------------\nListe Attributs\n------------\n";
		for(String attribut : listAttributs)
		{
			str += attribut+"\n";
		}
		str+= "------------";
		return str;
	}
	public String toString()
	{
		String str = "";
		for(Transaction trans : listTransactions)
		{
			str += trans + "\n";
		}
		return super.toString();
	}
	
	public float getMean()
	{
		return targetMean;
	}
	
	public String getTarget()
	{
		return target;
	}
}
