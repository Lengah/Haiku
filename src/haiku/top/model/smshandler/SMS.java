package haiku.top.model.smshandler;

import haiku.top.HaikuActivity;
import haiku.top.model.Word;
import haiku.top.model.date.Month;
import haiku.top.model.date.YearMonth;
import haiku.top.model.generator.HaikuGenerator;
import haiku.top.view.date.DateView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class SMS {
	private long id;
	private String message;
	private String date;
	private long contactID;
	private boolean sent = false; // if false -> received
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private ArrayList<Word> wordsInSms;
	
	private ArrayList<String> notRealWords;
	
	public SMS(long id, String message, String date, long contactID, String sentString){
		this.id = id;
		this.message = message;
		this.date = date;
		this.contactID = contactID;
		if (!sentString.equals("1")) {
	        sent = true;
	    }
	}
	
	public SMS(long id, String message, String date, long contactID, boolean sent){
		this.id = id;
		this.message = message;
		this.date = date;
		this.contactID = contactID;
		this.sent = sent;
	}
	
	public boolean isSent(){
		return sent;
	}
	
	public ArrayList<Word> getWords(){
		if (wordsInSms == null)
			wordsInSms = HaikuActivity.databaseHandler.initSMS(this);
		return wordsInSms;
	}
	
	public ArrayList<String> getNotRealWords(){
		if(notRealWords == null){
			initNotRealWords();
		}
		return notRealWords;
	}
	
	public void setNotRealWords(ArrayList<String> notRealWords){
		this.notRealWords = new ArrayList<String>(notRealWords);
	}
	
	private void initNotRealWords(){
		notRealWords = HaikuGenerator.getWords(message);
//		notRealWords = new ArrayList<String>();
//		String textMessage = message.toLowerCase();
//		String word;
//		int pos1;
//		int pos2;
//		while(textMessage.length() > 0){
//			// Remove symbols from the start
//			pos1 = 0;
//			while(textMessage.charAt(pos1) != 'a' && textMessage.charAt(pos1) != 'b' && textMessage.charAt(pos1) != 'c' && textMessage.charAt(pos1) != 'd'
//				 && textMessage.charAt(pos1) != 'e' && textMessage.charAt(pos1) != 'f' && textMessage.charAt(pos1) != 'g' && textMessage.charAt(pos1) != 'h'
//				 && textMessage.charAt(pos1) != 'i' && textMessage.charAt(pos1) != 'j' && textMessage.charAt(pos1) != 'k' && textMessage.charAt(pos1) != 'l'
//				 && textMessage.charAt(pos1) != 'm' && textMessage.charAt(pos1) != 'n' && textMessage.charAt(pos1) != 'o' && textMessage.charAt(pos1) != 'p'
//				 && textMessage.charAt(pos1) != 'q' && textMessage.charAt(pos1) != 'r' && textMessage.charAt(pos1) != 's' && textMessage.charAt(pos1) != 't'
//				 && textMessage.charAt(pos1) != 'u' && textMessage.charAt(pos1) != 'v' && textMessage.charAt(pos1) != 'w' && textMessage.charAt(pos1) != 'x'
//			     && textMessage.charAt(pos1) != 'y' && textMessage.charAt(pos1) != 'z' && textMessage.charAt(pos1) != 'é' && textMessage.charAt(pos1) != 'è'
//			     && textMessage.charAt(pos1) != 'å' && textMessage.charAt(pos1) != 'ä' && textMessage.charAt(pos1) != 'ö' && textMessage.charAt(pos1) != '\''){
//				pos1++;
//				if(pos1 >= textMessage.length()){
//					break;
//				}
//			}
//			if(pos1 >= textMessage.length()){
//				break; // just a bunch of symbols left of the message
//			}
//			// find the end of the word
//			pos2 = pos1;
//			while(textMessage.charAt(pos2) == 'a' || textMessage.charAt(pos2) == 'b' || textMessage.charAt(pos2) == 'c' || textMessage.charAt(pos2) == 'd'
//				 || textMessage.charAt(pos2) == 'e' || textMessage.charAt(pos2) == 'f' || textMessage.charAt(pos2) == 'g' || textMessage.charAt(pos2) == 'h'
//				 || textMessage.charAt(pos2) == 'i' || textMessage.charAt(pos2) == 'j' || textMessage.charAt(pos2) == 'k' || textMessage.charAt(pos2) == 'l'
//				 || textMessage.charAt(pos2) == 'm' || textMessage.charAt(pos2) == 'n' || textMessage.charAt(pos2) == 'o' || textMessage.charAt(pos2) == 'p'
//				 || textMessage.charAt(pos2) == 'q' || textMessage.charAt(pos2) == 'r' || textMessage.charAt(pos2) == 's' || textMessage.charAt(pos2) == 't'
//				 || textMessage.charAt(pos2) == 'u' || textMessage.charAt(pos2) == 'v' || textMessage.charAt(pos2) == 'w' || textMessage.charAt(pos2) == 'x'
//			     || textMessage.charAt(pos2) == 'y' || textMessage.charAt(pos2) == 'z' || textMessage.charAt(pos1) == 'é' || textMessage.charAt(pos1) == 'è'
//			     || textMessage.charAt(pos1) == 'å' || textMessage.charAt(pos1) == 'ä' || textMessage.charAt(pos1) == 'ö' || textMessage.charAt(pos1) == '\''){
//				pos2++;
//				if(pos1+pos2 >= textMessage.length()){
//					break;
//				}
//			}
//			// a word is found between indexes pos1 and pos2
//			word = textMessage.substring(pos1, pos2);
//			if(word.length() == 0){
//				break;
//			}
//			notRealWords.add(word);
//			if(pos2+1 <= textMessage.length()){
//				textMessage = textMessage.substring(pos2+1);
//			}
//			else{
//				break;
//			}
//		}
	}
	
	public void setWords(ArrayList<Word> words){
		this.wordsInSms = new ArrayList<Word>(words);
	}
	
	public void addWord(Word word){
		if(wordsInSms == null){
			wordsInSms = new ArrayList<Word>();
		}
		if(word != null){
			wordsInSms.add(word);
		}
	}
	
	/**
	 * 
	 * @return A long integer
	 */
	public String getDate(){
		return date;
	}
	
	/**
	 * converts the date to a more readable format
	 * @return dd/MM/yyyy HH:mm
	 */
	public String getFullDate(){
		Date obj = new Date(Long.parseLong(date));
		return sdf.format(obj.getTime());
	}
	
	public YearMonth getYearMonth(){
		String dateString = getFullDate();
		int monthPos = Integer.parseInt(dateString.substring(dateString.indexOf('/')+1, dateString.lastIndexOf('/')));
		Month month = DateView.MONTHS_NAME[monthPos-1];
		int year = Integer.parseInt(dateString.substring(dateString.lastIndexOf('/')+1, dateString.indexOf(' ')));
		return new YearMonth(year, month);
	}
	
	public int getYear(){
		String dateString = getFullDate();
		return Integer.parseInt(dateString.substring(dateString.lastIndexOf('/')+1, dateString.indexOf(' ')));
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public long getID(){
		return id;
	}
	
	public void setID(long id){
		this.id = id;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public long getContactID(){
		return contactID;
	}
	
	public void setContact(long contactID){
		this.contactID = contactID;
	}
	
	@Override
	public boolean equals(Object sms){
		if(sms instanceof SMS && this.id == ((SMS)sms).getID()){
			return true;
		}
		return false;
	}
}
