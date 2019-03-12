#!/usr/bin/python3

import json
import datetime
from shutil import copyfile
import os

# cd into directory
os.chdir("sessionize/app/src/main/assets/")


# Copy original file
exists = os.path.isfile("originalSchedule.json")
if exists:
    print("Already Exists")
else:
    copyfile("schedule.json", "originalSchedule.json")


# read existing file
with open("schedule.json", "r") as f:
    data = json.load(f)

# Update date
todayDateTime = datetime.date.today()
tomorrowDateTime = datetime.date.today() + datetime.timedelta(days=1)
date1Str = todayDateTime.strftime("%Y-%m-%dT00:00:00")
date2Str =  tomorrowDateTime.strftime("%Y-%m-%dT00:00:00")

data[0]["date"] = date1Str
data[1]["date"] = date2Str

# update existing file
with open("schedule.json", "w") as jsonFile:
    json.dump(data, jsonFile)
