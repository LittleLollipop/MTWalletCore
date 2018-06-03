package com.mt.wallet.core.business.eth;

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

import java.math.BigInteger;

/**
 * Created by sai on 2018/4/8.
 */

public abstract class ContractBusiness implements Business {

    String name;
    String symbol;
    BigInteger decimals;
    BigInteger totalSupply;

    protected String contractAddress;

    public ContractBusiness(ContractBusiness contractBusiness, String contractAddress){

        if(contractBusiness != null){
            name = contractBusiness.name;
            symbol = contractBusiness.symbol;
            decimals = contractBusiness.decimals;
            totalSupply = contractBusiness.totalSupply;
            this.contractAddress = contractBusiness.contractAddress;
        }else{
            this.contractAddress = contractAddress;
        }

    }

    @Override
    public String getInfo() {
        return null;
    }

    public abstract void updateInfo(String info);

    public String getSymbol() {
        return symbol;
    }

    public int getDecimals() {
        return decimals.intValue();
    }
}