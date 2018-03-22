package com.cglee079.cointelebot.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cglee079.cointelebot.dao.ClientSuggestDao;
import com.cglee079.cointelebot.model.ClientSuggestVo;

@Service
public class ClientSuggestService {
	Logger logger = Logger.getLogger(ClientSuggestService.class.getName());
	
	@Autowired
	private ClientSuggestDao clientSuggestDao;

	public boolean insert(Integer userId, String userame, String message) {
		ClientSuggestVo clientSuggest = new ClientSuggestVo();
		clientSuggest.setUserId(userId.toString());
		clientSuggest.setUsername(userame);
		clientSuggest.setMsg(message);
		clientSuggest.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		clientSuggest.setMsg(message);
		
		return clientSuggestDao.insert(clientSuggest);
	}

}
