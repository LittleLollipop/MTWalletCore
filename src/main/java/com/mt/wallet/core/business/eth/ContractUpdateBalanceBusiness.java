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

import com.mt.wallet.core.Wallet;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.exceptions.MessageDecodingException;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by sai on 2018/4/8.
 */

public class ContractUpdateBalanceBusiness extends ContractBusiness {

    BigDecimal balance;
    String address;
    CallBack callBack;

    public ContractUpdateBalanceBusiness(ContractBusiness contractBusiness, String address, CallBack callBack) {

        super(contractBusiness, null);
        this.address = address;
        this.callBack = callBack;
    }

    public ContractUpdateBalanceBusiness(String contractAddress, BigInteger decimals, String address, CallBack callBack) {

        super(null, contractAddress);
        this.address = address;
        this.callBack = callBack;
        this.decimals = decimals;
    }

    @Override
    public void updateInfo(String info) {

        try {
            this.balance = new BigDecimal(Numeric.decodeQuantity(info)).divide(BigDecimal.TEN.pow(decimals.intValue()));
        }catch (MessageDecodingException e){
            this.balance = new BigDecimal(0);
        }
    }

    @Override
    public void run(Wallet.Avatar avatar) {

        String data = FunctionEncoder.encode(new Function(
                "balanceOf",
                Arrays.asList((Type) new Address(address)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Type>() {})));

        Transaction transaction = Transaction.createEthCallTransaction(
                address, contractAddress, data);

        avatar.ethCall(transaction, this);
    }

    @Override
    public void finish() {

        callBack.onUpdateBalance(balance);
    }

    public interface CallBack{

        void onUpdateBalance(BigDecimal balanceInfo);
    }
}
