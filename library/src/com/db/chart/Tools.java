/*
 * Copyright 2015 Diogo Bernardino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.db.chart;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class Tools {


    /**
     * Converts dp size into pixels.
     *
     * @param dp   dp size to get converted
     * @return Pixel size
     */
	public static float fromDpToPx(float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }


    /**
     * Converts a {@link android.graphics.drawable.Drawable} into {@link android.graphics.Bitmap}.
     *
     * @param drawable   {@link android.graphics.drawable.Drawable} to be converted
     * @return {@link android.graphics.Bitmap} object
     */
	public static Bitmap drawableToBitmap (@NonNull Drawable drawable) {
		
	    if (drawable instanceof BitmapDrawable)
	        return ((BitmapDrawable)drawable).getBitmap();
	 
	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);
	 
	    return bitmap;
	}


	/**
	 * Find the Greatest Common Denominator.
	 * https://en.wikipedia.org/wiki/Euclidean_algorithm
	 *
	 * @param min   Mininum value
	 * @param max   Maximum value
	 * @return Greatest common denominator
	 */
	public static int GCD(int min, int max) {
		return max==0 ? min : GCD(max, min % max);
	}
	
}
