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

import static com.mt.wallet.core.walletImp.EthWallet.ENV_MAINNET;
import static com.mt.wallet.core.walletImp.EthWallet.ENV_RINKEBY;

/**
 * Created by sai on 2018/4/16.
 */

public class Config {

    private Config() {
        throw new IllegalStateException("Utility class");
    }

    public static final String NETCONFIG = ENV_MAINNET;

    public static final String ETH_FLAG = "0x0";

}
