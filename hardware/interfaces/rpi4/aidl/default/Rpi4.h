#pragma once

#include <aidl/vendor/demo/rpi4/BnRpi4.h>
#include <log/log.h>

#ifdef LOG_TAG
#undef LOG_TAG
#endif
#define LOG_TAG "Rpi4_HAL_10"

namespace aidl {
namespace vendor {
namespace demo {
namespace rpi4 {
    /**
     * Class to handle RPI4 GPIO.
     */
    class Rpi4 : public BnRpi4 {
    public :
        /**
         * Method to config the GPIO pin.
         * @param in_pin pin number.
         * @param in_mode mode of the pin.
         * @param _aidl_return true or false.
         * @return scoped status.
         */
        ::ndk::ScopedAStatus configPin(::aidl::vendor::demo::rpi4::Pin in_pin,
                                       ::aidl::vendor::demo::rpi4::PinMode in_mode,
                                       bool *_aidl_return);

        /**
         * Method to set GPIO pin.
         * @param in_pin pin number.
         * @param in_mode mode of the pin.
         * @param _aidl_return true or false.
         * @return scoped status.
         */
        ::ndk::ScopedAStatus
        setGpio(::aidl::vendor::demo::rpi4::Pin in_pin, int32_t in_status, bool *_aidl_return);

        /**
         * Method to get the GPIO pin
         * @param in_pin pin number.
         * @param _aidl_return true or false.
         * @return scoped status.
         */
        ::ndk::ScopedAStatus getGpio(::aidl::vendor::demo::rpi4::Pin in_pin, int32_t *_aidl_return);

        /**
         * Method to set PWM pin.
         * @param in_pin pin number.
         * @param in_period value of period.
         * @param in_dutyCycle value of duty cycle.
         * @param _aidl_return true or false.
         * @return scoped status.
         */
        ::ndk::ScopedAStatus
        setPwm(::aidl::vendor::demo::rpi4::Pin in_pin, int32_t in_period, int32_t in_dutyCycle,
               bool *_aidl_return);

        /**
         * Method to change the duty cycle.
         * @param in_pin pin number.
         * @param in_dutyCycle value of duty cycle.
         * @param _aidl_return true or false.
         * @return scoped status.
         */
        ::ndk::ScopedAStatus
        changeDutyCycle(::aidl::vendor::demo::rpi4::Pin in_pin, int32_t in_dutyCycle,
                        bool *_aidl_return);

        /**
         * Method to change the LED pattern.
         * @return scoped status.
         */
        ndk::ScopedAStatus changeLedPattern();
    };
}
}
}
}