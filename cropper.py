from PIL import Image
import os
import numpy as np
import cv2

img_path = "rawExample.jpeg"
save_path = ""

alphabet = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']

def cropBoard(image_path, x, y, crop_size=820):
    img = Image.open(image_path)
    img = img.crop((x, y, x + crop_size, y + crop_size))
    return img

def cropLetter(image, x, y, crop_size=184):
    letter = image.crop((x, y, x + crop_size, y + crop_size))
    return letter

def centerOfLetter(x, y):
    return (x * 212 + 92, y * 212 + 92)

def getLetter(img, x, y):
    topleft = (212 * x, 212 * y)
    image = cropLetter(img, topleft[0], topleft[1])
    return image

def load_templates(base_folder):
    templates = {}
    for letter in alphabet:
        folder = os.path.join(base_folder, letter)

        images = [f for f in os.listdir(folder) if f.endswith('.png')]

        path = os.path.join(folder, images[0])
        img = Image.open(path).convert("L").resize((28, 28))
        templates[letter] = np.array(img, dtype=float)

    return templates

def predict(image, templates):
    img = image.convert("L").resize((28, 28))

    img = np.array(img, dtype=float)
    scores = {
        letter: np.mean((img - tmpl) ** 2)
        for letter, tmpl in templates.items()
    }

    ranked = sorted(scores.items(), key=lambda x: x[1])
    return ranked[0][0]



img = cropBoard(img_path, x=175, y=1131)
npArray = np.array(img)

img.show()
img.save(save_path)

board = [[0 for _ in range(4)] for _ in range(4)]
templates = load_templates("letters")

for x in range (4): 
    for y in range (4): 
        board[x][y] = predict(getLetter(img, x, y), templates)


with open("/croppedBoard.png", "w") as f:
    for x in range(4):
        for y in range(4):
            f.write(board[y][x] + " ")
        f.write("\n")
