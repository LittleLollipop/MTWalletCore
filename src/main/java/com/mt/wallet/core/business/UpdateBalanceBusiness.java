package com.mt.wallet.core.business;

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

import com.mt.wallet.core.Business;
import com.mt.wallet.core.Wallet;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Sai on 2018/4/3.
 */

public class UpdateBalanceBusiness implements Business {

    String address;
    String balanceInfo;
    CallBack callBack;

    public UpdateBalanceBusiness(@NotNull String addressInfo,@NotNull CallBack callBack) {

        this.address = addressInfo;
        this.callBack = callBack;
    }

    @Override
    public void run(Wallet.Avatar avatar) {
        avatar.getBalance(this);
    }

    @Override
    public String getInfo() {
        return balanceInfo;
    }

    @Override
    public void finish() {
        callBack.onUpdateBalance(balanceInfo);
    }

    public String getAddress() {
        return address;
    }

    public void updateInfo(String balanceInfo) {
        this.balanceInfo = balanceInfo;
    }

    public interface CallBack{

        void onUpdateBalance(String balanceInfo);
    }

}
