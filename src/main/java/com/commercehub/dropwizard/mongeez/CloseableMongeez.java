/*
 * Copyright (C) 2014 Commerce Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.commercehub.dropwizard.mongeez;

import com.commercehub.dropwizard.mongo.ManagedMongoClient;
import org.mongeez.Mongeez;
import org.springframework.core.io.ClassPathResource;

public class CloseableMongeez extends Mongeez implements AutoCloseable {

    private static final String DEFAULT_MONGEEZ_FILE = "mongeez.xml";

    private ManagedMongoClient mongoClient;

    public CloseableMongeez(ManagedMongoClient mongoClient, String dbName) {
        this(mongoClient, dbName, DEFAULT_MONGEEZ_FILE);
    }

    public CloseableMongeez(ManagedMongoClient mongoClient, String dbName, String file) {
        this.mongoClient = mongoClient;
        setMongo(mongoClient);
        setDbName(dbName);
        setFile(new ClassPathResource(file));
    }

    @Override
    public void close() throws Exception {
        mongoClient.stop();
    }

}
