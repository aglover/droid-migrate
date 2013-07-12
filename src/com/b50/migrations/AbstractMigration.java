package com.b50.migrations;

import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractMigration {
	
	protected SQLiteDatabase database;
	
	public AbstractMigration(){}
	
	public void setDatabase(SQLiteDatabase database){
		this.database = database;
	}
	
	protected void execSQL(String sql){
		this.database.execSQL(sql);
	}

	public abstract void up();

	public abstract void down();

}
