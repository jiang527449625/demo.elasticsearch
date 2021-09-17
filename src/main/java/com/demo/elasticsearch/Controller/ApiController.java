package com.demo.elasticsearch.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.elasticsearch.Model.CictecLogUtil;
import com.demo.elasticsearch.Model.Result;
import com.demo.elasticsearch.Model.ResultGenerator;
import com.demo.elasticsearch.config.Page;
import com.demo.elasticsearch.service.IApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @Author jky
 * @Time 2019/7/25 16:31
 * @Description
 */
@RestController
@RequestMapping("/elasticsearch/auth")
@Slf4j
public class ApiController {


    @Autowired
    private IApiService apiService;


    @RequestMapping(value = "/add/{index}/index/{type}/type", method = {RequestMethod.POST})
    @ResponseBody
    public Result addEntities(@PathVariable("index") String index, @PathVariable("type") String type, @RequestBody JSONArray entity) throws IOException {
        log.info(CictecLogUtil.logResult("esService","添加json对象调用,参数：index="+index+",type="+type+",entity="+entity.toString()));
        boolean add = apiService.add(index, type, entity);
        Result moblieResult=add ? ResultGenerator.genSuccessResult("成功！", null) : ResultGenerator.genFailResult("添加失败。");
        log.info(CictecLogUtil.logResult("esService","添加json对象调用返回结果："+JSON.toJSONString(moblieResult)));
        return moblieResult;
    }

    @RequestMapping(value = "/search/{index}/index/{type}/type", method = {RequestMethod.POST})
    @ResponseBody
    public Result queryStationUserInfo(@PathVariable("index") String index, @PathVariable("type") String type, @RequestBody JSONObject entity) {
        log.info(CictecLogUtil.logResult("esService","根据条件所有对象调用,参数：index="+index+",type="+type+",entity="+entity.toString()));
        Page<JSONObject> result = apiService.getEntity(index, type, entity);
        Result moblieResult= result == null ? ResultGenerator.genFailResult() : ResultGenerator.genSuccessResult(result);
        log.info(CictecLogUtil.logResult("esService","根据条件所有对象调用返回结果："+JSON.toJSONString(moblieResult)));
        return moblieResult;
    }

    /**
     * 滚动查询 所有条件下的集合
     *
     * @param index
     * @param type
     * @param entity
     * @return
     */
    @RequestMapping(value = "/search/{index}/index/{type}/type/list", method = {RequestMethod.POST})
    @ResponseBody
    public Result queryStationUserInfoList(@PathVariable("index") String index, @PathVariable("type") String type, @RequestBody JSONObject entity) {
        log.info(CictecLogUtil.logResult("esService","根据条件所有对象调用,参数：index="+index+",type="+type+",entity="+entity.toString()));
        List<JSONObject> result = apiService.getEntityToList(index, type, entity);
        Result moblieResult= result == null ? ResultGenerator.genFailResult() : ResultGenerator.genSuccessResult(result);
        log.info(CictecLogUtil.logResult("esService","根据条件所有对象调用返回结果："+JSON.toJSONString(moblieResult)));
        return moblieResult;
    }

    /**
     * 删除index
     *
     * @param index
     * @return
     */
    @RequestMapping(value = "/deleteIndex/{index}", method = {RequestMethod.POST})
    @ResponseBody
    public Result deleteIndex(@PathVariable("index") String index) {
        log.info(CictecLogUtil.logResult("esService","删除index调用,参数：index="+index));
        Result moblieResult=ResultGenerator.genFailResult("删除" + index + "失败！");
        boolean result = apiService.deleteIndex(index);
        if(result) moblieResult =ResultGenerator.genSuccessResult("删除" + index + "成功！");
        log.info(CictecLogUtil.logResult("esService","删除index调用返回结果："+JSON.toJSONString(moblieResult)));
        return moblieResult;
    }

