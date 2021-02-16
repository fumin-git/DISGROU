package sgpae.extractor.data;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.management.BadAttributeValueExpException;

public class Transaction
{
	private static int IDGEN = 0;
	private final int id;
	private final ArrayList<String> listAttributs;
	private final Hashtable<String, Valeur> tableValue;
	
	public Transaction(ArrayList<String> model, ArrayList<Valeur> valeurs) throws BadAttributeValueExpException
	{
		this.id = IDGEN;
		IDGEN++;
		if(model.size() != valeurs.size())
		{
			throw new BadAttributeValueExpException("Size");
		}
		listAttributs = model;
		tableValue = new Hashtable<String, Valeur>();
		for(Valeur val : valeurs)
		{
			String attribut = val.getAttribut();
			if(!listAttributs.contains(attribut))
			{
				throw new BadAttributeValueExpException("Invalid : " + attribut);
			}
			if(tableValue.get(attribut) != null)
			{
				throw new BadAttributeValueExpException("Duplicate");	
			}
			tableValue.put(attribut, val);
		}
	}

	public Valeur getValue(String attribut)
	{
		return tableValue.get(attribut);
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
		String str = id + " : [";
		for(String attribut : listAttributs)
		{
			str += " " + tableValue.get(attribut) + "\t";
		}
		str += "]";
		return str;
	}
	public int getID()
	{
		return id;
	}
}
