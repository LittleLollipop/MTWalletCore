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
import com.mt.wallet.core.TransactionInfo;
import com.mt.wallet.core.Wallet;

import java.util.List;

/**
 * Created by sai on 2018/4/9.
 */

public class FetchHistoryBusiness implements Business {

    String address;

    CallBack callBack;

    List<TransactionInfo> info;

    public FetchHistoryBusiness(String address, CallBack callBack){

        this.address = address;
        this.callBack = callBack;
    }

    @Override
    public void run(Wallet.Avatar avatar) {
        avatar.getHistory(this);
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void finish() {

        /** This method is executed after the request is added to the queue,
         *  and do not attempt to process requests here.
         */
    }

    public String getAddress() {
        return address;
    }

    public void updateInfo(List<? extends TransactionInfo> info) {

        this.info = (List<TransactionInfo>) info;
        callBack.onFetchOver(this.info);
    }

    public void updateErrorInfo(String message) {
        callBack.onError(message);
    }

    public boolean isContract() {
        return false;
    }

    public String getContractAddress() {
        return null;
    }

    public interface CallBack{

        void onFetchOver(List<TransactionInfo> info);

        void onError(String message);
    }
}
