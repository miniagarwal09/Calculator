package com.hfad.calculator;

import android.app.Activity;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.pow;


public class calculator extends Activity {
    boolean clearedOperator=false;
    TextView text;
    String s1="";
    String[] infix=new String[20];
    int a=0;


    public StringBuffer ch=new StringBuffer("");

    //HashMap for priority of operators
    Map<String,Integer> priority=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        text = (TextView) findViewById(R.id.input);
        text.setText("");
        //priority initialisation
        priority.put("+",0);
        priority.put("-",0);
        priority.put("*",1);
        priority.put("/",1);
        priority.put("^",2);

    }
    //Number button clicked.Therefore get the number and add it to the textbox.
    //Add the number to s1 string to be used for calculation.
    public void input(View v) {
        if(ch.length()<20) {

            Button b = (Button) v;
            CharSequence in = b.getText();
            ch = ch.append(in);
            s1 = s1 + in;
            text.setText(ch);
        }

    }
    //output the result
    public void output(View v) {
        infix[a++] = s1;
        s1="";

       double result=postfix();
        System.out.println("Result is:"+result);
        text.setText(ch);

        ch.delete(0,ch.length());
        for (int i = 0; i <a; i++) {
            System.out.println(infix[i]);
        }
        infix=new String[20];
        a=0;
    }
    //get the number from s1 and add it in infix string that will be converted to postfix.
    //add the operator to the infix string.
    //Again make s1 empty in order to get it refilled for next number.
    public void operator(View view){
        if(ch.length()<20) {
            Button b = (Button) view;
            text.setText(ch.append(((Button) view).getText()));
            System.out.println("s1 is:"+s1);
            if(!clearedOperator) {
                infix[a++] = s1;
                s1 = "";
            }
            infix[a++] = String.valueOf(b.getText());
            clearedOperator=false;

        }

    }
    public void clear(View view){
        if (ch.length()>0){
            Log.i("Deleted is:",ch.substring(ch.length()-1));
            Log.i("a before:",String.valueOf(a));
            //System.out.println("Last Element was:"+infix[a-1]);
            if(ch.substring(ch.length()-1).equals("+")||ch.substring(ch.length()-1).equals("/")||
                    ch.substring(ch.length()-1).equals("-")||ch.substring(ch.length()-1).equals("*")||
                    ch.substring(ch.length()-1).equals("^")) {
                a-=1;
                clearedOperator=true;
            }
            else if (!s1.equals("")){
                System.out.println("s1: "+s1.substring(0,s1.length()-1));
                s1=s1.substring(0,s1.length()-1);
            }
            ch.delete(ch.length()-1,ch.length());
            text.setText(ch);
            Log.i("a After:",String.valueOf(a));
            //System.out.println("Now last element is: "+infix[a-1]);
        }

    }
    //convert infix to postfix and then solve it and return the result
    public double postfix(){

        double result=0;
        int j=0;//index of stack
        int k=0;//index of postfix
        String[] postfix=new String[20];
        String stack[]=new String[20];
        for (int i=0;i<a;i++){
            if(isOperator(infix,i)){
                if(j!=0) {
                    while (j != 0) {
                        String stackTop = stack[j - 1];
                        String infixTop = infix[i];
                        if (priority.get(stackTop) >= priority.get(infixTop)) {
                            postfix[k++] = stack[j - 1];
                            j--;
                        }

                        else {
                            break;
                        }
                    }
                    stack[j++] = infix[i];
                }
                else{
                        stack[j++] = infix[i];
                    }

            }
            else {
                postfix[k++]=infix[i];
            }


        }
        while(j>0){
            postfix[k++]=stack[--j];

        }
        Log.i("Postfix:","Result is:");
        k=0;
        String r="";
        while(k<a) {
            //System.out.println(postfix[k--]);
            r=r+postfix[k];
            Log.i("Postfix:",postfix[k++]+"");
        }
        TextView resultText=(TextView)findViewById(R.id.result);
        try {
            result = solve(postfix);
            resultText.setText(String.valueOf(result));
        }
        catch (Exception e){
            resultText.setText("Invalid Input");
        }
        if (text.length()>18){
            resultText.setTextSize(20);
        }
        else{
            resultText.setTextSize(36);
        }
        return result;

    }
    public double solve(String[] postfix) throws Exception{
        double result=0;
        String[] stack=new String[20];
        int j=0;
        try {
            for (int i = 0; i < a; i++) {
                if (isOperator(postfix, i)) {
                    System.out.print("FirstOp:" + stack[j - 1] + "\t" + "SecondOp" + stack[j - 2]);
                    double firstOp = Double.parseDouble(stack[--j]);
                    double secondOp = Double.parseDouble(stack[--j]);
                    System.out.print("FirstOp:" + firstOp + "\t" + "SecondOp" + secondOp);
                    if (postfix[i].equals("+")) {
                        stack[j++] = String.valueOf(firstOp + secondOp);
                    } else if (postfix[i].equals("-")) {
                        stack[j++] = String.valueOf(secondOp - firstOp);
                    } else if (postfix[i].equals("*")) {
                        stack[j++] = String.valueOf(secondOp * firstOp);
                    } else if (postfix[i].equals("/")) {
                        stack[j++] = String.valueOf(secondOp / firstOp);
                    } else if (postfix[i].equals("^")) {
                        stack[j++] = String.valueOf(pow(secondOp, firstOp));
                    }
                } else {
                    stack[j++] = postfix[i];
                }
            }
            result = Double.parseDouble(stack[0]);
            return result;
        }
        catch (Exception e){
            throw e;
        }

    }
    //To check if currently scanned element is operator or not
    public boolean isOperator(String[] infix ,int i){
        return infix[i].equals("+")||infix[i].equals("-")||infix[i].equals("/")||infix[i].equals("*")||infix[i].equals("^");

    }
}




