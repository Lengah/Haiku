package haiku.top.view.binview;

import haiku.top.HaikuActivity;
import haiku.top.view.main.MainView;

public class ScrollThread extends Thread{
	private boolean scrollUp;
	private boolean stop = false;
	
	public ScrollThread(boolean scrollUp){
		this.scrollUp = scrollUp;
	}
	
	public void stopScrolling(){
		stop = true;
	}
	
	public void run(){
		try {
			while(!stop){
				if(scrollUp){
					HaikuActivity.getInstance().runOnUiThread(new Runnable() {
				        public void run() {
				        	MainView.getInstance().getBinView().scrollUp();
				        }
					});
				}
				else{
					HaikuActivity.getInstance().runOnUiThread(new Runnable() {
				        public void run() {
				        	MainView.getInstance().getBinView().scrollDown();
				        }
					});
				}
				Thread.sleep(BinView.SCROLL_TIME);
			}
		} catch (Exception e) {
			return;
		}
	}

}
