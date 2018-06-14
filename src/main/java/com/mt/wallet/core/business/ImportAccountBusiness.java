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

import com.mt.wallet.core.Config;
import com.mt.wallet.core.account.Account;
import com.mt.wallet.core.account.AccountData;
import com.mt.wallet.core.Business;
import com.mt.wallet.core.Wallet;
import com.mt.wallet.core.safe.SafeCase;

import org.web3j.utils.Numeric;

/**
 * Created by Sai on 2018/4/3.
 */

public class ImportAccountBusiness implements Business {

    public static final int IMPORT_TYPE_KEYSTORE = 1;
    public static final int IMPORT_TYPE_ECDSAKEY = 2;

    int type;

    String walletName;
    String keyText;
    String passphrase;
    SafeCase newPassphrase;
    CallBack callBack;
    String walletType;
    int accountNumber = -1;

    String info;

    public ImportAccountBusiness(String walletName, String keyText, String passphrase, SafeCase newPassphrase, CallBack callBack, int type){

        this.walletName = walletName;
        this.keyText = keyText;
        this.passphrase = passphrase;
        this.newPassphrase = newPassphrase;
        this.callBack = callBack;
        this.type = type;
    }

    public ImportAccountBusiness(String walletName, String privateKey, SafeCase newPassphrase, CallBack callBack){

        this(walletName, privateKey, null, newPassphrase, callBack, IMPORT_TYPE_ECDSAKEY);
    }

    @Override
    public void run(Wallet.Avatar avatar) {

        walletType = avatar.getWalletType();
        avatar.importAccount(this);
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void finish() {

        if(accountNumber == -1){
            if(callBack != null)
                callBack.onImportOver(accountNumber, info);
        }else{
            Account account = new Account(walletName, info);
            account.addContracts(Config.ETH_FLAG);
            AccountData.putAccount(account);
            if(callBack != null)
                callBack.onImportOver(accountNumber, info);
        }
    }

    public String getKeyText() {
        return keyText;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public SafeCase getNewPassphrase() {
        return newPassphrase;
    }

    public void updateInfo(String info, int i) {

        this.info = info;
        accountNumber = i;
    }

    public int getType() {
        return type;
    }

    public byte[] getECDSAKey() {
        return Numeric.hexStringToByteArray(keyText);
    }

    public interface CallBack{

        void onImportOver(int accountNumber, String info);
    }
}
