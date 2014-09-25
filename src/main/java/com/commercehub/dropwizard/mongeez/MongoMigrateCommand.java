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
import io.dropwizard.Configuration;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.mongeez.Mongeez;

public class MongoMigrateCommand<T extends Configuration> extends AbstractMongeezCommand<T> {

    public MongoMigrateCommand(MongoConfiguration<T> mongoConfiguration, Class<T> configurationClass) {
        super("migrate", "Apply all pending change sets.", mongoConfiguration, configurationClass);
    }

    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);
    }

    @Override
    public void run(Namespace namespace, Mongeez mongeez) throws Exception {
        mongeez.process();
    }

}
