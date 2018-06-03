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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mt.wallet.core.WalletApplication;
import com.sai.frame.footstone.base.DataRunnable;
import com.sai.frame.footstone.base.RunnablePocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by sai on 2018/5/3.
 * Not Ready
 */

public class Loopring {

    public static final String BASEURL = "https://relay1.loopring.io";
    public static final String RPCURL = BASEURL + "/rpc/v2";
    public static final String ETHURL = BASEURL + "/eth";
    public static final String SOCKETURL = BASEURL;

    public static final String DELEGATEADDRESS = "0x17233e07c67d086464fD408148c3ABB56245FA64";
    public static final String ORDERWALLETADDRESS = "0xb94065482ad64d4c2b9252358d746b39e820a582";


    private static Map<String, Token> tokens = new Hashtable<>();
    private static ArrayList<Ticker> tickers = new ArrayList<>();
    static Gson gson = new Gson();

    public static ArrayList<Token> fetchTokens() {

        try {
            InputStream wis = WalletApplication.getInstance().getResources().getAssets().open("tokens.json");
            return gson.fromJson(new JsonReader(new InputStreamReader(wis)), new TypeToken<ArrayList<Token>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void fetchMarket(final Runnable runnable) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "loopring_getSupportedMarket");
            jsonObject.put("params", new JSONArray().put(new JSONObject().put("delegateAddress", DELEGATEADDRESS)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response.Listener succeedListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                WalletApplication.getInstance().getMachine().runInMachine(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonArray = response.getJSONArray("result");
                            for(int i = jsonArray.length() - 1; i >=0; i--){
                                tickers.add(new Ticker(jsonArray.getString(i)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayList<Token> tokenList = fetchTokens();
                        for (Token token : tokenList){

                            for(Ticker ticker : tickers){
                                if(ticker.getTokenSymbol1().equals(token.getSymbol())){
                                    ticker.setToken1(token);
                                    token.addTicker(ticker);
                                }else if(ticker.getTokenSymbol2().equals(token.getSymbol())){
                                    ticker.setToken2(token);
                                    token.addTicker(ticker);
                                }
                            }

                            tokens.put(token.getSymbol(), token);
                        }

                        RunnablePocket.post(runnable);
                    }
                });

            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO FIXME
            }
        };

        send(jsonObject, succeedListener, errorListener);
    }

    public static void fetchTicker(final Runnable runnable) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "loopring_getTicker");
            jsonObject.put("params", new JSONArray().put(new JSONObject().put("delegateAddress", DELEGATEADDRESS)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response.Listener succeedListener = new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(final JSONObject response) {
                WalletApplication.getInstance().getMachine().runInMachine(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<TickerInfoInstant> tickerInfoInstants = null;
                        try {
                            tickerInfoInstants = gson.fromJson(response.getJSONArray("result").toString(), new TypeToken<ArrayList<TickerInfoInstant>>(){}.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for(TickerInfoInstant tickerInfoInstant : tickerInfoInstants){
                            for(Ticker ticker : tickers){
                                if(ticker.getName().equals(tickerInfoInstant.getMarket())){
                                    ticker.setTickerInfoInstant(tickerInfoInstant);
                                    break;
                                }
                            }
                        }

                        RunnablePocket.post(runnable);
                    }
                });
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO FIXME
            }
        };

        send(jsonObject, succeedListener, errorListener);
    }

    public static void fetchFrozenLRCFee(String address, final DataRunnable callBack){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "loopring_getFrozenLRCFee");
            jsonObject.put("params", new JSONArray().put(new JSONObject().put("owner", address).put("delegateAddress", DELEGATEADDRESS)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response.Listener succeedListener = new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    callBack.setData(response.getString("result"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RunnablePocket.post(callBack);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO FIXME
            }
        };

        send(jsonObject, succeedListener, errorListener);
    }

    public static void fetchEstimatedAllocatedAllowance(String owner, String token, final DataRunnable callBack){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", "loopring_getEstimatedAllocatedAllowance");
            jsonObject.put("params", new JSONArray().put(new JSONObject().put("owner", owner).put("delegateAddress", DELEGATEADDRESS).put("token", token)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response.Listener succeedListener = new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    callBack.setData(response.getString("result"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RunnablePocket.post(callBack);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO FIXME
            }
        };

        send(jsonObject, succeedListener, errorListener);
    }

    private static void send(JSONObject json, Response.Listener listener, Response.ErrorListener errorListener){

        try {
            json.put("jsonrpc", "2.0");
            json.put("id", WalletApplication.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(RPCURL, json, listener, errorListener);

        WalletApplication.getQueue().add(request);
    }


    public static Map<String, Token> getMarketTokens() {
        return tokens;
    }
}
