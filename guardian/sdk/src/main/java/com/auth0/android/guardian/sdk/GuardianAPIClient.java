/*
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.guardian.sdk;

import com.auth0.android.guardian.sdk.networking.Request;
import com.auth0.android.guardian.sdk.networking.RequestFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.OkHttpClient;

public class GuardianAPIClient {

    private final RequestFactory requestFactory;
    private final String baseUrl;

    GuardianAPIClient(RequestFactory requestFactory, String baseUrl) {
        this.requestFactory = requestFactory;
        this.baseUrl = baseUrl;
    }

    /**
     * Returns the "device_account_token" that can be used to update the push notification settings
     * and also to un-enroll the device account
     * This endpoint should only be called once (when starting the enroll)
     *
     * @param enrollmentTransactionId the enrollment transaction id
     * @return a request to execute
     */
    public GuardianAPIRequest<String> getDeviceToken(String enrollmentTransactionId) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Request<Map<String, String>> request = requestFactory
                .<Map<String, String>>newRequest("POST", completeUrl("api/enrollment-info"), type)
                .addParameter("enrollment_tx_id", enrollmentTransactionId);
        return new DeviceTokenRequest(request);
    }

    private String completeUrl(String path) {
        return String.format("%s/%s", baseUrl, path);
    }

    public static class Builder {

        private String baseUrl;
        private OkHttpClient client;
        private Gson gson;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public Builder gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public GuardianAPIClient build() {
            if (baseUrl == null) {
                throw new IllegalArgumentException("baseUrl cannot be null");
            }

            if (client == null) {
                client = new OkHttpClient();
            }

            if (gson == null) {
                gson = new GsonBuilder()
                        .create();
            }

            RequestFactory requestFactory = new RequestFactory(gson, client);

            return new GuardianAPIClient(requestFactory, baseUrl);
        }
    }
}
