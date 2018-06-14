package com.mt.wallet.core.walletImp;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mt.wallet.core.Config;
import com.mt.wallet.core.TransactionInfo;
import com.mt.wallet.core.WalletApplication;
import com.mt.wallet.core.business.ChangePasswordBusiness;
import com.mt.wallet.core.business.DeleteAccountBusiness;
import com.mt.wallet.core.business.ExportKeyBusiness;
import com.mt.wallet.core.business.FetchHistoryBusiness;
import com.mt.wallet.core.business.eth.ContractBusiness;
import com.mt.wallet.core.business.CreateAccountBusiness;
import com.mt.wallet.core.business.FetchAccountsBusiness;
import com.mt.wallet.core.business.ImportAccountBusiness;
import com.mt.wallet.core.business.TransactionBusiness;
import com.mt.wallet.core.business.UpdateBalanceBusiness;
import com.mt.wallet.core.ContractTransactionInfo;
import com.mt.wallet.core.Wallet;
import com.mt.wallet.core.safe.SafeCase;
import com.mt.wallet.core.support.Utils;

import org.ethereum.geth.Account;
import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Geth;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.generators.SCrypt;
import org.spongycastle.crypto.params.KeyParameter;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.ens.EnsResolver;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


import static com.mt.wallet.core.business.ExportKeyBusiness.TYPE_KEYSTORE;
import static com.mt.wallet.core.business.ExportKeyBusiness.TYPE_MNEMONIC;
import static com.mt.wallet.core.business.ExportKeyBusiness.TYPE_PRIVATE_KEY;
import static com.mt.wallet.core.business.ImportAccountBusiness.IMPORT_TYPE_ECDSAKEY;
import static com.mt.wallet.core.business.ImportAccountBusiness.IMPORT_TYPE_KEYSTORE;

/**
 * Created by Sai on 2018/4/3.
 */

public class EthWallet extends Wallet.Avatar {

    public static final String ETHERSCAN_URL_MAIN = "https://etherscan.io/";
    public static final String ETHERSCAN_URL_RINKEBY= "https://rinkeby.etherscan.io/";

    public static final String ETHERSCAN_APIURL_MAIN = "https://api.etherscan.io/";
    public static final String ETHERSCAN_APIURL_RINKEBY= "https://api-rinkeby.etherscan.io/";


    public static final String ERROR_INFO_ADDRESS = "check address failed";

    public static final String WALLET_TYPE = "ETH";
    public static final String INFO_GASPRICE = "INFO_GASPRICE";

    public static final String ENV_MAINNET = "mainnet";
    public static final String ENV_RINKEBY = "rinkeby";

    private Web3j web3j;
    private String datadir;
    KeyManager keyManager;
    long networkID;
    Wallet.Config config;

    @Override
    public void init(Wallet.Config config) {

        this.config = config;
        web3j = Web3jFactory.build(new HttpService("https://" + config.getEnv() + ".infura.io/JZsXxwSuoh65DbcVmwPd"));
        datadir = WalletApplication.getInstance().getFilesDir().getAbsolutePath();
        deleteDirIfExists(new File(datadir + "/GethDroid/" + config.getEnv()));

        switch (config.getEnv()){
            case ENV_RINKEBY:
                networkID = 4;
                break;
            case ENV_MAINNET:
                networkID = 1;
                break;
            default:
                networkID = 4;
                break;
        }

        try {
            keyManager = KeyManager.newKeyManager(datadir);
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
        }

    }

