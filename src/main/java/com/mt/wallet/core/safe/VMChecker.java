package com.mt.wallet.core.safe;

/**
 * Copyright 2018 MyToken
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.DataOutputStream;

/**
 * Created by sai on 2018/5/28.
 */

public class VMChecker {

    static int zygoteInitCallCount = 0;

    private VMChecker() {
        throw new IllegalStateException("Utility class");
    }

    public static final void checkVM(){

        zygoteInitCallCount = 0;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for(StackTraceElement stackTraceElement : stackTraceElements){
            checkXposed(stackTraceElement);
            checkCydiaSubstrate(stackTraceElement);
        }
        checkRoot();
    }

    public static final void checkRoot(){

        Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();

            if (exitValue == 0) {
                throw new RuntimeException("root");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static final void checkXposed(StackTraceElement stackTraceElement){

        if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge")
                && stackTraceElement.getMethodName().equals("main")) {
            throw new RuntimeException("xposed");
        }
        if (stackTraceElement.getClassName().equals("de.robv.android.xposed.XposedBridge")
                && stackTraceElement.getMethodName().equals("handleHookedMethod")) {
            throw new RuntimeException("xposed");
        }

    }

    public static final void checkCydiaSubstrate(StackTraceElement stackTraceElement){

        if (stackTraceElement.getClassName().equals("com.android.internal.os.ZygoteInit")) {
            zygoteInitCallCount++;
            if (zygoteInitCallCount == 2) {
                throw new RuntimeException("Cydia Substrate");
            }
        }

        if (stackTraceElement.getClassName().equals("com.saurik.substrate.MS$2")
                && stackTraceElement.getMethodName().equals("invoked")) {
            throw new RuntimeException("Cydia Substrate");
        }
    }



}
