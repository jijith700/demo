//
// Created by jijith on 07/05/23.
//
#include <iostream>
#include <fstream>
#include <string>
#include <thread>

#include "Rpi4Config.h"

// GPIO sysfs path in Android OS
const char *kGpioSysfsPath = "/sys/class/gpio";
const char *kPwmSysfsPath = "/sys/class/pwm/pwmchip0";
const char *kExport = "/export";
const char *kUnExport = "/unexport";
const char *kGpio = "/gpio";
const char *kPwm = "/pwm";
const char *kDirection = "/direction";
const char *kValue = "/value";
const char *kEnable = "/enable";
const char *kPeriod = "/period";
const char *kDutyCycle = "/duty_cycle";
const char *kIn = "in";
const char *kOut = "out";

/**
 * Method to configure the GPIO pins.
 * @param pin GPIO pin number.
 * @param mode mode of the pin.
 * @return success or fail.
 */
bool Rpi4Config::ConfigPin(int pin, int mode) {
    if (mode == INPUT || mode == OUTPUT) {
        return ConfigGpio(pin, mode);
    } else if (mode == PWM_OUTPUT) {
        return ConfigPwm(pin);
    } else {
        ALOGE("Rpi4Config: ConfigPin-> Invalid operation");
        return false;
    }
}

/**
 * Method to set the status to the GPIO pin.
 * @param pin GPIO pin number.
 * @param status status (ON/OFF) of the pin.
 * @return success or fail.
 */
bool Rpi4Config::SetGpio(int pin, int status) {
    std::fstream write_strm;
    std::string value_path = std::string(kGpioSysfsPath) + std::string(kGpio) + std::to_string(pin)
                             + std::string(kValue);
    ALOGI("Rpi4Config: SetPin-> value_path %s", value_path.c_str());

    write_strm.open(value_path.c_str(), std::ios_base::out);
    if (write_strm.is_open()) {
        ALOGI("Rpi4Config: SetPin-> write_strm opened");
        std::string pinValue = std::to_string(status);
        const char *value = pinValue.c_str();
        write_strm.write(value, static_cast<int>(strlen(value)));
        write_strm.close();

        return true;
    }
    ALOGE("Rpi4Config: SetPin-> Failed to open write_strm");
    return false;
}

/**
 * Method to get the status of the pin.
 * @param pin GPIO pin number.
 * @return success or fail.
 */
int Rpi4Config::GetGpio(int pin) {
    std::fstream read_strm;
    std::string value_path = std::string(kGpioSysfsPath) + std::string(kGpio) + std::to_string(pin)
                             + std::string(kValue);
    ALOGI("Rpi4Config: GetPin-> value_path %s", value_path.c_str());

    read_strm.open(value_path.c_str(), std::ios_base::in);
    if (read_strm.is_open()) {
        ALOGI("Rpi4Config: GetPin-> read_strm opened");
        char c;
        while (!read_strm.eof()) {
            read_strm.get(c);
            std::cout << c;
        }
        read_strm.close();
        return int(c);
    }
    ALOGE("Rpi4Config: GetPin-> Failed to open read_strm");
    return -1;
}

/**
 * Method to set the PWM pin.
 * @param pin PWM pin number.
 * @param period initial value of period.
 * @param duty_cycle initial value of duty cycle.
 * @return success or fail.
 */
