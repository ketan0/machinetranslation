# Statistical Machine Translation

NOTE: This code is from a high school computer science project in 2016--not reflective of my current coding style standards.

Bayesian machine translation project . Refer to [smtinfosheet](smtinfosheet.pdf) for the info on how this project functions. Model was trained on a Europarl corpus containing side-by-side transcripts of UN proceedings in German and English. 

## Requirements: 

JAVA SE 6, DrJava (or similar Java IDE to run code)

## Setup

In order to run this project, clone this repository, download the pre-trained models from [this Google Drive folder](https://drive.google.com/drive/folders/10ZZ6L2dE-Cgl6onc1wOZHAgT2GLTvxDQ?usp=sharing) and place the models (`*.ser`) in this repo; run Main.java to begin deserializing the models. This takes ~20 minutes.

## Demos
This project supports simple translations, as such:
![translation window](demos/translation.png)

The project also uses the English probability counts to implement word prediction, as such:
![prediction window](demos/prediction.png)

Additionally, the project can take two sentences in English and German as input and attempt to match the words:
![alignment window](demos/alignment.png)

Ideas and pseudocode derived from https://www.isi.edu/natural-language/mt/wkbk.pdf.
