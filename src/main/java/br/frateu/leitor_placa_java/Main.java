package br.frateu.leitor_placa_java;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Main {
	public static void main(String[] args) {
		// Definindo o caminho da imagem alterada
		String caminhoImagemAlterada = "D:\\Projects\\leitor-placa-java\\temp-files\\placa.png";

		// Definindo os dois padrões de placa de carro do Brasil
		Pattern padraoPlacaBrasil = Pattern.compile("[A-Z]{3}-\\d{4}|" + "[A-Z]{3}-\\d[A-Z]\\d{2}|" + "[A-Z]{3}.\\d{4}|"
				+ "[A-Z]{3}.\\d[A-Z]\\d{2}|" + "[A-Z]{3}:\\d{4}|" + "[A-Z]{3}:\\d[A-Z]\\d{2}|" + "[A-Z]{3}\\d{4}|"
				+ "[A-Z]{3}\\d[A-Z]\\d{2}");

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Configuração do Tesseract OCR
		Tesseract tesseract = new Tesseract();
		tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");

		// Criando um seletor de arquivo
		JFileChooser seletorArquivoPlaca = new JFileChooser();

		// Definindo filtros de extensão de arquivo para permitir apenas imagens
		FileNameExtensionFilter filtroImagem = new FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "gif");
		seletorArquivoPlaca.setFileFilter(filtroImagem);

		// Exibindo o diálogo de seleção de arquivo
		int result = seletorArquivoPlaca.showOpenDialog(null);

		// Verificando se o usuário selecionou um arquivo
		if (result == JFileChooser.APPROVE_OPTION) {
			// Obtendo o caminho do arquivo selecionado
			String caminhoOriginalPlaca = seletorArquivoPlaca.getSelectedFile().getAbsolutePath();

			try {
				Mat placaMat = Imgcodecs.imread(caminhoOriginalPlaca);

				// Converter a imagem para escala de cinza
				Mat imagemPlacaCinza = new Mat();

				Imgproc.cvtColor(placaMat, imagemPlacaCinza, Imgproc.COLOR_BGR2GRAY);

				// Converter a imagem para o tipo de dados de ponto flutuante
				Mat floatImage = new Mat();
				imagemPlacaCinza.convertTo(floatImage, CvType.CV_32F, 1.0 / 255.0);

				// Alterando a imagem para obter o contraste necessario para identificar
				Mat adjustedImage = new Mat();
				Core.pow(floatImage, -8.5, adjustedImage);

				// Converter a imagem ajustada de volta para o tipo de dados de 8 bits
				Mat finalImage = new Mat();
				adjustedImage.convertTo(finalImage, CvType.CV_8U, 255.0);

				// Salvar a imagem alterada
				Imgcodecs.imwrite(caminhoImagemAlterada, finalImage);

				// Pegando o arquivo da placa alterada
				File arquivoPlaca = new File(caminhoImagemAlterada);

				String placaOCR = tesseract.doOCR(arquivoPlaca);

				placaOCR = placaOCR.replace(" ", "");

				System.out.println("Resultado sem filtro: " + placaOCR);

				Matcher resultadosPadraoPlaca = padraoPlacaBrasil.matcher(placaOCR);

				// Rodando entre todos os resultados encontrados
				while (resultadosPadraoPlaca.find()) {
					String padraoPlaca = resultadosPadraoPlaca.group();
					
					padraoPlaca = padraoPlaca.replace(":", "-").replace(".", "-");

					System.out.println("Placa encontrada: " + padraoPlaca);
				}
			} catch (TesseractException e) {
				System.err.println(e.getMessage());
			}
		} else {
			System.out.println("Nenhum arquivo selecionado.");
		}

	}
}
