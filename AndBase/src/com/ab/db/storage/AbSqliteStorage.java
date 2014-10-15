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

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.ab.db.storage.AbSqliteStorageListener.AbDataInfoListener;
import com.ab.db.storage.AbSqliteStorageListener.AbDataInsertListener;
import com.ab.db.storage.AbSqliteStorageListener.AbDataOperationListener;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListListener;
import com.ab.task.AbTaskObjectListener;
import com.ab.task.AbTaskQueue;

// TODO: Auto-generated Javadoc

/**
 * © 2012 amsoft.cn
 * 名称：AbSqliteStorage.java 
 * 描述：数据库对象操作类
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2013-10-16 下午1:33:39
 */
public class AbSqliteStorage {
	
	/** The m context. */
	private static Context mContext;
	
	/** 单例. */
	private static AbSqliteStorage mSqliteStorage = null;
	
	/** 队列. */
	private static AbTaskQueue mAbTask = null;
	
	/** 参数错误的code. */
	private int errorCode100 = 100;
	
	/** 参数错误的message. */
	private String errorMessage100 = "参数错误";
	
	/** 执行时错误的code. */
	private int errorCode101 = 101;
	
	/** 执行时错误的message. */
	private String errorMessage101 = "执行时错误";
	
	/**
	 * 描述：获取存储实例.
	 *
	 * @param context the context
	 * @return single instance of AbSqliteStorage
	 */
	public static AbSqliteStorage getInstance(Context context){
		mContext = context;
	    if (null == mSqliteStorage){
	    	mSqliteStorage = new AbSqliteStorage(context);
	    }
	    if(mAbTask == null){
	        //用队列避免并发访问数据库问题
	        mAbTask = new AbTaskQueue();
	    }
	    
	    return mSqliteStorage;
	}
	
	/**
	 * 初始化.
	 *
	 * @param context the context
	 */
	private AbSqliteStorage(Context context) {
		super();
	}
	
	/**
	 * 描述：插入数据.
	 *
	 * @param <T> the generic type
	 * @param entity  实体类 设置了对象关系映射
	 * @param dao     实现AbDBDaoImpl的Dao
	 * @param paramDataInsertListener 返回监听器
	 */
	public <T> void insertData(final T entity,final AbDBDaoImpl<T> dao, final AbDataInsertListener paramDataInsertListener){
		 
		 if (entity != null){
	    	
	    	AbTaskItem item = new AbTaskItem();
	    	item.setListener(new AbTaskObjectListener() {
				
				@Override
				public void update(Object obj) {
				    long ret = (Long)obj;
					if(ret >= 0){
						if (paramDataInsertListener != null){
				    		paramDataInsertListener.onSuccess(ret);
					    }
					}else{
						if (paramDataInsertListener != null){
			    		    paramDataInsertListener.onFailure(errorCode101, errorMessage101);
				        }
					}
				}
				
				@Override
				public Object getObject() {
					//执行插入 
					//(1)获取数据库 
					dao.startWritableDatabase(false);
				  	//(2)执行
					long retValue = dao.insert(entity);
				    //(3)关闭数据库
				  	dao.closeDatabase();
				  	return retValue;
				}
			});
	    	mAbTask.execute(item);
	    	
	    }else{
	    	if (paramDataInsertListener != null){
	    		paramDataInsertListener.onFailure(errorCode100, errorMessage100);
		    }
	    }
	    
	}
	
	/**
	 * 描述：插入数据.
	 *
	 * @param <T> the generic type
	 * @param entityList  实体类 设置了对象关系映射
	 * @param dao     实现AbDBDaoImpl的Dao
	 * @param paramDataInsertListener 返回监听器
	 */
	public <T> void insertData(final List<T> entityList,final AbDBDaoImpl<T> dao, final AbDataInsertListener paramDataInsertListener){
		 
		 if (entityList != null){
	    	
	    	AbTaskItem item = new AbTaskItem();
	    	item.setListener(new AbTaskObjectListener() {
				
				@Override
				public void update(Object obj) {
				    long ret = (Long)obj;
					if(ret >= 0){
						if (paramDataInsertListener != null){
				    		paramDataInsertListener.onSuccess(ret);
					    }
					}else{
						if (paramDataInsertListener != null){
			    		    paramDataInsertListener.onFailure(errorCode101, errorMessage101);
				        }
					}
				}
				
				@Override
				public Object getObject() {
					//执行插入 
					//(1)获取数据库 
					dao.startWritableDatabase(false);
				  	//(2)执行
					long retValue = dao.insertList(entityList);
				    //(3)关闭数据库
				  	dao.closeDatabase();
				  	return retValue;
			    	
				}
			});
	    	mAbTask.execute(item);
	    	
	    }else{
	    	if (paramDataInsertListener != null){
	    		paramDataInsertListener.onFailure(errorCode100, errorMessage100);
		    }
	    }
	    
	  }
	
	
	/**
	 * 查找数据.
	 *
	 * @param <T> 描述：查询数据
	 * @param storageQuery the storage query
	 * @param dao     实现AbDBDaoImpl的Dao
	 * @param paramDataInsertListener 返回监听器
	 */
	public <T> void findData(final AbStorageQuery storageQuery,final AbDBDaoImpl<T> dao, final AbDataInfoListener paramDataInsertListener){
		     
	    	final AbTaskItem item = new AbTaskItem();
	    	item.setListener(new AbTaskListListener() {
				
				@Override
				public void update(List<?> paramList) {
					if (paramDataInsertListener != null){
			    		paramDataInsertListener.onSuccess(paramList);
				    }
				}
				
				@Override
				public List<?> getList() {
					List<?> list = null;   
					//执行插入 
					//(1)获取数据库 
					dao.startReadableDatabase();
				  	//(2)执行
					if(storageQuery.getLimit()!=-1 && storageQuery.getOffset()!=-1){
						list = dao.queryList(null, storageQuery.getWhereClause(),storageQuery.getWhereArgs(), storageQuery.getGroupBy(), storageQuery.getHaving(), storageQuery.getOrderBy()+" limit "+storageQuery.getLimit()+ " offset " +storageQuery.getOffset(), null);
					}else{
						list = dao.queryList(null, storageQuery.getWhereClause(),storageQuery.getWhereArgs(), storageQuery.getGroupBy(), storageQuery.getHaving(), storageQuery.getOrderBy(), null);
					}
				    //(3)关闭数据库
				  	dao.closeDatabase();
				  	return list;
			    	
				}
			});
	    	mAbTask.execute(item);
	    
	  }
	
