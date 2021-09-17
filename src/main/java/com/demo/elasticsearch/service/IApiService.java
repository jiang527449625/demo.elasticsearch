package com.demo.elasticsearch.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.elasticsearch.config.Page;

import java.io.IOException;
import java.util.List;

/**
 * @Author jky
 * @Time 2019/7/25 17:24
 * @Description
 */
public interface IApiService {

    /**
     * 向elasticsearch放入实体数据
     *
     * @param index  索引
     * @param type   类型
     * @param entities 实体数据
     * @return
     * @throws IOException
     */
    public boolean add(String index, String type, JSONArray entities) throws IOException;

    /**
     * 根据条件从elasticsearch中取出实体数据列表
     *
     * @param index  索引
     * @param type   类型
     * @param search 搜索信息
     * @return
     */
    public Page<JSONObject> getEntity(String index, String type, JSONObject search);

    /**
     * 根据条件从elasticsearch中取出实体数据列表(无分页,滚动查询)
     * @param index
     * @param type
     * @param entity
     * @return
     */
	public List<JSONObject> getEntityToList(String index, String type, JSONObject entity);

    /**
     * 删除index
     * @param index
     */
    public Boolean deleteIndex(String index);


    /**
     * 删除数据
     * @param indexName
     * @param typeName
     * @param id
     * @return
     */
    public boolean delete(String indexName, String typeName, String id);
    /**
     * 根据条件获取点击量数据
     * @param index
     * @param type
     * @param entity
     * @return
     */
	public Page<JSONObject> getReadCountEntity(String index, String type, JSONObject entity);
    /**
     * 根据条件获取城市列表
     * @param index
     * @param type
     * @param entity
     * @return
     */
	public Page<JSONObject> getCityListPageEntity(String index, String type, JSONObject entity);
	/**
	 * 编辑
	 * @param index
	 * @param type
	 * @param entities
	 * @return
	 */
	boolean update(String index, String type, JSONObject entities);
    /**
    * 根据条件查询消息发送日志
	*
	* @param index
	* @param type
	* @return
	*/
	public Page<JSONObject> getSmsSendlogEntity(String index, String type, JSONObject entity);
}
