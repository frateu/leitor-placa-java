# Leitor de placa de veículos do Brasil!

Este é um projeto que realizei na faculdade, mas agora estou fazendo em Java e tentando aprimorá-lo conforme o passar do tempo.

Nesta primeira versão eu só peguei a versão básica que tinha feito na faculdade mesmo.
Até agora ele só faz uma verificação básica da placa do carro e tenta encontrar no **padrão brasileiro AAA-9999 ou no padrão Mercosul AAA-9A99**.
**Também só é possível encontrar as placas de carros**. Depois irei implementar para ler placas de motos.
Conforme teste de placas, as que foram registradas com uma inclinação para algum dos lados, não foi possível encontrar o padrão.


# Dependências


## OpenCV
> O projeto já vem com a biblioteca como dependência necessitando coloca-la no **C:\opencv**.


## Tesseract-OCR
> O projeto tem como dependência o Tesseract-OCR, a sua localização padrão é na **C:\Program Files\Tesseract-OCR**, mas caso precise trocar a localização, trocar na classe **Constantes**.



# Melhorias

- Ler placas de motos
- Reconhecimento de placas independente da sua inclinação