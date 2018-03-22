package com.cglee079.cointelebot.telegram;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.MessageEntity;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.cglee079.cointelebot.coin.CoinManager;
import com.cglee079.cointelebot.constants.C;
import com.cglee079.cointelebot.exception.ServerErrorException;
import com.cglee079.cointelebot.log.Log;
import com.cglee079.cointelebot.model.ClientVo;
import com.cglee079.cointelebot.model.DailyInfoVo;
import com.cglee079.cointelebot.model.TimelyInfoVo;
import com.cglee079.cointelebot.service.ClientMsgService;
import com.cglee079.cointelebot.service.ClientService;
import com.cglee079.cointelebot.service.ClientSuggestService;
import com.cglee079.cointelebot.util.TimeStamper;

public class TelegramBot extends AbilityBot  {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private ClientMsgService clientMsgService;
	
	@Autowired
	private ClientSuggestService clientSuggestService;
	
	@Autowired
	private CoinManager coinManager;
	
	private String helpMsg;
	
	protected TelegramBot(String botToken, String botUsername) {
		super(botToken, botUsername);
		
		helpMsg = "";
		helpMsg += "별도의 설정을 안하셨다면,\n";
		helpMsg += "3시간 간격으로 " + C.MY_COIN_STR + " 가격 알림이 전송됩니다.\n";
		helpMsg += "\n";
		
		helpMsg += "별도의 설정을 안하셨다면,\n";
		helpMsg += "1일 간격으로 거래량, 상한가, 하한가, 종가가 비교되어 전송됩니다.\n";
		helpMsg += "\n";
		
		helpMsg += "별도의 설정을 안하셨다면,\n";
		
		//
		String exchange = "";
		if(C.ENABLED_UPBIT) {exchange = "업비트";}
		if(C.ENABLED_BITHUMB) {exchange = "빗썸";}
		if(C.ENABLED_COINONE) {exchange = "코인원";}
		helpMsg += exchange;
		//
		
		helpMsg += " 기준의 정보가 전송됩니다.\n";
		helpMsg += "\n";

		helpMsg += "평균단가,코인개수를 설정하시면,\n";
		helpMsg += "원금, 현재금액, 손익금을 확인 하실 수 있습니다.\n";
		helpMsg += "\n";
		
		helpMsg += "목표가격을 설정하시면,\n";
		helpMsg += "목표가격이 되었을때 알림을 받을 수 있습니다.\n";
		helpMsg += "목표가격을 위한 가격정보는 각 거래소에서 1분 주기로 갱신합니다.\n";
		helpMsg += "\n";
		
		helpMsg += "톡을 보내시면 현재 " + C.MY_COIN_STR + " 가격을 확인 하실 수 있습니다.\n";
		helpMsg += "\n";
		
		helpMsg += "한국 프리미엄 정보를 확인 하실 수 있습니다.\n";
		helpMsg += "\n";
		
		if(!(C.MY_COIN == C.COIN_BTC)) {
			helpMsg += "비트코인대비 변화량을 확인 하실 수 있습니다.\n";
		}
		helpMsg += "-------------------------\n";
		helpMsg += "\n";
		
		helpMsg += "다음 명령어를 사용해주세요.\n";
		helpMsg += "\n";

		helpMsg += "* 시간 알림 간격 설정\n";
		helpMsg += "/timeloop 시간\n";
		helpMsg += "0(시간 알림 끄기), 1 ~ 12 시간 간격만 적용됩니다.\n";
		helpMsg += "ex) /timeloop 0 = 시간 알림 없음\n";
		helpMsg += "ex) /timeloop 3 = 3시간 간격 알림\n";
		helpMsg += "\n";

		helpMsg += "* 일일 알림 간격 설정\n";
		helpMsg += "/dayloop 일\n";
		helpMsg += "0(일일 알림 끄기), 1 ~ 7 일 간격만 적용됩니다.\n";
		helpMsg += "ex) /dayloop 0 = 일일 알림 없음\n";
		helpMsg += "ex) /dayloop 3 = 3일 간격 알림\n";
		helpMsg += "\n";

		helpMsg += "* 거래소 설정\n";
		helpMsg += "/exchange 거래소번호\n";
	
		//
		if(C.ENABLED_COINONE) { helpMsg += "1 - 코인원, ";}
		if(C.ENABLED_BITHUMB) { helpMsg += "2 - 빗썸, ";}
		if(C.ENABLED_UPBIT) { helpMsg += "3 - 업비트 ";}
		helpMsg += "\n";
		
		if(C.ENABLED_COINONE) {helpMsg += "ex) /exchange 1 = 코인원으로 거래소 설정\n";}
		if(C.ENABLED_BITHUMB) {helpMsg += "ex) /exchange 2 = 빗썸으로 거래소 설정\n"; }
		if(C.ENABLED_UPBIT) {helpMsg += "ex) /exchange 3 = 업비트로 거래소 설정\n"; }
		helpMsg += "\n";
		//
		
		helpMsg += "* 목표가격 설정\n";
		helpMsg += "/target 금액  \n";
		helpMsg += "ex) /target 0 = 목표가격 입력 초기화\n";
		helpMsg += "ex) /target " + C.MY_COIN_TARGET + " = 목표가격 " + C.MY_COIN_TARGET + " 원\n";
		helpMsg += "\n";

		helpMsg += "* 평균단가 설정\n";
		helpMsg += "/price 금액  \n";
		helpMsg += "ex) /price 0 = 평균단가 입력 초기화\n";
		helpMsg += "ex) /price " + C.MY_COIN_PRICE + " = 평균단가 " + C.MY_COIN_PRICE + " 원\n";
		helpMsg += "\n";

		helpMsg += "* 코인개수 설정\n";
		helpMsg += "/number 개수\n";
		helpMsg += "ex) /number 0 = " + C.MY_COIN_STR + " 개수 입력 초기화\n";
		helpMsg += "ex) /number " + C.MY_COIN_NUMBER +" = " + C.MY_COIN_STR+ " 개수 설정\n";
		helpMsg += "\n";

		helpMsg += "/help - 도움말 \n";
		helpMsg += "/calc - 원금,현재금액,손익금 확인 \n";
		helpMsg += "/kimp - 한국 프리미엄 정보 확인 \n";
		if(!(C.MY_COIN == C.COIN_BTC)) {
			helpMsg += "/btc  - 비트코인 대비 변화량 확인\n";
		}
		helpMsg += "/info - 설정확인\n";
		helpMsg += "/stop - 모든알림(시간알림 간격, 일일알림 간격, 목표가격) 끄기 \n";
		helpMsg += "\n";
		helpMsg += "* 문의,제안,건의사항은 다음 명령어를 이용해주세요.\n";
		helpMsg += "/msg 내용\n";
		helpMsg += "ex) /msg 안녕하세요. 건의사항이~~~\n";
		helpMsg += "\n";
		helpMsg += "\n";
		
		//
		helpMsg += "국내정보 By ";
		if(C.ENABLED_COINONE) { helpMsg += "코인원, ";}
		if(C.ENABLED_BITHUMB) { helpMsg += "빗썸, ";}
		if(C.ENABLED_UPBIT) { helpMsg += "업비트";}
		helpMsg += "\n";
		//
		
		helpMsg += "미국정보 By ";
		if(C.ENABLED_BITTREX) { helpMsg += "Bittrex, ";}
		if(C.ENABLED_BITFINEX) { helpMsg += "Bitfinex ";}
		
		helpMsg += "\n";
		
		helpMsg += "환율정보 By the European Central Bank\n";
		helpMsg += "\n";
		helpMsg += "Developed By CGLEE ( cglee079@gmail.com )\n";
	}
	
