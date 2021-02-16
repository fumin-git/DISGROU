package sgpae.manifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;

import javax.management.BadAttributeValueExpException;

import sgpae.Subgroup;
import sgpae.extractor.Extractor;
import sgpae.extractor.SelectorExtractionThread;
import sgpae.extractor.data.DataSet;
import sgpae.extractor.data.Interval;
import sgpae.extractor.data.Transaction;
import sgpae.tree.creator.FpTree;
import sgpae.tree.creator.FpTreeCreator;
import sgpae.tree.creator.Node;
import sgpae.tree.creator.SubTreeCreatorThread;

public class Manifest
{
	/**Objet dataset contenant toute les informations de la base de donnée*/
	public static DataSet dataset;
	
	//TODO : Définir ce truc
	public static int counterTest = 0;
	
	/**Objet final pour définir la variable debugMod, i.e. pas d'affichage de mode débug*/
	public static final int DEBUG_FALSE = 0;
	/**Objet final pour définir la variable debugMod, i.e. affichage du texte uniquement dans le mode débug*/
	public static final int DEBUG_PART = 1;
	/**Objet final pour définir la variable debugMod, i.e. affichage du texte, du nom du fichier et du numéro de ligne dans le mode débug*/
	public static final int DEBUG_TRUE = 2;
	
	/**Permet l'affichage des texte à afficher en mode debug via la fonction debugPrint*/
	private static int debugMod=DEBUG_FALSE;
	
	private static Hashtable<String, ArrayList<ArrayList<Interval>>> tableSelector = new Hashtable<String, ArrayList<ArrayList<Interval>>>();
	
	private static String debugText(String str)
	{
		if(debugMod==DEBUG_TRUE)
		{
			StackTraceElement getLine = Thread.currentThread().getStackTrace()[2];
			str = "<"+getLine.getFileName() + " - Line " + getLine.getLineNumber() + "> : " + str;
		}
		return str;
	}
	
	public static void debugPrint(String str, int debugMod, boolean erreur)
	{
		if(debugMod!=DEBUG_FALSE)
		{
			str = debugText(str);
			if(erreur)
			{
				System.err.println(str);
			}
			else
			{
				System.out.println(str);
			}
		}
	}
	
	public static void debugPrint(String str, int debugMod)
	{
		debugPrint(str, debugMod, false);
	}
	
	public static void debugPrint(String str)
	{
		debugPrint(str, debugMod);
	}
	
	public static void debugPrint(Object obj)
	{
		debugPrint(obj.toString());
	}
	
	/**
	 * 
	 * @param args
	 * @throws BadAttributeValueExpException
	 * @throws InterruptedException 
	 * @throws IOException 
	 * 
	 * The first parameter need to be the path to the data file
	 * The second one have to be the label of the target attribute
	 * The third attribute need to be a threshold beta to make the 1-intervalles
	 * The fourth parameters have to be the number of core
	 * 
	 * data/Body.csv heart_rate 0.5 4
	 * data/SubGroupTest.csv Tonnage 0.5 4
	 */
	
