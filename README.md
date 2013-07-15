#Droid Migrate - Android SQLite migrations made easy!

Rails migrations are beautiful. Progressive database changes are nicely encapsulated in _migrations_, which can be applied both back and forth. When I started working on an Android app that made use of SQLite, I found myself wishing for the same facility. 

Android does come with a helper class, `android.database.sqlite.SQLiteOpenHelper`, that facilitates database migrations _somewhat_ and this framework makes use of it. In essence, the `SQLiteOpenHelper` class keys off of a database version and will run a corresponding `onCreate`, `onUpgrade`, or `onDowngrade` method depending on what version number an instance is initialized with. What those methods do and what version number is used is left entirely up to you, the app developer. That's where Droid Migrate comes in -- this project manages version numbers and the migrations associated with them. Each migration can have a `up` and `down` method associated with it. You then plug in the appropriate SQL.

If you've ever worked with Rails migrations, then you'll be right at home. If Rails migrations are new you to, but you've managed to deal with app SQLite upgrades and rollbacks, then you'll find this framework quite useful. 

#How to get started


##Staging

