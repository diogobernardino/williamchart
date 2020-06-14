---
title: Axis Chart
has_children: false
nav_order: 2
---

# Axis Chart

Learn how to customize everything related to axis charts (e.g. line chart, bar charts).
Williamchart supports only 2 dimensional charts, allowing you to customize XY axis and labels.

### Axis Attributes

| Kotlin | XML | Description |
|---|---|---|
| `axis` | `chart_axis` | Define which axis to display. Options are: only `x`, only `y`, both `xy`, or `none` of them. |
| `scale` | N/A | Set limits to the scale used in the chart. By default, charts will have a scale between 0 and the maximum value needed to accomodate the range of values passed.|

### Animations

Customize enter animations using different Android SDK interpolators or creating your own animations.

#### Interpolators
Use any interpolator provided by the Android SDK or create your own ones by extending `Interpolator`.

#### Custom animations
Custom animations should extend `ChartAnimation`.

### Labels Attributes

| Kotlin | Xml | Description |
|---|---|---|
| `labelsFont` | `chart_labelsFont` | Font used in labels and scale. |
| `labelsSize` | `chart_labelsSize`  | Font size used in labels and scale. |
| `labelsColor` | `chart_labelsColor ` | Text color used in labels and scale. |

### Format labels

Write your own formatter applied in labels shown on X or Y axis, depending on the orientation of your chart (vertical or horizontal). This comes in handy when charting specific metrics (e.g. meters).

| Kotlin |  XML | Description |
|---|---|---|
| `labelsFormatter` | N/A | Format labels before being displayed. |

The following snippet will concatenate "€" at the end of all labels (e.g. 300€).

```kotlin
lineChartView.labelsFormatter = { it + "€" }
```
