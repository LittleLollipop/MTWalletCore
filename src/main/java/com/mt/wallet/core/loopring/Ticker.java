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

/**
 * Created by sai on 2018/5/3.
 */
@Keep
public class Ticker {

    String name;
    String tokenSymbol1;
    String tokenSymbol2;
    Token token1;
    Token token2;
    TickerInfoInstant tickerInfoInstant;

    public Ticker(String string) {

        name = string;
        String[] tickerInfo = name.split("-");
        tokenSymbol1 = tickerInfo[0];
        tokenSymbol2 = tickerInfo[1];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTokenSymbol1() {
        return tokenSymbol1;
    }

    public void setTokenSymbol1(String tokenSymbol1) {
        this.tokenSymbol1 = tokenSymbol1;
    }

    public String getTokenSymbol2() {
        return tokenSymbol2;
    }

    public void setTokenSymbol2(String tokenSymbol2) {
        this.tokenSymbol2 = tokenSymbol2;
    }

    public Token getToken1() {
        return token1;
    }

    public void setToken1(Token token1) {
        this.token1 = token1;
    }

    public Token getToken2() {
        return token2;
    }

    public void setToken2(Token token2) {
        this.token2 = token2;
    }

    public TickerInfoInstant getTickerInfoInstant() {
        return tickerInfoInstant;
    }

    public void setTickerInfoInstant(TickerInfoInstant tickerInfoInstant) {
        this.tickerInfoInstant = tickerInfoInstant;
    }
}
