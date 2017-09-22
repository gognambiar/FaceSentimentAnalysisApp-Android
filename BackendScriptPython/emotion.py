import numpy as np
import os
os.environ["CUDA_DEVICE_ORDER"]="PCI_BUS_ID"   # see issue #152
os.environ["CUDA_VISIBLE_DEVICES"]="0"

import sys
import cPickle
import random
import cv2


from keras.models import Model
from keras.layers import Flatten
from keras.layers import Dense
from keras.layers import Input
from keras.layers import Convolution2D
from keras.layers import MaxPooling2D,Dropout
from keras import backend as K
from keras import regularizers
from keras.utils import to_categorical
from keras.callbacks import ModelCheckpoint, ReduceLROnPlateau, EarlyStopping,Callback
from keras.optimizers import Adam, SGD, RMSprop
from keras.layers.core import Lambda
from keras.layers import merge
from keras.utils import Sequence
from keras.models import load_model
from keras.models import Sequential
from keras.preprocessing.image import ImageDataGenerator
from keras.regularizers import l1#,activity_l1
from keras.layers.normalization import BatchNormalization


model = Sequential()
    #add dropout to reduce overfitting
#model.add(Dropout(0.2, input_shape=(48, 48, 3)))
    #with 64 filters, 5*5 for convolutional kernel and activation 'relu'
model.add(Convolution2D(64, 5, 5, input_shape=(48, 48, 3), activation='relu'))
model.add(Dropout(0.2))
#model.add(BatchNormalization())
    #pooling layer
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Convolution2D(128, 5, 5, activation='relu'))
#model.add(BatchNormalization())

model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Dropout(0.2))
model.add(Convolution2D(256, 3, 3, activation='relu'))
#model.add(BatchNormalization())

model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Dropout(0.2))
model.add(Flatten())
    #fully connected layer
model.add(Dense(600, activation='relu'))
#model.add(BatchNormalization())
model.add(Dropout(0.2))
model.add(Dense(200, activation='relu'))
#model.add(BatchNormalization())
model.add(Dense(7, W_regularizer=l1(0.02), 
                   activity_regularizer=regularizers.l1(0.03), 
                   activation='softmax'))
 
# optimizer:
model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])


train_datagen = ImageDataGenerator(rescale = 1./255)
test_datagen = ImageDataGenerator(rescale = 1./255)


training_set = train_datagen.flow_from_directory('dataset/training',
                                                 target_size = (48,48),
                                                 batch_size = 128,
                                                 class_mode = 'categorical')

test_set = test_datagen.flow_from_directory('dataset/testing',
                                            target_size = (48,48),
                                            batch_size = 128,
                                            class_mode = 'categorical')

filename = "MyContinuedBestModel4.hdf5"
check_point = ModelCheckpoint(filename, monitor='val_acc', verbose=2, save_best_only=True,
                              mode='max')
callbacks_list = [check_point]

model.fit_generator(training_set,
                         steps_per_epoch = 28709,
                         epochs = 20,
                         validation_data = test_set,
                         validation_steps =7178,
                         callbacks=callbacks_list
                          )


 ###### Model test

# import numpy as np
# from keras.preprocessing import image
# test_image = image.load_img('dataset/single_prediction/cat_or_dog_1.jpg', target_size = (64, 64))
# test_image = image.img_to_array(test_image)
# test_image = np.expand_dims(test_image, axis = 0)
# result = classifier.predict(test_image)
# training_set.class_indices
# if result[0][0] == 1:
#     prediction = 'dog'
# else:
#     prediction = 'cat'