	public static void main(String[] args) throws BadAttributeValueExpException, InterruptedException, IOException
	{
		
		StackTraceElement getLine = Thread.currentThread().getStackTrace()[1];
		
		/** Seuil beta que doivent dépasser les 1-intervals*/
		float beta;
		
		/**Label de la target variable*/
		String target;
		/**Nombre de coeur utilisés*/
		int nbCore;
		
		//Si les arguments sont incorect, utilisation des valeurs par défauts
		//*
		if(args.length!=4)
		{
			System.err.println("Wrong number of arguments, please refer to the README");
			System.exit(0);
			return;
		}
		//Sinon utilisation normale des paramètres
		else
		{//*/
			target = args[1];
			dataset = Extractor.retrieveData(args[0]);
			dataset.compileData(args[1]);
			beta=Float.parseFloat(args[2]);
			nbCore = Integer.parseInt(args[3]);
		//
		}
		
		/**Liste des sélecteur (ou 1 interval)*/
		ArrayList<Interval> selectors = make1Intervals(beta,target);
		
		selectors = Interval.filterSimilar(selectors);
		
		if(debugMod!=DEBUG_FALSE)
		{
			debugPrint("nb Selectors : " + selectors.size());
		}


		/**FP-Tree généré pour la recherche de sous-groupe*/
		FpTree tree = FpTreeCreator.makeTreeV1(selectors, dataset.getTransactions(), target, 1);
		
		
		/**Liste des éléments qui ont déjà été traité sur la branche actuelle, afin d'éviter de les compter 2 fois*/
		ArrayList<String> listElement = new ArrayList<String>();
		
		/**Hashtable contenant, pour chaque sous-group*/
		Hashtable<String, Subgroup> counter = new Hashtable<String, Subgroup>();
		
		ArrayList<Subgroup> subgroups = finish(selectors, tree, listElement, counter);
		
				
		System.out.println("nbSubGroup = " + subgroups.size());
		
		File f = new File("Results.txt");
		FileOutputStream fos = new FileOutputStream(f);
		int nbPrint = 0;
		for(int j = 0; j < subgroups.size(); j++)
		{
			if(nbPrint<10)
			{
				if((subgroups.get(j).intervals.size()!=0)&&(subgroups.get(j).nbHole()<2))
				{
					fos.write( (subgroups.get(j)+"\n").getBytes());
					System.out.println(subgroups.get(j) + " " + j);// + " " + subgroups.get(j).falseKey);
					nbPrint++;
				}
			}
		}
	}
	
	public static ArrayList<Interval> combinedSelectors(ArrayList<Interval> rawSelectors)
	{
		class labeledInterval
		{
			public String label;
			public Interval interval;
			
			public Interval convertToInteval()
			{
				return interval;
			}
			
			public ArrayList<Interval> convertToInteval(ArrayList<labeledInterval> labeledList)
			{
				ArrayList<Interval> unlabeled = new ArrayList<Interval>();
				for(labeledInterval labeled : labeledList)
				{
					unlabeled.add(labeled.convertToInteval());
				}
				return unlabeled;
			}
		}
		/***/
		
		HashSet<String> selectorList = new HashSet<String>();
		ArrayList<labeledInterval> refinedSelectors = new ArrayList<labeledInterval>();
		for(int i=0;i<rawSelectors.size();i++)
		{
			
		}
		ArrayList<Interval> combinedSelectors = new ArrayList<Interval>();
		return combinedSelectors;
	}
	
	public static void newScoreCalculator()
	{
		debugPrint("Place restance");
		debugPrint(Runtime.getRuntime().freeMemory()/(1024.0*1024*1024) + "/" + Runtime.getRuntime().totalMemory()/(1024.0*1024*1024));
	}
	
