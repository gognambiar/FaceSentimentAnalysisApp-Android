from flask import Flask,request
import json
import base64
import io
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
import numpy as np
from keras.preprocessing import image
from PIL import Image

app = Flask(__name__)

def ml_model(s):
	imgdata = base64.b64decode(s)
	filename = 'output_image.jpg'
	with open(filename, 'wb') as f:
		f.write(imgdata)

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

	model.load_weights('MyContinuedBestModel3.hdf5')
	#model.compile(loss='categorical_crossentropy',optimizer='adam',metrics=['accuracy'])

	test_image = image.load_img("output_image.jpg", target_size = (48,48)).convert('LA').convert('RGB')
	test_image = image.img_to_array(test_image)
	#print(test_image.shape)
	test_image = np.expand_dims(test_image, axis = 0)
	result = model.predict(test_image)
	#print(result)
	#training_set.class_indices



	#0=Angry, 1=Disgust, 2=Fear, 3=Happy, 4=Neutral, 5=Sad, 6=Surprise).

	if int(result[0][0]) == 1:
			prediction = 'Angry'
	elif int(result[0][1]) == 1:
			prediction = 'Disgust'
	elif int(result[0][2]) == 1:
			prediction = 'Fear'
	elif int(result[0][3]) == 1:
			prediction = 'Happy'
	elif int(result[0][4]) == 1:
			prediction = 'Neutral'
	elif int(result[0][5]) == 1:
			prediction = 'Sad'
	elif int(result[0][6]) == 1:
			prediction = 'Surprise'

	return(prediction)


@app.route("/iprd", methods=["POST"])
def test():
	rrjes = {}
	datar = request.data
	dataDictr = json.loads(datar)
	picall = dataDictr.get('imgr')
	pics = picall.split("-")
	for i in range(len(pics)):
		pred = ml_model(pics[i])
		rrjes.update({"pic"+str(i+1): pred})
		print(pred)
	return json.dumps(rrjes)

if __name__ == "__main__":
		app.run()
