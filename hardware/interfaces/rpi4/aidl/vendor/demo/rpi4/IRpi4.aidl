/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vendor.demo.rpi4;

import vendor.demo.rpi4.PinMode;
import vendor.demo.rpi4.Pin;

@VintfStability
interface IRpi4 {

    boolean configPin(Pin pin, PinMode mode);

    boolean setGpio(Pin pin, int status);

    int getGpio(Pin pin);

    boolean setPwm(Pin pin, int period, int dutyCycle);

    boolean changeDutyCycle(Pin pin, int dutyCycle);

    void changeLedPattern();
  
} 