	public static ArrayList<Subgroup> finish(ArrayList<Interval> selectors, FpTree fptree, ArrayList<String> listElement, Hashtable<String, Subgroup> counter)
	{
		ArrayList<Subgroup> subgroups = new ArrayList<Subgroup>();
		HashSet<String> UsedTransaction = new HashSet<String>();
		long nbTurn = 0;
		for(int i = selectors.size()-1; i >= 0; i--)
		{
			debugPrint(nbTurn + " " + selectors.size());
			nbTurn++;
			Interval selector = selectors.get(i);
			//System.out.println("Selector : " + selector);
			//System.out.println("Selector : " + selector.toString() + "("+i+"/"+selectors.size()+")");
			String intervalStr = selector.toString();
			int nbNode = fptree.getSelectorPos(intervalStr).size();
			//System.out.println(nbNode);
			for(Node actNode : fptree.getSelectorPos(intervalStr))
			{
				int nbApparition = actNode.getNbApparition();
				float score = actNode.getScore();
				ArrayList<Interval> listOnBranch = new ArrayList<Interval>();
				Node parent = actNode.getParent();
				while(parent.getInterval()!=null)
				{
					listOnBranch.add(0,parent.getInterval());
					parent=parent.getParent();
				}
				/*
				if(i<90)
				{
					System.out.println(selectors.size());
					continue;
				}
				//*/
				//System.out.println(listOnBranch.size());
				ArrayList<String> actList = new ArrayList<String>();
				newScoreCalculator();
				ArrayList<ArrayList<Interval>> listIntevals = Manifest.addScoreOfBranch(listOnBranch, 0, actList);
				for(ArrayList<Interval> tmpInters : listIntevals)
				{
					ArrayList<Interval> inters = Subgroup.sortInterval(tmpInters);
					String key = "";
					String falseKey = "";
					int count1=0;
					for(Interval inter : inters)
					{
						falseKey+=inter.toString();
						if(inter.getAttribut()==selector.getAttribut())
						{
							try
							{
								ArrayList<Interval> resFusion = inter.fusion(selector);
								for(Interval resUnit : resFusion)
								{
									key+=resUnit;
								}
								
								count1+=1;
								//System.out.println("Intervals : " + inter);
							}
							catch(IndexOutOfBoundsException e)
							{
								System.err.println("Erra");
							}
						}
						else
						{
							//System.out.println("----------------\n" + inter.getAttribut() + " " + selector.getAttribut() + "----------------\n");
							key+=inter;
						}
					}
					if(count1==0)
					{
						key+=selector;
					}
					
					
					//System.out.println("  "+ key + " : " + score + "/" + nbApparition);
					Subgroup count = counter.get(key);
//					System.out.println("'" + key + "'");
//					System.out.println("'" + falseKey + "'");
//					System.out.println();
					if(count==null)
					{
						count = new Subgroup(inters,0,0);
						counter.put(key,count);
						listElement.add(key);
						count.falseKey=falseKey;
						//System.out.println("The Key : " + key);
					}
					for(Transaction trans : actNode.getTransaction())
					{
						String secondKey=trans.getID() + " " + key;
						if(!UsedTransaction.contains(secondKey))
						{
							//System.out.println(secondKey);
//							count.add((float) trans.getValue(Manifest.dataset.getTarget()).getValue(), 1);
							count.add(trans);
							UsedTransaction.add(secondKey);
						}
					}
				}
			}			
		}
		System.out.println("Création subGroup");
		nbTurn = 0;
		subgroupLoop : for(String subGroupStr : listElement)
		{
			if(nbTurn%10==0)
			{
				float percent = 1.0f*nbTurn/listElement.size();
				percent = (int)(10000*percent)/100.0f;
				System.out.println(percent+"%");
			}
			nbTurn++;
			Subgroup subgroup = counter.get(subGroupStr);
			int j;
			positionInFinalLoop : for(j=0; j < subgroups.size(); j++)
			{
				Subgroup actSubG = subgroups.get(j);
				
				if(actSubG.getScore(0.5)<subgroup.getScore(0.5))
				{
					break positionInFinalLoop; 
				}
				else if(j>10000)
				{
					continue subgroupLoop;
				}
			}
			subgroups.add(j,subgroup);
		}
		return subgroups;
	}
	
	public static FpTree makeTree(ArrayList<Interval> selectors, ArrayList<Transaction> transactions, String target, int nbCore) throws InterruptedException
	{
		FpTree tree = new FpTree(selectors, target);
		ArrayList<SubTreeCreatorThread> listThread = new ArrayList<SubTreeCreatorThread>();
		for(int i = 0; i < nbCore; i++)
		{
			int indexMin = i*dataset.getNbTransactions()/nbCore;
			int indexMax = (i+1)*dataset.getNbTransactions()/nbCore;
			SubTreeCreatorThread thread = new SubTreeCreatorThread(indexMin, indexMax, selectors, target, transactions);
			System.out.println("limiteThread : [" +indexMin + ";" + indexMax+"]");
			listThread.add(thread);
			thread.start();
		}
		for(SubTreeCreatorThread thread : listThread)
		{
			thread.join();
			tree.addTree(thread.tree);
		}
		return listThread.get(0).tree;
	}
	
