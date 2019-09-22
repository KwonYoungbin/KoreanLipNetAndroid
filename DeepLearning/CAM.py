#-*- coding: utf-8 -*-
"""
사용법
python CAM.py

디렉토리 구조
CAM_input 에 frame 이미지
CAM_output - image 폴더 생성
CAM_output - video 폴더 생성
"""

from __future__ import print_function, division
from builtins import range, input

from keras.models import Model
from keras.applications.resnet50 import ResNet50, preprocess_input, decode_predictions
from keras.preprocessing import image

import numpy as np
import scipy as sp
import matplotlib.pyplot as plt

from glob import glob

import cv2
import os


# 파일 읽고 sort
image_files = glob("./CAM_input/*.png")
image_files.sort(key=lambda f: int(filter(str.isdigit, f)))

down_file_path = "./CAM_output/image/"
image_file_name = []
j=0

for j in image_files:
    image_file_name.append(j.split('/')[2])
    # print(image_file_path[2])

# print (image_file_name)

weight = ('./imagenet.h5')
# plt.imshow(image.load_img(np.random.choice(image_file)))
# plt.show()

resnet = ResNet50(input_shape=(224,224,3), include_top=True)

# resnet.summary()

activation_layer = resnet.get_layer('activation_49')

model = Model(inputs=resnet.input, outputs=activation_layer.output)

final_dense = resnet.get_layer('fc1000')
W = final_dense.get_weights()[0]

i = 0
k = 0

for i in image_files:

    img = image.load_img(str(i), target_size=(224,224))

    # y =np.array(img)
    y = np.expand_dims(img,0)
    
    print("####")
    print(y.shape)
    print("####")

    x = preprocess_input(y)

    print(x)

    # y = np.expand_dims(img,0).setflags(write=1)
    # x = preprocess_input(y)
    fmaps = model.predict(x)[0]

    probs = resnet.predict(x)
    classnames = decode_predictions(probs)[0]
 
    print(classnames)

    classname = classnames[0][1]
    pred = np.argmax(probs[0])

    w = W[:, pred]

    cam = fmaps.dot(w)
    cam = sp.ndimage.zoom(cam, (32, 32), order=1)

    # heatmap을 RGB 포맷으로 변환
    # cam2 = np.uint8(255 * cam)

    # cam2 = cv2.applyColorMap(cam2, cv2.COLORMAP_JET)
    # superimposed_img = cam2 * 0.7 + img


    # plt.subplot(1,2,1)
    plt.imshow(img, alpha=0.9)
    plt.imshow(cam, cmap='jet', alpha=0.7)
    print ("------------------")

    # 이미지 저장
    plt.savefig(down_file_path + image_file_name.__getitem__(k))

    # print (down_file_path + image_file_name.__getitem__(k)) # "./hi2/" + mouth_052.png
    # cv2.imwrite(down_file_path + image_file_name.__getitem__(k), superimposed_img)

    # plt.subplot(1,2,2)
    # plt.imshow(img)
    # plt.title(classname)
    # plt.show()
    # plt.close()
    k = k+1

# 비디오 생성
os.system("ffmpeg -f image2 -r 25 -i ./CAM_output/image/mouth_%3d.png -vcodec mpeg4 -y ./CAM_output/video/movie.mp4")