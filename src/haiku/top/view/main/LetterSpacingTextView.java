package haiku.top.view.main;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class LetterSpacingTextView extends TextView {
	public final static float NORMAL = 1;
    private float letterSpacing = NORMAL;
    public static final float MIN_SPACING = (float) 0.5;
    public static final float MAX_SPACING = (float) 12;
    private CharSequence originalText = "";


    public LetterSpacingTextView(Context context) {
        super(context);
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public float getLetterSpacing() {
        return letterSpacing;
    }

    public void setLetterSpacing(float letterSpacing) {
    	letterSpacing = Math.max(Math.min(letterSpacing, MAX_SPACING), MIN_SPACING);
        this.letterSpacing = letterSpacing;
        applyLetterSpacing();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        applyLetterSpacing();
    }

    @Override
    public CharSequence getText() {
        return originalText;
    }
    
    private void applyLetterSpacing() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if(i+1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if(builder.toString().length() > 1) {
        	int turn = 0;
            for(int i = 0; i < builder.toString().length(); i++) {
            	if(turn == 0){
            		turn = 1;
            		if(letterSpacing < 1){
            			finalText.setSpan(new ScaleXSpan(letterSpacing), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            		}
            	}
            	else if(turn == 1){
            		turn = 0;
            		finalText.setSpan(new ScaleXSpan((letterSpacing)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            	}
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);
    }

//    private void applyLetterSpacing() {
//        StringBuilder builder = new StringBuilder();
//        for(int i = 0; i < originalText.length(); i++) {
//            builder.append(originalText.charAt(i));
//            if(i+1 < originalText.length()) {
//                builder.append("\u00A0");
//            }
//        }
//        SpannableString finalText = new SpannableString(builder.toString());
//        if(builder.toString().length() > 1) {
//            for(int i = 1; i < builder.toString().length(); i+=2) {
//                finalText.setSpan(new ScaleXSpan((letterSpacing+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }
//        super.setText(finalText, BufferType.SPANNABLE);
//    }
}
