/*
 * Copyright (c) 2017-present ArcBlock Foundation Ltd <https://www.arcblock.io/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.arcblock.btcaccounttool.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.arcblock.btcaccounttool.bean.StaredAccountBean;
import com.arcblock.btcaccounttool.dao.StaredAccountDao;

@Database(entities = {StaredAccountBean.class}, version = 2)
public abstract class AccountToolDB extends RoomDatabase {

	private static AccountToolDB sInstance;

	public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
		@Override
		public void migrate(@NonNull SupportSQLiteDatabase database) {
			database.execSQL("ALTER TABLE stared_account "
					+ "ADD COLUMN del_flag INTEGER");
		}
	};

	public static AccountToolDB getDatabase(Context context) {
		if (sInstance == null) {
			sInstance = Room.databaseBuilder(context.getApplicationContext(), AccountToolDB.class,
					"account_tool.db").addMigrations(MIGRATION_1_2).build();
		}
		return sInstance;
	}

	public static void onDestroy() {
		sInstance = null;
	}

	@MainThread
	public abstract StaredAccountDao staredAccountDao();

}
