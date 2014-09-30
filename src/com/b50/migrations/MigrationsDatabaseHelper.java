package com.b50.migrations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MigrationsDatabaseHelper extends SQLiteOpenHelper {

	protected Migrator migrator;
	protected int intendedVersion;

	public MigrationsDatabaseHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory,
			int dbVersion, String packageName) {
		super(context, dbName, null, dbVersion);
		this.migrator = new Migrator(packageName);
		this.intendedVersion = dbVersion;
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
			if (intendedVersion > 1) {
				onUpgrade(db, 1, intendedVersion);
			}
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
