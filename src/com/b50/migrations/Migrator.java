package com.b50.migrations;

import android.database.sqlite.SQLiteDatabase;

public class Migrator {
	private String packageName;
	private boolean alertOnMissingMigrations = true;

	public Migrator(String packageName) {
		this.packageName = packageName;
	}

	public void setAlertOnMissingMigrations(boolean alertOnMissingMigrations) {
		this.alertOnMissingMigrations = alertOnMissingMigrations;
	}

	public void initialMigration(SQLiteDatabase db) throws MigrationException {
		try {
			handleUp(db, packageName, "DBVersion1");
		} catch (InstantiationException e) {
			throw new MigrationException(e);
		} catch (IllegalAccessException e) {
			throw new MigrationException(e);
		} catch (ClassNotFoundException e) {
			if (alertOnMissingMigrations) {
				throw new MigrationException(e);
			}
		}
	}

	public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws MigrationException {
		for (int x = (oldVersion + 1); x <= newVersion; x++) {
			try {
				handleUp(db, packageName, "DBVersion" + x);
			} catch (InstantiationException e) {
				throw new MigrationException(e);
			} catch (IllegalAccessException e) {
				throw new MigrationException(e);
			} catch (ClassNotFoundException e) {
				if (alertOnMissingMigrations) {
					throw new MigrationException(e);
				}
			}
		}
	}

	public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws MigrationException {
		for (int x = oldVersion; x > newVersion; x--) {
			try {
				handleDown(db, packageName, "DBVersion" + x);
			} catch (InstantiationException e) {
				throw new MigrationException(e);
			} catch (IllegalAccessException e) {
				throw new MigrationException(e);
			} catch (ClassNotFoundException e) {
				if (alertOnMissingMigrations) {
					throw new MigrationException(e);
				}
			}
		}
	}

	private AbstractMigration getMigrationForPackageAndName(SQLiteDatabase db, String pckName, String clzzName)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		AbstractMigration migration = (AbstractMigration) Class.forName(pckName + "." + clzzName).newInstance();
		migration.setDatabase(db);
		return migration;
	}

	private void handleUp(SQLiteDatabase db, String pckName, String clzzName) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		AbstractMigration migration = getMigrationForPackageAndName(db, packageName, clzzName);
		migration.up();
	}

	private void handleDown(SQLiteDatabase db, String pckName, String clzzName) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		AbstractMigration migration = getMigrationForPackageAndName(db, packageName, clzzName);
		migration.down();
	}
}
