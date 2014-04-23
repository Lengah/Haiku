package haiku.top.model.generator;

import java.util.ArrayList;

public class RuleRow {
	private int weight;
	private String rule;
	private ArrayList<String> ruleStructs = new ArrayList<String>();
	
	public RuleRow(String row){
		weight = Integer.parseInt(row.substring(0, row.indexOf('|')));
		row = row.substring(row.indexOf('|') + 1);
		rule = row.substring(0, row.indexOf('|'));
		generateRuleStructs();
	}
	
	private void generateRuleStructs(){
		String temp = rule;
		char tc;
		while(temp.contains("(") || temp.contains("[")){
			if(temp.indexOf('(') == 0){
				tc = ')';
			}
			else{ // [
				tc = ']';
			}
			ruleStructs.add(temp.substring(0, temp.indexOf(tc)+1));
			temp = temp.substring(temp.indexOf(tc)+1);
		}
	}
	
	public int getWeight(){
		return weight;
	}
	
	public String getRuleText(){
		return rule;
	}
	
	public ArrayList<String> getRuleStructs(){
		return ruleStructs;
	}
}
