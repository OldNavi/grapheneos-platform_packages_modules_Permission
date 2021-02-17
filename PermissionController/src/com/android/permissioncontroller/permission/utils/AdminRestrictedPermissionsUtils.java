/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.android.permissioncontroller.permission.utils;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Build;
import android.os.UserHandle;
import android.util.ArraySet;

/**
 * A class for dealing with permissions that the admin may not grant in certain configurations.
 */
public final class AdminRestrictedPermissionsUtils {
    /**
     * A set of permissions that the Profile Owner cannot grant and that the Device Owner
     * could potentially grant (depending on opt-out state).
     */
    private static final ArraySet<String> ADMIN_RESTRICTED_SENSORS_PERMISSIONS = new ArraySet<>();

    static {
        ADMIN_RESTRICTED_SENSORS_PERMISSIONS.add(Manifest.permission.ACCESS_FINE_LOCATION);
        ADMIN_RESTRICTED_SENSORS_PERMISSIONS.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        ADMIN_RESTRICTED_SENSORS_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        ADMIN_RESTRICTED_SENSORS_PERMISSIONS.add(Manifest.permission.CAMERA);
        ADMIN_RESTRICTED_SENSORS_PERMISSIONS.add(Manifest.permission.RECORD_AUDIO);
        ADMIN_RESTRICTED_SENSORS_PERMISSIONS.add(Manifest.permission.ACTIVITY_RECOGNITION);
        ADMIN_RESTRICTED_SENSORS_PERMISSIONS.add(Manifest.permission.BODY_SENSORS);
        // New S permissions - do not add unless running on S and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ADMIN_RESTRICTED_SENSORS_PERMISSIONS.add(Manifest.permission.BACKGROUND_CAMERA);
            ADMIN_RESTRICTED_SENSORS_PERMISSIONS.add(Manifest.permission.RECORD_BACKGROUND_AUDIO);
        }
    }

    /**
     * Returns true if the permission is one of the sensors-related permissions an admin of
     * the device may not be able to control in certain configurations.
     */
    public static boolean isPermissionRestrictedForAdmin(String permission) {
        return ADMIN_RESTRICTED_SENSORS_PERMISSIONS.contains(permission);
    }

    /**
     * Returns true if the admin may grant this permission, false otherwise.
     */
    public static boolean mayAdminGrantPermission(Context context, String permission, int userId) {
        if (!ADMIN_RESTRICTED_SENSORS_PERMISSIONS.contains(permission)) {
            return true;
        }
        Context userContext = context.createContextAsUser(UserHandle.of(userId), /* flags= */0);
        DevicePolicyManager dpm = userContext.getSystemService(DevicePolicyManager.class);
        return dpm.canAdminGrantSensorsPermissions();
    }

    /**
     * Returns true if the admin may grant this permission, false otherwise.
     */
    public static boolean mayAdminGrantPermission(String permission,
            boolean canAdminGrantSensorsPermissions) {
        if (!ADMIN_RESTRICTED_SENSORS_PERMISSIONS.contains(permission)) {
            return true;
        }

        return canAdminGrantSensorsPermissions;
    }
}
