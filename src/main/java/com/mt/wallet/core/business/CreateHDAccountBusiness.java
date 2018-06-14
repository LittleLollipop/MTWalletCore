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

import android.text.TextUtils;
import android.util.Log;

import com.mt.wallet.core.Config;
import com.mt.wallet.core.WalletApplication;
import com.mt.wallet.core.account.Account;
import com.mt.wallet.core.account.AccountData;
import com.mt.wallet.core.Wallet;
import com.mt.wallet.core.safe.SafeCase;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sai on 2018/4/3.
 */

public class CreateHDAccountBusiness extends ImportAccountBusiness {

    static final String BIP39_ENGLISH_SHA256 = "ad90bf3beb7b0eb7e5acd74727dc0da96e0a280a258354e7293fb7e211ac03db";

    String mnemonicPassphrase;
    CallBack callBackNed;
    String addressInfo;

    byte[] privateKey;
    List<String> mWordList;
    List<String> wordInfo;
    String mnemonicPathStr;

    public CreateHDAccountBusiness(String name, String key, SafeCase passphrase, String mnemonicPassphrase, String mnemonicPathStr, CallBack callBack){
        super(name, null, null, passphrase, null, 0);
        this.mnemonicPassphrase = mnemonicPassphrase;
        this.callBackNed = callBack;
        this.mnemonicPathStr = mnemonicPathStr;
        if(!TextUtils.isEmpty(key)){
            mWordList = Arrays.asList(key.split(" "));
        }
    }

    @Override
    public void run(Wallet.Avatar avatar) {

        privateKey = buildHDWallet(mnemonicPassphrase);

        if(privateKey.length == 0){

            return;
        }

        avatar.importAccount(this);

        wordInfo = avatar.saveMnemonic(mWordList, newPassphrase);
    }

    public void verifySucceed(){
        Account account = new Account(walletName, addressInfo);
        account.setWordInfo(wordInfo);
        account.addContracts(Config.ETH_FLAG);
        AccountData.putAccount(account);
    }

    private byte[] buildHDWallet(String password) {

        try {
            int purpose = 44;

            if(mWordList == null){

                int nbWords = 12;

                int len = (nbWords / 3) * 4;

                SecureRandom random = new SecureRandom();
                byte[] entropy = new byte[len];
                random.nextBytes(entropy);

                InputStream wis = WalletApplication.getInstance().getResources().getAssets().open("bip39/en.txt");
                MnemonicCode mc = new MnemonicCode(wis, BIP39_ENGLISH_SHA256);

                mWordList = mc.toMnemonic(entropy);
            }

            byte[] hdSeed = MnemonicCode.toSeed(mWordList, password);

            DeterministicKey mKey = HDKeyDerivation.createMasterPrivateKey(hdSeed);

            DeterministicKey t1 = HDKeyDerivation.deriveChildKey(mKey, purpose| ChildNumber.HARDENED_BIT);
            DeterministicKey mRoot = HDKeyDerivation.deriveChildKey(t1, 60|ChildNumber.HARDENED_BIT);
            mRoot = HDKeyDerivation.deriveChildKey(mRoot, 0|ChildNumber.HARDENED_BIT);
            mRoot = HDKeyDerivation.deriveChildKey(mRoot, 0);
            mRoot = HDKeyDerivation.deriveChildKey(mRoot, 0);

            return mRoot.getPrivKeyBytes();
        } catch (IOException|MnemonicException.MnemonicLengthException  e) {
            Log.e(getClass().getName(), "", e);
        }

        return new byte[0];
    }

    @Override
    public String getInfo() {
        return addressInfo;
    }

    @Override
    public void finish() {

        if(TextUtils.isEmpty(addressInfo) || mWordList == null){
            callBackNed.onCreateError();
        }else{
            callBackNed.onCreateFinished(addressInfo, mWordList);
        }
    }

    @Override
    public String getPassphrase() {
        return passphrase;
    }

    @Override
    public void updateInfo(String address, int i) {

        super.updateInfo(address, i);
        addressInfo = address;
    }

    @Override
    public int getType() {
        return IMPORT_TYPE_ECDSAKEY;
    }

    @Override
    public byte[] getECDSAKey(){
        return privateKey;
    }

    public interface CallBack{

        void onCreateFinished(String addressInfo, List<String> mWordList);

        void onCreateError();
    }
}
