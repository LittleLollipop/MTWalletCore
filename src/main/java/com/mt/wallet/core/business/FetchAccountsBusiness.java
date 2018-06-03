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

import java.util.List;

/**
 * Created by Sai on 2018/4/3.
 */

public class FetchAccountsBusiness implements Business {

    CallBack callBack;
    List<String> accounts;
    String accountsStr;

    public FetchAccountsBusiness(CallBack callBack){
        this.callBack = callBack;
    }

    @Override
    public void run(Wallet.Avatar avatar) {
        avatar.getAccounts(this, true);
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void finish() {

        if(accounts == null)
            callBack.onFetchFinish(accountsStr);
        else
            callBack.onFetchFinish(accounts);
    }

    public void updateInfo(Object accounts) {

        if(accounts instanceof List){
            this.accounts = (List<String>) accounts;
        }else{
            this.accountsStr = (String) accounts;
        }
    }

    public interface CallBack{

        void onFetchFinish(Object info);
    }
}
