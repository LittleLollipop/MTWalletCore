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

import java.util.List;

/**
 * Created by sai on 2018/4/27.
 */

public class ExportKeyBusiness implements Business {

    public static final int TYPE_PRIVATE_KEY = 1;
    public static final int TYPE_KEYSTORE = 2;
    public static final int TYPE_MNEMONIC = 3;

    int type;
    SafeCase password;
    String newPassword;
    CallBack callBack;
    Account account;

    String walletType;
    String key;
    String[] mnemonic;

    public ExportKeyBusiness(int type, SafeCase password, CallBack callBack, Account account, String newPassword){

        this.type = type;
        this.password = password;
        this.callBack = callBack;
        this.account = account;
        this.newPassword = newPassword;
    }

    @Override
    public void run(Wallet.Avatar avatar) {

        walletType = avatar.getWalletType();
        avatar.exportKey(this);
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void finish() {

        if(type == TYPE_MNEMONIC){
            callBack.onExported(mnemonic);
        }else{
            callBack.onExported(key);
        }
    }

    public int getExportType() {
        return type;
    }

    public SafeCase getPassword() {
        return password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void updateKey(String key) {
        this.key = key;
    }

    public List<String> getMnemonicInfo() {
        return account.getWordInfo();
    }

    public void updateMnemonic(String[] strings) {
        this.mnemonic = strings;
    }

    public String getAccountAddress() {
        return account.getAddress();
    }

    public interface CallBack{

        void onExported(String key);

        void onExported(String[] mnemonic);
    }
}
