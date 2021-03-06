package com.cglee079.coinchatbot.coin.pooler;

import org.json.JSONObject;

import com.cglee079.coinchatbot.coin.HttpClient;
import com.cglee079.coinchatbot.config.id.Coin;
import com.cglee079.coinchatbot.exception.ServerErrorException;

public class CoinonePooler extends ApiPooler{
	
	public JSONObject getCoin(Coin coin) throws ServerErrorException {
		String param = coinParam.get(coin);
		
		String url = "https://api.coinone.co.kr/ticker/?format=json&currency=" + param;
		HttpClient httpClient = new HttpClient();
		String response;
		try {
			response = httpClient.get(url);
			JSONObject jsonObj = new JSONObject(response);
			if(jsonObj.getString("result").equals("success")){ 
				jsonObj.put("errorMsg", "");
				
				return jsonObj;				
			} else {
				throw new ServerErrorException("Coinone server error", jsonObj.getInt("errorCode"));
			}
		} catch (Exception e) {
			throw new ServerErrorException("Coinone server error : " + e.getMessage());
		}
		
	}
}