	@Override
	public int creatorId() {
		return 503609560;
	}

	@Override
	public void onUpdateReceived(Update update) {
		clientMsgService.insert(update);
		
		List<MessageEntity> enitities = update.getMessage().getEntities();
		if(enitities != null) {
			MessageEntity entity = update.getMessage().getEntities().get(0);
			if(entity.getType().equals("bot_command")) {
				Integer userId = update.getMessage().getFrom().getId();
				String username = update.getMessage().getFrom().getLastName() + " " + update.getMessage().getFrom().getFirstName();
				
				String message = update.getMessage().getText().substring(1);
				String[] messageCutted = message.split("\\s+");
				String command = messageCutted[0];
				
				String[] args = new String[messageCutted.length -1];
				for(int i = 0 ; i < args.length; i++) {
					args[i] = messageCutted[i+1];
				}
				
				switch(command) {
				case "start": cmdStart(userId, username); break;
				case "stop": cmdStop(userId); break;
				case "help": cmdHelp(userId); break;
				case "info": cmdInfo(userId); break;
				case "kimp": cmdKimp(userId); break;
				case "btc": cmdBtc(userId); break;
				case "calc": cmdCalc(userId); break;
				case "price": cmdSetPrice(userId, args); break;
				case "target": cmdSetTargetPrice(userId, args); break;
				case "number": cmdSetCoinCnt(userId, args); break;
				case "timeloop": cmdSetTimeLoop(userId, args); break;
				case "dayloop": cmdSetDayLoop(userId, args); break;
				case "exchange": cmdSetExchange(userId, args); break;
				case "msg" : cmdMsg(userId, username, args); break;
				default:
					sendMessage("잘못된 명령어입니다\n도움말 - /help", userId.toString());
					break;
				
				}
			}
		}
		
		if(update.getMessage().getEntities() == null){
			Integer userId = update.getMessage().getFrom().getId();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String date = format.format(new Date());

			JSONObject coin = null;
			int currentValue = 0;
			String exchange = clientService.getExchange(userId);
			try {
				coin = coinManager.getCoin(C.MY_COIN, exchange);
			} catch (ServerErrorException e) {
				Log.i(e.log());
				Log.i(e.getStackTrace());
				sendMessage("잠시 후 다시 보내주세요.\n" + e.getTelegramMsg(), userId);
				return;
			}
			
			
			if(coin == null) {
				Log.i("가격정보를 보낼 수 없습니다. : return NULL");
				sendMessage("잠시 후 다시보내주세요", userId);
				return ;
			}
			
			currentValue = coin.getInt("last");
			String msg = "";
			msg += "현재시각 : " + date + "\n";
			msg += "현재가격 : " + toCommaStr(currentValue) + " 원\n";
			
			sendMessage(msg, userId);
		};
		
		
	}

