package com.example.taskReminder.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class LoadTest {
	
	@Test
	void getCodeTest() {
		assertThat(Load.LOW.getCode()).isEqualTo("L000");
		assertThat(Load.MIDDLE.getCode()).isEqualTo("L001");
		assertThat(Load.HIGH.getCode()).isEqualTo("L002");
	}
	
	@Test
	void getNameTest() {
		assertThat(Load.LOW.getName()).isEqualTo("低");
		assertThat(Load.MIDDLE.getName()).isEqualTo("中");
		assertThat(Load.HIGH.getName()).isEqualTo("高");
	}
	
	@Test
	void getValueTest() {
		assertThat(Load.getValue("L000")).isEqualTo(Load.LOW);
		assertThat(Load.getValue("L001")).isEqualTo(Load.MIDDLE);
		assertThat(Load.getValue("L002")).isEqualTo(Load.HIGH);
	}
	
	@Test
	void IllegalArgumentException例外の発生確認() {
		assertThrows(IllegalArgumentException.class, () -> Load.getValue("LLLL"));
	}
	
	@Test
	void getLoadDataTest() {
		assertThat(Load.getLoadData().get("L000")).isEqualTo("低");
		assertThat(Load.getLoadData().get("L001")).isEqualTo("中");
		assertThat(Load.getLoadData().get("L002")).isEqualTo("高");
	}

}
