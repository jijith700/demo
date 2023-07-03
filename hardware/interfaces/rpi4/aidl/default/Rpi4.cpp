#define LOG_TAG "Rpi4"

#include <utils/Log.h>
#include <iostream>
#include <fstream>
#include <cstdlib>
#include <thread>
#include <chrono>
#include <future>

#include "Rpi4.h"
#include "Rpi4Config.h"

using namespace std;

namespace aidl::vendor::demo::rpi4 {

    /**
     * Method to config the GPIO pin.
     * @param in_pin pin number.
     * @param in_mode mode of the pin.
     * @param _aidl_return true or false.
     * @return scoped status.
     */
    ::ndk::ScopedAStatus
    Rpi4::configPin(::aidl::vendor::demo::rpi4::Pin in_pin,
                    ::aidl::vendor::demo::rpi4::PinMode in_mode,
                    bool *_aidl_return) {
        ALOGI("Rpi4: configPin");
        bool status = Rpi4Config::ConfigPin(static_cast<int>(in_pin), static_cast<int>(in_mode));
        *_aidl_return = status;
        if (status)
            return ndk::ScopedAStatus::ok();
        else {
            return ndk::ScopedAStatus::fromExceptionCode(EX_UNSUPPORTED_OPERATION);
        }
    }

    /**
     * Method to set GPIO pin.
     * @param in_pin pin number.
     * @param in_mode mode of the pin.
     * @param _aidl_return true or false.
     * @return scoped status.
     */
    ::ndk::ScopedAStatus
    Rpi4::setGpio(::aidl::vendor::demo::rpi4::Pin in_pin, int32_t in_status, bool *_aidl_return) {
        ALOGI("Rpi4: setGpio");
        bool status = Rpi4Config::SetGpio(static_cast<int>(in_pin), in_status);
        *_aidl_return = status;
        if (status)
            return ndk::ScopedAStatus::ok();
        else {
            return ndk::ScopedAStatus::fromExceptionCode(EX_UNSUPPORTED_OPERATION);
        }
    }

    /**
     * Method to get the GPIO pin
     * @param in_pin pin number.
     * @param _aidl_return true or false.
     * @return scoped status.
     */
    ::ndk::ScopedAStatus
    Rpi4::getGpio(::aidl::vendor::demo::rpi4::Pin in_pin, int32_t *_aidl_return) {
        ALOGI("Rpi4: getGpio");
        int status = Rpi4Config::GetGpio(static_cast<int>(in_pin));
        *_aidl_return = status;
        if (status)
            return ndk::ScopedAStatus::ok();
        else {
            return ndk::ScopedAStatus::fromExceptionCode(EX_UNSUPPORTED_OPERATION);
        }
    }

    /**
     * Method to set PWM pin.
     * @param in_pin pin number.
     * @param in_period value of period.
     * @param in_dutyCycle value of duty cycle.
     * @param _aidl_return true or false.
     * @return scoped status.
     */
    ::ndk::ScopedAStatus
    Rpi4::setPwm(::aidl::vendor::demo::rpi4::Pin in_pin, int32_t in_period, int32_t in_dutyCycle,
                 bool *_aidl_return) {
        ALOGI("Rpi4: setPwm");
        bool status = Rpi4Config::SetPwm(static_cast<int>(in_pin), in_period, in_dutyCycle);
        *_aidl_return = status;
        if (status)
            return ndk::ScopedAStatus::ok();
        else {
            return ndk::ScopedAStatus::fromExceptionCode(EX_UNSUPPORTED_OPERATION);
        }
    }

    /**
     * Method to change the duty cycle.
     * @param in_pin pin number.
     * @param in_dutyCycle value of duty cycle.
     * @param _aidl_return true or false.
     * @return scoped status.
     */
    ::ndk::ScopedAStatus
    Rpi4::changeDutyCycle(::aidl::vendor::demo::rpi4::Pin in_pin, int32_t in_dutyCycle,
                          bool *_aidl_return) {
        ALOGI("Rpi4: changeDutyCycle");
        bool status = Rpi4Config::ChangeDutyCycle(static_cast<int>(in_pin), in_dutyCycle);
        *_aidl_return = status;
        if (status)
            return ndk::ScopedAStatus::ok();
        else {
            return ndk::ScopedAStatus::fromExceptionCode(EX_UNSUPPORTED_OPERATION);
        }
    }

    /**
     * Method to change the LED pattern.
     * @return scoped status.
     */
    ndk::ScopedAStatus rpi4::Rpi4::changeLedPattern() {
        ALOGI("Rpi4: changeLedPattern");
        std::thread thread([]() {
            int kPinPwm = 0;
            while (true) {
                for (int i = 0; i < 50; i++) {
                    Rpi4Config::ChangeDutyCycle(kPinPwm, i);
                    this_thread::sleep_for(chrono::milliseconds(50));
                }

                for (int i = 49; i > 0; i--) {
                    Rpi4Config::ChangeDutyCycle(kPinPwm, i);
                    this_thread::sleep_for(chrono::milliseconds(50));
                }
            }
        });
        thread.join();

        return ndk::ScopedAStatus();
    }

}