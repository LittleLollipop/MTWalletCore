package com.mt.wallet.core.account;

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

import android.util.SparseArray;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sai on 2018/4/9.
 */

public class AccountData {

    private static final String TRANSACTIONHASHS = "TRANSACTIONHASHS";

    private static final String ACCOUNTS = "accounts";

    private AccountData() {
        throw new IllegalStateException("Utility class");
    }

    public static void addTransaction(String address, String info) {

        ArrayList<String> hashs = new ArrayList<>();

        if(Hawk.contains(address + TRANSACTIONHASHS)){
            hashs = Hawk.get(address + TRANSACTIONHASHS);
        }
        hashs.add(info);
        Hawk.put(address + TRANSACTIONHASHS, hashs);
    }

    public static List<String> getTransactionsHash(String address) {

        if(Hawk.contains(address + TRANSACTIONHASHS))
            return Hawk.get(address + TRANSACTIONHASHS);
        else
            return new ArrayList<>();
    }

    public static SparseArray<Account> getAccounts(){
        SparseArray<Account> accounts = new SparseArray<>();
        SparseArray<String> accountsString = getAccountsString();

        Gson gson = new Gson();
        for(int i = accountsString.size() - 1; i >= 0; i--){
            accounts.append(accounts.size(),gson.fromJson(accountsString.get(i), Account.class));
        }
        return accounts;
    }

    public static SparseArray<String> getAccountsString(){
        if(Hawk.contains(ACCOUNTS)) {
            return Hawk.get(ACCOUNTS);
        }else{
            return new SparseArray<>();
        }
    }

    public static void putAccount(Account account){
        SparseArray<String> accounts = getAccountsString();
        accounts.append(accounts.size(), account.toJson());
        Hawk.put(ACCOUNTS, accounts);
    }

    public static void updateAccount(Account newAccount){
        Gson gson = new Gson();
        SparseArray<String> accounts = getAccountsString();
        for(int i = 0; i < accounts.size(); i++){
            if(gson.fromJson(accounts.get(i), Account.class).getAddress().equals(newAccount.getAddress()))
                accounts.put(i, newAccount.toJson());
        }
        Hawk.put(ACCOUNTS, accounts);
    }
}