bool Rpi4Config::SetPwm(int pin, int period, int duty_cycle) {
    std::fstream period_strm, cycle_strm, enable_strm;
    std::string period_path =
            std::string(kPwmSysfsPath) + std::string(kPwm) + std::to_string(pin)
            + std::string(kPeriod);
    ALOGI("Rpi4Config: SetPwm-> period_path %s", period_path.c_str());

    period_strm.open(period_path.c_str(), std::ios_base::out);
    if (period_strm.is_open()) {
        ALOGI("Rpi4Config: SetPwm-> period_path opened");
        std::string periodValue = std::to_string(period);
        const char *p = periodValue.c_str();
        period_strm.write(p, static_cast<int>(strlen(p)));
        period_strm.close();

        std::string cycle_path =
                std::string(kPwmSysfsPath) + std::string(kPwm) + std::to_string(pin)
                + std::string(kDutyCycle);
        ALOGI("Rpi4Config: SetPwm-> cycle_path %s", cycle_path.c_str());

        cycle_strm.open(cycle_path.c_str(), std::ios_base::out);
        if (cycle_strm.is_open()) {
            ALOGI("Rpi4Config: SetPwm-> cycle_path opened");
            std::string cycleValue = std::to_string(duty_cycle);
            const char *cycle = cycleValue.c_str();
            cycle_strm.write(cycle, static_cast<int>(strlen(cycle)));
            cycle_strm.close();

            std::string enable_path =
                    std::string(kPwmSysfsPath) + std::string(kPwm) + std::to_string(pin)
                    + std::string(kEnable);
            ALOGI("Rpi4Config: SetPwm-> enable_path %s", enable_path.c_str());

            enable_strm.open(enable_path.c_str(), std::ios_base::out);
            if (enable_strm.is_open()) {
                ALOGI("Rpi4Config: SetPwm-> enable_path opened");
                std::string enableValue = "1";
                const char *enable = enableValue.c_str();
                enable_strm.write(enable, static_cast<int>(strlen(enable)));
                enable_strm.close();
                ALOGI("Rpi4Config: SetPwm-> enable_path closed");
                return true;
            } else {
                ALOGE("Rpi4Config: SetPwm-> Failed to open enable_path");
            }

        } else {
            ALOGE("Rpi4Config: SetPwm-> Failed to open cycle_path");
        }

    } else {
        ALOGE("Rpi4Config: SetPwm-> Failed to open period_path");
    }

    return false;
}

/**
 * Method to change the duty cycle.
 * @param pin GPIO pin number.
 * @param duty_cycle value of duty cycle
 * @return success or fail.
 */
bool Rpi4Config::ChangeDutyCycle(int pin, int duty_cycle) {
    std::fstream cycle_strm;
    std::string cycle_path =
            std::string(kPwmSysfsPath) + std::string(kPwm) + std::to_string(pin)
            + std::string(kDutyCycle);
    ALOGI("Rpi4Config: ChangeDutyCycle-> cycle_path %s", cycle_path.c_str());

    cycle_strm.open(cycle_path.c_str(), std::ios_base::out);
    if (cycle_strm.is_open()) {
        ALOGI("Rpi4Config: ChangeDutyCycle-> cycle_path opened");
        std::string cycleValue = std::to_string(duty_cycle);
        const char *cycle = cycleValue.c_str();
        cycle_strm.write(cycle, static_cast<int>(strlen(cycle)));
        cycle_strm.close();
        ALOGI("Rpi4Config: ChangeDutyCycle-> cycle_path closed");
    } else {
        ALOGE("Rpi4Config: ChangeDutyCycle-> Failed to open cycle_path");
    }
    return false;
}

/**
 * Method to release all configuration.
 * @param pin GPIO pin number.
 * @param mode mode of the pin.
 */
void Rpi4Config::Release(int pin, int mode) {
    ALOGI("Rpi4Config: Release->");
    if (mode == INPUT || mode == OUTPUT) {
        ReleaseGpio(pin);
    } else if (mode == PWM_OUTPUT) {
        ReleasePwm(pin);
    } else {
        ALOGE("Rpi4Config: ConfigPin-> Invalid operation");
    }
}

/**
 * Method to configure the GPIO pins.
 * @param pin GPIO pin number.
 * @param mode mode of the pin.
 * @return success or fail.
 */
