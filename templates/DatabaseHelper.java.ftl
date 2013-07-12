package ${package};

import com.b50.migrations.MigrationException;
import com.b50.migrations.Migrator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private Migrator migrator;

	public DatabaseHelper(Context context) {
		super(context, context.getString(R.string.database_name), 
			  null, context.getResources().getInteger(R.integer.database_version));
		this.migrator = new Migrator(context.getString(R.string.package_name));
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			migrator.downgrade(db, oldVersion, newVersion);
		} catch (MigrationException e) {
			Log.e("Migrator", "exception with onDowngrade", e);
		}
	}

	public void onCreate(SQLiteDatabase db) {
		try {
			migrator.initialMigration(db);
		} catch (MigrationException e) {
			Log.e("Migrator", "exception with onCreate", e);
		}
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			migrator.upgrade(db, oldVersion, newVersion);
		} catch (MigrationException e) {
			Log.e("Migrator", "exception with onUpgrade", e);
		}

	}

	public static void cleanupResources(SQLiteDatabase db, Cursor cursor) {
		try { cleanupCurosr(cursor); } catch (Exception e) {}
		try { db.close(); } catch (Exception e) {}
	}

	public static void cleanupCurosr(Cursor cursor) {
		try {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		} catch (Exception e) {}
	}
}
