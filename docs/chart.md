---
title: Chart
has_children: false
nav_order: 2
---

# Chart

In this page you will find how to better customize charts which in their original form have axis and labels (e.g. line chart). Williamchart supports 2 dimensional chart, allowing you to customize XY axis and labels.

### Axis Attributes

| Kotlin | XML | Description |
|---|---|---|
| `axis` | `chart_axis` | Define which axis should be displayed. You can display only `x`, only `y`, both `xy`, or `none` of them. |
| `scale` | N/A | Set limits to the scale displayed in your chart. By default charts will have a scale between 0 and the maximum value needed to accomodate the range of values passed.|

#### WIP Animation

* Animation
* Interpolator
* Example

##### Custom animations
* Extend ChartAnimation

### Labels Attributes

| Kotlin | Xml | Description |
|---|---|---|
| `labelsFont` | `chart_labelsFont` | Font used in chart labels and scale. |
| `labelsSize` | `chart_labelsSize`  | What should be the size of your font. |
| `labelsColor` | `chart_labelsColor ` | Color of text used in labels and scale. |

### WIP Format labels

* Attribute
* Example
