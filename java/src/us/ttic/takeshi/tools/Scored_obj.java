package us.ttic.takeshi.tools;

public class Scored_obj<T> implements Comparable<Scored_obj<T>> {
	
	public T obj;
	public double score;

	public Scored_obj(T something, double s){
		score = s;
		obj = something;
	}
	
	@Override
	public int compareTo(Scored_obj<T> o) {
		return (this.score==o.score)? 0 : (this.score>o.score)? 1 : -1;
	}

	@Override
	public String toString(){
		return String.format("%4.5f", score)+" : "+obj.toString();
	}
}
