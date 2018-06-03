package com.mt.wallet.core;

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

import android.os.Handler;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mt.wallet.core.safe.VMChecker;
import com.orhanobut.hawk.Hawk;
import com.sai.frame.footstone.MVTApplication;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import static com.mt.wallet.core.Config.NETCONFIG;
import static com.mt.wallet.core.Wallet.Config.TYPE_ETH;

/**
 * Created by sai on 2018/5/24.
 */

public abstract class WalletApplication extends MVTApplication {

    static WalletApplication instance;
    RequestQueue queue;
    Wallet wallet;

    public static WalletApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        VMChecker.checkVM();
    }

    public static RequestQueue getQueue() {
        return getInstance().queue;
    }

    public void start(Handler handler) {

        Hawk.init(this).build();
        wallet = new Wallet(new Wallet.Config(TYPE_ETH, NETCONFIG));
        queue = Volley.newRequestQueue(WalletApplication.getInstance());
    }

    public Wallet getWallet(){
        return wallet;
    }

    public static String getID() {
        return "ID";
    }
}
