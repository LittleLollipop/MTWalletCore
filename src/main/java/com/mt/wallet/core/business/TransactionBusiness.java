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

import com.mt.wallet.core.account.AccountData;
import com.mt.wallet.core.Business;
import com.mt.wallet.core.Wallet;

import java.math.BigDecimal;

/**
 * Created by Sai on 2018/4/4.
 */

public class TransactionBusiness implements Business {

    int accountNumber;
    String passphrase;
    String to;
    String value;
    CallBack callBack;
    BigDecimal gasPrice;
    BigDecimal gasLimit;
    protected String hexData;

    String info;
    String address;

    protected TransactionBusiness(){

    }

    public TransactionBusiness(int accountNumber, String passphrase, String to, String value, BigDecimal gasPrice, BigDecimal gasLimit, String hexData, CallBack callBack){

        this.accountNumber = accountNumber;
        this.passphrase = passphrase;
        this.to = to;
        this.value = value;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.hexData = hexData;
        this.callBack = callBack;
    }

    @Override
    public void run(Wallet.Avatar avatar) {

        address = avatar.getAccountAddress(accountNumber);
        avatar.SendTransaction(this);
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void finish() {
        //asynchronous request，maybe before then function updateInfo
    }

    public String getValue() {
        return value;
    }

    public String getToAddress() {
        return to;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void updateInfo(String info) {

        AccountData.addTransaction(address, info);
        this.info = info;
        callBack.onTransactionBeenSend(info);
    }

    public void updateErrorInfo(String info){
        callBack.onError(info);
    }

    public BigDecimal getGasPrice() {
        return gasPrice;
    }

    public BigDecimal getGasLimit() {
        return gasLimit;
    }

    public String getData() {

        if(hexData == null)
            return "";

        return hexData;
    }

    public interface CallBack{

        void onTransactionBeenSend(String info);

        void onError(String info);
    }
}
