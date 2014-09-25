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
import com.commercehub.dropwizard.mongo.MongoClientFactory;
import com.commercehub.dropwizard.mongo.MongoConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.mongeez.Mongeez;

import java.net.UnknownHostException;

public abstract class AbstractMongeezCommand<T extends Configuration> extends ConfiguredCommand<T> {

    private final MongoConfiguration<T> mongoConfiguration;
    private final Class<T> configurationClass;

    protected AbstractMongeezCommand(String name,
                                     String description,
                                     MongoConfiguration<T> mongoConfiguration,
                                     Class<T> configurationClass) {
        super(name, description);
        this.mongoConfiguration = mongoConfiguration;
        this.configurationClass = configurationClass;
    }

    @Override
    protected Class<T> getConfigurationClass() {
        return configurationClass;
    }

    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);
        subparser.addArgument("--mongeez-file")
                .dest("mongeez-file")
                .help("the Mongeez file for the application");
    }

    @Override
    protected void run(Bootstrap<T> bootstrap, Namespace namespace, T configuration) throws Exception {
        final MongoClientFactory mongoClientFactory = mongoConfiguration.getMongoClientFactory(configuration);
        try (final CloseableMongeez mongeez = openMongeez(mongoClientFactory, namespace)) {
            run(namespace, mongeez);
        }
    }

    private CloseableMongeez openMongeez(final MongoClientFactory mongoClientFactory, final Namespace namespace)
            throws UnknownHostException {
        final CloseableMongeez mongeez;
        final ManagedMongoClient mongoClient = mongoClientFactory.build();
        final String dbName = mongoClientFactory.getDbName();
        final String mongeezFile = namespace.getString("mongeez-file");
        if (mongeezFile == null) {
            mongeez = new CloseableMongeez(mongoClient, dbName);
        } else {
            mongeez = new CloseableMongeez(mongoClient, dbName, mongeezFile);
        }
        return mongeez;
    }

    protected abstract void run(Namespace namespace, Mongeez mongeez) throws Exception;

}