	public static ArrayList<Interval> make1Intervals(float beta, String target) throws InterruptedException
	{
		int nbAttr = dataset.getNbAttributs();
		
		ArrayList<SelectorExtractionThread> extracThreads = new ArrayList<SelectorExtractionThread>();
		
		ArrayList<Interval> selectors = new ArrayList<Interval>();
		
		for(int i = 0; i < nbAttr; i++)
		{
			String attribut = dataset.getAttributs(i);
			
			debugPrint("The Attribut" + attribut);
			
			if(!attribut.equals(target))
			{
				Interval inter = new Interval(attribut,dataset,Manifest.dataset.getTarget());
				SelectorExtractionThread extracThread = new SelectorExtractionThread(inter,(int)(dataset.getNbPositiveTransactions()*beta));
				extracThreads.add(extracThread);
				extracThread.start();
			}
		}
		//TODO Adapter les threads
		for(SelectorExtractionThread extractThread : extracThreads)
		{
			extractThread.join();
		}
		
		for(SelectorExtractionThread extractThread : extracThreads)
		{
			debugPrint("Test");
			for(Interval inter : extractThread.selectors)
			{
				debugPrint("inter :  " + inter);
				int nbElements = inter.getNbElement();
				if(selectors.size()==0)
				{
					selectors.add(inter);
				}
				else
				{
					int place = 0;
					for(Interval inter2 : selectors)
					{
						if(inter2.getNbElement()<inter.getNbElement())
						{
							break;
						}
						place++;
					}
					selectors.add(place,inter);
				}
			}
		}
		
		return selectors;
	}
	
	/**
	 * @return Une liste de listes d'intervalles. Chaque sous-list correspond à un sous-groupe
	 */
	public static ArrayList<ArrayList<Interval>> addScoreOfBranch(ArrayList<Interval> intervals, int index, ArrayList<String> actList)
	{
		counterTest++;
		/**List a retourner*/
		ArrayList<ArrayList<Interval>> list = new ArrayList<ArrayList<Interval>>();
		//*
		/**Si on a pas fini la list intervals ...*/
		if(index<intervals.size())
		{
			/**Interval actuel*/
			Interval actInter = intervals.get(index);
			//System.out.println(intervals.get(index));
			/**On parcours l'ensemble des sous-groupes possibles et on ajoute des duplicata avec le nouvel interval*/
			
			for(ArrayList<Interval> subList : addScoreOfBranch(intervals,index+1,actList))
			{
				ArrayList<Interval> newList = new ArrayList<Interval>();
				/**On procède à une fusion des éléments ayant le même attribut*/
				int count=0;
				for(Interval interval : subList)
				{
					if(interval.getAttribut().equals(actInter.getAttribut()))
					{
						newList.addAll(actInter.fusion(interval));
						count++;
					}
					else
					{
						newList.add(interval);
					}
				}
				/**Si il n'y a aucune fusion, on ajouter le nouvel élement*/
				if(count==0)
				{
					newList.add(actInter);
				}
				list.add(newList);
				list.add(subList);
			}
		}
		else
		{
			list.add(new ArrayList<Interval>());
		}
		
		//System.out.println(list.size());
		//*/
		float totalMemory = Runtime.getRuntime().totalMemory()/(1024.0f*1024*1024);
		float freeMemory = Runtime.getRuntime().freeMemory()/(1024.0f*1024*1024);
		

		if(freeMemory < 1)
		{
			debugPrint("");
		}
		
		
		debugPrint(counterTest + " : " + freeMemory +"/"+totalMemory);
		counterTest--;
		return list;
		
	}
	
	/*
	public static ArrayList<String> addScoreOfBranch(ArrayList<String> intervals, int index, ArrayList<String> actList)
	{
		ArrayList<String> list = new ArrayList<String>();
		//*
		if(index<intervals.size())
		{
			//System.out.println(intervals.get(index));
			for(String str : addScoreOfBranch(intervals,index+1,actList))
			{
				list.add(str);
				list.add(intervals.get(index) + str);
			}
		}
		else
		{
			list.add("");
		}
		//System.out.println(list.size());
		//*
		return list;
	}
	//*/
	
	public static class Couple<T,U>
	{
		public T valeur1;
		public U valeur2;
		
		public Couple(T valeur1, U valeur2)
		{
			this.valeur1 = valeur1;
			this.valeur2 = valeur2;
		}
		
		@Override
		public String toString()
		{
			return "<" + valeur1 + "," + valeur2 + ">";
		}
	}

	
	public static void printWithLine(String str)
	{
		debugPrint(str);
	}
}
