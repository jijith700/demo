#define LOG_TAG "Rpi4"

#include "Rpi4.h"

#include <android-base/logging.h>
#include <android/binder_manager.h>
#include <android/binder_process.h>

using aidl::vendor::demo::rpi4::Rpi4;

int main() {
    ALOGI("Rpi4 service is starting...");

    ABinderProcess_setThreadPoolMaxThreadCount(0);
    std::shared_ptr<Rpi4> rpi4 = ndk::SharedRefBase::make<Rpi4>();

    const std::string instance = std::string() + Rpi4::descriptor + "/default";
    ALOGI("Rpi4 service instance = %s", instance.c_str());
    binder_status_t status = AServiceManager_addService(rpi4->asBinder().get(), instance.c_str());
    CHECK_EQ(status, STATUS_OK);

    ALOGI("Rpi4 service starts to join service pool...");
    ABinderProcess_joinThreadPool();
    return EXIT_FAILURE;  // should not reached
}