	public void cmdHelp(Integer userId) {
		String msg = "";
		msg += C.MY_COIN_STR + " 알리미 ver" + C.VERSION + "\n";
		msg += "\n";
		msg += helpMsg;
		sendMessage(msg, userId);
	}

	public void cmdInfo(Integer userId) {
		ClientVo client = clientService.get(userId);
		if(client == null){
			sendMessage("알림을 먼저 시작해주세요.\n명령어 /start", userId);
			return ;
		}
		
		String msg = "";
		msg += "현재 설정은 다음과 같습니다.\n";
		msg += "-----------------\n";
		
		if(client.getExchange().equals(C.EXCHANGE_COINONE)){ msg += "거래소     = 코인원\n";} 
		else if(client.getExchange().equals(C.EXCHANGE_BITHUMB)){ msg += "거래소     = 빗썸\n";}
		else if(client.getExchange().equals(C.EXCHANGE_UPBIT)){ msg += "거래소     = 업비트\n";}
		
		if(client.getTimeLoop() != 0){ msg += "시간알림 = 매 " + client.getTimeLoop() + " 시간 간격 알림\n";} 
		else{ msg += "시간알림 = 알람 없음\n";}
		
		if(client.getDayLoop() != 0){ msg += "일일알림 = 매 " + client.getDayLoop() + " 일 간격 알림\n";} 
		else{ msg += "일일알림 = 알람 없음\n";}
		
		if(client.getTargetUpPrice() != null){msg += "목표가격 = " + toCommaStr(client.getTargetUpPrice()) + " 원 \n";}
		else if(client.getTargetDownPrice() != null){msg += "목표가격 = " + toCommaStr(client.getTargetDownPrice()) + " 원 \n";}
		else { msg += "목표가격 = 입력되어있지 않음.\n";}
		
		if(client.getAvgPrice() != null){msg += "평균단가 = " + toCommaStr(client.getAvgPrice()) + " 원 \n";}
		else { msg += "평균단가 = 입력되어있지 않음.\n";}
		
		if(client.getCoinCount() != null){msg += "코인개수 = " + toStr(client.getCoinCount()) + " 개 \n"; }
		else { msg += "코인개수 = 입력되어있지 않음.\n";}
		
		sendMessage(msg, userId);
	}
	
	public void cmdStart(Integer userId, String username) {
		if (clientService.openChat(userId, username)) {
			String msg = C.MY_COIN_STR + " 알림이 시작되었습니다.\n";
			msg += helpMsg;
			sendMessage(msg, userId);
		} else {
			String msg = "이미 " + C.MY_COIN_STR + " 알리미에 설정 정보가 기록되어있습니다.";
			sendMessage(msg, userId);
			cmdInfo(userId);
		}
	}

	public void cmdStop(Integer userId) {
		if (clientService.stopChat(userId)) {
			String msg = C.MY_COIN_STR + " 모든알림(시간알림, 일일알림, 목표가격알림)이 중지되었습니다.\n";
			sendMessage(msg, userId);
		} else {
			String msg = "알람이 설정되어있지 않습니다";
			sendMessage(msg, userId);
		}
	}

	public void cmdKimp(Integer userId) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = format.format(new Date());

