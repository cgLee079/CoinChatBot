package com.cglee079.cointelebot.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.cglee079.cointelebot.constants.C;

public class ClientVo {
	private String userId = null;
	private String username = null;
	private String exchange = null;
	private Integer timeLoop = null;
	private Integer dayLoop = null;
	private Integer targetUpPrice = null;
	private Integer targetDownPrice = null;
	private Integer avgPrice = null;
	private Double coinCount = null;
	private String enabled = "Y";
	private String openDate;
	private String reopenDate;
	private String closeDate;

	public ClientVo() {
		if (C.ENABLED_UPBIT) { exchange = C.EXCHANGE_UPBIT; }
		if (C.ENABLED_BITHUMB) { exchange = C.EXCHANGE_BITHUMB; }
		if (C.ENABLED_COINONE) { exchange = C.EXCHANGE_COINONE; }
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public Integer getTimeLoop() {
		return timeLoop;
	}

	public void setTimeLoop(Integer timeLoop) {
		this.timeLoop = timeLoop;
	}

	public Integer getDayLoop() {
		return dayLoop;
	}

	public void setDayLoop(Integer dayLoop) {
		this.dayLoop = dayLoop;
	}

	public Integer getTargetUpPrice() {
		return targetUpPrice;
	}

	public void setTargetUpPrice(Integer targetPrice) {
		this.targetUpPrice = targetPrice;
	}
	
	public Integer getTargetDownPrice() {
		return targetDownPrice;
	}

	public void setTargetDownPrice(Integer targetPrice) {
		this.targetDownPrice = targetPrice;
	}

	public Integer getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(Integer avgPrice) {
		this.avgPrice = avgPrice;
	}

	public Double getCoinCount() {
		return coinCount;
	}

	public void setCoinCount(Double coinCount) {
		this.coinCount = coinCount;
	}

	public String getMakeDate() {
		return openDate;
	}

	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}

	public void setOpenDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.openDate = format.format(date);
	}

	public String getOpenDate() {
		return openDate;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getReopenDate() {
		return reopenDate;
	}

	public void setReopenDate(String reopenDate) {
		this.reopenDate = reopenDate;
	}

	public void setReopenDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.reopenDate = format.format(date);
	}

	public String getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}

	public void setCloseDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.closeDate = format.format(date);
	}

}
