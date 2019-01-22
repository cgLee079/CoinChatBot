package com.cglee079.coinchatbot.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cglee079.coinchatbot.constants.SET;
import com.cglee079.coinchatbot.dao.TimelyInfoDao;
import com.cglee079.coinchatbot.log.Log;
import com.cglee079.coinchatbot.model.TimelyInfoVo;
import com.google.gson.Gson;

@Service
public class TimelyInfoService {

	@Autowired
	private TimelyInfoDao timelyInfoDao;

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH");

	public TimelyInfoVo get(String coinId, Date d, String market) {
		return timelyInfoDao.get(coinId, formatter.format(d), market);
	}

	public boolean insert(String coinId, Date d, String market, JSONObject coin) {
		TimelyInfoVo timelyInfo = TimelyInfoVo.builder()
				.coinId(coinId)
				.date(formatter.format(d))
				.marketId(market)
				.high(coin.getDouble("high"))
				.low(coin.getDouble("low"))
				.last(coin.getDouble("last"))
				.volume((long) coin.getDouble("volume"))
				.result(coin.getString("result"))
				.errorCode(coin.getString("errorCode"))
				.errorMsg(coin.getString("errorMsg"))
				.build();
				
		return timelyInfoDao.insert(timelyInfo);
	}

	public JSONObject getBefore(String coinId, Date dateCurrent, String market) {
		Date dateBefore = new Date();
		dateBefore.setTime(dateCurrent.getTime() - (1000 * 60 * 60));
		TimelyInfoVo timelyInfo = timelyInfoDao.get(coinId, formatter.format(dateBefore), market);
		Gson gson = new Gson();
		String jsonString = gson.toJson(timelyInfo);
		return new JSONObject(jsonString);
	}

	public List<JSONObject> list(String coinId, String market, int cnt) {
		List<TimelyInfoVo> timelyInfos = timelyInfoDao.list(coinId, market, cnt);
		List<JSONObject> jsons = new ArrayList<>();
		String jsonString = null; 
		Gson gson = new Gson();
		
		for(int i = 0; i < timelyInfos.size(); i++) {
			jsonString = gson.toJson(timelyInfos.get(i));
			jsons.add(jsons.size(), new JSONObject(jsonString));
		}
		return jsons;
	}

}