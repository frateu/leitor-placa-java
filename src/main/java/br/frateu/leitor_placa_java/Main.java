package br.frateu.leitor_placa_java;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;

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
		String placaFinal  = "";

		HashMap<String, Integer> padroesPlacasEncontradas = new HashMap<String, Integer>();

		double variacaoGama = -50;

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Configuração do Tesseract OCR
		Tesseract tesseract = new Tesseract();
		tesseract.setDatapath(Constantes.caminhoTesseract);

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
				
				while (variacaoGama < 50) {
					// Converter a imagem para o tipo de dados de ponto flutuante
					Mat floatImage = new Mat();
					imagemPlacaCinza.convertTo(floatImage, CvType.CV_32F, 1.0 / 255.0);

					// Alterando a imagem para obter o contraste necessario para identificar
					Mat adjustedImage = new Mat();
					Core.pow(floatImage, variacaoGama, adjustedImage);

					// Converter a imagem ajustada de volta para o tipo de dados de 8 bits
					Mat finalImage = new Mat();
					adjustedImage.convertTo(finalImage, CvType.CV_8U, 255.0);

					// Salvar a imagem alterada
					Imgcodecs.imwrite(Constantes.caminhoImagemAlterada, finalImage);

					// Pegando o arquivo da placa alterada
					File arquivoPlaca = new File(Constantes.caminhoImagemAlterada);

					String placaOCR = tesseract.doOCR(arquivoPlaca);

					placaOCR = placaOCR.replace(" ", "");

					//System.out.println("Resultado sem filtro: " + placaOCR);

					Matcher resultadosPadraoPlaca = Constantes.padraoPlacaBrasil.matcher(placaOCR);

					// Rodando entre todos os resultados encontrados
					while (resultadosPadraoPlaca.find()) {
						String padraoPlaca = resultadosPadraoPlaca.group();

						padraoPlaca = padraoPlaca.replace(":", "-").replace(".", "-");

						if (padroesPlacasEncontradas.containsKey(padraoPlaca)) {
							Integer quantidadeEncontrada = padroesPlacasEncontradas.get(padraoPlaca);

							quantidadeEncontrada = quantidadeEncontrada + 1;

							padroesPlacasEncontradas.replace(padraoPlaca, padroesPlacasEncontradas.get(padraoPlaca), quantidadeEncontrada);
						} else {
							padroesPlacasEncontradas.put(padraoPlaca, 1);
						}

						//System.out.println("Teste: " + padraoPlaca);
					}

					variacaoGama = variacaoGama + 0.5;
				}

				if (padroesPlacasEncontradas.isEmpty()) {
					System.out.println("Placa não encontrada!");
				} else {
					Integer quantidadeComparacao = 0;

					for (String placa : padroesPlacasEncontradas.keySet()) {
						if (padroesPlacasEncontradas.get(placa) > quantidadeComparacao) {
							placaFinal = placa;

							quantidadeComparacao = padroesPlacasEncontradas.get(placa);
						}
					}

					System.out.println("Placa encontrada: " + placaFinal);
				}
			} catch (TesseractException e) {
				System.err.println(e.getMessage());
			}
		} else {
			System.out.println("Nenhum arquivo selecionado.");
		}

	}
}
