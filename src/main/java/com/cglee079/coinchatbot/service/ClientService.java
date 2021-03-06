package com.cglee079.coinchatbot.service;

import com.cglee079.coinchatbot.config.id.Coin;
import com.cglee079.coinchatbot.config.id.Lang;
import com.cglee079.coinchatbot.config.id.Market;
import com.cglee079.coinchatbot.config.id.MenuState;
import com.cglee079.coinchatbot.dao.ClientDao;
import com.cglee079.coinchatbot.model.ClientVo;
import com.cglee079.coinchatbot.util.TimeStamper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ClientService {

    @Value("#{constants['client.max.error.cnt']}")
    private int maxErrorCnt;

    @Autowired
    private ClientDao clientDao;

    public int count(Map<String, Object> map) {
        return clientDao.count(map);
    }

    public List<ClientVo> list(Map<String, Object> map) {
        if (map.containsKey("page") && map.containsKey("rows")) {
            int page = Integer.parseInt((String) map.get("page"));
            int rows = Integer.parseInt((String) map.get("rows"));
            int stRow = (page * rows) - rows;
            map.put("stRow", stRow);
        }

        return clientDao.list(map);
    }

    public List<ClientVo> list(Coin coinId, Market marketId, double coinValue, boolean isUp) {
        if (isUp) {
            return clientDao.listAlertTargetUp(coinId, marketId, coinValue);
        } else {
            return clientDao.listAlertTargetDown(coinId, marketId, coinValue);
        }
    }

    public List<ClientVo> list(Coin coinId, Market marketId, Integer timeLoop, Integer dayLoop) {
        return clientDao.listAlertLoop(coinId, marketId, timeLoop, dayLoop);
    }

    public List<ClientVo> listAtMidnight(Coin coinId, Market marketId, Integer timeLoop, int dayLoop, Date dateCurrent) {
        List<ClientVo> newClients = new ArrayList<>();
        List<ClientVo> clients = clientDao.listAlertLoop(coinId, marketId, timeLoop, dayLoop);
        Date newDate = new Date();
        for (int i = 0; i < clients.size(); i++) {
            ClientVo client = clients.get(i);
            newDate.setTime(dateCurrent.getTime() + client.getLocaltime());
            if (newDate.getHours() == 0) {
                newClients.add(client);
            }
        }
        return newClients;
    }


    @Transactional
    public boolean openChat(Coin coinId, Integer userId, String username, Market marketId) {

        ClientVo client = null;
        client = clientDao.get(coinId, userId.toString());

        if (client == null) {
            client = ClientVo.builder()
                    .coinId(coinId)
                    .marketId(marketId)
                    .userId(userId.toString())
                    .username(username)
                    .localtime((long) 0)
                    .lang(Lang.KR)
                    .timeloop(3)
                    .dayloop(1)
                    .stateId(MenuState.MAIN)
                    .openDate(TimeStamper.getDateTime())
                    .errCnt(0)
                    .enabled(true)
                    .build();
            return clientDao.insert(client);
        } else {
            boolean enabled = client.isEnabled();
            if (enabled) {
                return false;
            } else {
                client.setErrCnt(0);
                client.setEnabled(true);
                client.setReopenDate(TimeStamper.getDateTime());
                return clientDao.update(client);
            }
        }
    }

    @Transactional
    public boolean stopChat(Coin coinId, int userId) {
        ClientVo client = clientDao.get(coinId, String.valueOf(userId));
        if (client != null) {
            client.setTimeloop(0);
            client.setDayloop(0);
            return clientDao.update(client);
        }
        return false;
    }

    @Transactional
    public boolean increaseClientErrorCount(Coin coinId, String userId) {
        ClientVo client = clientDao.get(coinId, userId);
        if (client != null && client.isEnabled()) {
            int errorCount = client.getErrCnt() + 1;
            if (errorCount > maxErrorCnt) {
                log.info("DISABLED CLIENT ::  coinId {}, userId : {}", coinId, userId);
                client.setEnabled(false);
                client.setCloseDate(TimeStamper.getDateTime());
                return clientDao.update(client);
            }

            client.setErrCnt(errorCount);
            return clientDao.update(client);

        }
        return false;
    }

    public boolean delete(ClientVo client) {
        return clientDao.delete(client);
    }

    public boolean update(ClientVo client) {
        return clientDao.update(client);
    }

    @Transactional
    public boolean updateByAdmin(ClientVo client) {
        ClientVo saved = clientDao.get(client.getCoinId(), client.getUserId());

        saved.setMarketId(client.getMarketId());
        saved.setLang(client.getLang());
        saved.setStateId(client.getStateId());
        saved.setDayloop(client.getDayloop());
        saved.setTimeloop(client.getTimeloop());
        saved.setErrCnt(client.getErrCnt());
        saved.setEnabled(client.isEnabled());

        return clientDao.update(saved);

    }

    @Transactional
    public boolean updateStateId(Coin coinId, String userId, MenuState stateId) {
        ClientVo client = clientDao.get(coinId, userId);
        if (client != null) {
            client.setStateId(stateId);
            return clientDao.update(client);
        } else {
            return false;
        }

    }

    @Transactional
    public boolean updateMarketId(Coin coinId, String userId, Market marketId) {
        ClientVo client = clientDao.get(coinId, userId);
        if (client != null) {
            client.setMarketId(marketId);
            return clientDao.update(client);
        } else {
            return false;
        }
    }

    @Transactional
    public boolean updateInvest(Coin coinId, String userId, Double price) {
        ClientVo client = clientDao.get(coinId, userId);
        if (client != null) {
            client.setInvest(price);
            return clientDao.update(client);
        } else {
            return false;
        }
    }

    @Transactional
    public boolean updateTimeLoop(Coin coinId, String userId, int timeloop) {
        ClientVo client = clientDao.get(coinId, userId);
        if (client != null) {
            client.setTimeloop(timeloop);
            return clientDao.update(client);
        } else {
            return false;
        }
    }

    @Transactional
    public boolean updateDayLoop(Coin coinId, String userId, int dayLoop) {
        ClientVo client = clientDao.get(coinId, userId);
        if (client != null) {
            client.setDayloop(dayLoop);
            return clientDao.update(client);
        } else {
            return false;
        }
    }

    @Transactional
    public boolean updateNumber(Coin coinId, String userId, double number) {
        ClientVo client = clientDao.get(coinId, userId);
        if (client != null) {
            client.setCoinCnt(number);
            return clientDao.update(client);
        } else {
            return false;
        }
    }

    @Transactional
    public boolean updateLocalTime(Coin coinId, String userId, Long localTime) {
        ClientVo client = clientDao.get(coinId, userId);
        if (client != null) {
            client.setLocaltime(localTime);
            return clientDao.update(client);
        } else {
            return false;
        }
    }

    @Transactional
    public boolean updateLanguage(Coin coinId, String userId, Lang lang) {
        ClientVo client = clientDao.get(coinId, userId);
        if (client != null) {
            client.setLang(lang);
            return clientDao.update(client);
        } else {
            return false;
        }
    }

    public boolean updateMsgDate(ClientVo client) {
        if (client != null) {
            client.setLaMsgDate(TimeStamper.getDateTime());
            return clientDao.update(client);
        } else {
            return false;
        }
    }

    public ClientVo get(Coin coinId, String userId) {
        return clientDao.get(coinId, userId);
    }

    public ClientVo get(Coin coinId, int userId) {
        return this.get(coinId, String.valueOf(userId));
    }

    /* 통계 관련 카운트 */
    public Object countChat(Boolean enabled) {
        return clientDao.countChat(enabled);
    }

    public Object countUser(Boolean enabled) {
        return clientDao.countUser(enabled);
    }

    public JSONArray countNewClientInToday() {
        List<Map<String, Object>> clients = clientDao.countNewClientInToday();
        return new JSONArray(new Gson().toJson(clients));
    }

    public JSONArray countNewClientInTomonth() {
        List<Map<String, Object>> clients = clientDao.countNewClientInTomonth();
        return new JSONArray(new Gson().toJson(clients));
    }


}
