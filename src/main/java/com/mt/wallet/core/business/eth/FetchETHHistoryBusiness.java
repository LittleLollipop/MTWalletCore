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

import android.text.TextUtils;

import com.mt.wallet.core.business.FetchHistoryBusiness;
import com.mt.wallet.core.TokenInfo;
import com.mt.wallet.core.TransactionInfo;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sai on 2018/4/24.
 */

public class FetchETHHistoryBusiness extends FetchHistoryBusiness {

    TokenInfo tokenInfo;

    public FetchETHHistoryBusiness(String address, CallBack callBack, @NotNull TokenInfo tokenInfo) {

        super(address, callBack);
        this.tokenInfo = tokenInfo;
    }

    @Override
    public void updateInfo(List<? extends TransactionInfo> info) {

        List<ETHTransactionInfo> transactionInfos = new ArrayList<>();

        for(TransactionInfo transactionInfo : info) {

            if(!TextUtils.isEmpty(transactionInfo.getTo()) && !TextUtils.isEmpty(transactionInfo.getFrom()))
                transactionInfos.add(new ETHTransactionInfo(transactionInfo, tokenInfo));
        }

        super.updateInfo(transactionInfos);
    }

    @Override
    public boolean isContract() {

        return tokenInfo.isContract();
    }

    @Override
    public String getContractAddress() {

        return tokenInfo.getContract();
    }

    class ETHTransactionInfo extends TransactionInfo{

        TransactionInfo transactionInfo;
        TokenInfo tokenInfo;

        public ETHTransactionInfo(TransactionInfo transactionInfo, TokenInfo tokenInfo) {

            this.transactionInfo = transactionInfo;
            this.tokenInfo = tokenInfo;
        }

        @Override
        public String getBlockNumber() {
            return transactionInfo.getBlockNumber();
        }

        @Override
        public void setBlockNumber(String blockNumber) {
            transactionInfo.setBlockNumber(blockNumber);
        }

        @Override
        public long getTime(){
            return transactionInfo.getTime();
        }

        @Override
        public String getTimeStamp() {

            Date d = new Date(Long.valueOf(transactionInfo.getTimeStamp() + "000"));
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return sf.format(d);
        }

        @Override
        public void setTimeStamp(String timeStamp) {
            transactionInfo.setTimeStamp(timeStamp);
        }

        @Override
        public String getHash() {
            return transactionInfo.getHash();
        }

        @Override
        public void setHash(String hash) {
            transactionInfo.setHash(hash);
        }

        @Override
        public String getNonce() {
            return transactionInfo.getNonce();
        }

        @Override
        public void setNonce(String nonce) {
            transactionInfo.setNonce(nonce);
        }

        @Override
        public String getBlockHash() {
            return transactionInfo.getBlockHash();
        }

        @Override
        public void setBlockHash(String blockHash) {
            transactionInfo.setBlockHash(blockHash);
        }

        @Override
        public String getTransactionIndex() {
            return transactionInfo.getTransactionIndex();
        }

        @Override
        public void setTransactionIndex(String transactionIndex) {
            transactionInfo.setTransactionIndex(transactionIndex);
        }

        @Override
        public String getFrom() {
            return transactionInfo.getFrom();
        }

        @Override
        public void setFrom(String from) {
            transactionInfo.setFrom(from);
        }

        @Override
        public String getTo() {
            return transactionInfo.getTo();
        }

        @Override
        public void setTo(String to) {
            transactionInfo.setTo(to);
        }

        @Override
        public String getValue() {

            return new BigDecimal(transactionInfo.getValue()).divide(BigDecimal.TEN.pow(tokenInfo.getDecimals())).toPlainString();
        }

        @Override
        public void setValue(String value) {
            transactionInfo.setValue(new BigDecimal(value).multiply(BigDecimal.TEN.pow(tokenInfo.getDecimals())).toPlainString());
        }

        @Override
        public String getGas() {
            return transactionInfo.getGas();
        }

        @Override
        public void setGas(String gas) {
            transactionInfo.setGas(gas);
        }

        @Override
        public String getGasPrice() {
            return transactionInfo.getGasPrice();
        }

        @Override
        public void setGasPrice(String gasPrice) {
            transactionInfo.setGas(gasPrice);
        }

        @Override
        public String getIsError() {
            return transactionInfo.getIsError();
        }

        @Override
        public void setIsError(String isError) {
            transactionInfo.setIsError(isError);
        }

        @Override
        public String getTxreceipt_status() {
            return transactionInfo.getTxreceipt_status();
        }

        @Override
        public void setTxreceipt_status(String txreceiptStatus) {
            transactionInfo.setTxreceipt_status(txreceiptStatus);
        }

        @Override
        public String getInput() {
            return transactionInfo.getInput();
        }

        @Override
        public void setInput(String input) {
            transactionInfo.setInput(input);
        }

        @Override
        public String getContractAddress() {
            return transactionInfo.getContractAddress();
        }

        @Override
        public void setContractAddress(String contractAddress) {
            transactionInfo.setContractAddress(contractAddress);
        }

        @Override
        public String getCumulativeGasUsed() {
            return transactionInfo.getCumulativeGasUsed();
        }

        @Override
        public void setCumulativeGasUsed(String cumulativeGasUsed) {
            transactionInfo.setCumulativeGasUsed(cumulativeGasUsed);
        }

        @Override
        public String getGasUsed() {
            return transactionInfo.getGasUsed();
        }

        @Override
        public void setGasUsed(String gasUsed) {
            transactionInfo.setGasUsed(gasUsed);
        }

        @Override
        public String getConfirmations() {
            return transactionInfo.getConfirmations();
        }

        @Override
        public void setConfirmations(String confirmations) {
            transactionInfo.setConfirmations(confirmations);
        }
    }
}
