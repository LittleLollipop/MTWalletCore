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

import com.mt.wallet.core.account.Account;
import com.mt.wallet.core.Business;
import com.mt.wallet.core.Wallet;
import com.mt.wallet.core.safe.SafeCase;

/**
 * Created by sai on 2018/4/27.
 */

public class ChangePasswordBusiness implements Business {

    SafeCase password;
    SafeCase newPassword;
    Account account;
    CallBack callBack;
    String errorMessage;

    public ChangePasswordBusiness(SafeCase password, SafeCase newPassword, Account account, CallBack callBack){

        this.password = password;
        this.newPassword = newPassword;
        this.account = account;
        this.callBack = callBack;
    }


    @Override
    public void run(Wallet.Avatar avatar) {
        avatar.changePassword(this);
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void finish() {
        callBack.onOver(errorMessage);
    }

    public SafeCase getPassword() {
        return password;
    }

    public SafeCase getNewPassword() {
        return newPassword;
    }

    public void updateInfo(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getAccountAddress() {
        return account.getAddress();
    }

    public interface CallBack{
        void onOver(String errorInfo);
    }
}
