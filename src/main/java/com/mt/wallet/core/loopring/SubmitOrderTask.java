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

import com.sai.frame.footstone.base.DataRunnable;
import com.sai.frame.footstone.base.ManifoldValve;
import com.sai.frame.footstone.tools.Mission;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by sai on 2018/5/21.
 * Not Ready
 */

public class SubmitOrderTask extends Mission {

    public static final String TASKNAME_SUBMIT_ORDER = "TASKNAME_SUBMIT_ORDER";

    public static final String SDNAME_CREATE_ORDER = "SDNAME_CREATE_ORDER";
    public static final String SDNAME_CHECK_AMOUNT_PREPARE = "SDNAME_CHECK_AMOUNT_PREPARE";
    public static final String SDNAME_CHECK_AMOUNT_FETCH = "SDNAME_CHECK_AMOUNT_FETCH";
    public static final String SDNAME_CHECK_AMOUNT_CHECK = "SDNAME_CHECK_AMOUNT_CHECK";
    public static final String SDNAME_CHECK_GASENOUGH = "SDNAME_CHECK_GASENOUGH";

    public static final String SDNAME_APPROVE = "SDNAME_APPROVE";
    public static final String SDNAME_SUBMIT_ORDER = "SDNAME_SUBMIT_ORDER";

    Client client;

    OriginalOrder order;

    public SubmitOrderTask(Client client){

        this.client = client;

        ArrayList<Mission.StepDisposer> disposers = new ArrayList<>();

        disposers.add(createOrder);
        disposers.add(checkAmountPrepare);
        disposers.add(checkAmountFetch);
        disposers.add(checkAmountCheck);
        disposers.add(approve);
        disposers.add(submitOrder);


        registerTask(disposers, TASKNAME_SUBMIT_ORDER);
    }

    StepDisposer createOrder = new StepDisposer(){

        @Override
        protected String getStepName() {
            return SDNAME_CREATE_ORDER;
        }

        @Override
        protected void dispose(String s, String s1, Task task, Object[] objects) {
            order = client.createOrder();
            task.doNext(SDNAME_CHECK_AMOUNT_PREPARE, null);
        }
    };

    boolean needEthBalance = false;
    boolean needTokenFrozen = false;
    boolean needLrcFrozen = false;
    boolean needSellingFrozenLRC = false;
    boolean needLrcBalance = false;

    BigDecimal ethBalance;
    BigDecimal tokenAllowance;
    BigDecimal tokenFrozen;
    BigDecimal lrcAllowance;
    BigDecimal lrcFrozen;
    BigDecimal sellingFrozenLRC;
    BigDecimal lrcBalance;

    boolean gasToken = false;
    boolean gasLRC = false;
    double minusETH;
    double minusLRC;

    StepDisposer checkAmountPrepare = new StepDisposer() {
        @Override
        protected String getStepName() {
            return SDNAME_CHECK_AMOUNT_PREPARE;
        }

        @Override
        protected void dispose(String s, String s1, Task task, Object[] objects) {

            if(order.side.equals("buy")){
                if(order.tokenBuy.toUpperCase().equals("LRC")){
                    needEthBalance = true;
                    needTokenFrozen = true;
                }else{
                    needEthBalance = true;
                    needTokenFrozen = true;
                    needLrcFrozen = true;
                    needSellingFrozenLRC = true;
                    needLrcBalance = true;
                }
            }else{
                if(order.tokenSell.toUpperCase().equals("LRC")){
                    needLrcFrozen = true;
                    needLrcBalance = true;
                    needEthBalance = true;
                    needSellingFrozenLRC = true;
                }else{
                    needLrcFrozen = true;
                    needLrcBalance = true;
                    needEthBalance = true;
                    needTokenFrozen = true;
                }
            }

            task.doNext(SDNAME_CHECK_AMOUNT_FETCH, null);
        }
    };

