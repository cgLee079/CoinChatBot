package com.cglee079.cointelebot.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cglee079.cointelebot.model.CoinMarketConfigVo;

@Repository
public class CoinMarketConfigDao {
	final static String namespace = "com.cglee079.cointelebot.mapper.CoinMarketConfigMapper";

	@Autowired
	private SqlSessionTemplate sqlSession;
	
	public List<CoinMarketConfigVo> list(String market, String coinId) {
		Map<String, Object> map = new HashMap<>();
		map.put("market", market);
		map.put("coinId", coinId);
		return sqlSession.selectList(namespace + ".list", map);
	}
}
