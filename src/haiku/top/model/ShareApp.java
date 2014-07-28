package haiku.top.model;

import android.graphics.drawable.Drawable;

public class ShareApp {
	private Drawable icon;
	private String name;
	private String activityInfoName;
	private String packageName;
	
	
	public ShareApp(String name, Drawable icon, String activityInfoName, String packageName){
		this.name = name;
		this.icon = icon;
		this.activityInfoName = activityInfoName;
		this.packageName = packageName;
	}
	
	public String getName(){
		return name;
	}
	
	public Drawable getIcon(){
		return icon;
	}
	
	public String getActivityInfoName(){
		return activityInfoName;
	}
	
	public String getPackageName(){
		return packageName;
	}

}
