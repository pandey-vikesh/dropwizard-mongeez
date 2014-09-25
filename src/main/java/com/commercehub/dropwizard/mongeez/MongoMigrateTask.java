package com.commercehub.dropwizard.mongeez;

import com.commercehub.dropwizard.mongo.MongoConfiguration;
import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.Configuration;
import org.mongeez.Mongeez;

import java.io.PrintWriter;

public class MongoMigrateTask<T extends Configuration> extends AbstractMongoTask<T> {

    public MongoMigrateTask(String name, MongoConfiguration<T> mongoConfiguration, T configuration) {
        super(name, mongoConfiguration, configuration);
    }

    @Override
    protected void run(ImmutableMultimap<String, String> parameters, PrintWriter output, Mongeez mongeez)
            throws Exception {
        mongeez.process();
        // TODO Provide some useful output (how many changesets were executed, which ones, etc.)
    }

}
