package com.cglee079.coinchatbot.telegram.message;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.text.NumberFormatter;

import org.json.JSONObject;

import com.cglee079.coinchatbot.config.cmd.MainCmd;
import com.cglee079.coinchatbot.config.cmd.MarketCmd;
import com.cglee079.coinchatbot.config.cmd.SendMessageCmd;
import com.cglee079.coinchatbot.config.id.Coin;
import com.cglee079.coinchatbot.config.id.Lang;
import com.cglee079.coinchatbot.config.id.Market;
import com.cglee079.coinchatbot.model.ClientTargetVo;
import com.cglee079.coinchatbot.model.ClientVo;
import com.cglee079.coinchatbot.model.CoinConfigVo;
import com.cglee079.coinchatbot.model.CoinInfoVo;
import com.cglee079.coinchatbot.model.CoinWalletVo;
import com.cglee079.coinchatbot.model.TimelyInfoVo;
import com.cglee079.coinchatbot.util.NumberFormmater;
import com.cglee079.coinchatbot.util.TimeStamper;

public class MessageMaker {
	private Coin myCoinId;
	private NumberFormmater nf;
	private String version;
	private String exInvestKR;
	private String exInvestUS;
	private String exCoinCnt;
	private String exTargetKR;
	private String exTargetUS;
	private String exTargetRate;
	private HashMap<Market, Boolean> inBtcs;
		
	public MessageMaker(Coin myCoinId, CoinConfigVo config, NumberFormmater nts, HashMap<Market, Boolean> inBtcs) {
		this.myCoinId		= myCoinId;
		this.inBtcs		= inBtcs;
		version			= config.getVersion();
		exInvestKR 		= config.getExInvestKRW();
		exInvestUS 		= config.getExInvestUSD();
		exCoinCnt 		= config.getExCoinCnt();
		exTargetKR 		= config.getExTargetKRW();
		exTargetUS 		= config.getExTargetUSD();
		exTargetRate 	= config.getExTargetRate();
		
		this.nf = nts;
		
	}
	
	public String toMarketStr(Market marketId, Lang lang) {
		String marketStr = "";
		switch(lang) {
		case KR : 	marketStr = MarketCmd.from(marketId).getKr();break;
		case US : 	marketStr = MarketCmd.from(marketId).getUs();break;
		}
		return marketStr;
	}

