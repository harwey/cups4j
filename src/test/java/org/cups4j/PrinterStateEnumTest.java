package org.cups4j;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PrinterStateEnumTest {

	@Test
	void givenValidEnumValue_parsingSucceeds() {
		assertEquals(PrinterStateEnum.IDLE, PrinterStateEnum.fromInteger(3));
		assertEquals(PrinterStateEnum.PRINTING, PrinterStateEnum.fromInteger(4));
		assertEquals(PrinterStateEnum.STOPPED, PrinterStateEnum.fromInteger(5));
	}

	@Test
	void givenValidEnumStringValue_parsingSucceeds() {
		assertEquals(PrinterStateEnum.IDLE, PrinterStateEnum.fromStringInteger("3"));
		assertEquals(PrinterStateEnum.PRINTING, PrinterStateEnum.fromStringInteger("4"));
		assertEquals(PrinterStateEnum.STOPPED, PrinterStateEnum.fromStringInteger("5"));
	}
	
	@Test
	void givenInvalidEnumStringValue_returnNull() {
		assertNull(PrinterStateEnum.fromStringInteger(null));
		assertNull(PrinterStateEnum.fromStringInteger(""));
		assertNull(PrinterStateEnum.fromStringInteger("0"));
		assertNull(PrinterStateEnum.fromStringInteger("1"));
		assertNull(PrinterStateEnum.fromStringInteger("2"));
		assertNull(PrinterStateEnum.fromStringInteger("6"));
		assertNull(PrinterStateEnum.fromStringInteger("abc"));
	}
	
	@Test
	void givenInvalidEnumValue_returnNull() {
		assertNull(PrinterStateEnum.fromInteger(null));
		assertNull(PrinterStateEnum.fromInteger(0));
		assertNull(PrinterStateEnum.fromInteger(1));
		assertNull(PrinterStateEnum.fromInteger(2));
		assertNull(PrinterStateEnum.fromInteger(6));
	}
}
