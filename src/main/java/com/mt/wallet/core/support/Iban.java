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

import java.math.BigInteger;
import java.util.regex.Pattern;

/**
 * Created by sai on 2018/5/2.
 * From Iban.js translation
 * @see <a href="https://github.com/ethereum/web3.js/blob/develop/lib/web3/iban.js"> Iban.js </a>
 */
public class Iban {

    String ibanString;

    /**
     * This prototype should be used to create ibanString object from ibanString correct string
     *
     * @param {String} ibanString
     */
    public Iban(String ibanString){
        this.ibanString = ibanString;
    }

    String padLeft(String string, int bytesSize){
        StringBuilder result = new StringBuilder();
        while (result.length() + string.length() < bytesSize * 2){
            result.append('0');
        }
        result.append(string);
        return result.toString();
    }

    /**
     * Prepare an IBAN for mod 97 computation by moving the first 4 chars to the end and transforming the letters to
     * numbers (A = 10, B = 11, ..., Z = 35), as specified in ISO13616.
     *
     * @method iso13616Prepare
     * @param {String} ibanString the IBAN
     * @returns {String} the prepared IBAN
     */
    String iso13616Prepare(String iban){
        char a = 'A';
        char z = 'Z';

        iban = iban.toUpperCase();
        iban = iban.substring(4) + iban.substring(0, 4);

        StringBuilder newIban = new StringBuilder();

        int length = iban.length();
        for(int i = 0; i < length; i++){
            char n = iban.charAt(i);
            if(n >= a && n <= z){
                // A = 10, B = 11, ... Z = 35
                newIban.append(n - a + 10);
            }else{
                newIban.append(n);
            }
        }

        return newIban.toString();
    }

    /**
     * Calculates the MOD 97 10 of the passed IBAN as specified in ISO7064.
     *
     * @method mod9710
     * @param {String} ibanString
     * @returns {Number}
     */
    int mod9710(String iban){
        String remainder = iban;
        String block;

        while (remainder.length() > 2){
            if(remainder.length() >= 9){
                block = remainder.substring(0, 9);
            }else{
                block = remainder;
            }
            remainder = Integer.parseInt(block, 10) % 97 +remainder.substring(block.length());
        }
        return Integer.parseInt(remainder, 10) % 97;
    }

    /**
     * This method should be used to create ibanString object from ethereum address
     *
     * @method fromAddress
     * @param {String} address
     * @return {Iban} the IBAN object
     */
    Iban fromAddress(String address){
        BigInteger asBn = new BigInteger(address, 16);
        String base36 = asBn.toString(36);
        String padded = padLeft(base36, 15);
        return fromBban(padded.toUpperCase());
    }

    /**
     * Convert the passed BBAN to an IBAN for this country specification.
     * Please note that <i>"generation of the IBAN shall be the exclusive responsibility of the bank/branch servicing the account"</i>.
     * This method implements the preferred algorithm described in http://en.wikipedia.org/wiki/International_Bank_Account_Number#Generating_IBAN_check_digits
     *
     * @method fromBban
     * @param {String} bban the BBAN to convert to IBAN
     * @returns {Iban} the IBAN object
     */
    Iban fromBban(String bban){
        String countryCode = "XE";

        int remainder = mod9710(iso13616Prepare(countryCode + "00" + bban));
        String s = "0" + (98 - remainder);
        String checkDigit = s.substring(s.length() - 2);

        return new Iban(countryCode + checkDigit + bban);
    }

    /**
     * Should be used to create IBAN object for given institution and identifier
     *
     * @method createIndirect
     * @param {Object} options, required options are "institution" and "identifier"
     * @return {Iban} the IBAN object
     */
    Iban createIndirect(String institution, String identifier){
        return fromBban("ETH" + institution + identifier);
    }

    /**
     * Thos method should be used to check if given string is valid ibanString object
     *
     * @method isValid
     * @param {String} ibanString string
     * @return {Boolean} true if it is valid IBAN
     */
    boolean isValid(String iban){
        Iban i = new Iban(iban);
        return i.isValid();
    }

    /**
     * Should be called to check if ibanString is correct
     *
     * @method isValid
     * @returns {Boolean} true if it is, otherwise false
     */
    boolean isValid(){
        return Pattern.compile("/^XE[0-9]{2}(ETH[0-9A-Z]{13}|[0-9A-Z]{30,31})$/").matcher(ibanString).matches() && mod9710(iso13616Prepare(ibanString)) == 1;
    }

    /**
     * Should be called to check if ibanString number is direct
     *
     * @method isDirect
     * @returns {Boolean} true if it is, otherwise false
     */
    boolean isDirect(){
        return ibanString.length() == 34 || ibanString.length() == 35;
    }

    /**
     * Should be called to check if ibanString number if indirect
     *
     * @method isIndirect
     * @returns {Boolean} true if it is, otherwise false
     */
    boolean isIndirect(){
        return ibanString.length() == 20;
    }

    /**
     * Should be called to get ibanString checksum
     * Uses the mod-97-10 checksumming protocol (ISO/IEC 7064:2003)
     *
     * @method checksum
     * @returns {String} checksum
     */
    String checksum(){
        return ibanString.substring(2, 2);
    }

    /**
     * Should be called to get institution identifier
     * eg. XREG
     *
     * @method institution
     * @returns {String} institution identifier
     */
    String institution(){
        return isIndirect() ? ibanString.substring(7, 4) : "";
    }

    /**
     * Should be called to get client identifier within institution
     * eg. GAVOFYORK
     *
     * @method client
     * @returns {String} client identifier
     */
    String client(){
        return isIndirect() ? ibanString.substring(11) : "";
    }

    /**
     * Should be called to get client direct address
     *
     * @method address
     * @returns {String} client direct address
     */
    public String address(){
        if(isDirect()){
            String base36 = ibanString.substring(4);
            BigInteger asBn = new BigInteger(base36, 36);
            return "0x" + padLeft(asBn.toString(16), 20);
        }
        return "";
    }

}
