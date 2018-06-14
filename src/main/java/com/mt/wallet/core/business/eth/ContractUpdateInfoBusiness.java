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

import android.util.Log;

import com.mt.wallet.core.Wallet;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sai on 2018/4/8.
 */

public class ContractUpdateInfoBusiness extends ContractBusiness {

    private static final int STATE_NAME = 1;
    private static final int STATE_SYMBOL = 2;
    private static final int STATE_DECIMALS = 3;
    private static final int STATE_TOTALSUPPLY = 4;

    private int stateNow = 0;
    CallBack callBack;

    public ContractUpdateInfoBusiness(String contractAddress, CallBack callBack) {

        super(null, contractAddress);
        this.callBack = callBack;
    }

    @Override
    public void run(Wallet.Avatar avatar) {

        stateNow = STATE_NAME;
        String dataName = FunctionEncoder.encode(new Function(
                "name",
                new ArrayList<Type>(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {})));
        Transaction transactionName = Transaction.createEthCallTransaction(
                null,
                contractAddress,
                dataName
        );
        avatar.ethCall(transactionName, this);


        stateNow = STATE_SYMBOL;
        String dataSymbol = FunctionEncoder.encode(new Function(
                "symbol",
                new ArrayList<Type>(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {})));
        Transaction transactionSymbol = Transaction.createEthCallTransaction(
                null,
                contractAddress,
                dataSymbol
        );
        avatar.ethCall(transactionSymbol, this);


        stateNow = STATE_DECIMALS;
        String dataDecimals = FunctionEncoder.encode(new Function(
                "decimals",
                new ArrayList<Type>(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {})));
        Transaction transactionDecimals = Transaction.createEthCallTransaction(
                null,
                contractAddress,
                dataDecimals
        );
        avatar.ethCall(transactionDecimals, this);


        stateNow = STATE_TOTALSUPPLY;
        String dataTotalSupply = FunctionEncoder.encode(new Function(
                "totalSupply",
                new ArrayList<Type>(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {})));
        Transaction transactionTotalSupply = Transaction.createEthCallTransaction(
                null,
                contractAddress,
                dataTotalSupply
        );
        avatar.ethCall(transactionTotalSupply, this);
    }

    @Override
    public void finish() {

        callBack.onUpdateInfo(this, "\nname:" + name + "\nsymbol:" + symbol + "\ndecimals:" + decimals.intValue() + "\ntotalSupply:" + totalSupply.longValue());
    }

    @Override
    public void updateInfo(String info) {

        try {
            switch (stateNow){
                case STATE_NAME:
                    name = new String(Numeric.hexStringToByteArray(Numeric.toHexStringNoPrefix(Numeric.decodeQuantity(info))));
                    break;
                case STATE_SYMBOL:
                    symbol = new String(Numeric.hexStringToByteArray(Numeric.toHexStringNoPrefix(Numeric.decodeQuantity(info))));
                    break;
                case STATE_DECIMALS:

                    decimals = new BigDecimal(Numeric.decodeQuantity(info));
                    break;
                case STATE_TOTALSUPPLY:
                    totalSupply = new BigDecimal(Numeric.decodeQuantity(info));
                    break;
                default:
                    Log.d(getClass().getName(), "no type to update : " + info);
                    break;
            }
        }catch (Exception e){
            Log.e(getClass().getName(), "update info failed : " + info, e);
        }
    }

    public interface CallBack{

        void onUpdateInfo(ContractUpdateInfoBusiness contractUpdateInfoBusiness, String info);
    }
}
