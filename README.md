# Overview

A [Dropwizard](http://dropwizard.io/) bundle for using [mongeez](https://github.com/secondmarket/mongeez) to manage
[MongoDB](http://www.mongodb.org/) data migrations.


# Usage

First, add a dependency to your build file.  Releases are published to
[Bintray JCenter](https://bintray.com/bintray/jcenter).  See the [changelog](CHANGES.md) for the latest version.

Gradle:

```groovy
dependencies {
    compile "com.commercehub.dropwizard:dropwizard-mongeez:1.0.0"
}
```

Maven:

```xml
<dependency>
  <groupId>com.commercehub.dropwizard</groupId>
  <artifactId>dropwizard-mongeez</artifactId>
  <version>1.0.0</version>
</dependency>
```

Next, add a field of type `MongoClientFactory` to your application's configuration class:

```java
public class AppConfiguration extends Configuration {

    @Valid
    @NotNull
    private MongoClientFactory mongo = new MongoClientFactory();
    
    @JsonProperty
    public MongoClientFactory getMongo() {
        return mongo;
    }
    
    @JsonProperty
    public void setMongo(MongoClientFactory mongo) {
        this.mongo = mongo;
    }

}
```

Add the necessary bits to your configuration file:

```yaml
mongo:
  uri: mongodb://localhost:27017/?maxPoolSize=50&maxIdleTimeMS=300000
  dbName: mydb
```

Put change log files somewhere under `src/main/resources`. For example, `src/main/resources/migrations/changeset1.xml`:

```xml
<mongoChangeLog>
    <changeSet changeId="ChangeSet-1" author="mlysaght">
        <script>
            db.organization.insert({
            "Name" : "10Gen", "Location" : "NYC", DateFounded : {"Year":2008, "Month":01, "day":01}});
            db.organization.insert({
            "Name" : "SecondMarket", "Location" : "NYC", DateFounded : {"Year":2004, "Month":5, "day":4}});
        </script>
    </changeSet>
    <changeSet changeId="ChangeSet-2" author="mlysaght">
        <script>
            db.user.insert({ "Name" : "Michael Lysaght"});
        </script>
        <script>
            db.user.insert({ "Name" : "Oleksii Iepishkin"});
        </script>
    </changeSet>
</mongoChangeLog>
```

Add a `mongeez.xml` file to `src/main/resources`:

```xml
<changeFiles>
    <file path="migrations/changeset1.xml"/>
</changeFiles>
```


## Commands

In your `Application` class, add a `MongeezBundle`:

```java
public class App extends Application<AppConfiguration> {

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.addBundle(new MongeezBundle<AppConfiguration>() {

            @Override
            public MongoClientFactory getMongoClientFactory(AppConfiguration config) {
                return config.getMongo();
            }

        });
    }
    
}
```

Now you can apply pending migrations with the `mongo migrate` command:

```
$ java -jar app.jar mongo migrate config.yml
INFO  [2014-09-25 01:11:21,805] org.mongeez.reader.FilesetXMLReader: Num of changefiles 1
INFO  [2014-09-25 01:11:22,000] org.mongeez.ChangeSetExecutor: ChangeSet ChangeSet-1 has been executed
INFO  [2014-09-25 01:11:22,004] org.mongeez.ChangeSetExecutor: ChangeSet ChangeSet-2 has been executed
```


## Tasks

In your `Application` class, add a `MongoMigrateTask`:

```java
public class App extends Application<AppConfiguration> {

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {
        MongoConfiguration<AppConfiguration> mongoConfig = new MongoConfiguration<AppConfiguration>() {

            @Override
            public MongoClientFactory getMongoClientFactory(AppConfiguration configuration) {
                return configuration.getMongo();
            }

        };
        Task migrateTask = new MongoMigrateTask<>("mongoMigrate", mongoConfig, configuration);
        environment.admin().addTask(migrateTask);
    }

}
```

Now you can apply pending migrations by executing the `mongoMigrate` task with an HTTP POST:

```
$ curl -X POST http://localhost:8081/tasks/mongoMigrate
```

Mongeez will output changeset execution information in your application's log file:

```
INFO  [2014-09-25 13:55:32,589] org.mongeez.reader.FilesetXMLReader: Num of changefiles 1
INFO  [2014-09-25 13:55:32,721] org.mongeez.ChangeSetExecutor: ChangeSet ChangeSet-1 has been executed
INFO  [2014-09-25 13:55:32,740] org.mongeez.ChangeSetExecutor: ChangeSet ChangeSet-2 has been executed
```


# Development

## Releasing
Releases are uploaded to [Bintray](https://bintray.com/) via the
[gradle-release](https://github.com/townsfolk/gradle-release) plugin and
[gradle-bintray-plugin](https://github.com/bintray/gradle-bintray-plugin). To upload a new release, you need to be a
member of the [commercehub-oss Bintray organization](https://bintray.com/commercehub-oss). You need to specify your
Bintray username and API key when uploading. Your API key can be found on your
[Bintray user profile page](https://bintray.com/profile/edit). You can put your username and API key in
`~/.gradle/gradle.properties` like so:

    bintrayUserName = johndoe
    bintrayApiKey = 0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef

Then, to upload the release:

    ./gradlew release

Alternatively, you can specify your Bintray username and API key on the command line:

    ./gradlew -PbintrayUserName=johndoe -PbintrayApiKey=0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef release

The `release` task will prompt you to enter the version to be released, and will create and push a release tag for the
specified version. It will also upload the release artifacts to Bintray.

After the release artifacts have been uploaded to Bintray, they must be published to become visible to users. See
Bintray's [Publishing](https://bintray.com/docs/uploads/uploads_publishing.html) documentation for instructions.

After publishing the release on Bintray, it's also nice to create a GitHub release. To do so:
*   Visit the project's [releases](https://github.com/commercehub-oss/dropwizard-mongeez/releases) page
*   Click the "Draft a new release" button
*   Select the tag that was created by the Gradle `release` task
*   Enter a title; typically, this should match the tag (e.g. "1.2.0")
*   Enter a description of what changed since the previous release (see the [changelog](CHANGES.md))
*   Click the "Publish release" button

# License
This library is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

(c) All rights reserved Commerce Technologies, Inc.
