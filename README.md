Works on MacBook, may not on Windows.
Run cropper.py before Main.java

Main.java -> Takes currentBoard.txt as an input, and identifies all possible words. Marks the board with information of start letter (marked with a circle), and end letter. Filter through marked images by clicking the space bar on the image, and quit by clicking "q".

croppedBoard.png -> Example of cropped board

cropper.py -> Input is a raw iphone (14) screenshot, crops the screenshot and identifies letters on the board. Then, uses the /letters folder to compare and identify letters on the board. Saves output in currentBoard.txt for the Java file

currentBoard.txt -> Output of cropper.py, actual content of the board in the screenshot. Used in Main.java

rawExample.jpeg -> Example of screenshot given to cropper.py

wordset_hash.ser -> Compiled list of all valid words for hashsets: https://drive.google.com/file/d/1oGDf1wjWp5RF_X9C7HoedhIWMh5uJs8s/view -> Collins Scrabble Words
