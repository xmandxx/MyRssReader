/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ab.db.storage;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * © 2012 amsoft.cn
 * 名称：AbSqliteStorageListener.java 
 * 描述：数据操作结构监听器
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2013-10-16 下午1:33:39
 */
public class AbSqliteStorageListener {

	/**
	 * 描述：插入数据的监听.
	 *
	 * @see AbDataInsertEvent
	 */
	public static abstract interface AbDataInsertListener {

		/**
		 * On success.
		 *
		 * @param paramLong the param long
		 */
		public abstract void onSuccess(long paramLong);

		/**
		 * On failure.
		 *
		 * @param errorCode the error code
		 * @param errorMessage the error message
		 */
		public abstract void onFailure(int errorCode, String errorMessage);
	}

	/**
	 * 描述：查询数据的监听.
	 *
	 * @see AbDataInfoEvent
	 */
	public static abstract interface AbDataInfoListener {

		/**
		 * On success.
		 *
		 * @param paramList the param list
		 */
		public abstract void onSuccess(List<?> paramList);

		/**
		 * On failure.
		 *
		 * @param errorCode the error code
		 * @param errorMessage the error message
		 */
		public abstract void onFailure(int errorCode, String errorMessage);
	}

	/**
	 * 描述：修改数据的监听.
	 *
	 * @see AbDataOperationEvent
	 */
	public static abstract interface AbDataOperationListener {
		
		/**
		 * On success.
		 *
		 * @param paramLong the param long
		 */
		public abstract void onSuccess(long paramLong);

		/**
		 * On failure.
		 *
		 * @param errorCode the error code
		 * @param errorMessage the error message
		 */
		public abstract void onFailure(int errorCode, String errorMessage);
	}
}
