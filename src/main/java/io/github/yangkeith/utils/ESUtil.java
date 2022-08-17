package io.github.yangkeith.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetMappingRequest;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.get_mapping.IndexMappingRecord;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.jfinal.log.Log;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: KeithUtils
 * @package: io.github.yangkeith.utils
 * @description: 只支持Elasticsearch 7.15版本以上
 * @author: Keith
 * @date：Created in 2022-08-12 10:41
 */
public class ESUtil {
    
    private static final Log log = Log.getLog(ESUtil.class);
    
    private ElasticsearchClient client;
    
    private RestClient restClient;
    
    private final int elasticSearchDefaultCount = 10000;
    
    /**
     * escutils
     *
     * @param ip   ip
     * @param port port
     */
    public ESUtil(String ip, Integer port, String username, String password) {
        client = getClient(ip, port, username, password);
    }
    
    /**
     * 获取客户端
     *
     * @return {@link ElasticsearchClient  }
     * @author Keith
     * @date 2022/07/04
     */
    public ElasticsearchClient getClient() {
        return client;
    }
    
    /**
     * 获取客户端
     *
     * @param ip   IP
     * @param port 端口
     * @return
     * @Method getClient
     * @author Keith
     * @Date 2019年6月4日
     * @Copyright SuperMap
     * @version 1.0.0
     */
    public ElasticsearchClient getClient(String ip, Integer port, String username, String password) {
        RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(ip, port, "http"));
        if (StringUtil.isNotBlank(username)) {
            CredentialsProvider credentialsProvider =
                    new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));
            restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        restClient = restClientBuilder.build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
        return client;
    }
    
    /**
     * 创建映射
     *
     * @param mappingName 映射名称
     * @param properties  映射配置
     * @return boolean
     * @throws IOException ioexception
     */
    public boolean createMapping(String mappingName, Map<String, String> properties) throws IOException {
        boolean result;
        if (!this.isExistsIndex(mappingName)) {
            CreateIndexRequest.Builder requestBuilder = new CreateIndexRequest.Builder();
            requestBuilder.index(mappingName).mappings(createTypeMapping(properties));
            CreateIndexResponse createIndexResponse = client.indices().create(requestBuilder.build());
            result = createIndexResponse.acknowledged();
        } else {
            result = true;
        }
        return result;
    }
    
    /**
     * 获取映射
     *
     * @param indexName 索引名称
     * @return {@link Map }<{@link String }, {@link TypeMapping }>
     * @throws IOException ioexception
     * @author Keith
     * @date 2022-08-12
     */
    public Map<String, TypeMapping> getMapping(String indexName) throws IOException {
        Map<String, TypeMapping> result = new HashMap<>();
        if (isExistsIndex(indexName)) {
            GetMappingRequest getMappingRequest = GetMappingRequest.of(get -> get.index(indexName));
            GetMappingResponse getMappingResponse = client.indices().getMapping(getMappingRequest);
            Map<String, IndexMappingRecord> mappingRecordMap = getMappingResponse.result();
            for (String key : mappingRecordMap.keySet()) {
                result.put(key, mappingRecordMap.get(key).mappings());
            }
        }
        return result;
    }
    
    /**
     * 删除索引
     *
     * @param indexName 索引名称
     * @return boolean
     * @throws IOException ioexception
     * @author Keith
     * @date 2022-08-12
     */
    public boolean deleteIndex(String indexName) throws IOException {
        return client.indices().delete(deleteIndex -> deleteIndex.index(indexName)).acknowledged();
    }
    
    /**
     * 是存在索引
     *
     * @param indexName 索引名称
     * @return boolean
     * @throws IOException ioexception
     * @author Keith
     * @date 2022-08-12
     */
    public boolean isExistsIndex(String indexName) throws IOException {
        return client.indices().exists(exist -> exist.index(indexName)).value();
    }
    
    /**
     * 添加文档
     *
     * @param indexName 索引名称
     * @param document  文档
     * @return {@link String }
     * @throws Exception 异常
     * @author Keith
     * @date 2022-08-12
     */
    public String addDocument(String indexName, Object document) throws Exception {
        IndexRequest<Object> indexRequest = IndexRequest.of(index -> index.index(indexName).document(document));
        IndexResponse indexResponse = client.index(indexRequest);
        return indexResponse.result().jsonValue();
    }
    
    /**
     * 删除文档
     *
     * @param indexName 索引名称
     * @param id        id
     * @return {@link String }
     * @throws IOException ioexception
     * @author Keith
     * @date 2022-08-12
     */
    public String deleteDocument(String indexName, String id) throws IOException {
        DeleteRequest deleteRequest = DeleteRequest.of(delete -> delete.index(indexName).id(id));
        DeleteResponse deleteResponse = client.delete(deleteRequest);
        return deleteResponse.result().jsonValue();
    }
    
    /**
     * 更新文档
     *
     * @param indexName 索引名称
     * @param id        id
     * @param document  文档
     * @return {@link String }
     * @throws Exception 异常
     * @author Keith
     * @date 2022-08-12
     */
    public String updateDocument(String indexName, String id, Object document) throws Exception {
        UpdateRequest<Object, Object> updateRequest = UpdateRequest.of(update -> update.index(indexName).id(id).doc(document));
        UpdateResponse<Object> updateResponse = client.update(updateRequest, Object.class);
        return updateResponse.result().jsonValue();
    }
    
    /**
     * 查询文件
     *
     * @param indexName 索引名称
     * @param id        id
     * @param clazz     clazz
     * @return {@link T }
     * @throws IOException ioexception
     * @author Keith
     * @date 2022-08-12
     */
    public <T> T queryDocument(String indexName, String id, Class<T> clazz) throws IOException {
        GetRequest getRequest = GetRequest.of(get -> get.index(indexName).id(id));
        GetResponse<T> getResponse = client.get(getRequest, clazz);
        return getResponse.source();
    }
    
    /**
     * 搜索所有
     *
     * @param indexName 索引名称
     * @return {@link List }<{@link Map }>
     * @throws Exception 异常
     * @author Keith
     * @date 2022-08-15
     */
    public List<Map> searchAll(String indexName) throws Exception {
        return searchAll(indexName, 0, elasticSearchDefaultCount);
    }
    
    /**
     * 搜索所有
     *
     * @param indexName 索引名称
     * @param clazz     clazz
     * @return {@link List }<{@link T }>
     * @throws Exception 异常
     * @author Keith
     * @date 2022-08-15
     */
    public <T> List<T> searchAll(String indexName, Class<T> clazz) throws Exception {
        return searchAll(indexName, clazz, 0, elasticSearchDefaultCount);
    }
    
    /**
     * 搜索所有
     *
     * @param indexName 索引名称
     * @param from      从
     * @param size      大小
     * @return {@link List }<{@link Map }<{@link String }, {@link Object }>>
     * @throws IOException ioexception
     * @author Keith
     * @date 2022-08-12
     */
    public List<Map> searchAll(String indexName, int from, int size) throws Exception {
        return searchAll(indexName, Map.class, from, size);
    }
    
    /**
     * 搜索所有
     *
     * @param indexName 索引名称
     * @param clazz     clazz
     * @param from      从
     * @param size      大小
     * @return {@link List }<{@link T }>
     * @throws Exception 异常
     * @author Keith
     * @date 2022-08-12
     */
    public <T> List<T> searchAll(String indexName, Class<T> clazz, int from, int size) throws Exception {
        SearchRequest request = SearchRequest.of(search -> search.index(indexName).query(q -> q.matchAll(m -> m.queryName(null))).size(size).from(from));
        SearchResponse<T> response = client.search(request, clazz);
        List<Hit<T>> hitList = response.hits().hits();
        System.out.println(hitList.size());
        List<T> result = new ArrayList<>();
        for (Hit<T> hit : hitList) {
            result.add(hit.source());
        }
        return result;
    }
    
    public void close() throws IOException {
        restClient.close();
    }
    
    /**
     * 创建类型映射
     *
     * @param properties 属性
     * @return {@link TypeMapping }
     * @author Keith
     * @date 2022-08-12
     */
    private TypeMapping createTypeMapping(Map<String, String> properties) {
        TypeMapping.Builder typeMappingBuilder = new TypeMapping.Builder();
        Map<String, Property> mappingProperties = new HashMap<>();
        for (String key : properties.keySet()) {
            mappingProperties.put(key, getProperty(properties.get(key)));
        }
        typeMappingBuilder.properties(mappingProperties);
        return typeMappingBuilder.build();
    }
    
    
    /**
     * 获取财产
     *
     * @param key 关键
     * @return {@link Property }
     * @author Keith
     * @date 2022-08-12
     */
    private Property getProperty(String key) {
        if (StringUtil.isNotBlank(key)) {
            switch (key.trim().toUpperCase()) {
                case "GEOSHAPE":
                    return Property.of(property -> property.geoShape(GeoShapeProperty.of(geoShapeProperty -> geoShapeProperty)));
                case "INTEGER":
                    return Property.of(property -> property.integer(IntegerNumberProperty.of(integerNumberProperty -> integerNumberProperty.index(true))));
                case "BOOLEAN":
                    return Property.of(property -> property.boolean_(BooleanProperty.of(booleanProperty -> booleanProperty.index(true))));
                case "FLOAT":
                    return Property.of(property -> property.float_(FloatNumberProperty.of(floatProperty -> floatProperty.index(true))));
                case "DOUBLE":
                    return Property.of(property -> property.double_(DoubleNumberProperty.of(doubleNumberProperty -> doubleNumberProperty.index(true))));
                case "DATE":
                    return Property.of(property -> property.date(DateProperty.of(dateProperty -> dateProperty.index(true))));
                case "GEOPOINT":
                    return Property.of(property -> property.geoPoint(GeoPointProperty.of(geoPointProperty -> geoPointProperty)));
                default:
                case "STRING":
                    return Property.of(property -> property.text(TextProperty.of(textProperty -> textProperty.index(true).fielddata(true))));
            }
        }
        return null;
    }
    
    public static void main(String[] args) throws Exception {
        // ESUtil esUtil = new ESUtil("127.0.0.1", 9200, "elastic", "change");
        // System.out.println(esUtil.getMapping("poi"));
        // esUtil.close();
        System.out.println(Integer.MAX_VALUE);
    }
}
