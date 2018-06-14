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

import com.mt.wallet.core.business.TransactionBusiness;
import com.mt.wallet.core.TokenInfo;
import com.mt.wallet.core.Wallet;
import com.mt.wallet.core.safe.SafeCase;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Int;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by sai on 2018/5/9.
 */

public class ContractTransactionBusiness extends TransactionBusiness {

    String toAddress;
    String tokenValue;
    TokenInfo tokenInfo;

    public ContractTransactionBusiness(int accountNumber, SafeCase passphrase, String to, String value, BigDecimal gasPrice, BigDecimal gasLimit, TokenInfo tokenInfo, CallBack callBack) {

        super(accountNumber, passphrase, tokenInfo.getContract(), "0", gasPrice, gasLimit, null, callBack);
        this.tokenValue = value;
        this.toAddress = to;
        this.tokenInfo = tokenInfo;
    }

    @Override
    public void run(Wallet.Avatar avatar) {

        Function function = new Function(
                "transfer",
                Arrays.asList((Type) new Address(toAddress), new Uint256(new BigDecimal(tokenValue).multiply(BigDecimal.TEN.pow(tokenInfo.getDecimals())).toBigInteger())),
                Arrays.<TypeReference<?>>asList(new TypeReference<Type>() {}));

        super.hexData = FunctionEncoder.encode(function);

        super.run(avatar);
    }
}
