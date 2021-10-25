# performance-poc

WIP

https://confluence.enflick.com/pages/viewpage.action?spaceKey=AND&title=Preference+Redux+H2+2021

# Setup NDK to build this project:

You need CMAKE under SDK Manager -> SDK Tools -> Show Package Details -> CMAKE 3.18.1

I dont think a specific NDK is required at the moment, but if there is I will update this readme. This worked with the NDK version textnow uses.


# Adding benchmarking functions:

In the HomeFragment there are buttons that will output the results of what we want to measure. You can add whatever testing functionality you want here.
There is a scrollable textview in this fragment that functions like console output that test results can be appended to.

There is a viewmodel for this fragment we can use to test operations in a coroutine rather than the main thread if needed.

Any new libraries introduced for testing can be added to the build.gradle file.

APK size should be measured before and after adding a new library.



