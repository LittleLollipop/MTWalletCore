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

/**
 * Created by sai on 2018/4/23.
 */

public class TokenInfo {

    public static final int TYPE_ETH = 1;
    public static final int TYPE_ETH_TOKEN = 2;

    protected int type;

    protected String name;

    protected String symbol;

    protected String logo;

    protected String contract;

    protected int decimals;

    protected int suggest_gas;

    protected String is_infrastructure;

    protected String created_at;

    public String getName() {
        return name;
    }

    public int getType(){
        return type;
    }

    public boolean isContract() {
        return type == TYPE_ETH_TOKEN;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getContract() {

        return contract;
    }

    public void setContract(String contract) {

        if(contract.equals(Config.ETH_FLAG)){
            type = TYPE_ETH;
        }else{
            type = TYPE_ETH_TOKEN;
        }

        this.contract = contract;
    }

    public int getDecimals() {

        if(type == TYPE_ETH)
            return 18;
        else if (type == TYPE_ETH_TOKEN)
            return decimals;

        return -1;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public int getSuggest_gas() {
        return suggest_gas;
    }

    public void setSuggest_gas(int suggest_gas) {
        this.suggest_gas = suggest_gas;
    }

    public String getIs_infrastructure() {
        return is_infrastructure;
    }

    public void setIs_infrastructure(String is_infrastructure) {
        this.is_infrastructure = is_infrastructure;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void initType() {

        if(Integer.parseInt(is_infrastructure) == 1)
            type = TYPE_ETH;
        else
            type = TYPE_ETH_TOKEN;
    }
}
