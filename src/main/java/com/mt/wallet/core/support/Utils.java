package com.mt.wallet.core.support;

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

import org.web3j.crypto.Hash;

/**
 * Created by sai on 2018/5/13.
 */

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean checkAddress(String address){
        if(!Utils.isChecksumAddress(address)){

            if(address.equals(address.toLowerCase()) && address.length() == 42 && address.startsWith("0x")){
                //nothing to do
            }else{
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the given string is a checksummed address
     *
     * @method isChecksumAddress
     * @param {String} address the given HEX adress
     * @return {Boolean}
     */
    public static boolean isChecksumAddress(String address){
        address = address.replace("0x", "");
        String addressHash = Hash.sha3String(address.toLowerCase()).substring(2);

        for (int i = 0; i < 40; i++ ){

            if((Integer.parseInt(addressHash.substring(i, i + 1), 16) > 7 && !address.substring(i, i + 1).toUpperCase().equals(address.substring(i, i + 1))) ||
                    (Integer.parseInt(addressHash.substring(i, i + 1), 16) <= 7 && !address.substring(i, i + 1).toLowerCase().equals(address.substring(i, i + 1)))){
                return false;
            }

        }

        return true;
    }

    /**
     * Makes a checksum address
     *
     * @method toChecksumAddress
     * @param {String} address the given HEX adress
     * @return {String}
     */
    public static String toChecksumAddress(String address){
        if(TextUtils.isEmpty(address))
            return "";

        address = address.toLowerCase().replace("0x", "");
        String addressHash = Hash.sha3String(address).substring(2);
        StringBuilder checksumAddress = new StringBuilder("0x");

        for (int i = 0; i < 40; i++ ){

            if(Integer.parseInt(addressHash.substring(i, i + 1), 16) > 7){
                checksumAddress.append(address.substring(i, i + 1).toUpperCase());
            } else {
                checksumAddress.append(address.substring(i, i + 1));
            }
        }
        return checksumAddress.toString();
    }

}
