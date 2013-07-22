package com.b50.migrations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MigrationsDatabaseHelper extends SQLiteOpenHelper{

	protected Migrator migrator;

	public MigrationsDatabaseHelper(Context context, String dbName, Object object, int dbVersion, String packageName) {
		super(context, dbName, null, dbVersion);
		this.migrator = new Migrator(packageName);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			migrator.downgrade(db, oldVersion, newVersion);
		} catch (MigrationException e) {
			Log.e("MigrationsDatabaseHelper", "exception with onDowngrade", e);
		}
	}

	public void onCreate(SQLiteDatabase db) {
		try {
			migrator.initialMigration(db);
		} catch (MigrationException e) {
			Log.e("MigrationsDatabaseHelper", "exception with onCreate", e);
		}
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			migrator.upgrade(db, oldVersion, newVersion);
		} catch (MigrationException e) {
			Log.e("MigrationsDatabaseHelper", "exception with onUpgrade", e);
		}

	}	

}
