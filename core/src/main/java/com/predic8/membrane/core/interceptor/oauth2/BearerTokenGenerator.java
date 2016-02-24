/*
 * Copyright 2016 predic8 GmbH, www.predic8.com
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.predic8.membrane.core.interceptor.oauth2;

import com.predic8.membrane.annot.MCElement;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.NoSuchElementException;

@MCElement(name="bearerToken")
public class BearerTokenGenerator implements TokenGenerator {

    public class User{
        private String username;
        private String clientId;

        public User(String username, String clientId){
            this.username = username;
            this.clientId = clientId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }
    }

    private SecureRandom random = new SecureRandom();
    private HashMap<String,User> tokenToUser = new HashMap<String, User>();

    @Override
    public String getTokenType() {
        return "Bearer";
    }

    @Override
    public String getToken(String username, String clientId) {
        String token = new BigInteger(130, random).toString(32);
        tokenToUser.put(token,new User(username,clientId));
        return token;
    }

    @Override
    public String getUsername(String token) throws NoSuchElementException {
        try {
            return tokenToUser.get(token).getUsername();
        }catch(Exception e){
            throw new NoSuchElementException(e.getMessage());
        }
    }

    @Override
    public String getClientId(String token) throws NoSuchElementException {
        try {
            return tokenToUser.get(token).getClientId();
        }catch(Exception e){
            throw new NoSuchElementException(e.getMessage());
        }
    }

    @Override
    public void invalidateToken(String token, String clientId) throws NoSuchElementException {
        User user = tokenToUser.get(token);
        if (!clientId.equals(user.getClientId()))
            throw new NoSuchElementException("ClientId doesnt match");
        tokenToUser.remove(token);
    }
}
