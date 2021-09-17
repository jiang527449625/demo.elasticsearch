package com.demo.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.JsonArray;
import com.demo.elasticsearch.config.Config;
import com.demo.elasticsearch.config.Constants;
import com.demo.elasticsearch.config.Page;
import com.demo.elasticsearch.service.IApiService;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.*;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.params.Parameters;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author jky
 * @Time 2019/7/25 17:26
 * @Description
 */
@Service
@Slf4j
public class ApiService implements IApiService {
    @Autowired
    private JestClient jestClient;
    @Autowired
    private Config config;

    @Override
    public boolean add(String index, String type, JSONArray entities) {
        Bulk.Builder bulk = new Bulk.Builder();
        for (int i = 0; i < entities.size(); i++) {
            JSONObject jsonObject = entities.getJSONObject(i);
            String content = JSON.toJSONStringWithDateFormat(jsonObject, Constants.ES_DATE_FORMAT, SerializerFeature.WriteDateUseDateFormat);
            Index command = new Index.Builder(content).index(index).type(type).build();
            bulk.addAction(command);
        }
        BulkResult documentResult = null;
        try {
            documentResult = jestClient.execute(bulk.build());
            log.info("ES 插入数据完成：{}", JSONObject.toJSONString(entities));
            if (!documentResult.isSucceeded()) {
                log.error("ES 插入数据异常{}:{}", documentResult.getErrorMessage(), entities);
            }
        } catch (IOException e) {
            log.error("ES 插入数据异常{}：{}", e.getMessage(), entities);
        }
        return null != documentResult && documentResult.isSucceeded();
    }
	/**
	 * 编辑
	 * @param index
	 * @param type
	 * @param jsonObject
	 * @return
	 */
    @Override
    public boolean update(String index, String type, JSONObject jsonObject) {
    	BulkResult documentResult = null;
    	try {
    		// 先删除原本数据
			DocumentResult dr = jestClient.execute(new Delete.Builder(jsonObject.getString("es_metadata_id")).index(index).type(type).build());
			if(dr.isSucceeded()) {
				Bulk.Builder bulk = new Bulk.Builder();
		        String content = JSON.toJSONStringWithDateFormat(jsonObject, Constants.ES_DATE_FORMAT, SerializerFeature.WriteDateUseDateFormat);
		        Index command = new Index.Builder(content).index(index).type(type).build();
		        bulk.addAction(command);
		        documentResult  = jestClient.execute(bulk.build());
		        log.info("ES 插入数据完成：{}", JSONObject.toJSONString(jsonObject));
	            if (!documentResult.isSucceeded()) {
	                log.error("ES 插入数据异常{}:{}", documentResult.getErrorMessage(), jsonObject);
	            }
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        return null != documentResult && documentResult.isSucceeded();
    }
    @Override
    public Page<JSONObject> getEntity(String index, String type, JSONObject entity) {
        int maxPageSize = config.getMaxPageSize();
//        int pageSize = entity.getInteger("pageSize") == null ?
//                maxPageSize : entity.getInteger("pageSize") > maxPageSize ?
//                maxPageSize : entity.getInteger("pageSize");
        int pageSize = entity.getInteger("pageSize") == null ? maxPageSize : entity.getInteger("pageSize");

        int pageNum = entity.getInteger("pageNum") == null ?
                config.getDefaultPageNum() : entity.getInteger("pageNum");
        Page<JSONObject> page = new Page<JSONObject>();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        //搜索构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询构建器
        BoolQueryBuilder filterQueryBuilders = QueryBuilders.boolQuery();
        if(!StringUtils.isEmpty(entity.getString("taskUuid"))){
        	filterQueryBuilders.must(QueryBuilders.matchQuery("taskUuid", entity.getString("taskUuid")));
        }
        if(!StringUtils.isEmpty(entity.getString("respTimeStamp"))){
        	filterQueryBuilders.must(QueryBuilders.rangeQuery("respTimeStamp").gte(entity.getLong("respTimeStamp")));
        }
        //设置参数
        searchSourceBuilder.postFilter(filterQueryBuilders)
                .from(page.getFromNum())
                .size(page.getPageSize());
        //排序
        Search search = null;
        if(!StringUtils.isEmpty(entity.getString("respTimeStamp"))){
        	Sort sortTime = new Sort("reqTimeStamp", Sort.Sorting.ASC);
        	//搜索信息
            search = new Search.Builder(searchSourceBuilder.toString())
                    .addIndex(index).addType(type).addSort(sortTime).build();
        }else{
        	if(!StringUtils.isEmpty(entity.getString("sortName")) && !StringUtils.isEmpty(entity.getString("sortType"))){
        		Sort sortTime = new Sort(entity.getString("sortName"), (entity.getString("sortType").equals("asc")?Sort.Sorting.ASC:Sort.Sorting.DESC));
            	search = new Search.Builder(searchSourceBuilder.toString())
                        .addIndex(index).addType(type).addSort(sortTime).build();
        	}
        }
        try {
            JestResult result = jestClient.execute(search);
            if (result.isSucceeded()) {
                int hitCount = result.getJsonObject().get("hits").getAsJsonObject().get("total").getAsInt();
                page.setTotalNum(hitCount);
                page.setEntities(result.getSourceAsObjectList(JSONObject.class));
                page.calculate();
                log.info("result:{}.", result.getJsonString());
            } else {
                log.error("ES 插入数据异常{}:{}", result.getErrorMessage(), search);
            }
        } catch (Exception e) {
            log.error("ES 插入数据异常{}：{}", e.getMessage(), search);
            log.error("ES 插入数据异常{}：{}", e);
            e.printStackTrace();
        }
        return page.getEntities() == null || page.getEntities().size() == 0 ? null : page;
    }

    @Override
    public List<JSONObject> getEntityToList(String index, String type, JSONObject entity) {
        List<JSONObject> esList = new ArrayList<JSONObject>();
        //搜索构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询构建器
        BoolQueryBuilder filterQueryBuilders = QueryBuilders.boolQuery();
        for (String key : entity.keySet()) {
            filterQueryBuilders.must(QueryBuilders.termQuery(key, entity.getString(key)));
        }
        //设置参数
//        searchSourceBuilder.postFilter(filterQueryBuilders);
        searchSourceBuilder.query(filterQueryBuilders).size(10000);
        //排序
        Sort sortTime = new Sort("reqTimeStamp", Sort.Sorting.ASC);
        if ("ads_statistics".equals(index)){
            sortTime = new Sort("uploadTime", Sort.Sorting.ASC);
        }else if ("app".equals(index)){
            sortTime = new Sort("readTime", Sort.Sorting.ASC);
        }
//        搜索信息（没有滚动查询的搜索信息只能查1万条）
//        Search search = new Search.Builder(searchSourceBuilder.toString())
//                .addIndex(index).addType(type).addSort(sortTime).build();
//        搜索信息（附带滚动查询）
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(index)
                .addType(type)
                .setParameter(Parameters.SCROLL, "5m")
                .addSort(sortTime)
                .build();
        try {
            log.info("search---------------------:{}"+JSON.toJSONString(search));
            JestResult result = jestClient.execute(search);
            if (result.isSucceeded()) {
                JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
                esList.addAll(result.getSourceAsObjectList(JSONObject.class));
                String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
                if (hits.size() > 0) {
                    do {
                        SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m").build();
                        result = jestClient.execute(scroll);
                        hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
                        esList.addAll(result.getSourceAsObjectList(JSONObject.class));
                        log.info("document size {}", hits.size());
                        scrollId = result.getJsonObject().getAsJsonPrimitive("_scroll_id").getAsString();
                        if (hits.size() <= 0) {
                            break;
                        }
                    } while (true);
                }
                ClearScroll clearScroll = new ClearScroll.Builder().addScrollId(scrollId).build();
                jestClient.execute(clearScroll);
                log.info("result:{}.", result.getJsonString());
            } else {
                log.error("ES 查询数据异常{}:{}", result.getErrorMessage(), search);
            }
        } catch (Exception e) {
            log.error("ES 查询数据异常{}：{}", e.getMessage(), search);
            log.error("ES 查询数据异常{}：{}", e);
            e.printStackTrace();
        }
        return esList;
    }

    //删除index
    @Override
    public Boolean deleteIndex(String index) {
        try {
            JestResult jestResult = jestClient.execute(new DeleteIndex.Builder(index).build());
            System.out.println("deleteIndex result:{}" + jestResult.isSucceeded());
            return jestResult.isSucceeded();
        } catch (IOException e) {
            log.error("ES 删除index异常{}：{}", e.getMessage(), e);
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 删除数据
     *
     * @param indexName
     * @param typeName
     * @param id
     * @return
     */
    @Override
    public boolean delete(String indexName, String typeName, String id) {
        try {
            DocumentResult dr = jestClient.execute(new Delete.Builder(id).index(indexName).type(typeName).build());
            return dr.isSucceeded();
        } catch (IOException e) {
            log.error("ES 删除数据异常{}：{}", e.getMessage(), e);
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 根据条件获取点击量数据
     * @param index
     * @param type
     * @param entity
     * @return
     */
	@Override
	public Page<JSONObject> getReadCountEntity(String index, String type, JSONObject entity) {
		int maxPageSize = config.getMaxPageSize();
//      int pageSize = entity.getInteger("pageSize") == null ?
//              maxPageSize : entity.getInteger("pageSize") > maxPageSize ?
//              maxPageSize : entity.getInteger("pageSize");
      int pageSize = entity.getInteger("pageSize") == null ? maxPageSize : entity.getInteger("pageSize");

      int pageNum = entity.getInteger("pageNum") == null?config.getDefaultPageNum() : entity.getInteger("pageNum");
      Page<JSONObject> page = new Page<JSONObject>();
      page.setPageNum(pageNum);
      page.setPageSize(pageSize);
      //搜索构建器
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
      //查询构建器
      BoolQueryBuilder filterQueryBuilders = QueryBuilders.boolQuery();
      if ("app".equals(index)){
          if(!StringUtils.isEmpty(entity.getString("resourceUuid"))){
              filterQueryBuilders.must(QueryBuilders.matchQuery("resourceUuid", entity.getString("resourceUuid")));
          }
          if(!StringUtils.isEmpty(entity.getString("targetUuid"))){
              filterQueryBuilders.must(QueryBuilders.matchQuery("targetUuid", entity.getString("targetUuid")));
          }
          if(!StringUtils.isEmpty(entity.getString("readType"))){
              filterQueryBuilders.must(QueryBuilders.matchQuery("readType", entity.getString("readType")));
          }
          if(!StringUtils.isEmpty(entity.getString("detailType"))){
              filterQueryBuilders.must(QueryBuilders.matchQuery("detailType", entity.getString("detailType")));
          }
          if(entity.getLong("startTime") != null){
              filterQueryBuilders.must(QueryBuilders.rangeQuery("readTime").gte(entity.getLong("startTime")));
          }
          if(entity.getLong("endTime") != null){
              filterQueryBuilders.must(QueryBuilders.rangeQuery("readTime").lte(entity.getLong("endTime")));
          }
          if (entity.getLong("startTimeLong") != null){
              filterQueryBuilders.must(QueryBuilders.rangeQuery("uploadTime").gte(entity.getLong("startTimeLong")));
          }
          if (entity.getLong("endTimeLong") != null){
              filterQueryBuilders.must(QueryBuilders.rangeQuery("uploadTime").lte(entity.getLong("endTimeLong")));
          }
      }else if ("ads_statistics".equals(index)){
          if(!StringUtils.isEmpty(entity.getString("messageId"))){
              filterQueryBuilders.must(QueryBuilders.matchQuery("messageId", entity.getString("messageId")));
          }
          if(entity.getLong("startTime") != null){
              filterQueryBuilders.must(QueryBuilders.rangeQuery("uploadTime").gte(entity.getLong("startTime")));
          }
          if(entity.getLong("endTime") != null){
              filterQueryBuilders.must(QueryBuilders.rangeQuery("uploadTime").lte(entity.getLong("endTime")));
          }
      }
      //设置参数
      searchSourceBuilder.postFilter(filterQueryBuilders)
              .from(page.getFromNum())
              .size(page.getPageSize());
      //排序
      Search search = null;
	  if(!StringUtils.isEmpty(entity.getString("sortName")) && !StringUtils.isEmpty(entity.getString("sortType"))){
          Sort sortTime = new Sort(entity.getString("sortName"), (entity.getString("sortType").equals("asc")?Sort.Sorting.ASC:Sort.Sorting.DESC));
	      search = new Search.Builder(searchSourceBuilder.toString())
	                 .addIndex(index).addType(type).addSort(sortTime).build();
	  }else{
          search = new Search.Builder(searchSourceBuilder.toString())
                  .addIndex(index).addType(type).build();
      }
      try {
          log.info("search---------------------:{}"+JSON.toJSONString(search));
          JestResult result = jestClient.execute(search);
          if (result.isSucceeded()) {
              int hitCount = result.getJsonObject().get("hits").getAsJsonObject().get("total").getAsInt();
              page.setTotalNum(hitCount);
              page.setEntities(result.getSourceAsObjectList(JSONObject.class));
              page.calculate();
              log.info("result:{}.", result.getJsonString());
          } else {
        	  page.setTotalNum(0);
              page.calculate();
              log.error("ES 插入数据异常{}:{}", result.getErrorMessage(), search);
          }
      } catch (Exception e) {
          log.error("ES 插入数据异常{}：{}", e.getMessage(), search);
          log.error("ES 插入数据异常{}：{}", e);
          e.printStackTrace();
      }
      return  page;
	}
    /**
     * 根据条件获取城市列表
     * @param index
     * @param type
     * @param entity
     * @return
     */
	@Override
	public Page<JSONObject> getCityListPageEntity(String index, String type, JSONObject entity) {
		int maxPageSize = config.getMaxPageSize();
//      int pageSize = entity.getInteger("pageSize") == null ?
//              maxPageSize : entity.getInteger("pageSize") > maxPageSize ?
//              maxPageSize : entity.getInteger("pageSize");
      int pageSize = entity.getInteger("pageSize") == null ? maxPageSize : entity.getInteger("pageSize");

      int pageNum = entity.getInteger("pageNum") == null ?
              config.getDefaultPageNum() : entity.getInteger("pageNum");
      Page<JSONObject> page = new Page<JSONObject>();
      page.setPageNum(pageNum);
      page.setPageSize(pageSize);
      //搜索构建器
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
      //查询构建器
      BoolQueryBuilder filterQueryBuilders = QueryBuilders.boolQuery();
      if(!StringUtils.isEmpty(entity.getString("cityCode"))){
      	filterQueryBuilders.must(QueryBuilders.wildcardQuery("cityCode", "*"+entity.getString("cityCode")+"*"));
      }
      //设置参数
      searchSourceBuilder.postFilter(filterQueryBuilders)
              .from(page.getFromNum())
              .size(page.getPageSize());
      //排序
      Search search = null;
	  if(!StringUtils.isEmpty(entity.getString("sortName")) && !StringUtils.isEmpty(entity.getString("sortType"))){
	  	Sort sortTime = new Sort(entity.getString("sortName"), (entity.getString("sortType").equals("asc")?Sort.Sorting.ASC:Sort.Sorting.DESC));
	      search = new Search.Builder(searchSourceBuilder.toString())
	                 .addIndex(index).addType(type).addSort(sortTime).build();
	  }
      try {
          JestResult result = jestClient.execute(search);
          if (result.isSucceeded()) {
              int hitCount = result.getJsonObject().get("hits").getAsJsonObject().get("total").getAsInt();
              page.setTotalNum(hitCount);
              page.setEntities(result.getSourceAsObjectList(JSONObject.class));
              page.calculate();
              log.info("result:{}.", result.getJsonString());
          } else {
        	  page.setTotalNum(0);
              page.calculate();
              log.error("ES 插入数据异常{}:{}", result.getErrorMessage(), search);
          }
      } catch (IOException e) {
          log.error("ES 插入数据异常{}：{}", e.getMessage(), search);
          e.printStackTrace();
      }
      return  page;
	}
    /**
    * 根据条件查询消息发送日志
	*
	* @param index
	* @param type
	* @return
	*/
	@Override
	public Page<JSONObject> getSmsSendlogEntity(String index, String type, JSONObject entity) {
		int maxPageSize = config.getMaxPageSize();
//      int pageSize = entity.getInteger("pageSize") == null ?
//              maxPageSize : entity.getInteger("pageSize") > maxPageSize ?
//              maxPageSize : entity.getInteger("pageSize");
      int pageSize = entity.getInteger("pageSize") == null ? maxPageSize : entity.getInteger("pageSize");

      int pageNum = entity.getInteger("pageNum") == null?config.getDefaultPageNum() : entity.getInteger("pageNum");
      Page<JSONObject> page = new Page<JSONObject>();
      page.setPageNum(pageNum);
      page.setPageSize(pageSize);
      //搜索构建器
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
      //查询构建器
      BoolQueryBuilder filterQueryBuilders = QueryBuilders.boolQuery();
      if(!StringUtils.isEmpty(entity.getString("cityCode"))){
    	filterQueryBuilders.must(QueryBuilders.wildcardQuery("cityCode", "*"+entity.getString("cityCode")+"*"));
      }
      if(!StringUtils.isEmpty(entity.getString("startTime"))){
          filterQueryBuilders.must(QueryBuilders.rangeQuery("sendTime").gte(entity.getLong("startTime")));
      }
      if(!StringUtils.isEmpty(entity.getString("endTime"))){
          filterQueryBuilders.must(QueryBuilders.rangeQuery("sendTime").lte(entity.getLong("endTime")));
      }
      //设置参数
      searchSourceBuilder.postFilter(filterQueryBuilders)
              .from(page.getFromNum())
              .size(page.getPageSize());
      //排序
      Search search = null;
	  if(!StringUtils.isEmpty(entity.getString("sortName")) && !StringUtils.isEmpty(entity.getString("sortType"))){
          Sort sortTime = new Sort(entity.getString("sortName"), (entity.getString("sortType").equals("asc")?Sort.Sorting.ASC:Sort.Sorting.DESC));
	      search = new Search.Builder(searchSourceBuilder.toString())
	                 .addIndex(index).addType(type).addSort(sortTime).build();
	  }
      try {
          JestResult result = jestClient.execute(search);
          if (result.isSucceeded()) {
              int hitCount = result.getJsonObject().get("hits").getAsJsonObject().get("total").getAsInt();
              page.setTotalNum(hitCount);
              page.setEntities(result.getSourceAsObjectList(JSONObject.class));
              page.calculate();
              log.info("result:{}.", result.getJsonString());
          } else {
        	  page.setTotalNum(0);
              page.calculate();
              log.error("ES 插入数据异常{}:{}", result.getErrorMessage(), search);
          }
      } catch (IOException e) {
          log.info("ES 插入数据异常{}：{}", e.getMessage(), search);
          e.printStackTrace();
      }
      return  page;
	}
}
