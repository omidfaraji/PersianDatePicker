[![](https://jitpack.io/v/omidfaraji/PersianDatePicker.svg)](https://jitpack.io/#omidfaraji/PersianDatePicker)

## PersianDatePicker

An Android Library written in Kotlin to pick single and range date 

 <img src="https://raw.githubusercontent.com/omidfaraji/PersianDatePicker/master/ScreenShot/sc1.jpg"  height="600" width="500" />


#Usage

Step 1. Add the JitPack repository to your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
 
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.omidfaraji:PersianDatePicker:Tag'
	}
 
 Step 3. Add View to your xml file
 ```xml
 <com.faraji.persiandatepicker.PersianDatePickerView
     android:id="@+id/time_picker"
     android:layout_width="match_parent"
     android:layout_height="wrap_content" />
```

Step 4. add selection listener
```java
  val datePicker = findViewById<PersianDatePickerView>(R.id.time_picker)
  datePicker.onSelectionChanged = { start, end ->
      //...
  }
```
