package haiku.top.model;

public class WordAndNumber {
	private String word;
	private int numberOf;
	
	public WordAndNumber(String word){
		this.word = word;
		this.numberOf = 1; // always start at 1;
	}
	
	public WordAndNumber(String word, int number){
		this.word = word;
		this.numberOf = number;
	}
	
	public void increase(){
		numberOf++;
	}
	
	public void increase(int number){
		numberOf += number;
	}
	
	/**
	 * Decreases the object's counter and checks if it should be removed
	 * @return true if numberOf has been decreased to 0, false otherwise
	 */
	public boolean decrease(){
		numberOf--;
		return numberOf == 0;
	}
	
	/**
	 * Decreases the object's counter and checks if it should be removed
	 * @return true if numberOf has been decreased to 0, false otherwise
	 */
	public boolean decrease(int number){
		numberOf -= number;
		return numberOf <= 0;
	}
	
	public String getWord(){
		return word;
	}
	
	public int getNumberOf(){
		return numberOf;
	}
}
