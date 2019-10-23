package cn.itguiju;

import cn.itguiju.springdata.pojo.Item;
import cn.itguiju.springdata.repostity.ItemRepostity;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.geogrid.InternalGeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class test {
    @Autowired
    ElasticsearchTemplate templatel;
    @Autowired
    public ItemRepostity repostity;
    @Test
    public void test(){
        templatel.createIndex(Item.class);
        templatel.putMapping(Item.class);
    }
    @Test
    public void test1(){
        List<Item> list = new ArrayList<>();
        list.add(new Item(2L, "坚果手机R1", " 手机", "锤子", 3699.00, "http://image.leyou.com/123.jpg"));
        list.add(new Item(3L, "华为META10", " 手机", "华为", 4499.00, "http://image.leyou.com/3.jpg"));
        // 接收对象集合，实现批量新增
        repostity.saveAll(list);;
    }
    @Test
    public void testQuery(){
        Optional<Item> optional = this.repostity.findById(1l);
        System.out.println(optional.get());
    }
    @Test
    public void testSort(){
        repostity.findByPriceBetween(2000d,3000d );
    }
    @Test
    public void queryTest(){
        // 构建查询条件
        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        //添加基本查询
        builder.withQuery(QueryBuilders.matchQuery("title","小米手机" ));
        //添加结果过滤
        builder.withSourceFilter(new FetchSourceFilter(new String[]{"title","price","id"},null));
        //添加排序
        builder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        //添加分页
        builder.withPageable(PageRequest.of(0,2 ));
        Page<Item> items = repostity.search(builder.build());
    }
    @Test
    public void queryAgg(){
        NativeSearchQueryBuilder builder=new NativeSearchQueryBuilder();
        builder.addAggregation(AggregationBuilders.terms("brands").field("brand"));
        AggregatedPage<Item> search =(AggregatedPage<Item>)repostity.search(builder.build());
        StringTerms terms= (StringTerms) search.getAggregation("brands");
        List<StringTerms.Bucket> buckets = terms.getBuckets();
        buckets.forEach(bucket -> {
            System.out.println(bucket.getKeyAsString()+" "+bucket.getDocCount());
        });
    }
}