	/**
	 * 描述：修改数据.
	 *
	 * @param <T> the generic type
	 * @param entity  实体类 设置了对象关系映射
	 * @param dao     实现AbDBDaoImpl的Dao
	 * @param paramDataInsertListener 返回监听器
	 */
	public <T> void updateData(final T entity,final AbDBDaoImpl<T> dao, final AbDataOperationListener paramDataInsertListener){
		 
		 if (entity != null){
	    	
	    	AbTaskItem item = new AbTaskItem();
	    	item.setListener(new AbTaskObjectListener() {
	    	    
				@Override
                public Object getObject(){
                    // TODO Auto-generated method stub
				    //执行插入 
                    //(1)获取数据库 
                    dao.startWritableDatabase(false);
                    //(2)执行
                    long retValue = dao.update(entity);
                    //(3)关闭数据库
                    dao.closeDatabase();
                    return retValue;
                }

                @Override
                public void update(Object obj){
                    long ret = (Long)obj;
                    if(ret >= 0){
                        if (paramDataInsertListener != null){
                            paramDataInsertListener.onSuccess(ret);
                        }
                    }else{
                        if (paramDataInsertListener != null){
                            paramDataInsertListener.onFailure(errorCode101, errorMessage101);
                        }
                    }
                    
                }
               
			});
	    	mAbTask.execute(item);
	    	
	    }else{
	    	if (paramDataInsertListener != null){
	    		paramDataInsertListener.onFailure(errorCode100, errorMessage100);
		    }
	    }
	    
	  }
	
	/**
	 * 描述：修改数据.
	 *
	 * @param <T> the generic type
	 * @param entityList  实体类 设置了对象关系映射
	 * @param dao     实现AbDBDaoImpl的Dao
	 * @param paramDataInsertListener 返回监听器
	 */
	public <T> void updateData(final List<T> entityList,final AbDBDaoImpl<T> dao, final AbDataOperationListener paramDataInsertListener){
		 
		 if (entityList != null){
	    	
	    	AbTaskItem item = new AbTaskItem();
	    	item.setListener(new AbTaskObjectListener() {
				
				@Override
				public void update(Object obj) {
				    long ret = (Long)obj;
					if(ret >= 0){
						if (paramDataInsertListener != null){
				    		paramDataInsertListener.onSuccess(ret);
					    }
					}else{
						if (paramDataInsertListener != null){
			    		    paramDataInsertListener.onFailure(errorCode101, errorMessage101);
				        }
					}
				}
				
				@Override
				public Object getObject() {
					//执行插入 
					//(1)获取数据库 
					dao.startWritableDatabase(false);
				  	//(2)执行
					long retValue = dao.updateList(entityList);
				    //(3)关闭数据库
				  	dao.closeDatabase();
				  	return retValue;
			    	
				}
			});
	    	mAbTask.execute(item);
	    	
	    }else{
	    	if (paramDataInsertListener != null){
	    		paramDataInsertListener.onFailure(errorCode100, errorMessage100);
		    }
	    }
	    
	  }
	
	/**
	 * 描述：修改数据.
	 *
	 * @param <T> the generic type
	 * @param storageQuery  条件实体
	 * @param dao     实现AbDBDaoImpl的Dao
	 * @param paramDataInsertListener 返回监听器
	 */
	public <T> void deleteData(final AbStorageQuery storageQuery,final AbDBDaoImpl<T> dao, final AbDataOperationListener paramDataInsertListener){
		 
	    	
	    	AbTaskItem item = new AbTaskItem();
	    	item.setListener(new AbTaskObjectListener() {
				
				@Override
				public void update(Object obj) {
				    long ret = (Long)obj;
					if(ret >= 0){
						if (paramDataInsertListener != null){
				    		paramDataInsertListener.onSuccess(ret);
					    }
					}else{
						if (paramDataInsertListener != null){
			    		    paramDataInsertListener.onFailure(errorCode101, errorMessage101);
				        }
					}
				}
				
				@Override
				public Object getObject() {
					//执行插入 
					//(1)获取数据库 
					dao.startWritableDatabase(false);
				  	//(2)执行
					long retValue = dao.delete(storageQuery.getWhereClause(),storageQuery.getWhereArgs());
				    //(3)关闭数据库
				  	dao.closeDatabase();
				  	return retValue;
			    	
				}
			});
	    	mAbTask.execute(item);
	}
	
	/**
	 * 描述：释放存储实例.
	 */
	public void release(){
		if(mAbTask!=null){
			mAbTask.cancel(true);
			mAbTask = null;
		}
	}
	
}
