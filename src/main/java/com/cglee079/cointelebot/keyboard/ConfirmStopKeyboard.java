package com.cglee079.cointelebot.keyboard;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import com.cglee079.cointelebot.cmd.CMDER;

public class ConfirmStopKeyboard extends ReplyKeyboardMarkup {
	public ConfirmStopKeyboard(String lang) {
		super();

		this.setSelective(true);
		this.setResizeKeyboard(true);
		this.setOneTimeKeyboard(false);

		List<KeyboardRow> keyboard = new ArrayList<>();
		
		KeyboardRow keyboardFirstRow = new KeyboardRow();
		keyboardFirstRow.add(CMDER.getConfirmStopYes(lang));
		keyboardFirstRow.add(CMDER.getConfirmStopNo(lang));
		keyboard.add(keyboardFirstRow);

		this.setKeyboard(keyboard);
	}

}