    public static void deleteDirIfExists(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirIfExists(f);
            }
        }
        file.delete();
    }

    @Override
    public void getBalance(UpdateBalanceBusiness balanceBusiness) {

        final EthGetBalance balance;
        try {
            balance = web3j.ethGetBalance(balanceBusiness.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
            balanceBusiness.updateInfo(""+Convert.fromWei(balance.getBalance().toString(), Convert.Unit.ETHER));
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            balanceBusiness.updateErrorMessage(e.getMessage());
        }
    }

    @Override
    public void createAccount(CreateAccountBusiness createAccountBusiness) {
        try {
            org.ethereum.geth.Account account = keyManager.newAccount(createAccountBusiness.getPassphrase());
            createAccountBusiness.updateAccount(account);
            createAccountBusiness.updateInfo("address:" + Utils.toChecksumAddress(account.getAddress().getHex()));

        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            createAccountBusiness.updateInfo(e.getMessage());
        }
    }

    @Override
    public void getAccounts(FetchAccountsBusiness fetchAccountsBusiness, boolean isList) {

        if(isList){
            ArrayList<String> addressList = new ArrayList<>();

            try {
                List<org.ethereum.geth.Account> accounts = keyManager.getAccounts();

                for(org.ethereum.geth.Account account : accounts)
                    addressList.add(Utils.toChecksumAddress(account.getAddress().getHex()));

                fetchAccountsBusiness.updateInfo(addressList);
            } catch (Exception e) {
                Log.e(getClass().getName(), "", e);
                fetchAccountsBusiness.updateInfo(e.getMessage());
            }
        }else{
            StringBuilder stringBuilder = new StringBuilder();

            try {
                List<org.ethereum.geth.Account> accounts = keyManager.getAccounts();

                for(org.ethereum.geth.Account account : accounts)
                    stringBuilder.append(Utils.toChecksumAddress(account.getAddress().getHex()) + ",");

                fetchAccountsBusiness.updateInfo(stringBuilder.toString());
            } catch (Exception e) {
                Log.e(getClass().getName(), "", e);
                fetchAccountsBusiness.updateInfo(e.getMessage());
            }
        }



    }

    @Override
    public void importAccount(ImportAccountBusiness importAccountBusiness) {
        try {
            org.ethereum.geth.Account account = null;
            if(importAccountBusiness.getType() == IMPORT_TYPE_KEYSTORE){
                account = keyManager.getKeystore().importKey(importAccountBusiness.getKeyText().getBytes(), importAccountBusiness.getPassphrase(), importAccountBusiness.getNewPassphrase().value(new char[]{}));

            }
            if(importAccountBusiness.getType() == IMPORT_TYPE_ECDSAKEY){
                account = keyManager.getKeystore().importECDSAKey(importAccountBusiness.getECDSAKey(), importAccountBusiness.getNewPassphrase().value(new char[]{}));
            }

            if(account != null){

                List<org.ethereum.geth.Account> accounts = keyManager.getAccounts();

                importAccountBusiness.updateInfo(Utils.toChecksumAddress(account.getAddress().getHex()), accounts.indexOf(account));
            }

        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            importAccountBusiness.updateInfo(e.getMessage(), -1);
        }
    }

    @Override
    public void SendTransaction(TransactionBusiness transactionBusiness) {

        try {
            EnsResolver ensResolver = new EnsResolver(web3j);
            BigInteger gasPrice = transactionBusiness.getGasPrice().toBigInteger();
            BigInteger gasLimit = transactionBusiness.getGasLimit().toBigInteger();
            BigInt value = Geth.newBigInt(Convert.toWei(transactionBusiness.getValue(), Convert.Unit.ETHER).longValue());

            org.ethereum.geth.Account account = keyManager.getAccounts().get(transactionBusiness.getAccountNumber());
            String to = transactionBusiness.getToAddress();
            String resolvedAddress;

            resolvedAddress = ensResolver.resolve(to);
            if(!Utils.checkAddress(resolvedAddress)){
                Log.d( transactionBusiness.getClass().getName(), ERROR_INFO_ADDRESS);
                transactionBusiness.updateErrorInfo(ERROR_INFO_ADDRESS);
                return;
            }

            byte[] data = new byte[0];
            if(!TextUtils.isEmpty(transactionBusiness.getData()))
                data = Numeric.hexStringToByteArray(transactionBusiness.getData());


            BigInteger nonce = web3j.ethGetTransactionCount(
                    account.getAddress().getHex(), DefaultBlockParameterName.PENDING).send().getTransactionCount();

//            BigInt gasInt = Geth.newBigInt(gasLimit.longValue());
            BigInt gasPriceInt = Geth.newBigInt(gasPrice.longValue());
            org.ethereum.geth.Transaction transaction = Geth.newTransaction(nonce.longValue(), new Address(resolvedAddress), value, gasLimit.longValue(), gasPriceInt, data);

            EthSendTransaction ethSendRawTransaction = web3j.ethSendRawTransaction(Numeric.toHexString(keyManager.getKeystore().signTxPassphrase(account, transactionBusiness.getPassphrase().value(new char[]{}),transaction, new BigInt(networkID)).encodeRLP())).send();
            String info = ethSendRawTransaction.getTransactionHash();
            if(TextUtils.isEmpty(info)){
                transactionBusiness.updateErrorInfo(ethSendRawTransaction.getError().getMessage());
            }else{
                transactionBusiness.updateInfo(info);
            }

        }catch (Exception e){
            Log.e(getClass().getName(), "", e);
            transactionBusiness.updateErrorInfo(e.getMessage());
        }
    }

    @Override
    public void ethCall(Transaction transaction, ContractBusiness contractBusiness) {
        try {
            String info = web3j.ethCall(transaction, DefaultBlockParameterName.PENDING).send().getValue();
            contractBusiness.updateInfo(info);
        } catch (IOException e) {
            Log.e(getClass().getName(), "", e);
        }
    }

    @Override
    public String getAccountAddress(int accountNumber) {
        try {
            return Utils.toChecksumAddress(keyManager.getAccounts().get(accountNumber).getAddress().getHex());
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            return e.getMessage();
        }
    }

    @Override
    public TransactionInfo getTransactionInfo(String hash) {

        TransactionInfo transactionInfo = null;
        try {
            org.web3j.protocol.core.methods.response.Transaction transaction = web3j.ethGetTransactionByHash(hash).sendAsync().get().getTransaction();

            if(transaction == null)
                return null;

            transactionInfo = new TransactionInfo();
            transactionInfo.setBlockHash(transaction.getBlockHash());
            transactionInfo.setFrom(transaction.getFrom());
            transactionInfo.setGas(new BigDecimal(transaction.getGas()).toPlainString());
            transactionInfo.setGasPrice(new BigDecimal(transaction.getGasPrice()).toPlainString());
            transactionInfo.setHash(transaction.getHash());
            transactionInfo.setInput(transaction.getInput());
            transactionInfo.setNonce(new BigDecimal(transaction.getNonce()).toPlainString());
            transactionInfo.setTo(transaction.getTo());
            transactionInfo.setTransactionIndex(new BigDecimal(transaction.getTransactionIndex()).toPlainString());
            transactionInfo.setValue(Convert.fromWei(new BigDecimal(transaction.getValue()), Convert.Unit.ETHER).toPlainString());

        } catch (InterruptedException|ExecutionException e) {
            Log.e(getClass().getName(), "", e);
        }

        return transactionInfo;
    }

    @Override
    public void getHistory(final FetchHistoryBusiness business) {

        String url;
        if(business.isContract()){
            switch (config.getEnv()){
                case ENV_RINKEBY:
                    url = ETHERSCAN_APIURL_RINKEBY + "api?module=account&action=tokentx";
                    break;
                case ENV_MAINNET:
                    url = ETHERSCAN_APIURL_MAIN + "api?module=account&action=tokentx";
                    break;
                default:
                    url = ETHERSCAN_APIURL_RINKEBY + "api?module=account&action=tokentx";
                    break;
            }
            url = url + "&contractaddress=" + business.getContractAddress() +
                    "&address=" + business.getAddress() +
                    "&page=1&offset=30&sort=desc&apikey=3QAU4CZC4SISSV6DHYZ84CPKWK5YJGB3PX";

        }else{
            switch (config.getEnv()){
                case ENV_RINKEBY:
                    url = ETHERSCAN_APIURL_RINKEBY + "api?module=account&action=txlist";
                    break;
                case ENV_MAINNET:
                    url = ETHERSCAN_APIURL_MAIN + "api?module=account&action=txlist";
                    break;
                default:
                    url = ETHERSCAN_APIURL_RINKEBY + "api?module=account&action=txlist";
                    break;
            }
            url = url + "&address=" + business.getAddress() +
                    "&startblock=0&endblock=99999999&page=1&offset=30&sort=desc" +
                    "&apikey=3QAU4CZC4SISSV6DHYZ84CPKWK5YJGB3PX\n";
        }



        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                WalletApplication.getInstance().getWallet().runInMachine(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        JsonObject responseJson = gson.fromJson(response, JsonObject.class);
                        if(responseJson.get("status").getAsInt() == 1){

                            ArrayList<ContractTransactionInfo> transactions = gson.fromJson(responseJson.get("result"), new TypeToken<ArrayList<ContractTransactionInfo>>(){}.getType());
                            business.updateInfo(transactions);
                        }else{
                            business.updateErrorInfo(responseJson.get("message").getAsString());
                        }
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                business.updateErrorInfo(error.getMessage());
            }
        });

        WalletApplication.getQueue().add(request);
    }

    @Override
    public Object getInfo(String infoFlag) {

        if(INFO_GASPRICE.equals(infoFlag)){
            try {
                return web3j.ethGasPrice().send().getGasPrice();
            } catch (IOException e) {
                Log.e(getClass().getName(), "", e);
            }
        }

        return null;
    }

    @Override
    public void changePassword(ChangePasswordBusiness changePasswordBusiness) {
        try {
            keyManager.updateAccountPassphrase(findAccount(changePasswordBusiness.getAccountAddress()),
                    changePasswordBusiness.getPassword().value(new char[]{}), changePasswordBusiness.getNewPassword().value(new char[]{}));

        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            changePasswordBusiness.updateInfo(e.getMessage());
        }
    }

    private Account findAccount(String address){
        List<Account> accounts = null;
        try {
            accounts = keyManager.getAccounts();
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
        }
        for(Account account : accounts){
            if(Utils.toChecksumAddress(account.getAddress().getHex()).equals(address))
                return account;
        }
        return null;
    }

    @Override
    public String getWalletType() {
        return WALLET_TYPE;
    }

    @Override
    public void exportKey(ExportKeyBusiness exportKeyBusiness) {

        switch (exportKeyBusiness.getExportType()){
            case TYPE_PRIVATE_KEY:
                try {
                    ECKeyPair keyPair = decrypt(exportKeyBusiness.getPassword().value(new char[]{}), new String(keyManager.getKeystore().exportKey(findAccount(exportKeyBusiness.getAccountAddress()), exportKeyBusiness.getPassword().value(new char[]{}), exportKeyBusiness.getPassword().value(new char[]{}))));
                    if(keyPair != null)
                        exportKeyBusiness.updateKey(Numeric.toHexStringNoPrefix(keyPair.getPrivateKey()));

                } catch (Exception e) {
                    Log.e(getClass().getName(), "", e);
                }
                break;
            case TYPE_KEYSTORE:
                try {
                    exportKeyBusiness.updateKey(new String(keyManager.getKeystore().exportKey(findAccount(exportKeyBusiness.getAccountAddress()), exportKeyBusiness.getPassword().value(new char[]{}), exportKeyBusiness.getNewPassword())));
                } catch (Exception e) {
                    Log.e(getClass().getName(), "", e);
                }
                break;
            case TYPE_MNEMONIC:
                exportKeyBusiness.updateMnemonic(exportMnemonic(exportKeyBusiness));
                break;
            default:
                break;
        }

    }

    private String[] exportMnemonic(ExportKeyBusiness exportKeyBusiness) {

        StringBuilder builder = new StringBuilder();
        List<String> mnemonicInfo = exportKeyBusiness.getMnemonicInfo();
        try {
            List<Account> accounts = keyManager.getAccounts();

            int i = 0;
            int lastSize = 0;
            for(String string : mnemonicInfo){
                for(Account account : accounts){
                    if(string.equals(account.getAddress().getHex())){
                        ECKeyPair ecKeyPair = decrypt(exportKeyBusiness.getPassword().value(new char[]{}), new String(keyManager.getKeystore().exportKey(account, exportKeyBusiness.getPassword().value(new char[]{}), exportKeyBusiness.getPassword().value(new char[]{}))));
                        if(ecKeyPair == null)
                            return new String[0];

                        if(i == 0){
                            String s = new String(ecKeyPair.getPrivateKey().toByteArray());
                            builder.append(s.substring(2));
                            lastSize = Integer.parseInt(s.substring(0, 2));
                        }else if(i == mnemonicInfo.size() - 1){
                            String s = new String(ecKeyPair.getPrivateKey().toByteArray());
                            builder.append(s.substring(0, lastSize - 1));
                        }else{
                            String s = new String(ecKeyPair.getPrivateKey().toByteArray());
                            builder.append(s);
                        }
                        break;
                    }
                }
                i++;
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
        }

        return builder.toString().split(",");
    }

    @Override
    public List<String> saveMnemonic(List<String> mWordList, SafeCase passphrase) {
        StringBuilder wordBuilder = new StringBuilder();

        for(String s : mWordList){
            wordBuilder.append(s + ",");
        }

        String word = wordBuilder.toString();

        byte[] wordBytes = word.getBytes();

        ArrayList<byte[]> wordsByteList = new ArrayList<>();
        wordsByteList.add(new byte[32]);

        for(int i = 1; ; i++){
            byte[] bytes = new byte[32];

            if(wordBytes.length + 2 <= bytes.length * ( i + 1 )){

                int lastSize = wordBytes.length + 2 - i * bytes.length;

                System.arraycopy(wordBytes, i * bytes.length - 2, bytes, 0, lastSize);
                wordsByteList.add(bytes);

                System.arraycopy(wordBytes, 0, wordsByteList.get(0), 2, bytes.length - 2);

                String lastSizeStr = String.valueOf(lastSize);
                if(lastSizeStr.length() == 1)
                    lastSizeStr = "0" + lastSizeStr;

                System.arraycopy(lastSizeStr.getBytes(), 0, wordsByteList.get(0), 0, 2);
                break;
            }else{

                System.arraycopy(wordBytes, i * bytes.length - 2, bytes, 0, bytes.length);
                wordsByteList.add(bytes);
            }

        }

        ArrayList<String> wordsInfo = new ArrayList<>();
        for(byte[] mBytes : wordsByteList){
            try {
                wordsInfo.add(keyManager.getKeystore().importECDSAKey(mBytes, passphrase.value(new char[]{})).getAddress().getHex());
            } catch (Exception e) {
                Log.e(getClass().getName(), "", e);
            }
        }

        return wordsInfo;
    }

    @Override
    public void deleteWallet(DeleteAccountBusiness business) {

        try {
            keyManager.getKeystore().deleteAccount(findAccount(business.getAddress()), business.getPassword().value(new char[]{}));
        } catch (Exception e) {
            Log.e(business.getClass().getName(), "", e);
            business.onDeleteFailed();
            return;
        }
        business.onDeleteSucceed();
    }

    public static ECKeyPair decrypt(String password, String keystore)
            throws CipherException {

        try {
            JSONObject jsonObject = new JSONObject(keystore);
            JSONObject crypto = jsonObject.getJSONObject("crypto");
            byte[] mac = Numeric.hexStringToByteArray(crypto.getString("mac"));
            byte[] iv = Numeric.hexStringToByteArray(crypto.getJSONObject("cipherparams").getString("iv"));
            byte[] cipherText = Numeric.hexStringToByteArray(crypto.getString("ciphertext"));

            byte[] derivedKey;

            JSONObject kdfparams = crypto.getJSONObject("kdfparams");

            if(kdfparams.has("n")){

                int dklen = kdfparams.getInt("dklen");
                int n = kdfparams.getInt("n");
                int p = kdfparams.getInt("p");
                int r = kdfparams.getInt("r");
                byte[] salt = Numeric.hexStringToByteArray(kdfparams.getString("salt"));
                derivedKey = generateDerivedScryptKey(
                        password.getBytes(Charset.forName("UTF-8")), salt, n, r, p, dklen);
            }else{

                int c = kdfparams.getInt("c");
                String prf = kdfparams.getString("prf");
                byte[] salt = Numeric.hexStringToByteArray(kdfparams.getString("salt"));

                derivedKey = generateAes128CtrDerivedKey(
                        password.getBytes(Charset.forName("UTF-8")), salt, c, prf);
            }

            byte[] derivedMac = generateMac(derivedKey, cipherText);

            if (!Arrays.equals(derivedMac, mac)) {
                throw new CipherException("Invalid password provided");
            }

            byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
            byte[] privateKey = performCipherOperation(Cipher.DECRYPT_MODE, iv, encryptKey, cipherText);
            return ECKeyPair.create(privateKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] generateDerivedScryptKey(
            byte[] password, byte[] salt, int n, int r, int p, int dkLen) throws CipherException {
        return SCrypt.generate(password, salt, n, r, p, dkLen);
    }

    private static byte[] generateAes128CtrDerivedKey(
            byte[] password, byte[] salt, int c, String prf) throws CipherException {

        if (!prf.equals("hmac-sha256")) {
            throw new CipherException("Unsupported prf:" + prf);
        }

        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(password, salt, c);
        return ((KeyParameter) gen.generateDerivedParameters(256)).getKey();
    }

    private static byte[] generateMac(byte[] derivedKey, byte[] cipherText) {
        byte[] result = new byte[16 + cipherText.length];

        System.arraycopy(derivedKey, 16, result, 0, 16);
        System.arraycopy(cipherText, 0, result, 16, cipherText.length);

        return Hash.sha3(result);
    }

    private static byte[] performCipherOperation(
            int mode, byte[] iv, byte[] encryptKey, byte[] text) throws CipherException {

        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, "AES");
            cipher.init(mode, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(text);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getEtherscanBaseUrl() {

        String baseUrl = null;

        switch (Config.NETCONFIG){
            case ENV_RINKEBY:
                baseUrl = ETHERSCAN_URL_RINKEBY;
                break;
            case ENV_MAINNET:
                baseUrl = ETHERSCAN_URL_MAIN;
                break;
            default:
                baseUrl = ETHERSCAN_URL_RINKEBY;
                break;
        }

        return baseUrl;
    }
}
