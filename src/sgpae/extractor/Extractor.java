package sgpae.extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.management.BadAttributeValueExpException;

import sgpae.extractor.data.DataSet;
import sgpae.extractor.data.Transaction;
import sgpae.extractor.data.Valeur;

public class Extractor
{
	public static DataSet retrieveData(String fileName) throws FileNotFoundException, BadAttributeValueExpException
	{
		
		ArrayList<String> listAttributs = new ArrayList<String>();
		
		File f = new File(fileName);
		Scanner scan = new Scanner(f);
		String line = scan.next();
		String[] attributs = line.split(",");
		for(String attribut : attributs)
		{
			listAttributs.add(attribut);
		}
		
		DataSet set = new DataSet(listAttributs);
		
		while(scan.hasNext())
		{
			ArrayList<Valeur> valeurs= new ArrayList<Valeur>();
			line = scan.next();
			String[] values = line.split(",");
			for(int i = 0; i < listAttributs.size(); i++)
			{
				//TODO Faire des test pour caster
				String strValue = values[i];
				float value = Float.parseFloat(strValue);
				valeurs.add(new Valeur<Float>(listAttributs.get(i), value));
			}
			Transaction trans = new Transaction(listAttributs, valeurs);
			set.addTransactions(trans);
		}
		
		scan.close();
		return set;
	}
}
