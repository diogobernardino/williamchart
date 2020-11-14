---
title: Donut Chart
has_children: false
nav_order: 5
---

# Donut Chart
        
### Attributes

| Kotlin | Xml | Description |
|---|---|---|
| `donutTotal` | `chart_donutTotal` | Define what will be the value representing a full donut. |
| `donutThickness` | `chart_donutThickness`  | Thickness of the bar used to draw your nhami donut. |
| `donutColors ` | `N/A` | List of colors used to draw each bar segment. Its size must match exactly the number of datapoints passed to the chart, otherwise an `IllegalStateException` will be thrown.
 |
| `donutBackgroundColor ` | `chart_donutBackgroundColor ` | Background color staying behind bar. |
| `donutRoundCorners ` | `chart_donutRoundCorners ` | Define if donut should have round corners or not, `true` or `false`. |

### Animations

Customize enter animations using different Android SDK interpolators or creating your own animations.

#### Interpolators
Use any interpolator provided by the Android SDK or create your own ones by extending `Interpolator`.

#### Custom animations
Custom animations should extend `ChartAnimation`.
