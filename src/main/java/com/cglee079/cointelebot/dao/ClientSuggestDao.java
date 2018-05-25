package com.cglee079.cointelebot.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cglee079.cointelebot.log.Log;
import com.cglee079.cointelebot.model.ClientSuggestVo;

@Repository
public class ClientSuggestDao {
	final static String namespace = "com.cglee079.cointelebot.mapper.ClientSuggestMapper";

	@Autowired
	private SqlSessionTemplate sqlSession;
	
	public boolean insert(ClientSuggestVo clientSuggest){
		try { return sqlSession.insert(namespace + ".insert", clientSuggest) == 1; }
		catch (Exception e){
			Log.i("ERROR\t:\t" + e.getMessage());
			e.printStackTrace();
			return false; 
		}
	}

}
