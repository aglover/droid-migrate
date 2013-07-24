#Droid Migrate
##Android SQLite migrations made easy!

Two easy commands. One to create your first migration:

```
$> droid-migrate init -d my_database
```

And another to upgrade:

```
$> droid-migrate generate up
```

Need to rollback? No problem, run:

```
$> droid-migrate generate down
```

You can read my blog entry entitled [Introducing Android Migrations](http://thediscoblog.com/blog/2013/07/24/introducing-android-migrations/) for a detailed tutorial or you can read on. 

## Why do I need Droid Migrate?

[Rails migrations](http://guides.rubyonrails.org/migrations.html) are beautiful. Progressive database changes are nicely encapsulated in _migrations_, which can be applied for both upgrades and rollbacks. When I started working on an Android app that made use of SQLite, I found myself wishing for the same facility. 

Android does come with a helper class, `android.database.sqlite.SQLiteOpenHelper`, that facilitates database migrations _somewhat_ and this framework makes use of it. In essence, the `SQLiteOpenHelper` class keys off of a database version and will run a corresponding `onCreate`, `onUpgrade`, or `onDowngrade` method depending on what version number an instance is initialized with. What those methods do and what version number is used is left entirely up to you, the app developer. 

That's where Droid Migrate comes in -- this project manages version numbers and the migrations associated with them. Each migration can have a `up` and `down` method associated with it. You then plug in the appropriate SQL. Droid Migrate handles upgrades and rollbacks like Rails migrations. 

Droid Migrate also provides you with a handy facade, dubbed `DatabaseHelper` from which you should obtain database connections (for doing things like displaying data, writing to SQLite, etc). This class is the core behind any migration that'll take place for your app. 

If you've ever worked with Rails migrations, then you'll be right at home. If Rails migrations are new you to, but you've managed to deal with app SQLite upgrades and rollbacks, then you'll find this framework quite useful. 

#How to get started

##Step 1: Set up PATH

Clone this repository (or download the zip file). Fire up a terminal and run `ant dist`. This will run some tests and ultimately build a few jar files. One jar file contains the bare minimum classes you'll need in an Android project (it's 3KB in size). The other jar file has code required for generating migrations and is not indented to be bundled with your app. 

Once you've done that, you'll need to do two things:  create new environment variable dubbed `DROID_MIGRATE_HOME` and update your `PATH`. `DROID_MIGRATE_HOME` should be set to where you cloned (or unzipped) this project. Update your `PATH` to include `DROID_MIGRATE_HOME\bin`. 

##Step 2: Create initial migration

Next, open a terminal in the root of a desired Android project. Droid Migrate will create a number of assets for you depending on what command you issue. 

If you are creating an initial migration -- i.e. on app create, for example, then you'll need to initialize Droid Migrate like so:

```
$> droid-migrate init -d <your database name> -p <some Java package>
```

That is, the ```init``` command creates an initial migration for the database as specified by the ```-d``` flag and the migration class (along with a few other classes) will be put into the package as specified by the ```-p``` flag.

For example, if you specified the ```-p``` flag's value as ```com.acme.app``` then that package will have two classes:

+ ```DatabaseHelper```
+ ```DBVersion1```

Where the class ```DatabaseHelper``` manages migrations and ```DBVersion1``` is your first initial migration. What's more, a new XML document will be generated in the ```res/values``` directory dubbed ```migrations.xml```. This document will contain values for the database sequence for migrations, the package name, and the database name. That XML document will be represented in your app's ```R``` file; you'll notice that the class ```DatabaseHelper``` makes use of the ```R``` object too.

You will need to implement your database creation via the ```DBVersion1``` class -- simply provide your SQL `String` statements to the `execSQL` method inside the `up` method (and corresponding logic in the `down` method for rollbacks). 

For example, if you were creating a table dubbed `hops` and populating it with data, your initial migration's `up` and `down` methods could be implemented like so:

```
package com.b50.databaseexample;

import com.b50.migrations.AbstractMigration;

public class DBVersion1 extends AbstractMigration {

	public void up() {
		String create = "CREATE TABLE hops (_id integer  PRIMARY KEY AUTOINCREMENT DEFAULT NULL, name TEXT, "
				+ "description TEXT, substitutions TEXT DEFAULT '', alpha_acid TEXT DEFAULT '', beer_styles TEXT DEFAULT '', "
				+ "type TEXT DEFAULT '', user_notes TEXT DEFAULT '');";
		execSQL(create);
		String oneThing = "INSERT INTO 'hops' VALUES(1,'Amarillo','Spicy hop with mild bitterness and a noble aroma.  "
				+ "Good all around hop.','Cascade, Centennial','7 to 10','Ale, IPA','Aroma', '');";
		execSQL(oneThing);
	}

	public void down() {
		execSQL("DROP TABLE hops;");
	}
}
```

You _do not_ need to edit any other files (i.e. don't worry about the `migrations.xml` file nor the `DatabaseHelper` class).

Once your app starts up on a device, so long as you use the `DatabaseHelper` object to obtain a SQL connection for building `ListView`s or what have you, a migration will take place. 

For example, inside an `onCreate` method of your main `Activity`, you can grab a connection to SQLite like so: 

```
SQLiteDatabase db = (new DatabaseHelper(this)).getWritableDatabase();
```

This will result in a relevant migration.

##Step 3: Create subsequent migrations

When you need to update SQLite either with database tables changes (i.e. ```ALTER``` commands) or you need to add more data (```INSERT```), you can generate a migration. This process will increment the database version found in the ```migrations.xml``` file and create a new migration class (```DBVersion<next_sequence>```. Note, you can even generate a rollback. 

If you want to generate an upgrade migration, you need to open a terminal in the root of a previously Droid Migrate initialized project and type:

```
$> droid-migrate generate up
```

And if you need to rollback to a previous version, type:

```
$> droid-migrate generate down
```

You will then need to provide the relevant SQL details in the newly generated `DBVersion<next_sequence>` class (just like you did for the initial class, dubbed `DBVersion1`).

#How it works

Droid Migrate's secret sauce can be found in three files: 

+ ```DatabaseHelper```
+ ```DBVersion<some_sequence>```
+ ```res/values/migrations.xml```

`DatabaseHelper`, as mentioned in the how to section, is a simple facade that makes working with a SQLite instance easier. Think of it (or any class that extends Android's `SQLiteOpenHelper`) as the canonical way to interact with SQLite in an Android app. 

In practice, you shouldn't ever need to alter this class. In fact, it is important that you don't alter the constructor, nor override the methods `onDowngrade`, `onCreate`, or `onUpgrade` -- take a look at the source code for these methods if you will, but they leverage a simple class dubbed `Migrator` that ultimately works with instances of `AbstractMigration`, which all `DBVersion<sequence_number>` types extend from. 

For every corresponding version of your app's database, there's a `DBVersion` class -- these classes are named in sequence (i.e. `DBVersion1`, `DBVersion2`, and so on). These classes have `up` and `down` methods that correspond to upgrades and rollbacks, respectively. The base type, `AbstractMigration` has a method called `execSQL` that takes a `String` representing one to many SQL statements. 

Finally, every Java framework worth its salt has a configuration file and Droid Migrate doesn't disappoint. `migrations.xml` is the beast that drives things (although beast is a stretch as the file has only 3 elements!) . In fact, because Android generates a simple reference file for all elements in XML files (called `R`), you note that the `DatabaseHelper` class makes implicit use of your app's `R` file. 

For instance, the `migrations.xml` file has an element dubbed `database_version`:

```
<integer name="database_version">2</integer>
```

If you take a look in `DatabaseHelper`'s constructor, you'll see this line:

```
super(context, context.getString(R.string.database_name), 
	  null, context.getResources().getInteger(R.integer.database_version));
```

Note the reference to the `R` file obtaining the `database_version` value! 

#Bonus info

I built an app that uses SQLite to manage some simple data. I released it. Then I made some changes. Then I got a headache dealing with upgrades. I got a bigger headache when I saw an example of how to manage migrations (before Droid Migrate!). Take a look at [this class](http://grepcode.com/file_/repository.grepcode.com/java/ext/com.google.android/android-apps/4.0.1_r1/com/android/providers/calendar/CalendarDatabaseHelper.java/?v=source). That's not a trivial class (nor a trivial app), nevertheless, it blows my mind and, in fact, saddens me. So I created Droid Migrate.