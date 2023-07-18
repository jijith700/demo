# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)

# Declares and names the project.

project("rpi4")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        rpi4

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp
        /media/jijith/SSD/AndroidAutomotive13/vendor/demo/hardware/interfaces/rpi4/aidl/default/Android.bp
        /media/jijith/SSD/AndroidAutomotive13/vendor/demo/hardware/interfaces/rpi4/aidl/default/Rpi4.cpp
        /media/jijith/SSD/AndroidAutomotive13/vendor/demo/hardware/interfaces/rpi4/aidl/default/Rpi4.h
        /media/jijith/SSD/AndroidAutomotive13/vendor/demo/hardware/interfaces/rpi4/aidl/default/Rpi4Config.cpp
        /media/jijith/SSD/AndroidAutomotive13/vendor/demo/hardware/interfaces/rpi4/aidl/default/Rpi4Config.h
        /media/jijith/SSD/AndroidAutomotive13/vendor/demo/hardware/interfaces/rpi4/aidl/default/service.cpp
        /media/jijith/SSD/AndroidAutomotive13/vendor/demo/hardware/interfaces/rpi4/aidl/Android.bp
        /media/jijith/SSD/AndroidAutomotive13/vendor/demo/hardware/interfaces/rpi4/aidl/vendor/demo/rpi4/PinMode.aidl
        /media/jijith/SSD/AndroidAutomotive13/vendor/demo/hardware/interfaces/rpi4/aidl/vendor/demo/rpi4/IRpi4.aidl
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        rpi4

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})

include_directories(
        /media/jijith/SSD/AndroidAutomotive13/out/soong/.intermediates/vendor/demo/hardware/interfaces/rpi4/aidl/vendor.demo.rpi4-V1-ndk-source/gen
        /media/jijith/SSD/AndroidAutomotive13/out/soong/.intermediates/vendor/demo/hardware/interfaces/rpi4/aidl/vendor.demo.rpi4-V1-ndk-source/gen/include
        /media/jijith/SSD/AndroidAutomotive/system/core/libcutils/include/
        /media/jijith/SSD/AndroidAutomotive/system/libhwbinder/include/
        /media/jijith/SSD/AndroidAutomotive/system/core/liblog/include/
        /media/jijith/SSD/AndroidAutomotive/frameworks/native/include/
        /media/jijith/SSD/AndroidAutomotive/system/core/libutils/include
        /media/jijith/SSD/AndroidAutomotive13/external/libncurses/include
)
