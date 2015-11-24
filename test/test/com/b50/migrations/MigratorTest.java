package test.com.b50.migrations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import android.database.sqlite.SQLiteDatabase;

import com.b50.migrations.Migrator;
import com.b50.migrations.MigrationException;

public class MigratorTest {

	@Test
	public void testInitDBCreationLogic() throws Exception {
		SQLiteDatabase mockedDB = mock(SQLiteDatabase.class);
		Migrator migrator = new Migrator("test.com.b50.migrations");
		migrator.initialMigration(mockedDB);
		verify(mockedDB, times(1)).execSQL("some sql create stmts");
	}

	@Test
	public void testDBUpLogicMultiple() throws Exception {
		SQLiteDatabase mockedDB = mock(SQLiteDatabase.class);
		Migrator migrator = new Migrator("test.com.b50.migrations");
		migrator.upgrade(mockedDB, 1, 3);
		verify(mockedDB, times(1)).execSQL("some additional sql stmts from #2");
		verify(mockedDB, times(1)).execSQL("some additional sql stmts from #3");
	}
	
	@Test(expected = MigrationException.class)
	public void testDBLogicException() throws Exception {
		SQLiteDatabase mockedDB = mock(SQLiteDatabase.class);
		Migrator migrator = new Migrator("test.com.b50.migrations.nopackage");
		migrator.upgrade(mockedDB, 1, 2);		
	}

	@Test
	public void testDBCanIgnoreMissingInitialMigrationClass() throws Exception {
		SQLiteDatabase mockedDB = mock(SQLiteDatabase.class);
		Migrator migrator = new Migrator("test.com.b50.migrations");
		migrator.setAlertOnMissingMigrations(false);
		migrator.initialMigration(mockedDB);
	}

	@Test
	public void testDBCanIgnoreMissingClasses() throws Exception {
		SQLiteDatabase mockedDB = mock(SQLiteDatabase.class);
		Migrator migrator = new Migrator("test.com.b50.migrations");
		migrator.setAlertOnMissingMigrations(false);
		migrator.upgrade(mockedDB, 1, 4); // Migration version 4 does not exist
		verify(mockedDB, times(1)).execSQL("some additional sql stmts from #2");
		verify(mockedDB, times(1)).execSQL("some additional sql stmts from #3");
	}

	@Test
	public void testDBUpLogic() throws Exception {
		SQLiteDatabase mockedDB = mock(SQLiteDatabase.class);
		Migrator migrator = new Migrator("test.com.b50.migrations");
		migrator.upgrade(mockedDB, 2, 3);
		verify(mockedDB, times(1)).execSQL("some additional sql stmts from #3");
	}

	@Test
	public void testDBDownLogic() throws Exception {
		SQLiteDatabase mockedDB = mock(SQLiteDatabase.class);
		Migrator migrator = new Migrator("test.com.b50.migrations");
		migrator.downgrade(mockedDB, 3, 2);
		verify(mockedDB, times(1)).execSQL("some delete sql stmts from #3");
	}

	@Test
	public void testDBDownLogicMultiple() throws Exception {
		SQLiteDatabase mockedDB = mock(SQLiteDatabase.class);
		Migrator migrator = new Migrator("test.com.b50.migrations");
		migrator.downgrade(mockedDB, 3, 1);
		verify(mockedDB, times(1)).execSQL("some delete sql stmts from #3");
		verify(mockedDB, times(1)).execSQL("some delete sql stmts from #2");
	}
}
