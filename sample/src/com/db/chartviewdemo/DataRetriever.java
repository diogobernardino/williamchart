package com.db.chartviewdemo;

import java.util.Random;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import com.db.chart.Tools;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.bounce.BounceEaseOut;
import com.db.chart.view.animation.easing.elastic.ElasticEaseOut;
import com.db.chart.view.animation.easing.quint.QuintEaseOut;

public class DataRetriever {
	
	private final static String[] mColors = {"#f36c60","#7986cb", "#4db6ac", "#aed581", "#ffb74d"};
	
	public static boolean randBoolean(){
		return Math.random() < 0.5;
	}
	
	
	public static int randNumber(int min, int max) {
	    return new Random().nextInt((max - min) + 1) + min;
	} 

	
	public static float randValue(float min, float max) {
		return  (new Random().nextFloat() * (max - min)) + min;
	} 
	
	
	public static float randDimen(float min, float max){
		float ya = (new Random().nextFloat() * (max - min)) + min;
	    return  Tools.fromDpToPx(ya);
	}
	
	
	public static Paint randPaint() {
		
		if(randBoolean()){
			Paint paint = new Paint();
			paint.setColor(Color.parseColor("#b0bec5"));
			paint.setStyle(Paint.Style.STROKE);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(Tools.fromDpToPx(1));
			if(randBoolean())
				paint.setPathEffect(new DashPathEffect(new float[] {10,10}, 0));
			
			return paint;
		}
		
		return null;
	}
	
	
	public static boolean hasFill(int index){
		return (index == 2) ? true : false;
	}
	
	
	public static Animation randAnimation(Runnable endAction){
		
		switch (new Random().nextInt(3)){
			case 0:
				return new Animation()
					.setEasing(new QuintEaseOut())
					.setOverlap(randValue(0.5f, 1f))
					.setAlpha(randNumber(3,6))
					.setEndAction(endAction);
			case 1:
				return new Animation()
					.setEasing(new QuintEaseOut())
					.setStartPoint(0f, 0f)
					.setAlpha(randNumber(3,6))
					.setEndAction(endAction);
			case 2:
				return new Animation()
					.setEasing(new BounceEaseOut())
					.setOverlap(randValue(0.5f, 1f))
					.setEndAction(endAction);
			default:
				return new Animation()
					.setOverlap(randValue(0.5f, 1f))
					.setEasing(new ElasticEaseOut())
					.setEndAction(endAction);
		}
	}
	
	
	public static String getColor(int index){
		
		switch (index){
			case 0:
				return mColors[0];
			case 1:
				return mColors[1];
			case 2:
				return mColors[2];
			case 3:
				return mColors[0];
			case 4:
				return mColors[1];
			default:
				return mColors[2];
		}
	}
	
	
}
