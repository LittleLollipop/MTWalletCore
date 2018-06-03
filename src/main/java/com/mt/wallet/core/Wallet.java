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
import android.os.Handler;
import android.util.SparseArray;

import com.mt.wallet.core.account.Account;
import com.mt.wallet.core.account.AccountData;
import com.mt.wallet.core.business.ChangePasswordBusiness;
import com.mt.wallet.core.business.ExportKeyBusiness;
import com.mt.wallet.core.business.FetchHistoryBusiness;
import com.mt.wallet.core.business.eth.ContractBusiness;
import com.mt.wallet.core.business.CreateAccountBusiness;
import com.mt.wallet.core.business.FetchAccountsBusiness;
import com.mt.wallet.core.business.ImportAccountBusiness;
import com.mt.wallet.core.business.TransactionBusiness;
import com.mt.wallet.core.business.UpdateBalanceBusiness;
import com.mt.wallet.core.walletImp.EthWallet;
import com.orhanobut.hawk.Hawk;
import com.sai.frame.footstone.base.StateMachine;

import org.jetbrains.annotations.NotNull;
import org.web3j.protocol.core.methods.request.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sai on 2018/4/3.
 */

public class Wallet extends StateMachine {

    public static final String RUN_BUSINESS_STATE_SUCCEED = "succeed";
    public static final String RUN_BUSINESS_STATE_BUSY = "busy";

    private static final String DEFAULTACCOUNT = "DEFAULTACCOUNT";

    public static final int STATE_INIT = 1;
    public static final int STATE_WAITING_BUSINESS = 2;
    public static final int STATE_RUNNING_BUSINESS = 3;
    public static final int STATE_STOPED = 4;

    State initNed = new State(STATE_INIT, "STATE_INIT");
    State waiting = new State(STATE_WAITING_BUSINESS, "STATE_WAITING_BUSINESS");
    State running = new State(STATE_RUNNING_BUSINESS, "STATE_RUNNING_BUSINESS");
    State stopedNed = new State(STATE_STOPED, "STATE_STOPED");

    Config config;
    Avatar avatar;
    SparseArray<Class> avatarAlley = new SparseArray();
    Business next;
    WalletAccount accountNow;
    ArrayList<Business> taskList = new ArrayList<>();

    public Wallet(Config config){

        this.config = config;

        ArrayList allState = new ArrayList();
        allState.add(initNed);
        allState.add(waiting);
        allState.add(running);
        allState.add(stopedNed);

        init(allState, initNed, true);
    }

    @Override
    public boolean checkChange_InMachineThread(State newState, State state) {

        switch (state.stateNumber){
            case STATE_INIT:
                return true;
            case STATE_WAITING_BUSINESS:
                if (newState.stateNumber == STATE_INIT)
                    return false;
                else
                    return true;
            case STATE_RUNNING_BUSINESS:
                if(newState.stateNumber == STATE_WAITING_BUSINESS)
                    return true;
                else
                    return false;
            case STATE_STOPED:
                return false;
            default:
                break;
        }

        return false;
    }

    @Override
    public void stateIn_InMachineThread(State state, Handler handler) {

        switch (state.stateNumber){
            case STATE_INIT:
                initWallet();
                break;
            case STATE_RUNNING_BUSINESS:
                executeBusiness();
                break;
            case STATE_WAITING_BUSINESS:
                if(!taskList.isEmpty()){
                    next = taskList.get(0);
                    taskList.remove(0);
                    changeState(running);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void stateLeave_InMachineThread(State state, Handler handler) {

        switch (state.stateNumber){
            case STATE_RUNNING_BUSINESS:
                final Business business = next;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        business.finish();
                    }
                });
                next = null;
                break;
            default:
                break;
        }
    }

    private void executeBusiness() {

        next.run(avatar);
        changeState(waiting);
    }

    private void initWallet() {

        avatarAlley.put(Config.TYPE_ETH, EthWallet.class);

        avatar = Avatar.init(this);

        changeState(waiting);
    }

