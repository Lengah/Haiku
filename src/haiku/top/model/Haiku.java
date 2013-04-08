package haiku.top.model;

import haiku.top.HaikuActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Haiku {
	private Theme theme;
	private String poem;
	private int rating = -1; // not rated
	private Calendar date = Calendar.getInstance();
	
	public Haiku(Theme theme){
		this.theme = theme;
	}
	
	/**
	 * 
	 * @return DD/MM/YY hh:mm:ss
	 */
	public String getDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return sdf.format(date.getTime());
	}
	
	public String getHaikuPoem(){
		return poem;
	}
	
	public void generate(){
		
		//TEST
		poem = HaikuGenerator.getPartOfSentence("<sentence>");
		while(poem.contains("a/an")){
			int index = poem.indexOf("a/an");
			index += 5;
			if(poem.charAt(index) == 'a' || poem.charAt(index) == 'e' || poem.charAt(index) == 'u' || poem.charAt(index) == 'i' || poem.charAt(index) == 'o'){
				poem = poem.substring(0, index-5) + "an " + poem.substring(index);
			}
			else{
				poem = poem.substring(0, index-5) + "a " + poem.substring(index);
			}
		}
		while(poem.contains(" , ")){
			int index = poem.indexOf(" , ");
			poem = poem.substring(0, index) + ", " + poem.substring(index+3);
		}
		poem = poem.substring(0, 1).toUpperCase() + poem.substring(1);
		// /TEST
		// First row
		
		// Second row
		// Third row
	}
	
	public void rate(int rating){
		this.rating = rating;
	}
	
	/**
	 * -1 betyder att den inte har en rating
	 * @return
	 */
	public int getRating(){
		return rating;
	}
	
	public Theme getTheme(){
		return theme;
	}
}