		JSONObject coin = null;
		int currentValue = 0;
		try {
			String exchange = clientService.getExchange(userId);
			coin = coinManager.getCoinWithKimp(exchange);
		} catch (ServerErrorException e) {
			Log.i(e.log());
			Log.i(e.getStackTrace());
			sendMessage("잠시 후 다시 보내주세요.\n" + e.getTelegramMsg(), userId);
			return ;
		} 
		
		if(coin != null){
			currentValue = coin.getInt("last");
			
			String msg = "";
			msg += "현재시각 : " + date + "\n";
			msg += "달러환율 : $ 1 = " + toCommaStr(coin.getInt("rate")) +" 원 \n";
			msg += "--------------------\n";
			msg += "현재가격 : " + toCommaStr(currentValue) + " 원\n";
			msg += "미국가격 : " + toCommaStr(coin.getInt("usd2krw")) + " 원 ($ " + toStr(coin.getDouble("usd")) + ")\n";
			msg += "프리미엄 : " + toSignStr(coin.getDouble("kimp")) + " %\n";
			
			sendMessage(msg, userId);
		}
	}
	
	public void cmdBtc(Integer userId) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = format.format(new Date());

		JSONObject coin = null;
		JSONObject btc = null;
		String exchange = clientService.getExchange(userId);
		try {
			coin = coinManager.getCoin(C.MY_COIN, exchange);
			btc = coinManager.getCoin(C.COIN_BTC, exchange);
		} catch (ServerErrorException e) {
			Log.i(e.log());
			Log.i(e.getStackTrace());			
			sendMessage("잠시 후 다시 보내주세요.\n" + e.getTelegramMsg(), userId);
			return;
		}
	
		if(coin != null && btc != null){
			int coinCV = coin.getInt("last");
			int coinBV = coin.getInt("first");
			int btcCV = btc.getInt("last");
			int btcBV = btc.getInt("first");
			
			String msg = "";
			msg += "현재시각 : " + date + "\n";
			msg += "BTC 가격 : " + toCommaStr(btcCV) +" 원 \n";
			msg += C.MY_COIN_STR + " 가격 : " + toCommaStr(coinCV) +" 원 \n";
			msg += "BTC 24시간 변화량 : " + getPercent(btcCV, btcBV) + "\n";
			msg += C.MY_COIN_STR + " 24시간 변화량 : " + getPercent(coinCV, coinBV) + "\n";
			
			sendMessage(msg, userId);
		}
	}
	
	public void cmdCalc(Integer userId) {
		ClientVo client = clientService.get(userId);
		if(client != null){
			if( client.getCoinCount() == null){ sendMessage("먼저 코인개수를 설정해주세요.\nex)/number " + C.MY_COIN_NUMBER, userId); return ;}
			if( client.getAvgPrice() == null){ sendMessage("먼저 평균단가를 설정해주세요.\nex) /price " + C.MY_COIN_PRICE, userId); return ;}
			if( client.getAvgPrice() != null && client.getCoinCount() != null){
				try {
					JSONObject coin = coinManager.getCoin(C.MY_COIN,client.getExchange());
					sendKrwMessage(client, coin.getInt("last"));
				} catch (ServerErrorException e) {
					Log.i(e.log());
					Log.i(e.getStackTrace());
					sendMessage("잠시 후 다시 보내주세요.\n" + e.getTelegramMsg(), userId);
					return ;
				}
			}
		} else{
			 sendMessage("알림을 먼저 시작해주세요.", userId);
		}
	}
	
	public void cmdSetExchange(Integer userId, String[] args) {
		String ex = "";
		if(C.ENABLED_COINONE) { ex += "1 - 코인원, ";}
		if(C.ENABLED_BITHUMB) { ex += "2 - 빗썸 ";}
		if(C.ENABLED_UPBIT) { ex += "3 - 업비트 ";}
		ex += "\n";
		if(C.ENABLED_COINONE) {ex += "ex) /exchange 1\n";}
		if(C.ENABLED_BITHUMB) {ex += "ex) /exchange 2\n";}
		if(C.ENABLED_UPBIT) {ex += "ex) /exchange 3\n";}
		
		if (args.length == 0) { // case1. 평균단가를 입력하지 않았을때
			sendMessage("거래소 번호를 입력해주세요.\n" + ex, userId);
			return;
		} else if (args.length > 1) { // case2. 평균단가와 다른 파라미터를 붙였을때.
			sendMessage("거래소 번호만 입력해주세요.\n" + ex, userId);
			return;
		} else {
			String priceStr = args[0];
			int exchangeNum = 0;

			try { // case3. 평균단가에 문자가 포함될때
				exchangeNum = Integer.parseInt(priceStr);
			} catch (NumberFormatException e) {
				sendMessage("거래소 번호는 숫자로만 입력해주세요.\n" + ex, userId);
				return;
			}
			
			if(exchangeNum == 1 && C.ENABLED_COINONE) {//코인원
				clientService.updateExchange(userId.toString(), C.EXCHANGE_COINONE);
				sendMessage("거래소가 코인원으로 설정되었습니다.", userId);
			} 
			else if(exchangeNum == 2 && C.ENABLED_BITHUMB) {
				clientService.updateExchange(userId.toString(), C.EXCHANGE_BITHUMB);
				sendMessage("거래소가 빗썸으로 설정되었습니다.", userId);
			} 
			else if(exchangeNum == 3 && C.ENABLED_UPBIT) {
				clientService.updateExchange(userId.toString(), C.EXCHANGE_UPBIT);
				sendMessage("거래소가 업비트로 설정되었습니다.", userId);
			} 
			else {
				sendMessage("거래소 번호를 정확히 입력해주세요.\n" + ex, userId);
				return;
			}

		}
	}
	
	public void cmdSetPrice(Integer userId, String[] args) {
		if (args.length == 0) { // case1. 평균단가를 입력하지 않았을때
			sendMessage("평균단가를 입력해주세요.\nex) /price " + C.MY_COIN_PRICE, userId);
			return;
		} else if (args.length > 1) { // case2. 평균단가와 다른 파라미터를 붙였을때.
			sendMessage("평균단가만 입력해주세요.\nex) /price " + C.MY_COIN_PRICE, userId);
			return;
		} else {
			String priceStr = args[0];
			int price = 0;

			try { // case3. 평균단가에 문자가 포함될때
				price = Integer.parseInt(priceStr);
			} catch (NumberFormatException e) {
				sendMessage("평균단가는 숫자로만 입력해주세요.\nex) /price " + C.MY_COIN_PRICE, userId);
				return;
			}

			if(clientService.updatePrice(userId.toString(), price)){
				if (price == 0) { sendMessage("평균단가가 초기화 되었습니다.", userId); } // case4. 초기화
				else {sendMessage("평균단가 " + toCommaStr(price) + "원으로 설정되었습니다.", userId);} // case5.설정완료
			} else{
				sendMessage("알림을 먼저 시작해주세요.\n명령어 /start", userId);
			}
		}
	}
	

	public void cmdSetTargetPrice(Integer userId, String[] args) {
		ClientVo client = clientService.get(userId);
		String exchange = client.getExchange();
		int currentPrice = -1;
		try {
			currentPrice = coinManager.getCoin(C.MY_COIN, exchange).getInt("last");
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (ServerErrorException e1) {
			e1.printStackTrace();
			Logger.getLogger(this.getClass().getName()).severe("ErrorCode : " + e1.getErrCode());
			sendMessage("잠시 후 다시 보내주세요.\n" + e1.getTelegramMsg(), userId);
			return;
		}
		
		if (args.length == 0) { // case1. 평균단가를 입력하지 않았을때
			sendMessage("목표가격를 입력해주세요.\nex) /target " + C.MY_COIN_TARGET, userId);
			return;
		} else if (args.length > 1) { // case2. 평균단가와 다른 파라미터를 붙였을때.
			sendMessage("목표가격만 입력해주세요.\nex) /target " + C.MY_COIN_TARGET, userId);
			return;
		} else {
			String priceStr = args[0];
			int targetPrice = 0;

			try { // case3. 평균단가에 문자가 포함될때
				targetPrice = Integer.parseInt(priceStr);
			} catch (NumberFormatException e) {
				sendMessage("목표가격은 숫자로만 입력해주세요.\nex) /target " + C.MY_COIN_TARGET, userId);
				return;
			}

			if (targetPrice == 0) {  // case4. 초기화
				if(clientService.clearTargetPrice(userId.toString())) {
					sendMessage("목표가격이 초기화 되었습니다.", userId);
					return ;
				}
			} 
			
			String msg = "";
			msg += "목표가격 " + toCommaStr(targetPrice) + "원으로 설정되었습니다.\n";
			msg += "------------------------\n";
			msg += "목표가격 : " + toCommaStr(targetPrice) + " 원\n";
			msg += "현재가격 : " + toCommaStr(currentPrice) + " 원\n";
			msg += "가격차이 : " + toCommaStr(targetPrice - currentPrice) + " 원 (" + getPercent(targetPrice, currentPrice) + " )\n";
			if(targetPrice >= currentPrice) {
				if(clientService.updateTargetUpPrice(userId.toString(), targetPrice)){
					sendMessage(msg, userId); // case5.설정완료
				} else{
					sendMessage("알림을 먼저 시작해주세요.\n명령어 /start", userId);
				}
			} else {
				if(clientService.updateTargetDownPrice(userId.toString(), targetPrice)){
					sendMessage(msg, userId); // case5.설정완료
				} else{
					sendMessage("알림을 먼저 시작해주세요.\n명령어 /start", userId);
				}
			}
		}
	}
	
	public void cmdSetCoinCnt(Integer userId, String[] args) {
		if (args.length == 0) { // case1. 개수를 입력하지 않았을때
			sendMessage("코인개수를 입력해주세요.\nex) /number " + C.MY_COIN_NUMBER, userId);
			return;
		} else if (args.length > 1) { // case2. 평균단가와 다른 파라미터를
			sendMessage("코인개수만 입력해주세요.\nex) /number " + C.MY_COIN_NUMBER, userId);
			return;
		} else {
			String numberStr = args[0];
			double number = 0;

			try { // case3. 평균단가에 문자가 포함될때
				number = Double.parseDouble(numberStr);
			} catch (NumberFormatException e) {
				sendMessage("코인개수는 숫자로만 입력해주세요.\nex) /number " + C.MY_COIN_NUMBER, userId);
				return;
			}

			if(clientService.updateNumber(userId.toString(), number)){
				if (number == 0) { sendMessage("코인개수가 초기화 되었습니다.", userId); } // case4. 초기화
				else {sendMessage("코인개수가 " + number + " 개로 설정되었습니다.", userId);} // case5.설정완료
			} else{
				sendMessage("알림을 먼저 시작해주세요.\n명령어 /start", userId);
			}
		}
	}

	public void cmdSetTimeLoop(Integer userId, String[] args) {
		if (args.length == 0) { // case1. 시간을 입력하지 않았을때
			sendMessage("시간 알림 간격을 입력해주세요.\nex) /timeloop 3\n*가능 시간 간격 : 0(알림 끄기), 1 ~ 12", userId);
			return;
		} else if (args.length > 1) { // case2.시간과 다른 파라미터를 붙였을때.
			sendMessage("시간 알림 간격을 입력해주세요.\nex) /timeloop 3\n*가능 시간 간격 : 0(알림 끄기), 1 ~ 12", userId);
			return;
		} else {
			String priceStr = args[0];
			int timeloop = 0;

			try { // case3. 시간에 문자가 포함될때
				timeloop = Integer.parseInt(priceStr);
			} catch (NumberFormatException e) {
				sendMessage("시간 알림 간격은 숫자로만 입력해주세요.\nex) /timeloop 3\n*가능 시간 간격 : 0(알림 끄기), 1 ~ 12", userId);
				return;
			}

			switch (timeloop) {
			case 0: // case4. 알람 끄기
				if(clientService.updateTimeLoop(userId.toString(), timeloop)){
					sendMessage("시간 알림이 전송되지 않도록 설정하였습니다.", userId);
				} else{
					sendMessage("알림을 먼저 시작해주세요.\n명령어 /start", userId);
				}
				break;
			case 1: case 2: case 3: case 4: case 5: case 6:
			case 7: case 8: case 9: case 10: case 11: case 12:// case5.설정완료
				if(clientService.updateTimeLoop(userId.toString(), timeloop)){
					sendMessage("시간 알림 간격이 " + timeloop + "시간으로 설정되었습니다.", userId);
				} else{
					sendMessage("알림을 먼저 시작해주세요.\n명령어 /start", userId);
				}
				break;
			default: // case6. 다른 시간 입력
				sendMessage("시간 알림 간격을 정확히 입력해주세요.\n*가능 시간 간격 : 0(알림 끄기), 1 ~ 12", userId);
				break;
			}
		}
	}
	

	public void cmdSetDayLoop(Integer userId, String[] args) {
		if (args.length == 0) { // case1. 일일을 입력하지 않았을때
			sendMessage("일일 알림 간격을 입력해주세요.\nex) /dayloop 3\n*가능 일일 간격 : 0(알림 끄기), 1, 2, 3, 4, 5, 6, 7", userId);
			return;
		} else if (args.length > 1) { // case2.일일과 다른 파라미터를 붙였을때.
			sendMessage("일일 알림 간격을 입력해주세요.\nex) /dayloop 3\n*가능 일일 간격 : 0(알림 끄기), 1, 2, 3, 4, 5, 6, 7", userId);
			return;
		} else {
			String priceStr = args[0];
			int dayLoop = 0;

			try { // case3. 일일에 문자가 포함될때
				dayLoop = Integer.parseInt(priceStr);
			} catch (NumberFormatException e) {
				sendMessage("일일 알림 간격은 숫자로만 입력해주세요.\nex) /dayloop 3\n*가능 일일 간격 : 0(알림 끄기), 1, 2, 3, 4, 5, 6, 7", userId);
				return;
			}

			switch (dayLoop) {
			case 0: // case4. 알람 끄기
				if(clientService.updateDayLoop(userId.toString(), dayLoop)){
					sendMessage("일일 알림이 전송되지 않도록 설정하였습니다.", userId);
				} else{
					sendMessage("알림을 먼저 시작해주세요.\n명령어 /start", userId);
				}
				break;
			case 1: case 2: case 3: case 4: case 5: case 6: case 7:// case5.설정완료
				if(clientService.updateDayLoop(userId.toString(), dayLoop)){
					sendMessage("일일 알림 간격이 " + dayLoop + "일로 설정되었습니다.", userId);
				} else{
					sendMessage("알림을 먼저 시작해주세요.\n명령어 /start", userId);
				}
				break;
			default: // case6. 다른 시간 입력
				sendMessage("일일 알림 간격을 정확히 입력해주세요.\n*가능 일일 간격 : 0(알림 끄기), 1, 2, 3, 4, 5, 6, 7", userId);
				break;
			}
		}
	}
		
	public void cmdMsg(Integer userId, String username, String[] args) {
		if(args.length > 0) {
			String message  =  "";
			for(int i =0; i < args.length; i++) {
				message = message + args[i] + " ";
			}
			
			clientSuggestService.insert(userId, username, message);
			sendMessage("의견 감사드립니다.\n성투하세요!", userId);
			
			//To Me
			sendMessage("메세지가 도착했습니다!\n------------------\n\n"  + message +"\n\n------------------\n By " + username + " [" + userId + " ]", this.creatorId());
			
		} else {
			sendMessage("내용을 입력해주세요.\nex)/msg 안녕하세요.건의사항이~~", userId);
		}
	}
	
	/* Send Message */
	public void sendMessage(String msg, String id){
		Log.i("To Client  :  [id :" +id + " ]  " + msg.replace("\n", "  "));
		
		SendMessage sendMessage = new SendMessage(id, msg);
		
		try {
			sender.execute(sendMessage);
		} catch (TelegramApiException e) {
			Log.i(id + " 대화창은 닫혔습니다.\t" + e.getMessage());
			Log.i(e.getStackTrace());
			clientService.closeChat(id);
			return ;
		}
		
	}
	
	public void sendMessage(String msg, Integer id){
		this.sendMessage(msg, id.toString());
	}
	
	public void sendKrwMessage(ClientVo client, int coinVal){
		double cnt = client.getCoinCount();
		int price = client.getAvgPrice();
		String msg = "";
		msg += "평균단가 : " + toCommaStr(price) + " 원\n";
		msg += "현재가격 : " + toCommaStr(coinVal)+ " 원\n";
		msg += "코인개수 : " + toStr(cnt) + " 개\n";
		msg += "---------------------\n";
		msg += "투자금액 : " + toCommaStr((int)(cnt * price)) + "원\n"; 
		msg += "현재금액 : " + toCommaStr((int)(coinVal * cnt)) + "원\n";
		msg += "손익금액 : " + toSignStr((int)((coinVal * cnt) - (cnt * price))) + "원 (" + getPercent((int)(coinVal * cnt), (int)(cnt * price)) + ")\n";
		msg += "\n";
		this.sendMessage(msg, client.getUserId());
	}
	
	public void sendTargetPriceMessage(List<ClientVo> clients, JSONObject coinObj) {
		Integer currentPrice = coinObj.getInt("last");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = format.format(new Date());
		
		String msg = "";
		msg += "목표가격에 도달하였습니다!\n";
		msg += "현재시각 : " + date + "\n";

		
		ClientVo client = null;
		int clientLength = clients.size();
		for(int i = 0; i < clientLength; i++){
			client = clients.get(i);
			if(client.getTargetUpPrice() != null) { msg += "목표가격 : " + toCommaStr(client.getTargetUpPrice()) + " 원\n"; }
			if(client.getTargetDownPrice() != null) { msg += "목표가격 : " + toCommaStr(client.getTargetDownPrice()) + " 원\n"; }
			msg += "현재가격 : " + toCommaStr(currentPrice) + " 원\n";
			this.sendMessage(msg, client.getUserId());
			
			if(clientService.clearTargetPrice(client.getUserId())) {
				this.sendMessage("목표가격이 초기화되었습니다.", client.getUserId());
			}
		}
	}
	
	
	public void sendTimelyMessage(List<ClientVo> clients, TimelyInfoVo coinCurrent, TimelyInfoVo coinBefore, int timeLoop){
		int clientLength = clients.size();
		ClientVo client = null;
		
		int currentValue = coinCurrent.getLast();
		int beforeValue = coinBefore.getLast();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String date = format.format(new Date());
		
		String msg = "";
		msg += "현재시각: " + date + "\n";
		if(!coinCurrent.getResult().equals("success")){
			String currentErrorMsg = coinCurrent.getErrorMsg();
			String currentErrorCode = coinCurrent.getErrorCode();
			msg += "에러발생: " + currentErrorMsg +"\n";
			msg += "에러코드: " + currentErrorCode +"\n";
			msg += timeLoop + " 시간 전: " + toCommaStr(beforeValue) + " 원\n";
		} else{
			msg += "현재가격: " + toCommaStr(currentValue) + " 원\n";
			msg += timeLoop + " 시간 전: " + toCommaStr(beforeValue) + " 원\n";
			msg += "가격차이: " + toSignStr(currentValue - beforeValue) + " 원 (" + getPercent(currentValue, beforeValue) + ")\n";
		}
		
		for (int i = 0; i < clientLength; i++) {
			client = clients.get(i);
			this.sendMessage(msg, client.getUserId());
		}
	}
	
	public void sendDailyMessage(List<ClientVo> clients, DailyInfoVo coinCurrent, DailyInfoVo coinBefore, int dayLoop){
		long currentVolume = coinCurrent.getVolume();
		long beforeVolume  = coinBefore.getVolume();
		
		int currentHigh = coinCurrent.getHigh();
		int beforeHigh = coinBefore.getHigh();

		int currentLow = coinCurrent.getLow();
		int beforeLow = coinBefore.getLow();

		int currentLast = coinCurrent.getLast();
		int beforeLast = coinBefore.getLast();

		SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
		String date = format.format(new Date());

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
		
		String msg = "";
		msg += date + "\n";
		msg += "---------------------\n";
		msg += "금일의 거래량 : " + toCommaStr(currentVolume) + " \n";
		msg += dayLoopStr + "전 거래량 : " + toCommaStr(beforeVolume) + " \n";
		msg += "거래량의 차이 : " + toSignStr(currentVolume - beforeVolume) + " (" + getPercent(currentVolume, beforeVolume) + ")\n";
		msg += "\n";
		msg += "금일의 상한가 : " + toCommaStr(currentHigh) + " 원\n";
		msg += dayLoopStr + "전 상한가 : " + toCommaStr(beforeHigh) + " 원\n";
		msg += "상한가의 차이 : " + toSignStr(currentHigh - beforeHigh) + "원 (" + getPercent(currentHigh, beforeHigh) + ")\n";
		msg += "\n";
		msg += "금일의 하한가 : " + toCommaStr(currentLow) + " 원\n";
		msg += dayLoopStr + "전 하한가 : " + toCommaStr(beforeLow) + " 원\n";
		msg += "하한가의 차이 : " + toSignStr(currentLow - beforeLow) + " 원 (" + getPercent(currentLow, beforeLow) + ")\n";
		msg += "\n";
		msg += "금일의 종가 : " + toCommaStr(currentLast) + " 원\n";
		msg += dayLoopStr + "전 종가 : " + toCommaStr(beforeLast) + " 원\n";
		msg += "종가의 차이 : " + toSignStr(currentLast - beforeLast) + " 원 (" + getPercent(currentLast, beforeLast) + ")\n";
		msg += "\n";
		
		ClientVo client = null;
		int clientLength = clients.size();
		for(int i = 0; i < clientLength; i++){
			client = clients.get(i);
			this.sendMessage(msg, client.getUserId());
			
			if(client.getCoinCount() != null && client.getAvgPrice() != null){
				this.sendKrwMessage(client, currentLast);
			}
		}
	}
	
	/*
	 * Formatting Str
	 */
	public String toCommaStr(long i){
		return String.format("%,d", i);
	}
	
	public String toCommaStr(BigDecimal bd){
		return String.format("%,d", bd);
	}
	
	public String toStr(double d){
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(d);
	}
	
	public String toSignStr(long i){
		String prefix = "";
		if(i > 0){ prefix = "+";}
		return prefix + toCommaStr(i);
	}
	
	public String toSignStr(double d){
		String prefix = "";
		if(d > 0){ prefix = "+";}
		return prefix + toStr(d);
	}
	
	public String getPercent(long c, long b){
		String prefix = "";
		long gap = c - b;
		double percent = ((double)gap/(double)b) * 100;
		if (percent > 0) {
			prefix = "+";
		}
		DecimalFormat df = new DecimalFormat("#.##");
		return prefix + df.format(percent) + "%";
	}

}

