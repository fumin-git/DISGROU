package sgpae.tree.creator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import sgpae.extractor.data.Interval;
import sgpae.extractor.data.Transaction;
import sgpae.manifest.Manifest;

public class FpTreeCreator
{
	private static Method f = setMethod();
	
	private static Method setMethod()
	{
		try
		{
			return FpTreeCreator.class.getMethod("makeTreeV1",ArrayList.class,ArrayList.class,String.class,int.class);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static FpTree makeTree(ArrayList<Interval> selectors, ArrayList<Transaction> transactions, String target, int nbCore) throws InterruptedException
	{
		return makeTreeV1(selectors, transactions, target, nbCore);
		/*
		try
		{
			return (FpTree) f.invoke(FpTreeCreator.class,selectors, transactions,target,nbCore);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		 */
	}
	
	private static FpTree makeTreeV2(ArrayList<Interval> selectors, ArrayList<Transaction> transactions, String target, int nbCore) throws InterruptedException
	{
		FpTree tree = new FpTree(selectors, target);
		return null;
	}
	
	public static FpTree makeTreeV1(ArrayList<Interval> selectors, ArrayList<Transaction> transactions, String target, int nbCore) throws InterruptedException
	{
		FpTree tree = new FpTree(selectors, target);
		ArrayList<SubTreeCreatorThread> listThread = new ArrayList<SubTreeCreatorThread>();
		for(int i = 0; i < nbCore; i++)
		{
			int indexMin = i*Manifest.dataset.getNbTransactions()/nbCore;
			int indexMax = (i+1)*Manifest.dataset.getNbTransactions()/nbCore;
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
}