bool Rpi4Config::ConfigGpio(int pin, int mode) {
    std::fstream export_strm, dir_strm;
    std::string export_path = std::string(kGpioSysfsPath) + std::string(kExport);
    ALOGI("Rpi4Config: ConfigGpio-> export_path %s", export_path.c_str());

    export_strm.open(export_path.c_str(), std::ios_base::out);
    if (export_strm.is_open()) {
        ALOGI("Rpi4Config: ConfigGpio-> export_path opened");
        std::string pinValue = std::to_string(pin);
        const char *value = pinValue.c_str();
        export_strm.write(value, static_cast<int>(strlen(value)));
        export_strm.close();

        std::string direction_path =
                std::string(kGpioSysfsPath) + std::string(kGpio) + std::to_string(pin)
                + std::string(kDirection);
        ALOGI("Rpi4Config: ConfigGpio-> direction_path %s", direction_path.c_str());

        dir_strm.open(direction_path.c_str(), std::ios_base::out);
        if (dir_strm.is_open()) {
            ALOGI("Rpi4Config: ConfigGpio-> direction_path opened");
            if (mode == INPUT) {
                dir_strm.write(kIn, static_cast<int>(strlen(kIn)));
            } else {
                dir_strm.write(kOut, static_cast<int>(strlen(kOut)));
            }
            dir_strm.close();
            return true;
        } else {
            ALOGE("Rpi4Config: ConfigGpio-> Failed to open direction_path");
        }
    } else {
        ALOGE("Rpi4Config: ConfigGpio-> Failed to open export_path");
    }

    return false;
}

/**
 * Method to configure the PWM pin.
 * @param pin PWM pin number.
 * @return success or fail.
 */
bool Rpi4Config::ConfigPwm(int pin) {
    std::fstream export_strm, dir_strm;
    std::string export_path = std::string(kPwmSysfsPath) + std::string(kExport);
    ALOGI("Rpi4Config: ConfigPwm-> export_path %s", export_path.c_str());

    export_strm.open(export_path.c_str(), std::ios_base::out);
    if (export_strm.is_open()) {
        ALOGI("Rpi4Config: ConfigPwm-> export_path opened");
        std::string pinValue = std::to_string(pin);
        const char *value = pinValue.c_str();
        export_strm.write(value, static_cast<int>(strlen(value)));
        export_strm.close();
        return true;
    } else {
        ALOGE("Rpi4Config: ConfigPwm-> Failed to open export_path");
    }
    return false;
}

/**
 * Method to release the GPIO pins.
 * @param pin GPIO pin number.
 */
void Rpi4Config::ReleaseGpio(int pin) {
    std::fstream un_export_strm;
    std::string un_export_path = std::string(kGpioSysfsPath) + std::string(kUnExport);
    ALOGI("Rpi4Config: ReleaseGpio-> un_export_path %s", un_export_path.c_str());
    un_export_strm.open(un_export_path.c_str(), std::ios_base::out);
    if (un_export_strm.is_open()) {
        ALOGI("Rpi4Config: ReleaseGpio-> un_export_strm opened");
        std::string pinValue = std::to_string(pin);
        const char *value = pinValue.c_str();
        un_export_strm.write(value, static_cast<int>(strlen(value)));
        un_export_strm.close();
        ALOGI("Rpi4Config: ReleaseGpio-> %d released", pin);
    } else {
        ALOGE("Rpi4Config: ReleaseGpio-> Failed to open un_export_strm");
    }
}

/**
 * Method to release the PWM pin.
 * @param pin PWM pin number.
 */
void Rpi4Config::ReleasePwm(int pin) {
    std::fstream un_export_strm;
    std::string un_export_path = std::string(kPwmSysfsPath) + std::string(kUnExport);
    ALOGI("Rpi4Config: ReleasePwm-> un_export_path %s", un_export_path.c_str());
    un_export_strm.open(un_export_path.c_str(), std::ios_base::out);
    if (un_export_strm.is_open()) {
        ALOGI("Rpi4Config: ReleasePwm-> un_export_strm opened");
        std::string pinValue = std::to_string(pin);
        const char *value = pinValue.c_str();
        un_export_strm.write(value, static_cast<int>(strlen(value)));
        un_export_strm.close();
        ALOGI("Rpi4Config: ReleasePwm-> %d released", pin);
    } else {
        ALOGE("Rpi4Config: ReleasePwm-> Failed to open un_export_strm");
    }
}
