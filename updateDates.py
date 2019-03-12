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
date1Str = todayDateTime.strftime("%Y-%m-%d")
date2Str =  tomorrowDateTime.strftime("%Y-%m-%d")



# Updating day 1 Session Dates
for i in range(len(data)):
    day = data[i]

    dateStr = date1Str
    if i > 0:
        dateStr = date2Str

    data[i]["date"] = dateStr + "T" + "00:00:00"

    # Updating Session Dates
    for room in day["rooms"]:
        for session in room["sessions"]:

            startingTime = session["startsAt"]
            oldStartingTime = startingTime.split('T')
            newStartTime = dateStr + 'T' + oldStartingTime[1]
            session["startsAt"] = newStartTime

            endingTime = session["endsAt"]
            oldEndingTime = endingTime.split('T')
            newEndTime = dateStr + 'T' + oldEndingTime[1]
            session["endsAt"] = newEndTime



    #Updating timeslot dates
    for timeSlots in day["timeSlots"]:
        for room in timeSlots["rooms"]:
            session = room['session']
            if 'startsAt' in session:

                startingTime = session["startsAt"]
                oldStartingTime = startingTime.split('T')
                newStartTime = dateStr + 'T' + oldStartingTime[1]
                session["startsAt"] = newStartTime

                endingTime = session["endsAt"]
                oldEndingTime = endingTime.split('T')
                newEndTime = dateStr + 'T' + oldEndingTime[1]
                session["endsAt"] = newEndTime

            room['session'] = session

    data[i] = day



# update existing file
with open("schedule.json", "w") as jsonFile:
    json.dump(data, jsonFile)
