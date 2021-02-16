package sgpae.tree.creator;

import java.util.ArrayList;

import sgpae.extractor.data.Interval;
import sgpae.extractor.data.Transaction;

public class SubTreeCreatorThread extends Thread
{
	public FpTree tree;
	
	private int indexMin;
	private int indexMax;
	private ArrayList<Transaction> transactions;
	
	public SubTreeCreatorThread(int indexMin, int indexMax, ArrayList<Interval> selectors, String target, ArrayList<Transaction> transactions)
	{
		this.indexMax = indexMax;
		this.indexMin = indexMin;
		tree= new FpTree(selectors, target);
		this.transactions = transactions;
	}
	
	@Override
	public void run()
	{
		super.run();
		for(int index = indexMin; index < indexMax; index++)
		{
			Transaction trans = transactions.get(index);
			tree.addTransaction(trans);
		}
	}
}
