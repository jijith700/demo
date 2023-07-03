//
// Created by jijith on 07/05/23.
//

#include <log/log.h>

#ifndef RPI4GPIO_RPI4CONFIG_H
#define RPI4GPIO_RPI4CONFIG_H


// Pin modes
#define    INPUT             0
#define    OUTPUT             1
#define    PWM_OUTPUT         2

#define    ON 1
#define    OFF 0

#ifdef LOG_TAG
#undef LOG_TAG
#endif
#define LOG_TAG "Rpi4_HAL_10"

/**
 * Class to handle all the raspberry pi 4 GPIO pins.
 */
class Rpi4Config {

public:

    /**
     * Method to configure the GPIO pins.
     * @param pin GPIO pin number.
     * @param mode mode of the pin.
     * @return success or fail.
     */
    static bool ConfigPin(int pin, int mode);

    /**
     * Method to set the status to the GPIO pin.
     * @param pin GPIO pin number.
     * @param status status (ON/OFF) of the pin.
     * @return success or fail.
     */
    static bool SetGpio(int pin, int status);

    /**
     * Method to get the status of the pin.
     * @param pin GPIO pin number.
     * @return success or fail.
     */
    static int GetGpio(int pin);

    /**
     * Method to set the PWM pin.
     * @param pin PWM pin number.
     * @param period initial value of period.
     * @param duty_cycle initial value of duty cycle.
     * @return success or fail.
     */
    static bool SetPwm(int pin, int period, int duty_cycle);

    /**
     * Method to change the duty cycle.
     * @param pin GPIO pin number.
     * @param duty_cycle value of duty cycle
     * @return success or fail.
     */
    static bool ChangeDutyCycle(int pin, int duty_cycle);

    /**
     * Method to release all configuration.
     * @param pin GPIO pin number.
     * @param mode mode of the pin.
     */
    static void Release(int pin, int mode);

private:
    /**
     * Method to configure the GPIO pins.
     * @param pin GPIO pin number.
     * @param mode mode of the pin.
     * @return success or fail.
     */
    static bool ConfigGpio(int pin, int mode);

    /**
     * Method to configure the PWM pin.
     * @param pin PWM pin number.
     * @return success or fail.
     */
    static bool ConfigPwm(int pin);

    /**
     * Method to release the GPIO pins.
     * @param pin GPIO pin number.
     */
    static void ReleaseGpio(int pin);

    /**
     * Method to release the PWM pin.
     * @param pin PWM pin number.
     */
    static void ReleasePwm(int pin);

};


#endif //RPI4GPIO_RPI4CONFIG_H
