package test.com.b50.migrations;

import com.b50.migrations.AbstractMigration;

public class DBVersion1 extends AbstractMigration {

	@Override
	public void up() {
		execSQL("some sql create stmts");
	}

	@Override
	public void down() {
		execSQL("some delete sql stmts");
	}

}
