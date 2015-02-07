LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=off
OPENCV_INSTALL_MODULES:=on

OPENCV_LIB_TYPE:=STATIC

include /sdcard/OpenCV/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES  := facelock.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS    += -lm -llog 

LOCAL_MODULE     := facelock

include $(BUILD_SHARED_LIBRARY)
