package com.mt.wallet.core.loopring;

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
import android.util.Log;

import com.mt.wallet.core.WalletApplication;
import com.mt.wallet.core.walletImp.EthWallet;
import com.mt.wallet.core.walletImp.KeyManager;

import org.ethereum.geth.Account;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;


import static com.mt.wallet.core.loopring.Loopring.ORDERWALLETADDRESS;

/**
 * Created by sai on 2018/5/30.
 */
@Keep
public class OriginalOrder {

    String delegate;
    String address;
    String market;
    String tokenBuy;
    String tokenSell;
    double amountBuy;
    double amountSell;
    long validSince;
    long validUntil;
    double lrcFee;
    boolean buyNoMoreThanAmountB;
    String side;
    String hash;
    String walletAddress;
    String authPrivateKey;
    String authAddr;
    long marginSplitPercentage;
    String v;
    String r;
    String s;

    public OriginalOrder(){

    }

    public OriginalOrder(String delegate, final String address, String side, String tokenSell, String tokenBuy, long validSince, long validUntil,
                         double amountBuy, double amountSell, double lrcFee, boolean buyNoMoreThanAmountB, String market, String hash,
                         String v, String r, String s){
        this.delegate = delegate;
        this.address = address;
        this.side = side;
        this.tokenSell = tokenSell;
        this.tokenBuy = tokenBuy;
        this.validSince = validSince;
        this.validUntil = validUntil;
        this.amountBuy = amountBuy;
        this.amountSell = amountSell;
        this.lrcFee = lrcFee;
        this.buyNoMoreThanAmountB = buyNoMoreThanAmountB;
        this.market = market;
        this.hash = hash;
        this.v = v;
        this.r = r;
        this.s = s;
        walletAddress = ORDERWALLETADDRESS;
        try {
            KeyManager keyManager = KeyManager.newKeyManager(WalletApplication.getInstance().getFilesDir().getAbsolutePath() + "/loopring");
            Account account = keyManager.newAccount("passphrase");
            ECKeyPair keyPair = EthWallet.decrypt("exportKey", new String(keyManager.getKeystore().exportKey(account, "passphrase", "passphrase")));
            if(keyPair != null){
                authPrivateKey = Numeric.toHexStringNoPrefix(keyPair.getPrivateKey());
                authAddr = account.getAddress().getHex();
            }else{
                Log.e("create order", "failed");
            }
        } catch (Exception e) {
            Log.e("create order", "failed", e);
        }

    }

}
