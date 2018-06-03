package com.mt.wallet.core;

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

import android.text.TextUtils;

/**
 * Created by sai on 2018/5/9.
 */

public class UserTokenInfo extends TokenInfo {

    TokenInfo tokenInfo;
    protected String balance;

    public UserTokenInfo(TokenInfo tokenInfo){
        this.tokenInfo = tokenInfo;
    }

    public String getBalance() {

        if(TextUtils.isEmpty(balance))
            return "0";

        return balance;
    }

    public void setBalance(String balance){
        this.balance = balance;
    }

    @Override
    public String getName() {
        return tokenInfo.getName();
    }

    @Override
    public int getType() {
        return tokenInfo.getType();
    }

    @Override
    public boolean isContract() {
        return tokenInfo.isContract();
    }

    @Override
    public void setType(int type) {
        tokenInfo.setType(type);
    }

    @Override
    public void setName(String name) {
        tokenInfo.setName(name);
    }

    @Override
    public String getSymbol() {
        return tokenInfo.getSymbol();
    }

    @Override
    public void setSymbol(String symbol) {
        tokenInfo.setSymbol(symbol);
    }

    @Override
    public String getLogo() {
        return tokenInfo.getLogo();
    }

    @Override
    public void setLogo(String logo) {
        tokenInfo.setLogo(logo);
    }

    @Override
    public String getContract() {
        return tokenInfo.getContract();
    }

    @Override
    public void setContract(String contract) {
        tokenInfo.setContract(contract);
    }

    @Override
    public int getDecimals() {
        return tokenInfo.getDecimals();
    }

    @Override
    public void setDecimals(int decimals) {
        tokenInfo.setDecimals(decimals);
    }

    @Override
    public int getSuggest_gas() {
        return tokenInfo.getSuggest_gas();
    }

    @Override
    public void setSuggest_gas(int suggest_gas) {
        tokenInfo.setSuggest_gas(suggest_gas);
    }

    @Override
    public String getIs_infrastructure() {
        return tokenInfo.getIs_infrastructure();
    }

    @Override
    public void setIs_infrastructure(String is_infrastructure) {
        tokenInfo.setIs_infrastructure(is_infrastructure);
    }

    @Override
    public String getCreated_at() {
        return tokenInfo.getCreated_at();
    }

    @Override
    public void setCreated_at(String created_at) {
        tokenInfo.setCreated_at(created_at);
    }
}