    StepDisposer checkAmountFetch = new StepDisposer() {
        @Override
        protected String getStepName() {
            return SDNAME_CHECK_AMOUNT_FETCH;
        }

        @Override
        protected void dispose(String s, String s1, Task task, Object[] objects) {

            final ManifoldValve fetchValve = new ManifoldValve(new ManifoldValve.Outfall() {
                @Override
                public void discharge(ManifoldValve manifoldValve) {

                }
            }, 5);

            if(needEthBalance){
                client.fetchEthBalance(new DataRunnable(){
                    @Override
                    public void run() {
                        ethBalance = (BigDecimal) data;
                        fetchValve.openValveOnce(0);
                    }
                });
            }else{
                fetchValve.openValveOnce(0);
            }

            if(needTokenFrozen){

                client.fetchTokenAllow(new DataRunnable(){
                    @Override
                    public void run() {

                        tokenAllowance = (BigDecimal) data;
                        Loopring.fetchEstimatedAllocatedAllowance(order.address, order.tokenSell, new DataRunnable() {
                            @Override
                            public void run() {
                                tokenFrozen = new BigDecimal((String) data);
                                fetchValve.openValveOnce(1);
                            }
                        });
                    }
                });
            }else{
                fetchValve.openValveOnce(1);
            }

            if(needLrcFrozen){
                Loopring.fetchFrozenLRCFee(order.address, new DataRunnable() {
                    @Override
                    public void run() {
                        lrcFrozen = new BigDecimal((String)data);
                        fetchValve.openValveOnce(2);
                    }
                });
            }else{
                fetchValve.openValveOnce(2);
            }

            if(needSellingFrozenLRC){
                Loopring.fetchEstimatedAllocatedAllowance(order.address, "LRC", new DataRunnable() {
                    @Override
                    public void run() {
                        sellingFrozenLRC = new BigDecimal((String) data);
                        fetchValve.openValveOnce(3);
                    }
                });
            }else{
                fetchValve.openValveOnce(3);
            }

            if(needLrcBalance){
                client.fetchEthBalance(new DataRunnable(){
                    @Override
                    public void run() {
                        lrcBalance = (BigDecimal) data;
                        fetchValve.openValveOnce(4);
                    }
                });
            }else{
                fetchValve.openValveOnce(4);
            }

        }
    };

    StepDisposer checkAmountCheck = new StepDisposer(){

        @Override
        protected String getStepName() {
            return SDNAME_CHECK_AMOUNT_CHECK;
        }

        @Override
        protected void dispose(String s, String s1, Task task, Object[] objects) {
            if(order.side.equals("buy")){
                if(order.tokenBuy.toUpperCase().equals("LRC")){
                    checkGasEnough(false);
                }else{
                    checkLRCEnough();
                    checkGasEnough(true);
                }
            }else{
                if(order.tokenSell.toUpperCase().equals("LRC")){
                    checkLRCEnough();
                    checkLRCGasEnough();
                }else{
                    checkLRCEnough();
                    checkGasEnough(true);
                }
            }
        }
    };

    BigDecimal calculateGas(String token, double amount, double lrcFee){

        if(token.toUpperCase().equals("LRC")){
            if(tokenAllowance.subtract(new BigDecimal(lrcFee).add(lrcFrozen).add(sellingFrozenLRC)).doubleValue() >= 0){
                return new BigDecimal(0);
            }
        } else {
            if(tokenAllowance.subtract(new BigDecimal(amount).add(tokenFrozen)).doubleValue() >= 0){
                return new BigDecimal(0);
            }
        }
        BigDecimal gasAmount = getGasAmountInETH();
        gasToken = true;

        return gasAmount;
    }

    private void checkLRCEnough(){
        double result = lrcBalance.subtract(new BigDecimal(order.lrcFee).subtract(lrcFrozen)).doubleValue();
        if(result < 0)
            minusLRC = -result;
    }

    private void checkGasEnough(boolean includingLRC){
        BigDecimal result;
        BigDecimal tokenGas = calculateGas(order.tokenSell, order.amountSell, order.lrcFee);
        if(includingLRC){
            BigDecimal lrcGas = calculateGas("LRC", order.amountSell, order.lrcFee);
            result = ethBalance.subtract(lrcGas).subtract(tokenGas);
        }else{
            result = ethBalance.subtract(tokenGas);
        }
        if(result.doubleValue() < 0)
            minusETH = -result.doubleValue();
    }

    private void checkLRCGasEnough(){

        double result;

        result = ethBalance.subtract(calculateGasForLRC()).doubleValue();

        if(result < 0)
            minusETH = -result;
    }

    BigDecimal calculateGasForLRC(){
        if(new BigDecimal(order.lrcFee).add(lrcFrozen).add(sellingFrozenLRC).add(new BigDecimal(order.amountSell )).subtract(lrcAllowance).doubleValue() > 0){
            gasLRC = true;
            return getGasAmountInETH();
        }
        return new BigDecimal(0);
    }

    StepDisposer approve = new StepDisposer(){

        @Override
        protected String getStepName() {
            return null;
        }

        @Override
        protected void dispose(String s, String s1, Task task, Object[] objects) {

        }
    };

    StepDisposer submitOrder = new StepDisposer(){

        @Override
        protected String getStepName() {
            return null;
        }

        @Override
        protected void dispose(String s, String s1, Task task, Object[] objects) {

        }
    };

    public BigDecimal getGasAmountInETH() {
        //TODO FIXME
        return null;
    }

    interface Client {
        OriginalOrder createOrder();

        /**
         *
         * @param dataRunnable fetch eth balance and set to data in BigDecimal format
         */
        void fetchEthBalance(DataRunnable dataRunnable);

        BigDecimal getTokenBalance(String token);

        String getOwner();

        void fetchTokenAllow(DataRunnable dataRunnable);
    }
}
