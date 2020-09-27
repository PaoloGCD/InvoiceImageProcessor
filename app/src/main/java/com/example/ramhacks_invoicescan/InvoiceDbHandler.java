package com.example.ramhacks_invoicescan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InvoiceDbHandler extends SQLiteOpenHelper {

    // DATABASE parameters
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "INVOICE_DATABASE";

    // INVOICE TABLE parameters
    public static class InvoiceEntry implements BaseColumns {
        public static final String TABLE_NAME = "INVOICE_TABLE";

        public static final String COLUMN_ID = "_id";
        private static final String COLUMN_WEB_ID = "webId";

        private static final String COLUMN_PATH = "name";
        private static final String COLUMN_TYPE = "birthDate";
        private static final String COLUMN_STORE = "bloodType";
        private static final String COLUMN_COST = "motherBloodType";

        private static final String COLUMN_DATE = "risk";
        private static final String COLUMN_ITEMS = "state";

        private static final String CREATE_BABIES_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_WEB_ID + " TEXT," +

                COLUMN_PATH + " TEXT," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_STORE + " TEXT," +
                COLUMN_COST + " TEXT," +

                COLUMN_DATE + " TEXT," +
                COLUMN_ITEMS + " TEXT" + ")";
    }

    public InvoiceDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(InvoiceEntry.CREATE_BABIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InvoiceEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    // Invoice Methods
    public long addInvoice(InvoiceElement invoice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(InvoiceEntry.COLUMN_WEB_ID, invoice.mWebId);

        values.put(InvoiceEntry.COLUMN_PATH, invoice.mPath);
        values.put(InvoiceEntry.COLUMN_TYPE, invoice.mType);
        values.put(InvoiceEntry.COLUMN_STORE, invoice.mStore);

        values.put(InvoiceEntry.COLUMN_COST, invoice.mCost);

        values.put(InvoiceEntry.COLUMN_DATE, invoice.mDate);
        values.put(InvoiceEntry.COLUMN_ITEMS, invoice.mItems);

        long newRowId = db.insert(InvoiceEntry.TABLE_NAME, null, values);
        db.close(); // Closing database connection
        return newRowId;
    }

    public InvoiceElement getInvoiceById(long queryId){

        String query = "SELECT * FROM " + InvoiceEntry.TABLE_NAME + " WHERE " + InvoiceEntry.COLUMN_ID + " = " + Long.toString(queryId);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery(query, null);

        Integer idIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_ID);
        Integer webIdIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_WEB_ID);
        Integer pathIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_PATH);
        Integer typeIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_TYPE);
        Integer storeIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_STORE);
        Integer costIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_COST);
        Integer dateIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_DATE);
        Integer itemsIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_ITEMS);

        InvoiceElement invoiceElement = new InvoiceElement();

        if (cursor.moveToFirst()) {
            invoiceElement.mId = cursor.getInt(idIndex);
            invoiceElement.mWebId = cursor.getString(webIdIndex);
            invoiceElement.mPath = cursor.getString(pathIndex);
            invoiceElement.mType = cursor.getString(typeIndex);
            invoiceElement.mStore = cursor.getString(storeIndex);
            invoiceElement.mCost = cursor.getString(costIndex);
            invoiceElement.mDate = cursor.getString(dateIndex);
            invoiceElement.mItems = cursor.getString(itemsIndex);
        }

        if (invoiceElement.mPath!=null){
            return invoiceElement;
        } else {
            return null;
        }
    }

    public List<InvoiceElement> getInvoiceByDate() {
        String selectQuery = "SELECT * FROM " + InvoiceEntry.TABLE_NAME + " ORDER BY " + InvoiceEntry.COLUMN_DATE + " DESC";
        return getInvoiceList(selectQuery);
    }

    private List<InvoiceElement> getInvoiceList(String query){

        List<InvoiceElement> allInvoiceList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery(query, null);

        Integer idIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_ID);
        Integer webIdIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_WEB_ID);
        Integer pathIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_PATH);
        Integer typeIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_TYPE);
        Integer storeIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_STORE);
        Integer costIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_COST);
        Integer dateIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_DATE);
        Integer itemsIndex = cursor.getColumnIndex(InvoiceEntry.COLUMN_ITEMS);

        if (cursor.moveToFirst()) {
            do {
                InvoiceElement invoiceElement = new InvoiceElement();
                invoiceElement.mId = cursor.getInt(idIndex);
                invoiceElement.mWebId = cursor.getString(webIdIndex);
                invoiceElement.mPath = cursor.getString(pathIndex);
                invoiceElement.mType = cursor.getString(typeIndex);
                invoiceElement.mStore = cursor.getString(storeIndex);
                invoiceElement.mCost = cursor.getString(costIndex);
                invoiceElement.mDate = cursor.getString(dateIndex);
                invoiceElement.mItems = cursor.getString(itemsIndex);

                allInvoiceList.add(invoiceElement);
            } while (cursor.moveToNext());
        }
        return allInvoiceList;
    }
}
