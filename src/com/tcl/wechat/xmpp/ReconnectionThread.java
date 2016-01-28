/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tcl.wechat.xmpp;


/**
 * 掉线重连机制
 * @author rex.lei
 *
 */
public class ReconnectionThread extends Thread {

    private int waiting;

    public void run() {
        try {
            while (!isInterrupted() && !WeiXmppManager.getInstance().isConnected()) {
                Thread.sleep((long) waiting() * 1000L);
                WeiXmppManager.getInstance().login();
                waiting++;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    private int waiting() {
        if (waiting > 20) {
            return 600;
        }
        if (waiting > 13) {
            return 300;
        }
        return waiting <= 7 ? 10 : 60;
    }
}
