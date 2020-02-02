---
title: Line Chart
has_children: false
nav_order: 2
---

# Line Chart

#### Smooth

Use the `smooth` to have smooth line as displayed below.

IMAGE GOES HERE

| Kotlin | XML |
|---|---|
| `smooth = boolean` | `chart_smoothLine = "boolean"`  |

#### Line Thickness

| Kotlin | XML |
|---|---|
| `lineThickness = float` | `chart_lineThickness = "dimension"`  |

#### Line color

| Kotlin | XML |
|---|---|
| `lineColor = integer` | `chart_lineColor = "color"`  |

#### Line Fill

A fill can either be a single color or gradient.

IMAGE GOES HERE

| Kotlin |
|---|
| `fillColor = integer` |

IMAGE GOES HERE

| Kotlin |
|---|
| `gradientFillColors = list<integer>` |

#### Points

Points can be displayed using drawables. The drawable id resource can be passed directly in the XML layout or programatically.

IMAGE GOES HERE

| Kotlin | XML |
|---|---|
| `pointsDrawableRes = integer` | `chart_pointDrawable = "drawable"`  |
