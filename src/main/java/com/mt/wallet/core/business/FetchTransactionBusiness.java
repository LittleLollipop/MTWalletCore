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

import com.mt.wallet.core.TransactionInfo;
import com.mt.wallet.core.Business;
import com.mt.wallet.core.Wallet;


/**
 * Created by sai on 2018/4/9.
 */

public class FetchTransactionBusiness implements Business {

    String txHash;
    String info;
    CallBack callBack;
    TransactionInfo transactionInfo;

    public FetchTransactionBusiness(String txHash, CallBack callBack){
        this.txHash = txHash;
        this.callBack = callBack;
    }

    @Override
    public void run(Wallet.Avatar avatar) {

        transactionInfo = avatar.getTransactionInfo(txHash);
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void finish() {
        callBack.onFetchOver(transactionInfo);
    }

    public interface CallBack{

        void onFetchOver(TransactionInfo transactionInfo);
    }
}
