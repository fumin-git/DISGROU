package sgpae.extractor.data;

public class Valeur<T extends Number>
{
	private final String attribut;
	private final T value;
	
	public Valeur(String attribut, T value)
	{
		super();
		this.attribut = attribut;
		this.value = value;
	}

	public String getAttribut()
	{
		return attribut;
	}

	public T getValue()
	{
		return value;
	}
	
	public String toString()
	{
		String str = "<"+attribut+":"+value+">";
		return str;
	}
}
