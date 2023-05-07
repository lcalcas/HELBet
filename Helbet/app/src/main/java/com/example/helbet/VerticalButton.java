package com.example.helbet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class VerticalButton extends androidx.appcompat.widget.AppCompatButton {
    String s="";

    public VerticalButton(Context context) {
        super(context);
    }


    public VerticalButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }


    public VerticalButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        // TODO Auto-generated method stub

        // TODO Auto-generated method stub

        for(int i=0;i<text.length();i++)
        {
            if(s==null)
                s="";

            s= s+String.valueOf(text.charAt(i))+ "\n";
        }



        super.setText(s, type);
    }
}
