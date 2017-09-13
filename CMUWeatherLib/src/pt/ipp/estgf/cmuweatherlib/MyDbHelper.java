package pt.ipp.estgf.cmuweatherlib;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDbHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "WEATHERDROID_DATABASE";
	
    private static final String DATABASE_NAME = "weatherdroid.db";
    private static final int DATABASE_VERSION = 1;
    
    private final Context mContext;

    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }
	
	// executa um script externo com instru��es SQL
	protected void execSQLScript(int fileRes, SQLiteDatabase _db) {
		ScriptFileReader sfr = new ScriptFileReader(mContext, fileRes);
		sfr.open();

		String str = sfr.nextLine();
		while (str != null) {
			if (str.trim().length() > 0 && !str.startsWith("--"))
				_db.execSQL(str);
			str = sfr.nextLine();
		}

		sfr.close();
		sfr = null;
	}

    // invocado quando a base de dados n�o existe no disco
    // !!! AP�S MODIFICA��ES NESTE M�TODO, � NECESS�RIO MUDAR O
    // VALOR DE DATABASE_VERSION PARA QUE AS ALTERA��ES TENHAM EFEITO
    @Override
    public void onCreate(SQLiteDatabase _db) {
        Log.w(TAG, "Creating Database...");
		execSQLScript(R.raw.tbl_creates, _db);
        init(_db);
    }

    // inicializa a base de dados
    // !!! AP�S MODIFICA��ES NESTE M�TODO, � NECESS�RIO MUDAR O
    // VALOR DE DATABASE_VERSION PARA QUE AS ALTERA��ES TENHAM EFEITO
    protected void init(SQLiteDatabase _db) {
        Log.w(TAG, "Initializing database...");
		execSQLScript(R.raw.tbl_init, _db);
    }

    // elimina a base de dados
    // !!! AP�S MODIFICA��ES NESTE M�TODO, � NECESS�RIO MUDAR O
    // VALOR DE DATABASE_VERSION PARA QUE AS ALTERA��ES TENHAM EFEITO
    protected void dropAll(SQLiteDatabase _db) {
        Log.w(TAG, "Droping Database...");
		execSQLScript(R.raw.tbl_drops, _db);
    }

    // invocado quando existe uma base de dados que n�o corresponde
    // � vers�o actual
    // !!! AP�S MODIFICA��ES NESTE M�TODO, � NECESS�RIO MUDAR O
    // VALOR DE DATABASE_VERSION PARA QUE AS ALTERA��ES TENHAM EFEITO
    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
            int _newVersion) {
        Log.w(TAG, "Upgrading from version " + _oldVersion + " to " + _newVersion + ", which will destroy all old data.");

        dropAll(_db);
        onCreate(_db);
    }
}

