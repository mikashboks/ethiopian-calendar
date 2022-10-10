# ethiopian-calendar [![](https://jitpack.io/v/mikashboks/ethiopian-calendar.svg)](https://jitpack.io/#mikashboks/ethiopian-calendar)


Sample Usage
-----

-  Add this to your app `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.mikashboks:ethiopian-calendar:1.0.2'
}
```

-  Usage
There are two types of usage available for this lib, 1) As custom calendar view 2) As calendar date picker


**1) Custom Calendar View**

```
 <com.mkb.ethiopian.lib.CustomCalendarView
        android:id="@+id/calendar_view"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:calendarPrimaryColor="@color/colorPrimary"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

```


```
calendarView.openAt = Calendar.getInstance().let {
            it.set(2023, 6, 2); it.timeInMillis
        }

calendarView.minDate = Calendar.getInstance().let {
    it.set(2023, 5, 1); it.timeInMillis
}

calendarView.maxDate = Calendar.getInstance().let {
    it.set(2023, 7, 1); it.timeInMillis
}

calendarView.onSelectListener = object : OnSelectListener {
    override fun onDateSelect(date: Long) {
        val dateString = DateFormat.format("dd-MM-yyyy", date)
        Toast.makeText(
            requireContext(),
            "Selected Date: $dateString",
            Toast.LENGTH_SHORT
        ).show()
    }
}
```


**2) Calendar Date Picker**

```
val calendarPickerFragment = CalenderPickerFragment.newInstance(
            openAt = Calendar.getInstance().let {
                it.set(2023, 6, 2); it.timeInMillis
            },
            minDate = Calendar.getInstance().let {
                it.set(2023, 5, 2); it.timeInMillis
            },
            maxDate = Calendar.getInstance().let {
                it.set(2023, 7, 2); it.timeInMillis
            },
            onSelectListener = object : OnSelectListener {
                override fun onDateSelect(date: Long) {
                    val dateString = DateFormat.format("dd-MM-yyyy", date)
                    Toast.makeText(
                        requireContext(),
                        "Selected Date: $dateString",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        calendarPickerFragment.show(childFragmentManager, "Date Picker")
```

Screening

[device-2022-10-10-130130.webm](https://user-images.githubusercontent.com/1019268/194820601-ce7179ce-73ca-4fde-84eb-e74d249b64a5.webm)
<table>
<td><img src='https://user-images.githubusercontent.com/1019268/194820701-01c8c021-3077-4998-948a-eebd6fbe8ddb.png' /></td>
<td><img src='https://user-images.githubusercontent.com/1019268/194820750-1d48873e-a548-49b5-a67b-03012389d43d.png' /></td>
<td><img src='https://user-images.githubusercontent.com/1019268/194820791-44451afd-fc2a-46fc-95c8-3b71aa612e5a.png' /></td>
</table>







# Developers

* [MiKashBoks](https://github.com/mikashboks)

# License

```
Copyright 2020 MiKashBoks

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