	/********************/
	/** Common Message **/
	/********************/
	public String warningNeedToStart(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("알림을 먼저 시작해주세요. \n 명령어 /start  <<< 클릭!.\n"); break;
		case US : msg.append("Please start this service first.\n /start <<< click here.\n"); break; 
		}
		return msg.toString();
	}
	
	public String warningWaitSecond(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("잠시 후 다시 보내주세요.\n"); break;
		case US : msg.append("Please send message again after a while.\n"); break; 
		}
		return msg.toString();
	}
	
	
	public String msgToMain(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("\n# 메인화면으로 이동합니다.\n"); break;
		case US : msg.append("\n# Changed to main menu.\n"); break; 
		}
		return msg.toString();
	}
	
	public String msgPleaseSetInvestmentAmount(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("먼저 투자금액을 설정해주세요.\n메뉴에서 '" + MainCmd.SET_INVEST.getCmd(lang)  + "' 을 클릭해주세요."); break;
		case US : msg.append("Please set your investment amount first.\nPlease Click '" + MainCmd.SET_INVEST.getCmd(lang)  + "' on the main menu."); break; 
		}
		return msg.toString();
	}
	
	public String msgPleaseSetTheNumberOfCoins(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("먼저 코인개수를 설정해주세요.\n메뉴에서 '" + MainCmd.SET_COINCNT.getCmd(lang)  + "' 을 클릭해주세요."); break;
		case US : msg.append("Please set the number of coins first.\nPlease Click '" + MainCmd.SET_COINCNT.getCmd(lang)  + "' on the main menu."); break; 
		}
		return msg.toString();
	}
	
	/*******************/
	/** Start Message **/
	/*******************/
	public String msgStartService(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append(myCoinId + " 알림이 시작되었습니다.\n\n"); break;
		case US : msg.append(myCoinId + " Noticer Start.\n\n"); break; 
		}
		return msg.toString();
	}

	public String msgAlreadyStartService(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("이미 " + myCoinId + " 알리미에 설정 정보가 기록되어있습니다.\n"); break;
		case US : msg.append("Already " + myCoinId + " Noticer Started.\n Database have your setting information.\n"); break; 
		}
		return msg.toString();
	}

	/*****************************/
	/** Current Price Message ****/
	/*****************************/
	public String msgCurrentPrice(double currentValue, JSONObject coinMoney, ClientVo client) {
		StringBuilder msg = new StringBuilder();
		Lang lang 	= client.getLang();
		Market marketId 	= client.getMarketId();
		String date		= TimeStamper.getDateTime(client.getLocaltime());
		
		switch(lang) {
		case KR :
			msg.append("현재시각 : " +date + "\n");
			if(inBtcs.get(marketId)) {
				double currentMoney = coinMoney.getDouble("last");
				double currentBTC = currentValue;
				msg.append("현재가격 : " + nf.toMoneyStr(currentMoney, marketId) + " [" + nf.toBTCStr(currentBTC) + "]\n");
			} else {
				msg.append("현재가격 : " + nf.toMoneyStr(currentValue, marketId) + "\n");
			}
			break;
			
		case US :
			msg.append("Current Time  : " + date + "\n");
			if(inBtcs.get(marketId)) {
				double currentMoney = coinMoney.getDouble("last");
				double currentBTC = currentValue;
				msg.append("Current Price : " + nf.toMoneyStr(currentMoney, marketId) + " [" + nf.toBTCStr(currentBTC) + "]\n");
			} else {
				msg.append("Current Price : " + nf.toMoneyStr(currentValue, marketId) + "\n");
			}
			break; 
		}
		return msg.toString();
	}
	
	/**********************************/
	/** Each Market Price Message *****/
	/**********************************/
	public String msgEachMarketPrice(double exchangeRate, LinkedHashMap<Market, Double> lasts, ClientVo client) {
		StringBuilder msg = new StringBuilder();
		Market marketId = client.getMarketId();
		Lang lang 	= client.getLang();
		String date		= TimeStamper.getDateTime(client.getLocaltime());
		double mylast 	= lasts.get(marketId);
		
		switch(lang) {
		case KR :
			msg.append("현재 시각  : "  + date + "\n");
			msg.append("\n");
			msg.append("나의 거래소 : "  + toMarketStr(marketId, lang) + "\n");
			msg.append("금일의 환율 : $1 = " + nf.toExchangeRateKRWStr(exchangeRate) + "\n");
			msg.append("----------------------------\n");
			break;
		case US :
			msg.append("Current Time  : "  + date + "\n");
			msg.append("\n");
			msg.append("My Market     : "  + toMarketStr(marketId, lang) + "\n");
			msg.append("Exchange rate : $1 = " + nf.toKRWStr(exchangeRate) + "\n");
			msg.append("----------------------------\n");
			break; 
		}
		
		Iterator<Market> iter = lasts.keySet().iterator();
		
		Market marketKRW = Market.COINONE; // KRW 대표
		Market marketUSD = Market.BITFINEX; // USD 대표
		
		if(marketId.isKRW()) { // 설정된 마켓이 한화인 경우,
			while(iter.hasNext()) {
				Market key = iter.next();
				
				if(key == marketId) { // 내 마켓
					msg.append("★ ");
				} 
				
				msg.append(toMarketStr(key, lang) + "  : ");
				if(lasts.get(key) == -1) {
					msg.append("Server Error");
				} else { 
					if(key.isKRW()) {
						msg.append(nf.toMoneyStr(lasts.get(key), marketKRW));
						msg.append("  [" + nf.toMoneyStr(lasts.get(key)/ exchangeRate, marketUSD) + "]");
					} else if(key.isUSD()){
						msg.append(nf.toMoneyStr(lasts.get(key) * exchangeRate, marketKRW));
						msg.append("  [" + nf.toMoneyStr(lasts.get(key), marketUSD) + "]");
						msg.append(" ( P. " + nf.toSignPercentStr(mylast,  lasts.get(key) * exchangeRate ) + ") ");
					}
				}
				msg.append("\n");
			}
		}
		
		else if(marketId.isUSD()) { // 설정된 마켓이 달러인 경우,
			while(iter.hasNext()) {
				Market key = iter.next();
				if(key == marketId) {
					msg.append("★ ");
				}
				
				msg.append(toMarketStr(key, lang) + "  : ");
				if(lasts.get(key) == -1) {
					msg.append("Server Error");
				} else { 
					if(key.isKRW()) {
						msg.append(nf.toMoneyStr(lasts.get(key) / exchangeRate, marketUSD));
						msg.append("  [" + nf.toMoneyStr(lasts.get(key), marketKRW) + "]");
					} else if(key.isUSD()) {
						msg.append(nf.toMoneyStr(lasts.get(key), marketUSD));
						msg.append("  [" + nf.toMoneyStr(lasts.get(key) * exchangeRate, marketKRW) + "]");
					}
				}
				msg.append("\n");
			}
		}
		return msg.toString();
	}
	
	/******************************/
	/** BTC change rate Message*****/
	/******************************/
	public String msgBTCCurrentTime(String date, Lang lang) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR : msg.append("현재시각 : " + date + "\n"); break;
		case US : msg.append("Current Time : " + date + "\n"); break; 
		}
		
		msg.append("----------------------\n");
		msg.append("\n");
		return msg.toString();
	}
	
	public String msgBTCNotSupportAPI(Market marketID, Lang lang) {
		String marketStr = toMarketStr(marketID, lang);
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append(marketStr + " API는 해당 정보를 제공하지 않습니다.\n"); break;
		case US : msg.append(marketStr + " market API does not provide this information.\n"); break; 
		}
		return msg.toString();
	}

	public String msgBTCReplaceAnotherMarket(Market marketId, Lang lang) {
		String marketStr = toMarketStr(marketId, lang);
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append(marketStr + " 기준 정보로 대체합니다.\n"); break;
		case US : msg.append("Replace with " + marketStr  + " market information.\n"); break; 
		}
		return msg.toString();
	}
	
	public String msgBTCResult(double coinCV, double coinBV, double btcCV, double btcBV, JSONObject coinMoney, Market marketId, Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR :
			msg.append("BTC 현재 시각 가격 : " + nf.toOnlyBTCMoneyStr(btcCV, marketId) +"\n");
			msg.append("BTC 24시간전 가격 : " + nf.toOnlyBTCMoneyStr(btcBV, marketId) +"\n");
			msg.append("\n");
			if(inBtcs.get(marketId)) {
				msg.append(myCoinId + " 현재 시각 가격 : " + nf.toMoneyStr(coinMoney.getDouble("last"), marketId) + " [" + nf.toBTCStr(coinCV) + "]\n");
				msg.append(myCoinId + " 24시간전 가격 : " + nf.toMoneyStr(coinMoney.getDouble("first"), marketId) + " [" + nf.toBTCStr(coinBV) + "]\n");
			} else {
				msg.append(myCoinId + " 현재 시각 가격 : " + nf.toMoneyStr(coinCV, marketId) + "\n");
				msg.append(myCoinId + " 24시간전 가격 : " + nf.toMoneyStr(coinBV, marketId) + "\n");
			}
			msg.append("\n");
			msg.append("BTC 24시간 변화량 : " + nf.toSignPercentStr(btcCV, btcBV) + "\n");
			msg.append(myCoinId + " 24시간 변화량 : " + nf.toSignPercentStr(coinCV, coinBV) + "\n");
			break;
			
		case US : 
			msg.append("BTC Price at current time : " + nf.toMoneyStr(btcCV, marketId) +"\n");
			msg.append("BTC Price before 24 hours : " + nf.toMoneyStr(btcBV, marketId) +"\n");
			msg.append("\n");
			if(inBtcs.get(marketId)) {
				msg.append(myCoinId + " Price at current time : " + nf.toMoneyStr(coinMoney.getDouble("last"), marketId) + " [" + nf.toBTCStr(coinCV) + "]\n");
				msg.append(myCoinId + " Price before 24 hours : " + nf.toMoneyStr(coinMoney.getDouble("first"), marketId) + " [" + nf.toBTCStr(coinBV) + "]\n");
			} else {
				msg.append(myCoinId + " Price at current time : " + nf.toMoneyStr(coinCV, marketId) + "\n");
				msg.append(myCoinId + " Price before 24 hours : " + nf.toMoneyStr(coinBV, marketId) + "\n");
			}
			msg.append("\n");
			msg.append("BTC 24 hour rate of change : " + nf.toSignPercentStr(btcCV, btcBV) + "\n");
			msg.append(myCoinId + " 24 hour rate of change : " + nf.toSignPercentStr(coinCV, coinBV) + "\n");
			break; 
		}
		return msg.toString();
	}
	
	

	/**************************/
	/** Calculate Message *****/
	/**************************/
	public String msgCalcResult(double price, double cnt, double avgPrice, double coinValue, JSONObject btcObj, ClientVo client) {
		StringBuilder msg = new StringBuilder();
		Market marketId 	= client.getMarketId();
		Lang lang 	= client.getLang();
		String date		= TimeStamper.getDateTime(client.getLocaltime());
		
		switch(lang) {
		case KR :
			msg.append("현재 시각  : "  + date + "\n");
			msg.append("\n");
			msg.append("코인개수 : " + nf.toCoinCntStr(cnt, lang) + "\n");
			if(inBtcs.get(marketId)) {
				double btcMoney = btcObj.getDouble("last");
				double avgBTC = avgPrice / btcMoney;
				double coinBTC = coinValue;
				double coinMoney = coinValue * btcMoney;
				msg.append("평균단가 : " + nf.toMoneyStr(avgPrice, marketId) + "  [" + nf.toBTCStr(avgBTC) + "]\n");
				msg.append("현재단가 : " + nf.toMoneyStr(coinMoney, marketId) + " [" + nf.toBTCStr(coinBTC) + "]\n");
				msg.append("---------------------\n");
				msg.append("투자금액 : " + nf.toInvestAmountStr(price, marketId) + "\n");
				msg.append("현재금액 : " + nf.toInvestAmountStr((int)(coinMoney * cnt), marketId) + "\n");
				msg.append("손익금액 : " + nf.toSignInvestAmountStr((int)((coinMoney * cnt) - (cnt * avgPrice)), marketId) + " (" + nf.toSignPercentStr((int)(coinMoney * cnt), (int)(cnt * avgPrice)) + ")\n");
				msg.append("\n");
			} else {
				double coinMoney = coinValue;
				msg.append("평균단가 : " + nf.toMoneyStr(avgPrice, marketId) + "\n");
				msg.append("현재단가 : " + nf.toMoneyStr(coinMoney, marketId)+ "\n");
				msg.append("---------------------\n");
				msg.append("투자금액 : " + nf.toInvestAmountStr(price, marketId) + "\n");
				msg.append("현재금액 : " + nf.toInvestAmountStr((int)(coinMoney * cnt), marketId) + "\n");
				msg.append("손익금액 : " + nf.toSignInvestAmountStr((int)((coinMoney * cnt) - (cnt * avgPrice)), marketId) + " (" + nf.toSignPercentStr((int)(coinMoney * cnt), (int)(cnt * avgPrice)) + ")\n");
				msg.append("\n");
			}
			break;
			
		case US : 
			msg.append("Current Time  : "  + date + "\n");
			msg.append("\n");
			msg.append("The number of coins : " + nf.toCoinCntStr(cnt, lang) + "\n");
			if(inBtcs.get(marketId)) {
				double btcMoney = btcObj.getDouble("last");
				double avgBTC = avgPrice / btcMoney;
				double coinBTC = coinValue;
				double coinMoney = coinValue * btcMoney;
				msg.append("Average Coin Price : " + nf.toMoneyStr(avgPrice, marketId) + "  [ " + nf.toBTCStr(avgBTC) + "]\n");
				msg.append("Current Coin Price : " + nf.toMoneyStr(coinMoney, marketId) + " [ " + nf.toBTCStr(coinBTC) + "]\n");
				msg.append("---------------------\n");
				msg.append("Investment Amount : " + nf.toInvestAmountStr(price, marketId) + "\n");
				msg.append("Curernt Amount : " + nf.toInvestAmountStr((int)(coinMoney * cnt), marketId) + "\n");
				msg.append("Profit and loss : " + nf.toSignInvestAmountStr((int)((coinMoney * cnt) - (cnt * avgPrice)), marketId) + " (" + nf.toSignPercentStr((int)(coinMoney * cnt), (int)(cnt * avgPrice)) + ")\n");
				msg.append("\n");
			} else {
				double coinMoney = coinValue;
				msg.append("Average Coin Price : " + nf.toMoneyStr(avgPrice, marketId) + "\n");
				msg.append("Current Coin Price : " + nf.toMoneyStr(coinMoney, marketId)+ "\n");
				msg.append("---------------------\n");
				msg.append("Investment Amount : " + nf.toInvestAmountStr(price, marketId) + "\n");
				msg.append("Curernt Amount : " + nf.toInvestAmountStr((int)(coinMoney * cnt), marketId) + "\n");
				msg.append("Profit and loss : " + nf.toSignInvestAmountStr((int)((coinMoney * cnt) - (cnt * avgPrice)), marketId) + " (" + nf.toSignPercentStr((int)(coinMoney * cnt), (int)(cnt * avgPrice)) + ")\n");
				msg.append("\n");
			}
			break; 
		}
		return msg.toString();
	}
	
	/***************************/
	/** Happy Line Message *****/
	/***************************/
	public String warningHappyLineFormat(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("코인개수는 숫자로만 입력해주세요.\n"); break;
		case US : msg.append("Please enter the number of coins only in numbers.\n");break; 
		}
		return msg.toString();
	}

	public String explainHappyLine(Market marketId, Lang lang) {
		StringBuilder msg = new StringBuilder();
		String exampleTarget = null;
		if(marketId.isKRW()) { exampleTarget = exTargetKR;}
		if(marketId.isUSD()) { exampleTarget = exTargetUS;}
		
		switch(lang) {
		case KR :
			msg.append("원하시는 코인가격을 입력해주세요.\n");
			msg.append("희망 손익금을 확인 하실 수 있습니다.\n");
			msg.append("\n");
			msg.append("* 코인가격은 숫자로 입력해주세요.\n");
			msg.append("* ex) " + exampleTarget + "  : 희망 코인가격 " + nf.toMoneyStr(Double.parseDouble(exampleTarget), marketId) + "\n");
			msg.append("\n");
			msg.append("\n");
			msg.append("# 메인으로 돌아가시려면 문자를 입력해주세요.\n");
			break;
		case US : 
			msg.append("Please enter the desired coin price.\n");
			msg.append("if enter your desired coin price,  you can see expected profit and loss.\n");
			msg.append("\n");
			msg.append("* Please enter the coin price in numbers only.\n");
			msg.append("* example) " + exampleTarget + "  : desired coin price " + nf.toMoneyStr(Double.parseDouble(exampleTarget), marketId) + " set\n");
			msg.append("\n");
			msg.append("\n");
			msg.append("# To return to main, enter a character.\n");
			break; 
		}
		
		return msg.toString();
	}
	
	public String msgHappyLineResult(double price, double coinCnt, double happyPrice, Market marketId, Lang lang) {
		double avgPrice = (double) price / coinCnt;
		StringBuilder msg = new StringBuilder();
		
		switch(lang) {
		case KR :
			msg.append("코인개수 : " + nf.toCoinCntStr(coinCnt, lang) + "\n");
			msg.append("평균단가 : " + nf.toMoneyStr(avgPrice, marketId) + "\n");
			msg.append("희망단가 : " + nf.toMoneyStr(happyPrice, marketId) + "\n");
			msg.append("---------------------\n");
			msg.append("투자금액 : " + nf.toInvestAmountStr(price, marketId) + "\n"); 
			msg.append("희망금액 : " + nf.toInvestAmountStr((long)(happyPrice * coinCnt), marketId) + "\n");
			msg.append("손익금액 : " + nf.toSignInvestAmountStr((long)((happyPrice * coinCnt) - (price)), marketId) + " (" + nf.toSignPercentStr((int)(happyPrice * coinCnt), price) + ")\n");
			break;
		case US :
			msg.append("The number of coins : " + nf.toCoinCntStr(coinCnt, lang) + "\n");
			msg.append("Average Coin Price  : " + nf.toMoneyStr(avgPrice, marketId) + "\n");
			msg.append("Desired Coin Price  : " + nf.toMoneyStr(happyPrice, marketId) + "\n");
			msg.append("---------------------\n");
			msg.append("Investment Amount : " + nf.toInvestAmountStr(price, marketId) + "\n"); 
			msg.append("Desired Amount : " + nf.toInvestAmountStr((long)(happyPrice * coinCnt), marketId) + "\n");
			msg.append("Profit and loss : " + nf.toSignInvestAmountStr((long)((happyPrice * coinCnt) - (price)), marketId) + "(" + nf.toSignPercentStr((int)(happyPrice * coinCnt), price) + ")\n");
			break; 
		}
		
		return msg.toString();
	}
	
	/*********************************/
	/** Set investment Price Message**/
	/********************************/
	public String explainSetPrice(Lang lang, Market marketId) {
		String exampleInvest = null;
		if(marketId.isKRW()) { exampleInvest = exInvestKR;}
		if(marketId.isUSD()) { exampleInvest = exInvestUS;}
		
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR :
			msg.append("투자금액을 입력해주세요.\n");
			msg.append("투자금액과 코인개수를 입력하시면 손익금을 확인 하실 수 있습니다.\n");
			msg.append("\n");
			msg.append("* 투자금액은 숫자로만 입력해주세요.\n");
			msg.append("* 0을 입력하시면 초기화됩니다.\n");
			msg.append("* ex) " + 0 + " : 초기화\n");
			msg.append("* ex) " + exampleInvest + " : 투자금액 " + nf.toInvestAmountStr(Double.parseDouble(exampleInvest), marketId) + " 설정\n");
			msg.append("\n");
			msg.append("\n");
			msg.append("# 메인으로 돌아가시려면 문자를 입력해주세요.\n");
			break;
		case US : 
			msg.append("Please enter your investment amount.\n");
			msg.append("If you enter the amount of investment and the number of coins, you can see profit and loss.\n");
			msg.append("\n");
			msg.append("* Please enter the investment amount in numbers only.\n");
			msg.append("* If you enter 0, it is initialized.\n");
			msg.append("* example) " + 0 + " : Init investment amount\n");
			msg.append("* example) " + exampleInvest + " : investment amount " + nf.toInvestAmountStr(Double.parseDouble(exampleInvest), marketId) + " set\n");
			msg.append("\n");
			msg.append("\n");
			msg.append("# To return to main, enter a character.\n");
			break;
		}
		return msg.toString();
	}

	public String warningPriceFormat(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("투자금액은 숫자로만 입력해주세요.\n"); break;
		case US : msg.append("Please enter the investment amount only in numbers.\n"); break; 
		}
		return msg.toString();
	}

	public String msgPriceInit(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("투자금액이 초기화 되었습니다.\n"); break;
		case US : msg.append("Investment Price has been init.\n"); break; 
		}
		return msg.toString();
	}

	public String msgPriceSet(double price, Market marketId, Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("투자금액이 " + nf.toInvestAmountStr(price, marketId) + "으로 설정되었습니다.\n"); break;
		case US : msg.append("The investment amount is set at " + nf.toInvestAmountStr(price, marketId)  + "\n"); break; 
		}
		return msg.toString();
	}
	
	/***************************/
	/** Set Coin Count Message**/
	/***************************/
	public String explainSetCoinCount(Lang lang) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR :
			msg.append("코인개수를 입력해주세요.\n");
			msg.append("투자금액과 코인개수를 입력하시면 손익금을 확인 하실 수 있습니다.\n");
			msg.append("\n");
			msg.append("* 코인개수는 숫자로만 입력해주세요.\n");
			msg.append("* 0을 입력하시면 초기화됩니다.\n");
			msg.append("* ex) " + 0 + " : 초기화\n");
			msg.append("* ex) " + exCoinCnt + " : 코인개수 " + nf.toCoinCntStr(Double.parseDouble(exCoinCnt), lang) + " 설정\n");
			msg.append("\n");
			msg.append("\n");
			msg.append("# 메인으로 돌아가시려면 문자를 입력해주세요.\n");
			break;
		case US : 
			msg.append("Please enter your number of coins.\n");
			msg.append("If you enter the amount of investment and the number of coins, you can see profit and loss.\n");
			msg.append("\n");
			msg.append("* Please enter the number of coins in numbers only.\n");
			msg.append("* If you enter 0, it is initialized.\n");
			msg.append("* example) " + 0 + " : Init the number of coins\n");
			msg.append("* example) " + exCoinCnt + " : the number of coins " + nf.toCoinCntStr(Double.parseDouble(exCoinCnt), lang) + " set\n");
			msg.append("\n");
			msg.append("\n");
			msg.append("# To return to main, enter a character.\n");
			break;
		}
		return msg.toString();
	}
	
	public String warningCoinCountFormat(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("코인개수는 숫자로만 입력해주세요.\n"); break;
		case US : msg.append("Please enter the number of coins only in numbers.\n"); break; 
		}
		return msg.toString();
	}
	
	public String msgCoinCountInit(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("코인개수가 초기화 되었습니다.\n"); break;
		case US : msg.append("Investment Price has been init.\n"); break; 
		}
		return msg.toString();
	}

	public String msgCoinCountSet(double number, Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("코인개수가 " + nf.toCoinCntStr(number, lang) + "로 설정되었습니다.\n"); break;
		case US : msg.append("The number of coins is set at " + nf.toCoinCntStr(number, lang)  + "\n"); break; 
		}
		return msg.toString();
	}

	/************************/
	/** Set Market Message **/
	/************************/
	public String explainMarketSet(Lang lang) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR :
			msg.append("거래중인 거래소를 설정해주세요.\n");
			msg.append("모든 정보는 설정 거래소 기준으로 전송됩니다.\n");
			break;
		case US : 
			msg.append("Please select an market.\n");
			msg.append("All information will be sent on the market you selected.\n");
			break;
		}
		return msg.toString();
	}

	public String msgMarketSet(Market marketId, Lang lang) {
		StringBuilder msg = new StringBuilder("");
		String marketStr = toMarketStr(marketId, lang);
		
		switch(lang) {
		case KR : msg.append("거래소가 " + marketStr + "(으)로 설정되었습니다.\n"); break;
		case US : msg.append("The exchange has been set up as " + marketStr + ".\n"); break; 
		}
		return msg.toString();
	}

	public String msgMarketNoSet(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("거래소가 설정되지 않았습니다.\n"); break;
		case US : msg.append("The market has not been set up.\n"); break; 
		}
		return msg.toString();
	}
	
	public String msgMarketSetChangeCurrency(ClientVo client, Double changePrice, List<Double> currentTargetPrices, List<Double> changedTargetPrices, Market changeMarketId) {
		StringBuilder msg = new StringBuilder();
		Market marketId 				= client.getMarketId();
		Lang lang					= client.getLang();
		Double currentPrice 		= client.getInvest();
	
		switch(lang) {
		case KR:
			msg.append("변경하신 거래소의 화폐단위가 변경되어,\n");
			msg.append("설정하신 투자금액/목표가를 환율에 맞추어 변동하였습니다.\n");
			if(currentPrice != null) { msg.append("투자금액 : " + nf.toInvestAmountStr(currentPrice, marketId) + " -> " + nf.toInvestAmountStr(changePrice, changeMarketId) + "\n"); }
			for(int i = 0; i < currentTargetPrices.size(); i++) {
				msg.append("목표가격 #" + nf.toNumberStr(i+1, 2) +  ": " + nf.toMoneyStr(currentTargetPrices.get(i), marketId) + " -> " + nf.toMoneyStr(changedTargetPrices.get(i), changeMarketId) + "\n"); 
			}
			break;
		case US:
			msg.append("* The currency unit of the exchange has been changed,\n");
			msg.append("the investment amount / target price you set has been changed to match the exchange rate.\n");
			msg.append("\n");
			if(currentPrice != null) { msg.append("Investment amount : " + nf.toInvestAmountStr(currentPrice, marketId) + " -> " + nf.toInvestAmountStr(changePrice, changeMarketId) + "\n"); }
			for(int i = 0; i < currentTargetPrices.size(); i++) {
				msg.append("Target Price #"+ nf.toNumberStr(i+1, 2) +  ": " + nf.toMoneyStr(currentTargetPrices.get(i), marketId) + " -> " + nf.toMoneyStr(changedTargetPrices.get(i), changeMarketId) + "\n");  
			}
			break;
		}
		
		return msg.toString();
	}
	
	
	
	/*****************************/
	/**** Target Price Message ***/
	/*****************************/
	public String showTargetList(Lang lang, Market marketId, List<ClientTargetVo> targets) {
		StringBuilder msg = new StringBuilder();
		
		ClientTargetVo target;
		switch(lang) {
		case KR : 
			msg.append("설정된 목표가격은 다음과 같습니다.\n");
			msg.append("-------------\n");
			if(targets.size() == 0) {
				msg.append("설정된 목표가격이 없습니다\n");
			}
			
			for(int i = 0; i < targets.size(); i++) {
				target = targets.get(i);
				msg.append(nf.toNumberStr(i+1, 2) +  "# " + nf.toMoneyStr(target.getPrice(), marketId) + " " + target.getFocus().getKr() +"\n");
			}
			break;
		case US:
			msg.append("Current Target Setting.\n");
			msg.append("-------------\n");
			if(targets.size() == 0) {
				msg.append("There are no set target prices.\n");
			}
			for(int i = 0; i < targets.size(); i++) {
				target = targets.get(i);
				msg.append(nf.toNumberStr(i+1, 2) + "# " + nf.toMoneyStr(target.getPrice(), marketId) + " " + target.getFocus().getUs() + "\n");
			}
			break;
		}
		
		return msg.toString();
	}
	
	public String explainTargetAdd(Lang lang, Market marketId) {
		StringBuilder msg = new StringBuilder();
		String exampleTarget = null;
		if(marketId.isKRW()) { exampleTarget = exTargetKR;}
		if(marketId.isUSD()) { exampleTarget = exTargetUS;}
		
		switch(lang) {
		case KR :
			msg.append("목표가격을 설정해주세요.\n");
			msg.append("목표가격이 되었을 때 알림이 전송됩니다.\n");
			msg.append("목표가격을 위한 가격정보는 1분 주기로 갱신됩니다.\n");
			msg.append("\n");
			msg.append("* 목표가격은 숫자 또는 백분율로 입력해주세요.\n");
			msg.append("* ex) " + exampleTarget + "  : 목표가격 " + nf.toMoneyStr(Double.parseDouble(exampleTarget), marketId) + "\n");
			msg.append("* ex) " + exTargetRate + "    : 현재가 +" + exTargetRate + "\n");
			msg.append("* ex) -" + exTargetRate + "  : 현재가 -" + exTargetRate + "\n");
			msg.append("\n");
			msg.append("\n");
			msg.append("# 메인으로 돌아가시려면 문자를 입력해주세요.\n");
			break;
		case US :
			msg.append("Please set Target Price.\n");
			msg.append("Once you reach the target price, you will be notified.\n");
			msg.append("Coin price information is updated every 1 minute.\n");
			msg.append("\n");
			msg.append("* Please enter the target price in numbers or percentages.\n");
			msg.append("* If you enter 0, it is initialized.\n");
			msg.append("* example)  " + exampleTarget + "  : Target price " + nf.toMoneyStr(Double.parseDouble(exampleTarget), marketId)+ "\n");
			msg.append("* example)  " + exTargetRate + "   : Current Price +" + exTargetRate + "\n");
			msg.append("* example)  -" + exTargetRate + "  : Current Prcie -" + exTargetRate + "\n");
			msg.append("\n");
			msg.append("\n");
			msg.append("# To return to main, enter a character.\n");
			break;
		}
		
		return msg.toString();
	}
	
	public String explainTargetDel(Lang lang, Market marketId) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR :
			msg.append("삭제할 목표가를 선택해주세요.\n");
			break;
		case US :
			msg.append("Please select Target you want to delete \n");
			break;
		}
		
		return msg.toString();
	}
	
	public String msgCompleteDelTarget(double price, Market marketId, Lang lang) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR :
			msg.append(nf.toMoneyStr(price, marketId) + " 목표가격이 삭제 되었습니다 \n");
			break;
		case US :
			msg.append(nf.toMoneyStr(price, marketId) + " Target Deleted\n");
			break;
		}
		
		return msg.toString();
	}

	public String warningTargetPriceAddPercent(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("목표가격 백분율을 -100% 이하로 설정 할 수 없습니다.\n"); break;
		case US : msg.append("You can not set the target price percentage below -100%.\n"); break; 
		}
		return msg.toString();
	}
	
	public String warningTargetPriceAddFormat(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("목표가격을 숫자 또는 백분율로 입력해주세요.\n"); break;
		case US : msg.append("Please enter target price as a number or percentage.\n"); break; 
		}
		return msg.toString();
	}
	
	public String warningTargetPriceDelFormat(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("목표가격을 정확히 입력해주세요.\n"); break;
		case US : msg.append("Please enter correct Target Price.\n"); break; 
		}
		return msg.toString();
	}
	
	public String warningAlreadyTarget(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("이미 설정된 목표가격 입니다.\n"); break;
		case US : msg.append("Target price already set.\n"); break; 
		}
		return msg.toString();
	}
	
	
	public String msgTargetPriceDeleted(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("해당 목표가격이 삭제되었습니다.\n"); break;
		case US : msg.append("The Target been deleted.\n"); break; 
		}
		return msg.toString();
	}
	
	public String msgTargetPriceSetResult(double TargetPrice, double currentValue, Market marketId, Lang lang) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR :
			msg.append("목표가격 " + nf.toMoneyStr(TargetPrice, marketId) + "으로 설정되었습니다.\n");
			msg.append("------------------------\n");
			msg.append("목표가격 : " + nf.toMoneyStr(TargetPrice, marketId) + "\n");
			msg.append("현재가격 : " + nf.toMoneyStr(currentValue, marketId) + "\n");
			msg.append("가격차이 : " + nf.toSignMoneyStr(TargetPrice - currentValue, marketId) + "(" + nf.toSignPercentStr(TargetPrice, currentValue) + " )\n");
			break;
		case US : 
			msg.append("The target price is set at " + nf.toMoneyStr(TargetPrice, marketId) + ".\n");
			msg.append("------------------------\n");
			msg.append("Target Price       : " + nf.toMoneyStr(TargetPrice, marketId) + "\n");
			msg.append("Current Price      : " + nf.toMoneyStr(currentValue, marketId) + "\n");
			msg.append("Price difference : " + nf.toSignMoneyStr(TargetPrice - currentValue, marketId) + " (" + nf.toSignPercentStr(TargetPrice, currentValue) + " )\n");
			break;
		}
		return msg.toString();
	}

	public String msgTargetPriceNotify(double currentValue, double price, Market marketId, Lang lang) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR :
			msg.append("목표가격에 도달하였습니다!\n");
			msg.append("목표가격 : " + nf.toMoneyStr(price, marketId) + "\n"); 
			msg.append("현재가격 : " + nf.toMoneyStr(currentValue, marketId) + "\n");
			break;
		case US : 
			msg.append("Target price reached!\n");
			msg.append("Traget Price : " + nf.toMoneyStr(price, marketId) + "\n"); 
			msg.append("Current Price : " + nf.toMoneyStr(currentValue, marketId) + "\n");
			break;
		}
		return msg.toString();
	}
	
	/******************************/
	/** Set Daily Notification **/
	/******************************/
	public String explainSetDayloop(Lang lang) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR : 
			msg.append("일일 알림 주기를 선택해주세요.\n");
			msg.append("선택 하신 일일주기로 알림이 전송됩니다.\n");
			break;
		case US : 
			msg.append("Please select daily notifications cycle.\n");
			msg.append("Coin Price information will be sent according to the cycle.\n");
			break;
		}
		return msg.toString();
	}

	public String msgDayloopSet(int dayloop, Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("일일 알림이 매 " +  dayloop + " 일주기로 전송됩니다.\n"); break;
		case US : msg.append("Daily notifications are sent every " + dayloop + " days.\n"); break; 
		}
		return msg.toString();
	}

	public String msgDayloopNoSet(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("일일 알림 주기가 설정 되지 않았습니다.\n"); break;
		case US : msg.append("Daily notifications cycle is not set.\n"); break; 
		}
		return msg.toString();
	}

	public String msgDayloopStop(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("일일 알림이 전송되지 않습니다.\n"); break;
		case US : msg.append("Daily notifications are not sent.\n"); break; 
		}
		return msg.toString();
	}
	

	/*******************************/
	/** Set Hourly Notification **/
	/*******************************/
	public String explainSetTimeloop(Lang lang) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR :
			msg.append("시간 알림 주기를 선택해주세요.\n");
			msg.append("선택 하신 시간주기로 알림이 전송됩니다.\n");
			break;
		case US :
			msg.append("Please select hourly notifications cycle.\n");
			msg.append("Coin Price information will be sent according to the cycle.\n");
			break;
		}
		return msg.toString();
	}
	
	public String msgTimeloopSet(int timeloop, Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("시간 알림이 " +  timeloop + " 시간 주기로 전송됩니다.\n"); break;
		case US : msg.append("Houly notifications are sent every " + timeloop + " hours.\n"); break; 
		}
		return msg.toString();
	}
	
	public String msgTimeloopNoSet(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("시간알림 주기가 설정 되지 않았습니다.\n"); break;
		case US : msg.append("Hourly notifications cycle is not set.\n"); break; 
		}
		return msg.toString();
	}


	public String msgTimeloopStop(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("시간 알림이 전송되지 않습니다.\n"); break;
		case US : msg.append("Hourly notifications are not sent.\n"); break; 
		}
		return msg.toString();
	}
	
	/*********************/
	/** Show setting *****/
	/*********************/
	public String msgClientSetting(ClientVo client, Lang lang) {
		StringBuilder msg = new StringBuilder();
		Market marketId = client.getMarketId();
		switch(lang) {
		case KR :
			msg.append("현재 설정은 다음과 같습니다.\n");
			msg.append("-----------------\n");
			
			msg.append("거래소     = ");
			msg.append(toMarketStr(client.getMarketId(), lang) + "\n");
					
			
			if(client.getDayloop() != 0){ msg.append("일일알림 = 매 " + client.getDayloop() + " 일 주기 알림\n");} 
			else{ msg.append("일일알림 = 알람 없음\n");}
			
			if(client.getTimeloop() != 0){ msg.append("시간알림 = 매 " + client.getTimeloop() + " 시간 주기 알림\n");} 
			else{ msg.append("시간알림 = 알람 없음\n");}
			
			if(client.getInvest() != null){msg.append("투자금액 = " + nf.toInvestAmountStr(client.getInvest(), marketId) + "\n");}
			else { msg.append("투자금액 = 입력되어있지 않음.\n");}
			
			if(client.getCoinCnt() != null){msg.append("코인개수 = " + nf.toCoinCntStr(client.getCoinCnt(), lang) + "\n"); }
			else { msg.append("코인개수 = 입력되어있지 않음.\n");}
			
			break;
			
		case US : 
			msg.append("The current setting is as follows.\n");
			msg.append("-----------------\n");
			
			msg.append("Market = ");
			msg.append(toMarketStr(client.getMarketId(), lang) + "\n");
			
			if(client.getDayloop() != 0){ msg.append("Daily Notification = every " + client.getDayloop() + " days\n");} 
			else{ msg.append("Daily Notification = No notifications.\n");}
			
			if(client.getTimeloop() != 0){ msg.append("Hourly Notification = every " + client.getTimeloop() + " hours\n");} 
			else{ msg.append("Hourly Notification = No notifications.\n");}
			
			if(client.getInvest() != null){msg.append("Investment amount = " + nf.toInvestAmountStr(client.getInvest(), marketId) + "\n");}
			else { msg.append("Investment amount = Not entered.\n");}
			
			if(client.getCoinCnt() != null){msg.append("The number of coins = " + nf.toCoinCntStr(client.getCoinCnt(), lang) + "\n"); }
			else { msg.append("The number of coins = Not entered.\n");}
			break;
		}
		
		return msg.toString();
	}
	
	
	/****************************/
	/** Stop all notifications **/
	/****************************/
	public String explainStop(Lang lang) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR :
			msg.append( "모든 알림(일일알림, 시간알림, 목표가알림)을 중지하시겠습니까?\n");
			msg.append( "\n");
			msg.append( "★ 필독!\n");
			msg.append( "1. 모든알림이 중지되더라도 공지사항은 전송됩니다.\n");
			msg.append( "2. 모든알림이 중지되더라도 버튼을 통해 코인관련정보를 받을 수 있습니다.\n");
			msg.append( "3. 서비스를 완전히 중지하시려면 대화방을 삭제해주세요!\n");
			break;
		case US : 
			msg.append( "Are you sure you want to stop all notifications (daily, hourly , target price notifications )?\n");
			msg.append( "\n");
			msg.append( "★  Must read!\n");
			msg.append( "1. Even if all notifications have been stopped, you will continue to receive notification of service usage.\n");
			msg.append( "2. Even if all notifications have been stopped, you received coin information using menu.\n");
			msg.append( "3. If you want to stop completry this service, Please block bot.\n");
			break;
		}
		
		return msg.toString();
	}
	
	public String msgStopAllNotice(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append(myCoinId + " 모든알림(시간알림, 일일알림, 목표가격알림)이 중지되었습니다.\n"); break;
		case US : msg.append("All notifications (daily, hourly , target price notifications ) be stoped.\n"); break; 
		}
		return msg.toString();
	}
	
	/***********************/
	/** Explain Coin List **/
	/***********************/
	public String explainCoinList(List<CoinInfoVo> coinInfos, Lang lang) {
		StringBuilder msg = new StringBuilder();
		CoinInfoVo coinInfo = null;
		int coinInfosLen = coinInfos.size();
		
		switch(lang) {
		case KR:
			msg.append("링크를 클릭 하시면,\n");
			msg.append("해당 코인알리미 봇으로 이동합니다.\n");
			
			msg.append("-----------------------\n");
			for (int i = 0; i < coinInfosLen; i++) {
				coinInfo = coinInfos.get(i);
				msg.append(coinInfo.getCoinId() + " [" + coinInfo.getCoinId().getKr() + "] \n");
				msg.append(coinInfo.getChatAddr() + "\n");
				msg.append("\n");
			}
			msg.append("\n");
			break;
		case US:
			msg.append("Click on the link to go to other Coin Noticer.\n");
			msg.append("-----------------------\n");
			for (int i = 0; i < coinInfosLen; i++) {
				coinInfo = coinInfos.get(i);
				msg.append(coinInfo.getCoinId() + " [" + coinInfo.getCoinId().getUs() + "] \n");
				msg.append(coinInfo.getChatAddr() + "\n");
				msg.append("\n");
			}
			msg.append("\n");
			break;
		}
	
		
		return msg.toString();
	}
	
	/***********/
	/** Help  **/
	/***********/
	public String explainHelp(List<Market> enabledMarketIds, Lang lang) {
		StringBuilder msg = new StringBuilder();
		if (lang.equals(Lang.KR)) {
			msg.append(myCoinId + " 알리미 ver" + version + "\n");
			msg.append("\n");
			msg.append("별도의 시간 알림 주기 설정을 안하셨다면,\n");
			msg.append("3시간 주기로 " + myCoinId + " 가격 알림이 전송됩니다.\n");
			msg.append("\n");
			msg.append("별도의 일일 알림 주기 설정을 안하셨다면,\n");
			msg.append("1일 주기로 거래량, 상한가, 하한가, 종가가 비교되어 전송됩니다.\n");
			msg.append("\n");
			msg.append("별도의 거래소 설정을 안하셨다면,\n");
	
			//
			msg.append(toMarketStr(enabledMarketIds.get(0), lang) + " 기준의 정보가 전송됩니다.\n");
			msg.append("\n");
			msg.append("투자금액,코인개수를 설정하시면,\n");
			msg.append("원금, 현재금액, 손익금을 확인 하실 수 있습니다.\n");
			msg.append("\n");
			msg.append("목표가격을 설정하시면,\n");
			msg.append("목표가격이 되었을때 알림을 받을 수 있습니다.\n");
			msg.append("목표가격을 위한 가격정보는 각 거래소에서 1분 주기로 갱신됩니다.\n");
			msg.append("\n");
			msg.append("프리미엄 정보를 확인 하실 수 있습니다.\n");
			msg.append("\n");
			msg.append("비트코인대비 변화량을 확인 하실 수 있습니다.\n");
			msg.append("\n");
			
			msg.append("거래소 By ");
			for(int i = 0; i < enabledMarketIds.size(); i++) {
				msg.append(toMarketStr(enabledMarketIds.get(i), lang) + ", ");
			}
			msg.append("\n");
			msg.append("환율정보 By the European Central Bank\n");
			msg.append("\n");
			msg.append("Developed By CGLEE ( cglee079@gmail.com )\n");
		} else if(lang.equals(Lang.US)) {
			msg.append(myCoinId + " Coin Noticer Ver" + version + "\n");
			msg.append("\n");
			msg.append("If you are using this service for the first time,\n");
			msg.append(myCoinId + " price are sent every 3 hours.\n");
			msg.append("\n");
			msg.append("If you are using this service for the first time,\n");
			msg.append(myCoinId + " price are sent every 1 days. (with high, low, last price and volume)\n");
			msg.append("\n");
			msg.append("If you are using this service for the first time,\n");
			msg.append("Information based on ");
			//
			
			msg.append(toMarketStr(enabledMarketIds.get(0), lang) + "  market will be sent.\n");
			msg.append("\n");
			msg.append("If you set the amount of investment and the number of coins,\n");
			msg.append("you can check the current amount of profit and loss.\n");
			msg.append("\n");
			msg.append("If you set Target price,\n");
			msg.append("Once you reach the target price, you will be notified.\n");
			msg.append("Coin price information is updated every 1 minute.\n");
			msg.append("\n");
			msg.append("You can check the coin price on each market\n");
			msg.append("\n");
			msg.append("You can check coin price change rate compared to BTC\n");
			msg.append("\n");
			
			msg.append("* Markets : ");
			for(int i = 0; i < enabledMarketIds.size(); i++) {
				msg.append(toMarketStr(enabledMarketIds.get(i), lang) + ", ");
			}
			msg.append("\n");
			msg.append("* Exchange Rate Information By the European Central Bank\n");
			msg.append("\n");
			msg.append("Developed By CGLEE ( cglee079@gmail.com )\n");
		}
		return msg.toString();
	}
	
	public String explainSetForeginer(Lang lang) {
		StringBuilder msg = new StringBuilder();
		msg.append("★  If you are not Korean, Must read!!\n");
		msg.append("* Use the " + MainCmd.PREFERENCE.getCmd(Lang.US) + " Menu.\n");
		msg.append("* First. Please set language to English.\n");
		msg.append("* Second. Set the time adjustment for accurate notifications. Because of time difference by country.\n");
//		msg.append("* Last. if you set market in USA using '" +CMDER.getMainSetMarket(Lang.US) + "' menu, the currency unit is changed to USD.\n");
		return msg.toString();
	}
	
	/*******************************/
	/** Send message to developer **/
	/*******************************/
	public String explainSendSuggest(Lang lang) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR :
			msg.append("개발자에게 내용이 전송되어집니다.\n");
			msg.append("내용을 입력해주세요.\n");
			msg.append("\n");
			msg.append("\n");
			msg.append("# 메인으로 돌아가시려면 " + SendMessageCmd.OUT.getCmd(lang)  + " 를 입력해주세요.\n");
			break;
		case US : 
			msg.append("Please enter message.\n");
			msg.append("A message is sent to the developer.\n");
			msg.append("\n");
			msg.append("\n");
			msg.append("# To return to main, enter " + SendMessageCmd.OUT.getCmd(lang) + "\n");
			break;
		}
		return msg.toString();
	}
	
	public String msgThankyouSuggest(Lang lang) {
		StringBuilder msg = new StringBuilder();
		switch(lang) {
		case KR :
			msg.append("의견 감사드립니다.\n");
			msg.append("성투하세요^^!\n");
			break;
		case US : 
			msg.append("Thank you for your suggest.\n");
			msg.append("You will succeed in your investment :)!\n");
			break;
		}
		return msg.toString();
	}

	
	/***************************/
	/*** Sponsoring Message ****/
	/***************************/
	public String explainSupport(Lang lang) {
		StringBuilder msg = new StringBuilder();
		String url = "https://toon.at/donate/dev-cglee";
		switch(lang) {
		case KR :
			msg.append("안녕하세요. 개발자 CGLEE 입니다.\n");
			msg.append("본 서비스는 무료 서비스 임을 다시 한번 알려드리며,\n");
			msg.append("절대로! 후원하지 않는다하여 사용자 여러분에게 불이익을 제공하지 않습니다.^^\n");
			msg.append("\n");
			msg.append("<a href='");
			msg.append(url);
			msg.append("'>★ 후원하러가기!</a>");
			msg.append("\n");
			msg.append("\n");
			msg.append("감사합니다.\n");
			break;
			
		case US : 
			msg.append("Hi. I'm developer CGLEE\n");
			msg.append("Never! I don't offer disadvantages to users by not sponsoring. :D\n");
			msg.append("\n");
			msg.append("<a href='");
			msg.append(url);
			msg.append("'>★ Go to Sponsoring!</a>");
			msg.append("\n");
			msg.append("\n");
			msg.append("Thank you for sponsoring.\n");
			break; 
		}
		return msg.toString();
	}
	

	/*********************/
	/*** Language Set  ***/
	/*********************/
	public String explainSetLanguage(Lang lang) {
		return "Please select a language.";
	}
	
	public String msgSetLanguageSuccess(Lang lang) {
		StringBuilder msg = new StringBuilder("");
		switch(lang) {
		case KR : msg.append("언어가 한국어로 변경되었습니다.\n"); break;
		case US : msg.append("Language changed to English.\n"); break; 
		}
		return msg.toString();
	}

	
	/*********************/
	/** Time Adjust  *****/
	/*********************/
	public String explainTimeAdjust(Lang lang) {
		StringBuilder msg = new StringBuilder();
		msg.append("한국분이시라면 별도의 시차조절을 필요로하지 않습니다.^^  <- for korean\n");
		msg.append("\n");
		msg.append("Please enter the current time for accurate time notification.\n");
		msg.append("because the time differs for each country.\n");
		msg.append("\n");
		msg.append("* Please enter in the following format.\n");
		msg.append("* if you entered 0, time adjust initialized.\n");
		msg.append("* example) 0 : init time adjust\n");
		msg.append("* example) " + TimeStamper.getDateBefore() + " 23:00 \n");
		msg.append("* example) " + TimeStamper.getDate() + " 00:33 \n");
		msg.append("* example) " + TimeStamper.getDate() +  " 14:30 \n");
		msg.append("\n");
		msg.append("\n");
		msg.append("# To return to main, enter a character.\n");
		return msg.toString();
	}
	
	public String warningTimeAdjustFormat(Lang lang) {
		return "Please type according to the format.\n";
	}
	
	public String msgTimeAdjustSuccess(Date date) {
		StringBuilder msg = new StringBuilder();
		msg.append("Time adjustment succeeded.\n");
		msg.append("Current Time : " + TimeStamper.getDateTime(date) + "\n");
		return msg.toString();
	}
	
