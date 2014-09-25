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

import com.commercehub.dropwizard.mongo.MongoConfiguration;
import com.google.common.collect.Maps;
import io.dropwizard.Configuration;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.mongeez.Mongeez;

import java.util.SortedMap;

public class MongoCommand<T extends Configuration> extends AbstractMongeezCommand<T> {

    private static final String COMMAND_NAME_ATTR = "subcommand";

    private final SortedMap<String, AbstractMongeezCommand<T>> subcommands;

    public MongoCommand(MongoConfiguration<T> mongoConfiguration, Class<T> configurationClass) {
        super("mongo", "Run MongoDB migration tasks", mongoConfiguration, configurationClass);
        this.subcommands = Maps.newTreeMap();
        addSubcommand(new MongoMigrateCommand<>(mongoConfiguration, configurationClass));
    }

    private void addSubcommand(AbstractMongeezCommand<T> subcommand) {
        subcommands.put(subcommand.getName(), subcommand);
    }

    @Override
    public void configure(Subparser subparser) {
        for (AbstractMongeezCommand<T> subcommand : subcommands.values()) {
            final Subparser cmdParser = subparser.addSubparsers()
                                                 .addParser(subcommand.getName())
                                                 .setDefault(COMMAND_NAME_ATTR, subcommand.getName())
                                                 .description(subcommand.getDescription());
            subcommand.configure(cmdParser);
        }
    }

    @Override
    public void run(Namespace namespace, Mongeez mongeez) throws Exception {
        final AbstractMongeezCommand<T> subcommand = subcommands.get(namespace.getString(COMMAND_NAME_ATTR));
        subcommand.run(namespace, mongeez);
    }

}
