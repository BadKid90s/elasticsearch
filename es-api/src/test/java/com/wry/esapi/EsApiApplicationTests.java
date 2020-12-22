package com.wry.esapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wry.esapi.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@SpringBootTest
class EsApiApplicationTests {

    private final String INDEX_NAME = "spring_boot_index";

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     *
     * @throws IOException
     */
    @Test
    void testCreateIndexRequest() throws IOException {
        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
        //执行请求并获取响应结果
        IndicesClient indices = restHighLevelClient.indices();
        CreateIndexResponse response = indices.create(request, RequestOptions.DEFAULT);
        System.out.println(response.index());
    }

    /**
     * 创建知道字段类型的索引
     * 指定 message 字段的类型为 text
     *
     * @throws IOException
     */
    @Test
    void testCreateIndexRequest2() throws IOException {
        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
        Map<String, Object> message = new HashMap<>();
        message.put("type", "text");
        Map<String, Object> properties = new HashMap<>();
        properties.put("message", message);
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
        request.mapping(mapping);
        //执行请求并获取响应结果
        IndicesClient indices = restHighLevelClient.indices();
        CreateIndexResponse response = indices.create(request, RequestOptions.DEFAULT);
        System.out.println(response.index());
    }

    /**
     * 判断索引是否存在
     *
     * @throws IOException
     */
    @Test
    void testExistsIndexRequest() throws IOException {
        //创建索引请求
        GetIndexRequest request = new GetIndexRequest(INDEX_NAME);
        //执行请求并获取响应结果
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(exists);
    }

    /**
     * 删除索引
     *
     * @throws IOException
     */
    @Test
    void testDeleteIndexRequest() throws IOException {
        //创建索引请求
        DeleteIndexRequest request = new DeleteIndexRequest(INDEX_NAME);
        //执行请求并获取响应结果
        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);

        System.out.println(response.isAcknowledged());
    }


    /**
     * 创建文档
     *
     * @throws IOException
     */
    @Test
    void testAddDocumentRequest() throws IOException {
        //创建对象
        User user = new User("张三", 24);
        //创建文档请求
        IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
        // PUT /spring_boot_index/_doc/1
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));

        //将对象转换为JSON
        ObjectMapper mapper = new ObjectMapper();
        indexRequest.source(mapper.writeValueAsString(user), XContentType.JSON);
        //执行请求并获取响应结果
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(response.toString());

    }

    /**
     * 判断文档是否存在
     *
     * @throws IOException
     */
    @Test
    void testExistsDocumentRequest() throws IOException {
        //创建文档请求
        //GET /spring_boot_index/_doc/1
        GetRequest getRequest = new GetRequest(INDEX_NAME, "1");
        //不获取返回的_source 的上下文
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        //执行请求并获取响应结果
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(response.toString());

    }

    /**
     * 查询文档内容
     *
     * @throws IOException
     */
    @Test
    void testGetDocumentRequest() throws IOException {
        //创建文档请求
        //GET /spring_boot_index/_doc/1
        GetRequest getRequest = new GetRequest(INDEX_NAME, "1");
        //执行请求并获取响应结果
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        System.out.println(response.toString());

    }

    /**
     * 更新文档内容
     *
     * @throws IOException
     */
    @Test
    void testUpdateDocumentRequest() throws IOException {
        //创建文档请求
        //POST /wry/_doc/1/_update
        UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, "1");
        updateRequest.timeout(TimeValue.timeValueSeconds(1));
        //创建对象
        User user = new User("张三", 124);
        //将对象转换为JSON
        ObjectMapper mapper = new ObjectMapper();
        updateRequest.doc(mapper.writeValueAsString(user), XContentType.JSON);
        //执行请求并获取响应结果
        UpdateResponse response = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(response.status());
        System.out.println(response.toString());

    }

    /**
     * 删除文档内容
     *
     * @throws IOException
     */
    @Test
    void testDeleteDocumentRequest() throws IOException {
        //创建文档请求
        DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, "1");
        deleteRequest.timeout(TimeValue.timeValueSeconds(1));
        //执行请求并获取响应结果
        DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(response.status());
        System.out.println(response.toString());

    }

    /**
     * 批量操作文档内容
     *
     * @throws IOException
     */
    @Test
    void testBulkDocumentRequest() throws IOException {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new User("z" + i, i + 10));
        }
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueSeconds(10));

        //将对象转换为JSON
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < list.size(); i++) {
            IndexRequest indexRequest = new IndexRequest(INDEX_NAME)
                    .id(String.valueOf(i+1))
                    .source(mapper.writeValueAsString(list.get(i)),XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        //执行请求并获取响应结果
        BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        //是否失败 false 成功， true 失败
        System.out.println(response.hasFailures());
    }

    /**
     * 查询文档内容
     *
     * @throws IOException
     */
    @Test
    void testSearchDocumentRequest() throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询条件，通过 QueryBuilders 工具类实现
        //termQuery 精确匹配
        //matchAllQuery 匹配所有
        QueryBuilders.termQuery("name","z10");
        sourceBuilder.query();
        sourceBuilder.timeout(TimeValue.timeValueSeconds(10));

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit hitsHit : hitsHits) {
            System.out.println(hitsHit);
        }
    }

}