//	public String msgToPreference() {
//		StringBuilder msg = new StringBuilder("");
//		msg.append("\n# Changed to Preference menu\n");
//		return msg.toString();
//	}
	
	/**********************************/
	/** Daily Notification Message ***/
	/**********************************/
	public String msgSendDailyMessage(ClientVo client, TimelyInfoVo coinCurrent, TimelyInfoVo coinBefore, JSONObject coinCurrentMoney, JSONObject coinBeforeMoney) {
		long currentVolume = coinCurrent.getVolume();
		long beforeVolume  = coinBefore.getVolume();
		double currentHigh = coinCurrent.getHigh();
		double beforeHigh = coinBefore.getHigh();
		double currentLow = coinCurrent.getLow();
		double beforeLow = coinBefore.getLow();
		double currentLast = coinCurrent.getLast();
		double beforeLast = coinBefore.getLast();
		double currentHighBTC	= 0;
		double beforeHighBTC	= 0;
		double currentLowBTC	= 0;
		double beforeLowBTC		= 0;
		double currentLastBTC 	= 0;
		double beforeLastBTC	= 0;
		
		Market marketId 	= client.getMarketId();
		Lang lang		= client.getLang();
		long localTime	= client.getLocaltime();
		String date 	= TimeStamper.getDateTime(localTime);
		int dayLoop 	= client.getDayloop();

		StringBuilder msg = new StringBuilder();
		msg.append(date + "\n");
		
		switch(lang) {
		case KR :
			String dayLoopStr = "";
			switch(dayLoop){
			case 1 : dayLoopStr ="하루"; break;
			case 2 : dayLoopStr ="이틀"; break;
			case 3 : dayLoopStr ="삼일"; break;
			case 4 : dayLoopStr ="사일"; break;
			case 5 : dayLoopStr ="오일"; break;
			case 6 : dayLoopStr ="육일"; break;
			case 7 : dayLoopStr ="한주"; break;
			}
			
			if(inBtcs.get(marketId)) {
				currentHighBTC	= currentHigh;
				beforeHighBTC	= beforeHigh;
				currentLowBTC	= currentLow;
				beforeLowBTC	= beforeLow;
				currentLastBTC 	= currentLast;
				beforeLastBTC	= beforeLast;
				
				currentLast = coinCurrentMoney.getDouble("last");
				currentHigh = coinCurrentMoney.getDouble("high");
				currentLow = coinCurrentMoney.getDouble("low");
				beforeLast = coinBeforeMoney.getDouble("last");
				beforeHigh = coinBeforeMoney.getDouble("high");
				beforeLow = coinBeforeMoney.getDouble("low");
				
				msg.append("---------------------\n");
				msg.append("금일의 거래량 : " + nf.toVolumeStr(currentVolume) + " \n");
				msg.append(dayLoopStr + "전 거래량 : " + nf.toVolumeStr(beforeVolume) + " \n");
				msg.append("거래량의 차이 : " + nf.toSignVolumeStr(currentVolume - beforeVolume) + " (" + nf.toSignPercentStr(currentVolume, beforeVolume) + ")\n");
				msg.append("\n");
				
				msg.append("금일의 상한가 : " + nf.toMoneyStr(currentHigh, marketId) + " ["+ nf.toBTCStr(currentHighBTC) + "]\n");
				msg.append(dayLoopStr + "전 상한가 : " + nf.toMoneyStr(beforeHigh, marketId) + " ["+ nf.toBTCStr(beforeHighBTC) + "]\n");
				msg.append("상한가의 차이 : " + nf.toSignMoneyStr(currentHigh - beforeHigh, marketId) + " (" + nf.toSignPercentStr(currentHigh, beforeHigh) + ")\n");
				msg.append("\n");
				
				msg.append("금일의 하한가 : " + nf.toMoneyStr(currentLow, marketId) + " ["+ nf.toBTCStr(currentLowBTC) + "]\n");
				msg.append(dayLoopStr + "전 하한가 : " + nf.toMoneyStr(beforeLow, marketId) + " ["+ nf.toBTCStr(beforeLowBTC) + "]\n");
				msg.append("하한가의 차이 : " + nf.toSignMoneyStr(currentLow - beforeLow, marketId) + " (" + nf.toSignPercentStr(currentLow, beforeLow) + ")\n");
				msg.append("\n");
				
				msg.append("금일의 종가 : " + nf.toMoneyStr(currentLast, marketId) + " ["+ nf.toBTCStr(currentLastBTC) + "]\n");
				msg.append(dayLoopStr + "전 종가 : " + nf.toMoneyStr(beforeLast, marketId) + " ["+ nf.toBTCStr(beforeLastBTC) + "]\n");
				msg.append("종가의 차이 : " + nf.toSignMoneyStr(currentLast - beforeLast, marketId) + " (" + nf.toSignPercentStr(currentLast, beforeLast) + ")\n");
				msg.append("\n");
			} else {
				msg.append("---------------------\n");
				msg.append("금일의 거래량 : " + nf.toVolumeStr(currentVolume) + " \n");
				msg.append(dayLoopStr + "전 거래량 : " + nf.toVolumeStr(beforeVolume) + " \n");
				msg.append("거래량의 차이 : " + nf.toSignVolumeStr(currentVolume - beforeVolume) + " (" + nf.toSignPercentStr(currentVolume, beforeVolume) + ")\n");
				msg.append("\n");
				
				msg.append("금일의 상한가 : " + nf.toMoneyStr(currentHigh, marketId) + "\n");
				msg.append(dayLoopStr + "전 상한가 : " + nf.toMoneyStr(beforeHigh, marketId) + "\n");
				msg.append("상한가의 차이 : " + nf.toSignMoneyStr(currentHigh - beforeHigh, marketId) + " (" + nf.toSignPercentStr(currentHigh, beforeHigh) + ")\n");
				msg.append("\n");
				
				msg.append("금일의 하한가 : " + nf.toMoneyStr(currentLow, marketId) + "\n");
				msg.append(dayLoopStr + "전 하한가 : " + nf.toMoneyStr(beforeLow, marketId) + "\n");
				msg.append("하한가의 차이 : " + nf.toSignMoneyStr(currentLow - beforeLow, marketId) + " (" + nf.toSignPercentStr(currentLow, beforeLow) + ")\n");
				msg.append("\n");
				
				
				msg.append("금일의 종가 : " + nf.toMoneyStr(currentLast, marketId) + "\n");
				msg.append(dayLoopStr + "전 종가 : " + nf.toMoneyStr(beforeLast, marketId) + "\n");
				msg.append("종가의 차이 : " + nf.toSignMoneyStr(currentLast - beforeLast, marketId) + " (" + nf.toSignPercentStr(currentLast, beforeLast) + ")\n");
				msg.append("\n");
			}
			break;
			
		case US : 
			if(inBtcs.get(marketId)) {
				currentHighBTC	= currentHigh;
				beforeHighBTC	= beforeHigh;
				currentLowBTC	= currentLow;
				beforeLowBTC	= beforeLow;
				currentLastBTC 	= currentLast;
				beforeLastBTC	= beforeLast;
				
				currentLast = coinCurrentMoney.getDouble("last");
				currentHigh = coinCurrentMoney.getDouble("high");
				currentLow = coinCurrentMoney.getDouble("low");
				beforeLast = coinBeforeMoney.getDouble("last");
				beforeHigh = coinBeforeMoney.getDouble("high");
				beforeLow = coinBeforeMoney.getDouble("low");
				
				msg.append("---------------------\n");
				msg.append("Volume at today : " + nf.toVolumeStr(currentVolume) + " \n");
				msg.append("Volume before " + dayLoop + " day : " + nf.toVolumeStr(beforeVolume) + " \n");
				msg.append("Volume difference : " + nf.toSignVolumeStr(currentVolume - beforeVolume) + " (" + nf.toSignPercentStr(currentVolume, beforeVolume) + ")\n");
				msg.append("\n");
				msg.append("High at Today : " + nf.toMoneyStr(currentHigh, marketId) + " ["+ nf.toBTCStr(currentHighBTC) + "]\n");
				msg.append("High before " + dayLoop + " day : " + nf.toMoneyStr(beforeHigh, marketId) + " ["+ nf.toBTCStr(beforeHighBTC) + "]\n");
				msg.append("High difference : " + nf.toSignMoneyStr(currentHigh - beforeHigh, marketId) + " (" + nf.toSignPercentStr(currentHigh, beforeHigh) + ")\n");
				msg.append("\n");
				msg.append("Low at Today : " + nf.toMoneyStr(currentLow, marketId) + " ["+ nf.toBTCStr(currentLowBTC) + "]\n");
				msg.append("Low before " + dayLoop + " day : "+ nf.toMoneyStr(beforeLow, marketId) + " ["+ nf.toBTCStr(beforeLowBTC) + "]\n");
				msg.append("Low difference : " + nf.toSignMoneyStr(currentLow - beforeLow, marketId) + " (" + nf.toSignPercentStr(currentLow, beforeLow) + ")\n");
				msg.append("\n");
				msg.append("Last at Today : " + nf.toMoneyStr(currentLast, marketId) + " ["+ nf.toBTCStr(currentLastBTC) + "]\n");
				msg.append("Last before " + dayLoop + " day : "+ nf.toMoneyStr(beforeLast, marketId) + " ["+ nf.toBTCStr(beforeLastBTC) + "]\n");
				msg.append("Last difference : " + nf.toSignMoneyStr(currentLast - beforeLast, marketId) + " (" + nf.toSignPercentStr(currentLast, beforeLast) + ")\n");
				msg.append("\n");
			} else {
				msg.append("---------------------\n");
				msg.append("Volume at today : " + nf.toVolumeStr(currentVolume) + " \n");
				msg.append("Volume before " + dayLoop + " day : " + nf.toVolumeStr(beforeVolume) + " \n");
				msg.append("Volume difference : " + nf.toSignVolumeStr(currentVolume - beforeVolume) + " (" + nf.toSignPercentStr(currentVolume, beforeVolume) + ")\n");
				msg.append("\n");
				msg.append("High at Today : " + nf.toMoneyStr(currentHigh, marketId) + "\n");
				msg.append("High before " + dayLoop + " day : " + nf.toMoneyStr(beforeHigh, marketId) + "\n");
				msg.append("High difference : " + nf.toSignMoneyStr(currentHigh - beforeHigh, marketId) + " (" + nf.toSignPercentStr(currentHigh, beforeHigh) + ")\n");
				msg.append("\n");
				msg.append("Low at Today : " + nf.toMoneyStr(currentLow, marketId) + "\n");
				msg.append("Low before " + dayLoop + " day : " + nf.toMoneyStr(beforeLow, marketId) + "\n");
				msg.append("Low difference : " + nf.toSignMoneyStr(currentLow - beforeLow, marketId) + " (" + nf.toSignPercentStr(currentLow, beforeLow) + ")\n");
				msg.append("\n");
				msg.append("Last at Today : "  + nf.toMoneyStr(currentLast, marketId) + "\n");
				msg.append("Last before " + dayLoop + " day : " + nf.toMoneyStr(beforeLast, marketId) + "\n");
				msg.append("Last difference : " + nf.toSignMoneyStr(currentLast - beforeLast, marketId) + " (" + nf.toSignPercentStr(currentLast, beforeLast) + ")\n");
				msg.append("\n");
			}
			break; 
		}
		
		return msg.toString();
	}
	
	/**********************************/
	/** Timely Notification Message ***/
	/**********************************/
	public String msgSendTimelyMessage(ClientVo client, TimelyInfoVo coinCurrent, TimelyInfoVo coinBefore, JSONObject coinCurrentMoney, JSONObject coinBeforeMoney) {
		StringBuilder msg = new StringBuilder();
		Market marketId = client.getMarketId();
		Lang lang		= client.getLang();
		int timeLoop 	= client.getTimeloop();
		long localTime	= client.getLocaltime();
		String date 	= TimeStamper.getDateTime(localTime);
		
		double currentValue = coinCurrent.getLast();
		double beforeValue = coinBefore.getLast();
		
		switch(lang) {
		case KR :
			msg.append("현재시각: " + date + "\n");
			if(!coinCurrent.getResult().equals("success")){
				String currentErrorMsg = coinCurrent.getErrorMsg();
				String currentErrorCode = coinCurrent.getErrorCode();
				msg.append("에러발생: " + currentErrorMsg +"\n");
				msg.append("에러코드: " + currentErrorCode +"\n");
				
				if(inBtcs.get(marketId)) {
					double beforeBTC = beforeValue;
					double beforeMoney = coinBeforeMoney.getDouble("last");
					
					msg.append(timeLoop + " 시간 전: " + nf.toMoneyStr(beforeMoney, marketId) + " 원 [" + nf.toBTCStr(beforeBTC) + " BTC]\n");
				} else {
					msg.append(timeLoop + " 시간 전: " + nf.toMoneyStr(beforeValue, marketId) + " 원\n");
				}
			} else{
				if(inBtcs.get(marketId)) {
					double currentBTC = currentValue;
					double beforeBTC = beforeValue;
					double currentMoney = coinCurrentMoney.getDouble("last");
					double beforeMoney = coinBeforeMoney.getDouble("last");

					msg.append("현재가격: " + nf.toMoneyStr(currentMoney, marketId) + " [" + nf.toBTCStr(currentBTC)+ "]\n");
					msg.append(timeLoop + " 시간 전: " + nf.toMoneyStr(beforeMoney, marketId) + " [" + nf.toBTCStr(beforeBTC) + "]\n");
					msg.append("가격차이: " + nf.toSignMoneyStr(currentMoney - beforeMoney, marketId) + " (" + nf.toSignPercentStr(currentMoney, beforeMoney) + ")\n");
				} else {
					msg.append("현재가격: " + nf.toMoneyStr(currentValue, marketId) + "\n");
					msg.append(timeLoop + " 시간 전: " + nf.toMoneyStr(beforeValue, marketId) + "\n");
					msg.append("가격차이: " + nf.toSignMoneyStr(currentValue - beforeValue, marketId) + " (" + nf.toSignPercentStr(currentValue, beforeValue) + ")\n");
				}
			}
			break;
			
		case US :
			msg.append("Current Time: " + date + "\n");
			if(!coinCurrent.getResult().equals("success")){
				String currentErrorMsg = coinCurrent.getErrorMsg();
				String currentErrorCode = coinCurrent.getErrorCode();
				msg.append("Error Msg : " + currentErrorMsg +"\n");
				msg.append("Error Code: " + currentErrorCode +"\n");
				
				if(inBtcs.get(marketId)) {
					double beforeBTC = beforeValue;
					double beforeMoney = coinBeforeMoney.getDouble("last");
					
					msg.append("Coin Price before " + timeLoop + " hour : " + nf.toMoneyStr(beforeMoney, marketId) + " [" + nf.toBTCStr(beforeBTC) + "]\n");
				} else {
					msg.append("Coin Price before " + timeLoop + " hour : " + nf.toMoneyStr(beforeValue, marketId) + "\n");
				}
			} else{
				if(inBtcs.get(marketId)) {
					double currentBTC = currentValue;
					double beforeBTC = beforeValue;
					double currentMoney = coinCurrentMoney.getDouble("last");
					double beforeMoney = coinBeforeMoney.getDouble("last");

					msg.append("Coin Price at Current Time : " + nf.toMoneyStr(currentMoney, marketId) + " [" + nf.toBTCStr(currentBTC)+ "]\n");
					msg.append("Coin Price before " + timeLoop + " hour : " + nf.toMoneyStr(beforeMoney, marketId) + " [" + nf.toBTCStr(beforeBTC) + "]\n");
					msg.append("Coin Price Difference : " + nf.toSignMoneyStr(currentMoney - beforeMoney, marketId) + " (" + nf.toSignPercentStr(currentMoney, beforeMoney) + ")\n");
				} else {
					msg.append("Coin Price at Current Time : " + nf.toMoneyStr(currentValue, marketId) + "\n");
					msg.append("Coin Price before " + timeLoop + " hour : " + nf.toMoneyStr(beforeValue, marketId) + "\n");
					msg.append("Coin Price Difference : " + nf.toSignMoneyStr(currentValue - beforeValue, marketId) + " (" + nf.toSignPercentStr(currentValue, beforeValue) + ")\n");
				}
			}
			break; 
		}
		
		return msg.toString();
	}


}