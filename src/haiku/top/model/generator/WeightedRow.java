package haiku.top.model.generator;

public class WeightedRow {
	private int row;
	private int weight;
	
	public WeightedRow(int row, int weight){
		this.row = row;
		this.weight = weight;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getWeight(){
		return weight;
	}
}