    /**
     * 根据条件删除数据
     *
     * @param index
     * @return
     */
    @RequestMapping(value = "/delete/{index}/index/{type}/type/{id}/id", method = {RequestMethod.POST})
    @ResponseBody
    public Result delete(@PathVariable("index") String index, @PathVariable("type") String type,@PathVariable("id") String id) {
        log.info(CictecLogUtil.logResult("esService","根据条件删除数据调用,参数：index="+index+",type="+type+",id="+id));
        Result moblieResult=ResultGenerator.genFailResult("删除" + index + "失败！");
        boolean result = apiService.delete(index,type,id);
        if(result) moblieResult =ResultGenerator.genSuccessResult("删除" + index + "成功！");
        log.info(CictecLogUtil.logResult("esService","根据条件删除数据调用返回结果："+JSON.toJSONString(moblieResult)));
        return moblieResult;
    }
   
    /**
             * 根据条件查询点击量
     *
     * @param index
     * @return
     */
    @RequestMapping(value = "/search/{index}/index/{type}/type/readCountPage", method = {RequestMethod.POST})
    @ResponseBody
    public Result queryReadCount(
    		@PathVariable("index") String index, 
    		@PathVariable("type") String type, 
    		@RequestBody JSONObject entity) {
        log.info(CictecLogUtil.logResult("esService","根据条件所有对象调用,参数：index="+index+",type="+type+",entity="+entity.toString()));
        Page<JSONObject> result = apiService.getReadCountEntity(index, type, entity);
        Result moblieResult= result == null ? ResultGenerator.genFailResult() : ResultGenerator.genSuccessResult(result);
        log.info(CictecLogUtil.logResult("esService","根据条件所有对象调用返回结果："+JSON.toJSONString(moblieResult)));
        return moblieResult;
    }
    /**
 	 * 编辑
	 * @param index
	 * @param type
	 * @param entity
	 * @return
	 */
	@RequestMapping(value = "/update/{index}/index/{type}/type", method = {RequestMethod.POST})
	@ResponseBody
	public Result updateEntities(
			@PathVariable("index") String index, 
			@PathVariable("type") String type, 
			@RequestBody JSONObject  entity) throws IOException {
	    log.info(CictecLogUtil.logResult("esService","添加json对象调用,参数：index="+index+",type="+type+",entity="+entity.toString()));
	    boolean add = apiService.update(index, type, entity);
	    Result moblieResult=add ? ResultGenerator.genSuccessResult("成功！", null) : ResultGenerator.genFailResult("编辑失败。");
	    log.info(CictecLogUtil.logResult("esService","添加json对象调用返回结果："+JSON.toJSONString(moblieResult)));
	    return moblieResult;
	}
	/**
     * 根据条件查询城市列表
     *
     * @param index
     * @return
     */
	@RequestMapping(value = "/search/{index}/index/{type}/type/sms/cityListPage", method = {RequestMethod.POST})
	@ResponseBody
	public Result querySmsCityListPage(
			@PathVariable("index") String index, 
			@PathVariable("type") String type, 
			@RequestBody JSONObject entity) {
		log.info(CictecLogUtil.logResult("esService","根据条件所有对象调用,参数：index="+index+",type="+type+",entity="+entity.toString()));
		Page<JSONObject> result = apiService.getCityListPageEntity(index, type, entity);
		Result moblieResult= result == null ? ResultGenerator.genFailResult() : ResultGenerator.genSuccessResult(result);
		log.info(CictecLogUtil.logResult("esService","根据条件所有对象调用返回结果："+JSON.toJSONString(moblieResult)));
		return moblieResult;
	}
    /**
    * 根据条件查询消息发送日志
	*
	* @param index
	* @param type
	* @return
	*/
	@RequestMapping(value = "/search/{index}/index/{type}/type/sms/sendlogPage", method = {RequestMethod.POST})
	@ResponseBody
	public Result querySmsSendlogPage(
			@PathVariable("index") String index,
			@PathVariable("type") String type, 
			@RequestBody JSONObject entity) {
		log.info(CictecLogUtil.logResult("esService","根据条件分页查询消息发送日志,参数：index="+index+",type="+type+",entity="+entity.toString()));
		Page<JSONObject> result = apiService.getSmsSendlogEntity(index, type, entity);
		Result moblieResult= result == null ? ResultGenerator.genFailResult() : ResultGenerator.genSuccessResult(result);
		log.info(CictecLogUtil.logResult("esService","根据条件分页查询消息发送日志返回结果："+JSON.toJSONString(moblieResult)));
		return moblieResult;
	}
}
