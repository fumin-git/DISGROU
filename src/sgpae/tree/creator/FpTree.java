package sgpae.tree.creator;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Hashtable;

import sgpae.extractor.data.Interval;
import sgpae.extractor.data.Transaction;

public class FpTree
{
	private final Node racine;
	
	private final ArrayList<Interval> selectors;
	
	private final Hashtable<String, ArrayList<Node>> selectorsPos;

	private final String target;
	
	public FpTree(ArrayList<Interval> selectors, String target)
	{
		super();
		this.racine = new Node(target);
		this.selectors = selectors;
		selectorsPos = new Hashtable<String, ArrayList<Node>>();
		for(Interval selector : selectors)
		{
			String label = selector.toString();
			ArrayList<Node> newListNode = new ArrayList<Node>();
			selectorsPos.put(label, newListNode);
		}
		this.target = target;
	}
	
	/**SemiBrute Method*/
	public void addTransactionV2_1(Transaction trans)
	{
		
	}
	
	public void addTransaction(Transaction trans)
	{
		Node actNode = racine;
		for(Interval selector : selectors)
		{
			boolean isValid = selector.isTransactionValid(trans);
			if(isValid)
			{
				boolean exist=false;
				for(Node child : actNode.getChildren())
				{
					if(child.getInterval().equals(selector))
					{
						System.out.println("Treated : "+trans.getID() + " with " + selector);
						child.addOne(trans);
						exist=true;
						actNode=child;
						break;
					}
				}
				if(!exist)
				{
					Node child = new Node(actNode, selector, trans, target);
					actNode.addChildren(child);
					ArrayList<Node> listNode = selectorsPos.get(selector.toString());
					listNode.add(child);
					actNode=child;
				}
			}
			//System.out.println( trans + " -> "+ selector + " : " +  isValid);
		}
	}
	
	
	public void addTree(FpTree otherTree)
	{
		this.racine.addNode(otherTree.racine);
	}
	
	public String selectorsToString()
	{
		String str = "";
		for(Interval inter : selectors)
		{
			System.out.println(inter);
		}
		return str;
	}
	
	public String toString()
	{
		return racine.toString(0);
	}

	public ArrayList<Node>getSelectorPos(String str)
	{
		try
		{
			ArrayList<Node> selectorPos = (ArrayList<Node>) selectorsPos.get(str).clone(); 
			return selectorPos;
		}
		catch(NullPointerException e)
		{
			return null;
		}
	}
}
