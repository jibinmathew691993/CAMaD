/**
 * 
 */
package us.ttic.takeshi.tools;


/**
 * @author takeshi.onishi
 *
 */
public class Pair<T extends Comparable<T>> implements Comparable<Pair<T>>{
	
	public T first;
	public T second;
	
	public Pair(T first_elm, T second_elm){
		first = first_elm;
		second = second_elm;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == null) return false;
		
		if(!this.getClass().isInstance(obj)) return false;
		Pair<T> another = this.getClass().cast(obj);
		if(first.equals(another.first) && second.equals(another.second)) return true;
		return false;
	}
	
	@Override
	public int compareTo(Pair<T> another) {
		if(first.compareTo(another.first)!=0) return first.compareTo(another.first);
		if(second.compareTo(another.second)!=0) return second.compareTo(another.second);
		return 0;
	}

}
