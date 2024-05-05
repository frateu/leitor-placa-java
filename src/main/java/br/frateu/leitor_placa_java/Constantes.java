package br.frateu.leitor_placa_java;

import java.util.regex.Pattern;

public class Constantes {
	public static final String caminhoImagemAlterada = "D:\\Projects\\leitor-placa-java\\temp-files\\placa.png";

	public static final Pattern padraoPlacaBrasil = Pattern.compile("[A-Z]{3}-\\d{4}|" + "[A-Z]{3}-\\d[A-Z]\\d{2}|" + "[A-Z]{3}.\\d{4}|"
			+ "[A-Z]{3}.\\d[A-Z]\\d{2}|" + "[A-Z]{3}:\\d{4}|" + "[A-Z]{3}:\\d[A-Z]\\d{2}|" + "[A-Z]{3}\\d{4}|"
			+ "[A-Z]{3}\\d[A-Z]\\d{2}");
	
	public static final String caminhoTesseract = "C:\\Program Files\\Tesseract-OCR\\tessdata";
}
