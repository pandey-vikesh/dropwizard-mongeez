package com.commercehub.dropwizard.mongeez;

import com.commercehub.dropwizard.mongo.ManagedMongoClient;
import com.commercehub.dropwizard.mongo.MongoClientFactory;
import com.commercehub.dropwizard.mongo.MongoConfiguration;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.Configuration;
import io.dropwizard.servlets.tasks.Task;
import org.mongeez.Mongeez;

import java.io.PrintWriter;
import java.net.UnknownHostException;

public abstract class AbstractMongoTask<T extends Configuration> extends Task {

    private final MongoConfiguration<T> mongoConfiguration;
    private final T configuration;

    public AbstractMongoTask(String name, MongoConfiguration<T> mongoConfiguration, T configuration) {
        super(name);
        this.mongoConfiguration = mongoConfiguration;
        this.configuration = configuration;
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
        final MongoClientFactory mongoClientFactory = mongoConfiguration.getMongoClientFactory(configuration);
        try (final CloseableMongeez mongeez = openMongeez(mongoClientFactory, parameters)) {
            run(parameters, output, mongeez);
        }
    }

    private CloseableMongeez openMongeez(final MongoClientFactory mongoClientFactory,
                                         ImmutableMultimap<String, String> parameters) throws UnknownHostException {
        final CloseableMongeez mongeez;
        final ManagedMongoClient mongoClient = mongoClientFactory.build();
        final String dbName = mongoClientFactory.getDbName();
        final ImmutableList<String> mongeezFiles = parameters.get("mongeez-file").asList();
        final String mongeezFile = mongeezFiles.size() > 0 ? mongeezFiles.get(0) : null;
        if (mongeezFile == null) {
            mongeez = new CloseableMongeez(mongoClient, dbName);
        } else {
            mongeez = new CloseableMongeez(mongoClient, dbName, mongeezFile);
        }
        return mongeez;
    }

    protected abstract void run(ImmutableMultimap<String, String> parameters, PrintWriter output, Mongeez mongeez)
            throws Exception;

}
