package com.wry.esjd.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wry.esjd.constant.ConstantJD;
import com.wry.esjd.pojo.ContentJD;
import com.wry.esjd.util.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import javax.swing.text.Highlighter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务层
 * </p>
 *
 * @author wangruiyu
 * @since 2020/8/3
 */
@Service
public class ContentJDService {
    @Autowired
    private RestHighLevelClient client;

    /**
     * 解析数据，保存到es中
     *
     * @param keyword 关键字
     * @return
     * @throws IOException
     */
    public boolean parseContentJD(String keyword) throws IOException {
        List<ContentJD> contentJDS = HtmlParseUtil.parseJD(keyword);
        //把查询的数据存放到es 中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueSeconds(5));

        IndexRequest indexRequest = null;
        for (int i = 0; i < contentJDS.size(); i++) {
            indexRequest = new IndexRequest(ConstantJD.INDEX_NAME);
            indexRequest.source(new ObjectMapper().writeValueAsString(contentJDS.get(i)), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    /**
     * 分页查询
     *
     * @param keyword  关键字
     * @param pageNum  页码
     * @param pageSize 页数
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> searchPage(String keyword, Integer pageNum, Integer pageSize) throws IOException {
        if (pageNum < 0) {
            pageNum = 1;
        }
        SearchRequest searchRequest = new SearchRequest(ConstantJD.INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from((pageNum - 1) * pageSize);
        sourceBuilder.size(pageSize);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        sourceBuilder.query(termQueryBuilder);


        sourceBuilder.timeout(TimeValue.timeValueSeconds(5));
        searchRequest.source(sourceBuilder);
        //执行请求
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        //解析结果
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            //解析高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (title != null) {
                Text[] fragments = title.fragments();
                String n_title = "";
                for (Text text : fragments) {
                    n_title += text.string();
                }
                sourceAsMap.put("title", n_title);
            }
            list.add(sourceAsMap);
        }
        return list;
    }
}
