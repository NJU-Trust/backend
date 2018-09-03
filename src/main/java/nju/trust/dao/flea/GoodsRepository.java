package nju.trust.dao.flea;

import nju.trust.entity.flea.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * All rights Reserved, Designed by Popping Lim
 *
 * @Author: Popping Lim
 * @Date: 2018/9/1
 * @Todo:
 */
@Repository
public interface GoodsRepository extends JpaRepository<Goods, Integer>{

}
