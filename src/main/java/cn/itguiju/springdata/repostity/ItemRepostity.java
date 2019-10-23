package cn.itguiju.springdata.repostity;

import cn.itguiju.springdata.pojo.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ItemRepostity extends ElasticsearchRepository<Item,Long>{
    List<Item> findByPriceBetween(double d1,double d2);
}
