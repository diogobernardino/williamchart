WilliamChart
===============

Chart View is and Android Library based on Views to help the implementation of charts in android applications.

At the moment it offers the following charts:
* ``LineChartView``
* ``BarChartView``

It has been tested in Android 2.2 and above.

To create a new chart type extend the class ``ChartView`` and implement the necessary abstract methods. I believe the data you get from those methods should be enough to draw whatever you feel like.

<a href="https://play.google.com/store/apps/details?id=com.db.williamchartdemo">
  <img alt="Android app on Google Play" src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

Screenshots
-----------

![Demo Screenshot][1]


Usage
-----
Each chart type has common and specific customization attributes. Add the View to your layout and configure it using styleable attributes or/and programmatically. Check the examples below.


Chart - Common customization
----------------------

```xml

    <com.db.chart.view.ChartView
        xmlns:chart="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="180dp"
        android:padding="5dp"
        chart:chart_shadowDx="1dp"
        chart:chart_shadowDy="1dp"
        chart:chart_shadowRadius="1dp"
        chart:chart_shadowColor="#000000"
        chart:chart_gridThickness="1dp"
        chart:chart_labelColor="@color/label"
        chart:chart_gridColor="@color/grid"
        chart:chart_axisColor="@color/axis"
        chart:chart_fontSize="10dp"
        chart:chart_typeface="Roboto-Regular.ttf"
        chart:chart_axisBorderSpacing="0dp"
        chart:chart_axisThickness="2dp"
        chart:chart_axisTopSpacing="2dp"
        chart:chart_label="true"
        chart:chart_grid="true"
        chart:chart_horizontalGrid="true"
    />

```

```java
    chart.setLabels(bool)
    chart.setLabelColor(color)
    
    chart.setGrid(bool)
    chart.setGridColor(color)
    chart.setGridStrokeThickness(integer)
    chart.setHorizontalGrid(bool)

    chart.setBorderSpacing(float)
    chart.setStep(integer)
    chart.setMaxAxisValue(integer, integer)
    chart.setTopSpacing(float)

    chart.setFontSize(integer)
    chart.setTypeface(typeface)
```


Line Chart
----------

```xml
    <com.db.chart.LineChartView
        ... 
    />
```

```java
    LineChartView chartView= new LineChartView();

    LineSet lineSet = new LineSet();
    lineSet.addPoint(new Point(string, float);
    
    // Style dots
    lineSet.setDots(bool)
    lineSet.setDotsColor(color)
    lineSet.setDotsRadius(float)
    lineSet.setDotsStrokeThickness(float)
    lineSet.setDotsStrokeColor(color)
    
    // Style line
    lineSet.setLineThickness(rloat)
    lineSet.setLineColor(color)
    
    // Style background fill
    lineSet.setFill(bool)
    lineSet.setFillColor(color)
    
    // Style type
    lineSet.setDashed(bool)
    lineSet.setSmooth(bool);

    chartView.addData(lineSet)
```


Bar chart
---------

```xml
    <com.db.chart.BarChartView
        ... 
        chart:chart_barSpacing="10dp"
    />
```

```java
    BarChartView chartView = new BarcChartView();
    barChart.setBarSpacing(randDimen(15, 35));

    BarSet barSet = new BarSet();
    Bar bar = new Bar(mLabels[j], randValue());
    bar.setColor(Color.parseColor(getColor(j)));
    barSet.addBar(bar);

    chartView.addData(barSet)
```


Listener
---------

```java
    chart.setOnEntryClickListener(new OnEntryClickListener(){
        @Override
        public void onClick(int setIndex, int entryIndex) {
            //Do things
        }
    });
```


Animation
---------

Note: I've implement a few easing functions (credits to [Jesus Gollonet](http://jesusgollonet.com/)). Implementing the interface ``BaseEasingMethod`` you can create your own easing function.

```java
    Animation anim = new Animation();

    anim.setDuration(int);
    anim.setEasing(easingFunction);
    anim.setEndAction(runnable);
    
    chart.setAnimation(anim);
```


TODO
----
* Pie Chart
* Stack Bar Chart


License
-------

    Copyright 2014 Diogo Bernardino

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



[1]: ./art/demo.gif