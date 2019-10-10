# CircularLayoutManager

Simple in usage Recycler Layout Manager with circular item positioning.

|  ![](animation.gif) | ![](example.gif)  |  ![](example2.gif) |
|---|---|---|

## What's new

1.1.4 - Small fixes.

1.1.2 - Add possibility change initial angle and clockwise/counter-clockwise direction.

1.1.0 - Now you can configure some individual components like `anglePerItem`, `firstCircleRadius`, `angleStepForCircles`. Also horizontal scrolling works, it moves all elements by a circle.

## Usage
Just add a `CircularRecyclerLayoutManager` as a layout manager for your recycler view.
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        circularRecycler.layoutManager = CircularRecyclerLayoutManager(
                   canScrollHorizontally = true,
                   itemsPerCircle = 4,
                   anglePerItem = 100.0,
                   firstCircleRadius = 200.0,
                   angleStepForCircles = 45.0,
                   isClockwise = false,
                   initialAngle = 180.0
               )
        circularRecycler.adapter = Adapter()
    }
}
```

### Installation
- Add this to your project level `build.gradle`: 
```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
- Add this to your app's `build.gradle`:
```
implementation 'com.github.leshchenko:CircularLayoutManager:1.1.4'
```

```
Copyright 2019 Ruslan Leshchenko (leshchenko)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```