    public String sendBusiness(@NotNull Business business) {

        if(getStateNow() == waiting && taskList.isEmpty()){
            next = business;
            changeState(running);
            return RUN_BUSINESS_STATE_SUCCEED;
        }else{
            taskList.add(business);
            return RUN_BUSINESS_STATE_BUSY;
        }

    }

    public Account getAccountNow() {

        if(accountNow == null){
            updateAccount();
        }
        return accountNow;
    }

    public Object getInfo(String infoFlag) {
        return avatar.getInfo(infoFlag);
    }

    public SparseArray<Account> getAccounts() {
        return AccountData.getAccounts();
    }

    public void updateAccount() {

        if(accountNow == null){
            accountNow = new WalletAccount(AccountData.getAccounts().get(Hawk.get(DEFAULTACCOUNT, 0).intValue()));
        }else{
            accountNow.account = AccountData.getAccounts().get(Hawk.get(DEFAULTACCOUNT, 0).intValue());
        }
    }

    public void changeDefaultAccount(Account account) {

        SparseArray<Account> accounts = AccountData.getAccounts();

        for(int i = accounts.size() - 1; i >= 0; i--){
            if(account.getName().equals(accounts.get(i).getName())){
                Hawk.put(DEFAULTACCOUNT, i);
                break;
            }
        }
        updateAccount();
    }

    public static class Config{

        public static final int TYPE_ETH = 1;
        private int type;
        private String env;

        public Config(int type, String env){
            this.env = env;
            this.type = type;
        }

        public String getEnv(){
            return env;
        }
    }

    public static class WalletAccount extends Account {

        Account account;

        public WalletAccount(Account account){
            super(null, null);
            this.account = account;
        }

        @Override
        public List<String> getContracts() {
            return account.getContracts();
        }

        @Override
        public String getName() {
            return account.getName();
        }

        @Override
        public void setName(String name) {
            account.setName(name);
        }

        @Override
        public String getAddress() {
            return account.getAddress();
        }

        @Override
        public void setAddress(String address) {
            account.setAddress(address);
        }

        public List<String> getHidingContracts() {
            return account.getHidingContracts();
        }

        @Override
        public void setHidingContracts(List<String> hidingContracts) {
            account.setHidingContracts(hidingContracts);
        }

        @Override
        public void setContracts(List<String> contracts) {
            account.setContracts(contracts);
        }

        @Override
        public List<String> getTxs() {
            return account.getTxs();
        }

        public void addTxHash(String info) {
            account.addTxHash(info);
            AccountData.updateAccount(account);
            WalletApplication.getInstance().getWallet().updateAccount();
        }

        @Override
        public String toJson() {
            return account.toJson();
        }
    }

    public static abstract class Avatar{

        public static Avatar init(Wallet wallet) {

            try {
                Avatar avatar = ((Avatar)wallet.avatarAlley.get(wallet.config.type).newInstance());
                avatar.init(wallet.config);
                return avatar;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        public abstract void init(Config config);

        public abstract void getBalance(UpdateBalanceBusiness balanceBusiness);

        public abstract void createAccount(CreateAccountBusiness createAccountBusiness);

        public abstract void getAccounts(FetchAccountsBusiness fetchAccountsBusiness, boolean isList);

        public abstract void importAccount(ImportAccountBusiness importAccountBusiness);

        public abstract void SendTransaction(TransactionBusiness transactionBusiness);

        public abstract void ethCall(Transaction transaction, ContractBusiness contractBusiness);

        public abstract String getAccountAddress(int accountNumber);

        public abstract TransactionInfo getTransactionInfo(String hash);

        public abstract void getHistory(FetchHistoryBusiness business);

        public abstract Object getInfo(String infoFlag);

        public abstract void changePassword(ChangePasswordBusiness changePasswordBusiness);

        public abstract String getWalletType();

        public abstract void exportKey(ExportKeyBusiness exportKeyBusiness);

        public abstract List<String> saveMnemonic(List<String> mWordList, String passphrase);
    }
}
