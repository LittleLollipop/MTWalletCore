package com.mt.wallet.core.business;

import com.mt.wallet.core.Business;
import com.mt.wallet.core.Wallet;
import com.mt.wallet.core.WalletApplication;
import com.mt.wallet.core.account.Account;
import com.mt.wallet.core.account.AccountData;
import com.mt.wallet.core.safe.SafeCase;

/**
 * Created by sai on 2018/6/5.
 */

public class DeleteAccountBusiness implements Business {

    Account account;
    SafeCase password;
    CallBack callBack;

    public DeleteAccountBusiness(Account account, SafeCase password, CallBack callBack){
        this.account = account;
        this.password = password;
        this.callBack = callBack;
    }

    @Override
    public void run(Wallet.Avatar avatar) {
        avatar.deleteWallet(this);
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void finish() {

    }

    public String getAddress() {
        return account.getAddress();
    }

    public SafeCase getPassword() {
        return password;
    }

    public void onDeleteFailed() {
        callBack.onDeleteFailed();
    }

    public void onDeleteSucceed() {

        WalletApplication.getInstance().getWallet().deleteAccount(account);

        callBack.onDeleteSucceed();
    }

    public interface CallBack{
        void onDeleteFailed();

        void onDeleteSucceed();
    }
}
