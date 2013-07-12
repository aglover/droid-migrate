package test.com.b50.migrations;

import com.b50.migrations.AbstractMigration;

public class DBVersion2 extends AbstractMigration {

	@Override
	public void up() {
		execSQL("some additional sql stmts from #2");
	}

	@Override
	public void down() {
		execSQL("some delete sql stmts from #2");
	}

}
