#Droid Migrate
##Android SQLite migrations made easy!

[Rails migrations](http://guides.rubyonrails.org/migrations.html) are beautiful. Progressive database changes are nicely encapsulated in _migrations_, which can be applied for both upgrades and rollbacks. When I started working on an Android app that made use of SQLite, I found myself wishing for the same facility. 

Android does come with a helper class, `android.database.sqlite.SQLiteOpenHelper`, that facilitates database migrations _somewhat_ and this framework makes use of it. In essence, the `SQLiteOpenHelper` class keys off of a database version and will run a corresponding `onCreate`, `onUpgrade`, or `onDowngrade` method depending on what version number an instance is initialized with. What those methods do and what version number is used is left entirely up to you, the app developer. 

That's where Droid Migrate comes in -- this project manages version numbers and the migrations associated with them. Each migration can have a `up` and `down` method associated with it. You then plug in the appropriate SQL.

If you've ever worked with Rails migrations, then you'll be right at home. If Rails migrations are new you to, but you've managed to deal with app SQLite upgrades and rollbacks, then you'll find this framework quite useful. 

#How to get started

##Step 1

Clone this repository (or download the zip file). Fire up a terminal and run `ant dist`. This will run some tests and ultimately build a few jar files. One jar file contains the bare minimum classes you'll need in an Android project (it's 3KB in size). The other jar file has code required for generating migrations and is not indented to be bundled with your app. 

Once you've done that, you'll need to do two things:  create new environment variable dubbed `DROID_MIGRATE_HOME` and update your `PATH`. `DROID_MIGRATE_HOME` should be set to where you cloned (or unzipped) this project. Update your `PATH` to include `DROID_MIGRATE_HOME\bin`. 

##Step 2

Next, 



