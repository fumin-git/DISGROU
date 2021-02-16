package sgpae.tree.creator;

import java.util.ArrayList;
import java.util.Hashtable;

import sgpae.extractor.data.Interval;
import sgpae.extractor.data.Transaction;

public class Node
{
	
	private Node parent;
	private Interval interval;
	private ArrayList<Node> children;
	private int nbApparition;
	private float score;
	private String target;
	private ArrayList<Transaction> listTrans;
	
	public Node(String target)
	{
		super();
		this.parent = null;
		this.interval = null;
		children = new ArrayList<Node>();
		this.target = target;
		nbApparition=0;
		listTrans = new ArrayList<Transaction>();
	}
	
	public Node(Node parent, Interval interval, Transaction trans, String target)
	{
		super();
		this.parent = parent;
		this.interval = interval;
		children = new ArrayList<Node>();
		this.target = target;
		nbApparition=0;
		score = 0;
		listTrans = new ArrayList<Transaction>();
		addOne(trans);
	}

	public Node getParent()
	{
		return parent;
	}
	
	public Interval getInterval()
	{
		return interval;
	}

	public ArrayList<Node> getChildren()
	{
		@SuppressWarnings("unchecked")
		ArrayList<Node> copyChildren=(ArrayList<Node>)children.clone();
		return copyChildren;
	}

	public void addChildren(Node child)
	{
		this.children.add(child);
	}

	public void addOne(Transaction trans)
	{
		score += (Float)(trans.getValue(target).getValue());
		nbApparition++;
		listTrans.add(trans);
	}
	
	public void addNode(Node otherNode)
	{
		/*
		for(Node child : children)
		{
			System.out.println(child.interval.toString() + "->" + child.nbApparition);
		}
		System.out.println("*-*-*-*-*");
		for(Node child : otherNode.children)
		{
			System.out.println(child.interval.toString() + "->" + child.nbApparition);
		}
		System.out.println("************");
		//*/
		if(		((interval==null) && (otherNode.interval==null) ) ||
				(interval.toString().equals(otherNode.interval.toString())))
		{
			nbApparition+=otherNode.nbApparition;
			Hashtable<String, Node> hash = new Hashtable<String, Node>();
			int i = 0;
			outerLoop :
			for(Node otherchild : otherNode.children)
			{
				String otherChildString = otherchild.interval.toString();
				Node child = hash.get(otherChildString);
				if(child!=null)
				{
					child.addNode(otherchild);
					continue outerLoop;
				}
				for(; i < children.size(); i++)
				{
					child = children.get(i);
					String childString = child.interval.toString();
					hash.put(childString, child);
					if(childString.equals(otherChildString))
					{
						child.addNode(otherchild);
						continue outerLoop ;
					}
				}
				addChildren(otherchild);
			}
			
		}
		/*for(Node child : children)
		{
			System.out.println(child.interval + "->" + child.nbApparition);
		}
		*/
	}
	
	public String toString()
	{
		return this.toString(0);
	}
	public String toString(int level)
	{
		
		String str = "";
		for(int i = 0 ; i < level ; i++)
		{
			str+="| ";
		}
		str+=interval+" : " + nbApparition + ", " + score + "\n";
		for(Node child : children)
		{
			if(child!=null)
			{
				str+=child.toString(level+1);
			}
		}
		return str;
	}

	/*public Node getPathTo()
	{
		Node actNode = this;
		int nbApparition = actNode.nbApparition;
		Node ancNode=null;
		while(actNode.interval!=null)
		{
			Node newNode = new Node(null, actNode.interval);
			newNode.nbApparition = nbApparition;
			if(ancNode!=null)
			{
				ancNode.parent = newNode;
				newNode.children.add(ancNode);
			}
			ancNode = newNode;
			actNode = actNode.parent;
			
		}
		return ancNode;
	}
	*/
	
	public int getNbApparition()
	{
		return nbApparition;
	}
	
	public float getScore()
	{
		return score;
	}

	public ArrayList<Transaction> getTransaction()
	{
		return (ArrayList<Transaction>) listTrans.clone();
	}
}
