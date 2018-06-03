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

import org.ethereum.geth.Account;

/**
 * Created by Sai on 2018/4/3.
 */

public class CreateAccountBusiness implements Business {

    String passphrase;
    CallBack callBack;
    String addressInfo;
    Account account;

    public CreateAccountBusiness(String passphrase, CallBack callBack){

        this.passphrase = passphrase;
        this.callBack = callBack;
    }

    @Override
    public void run(Wallet.Avatar avatar) {
        avatar.createAccount(this);
    }

    @Override
    public String getInfo() {
        return addressInfo;
    }

    @Override
    public void finish() {

        if(account == null)
            callBack.onCreateFinished(addressInfo);
        else
            callBack.onCreateFinished(account);
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void updateInfo(String address) {
        addressInfo = address;
    }

    public void updateAccount(Account account) {
        this.account = account;
    }

    public interface CallBack{

        void onCreateFinished(String info);

        void onCreateFinished(Account account);
    }
}
