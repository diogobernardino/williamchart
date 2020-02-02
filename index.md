# Welcome

[![Kotlin Version](https://img.shields.io/badge/kotlin-1.3.50-blue.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![CodeFactor](https://www.codefactor.io/repository/github/diogobernardino/williamchart/badge)](https://www.codefactor.io/repository/github/diogobernardino/williamchart)

Williamchart is an Android Library to rapidly implement attractive and insightful charts in android applications.

![screenshot][4]

### Hands-on!

Add your gradle dependency:

``` groovy
implementation 'com.diogobernardino:williamchart:3.2.0'
```

This one goes into your layout:

```xml
<com.db.williamchart.view.LinechartView
	android:id="@+id/myWilliamChart"
	android:layout_width="match_parent"
	android:layout_height="200dp"
/>
```

Finally, call `show(...)` with your datapoints:

```kotlin
val mySet = linkedMapOf("label1" to 4F, "label2" to 7F, "label3" to 2F)
myWilliamChart.show(mySet)
```

### Credits

If you liked williamchart and decide to use it in your projects, please drop me a line [@dfbernardino][1], I will be happy to know about it.

License
-------

    Copyright 2019 Diogo Bernardino

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: https://twitter.com/dfbernardino
[2]: ./art/phone.png
[3]: ./art/watch.png
[4]: ./art/demo_screenshot.png