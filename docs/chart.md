---
title: Axis Chart
has_children: false
nav_order: 2
---

# Axis Chart

In this page you will see how to customize everything that related to axis charts (e.g. line chart). Williamchart supports only 2 dimensional charts, allowing you to customize XY axis and labels.

### Axis Attributes

| Kotlin | XML | Description |
|---|---|---|
| `axis` | `chart_axis` | Define which axis should be displayed. You can display only `x`, only `y`, both `xy`, or `none` of them. |
| `scale` | N/A | Set limits to the scale displayed in your chart. By default, charts will have a scale between 0 and the maximum value needed to accomodate the range of values passed.|

#### Animations

williamchart allows to customize your chart animation by either playing with interpolators or creating your own animations

#### Interpolators
Use any interpolator provided by the Android SDK or create your own ones by extending `Interpolator`.

##### Custom animations
Custom animations can be implemented by extending `ChartAnimation`. I will let you look at the class and understand what you will be required to implement.

### Labels Attributes

| Kotlin | Xml | Description |
|---|---|---|
| `labelsFont` | `chart_labelsFont` | Font used in chart labels and scale. |
| `labelsSize` | `chart_labelsSize`  | What should be the size of your font. |
| `labelsColor` | `chart_labelsColor ` | Color of text used in labels and scale. |

### Format labels

Usually useful when you have labels with specific metrics, you can write your own format which will be applied to each label in your X or Y axis, depending if you have a vertical or horizontal chart.

| Kotlin |  XML | Description |
|---|---|---|
| `labelsFormatter` | N/A | Format labels before being displayed. |

The following snippet will concatenate "€" at the end of all labels (e.g. 300€).

```kotlin
lineChartView.labelsFormatter = { it + "€" }
```
