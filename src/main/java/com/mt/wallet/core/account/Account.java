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

import android.support.annotation.Keep;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sai on 2018/4/16.
 */
@Keep
public class Account {

    private String name;
    private String address;
    private List<String> contracts;
    private List<String> hidingContracts;
    private List<String> wordInfo;
    private List<String> txs;

    public Account(String walletName, String addressInfo) {
        this.name = walletName;
        this.address = addressInfo;
    }

    public List<String> getContracts() {
        if(contracts == null)
            return new ArrayList<>();

        return contracts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public List<String> getWordInfo() {
        return wordInfo;
    }

    public void setWordInfo(List<String> wordInfo) {
        this.wordInfo = wordInfo;
    }

    public void addContracts(String contract) {
        if(contracts == null)
            contracts = new ArrayList<>();

        if (!contracts.contains(contract))
            contracts.add(contract);
    }

    public List<String> getHidingContracts() {
        if(hidingContracts == null)
            return new ArrayList<>();

        return hidingContracts;
    }

    public void setHidingContracts(List<String> hidingContracts) {
        this.hidingContracts = hidingContracts;
    }

    public void setContracts(List<String> contracts) {
        this.contracts = contracts;
    }

    public void removeTxHash(String info){
        txs.remove(info);
    }

    public void addTxHash(String info) {
        if(txs == null)
            txs = new ArrayList<>();

        txs.add(info);
    }

    public List<String> getTxs() {
        if(txs == null)
            return new ArrayList<>();

        return txs;
    }

    public void setTxs(List<String> txs) {
        this.txs = txs;
    }
}
